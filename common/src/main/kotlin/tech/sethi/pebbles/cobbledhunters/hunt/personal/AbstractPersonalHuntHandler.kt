package tech.sethi.pebbles.cobbledhunters.hunt.personal

import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.entity.boss.BossBar
import net.minecraft.entity.boss.ServerBossBar
import net.minecraft.server.network.ServerPlayerEntity
import tech.sethi.pebbles.cobbledhunters.config.baseconfig.BaseConfig
import tech.sethi.pebbles.cobbledhunters.data.DatabaseHandler
import tech.sethi.pebbles.cobbledhunters.economy.EconomyHandler
import tech.sethi.pebbles.cobbledhunters.hunt.type.HuntDifficulties
import tech.sethi.pebbles.cobbledhunters.hunt.type.HuntGoals
import tech.sethi.pebbles.cobbledhunters.hunt.type.HuntTracker
import tech.sethi.pebbles.cobbledhunters.hunt.type.PersonalHunts
import tech.sethi.pebbles.cobbledhunters.util.PM
import tech.sethi.pebbles.partyapi.dataclass.Party
import tech.sethi.pebbles.partyapi.datahandler.PartyHandler
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors

abstract class AbstractPersonalHuntHandler {
    abstract val personalHunts: ConcurrentHashMap<String, PersonalHunts>
    abstract val rolledHunts: ConcurrentHashMap<String, HuntTracker>
    abstract val activeBossbars: ConcurrentHashMap<String, ServerBossBar>

    val personalHuntWorker = Executors.newSingleThreadExecutor()

    open fun getPersonalHunts(playerUUID: String, playerName: String): PersonalHunts = personalHunts.getOrPut(playerUUID) {
        PersonalHunts(playerUUID, playerName, null, null, null, null, null)
    }


    abstract fun rollPersonalHunt(playerUUID: String, playerName: String)

    abstract fun activateHunt(playerUUID: String, playerName: String, difficulty: HuntDifficulties): Boolean

    abstract fun startHunt(huntTracker: HuntTracker)

    fun createBossBarText(huntTracker: HuntTracker): String =
        "${huntTracker.hunt.name} <yellow>${huntTracker.progress}/${huntTracker.hunt.amount}</yellow>"

    fun createServerBossBar(text: String): ServerBossBar =
        ServerBossBar(PM.returnStyledText(text), BossBar.Color.PURPLE, BossBar.Style.PROGRESS)

    fun addPlayerToBossbar(bossbar: ServerBossBar, player: ServerPlayerEntity, huntUUID: String) {
        bossbar.addPlayer(player)
        bossbar.name = PM.returnStyledText(createBossBarText(rolledHunts[huntUUID]!!))
    }

    abstract fun cancelHunt(playerUUID: String, playerName: String, difficulty: HuntDifficulties)

    abstract fun joinOngoingHunt(player: ServerPlayerEntity, party: Party)

    abstract fun completeHunt(playerUUID: String, playerName: String, difficulty: HuntDifficulties)

    abstract fun rewardHunt(playerUUID: String, playerName: String, difficulty: HuntDifficulties)

    abstract fun onPokemonAction(player: ServerPlayerEntity, pokemon: Pokemon, goal: HuntGoals)

    fun isInParty(playerUUID: String): Boolean =
        BaseConfig.config.enablePartyHunts && PartyHandler.db.getPlayerParty(playerUUID) != null

    fun hasMinLevel(playerUUID: String, difficulty: HuntDifficulties): Boolean {
        val playerLevel = DatabaseHandler.db!!.playerLevel(playerUUID)
        when (difficulty) {
            HuntDifficulties.EASY -> {
                if (playerLevel < BaseConfig.config.easyMinLevel) {
                    return false
                }
            }

            HuntDifficulties.MEDIUM -> {
                if (playerLevel < BaseConfig.config.mediumMinLevel) {
                    return false
                }
            }

            HuntDifficulties.HARD -> {
                if (playerLevel < BaseConfig.config.hardMinLevel) {
                    return false
                }
            }

            HuntDifficulties.LEGENDARY -> {
                if (playerLevel < BaseConfig.config.legendaryMinLevel) {
                    return false
                }
            }

            HuntDifficulties.GODLIKE -> {
                if (playerLevel < BaseConfig.config.godlikeMinLevel) {
                    return false
                }
            }
        }
        return true
    }

    fun hasEnoughBalance(playerUUID: String, huntTracker: HuntTracker): Boolean {
        val playerBalance = EconomyHandler.economy.getBalance(UUID.fromString(playerUUID))
        val cost = huntTracker.hunt.cost
        if (playerBalance < huntTracker.hunt.cost) return false
        if (cost > 0) EconomyHandler.economy.withdraw(UUID.fromString(playerUUID), cost.toDouble())

        return true
    }
}