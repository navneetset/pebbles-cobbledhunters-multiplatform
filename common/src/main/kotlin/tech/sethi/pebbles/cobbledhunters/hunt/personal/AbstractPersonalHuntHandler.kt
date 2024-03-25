package tech.sethi.pebbles.cobbledhunters.hunt.personal

import net.minecraft.entity.boss.ServerBossBar
import tech.sethi.pebbles.cobbledhunters.config.baseconfig.BaseConfig
import tech.sethi.pebbles.cobbledhunters.data.DatabaseHandler
import tech.sethi.pebbles.cobbledhunters.economy.EconomyHandler
import tech.sethi.pebbles.cobbledhunters.hunt.type.HuntDifficulties
import tech.sethi.pebbles.cobbledhunters.hunt.type.HuntTracker
import tech.sethi.pebbles.cobbledhunters.hunt.type.PersonalHunts
import java.util.*
import java.util.concurrent.ConcurrentHashMap

abstract class AbstractPersonalHuntHandler {
    abstract val personalHunts: ConcurrentHashMap<String, PersonalHunts>
    abstract val rolledHunts: ConcurrentHashMap<String, HuntTracker>
    abstract val activeBossbars: ConcurrentHashMap<String, ServerBossBar>


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