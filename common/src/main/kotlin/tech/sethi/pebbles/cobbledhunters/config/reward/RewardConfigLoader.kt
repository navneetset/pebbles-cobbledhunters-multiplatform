package tech.sethi.pebbles.cobbledhunters.config.reward

import tech.sethi.pebbles.cobbledhunters.config.ConfigHandler
import tech.sethi.pebbles.cobbledhunters.hunt.type.HuntReward
import tech.sethi.pebbles.cobbledhunters.util.ConfigDirectoryHandler
import java.io.File

object RewardConfigLoader {

    val gson = ConfigHandler.gson
    val rewardDirectory = File(ConfigHandler.configDirectory, "reward")

    val rewardFilesHandler = ConfigDirectoryHandler(
        HuntReward::class.java, rewardDirectory, gson
    )

    var rewards = mutableListOf<HuntReward>()

    init {
        reload()
    }

    fun reload() {
        rewardFilesHandler.reload()
        rewards = rewardFilesHandler.configs.toMutableList()
    }

    fun createReward(reward: HuntReward) {
        rewards += reward
        rewardDirectory.mkdirs()
        val configString = gson.toJson(reward)
        val file = File(rewardDirectory, "${reward.id}.json")
        file.writeText(configString)
    }

    fun getRewardById(id: String): HuntReward? {
        return rewards.find { it.id == id }
    }
}