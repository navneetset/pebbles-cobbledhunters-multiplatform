package tech.sethi.pebbles.cobbledhunters.hunt

import com.cobblemon.mod.common.pokemon.Pokemon
import dev.architectury.event.events.common.PlayerEvent
import kotlinx.coroutines.*
import net.minecraft.entity.boss.BossBar.Color
import net.minecraft.entity.boss.BossBar.Style
import net.minecraft.entity.boss.ServerBossBar
import net.minecraft.server.network.ServerPlayerEntity
import tech.sethi.pebbles.cobbledhunters.CobbledHunters
import tech.sethi.pebbles.cobbledhunters.config.baseconfig.BaseConfig
import tech.sethi.pebbles.cobbledhunters.config.baseconfig.LangConfig
import tech.sethi.pebbles.cobbledhunters.config.reward.RewardConfigLoader
import tech.sethi.pebbles.cobbledhunters.data.DatabaseHandler
import tech.sethi.pebbles.cobbledhunters.hunt.type.HuntDifficulties
import tech.sethi.pebbles.cobbledhunters.hunt.type.HuntTracker
import tech.sethi.pebbles.cobbledhunters.hunt.type.PersonalHunts
import tech.sethi.pebbles.cobbledhunters.util.PM
import tech.sethi.pebbles.partyapi.dataclass.Party
import tech.sethi.pebbles.partyapi.datahandler.PartyHandler
import tech.sethi.pebbles.partyapi.eventlistener.JoinPartyEvent
import tech.sethi.pebbles.partyapi.eventlistener.LeavePartyEvent
import java.util.concurrent.ConcurrentHashMap

object PersonalHuntHandler {
    val personalHunts = ConcurrentHashMap<String, PersonalHunts>()
    val progressBossbar = ConcurrentHashMap<String, ServerBossBar?>()

    init {
        PlayerEvent.PLAYER_JOIN.register { player ->
            val playerUUID = player.uuidAsString
            val playerName = player.name.string
            personalHunts[playerUUID] = getPersonalHunts(playerUUID, playerName)
        }

        CoroutineScope(Dispatchers.IO).launch {
            while (this.isActive) {
                personalHunts.forEach { (_, personalHunt) ->
                    personalHunt.getHunts().forEach { huntTracker ->
                        if (huntTracker != null) {
                            if (huntTracker.active && (huntTracker.endTime
                                    ?: Long.MAX_VALUE) < System.currentTimeMillis()
                            ) {
                                huntTracker.active = false
                                huntTracker.success = huntTracker.success ?: false
                            }
                        }
                    }
                }

                // if player has any empty hunts, roll them
                personalHunts.forEach { (playerUUID, personalHunt) ->
                    if (personalHunt.getHunts()
                            .any { it == null || it.success != null || (it.endTime != null && it.endTime!! < System.currentTimeMillis()) }
                    ) {
                        rollPersonalHunt(playerUUID, personalHunt.playerName)
                    }
                }

                // bossbar update
                val bossbarToRemove = mutableListOf<String>()
                progressBossbar.forEach { (uuid, bossbar) ->
                    val player = PM.getPlayer(uuid)
                    val personalHunt = player?.name?.let { getPersonalHunts(uuid, it.string) }
                    val activeHuntTracker = personalHunt?.getHunts()?.firstOrNull { it?.active == true }
                    if (activeHuntTracker != null && bossbar != null) {
                        val timeLeft = (activeHuntTracker.endTime ?: Long.MAX_VALUE) - System.currentTimeMillis()
                        val progress =
                            (timeLeft / (activeHuntTracker.hunt.timeLimitMinutes * 60 * 1000).toDouble()).toFloat()
                        bossbar.percent = progress
                        bossbar.name =
                            PM.returnStyledText("${activeHuntTracker.hunt.name} <yellow>${activeHuntTracker.progress}/${activeHuntTracker.hunt.amount}</yellow>")
                        bossbar.addPlayer(player)
                    } else {
                        bossbar?.removePlayer(player)
                        bossbarToRemove.add(uuid)
                    }
                }

                bossbarToRemove.forEach { progressBossbar.remove(it) }

                delay(1000)
            }
        }


        if (BaseConfig.baseConfig.enablePartyHunts) {

            LeavePartyEvent.EVENT.register(object : LeavePartyEvent {
                override fun onLeaveParty(playerName: String) {
                    // end active hunt if player leaves party
                    val player = PM.getPlayer(playerName) ?: return
                    val playerUUID = player.uuidAsString
                    val personalHunt = getPersonalHunts(playerUUID, playerName)
                    personalHunt.getHunts().forEach {
                        if (it?.active == true) {
                            cancelHunt(playerUUID, playerName, it.hunt.difficulty)
                            player.sendMessage(
                                PM.returnStyledText(LangConfig.langConfig.partyLeaveCancelHunt), false
                            )
                        }
                    }
                }
            })

            JoinPartyEvent.EVENT.register(object : JoinPartyEvent {
                override fun onJoinParty(playerName: String) {
                    // join on-going hunts if any exists for the party owner
                    val player = PM.getPlayer(playerName) ?: return
                    val party = PartyHandler.db.getPlayerParty(playerName)
                    val owner = party?.owner?.let { PM.getPlayer(it) }
                    val ownerUUID = owner?.uuidAsString
                    val ownerPersonalHunt = ownerUUID?.let { getPersonalHunts(it, owner.name.string) }
                    ownerPersonalHunt?.getHunts()?.forEach {
                        if (it?.active == true) {
                            joinOngoingHunt(player, party)
                            player.sendMessage(
                                PM.returnStyledText(LangConfig.langConfig.partyJoinActiveHunt), false
                            )
                        }
                    }
                }
            })
        }
    }


