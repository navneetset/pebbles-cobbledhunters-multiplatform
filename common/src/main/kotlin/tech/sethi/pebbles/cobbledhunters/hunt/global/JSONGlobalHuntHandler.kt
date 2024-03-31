package tech.sethi.pebbles.cobbledhunters.hunt.global

import com.cobblemon.mod.common.api.scheduling.after
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.server
import dev.architectury.event.events.common.LifecycleEvent
import dev.architectury.event.events.common.PlayerEvent
import kotlinx.coroutines.*
import net.minecraft.entity.boss.ServerBossBar
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.util.Identifier
import tech.sethi.pebbles.cobbledhunters.CobbledHunters
import tech.sethi.pebbles.cobbledhunters.config.baseconfig.LangConfig
import tech.sethi.pebbles.cobbledhunters.config.hunt.global.GlobalHuntConfigLoader
import tech.sethi.pebbles.cobbledhunters.config.hunt.global.GlobalHuntPoolConfigLoader
import tech.sethi.pebbles.cobbledhunters.config.reward.RewardConfigLoader
import tech.sethi.pebbles.cobbledhunters.data.DatabaseHandler
import tech.sethi.pebbles.cobbledhunters.hunt.type.*
import tech.sethi.pebbles.cobbledhunters.util.PM
import tech.sethi.pebbles.cobbledhunters.util.UnvalidatedSound
import java.lang.Thread.sleep
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors

object JSONGlobalHuntHandler : AbstractGlobalHuntHandler() {

    override var globalHuntPools: ConcurrentHashMap<String, GlobalHuntTracker?> = ConcurrentHashMap()
    override val activeBossbars: ConcurrentHashMap<String, ServerBossBar> = ConcurrentHashMap()

    val globalHuntWorker = Executors.newSingleThreadExecutor()

    init {
        onLoad()
    }

    fun onLoad() {
        GlobalHuntPoolConfigLoader.globalHuntsPool.forEach { rollNewHunt(it.id) }

        globalHuntWorker.submit {
            while (server() != null && server()!!.isRunning) {
                checkPoolsExpiration()

                activeBossbars.forEach { (_, bossbar) ->
                    val poolId = activeBossbars.entries.find { it.value == bossbar }?.key ?: return@forEach
                    val tracker = globalHuntPools[poolId] ?: return@forEach
                    if (tracker.expired().not()) {
                        val timeLeft = tracker.expireTime - System.currentTimeMillis()
                        val timeProgress = (timeLeft.toDouble() / (tracker.expireTime - tracker.rolledTime)).toFloat()
                        bossbar.percent = timeProgress
                    }
                }

                sleep(1000)
            }
        }

        LifecycleEvent.SERVER_STOPPING.register {
            globalHuntWorker.shutdownNow()
        }

        onPlayerJoin()
    }

