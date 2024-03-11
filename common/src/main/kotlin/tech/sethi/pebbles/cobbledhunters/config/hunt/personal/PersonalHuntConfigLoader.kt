package tech.sethi.pebbles.cobbledhunters.config.hunt.personal

import tech.sethi.pebbles.cobbledhunters.config.ConfigHandler
import tech.sethi.pebbles.cobbledhunters.hunt.type.Hunt
import tech.sethi.pebbles.cobbledhunters.util.ConfigDirectoryHandler
import java.io.File

object PersonalHuntConfigLoader {

    val gson = ConfigHandler.gson
    val personalHuntDirectory = File(ConfigHandler.configDirectory, "personal_hunts")

    val personalHuntFilesHandler = ConfigDirectoryHandler(
        Hunt::class.java, personalHuntDirectory, gson
    )

    var personalHunts = mutableListOf<Hunt>()

    init {
        reload()
    }

    fun reload() {
        personalHuntFilesHandler.reload()
        personalHunts = personalHuntFilesHandler.configs.toMutableList()
    }
}