    fun getPersonalHunts(playerUUID: String, playerName: String): PersonalHunts {
        return personalHunts.getOrPut(playerUUID) {
            PersonalHunts(playerUUID, playerName, null, null, null, null, null)
        }
    }

    fun rollPersonalHunt(playerUUID: String, playerName: String) {
        val personalHunts = getPersonalHunts(playerUUID, playerName)
        val allHunts = DatabaseHandler.db!!.getPersonalHunts()

        fun rollHuntForDifficulty(difficulty: HuntDifficulties) {
            val hunt = personalHunts.getHuntByDifficulty(difficulty)
            if (hunt == null || hunt.success != null || (hunt.endTime != null && hunt.endTime!! < System.currentTimeMillis())) {
                val randomHunt = allHunts.filter { it.difficulty == difficulty }.random()
                randomHunt.rewardPools.forEach {
                    it.reward = it.getRolledReward()
                }
                personalHunts.setHuntByDifficulty(
                    difficulty, HuntTracker(
                        randomHunt,
                        rolledTime = System.currentTimeMillis(),
                        startTime = null,
                        endTime = null,
                        active = false,
                        success = null
                    )
                )
            }
        }

        HuntDifficulties.values().forEach { rollHuntForDifficulty(it) }
    }

    fun activateHunt(playerUUID: String, playerName: String, difficulty: HuntDifficulties) {
        val personalHunts = getPersonalHunts(playerUUID, playerName)
        val huntTracker = personalHunts.getHuntByDifficulty(difficulty) ?: return
        if (huntTracker.active) return

        startHunt(huntTracker)
        val bossBarTextWithProgress = createBossBarText(huntTracker)
        val bossbar = createServerBossBar(bossBarTextWithProgress)

        val player = PM.getPlayer(playerName) ?: return
        addPlayerToBossbar(bossbar, player, playerUUID)

        if (BaseConfig.baseConfig.enablePartyHunts) {
            updatePartyMembersHunts(playerName, difficulty, huntTracker, bossBarTextWithProgress)
        }
    }

    private fun startHunt(huntTracker: HuntTracker) {
        val currentTime = System.currentTimeMillis()
        huntTracker.apply {
            active = true
            startTime = currentTime
            endTime = currentTime + hunt.timeLimitMinutes * 60 * 1000
        }
    }

    private fun createBossBarText(huntTracker: HuntTracker): String {
        return "${huntTracker.hunt.name} <yellow>${huntTracker.progress}/${huntTracker.hunt.amount}</yellow>"
    }

    private fun createServerBossBar(text: String): ServerBossBar {
        return ServerBossBar(PM.returnStyledText(text), Color.PURPLE, Style.PROGRESS)
    }

    private fun addPlayerToBossbar(bossbar: ServerBossBar, player: ServerPlayerEntity, playerUUID: String) {
        bossbar.addPlayer(player)
        progressBossbar[playerUUID] = bossbar
    }

    private fun updatePartyMembersHunts(
        playerName: String, difficulty: HuntDifficulties, huntTracker: HuntTracker, bossBarText: String
    ) {
        val party = PartyHandler.db.getPlayerParty(playerName) ?: return
        party.members.forEach { memberName ->
            val memberPersonalHunts = getPersonalHunts(memberName, memberName)
            val memberHuntTracker = memberPersonalHunts.getHuntByDifficulty(difficulty)
            memberHuntTracker?.apply {
                active = true
                startTime = huntTracker.startTime
                endTime = huntTracker.endTime
                progress = huntTracker.progress
            }

            PM.getPlayer(memberName)?.let { partyPlayer ->
                val memberBossbar = progressBossbar[partyPlayer.uuidAsString] ?: createServerBossBar(bossBarText)
                memberBossbar.addPlayer(partyPlayer)
                progressBossbar[partyPlayer.uuidAsString] = memberBossbar
            }
        }
    }


