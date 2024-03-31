package tech.sethi.pebbles.cobbledhunters.hunt.personal

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
import tech.sethi.pebbles.cobbledhunters.config.baseconfig.BaseConfig
import tech.sethi.pebbles.cobbledhunters.config.baseconfig.LangConfig
import tech.sethi.pebbles.cobbledhunters.config.economy.EconomyConfig
import tech.sethi.pebbles.cobbledhunters.data.DatabaseHandler
import tech.sethi.pebbles.cobbledhunters.data.MongoDBHandler
import tech.sethi.pebbles.cobbledhunters.economy.EconomyHandler
import tech.sethi.pebbles.cobbledhunters.hunt.type.HuntDifficulties
import tech.sethi.pebbles.cobbledhunters.hunt.type.HuntGoals
import tech.sethi.pebbles.cobbledhunters.hunt.type.HuntTracker
import tech.sethi.pebbles.cobbledhunters.hunt.type.PersonalHunts
import tech.sethi.pebbles.cobbledhunters.util.PM
import tech.sethi.pebbles.cobbledhunters.util.UnvalidatedSound
import tech.sethi.pebbles.partyapi.dataclass.Party
import tech.sethi.pebbles.partyapi.datahandler.PartyHandler
import tech.sethi.pebbles.partyapi.eventlistener.JoinPartyEvent
import tech.sethi.pebbles.partyapi.eventlistener.LeavePartyEvent
import java.lang.Thread.sleep
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object MongoPersonalHuntHandler : AbstractPersonalHuntHandler() {
    override val personalHunts = ConcurrentHashMap<String, PersonalHunts>()
    override val rolledHunts = ConcurrentHashMap<String, HuntTracker>()
    override val activeBossbars = ConcurrentHashMap<String, ServerBossBar>()

    val db = DatabaseHandler.db!! as MongoDBHandler

    init {
        PlayerEvent.PLAYER_JOIN.register { player ->
            val playerUUID = player.uuidAsString
            val playerName = player.name.string
            personalHunts[playerUUID] = getPersonalHunts(playerUUID, playerName)

            // if player is in a hunt, redisplay the bossbar
            val personalHunt = getPersonalHunts(playerUUID, playerName)
            personalHunt.getHunts().forEach {
                if (it?.active == true) {
                    val bossbar = activeBossbars[it.uuid]
                    if (bossbar != null) {
                        addPlayerToBossbar(bossbar, player, it.uuid)
                    }
                }
            }
        }

        personalHuntWorker.submit {
            while (server() != null && server()!!.isRunning) {
                // update bossbar time progress
                activeBossbars.forEach { (uuid, bossbar) ->
                    val huntTracker = rolledHunts[uuid] ?: return@forEach
                    if (huntTracker.active) {
                        // countdown time by depleting the progress
                        val timeLeft = huntTracker.endTime!! - System.currentTimeMillis()
                        val timeProgress =
                            (timeLeft.toDouble() / (huntTracker.endTime!! - huntTracker.startTime!!)).toFloat()
                        bossbar.percent = timeProgress
                    }
                }

                sleep(1000)
            }
        }

        LifecycleEvent.SERVER_STOPPING.register {
            personalHuntWorker.shutdownNow()
        }


        if (BaseConfig.config.enablePartyHunts) {
            LeavePartyEvent.EVENT.register(object : LeavePartyEvent {
                override fun onLeaveParty(playerUuid: String) {
                    // end active hunt if player leaves party
                    val player = PM.getPlayer(playerUuid) ?: return
                    val playerUUID = player.uuidAsString
                    val personalHunt = getPersonalHunts(playerUUID, player.name.string)
                    personalHunt.getHunts().forEach {
                        if (it?.active == true) {
                            it.participants.remove(playerUUID)
                            activeBossbars[it.uuid]?.removePlayer(player)
                            if (it.participants.isEmpty()) {
                                cancelHunt(playerUUID, player.name.string, it.hunt.difficulty)
                            }
                            player.sendMessage(
                                PM.returnStyledText(LangConfig.config.partyLeaveCancelHunt), false
                            )
                        }
                    }
                }
            })

            JoinPartyEvent.EVENT.register(object : JoinPartyEvent {
                override fun onJoinParty(playerUuid: String) {
                    // join on-going hunts if any exists for the party owner
                    val player = PM.getPlayer(playerUuid) ?: return
                    val party = PartyHandler.db.getPlayerParty(player.uuidAsString)
                    val owner = party?.owner?.let { PM.getPlayer(it.name) }
                    val ownerUUID = owner?.uuidAsString
                    if (party != null) {
                        if (ownerUUID == player.uuidAsString && party.members.size == 1) return
                    }
                    val ownerPersonalHunt = ownerUUID?.let { getPersonalHunts(it, owner.name.string) }
                    ownerPersonalHunt?.getHunts()?.forEach {
                        if (it?.active == true) {
                            joinOngoingHunt(player, party)
                        }
                    }
                }
            })
        }
    }

    override fun getPersonalHunts(playerUUID: String, playerName: String): PersonalHunts {
        val playerHunt = db.getPlayerPersonalHuntSessions(playerUUID)
        return if (playerHunt != null) {
            PersonalHunts(
                playerUUID,
                playerName,
                playerHunt.easyHunt,
                playerHunt.mediumHunt,
                playerHunt.hardHunt,
                playerHunt.legendaryHunt,
                playerHunt.godlikeHunt
            )
        } else {
            val newPh = PersonalHunts(playerUUID, playerName, null, null, null, null, null)
            val playerParty = PartyHandler.db.getPlayerParty(playerUUID)
            if (playerParty != null && playerParty.owner.uuid != playerUUID) {
                val owner = playerParty.owner
                val ownerPersonalHunt = db.getPlayerPersonalHuntSessions(owner.uuid)
                if (ownerPersonalHunt != null) {
                    ownerPersonalHunt.getHuntByDifficulty(HuntDifficulties.EASY)?.active.let {
                        if (it == true) newPh.easyHunt = ownerPersonalHunt.easyHunt
                    }
                    ownerPersonalHunt.getHuntByDifficulty(HuntDifficulties.MEDIUM)?.active.let {
                        if (it == true) newPh.mediumHunt = ownerPersonalHunt.mediumHunt
                    }
                    ownerPersonalHunt.getHuntByDifficulty(HuntDifficulties.HARD)?.active.let {
                        if (it == true) newPh.hardHunt = ownerPersonalHunt.hardHunt
                    }
                    ownerPersonalHunt.getHuntByDifficulty(HuntDifficulties.LEGENDARY)?.active.let {
                        if (it == true) newPh.legendaryHunt = ownerPersonalHunt.legendaryHunt
                    }
                    ownerPersonalHunt.getHuntByDifficulty(HuntDifficulties.GODLIKE)?.active.let {
                        if (it == true) newPh.godlikeHunt = ownerPersonalHunt.godlikeHunt
                    }
                }
            }

            db.addPlayerPersonalHuntSession(playerUUID, newPh)
            newPh
        }
    }

    override fun rollPersonalHunt(playerUUID: String, playerName: String) {
        val personalHunts = getPersonalHunts(playerUUID, playerName)
        val allHunts = db.getPersonalHunts()

        fun rollHuntForDifficulty(difficulty: HuntDifficulties) {
            val hunt = personalHunts.getHuntByDifficulty(difficulty)
            if (hunt == null || hunt.success != null || (hunt.endTime != null && hunt.endTime!! < System.currentTimeMillis()) || (hunt.expireTime < System.currentTimeMillis() && hunt.active.not())) {
                // if player ran out of time, notify them
                if (hunt?.endTime != null && hunt.endTime!! < System.currentTimeMillis()) {
                    val player = PM.getPlayer(playerName) ?: return
                    PM.sendText(player, LangConfig.config.huntTimeEnded)
                    cancelHunt(playerUUID, playerName, difficulty)

                }

                val randomHunt = allHunts.filter { it.difficulty == difficulty }.random()

                randomHunt.rewardPools.forEach { it.reward = it.getRolledReward() }

                val expireMinutes = when (difficulty) {
                    HuntDifficulties.EASY -> BaseConfig.config.easyRefreshTime
                    HuntDifficulties.MEDIUM -> BaseConfig.config.mediumRefreshTime
                    HuntDifficulties.HARD -> BaseConfig.config.hardRefreshTime
                    HuntDifficulties.LEGENDARY -> BaseConfig.config.legendaryRefreshTime
                    HuntDifficulties.GODLIKE -> BaseConfig.config.godlikeRefreshTime
                }

                val newHunt = HuntTracker(
                    UUID.randomUUID().toString(),
                    randomHunt,
                    rolledTime = System.currentTimeMillis(),
                    expireTime = System.currentTimeMillis() + expireMinutes * 60 * 1000,
                    startTime = null,
                    endTime = null,
                    active = false,
                    success = null
                )

                val ack = db.addRolledHuntTracker(newHunt)

                if (ack) {
                    personalHunts.setHuntByDifficulty(
                        difficulty, newHunt
                    )

                    db.updatePlayerPersonalHuntSession(playerUUID, personalHunts)
                }
            }
        }

        HuntDifficulties.values().forEach { rollHuntForDifficulty(it) }

        db.updatePlayerPersonalHuntSession(playerUUID, personalHunts)
    }

    override fun activateHunt(playerUUID: String, playerName: String, difficulty: HuntDifficulties): Boolean {
        val personalHunts = getPersonalHunts(playerUUID, playerName)
        val huntTracker = personalHunts.getHuntByDifficulty(difficulty) ?: return false

        if (!hasMinLevel(playerUUID, difficulty)) {
            PM.sendText(PM.getPlayer(playerName) ?: return false, LangConfig.config.huntLevelRequirement)
            return false
        }

        if (!hasEnoughBalance(playerUUID, huntTracker)) {
            PM.sendText(
                PM.getPlayer(playerName) ?: return false,
                LangConfig.config.notEnoughBalance.replace("{currency}", EconomyConfig.config.currencyName)
            )
            return false
        }

        if (huntTracker.active) return false
        if (huntTracker.expireTime < System.currentTimeMillis()) {
            PM.sendText(PM.getPlayer(playerName) ?: return false, LangConfig.config.huntExpired)
            return false
        }

        // check if player has any active hunt
        personalHunts.getHunts().forEach {
            if (it?.active == true) {
                PM.sendText(
                    PM.getPlayer(playerName) ?: return false, LangConfig.config.huntAlreadyActive
                )
                return false
            }
        }

        EconomyHandler.economy.withdraw(UUID.fromString(playerUUID), huntTracker.hunt.cost.toDouble())

        startHunt(huntTracker)

        val bossBarTextWithProgress = createBossBarText(huntTracker)
        val bossbar = createServerBossBar(bossBarTextWithProgress)

        val player = PM.getPlayer(playerName) ?: return false
        addPlayerToBossbar(bossbar, player, huntTracker.uuid)
        activeBossbars[huntTracker.uuid] = bossbar

        if (isInParty(playerUUID)) {
            val party = PartyHandler.db.getPlayerParty(playerUUID)
            party?.members?.forEach {
                val member = PM.getPlayer(it.name)
                if (member != null) {
                    addPlayerToBossbar(bossbar, member, huntTracker.uuid)
                    // replace the party member hunt with the same hunt
                    val memberPersonalHunt = getPersonalHunts(it.uuid, it.name)
                    memberPersonalHunt.setHuntByDifficulty(difficulty, huntTracker)
                }
            }
            huntTracker.participants.addAll(party?.members?.map { it.uuid } ?: listOf())
        } else {
            huntTracker.participants.add(playerUUID)
        }

        return true
    }

    override fun startHunt(huntTracker: HuntTracker) {
        val currentTime = System.currentTimeMillis()
        huntTracker.apply {
            active = true
            startTime = currentTime
            endTime = currentTime + hunt.timeLimitMinutes * 60 * 1000
        }
    }

    override fun cancelHunt(playerUUID: String, playerName: String, difficulty: HuntDifficulties) {
        val personalHunts = getPersonalHunts(playerUUID, playerName)
        val huntTracker = personalHunts.getHuntByDifficulty(difficulty) ?: return
        if (huntTracker.active) {
            huntTracker.active = false
            huntTracker.success = false
        }

        val bossbar = activeBossbars[huntTracker.uuid]
        after(ticks = 10) {
            bossbar?.clearPlayers()
            activeBossbars.remove(huntTracker.uuid)
        }

        val player = PM.getPlayer(playerName) ?: return
        if (isInParty(playerUUID)) {
            val party = PartyHandler.db.getPlayerParty(playerUUID)
            party?.members?.forEach {
                val member = PM.getPlayer(it.name)
                member?.sendMessage(
                    PM.returnStyledText(LangConfig.config.huntCancelled), false
                )
            }
        } else {
            player.sendMessage(
                PM.returnStyledText(LangConfig.config.huntCancelled), false
            )
        }
    }

    override fun joinOngoingHunt(player: ServerPlayerEntity, party: Party) {
        val playerUUID = player.uuidAsString
        val playerName = player.name.string
        val personalHunts = getPersonalHunts(playerUUID, playerName)
        personalHunts.getHunts().forEach {
            if (it?.active == true) {
                cancelHunt(playerUUID, playerName, it.hunt.difficulty)
            }
        }

        val owner = party.owner.let { PM.getPlayer(it.name) }
        val ownerUUID = owner?.uuidAsString
        val ownerPersonalHunt = ownerUUID?.let { getPersonalHunts(it, owner.name.string) }
        ownerPersonalHunt?.getHunts()?.forEach {
            if (it?.active == true) {
                val bossbar = activeBossbars[it.uuid]
                if (bossbar != null) {
                    addPlayerToBossbar(bossbar, player, it.uuid)
                    it.participants.add(playerUUID)
                }
            }
        }

        // override the player hunt with the owner hunt
        if (ownerPersonalHunt != null) {
            personalHunts.setHuntByDifficulty(
                HuntDifficulties.EASY, ownerPersonalHunt.getHuntByDifficulty(HuntDifficulties.EASY)
            )
        }

        player.sendMessage(
            PM.returnStyledText(LangConfig.config.partyJoinActiveHunt), false
        )
    }

    override fun completeHunt(playerUUID: String, playerName: String, difficulty: HuntDifficulties) {
        val personalHunts = getPersonalHunts(playerUUID, playerName)
        val huntTracker = personalHunts.getHuntByDifficulty(difficulty) ?: return
        if (huntTracker.active) {
            huntTracker.active = false
            huntTracker.success = true
        }

        if (huntTracker.rewarded) return
        rewardHunt(playerUUID, playerName, difficulty)

        huntTracker.participants.forEach {
            val player = PM.getPlayer(it) ?: return@forEach
            PM.sendText(player, LangConfig.config.huntCompleted)
        }

        val bossbar = activeBossbars[huntTracker.uuid]
        bossbar?.clearPlayers()
        activeBossbars.remove(huntTracker.uuid)
        rolledHunts.remove(huntTracker.uuid)
    }

    override fun rewardHunt(playerUUID: String, playerName: String, difficulty: HuntDifficulties) {
        val dbHandler = DatabaseHandler.db!! as MongoDBHandler
        val personalHunts = getPersonalHunts(playerUUID, playerName)
        val huntTracker = personalHunts.getHuntByDifficulty(difficulty) ?: return
        if (huntTracker.rewarded) return
        huntTracker.rewarded = true
        val rewardPools = huntTracker.hunt.rewardPools
        val rolledRewardIds = rewardPools.map { it.reward }
        val rolledRewards = rolledRewardIds.map { dbHandler.getRewardById(it.rewardId) }
        val baseRewardIds = huntTracker.hunt.guaranteedRewardId
        val baseRewards = baseRewardIds.map { dbHandler.getRewardById(it) }

        if (isInParty(playerUUID)) {
            val party = PartyHandler.db.getPlayerParty(playerUUID)
            val partySize = party?.members?.size ?: 1
            // split rolled rewards with splitable = true
            val splitableRolledReward = rolledRewards.filter { it?.splitable == true }.map { it?.deepCopy() }
            val nonSplitableReward = rolledRewards.filter { it?.splitable != true }
            val splitableGuaranteedReward = baseRewards.filter { it?.splitable == true }.map { it?.deepCopy() }
            val nonSplitableGuaranteedReward = baseRewards.filter { it?.splitable != true }
            splitableRolledReward.forEach {
                it?.amount = it?.amount?.div(partySize) ?: 0
                it?.displayItem!!.lore.add(
                    "<gray>${
                        LangConfig.config.splitRewardLore.replace(
                            "{party_size}", partySize.toString()
                        )
                    }"
                )
            }
            splitableGuaranteedReward.forEach {
                it?.amount = it?.amount?.div(partySize) ?: 0
                it?.displayItem!!.lore.add(
                    "<gray>${
                        LangConfig.config.splitRewardLore.replace(
                            "{party_size}", partySize.toString()
                        )
                    }"
                )
            }
            val rewards =
                splitableRolledReward + nonSplitableReward + splitableGuaranteedReward + nonSplitableGuaranteedReward

            val expPerPerson = huntTracker.hunt.experience.div(partySize)

            CoroutineScope(Dispatchers.IO).launch {
                party?.members?.forEach {
                    DatabaseHandler.db!!.addPlayerRewards(it.uuid, rewards.filterNotNull(), expPerPerson)
                }
            }
        } else {
            val rewards = rolledRewards + baseRewards
            CoroutineScope(Dispatchers.IO).launch {
                DatabaseHandler.db!!.addPlayerRewards(playerUUID, rewards.filterNotNull(), huntTracker.hunt.experience)
            }
        }
    }

    override fun onPokemonAction(player: ServerPlayerEntity, pokemon: Pokemon, goal: HuntGoals) {
        if (goal != HuntGoals.CATCH && pokemon.isWild().not()) return
        val personalHunts = getPersonalHunts(player.uuidAsString, player.name.string)
        personalHunts.getHunts().forEach { huntTracker ->
            if (huntTracker?.active == true) {
                val feature = huntTracker.hunt.huntFeature
                if (feature.checkRequirement(pokemon, goal)) {
                    huntTracker.progress++
                    val bossbar = activeBossbars[huntTracker.uuid]

                    if (isInParty(player.uuidAsString)) {
                        val party = PartyHandler.db.getPlayerParty(player.uuidAsString)
                        party?.members?.forEach {
                            val member = PM.getPlayer(it.name)
                            if (member != null) {
                                PM.sendText(
                                    member, LangConfig.config.huntProgressIncrease.replace(
                                        "{progress}", huntTracker.progress.toString() + "/" + huntTracker.hunt.amount
                                    )
                                )
                                val memberPersonalHunt = getPersonalHunts(it.uuid, it.name)
                                memberPersonalHunt.getHunts().forEach { memberHuntTracker ->
                                    if (memberHuntTracker?.active == true && memberHuntTracker.uuid == huntTracker.uuid) {
                                        val memberBossbar = activeBossbars[memberHuntTracker.uuid]
                                        memberBossbar?.name = PM.returnStyledText(createBossBarText(memberHuntTracker))
                                        if (memberHuntTracker.progress >= memberHuntTracker.hunt.amount) {
                                            completeHunt(it.uuid, it.name, memberHuntTracker.hunt.difficulty)

                                            UnvalidatedSound.playToPlayer(
                                                Identifier("minecraft", "ui.toast.challenge_complete"),
                                                SoundCategory.MASTER,
                                                0.75f,
                                                1.5f,
                                                member.blockPos,
                                                member.world,
                                                8.0,
                                                member
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        bossbar?.name = PM.returnStyledText(createBossBarText(huntTracker))
                        PM.sendText(
                            player, LangConfig.config.huntProgressIncrease.replace(
                                "{progress}", huntTracker.progress.toString() + "/" + huntTracker.hunt.amount
                            )
                        )
                        if (huntTracker.progress >= huntTracker.hunt.amount) {
                            completeHunt(player.uuidAsString, player.name.string, huntTracker.hunt.difficulty)
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
        }
    }

}