package tech.sethi.pebbles.cobbledhunters.config.hunt.global

import tech.sethi.pebbles.cobbledhunters.config.ConfigHandler
import tech.sethi.pebbles.cobbledhunters.hunt.type.Hunt
import tech.sethi.pebbles.cobbledhunters.util.ConfigDirectoryHandler
import java.io.File

object GlobalHuntConfigLoader {

    val gson = ConfigHandler.gson
    val globalHuntDirectory = File(ConfigHandler.configDirectory, "global_hunts")

    val globalHuntFilesHandler = ConfigDirectoryHandler(
        Hunt::class.java, globalHuntDirectory, gson
    )

    var globalHunts = listOf<Hunt>()

    init {
        reload()
    }

    fun reload() {
        globalHuntFilesHandler.reload()
        globalHunts = globalHuntFilesHandler.configs
    }

    fun createHunt(hunt: Hunt) {
        globalHunts += hunt
        globalHuntDirectory.mkdirs()
        val configString = gson.toJson(hunt)
        val file = File(globalHuntDirectory, "${hunt.id}.json")
        file.writeText(configString)
    }
}