    fun cancelHunt(playerUUID: String, playerName: String, difficulty: HuntDifficulties) {
        val personalHunts = getPersonalHunts(playerUUID, playerName)
        val huntTracker = personalHunts.getHuntByDifficulty(difficulty) ?: return
        if (!huntTracker.active) return
        huntTracker.active = false
        huntTracker.success = false

        // remove hunt from player
        personalHunts.setHuntByDifficulty(difficulty, null)
    }

    fun joinOngoingHunt(player: ServerPlayerEntity, party: Party) {
        val personalHunts = getPersonalHunts(player.uuidAsString, player.name.string)
        val partyOwner = PM.getPlayer(party.owner) ?: return
        val partyOwnerPersonalHunts = getPersonalHunts(partyOwner.uuidAsString, partyOwner.name.string)
        partyOwnerPersonalHunts.getHunts().forEach {
            if (it?.active == true) {
                val memberHuntTracker = it.copy()
                personalHunts.setHuntByDifficulty(it.hunt.difficulty, memberHuntTracker)
                activateHunt(player.uuidAsString, player.name.string, it.hunt.difficulty)
            }
        }
    }

    fun completeHunt(playerUUID: String, playerName: String, difficulty: HuntDifficulties) {
        val personalHunts = getPersonalHunts(playerUUID, playerName)
        val huntTracker = personalHunts.getHuntByDifficulty(difficulty) ?: return
        if (!huntTracker.active) return
        huntTracker.active = false
        huntTracker.success = true

        // reward player
        rewardHunt(playerUUID, playerName, difficulty)

        // remove hunt from player
        personalHunts.setHuntByDifficulty(difficulty, null)

        val player = PM.getPlayer(playerName) ?: return
        player.sendMessage(
            PM.returnStyledText(LangConfig.langConfig.huntCompleted), false
        )

    }

    fun rewardHunt(playerUUID: String, playerName: String, difficulty: HuntDifficulties) {
        val personalHunts = getPersonalHunts(playerUUID, playerName)
        val huntTracker = personalHunts.getHuntByDifficulty(difficulty) ?: return
        if (huntTracker.success == true) {
            val rewards = huntTracker.hunt.rewardPools.flatMap { it.rewards }
            rewards.forEach { reward ->
                RewardConfigLoader.rewards.find { it.id == reward.rewardId }?.let { rewardConfig ->
                    rewardConfig.commands.forEach { command ->
                        PM.runCommand(
                            command.replace("{player_name}", playerName)
                                .replace("{amount}", rewardConfig.amount.toString())
                        )
                    }
                }
            }
        }
    }

    fun onPokemonCaptured(player: ServerPlayerEntity, pokemon: Pokemon) {
        val personalHunts = getPersonalHunts(player.uuidAsString, player.name.string)
        personalHunts.getHunts().forEach { huntTracker ->
            if (huntTracker?.active == true) {
                if (huntTracker.hunt.huntFeature.checkRequirement(pokemon)) {
                    huntTracker.progress++

                    if (BaseConfig.baseConfig.enablePartyHunts && PartyHandler.db.getPlayerParty(player.name.string) != null) {
                        val party = PartyHandler.db.getPlayerParty(player.name.string)
                        party?.members?.forEach { memberName ->
                            val partyPlayer = PM.getPlayer(memberName) ?: return
                            val memberPersonalHunts = getPersonalHunts(partyPlayer.uuidAsString, memberName)
                            val memberHuntTracker = memberPersonalHunts.getHuntByDifficulty(huntTracker.hunt.difficulty)
                            memberHuntTracker?.progress = huntTracker.progress

                            val memberBossbar = progressBossbar[partyPlayer.uuidAsString]
                            memberBossbar?.name = PM.returnStyledText(createBossBarText(huntTracker))

                            if (memberHuntTracker != null) {
                                CobbledHunters.LOGGER.info("Member hunt progress: ${memberHuntTracker.progress}")
                                if (memberHuntTracker.progress >= huntTracker.hunt.amount) {
                                    CobbledHunters.LOGGER.info("Member hunt completed")
                                    completeHunt(partyPlayer.uuidAsString, memberName, huntTracker.hunt.difficulty)
                                }
                            }
                        }
                    } else {
                        val bossbar = progressBossbar[player.uuidAsString]
                        bossbar?.name = PM.returnStyledText(createBossBarText(huntTracker))

                        if (huntTracker.progress >= huntTracker.hunt.amount) {
                            completeHunt(player.uuidAsString, player.name.string, huntTracker.hunt.difficulty)
                        }
                    }
                }
            }
        }
    }

}