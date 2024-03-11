package tech.sethi.pebbles.cobbledhunters.config.hunt.personal

import tech.sethi.pebbles.cobbledhunters.config.ConfigHandler
import tech.sethi.pebbles.cobbledhunters.hunt.type.Hunt
import tech.sethi.pebbles.cobbledhunters.util.ConfigDirectoryHandler
import java.io.File

object PersonalHuntSessionConfigLoader {

    val gson = ConfigHandler.gson
    val personalHuntSessionDirectory = File(ConfigHandler.configDirectory, "personal_hunts_session")

    val personalHuntFilesHandler = ConfigDirectoryHandler(
        Hunt::class.java, personalHuntSessionDirectory, gson
    )

    var personalHunts = mutableListOf<Hunt>()

    init {
        reload()
    }

    fun reload() {
        personalHuntFilesHandler.reload()
        personalHunts = personalHuntFilesHandler.configs.toMutableList()
    }

    fun createHunt(hunt: Hunt) {
        personalHunts += hunt
        personalHuntSessionDirectory.mkdirs()
        val configString = gson.toJson(hunt)
        val file = File(personalHuntSessionDirectory, "${hunt.id}.json")
        file.writeText(configString)
    }
}