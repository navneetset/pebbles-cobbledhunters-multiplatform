package tech.sethi.pebbles.cobbledhunters.config.reward

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tech.sethi.pebbles.cobbledhunters.config.ConfigHandler
import tech.sethi.pebbles.cobbledhunters.hunt.type.RewardStorage
import java.io.File

object RewardStorageConfigLoader {

    val gson = ConfigHandler.gson
    val rewardStorageDirectory = File(ConfigHandler.configDirectory, "reward_storage")

    init {
        rewardStorageDirectory.mkdirs()
    }

    fun createRewardStorage(playerUUID: String, playerName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val file = File(rewardStorageDirectory, "$playerUUID.json")
            if (!file.exists()) {
                val rewardStorage = RewardStorage(playerUUID, playerName)
                val rewardStorageString = gson.toJson(rewardStorage)
                file.writeText(rewardStorageString)
            }
        }
    }

    fun save(rewardStorage: RewardStorage) {
        CoroutineScope(Dispatchers.IO).launch {
            val file = File(rewardStorageDirectory, "${rewardStorage.playerUUID}.json")
            val rewardStorageString = gson.toJson(rewardStorage)
            file.writeText(rewardStorageString)
        }
    }

    fun getRewardStorage(playerUUID: String): RewardStorage {
        val file = File(rewardStorageDirectory, "$playerUUID.json")
        val rewardStorageString = file.readText()
        return gson.fromJson(rewardStorageString, RewardStorage::class.java)
    }
}