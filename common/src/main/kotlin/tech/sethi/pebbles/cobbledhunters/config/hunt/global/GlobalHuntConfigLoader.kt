package tech.sethi.pebbles.cobbledhunters.config.hunt.global

import tech.sethi.pebbles.cobbledhunters.config.ConfigHandler
import tech.sethi.pebbles.cobbledhunters.hunt.type.GlobalHunt
import tech.sethi.pebbles.cobbledhunters.hunt.type.Hunt
import tech.sethi.pebbles.cobbledhunters.util.ConfigDirectoryHandler
import java.io.File

object GlobalHuntConfigLoader {

    val gson = ConfigHandler.gson
    val globalHuntDirectory = File(ConfigHandler.configDirectory, "global_hunts")

    val globalHuntFilesHandler = ConfigDirectoryHandler(
        GlobalHunt::class.java, globalHuntDirectory, gson
    )

    var globalHunts = listOf<GlobalHunt>()

    init {
        reload()
    }

    fun reload() {
        globalHuntFilesHandler.reload()
        globalHunts = globalHuntFilesHandler.configs
    }

    fun createHunt(globalHunt: GlobalHunt) {
        globalHunts += globalHunt
        globalHuntDirectory.mkdirs()
        val configString = gson.toJson(globalHunt)
        val file = File(globalHuntDirectory, "${globalHunt.id}.json")
        file.writeText(configString)
    }
}