package tech.sethi.pebbles.cobbledhunters.config.reward

import com.google.gson.GsonBuilder
import dev.architectury.event.events.common.LifecycleEvent
import kotlinx.coroutines.*
import tech.sethi.pebbles.cobbledhunters.config.ConfigHandler
import tech.sethi.pebbles.cobbledhunters.hunt.type.RewardStorage
import tech.sethi.pebbles.cobbledhunters.hunt.type.RewardsDeserializer
import java.io.File

object RewardStorageConfigLoader {

    val gson = GsonBuilder()
//        .registerTypeAdapter(RewardStorage::class.java, RewardsDeserializer())
        .setPrettyPrinting().disableHtmlEscaping().create()

    val rewardStorageDirectory = File(ConfigHandler.configDirectory, "reward_storage")

    val rewardJob = Dispatchers.IO + Job()

    init {
        rewardStorageDirectory.mkdirs()

        LifecycleEvent.SERVER_STOPPING.register {
            rewardJob.job.cancel()
        }
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

    @Synchronized
    fun save(rewardStorage: RewardStorage) {
        CoroutineScope(rewardJob).launch {
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