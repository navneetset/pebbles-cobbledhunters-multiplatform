package tech.sethi.pebbles.cobbledhunters.config.hunt.global

import tech.sethi.pebbles.cobbledhunters.config.ConfigHandler
import tech.sethi.pebbles.cobbledhunters.hunt.type.HuntPool
import tech.sethi.pebbles.cobbledhunters.util.ConfigDirectoryHandler
import java.io.File

object GlobalHuntPoolConfigLoader {

    val gson = ConfigHandler.gson
    val globalHuntPoolDirectory = File(ConfigHandler.configDirectory, "global_hunts_pool")

    val globalHuntPoolFilesHandler = ConfigDirectoryHandler(
        HuntPool::class.java, globalHuntPoolDirectory, gson
    )

    var globalHuntsPool = listOf<HuntPool>()

    init {
        reload()
    }

    fun reload() {
        globalHuntPoolFilesHandler.reload()
        globalHuntsPool = globalHuntPoolFilesHandler.configs
    }

    fun createHuntPool(huntPool: HuntPool) {
        globalHuntsPool += huntPool
        globalHuntPoolDirectory.mkdirs()
        val configString = gson.toJson(huntPool)
        val file = File(globalHuntPoolDirectory, "${huntPool.id}.json")
        file.writeText(configString)
    }
}