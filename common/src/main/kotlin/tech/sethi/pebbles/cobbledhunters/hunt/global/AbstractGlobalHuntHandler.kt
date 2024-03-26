package tech.sethi.pebbles.cobbledhunters.hunt.global

import net.minecraft.entity.boss.BossBar
import net.minecraft.entity.boss.ServerBossBar
import net.minecraft.server.network.ServerPlayerEntity
import tech.sethi.pebbles.cobbledhunters.config.baseconfig.BaseConfig
import tech.sethi.pebbles.cobbledhunters.config.baseconfig.LangConfig
import tech.sethi.pebbles.cobbledhunters.config.hunt.global.GlobalHuntConfigLoader
import tech.sethi.pebbles.cobbledhunters.config.hunt.global.GlobalHuntPoolConfigLoader
import tech.sethi.pebbles.cobbledhunters.data.DatabaseHandler
import tech.sethi.pebbles.cobbledhunters.economy.EconomyHandler
import tech.sethi.pebbles.cobbledhunters.hunt.personal.JSONPersonalHuntHandler
import tech.sethi.pebbles.cobbledhunters.hunt.type.*
import tech.sethi.pebbles.cobbledhunters.util.PM
import java.util.*
import java.util.concurrent.ConcurrentHashMap

abstract class AbstractGlobalHuntHandler {
    val globalHuntPools: ConcurrentHashMap<String, GlobalHuntTracker?> = ConcurrentHashMap()
    val activeBossbars: ConcurrentHashMap<String, ServerBossBar> = ConcurrentHashMap()


    fun canJoinHunt(player: ServerPlayerEntity, poolId: String): Pair<Boolean, String> {
        val tracker = globalHuntPools[poolId] ?: return Pair(false, LangConfig.config.huntNotFound)
        if (tracker.expired()) return Pair(false, LangConfig.config.huntExpired)
        if (tracker.isCompleted()) return Pair(false, LangConfig.config.huntAlreadyCompleted)
        if (tracker.isParticipant(player.uuidAsString)) return Pair(false, LangConfig.config.alreadyParticipating)
        if (tracker.hunt.cost > 0) {
            val balance = EconomyHandler.economy.getBalance(player.uuid)
            if (balance < tracker.hunt.cost) return Pair(false, LangConfig.config.notEnoughBalance)
        }

        return Pair(true, LangConfig.config.successfulJoin)
    }

    abstract fun joinHunt(player: ServerPlayerEntity, poolId: String)

    fun participatingHunt(player: ServerPlayerEntity): GlobalHuntTracker? {
        return globalHuntPools.values.firstOrNull { it?.isParticipant(player.uuidAsString) == true }
    }

    fun generateBossBar(poolId: String) {
        val tracker = globalHuntPools[poolId] ?: return
        val bossbar = createServerBossBar(createBossBarText(tracker))
        activeBossbars[poolId] = bossbar
    }

    private fun createServerBossBar(text: String): ServerBossBar {
        return ServerBossBar(PM.returnStyledText(text), BossBar.Color.PURPLE, BossBar.Style.PROGRESS)
    }

    fun createBossBarText(tracker: GlobalHuntTracker) =
        "${tracker.hunt.name} <yellow>${tracker.getProgress()}/${tracker.hunt.amount}</yellow>"

    fun addPlayerToBossbar(bossbar: ServerBossBar, player: ServerPlayerEntity, poolId: String) {
        bossbar.addPlayer(player)
        bossbar.name = globalHuntPools[poolId]?.let { createBossBarText(it) }?.let { PM.returnStyledText(it) }
    }

}