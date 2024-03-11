package tech.sethi.pebbles.cobbledhunters.config.economy

import tech.sethi.pebbles.cobbledhunters.config.ConfigHandler
import tech.sethi.pebbles.cobbledhunters.util.ConfigFileHandler
import java.io.File

object EconomyConfig {
    val gson = ConfigHandler.gson
    val economyConfigFile = File("config/pebbles-cobbledhunters/economy/economy.json")

    private val economyConfigHandler = ConfigFileHandler(EconomyConfig::class.java, economyConfigFile, gson)

    var economyConfig = EconomyConfig()

    init {
        reload()
        EconomyReadMe
    }

    fun reload() {
        economyConfigHandler.reload()
        economyConfig = economyConfigHandler.config
    }


    enum class EconomyType {
        PEBBLES, VAULT, IMPACTOR
    }

    data class EconomyConfig(
        val economy: EconomyType = EconomyType.IMPACTOR,
        val currencySymbol: String = "₽",
        val currencyName: String = "Pebbles"
    )
}