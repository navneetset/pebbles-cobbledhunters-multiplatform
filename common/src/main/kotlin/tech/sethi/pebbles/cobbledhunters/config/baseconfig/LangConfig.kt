package tech.sethi.pebbles.cobbledhunters.config.baseconfig

import tech.sethi.pebbles.cobbledhunters.config.ConfigHandler
import tech.sethi.pebbles.cobbledhunters.util.ConfigFileHandler
import java.io.File

object LangConfig {
    val gson = ConfigHandler.gson
    val langConfigFile = File("config/pebbles-cobbledhunters/lang.json")

    private val langConfigHandler = ConfigFileHandler(Lang::class.java, langConfigFile, gson)

    var langConfig = Lang()

    init {
        reload()
    }

    fun reload() {
        langConfigHandler.reload()
        langConfig = langConfigHandler.config
    }

    data class Lang(
        val partyLeaveCancelHunt: String = "<gold>[CobbledHunters] <red>Hunt has been cancelled due to lost of party status",
    )
}