package tech.sethi.pebbles.cobbledhunters.hunt

import com.cobblemon.mod.common.pokemon.Pokemon
import dev.architectury.event.events.common.PlayerEvent
import kotlinx.coroutines.*
import net.minecraft.entity.boss.BossBar.Color
import net.minecraft.entity.boss.BossBar.Style
import net.minecraft.entity.boss.ServerBossBar
import net.minecraft.server.network.ServerPlayerEntity
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
        huntTracker.active = true
        huntTracker.startTime = System.currentTimeMillis()
        huntTracker.endTime = System.currentTimeMillis() + huntTracker.hunt.timeLimitMinutes * 60 * 1000

        val bossBarTextWithProgress =
            PM.returnStyledText("${huntTracker.hunt.name} <yellow>${huntTracker.progress}/${huntTracker.hunt.amount}</yellow>")

        val bossbar = ServerBossBar(
            bossBarTextWithProgress, Color.PURPLE, Style.PROGRESS
        )
        val player = PM.getPlayer(playerName) ?: return
        bossbar.addPlayer(player)
        progressBossbar[playerUUID] = bossbar

        if (BaseConfig.baseConfig.enablePartyHunts) {
            val party = PartyHandler.db.getPlayerParty(playerName)
            if (party != null) {
                party.members.forEach {
                    val memberPersonalHunts = getPersonalHunts(it, it)
                    val memberHuntTracker = memberPersonalHunts.getHuntByDifficulty(difficulty)
                    memberHuntTracker?.active = true
                    memberHuntTracker?.startTime = huntTracker.startTime
                    memberHuntTracker?.endTime = huntTracker.endTime
                    memberHuntTracker?.progress = huntTracker.progress

                    val partyPlayer = PM.getPlayer(it)
                    if (partyPlayer != null) {
                        val membeerBossbar = progressBossbar[partyPlayer.uuidAsString]
                        if (membeerBossbar != null) {
                            membeerBossbar.name = bossBarTextWithProgress
                            membeerBossbar.addPlayer(partyPlayer)
                        } else {
                            val newBossbar = ServerBossBar(
                                bossBarTextWithProgress, Color.PURPLE, Style.PROGRESS
                            )
                            newBossbar.addPlayer(partyPlayer)
                            progressBossbar[partyPlayer.uuidAsString] = newBossbar
                        }
                    }
                }
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

                    if (BaseConfig.baseConfig.enablePartyHunts) {
                        val party = PartyHandler.db.getPlayerParty(player.name.string)
                        if (party != null) {
                            party.members.forEach {
                                val memberPersonalHunts = getPersonalHunts(it, it)
                                val memberHuntTracker =
                                    memberPersonalHunts.getHuntByDifficulty(huntTracker.hunt.difficulty)
                                memberHuntTracker?.progress = huntTracker.progress

                                val partyPlayer = PM.getPlayer(it)
                                if (partyPlayer != null) {
                                    if (memberHuntTracker != null) {
                                        progressBossbar[partyPlayer.uuidAsString]?.name =
                                            PM.returnStyledText("${memberHuntTracker.hunt.name} <yellow>${memberHuntTracker.progress}/${memberHuntTracker.hunt.amount}</yellow>")
                                    }

                                    if ((memberHuntTracker?.progress ?: 0) >= (memberHuntTracker?.hunt?.amount ?: 1)) {
                                        memberHuntTracker?.hunt?.difficulty?.let { diff ->
                                            completeHunt(
                                                partyPlayer.uuidAsString, partyPlayer.name.string, diff
                                            )
                                            rewardHunt(
                                                partyPlayer.uuidAsString, partyPlayer.name.string, diff
                                            )
                                        }
                                    }
                                }
                            }

                            return
                        }
                    }

                    progressBossbar[player.uuidAsString]?.name =
                        PM.returnStyledText("${huntTracker.hunt.name} <yellow>${huntTracker.progress}/${huntTracker.hunt.amount}</yellow>")

                    if (huntTracker.progress >= huntTracker.hunt.amount) {
                        completeHunt(player.uuidAsString, player.name.string, huntTracker.hunt.difficulty)
                        rewardHunt(player.uuidAsString, player.name.string, huntTracker.hunt.difficulty)
                    }
                }
            }
        }
    }

}