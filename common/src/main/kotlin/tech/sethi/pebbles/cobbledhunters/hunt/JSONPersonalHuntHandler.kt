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
import tech.sethi.pebbles.cobbledhunters.config.economy.EconomyConfig
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
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object JSONPersonalHuntHandler : AbstractPersonalHuntHandler() {
    override val personalHunts = ConcurrentHashMap<String, PersonalHunts>()
    override val rolledHunts = ConcurrentHashMap<String, HuntTracker>()
    override val activeBossbars = ConcurrentHashMap<String, ServerBossBar>()

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
                            .any { it == null || it.success != null || (it.endTime != null && it.endTime!! < System.currentTimeMillis()) || (it.expireTime < System.currentTimeMillis() && it.active.not()) }
                    ) {
                        rollPersonalHunt(playerUUID, personalHunt.playerName)
                    }
                }

                // update bossbar time progress
                activeBossbars.forEach { (uuid, bossbar) ->
                    val huntTracker = rolledHunts[uuid] ?: return@forEach
                    if (huntTracker.active) {
                        // countdown time by depleting the progress
                        val timeLeft = huntTracker.endTime!! - System.currentTimeMillis()
                        val timeProgress = (timeLeft.toDouble() / (huntTracker.endTime!! - huntTracker.startTime!!)).toFloat()
                        bossbar.percent = timeProgress
                    }
                }

                delay(1000)
            }
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
                            cancelHunt(playerUUID, playerUuid, it.hunt.difficulty)
                            player.sendMessage(
                                PM.returnStyledText(LangConfig.langConfig.partyLeaveCancelHunt), false
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
            if (hunt == null || hunt.success != null || (hunt.endTime != null && hunt.endTime!! < System.currentTimeMillis()) || (hunt.expireTime < System.currentTimeMillis() && hunt.active.not())) {
                // if player ran out of time, notify them
                if (hunt?.endTime != null && hunt.endTime!! < System.currentTimeMillis()) {
                    val player = PM.getPlayer(playerName) ?: return
                    PM.sendText(player, LangConfig.langConfig.huntTimeEnded)
                    cancelHunt(playerUUID, playerName, difficulty)
                }

                val randomHunt = allHunts.filter { it.difficulty == difficulty }.random()
                randomHunt.rewardPools.forEach {
                    it.reward = it.getRolledReward()
                }
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
                personalHunts.setHuntByDifficulty(
                    difficulty, newHunt
                )
            }
        }

        HuntDifficulties.values().forEach { rollHuntForDifficulty(it) }
    }

    fun activateHunt(playerUUID: String, playerName: String, difficulty: HuntDifficulties): Boolean {
        val personalHunts = getPersonalHunts(playerUUID, playerName)
        val huntTracker = personalHunts.getHuntByDifficulty(difficulty) ?: return false

        if (!hasMinLevel(playerUUID, difficulty)) {
            PM.sendText(PM.getPlayer(playerName) ?: return false, LangConfig.langConfig.huntLevelRequirement)
            return false
        }

        if (!hasEnoughBalance(playerUUID, huntTracker)) {
            PM.sendText(
                PM.getPlayer(playerName) ?: return false,
                LangConfig.langConfig.notEnoughBalance.replace("{currency}", EconomyConfig.economyConfig.currencyName)
            )
            return false
        }

        if (huntTracker.active) return false
        if (huntTracker.expireTime < System.currentTimeMillis()) {
            PM.sendText(PM.getPlayer(playerName) ?: return false, LangConfig.langConfig.huntExpired)
            return false
        }

        // check if player has any active hunt
        personalHunts.getHunts().forEach {
            if (it?.active == true) {
                PM.sendText(
                    PM.getPlayer(playerName) ?: return false, LangConfig.langConfig.huntAlreadyActive
                )
                return false
            }
        }

        startHunt(huntTracker)
        val bossBarTextWithProgress = createBossBarText(huntTracker)
        val bossbar = createServerBossBar(bossBarTextWithProgress)

        val player = PM.getPlayer(playerName) ?: return false
        addPlayerToBossbar(bossbar, player, huntTracker.uuid)
        activeBossbars[huntTracker.uuid] = bossbar

        if (isInParty(playerUUID)) {
            val party = PartyHandler.db.getPlayerParty(playerUUID)
            if (party != null) {
                party.members.forEach {
                    val member = PM.getPlayer(it.name)
                    if (member != null) {
                        addPlayerToBossbar(bossbar, member, huntTracker.uuid)
                        // replace the party member hunt with the same hunt
                        val memberPersonalHunt = getPersonalHunts(it.uuid, it.name)
                        memberPersonalHunt.setHuntByDifficulty(difficulty, huntTracker)
                    }
                }
            }
            huntTracker.participants.addAll(party?.members?.map { it.uuid } ?: listOf())
        } else {
            huntTracker.participants.add(playerUUID)
        }

        return true
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

    private fun addPlayerToBossbar(bossbar: ServerBossBar, player: ServerPlayerEntity, huntUUID: String) {
        bossbar.addPlayer(player)
        bossbar.name = PM.returnStyledText(createBossBarText(rolledHunts[huntUUID]!!))
    }

    fun cancelHunt(playerUUID: String, playerName: String, difficulty: HuntDifficulties) {
        val personalHunts = getPersonalHunts(playerUUID, playerName)
        val huntTracker = personalHunts.getHuntByDifficulty(difficulty) ?: return
        if (huntTracker.active) {
            huntTracker.active = false
            huntTracker.success = false
        }

        val bossbar = activeBossbars[huntTracker.uuid]
        bossbar?.clearPlayers()
        activeBossbars.remove(huntTracker.uuid)

        val player = PM.getPlayer(playerName) ?: return
        player.sendMessage(
            PM.returnStyledText(LangConfig.langConfig.huntCancelled), false
        )
    }

    fun joinOngoingHunt(player: ServerPlayerEntity, party: Party) {
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
            PM.returnStyledText(LangConfig.langConfig.partyJoinActiveHunt), false
        )
    }

    fun completeHunt(playerUUID: String, playerName: String, difficulty: HuntDifficulties) {
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
            PM.sendText(player, LangConfig.langConfig.huntCompleted)
        }

        val bossbar = activeBossbars[huntTracker.uuid]
        bossbar?.clearPlayers()
        activeBossbars.remove(huntTracker.uuid)
        rolledHunts.remove(huntTracker.uuid)
    }

    fun rewardHunt(playerUUID: String, playerName: String, difficulty: HuntDifficulties) {
        val personalHunts = getPersonalHunts(playerUUID, playerName)
        val huntTracker = personalHunts.getHuntByDifficulty(difficulty) ?: return
        if (huntTracker.rewarded) return
        huntTracker.rewarded = true
        val rewardPools = huntTracker.hunt.rewardPools
        val rolledRewardIds = rewardPools.map { it.reward }
        val rolledRewards = rolledRewardIds.map { RewardConfigLoader.getRewardById(it.rewardId) }
        val baseRewardIds = huntTracker.hunt.guaranteedRewardId
        val baseRewards = baseRewardIds.map { RewardConfigLoader.getRewardById(it) }

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
                        LangConfig.langConfig.splitRewardLore.replace(
                            "{party_size}", partySize.toString()
                        )
                    }"
                )
            }
            splitableGuaranteedReward.forEach {
                it?.amount = it?.amount?.div(partySize) ?: 0
                it?.displayItem!!.lore.add(
                    "<gray>${
                        LangConfig.langConfig.splitRewardLore.replace(
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

    fun onPokemonCaptured(player: ServerPlayerEntity, pokemon: Pokemon) {
        val personalHunts = getPersonalHunts(player.uuidAsString, player.name.string)
        personalHunts.getHunts().forEach { huntTracker ->
            if (huntTracker?.active == true) {
                if (huntTracker.hunt.huntFeature.checkRequirement(pokemon)) {
                    huntTracker.progress++
                    val bossbar = activeBossbars[huntTracker.uuid]

                    if (isInParty(player.uuidAsString)) {
                        val party = PartyHandler.db.getPlayerParty(player.uuidAsString)
                        party?.members?.forEach {
                            val member = PM.getPlayer(it.name)
                            if (member != null) {
                                val memberPersonalHunt = getPersonalHunts(it.uuid, it.name)
                                memberPersonalHunt.getHunts().forEach { memberHuntTracker ->
                                    if (memberHuntTracker?.active == true && memberHuntTracker.uuid == huntTracker.uuid) {
                                        memberHuntTracker.progress++
                                        val memberBossbar = activeBossbars[memberHuntTracker.uuid]
                                        memberBossbar?.name = PM.returnStyledText(createBossBarText(memberHuntTracker))
                                        if (memberHuntTracker.progress >= memberHuntTracker.hunt.amount) {
                                            completeHunt(it.uuid, it.name, memberHuntTracker.hunt.difficulty)
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        bossbar?.name = PM.returnStyledText(createBossBarText(huntTracker))
                        if (huntTracker.progress >= huntTracker.hunt.amount) {
                            completeHunt(player.uuidAsString, player.name.string, huntTracker.hunt.difficulty)
                        }
                    }
                }
            }
        }
    }

    fun isInParty(playerUUID: String): Boolean {
        return BaseConfig.config.enablePartyHunts && PartyHandler.db.getPlayerParty(playerUUID) != null
    }

}