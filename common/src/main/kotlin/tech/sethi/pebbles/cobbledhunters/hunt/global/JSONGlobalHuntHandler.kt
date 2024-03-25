package tech.sethi.pebbles.cobbledhunters.hunt.global

import dev.architectury.event.events.common.PlayerEvent
import kotlinx.coroutines.*
import tech.sethi.pebbles.cobbledhunters.config.baseconfig.LangConfig
import tech.sethi.pebbles.cobbledhunters.config.hunt.global.GlobalHuntConfigLoader
import tech.sethi.pebbles.cobbledhunters.config.hunt.global.GlobalHuntPoolConfigLoader
import tech.sethi.pebbles.cobbledhunters.hunt.type.*
import tech.sethi.pebbles.cobbledhunters.util.PM

object JSONGlobalHuntHandler : AbstractGlobalHuntHandler() {


    init {
        onLoad()

        CoroutineScope(Dispatchers.IO).launch {
            while (this.isActive) {
                checkPoolsExpiration()

                delay(1000)
            }
        }

        PlayerEvent.PLAYER_JOIN.register { player ->
            CoroutineScope(Dispatchers.IO).launch {
                val playerUUID = player.uuidAsString
                globalHuntPools.values.forEach { tracker ->
                    tracker?.isParticipant(playerUUID)?.let {
                        if (it) {
                            val bossbar = activeBossbars.values.firstOrNull()
                            if (bossbar != null) {
                                addPlayerToBossbar(bossbar, player, tracker.hunt.id)
                            }
                        }
                    }
                }
            }
        }
    }

    fun onLoad() {
        GlobalHuntPoolConfigLoader.globalHuntsPool.forEach {
            rollNewHunt(it.id)
        }
    }

    fun checkPoolsExpiration() {
        globalHuntPools.forEach { (poolId, tracker) ->
            if (tracker != null && tracker.expired()) expireHunt(poolId, tracker)
        }
    }

    fun expireHunt(poolId: String, tracker: GlobalHuntTracker) {
        if (tracker.isCompleted().not()) {
            tracker.getParticipants().forEach {
                PM.getPlayer(it.uuid)?.sendMessage(PM.returnStyledText(LangConfig.langConfig.globalHuntExpired))
            }
        }

        globalHuntPools[poolId] = null
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

        PM.broadcast(LangConfig.langConfig.globalPoolRefreshAnnouncement.replace("{pool}", pool.name))
    }


}