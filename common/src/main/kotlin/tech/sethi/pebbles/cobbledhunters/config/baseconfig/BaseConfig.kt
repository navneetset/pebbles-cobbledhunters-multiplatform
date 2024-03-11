package tech.sethi.pebbles.cobbledhunters.config.baseconfig

import tech.sethi.pebbles.cobbledhunters.config.ConfigHandler
import tech.sethi.pebbles.cobbledhunters.util.ConfigFileHandler
import java.io.File

object BaseConfig {
    val gson = ConfigHandler.gson
    val baseConfigFile = File("config/pebbles-cobbledhunters/base-config.json")

    private val datastoreConfigHandler = ConfigFileHandler(BaseConfig::class.java, baseConfigFile, gson)


    var baseConfig = BaseConfig()

    init {
        reload()
    }

    fun reload() {
        datastoreConfigHandler.reload()
        baseConfig = datastoreConfigHandler.config
    }


    data class BaseConfig(
        val easyMinLevel: Int = 0,
        val mediumMinLevel: Int = 20,
        val hardMinLevel: Int = 40,
        val legendaryMinLevel: Int = 60,
        val godlikeMinLevel: Int = 80,
        val easyRefreshTime: Int = 60,
        val mediumRefreshTime: Int = 120,
        val hardRefreshTime: Int = 180,
        val legendaryRefreshTime: Int = 240,
        val godlikeRefreshTime: Int = 300,
        val expPerLevel: Int = 100,
        val enablePartyHunts: Boolean = true,
        val enableGlobalHunts: Boolean = true,
        val enablePersonalHunts: Boolean = true
    )
}