    fun onPlayerJoin() {
        PlayerEvent.PLAYER_JOIN.register { player ->
            CoroutineScope(Dispatchers.IO).launch {
                val playerUUID = player.uuidAsString
                globalHuntPools.forEach { (poolId, tracker) ->
                    tracker?.isParticipant(playerUUID)?.let {
                        if (it) {
                            val bossbar = activeBossbars.values.firstOrNull()
                            if (bossbar != null) {
                                addPlayerToBossbar(bossbar, player, poolId)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun joinHunt(player: ServerPlayerEntity, poolId: String) {
        val canJoin = canJoinHunt(player, poolId)
        if (canJoin.first.not()) {
            PM.getPlayer(player.uuidAsString)?.sendMessage(PM.returnStyledText(canJoin.second))
            return
        }

        val tracker = globalHuntPools[poolId] ?: return
        tracker.addParticipant(Participant(player.uuidAsString, player.name.string, 0))
        addPlayerToBossbar(activeBossbars[poolId]!!, player, poolId)
        PM.getPlayer(player.uuidAsString)?.sendMessage(PM.returnStyledText(canJoin.second))
    }

    fun checkPoolsExpiration() {
        globalHuntPools.forEach { (poolId, tracker) ->
            if (tracker != null && tracker.expired()) expireHunt(poolId, tracker)
        }
    }

    fun expireHunt(poolId: String, tracker: GlobalHuntTracker) {
        if (tracker.isCompleted().not()) {
            tracker.getParticipants().forEach {
                PM.getPlayer(it.uuid)?.sendMessage(PM.returnStyledText(LangConfig.config.globalHuntExpired))
            }
        } else {
            tracker.getParticipants().forEach {
                PM.getPlayer(it.uuid)?.sendMessage(PM.returnStyledText(LangConfig.config.globalHuntRefreshed))
            }
        }

        CobbledHunters.LOGGER.info("Hunt $poolId expired. Rolling new hunt.")
        activeBossbars[poolId]?.clearPlayers()
        activeBossbars.remove(poolId)
        CobbledHunters.LOGGER.info("Hunt $poolId expired. Rolling new hunt.")
        rollNewHunt(poolId)
    }

    fun rollNewHunt(poolId: String) {
        val pool = GlobalHuntPoolConfigLoader.globalHuntsPool.find { it.id == poolId } ?: return
        val huntId = pool.huntIds.random()
        val hunt = GlobalHuntConfigLoader.globalHunts.find { it.id == huntId }
        if (hunt == null) throw Exception("[CobbledHunters] Global Hunt in pool $poolId not found! Please check your config.")

        val expireTime = System.currentTimeMillis() + hunt.timeLimitMinutes * 60 * 1000
        val tracker = GlobalHuntTracker(
            hunt = hunt,
            rolledTime = System.currentTimeMillis(),
            expireTime = expireTime,
        )

        globalHuntPools[poolId] = tracker
        generateBossBar(poolId)

        PM.broadcast(LangConfig.config.globalPoolRefreshAnnouncement.replace("{pool}", pool.name))
    }

    fun rewardHunters(poolId: String) {
        val tracker = globalHuntPools[poolId] ?: return
        if (tracker.rewarded) return

        tracker.rewarded = true
        val rewardPools = tracker.hunt.rewardPools
        val rolledRewardIds = rewardPools.map { it.reward }
        val rolledRewards = rolledRewardIds.map { RewardConfigLoader.getRewardById(it.rewardId) }
        val baseRewardIds = tracker.hunt.guaranteedRewardId
        val baseRewards = baseRewardIds.map { RewardConfigLoader.getRewardById(it) }

        val allRewards = rolledRewards + baseRewards

        CoroutineScope(Dispatchers.IO).launch {
            tracker.getParticipants().forEach { participant ->
                if (participant.progress > 0) {
                    DatabaseHandler.db!!.addPlayerRewards(
                        participant.uuid, allRewards.filterNotNull(), tracker.hunt.experience
                    )

                    PM.getPlayer(participant.uuid)?.let { player ->
                        UnvalidatedSound.playToPlayer(
                            Identifier("minecraft", "ui.toast.challenge_complete"),
                            SoundCategory.MASTER,
                            0.75f,
                            1.5f,
                            player.blockPos,
                            player.world,
                            8.0,
                            player
                        )
                    }
                }
            }
        }

        val ranking = tracker.getRanking()
        val rankingRewards = tracker.hunt.extraRankingRewards

        rankingRewards.forEachIndexed { index, rankingReward ->
            if (index >= ranking.size) return@forEachIndexed
            val participant = ranking[rankingReward.rank - 1]
            val rankRolledRewards =
                rankingReward.rewardPools.map { RewardConfigLoader.getRewardById(it.reward.rewardId) }
            val rankBaseRewards = rankingReward.guaranteedRewardId.map { RewardConfigLoader.getRewardById(it) }
            val allRankRewards = rankRolledRewards + rankBaseRewards

            CoroutineScope(Dispatchers.IO).launch {
                delay(1000)
                DatabaseHandler.db!!.addPlayerRewards(
                    participant.uuid, allRankRewards.filterNotNull(), rankingReward.experience
                )

                PM.getPlayer(participant.uuid)
                    ?.sendMessage(PM.returnStyledText(LangConfig.config.globalHuntRankingReward))
            }
        }

    }

    fun onPokemonAction(player: ServerPlayerEntity, pokemon: Pokemon, goal: HuntGoals) {
        if (goal != HuntGoals.CATCH && pokemon.isWild().not()) return
        val tracker = participatingHunt(player) ?: return
        val checkFeature = tracker.hunt.huntFeature.checkRequirement(pokemon, goal)
        val poolId = globalHuntPools.entries.find { it.value == tracker }?.key ?: return

        if (checkFeature.not()) return

        if (tracker.isCompleted().not()) {
            tracker.addProgress(player.uuidAsString, 1)
            val newBossbarName = createBossBarText(tracker)
            activeBossbars[poolId]?.name = PM.returnStyledText(newBossbarName)
        }

        if (tracker.isCompleted() && tracker.success != true) {
            tracker.success = true

            PM.broadcast(LangConfig.config.globalHuntCompletedBroadcast.replace("{hunt}", tracker.hunt.name))

            val ranking = tracker.getRanking()
            var rankingText = LangConfig.config.globalHuntCompletionLeaderboard

            ranking.forEachIndexed { index, participant ->
                rankingText = rankingText.replace("{player${index + 1}}", participant.name)
                    .replace("{progress${index + 1}}", participant.progress.toString())
            }

            for (i in ranking.size until 20) {
                rankingText = rankingText.replace("{player${i + 1}}", "").replace("{progress${i + 1}}", "")
            }

            after(ticks = 10) { activeBossbars[poolId]?.clearPlayers() }

            rankingText = rankingText.replace("{hunt}", tracker.hunt.name)
            PM.broadcast(rankingText)
            rewardHunters(poolId)
        }
    }
}