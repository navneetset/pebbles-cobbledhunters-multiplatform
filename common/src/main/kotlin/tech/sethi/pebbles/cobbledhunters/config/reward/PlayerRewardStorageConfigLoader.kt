package tech.sethi.pebbles.cobbledhunters.config.reward

import tech.sethi.pebbles.cobbledhunters.config.ConfigHandler
import tech.sethi.pebbles.cobbledhunters.hunt.type.RewardStorage
import tech.sethi.pebbles.cobbledhunters.util.ConfigDirectoryHandler
import java.io.File

object PlayerRewardStorageConfigLoader {

    val gson = ConfigHandler.gson
    val rewardStorageDirectory = File(ConfigHandler.configDirectory, "reward_storage")

    val rewardStorageFileHandler = ConfigDirectoryHandler(
        RewardStorage::class.java, rewardStorageDirectory, gson
    )

    var rewardStorages = mutableListOf<RewardStorage>()

    init {
        reload()
    }

    fun reload() {
        rewardStorageFileHandler.reload()
        rewardStorages = rewardStorageFileHandler.configs.toMutableList()
    }

    fun createRewardStorage(playerUUID: String, playerName: String) {
        if (rewardStorages.any { it.playerUUID == playerUUID }) return
        val rewardStorage = RewardStorage(playerUUID, playerName)
        rewardStorages.add(rewardStorage)
        rewardStorageDirectory.mkdirs()
        val configString = gson.toJson(rewardStorage)
        val file = File(rewardStorageDirectory, "${playerUUID}.json")
        file.writeText(configString)
    }

    fun save(playerUUID: String) {
        val rewardStorage = rewardStorages.find { it.playerUUID == playerUUID } ?: return
        val configString = gson.toJson(rewardStorage)
        val file = File(rewardStorageDirectory, "${playerUUID}.json")
        file.writeText(configString)
    }
}