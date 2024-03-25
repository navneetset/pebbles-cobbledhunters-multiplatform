package tech.sethi.pebbles.cobbledhunters.config.screenhandler

import tech.sethi.pebbles.cobbledhunters.config.ConfigHandler
import tech.sethi.pebbles.cobbledhunters.util.ConfigFileHandler
import java.io.File

object RewardScreenConfig {

    val gson = ConfigHandler.gson
    val rewardScreenConfigFile = File("config/pebbles-cobbledhunters/screens/reward-screen.json")

    var config = RewardScreenConfig()
    val rewardScreenConfigHandler = ConfigFileHandler(RewardScreenConfig::class.java, rewardScreenConfigFile, gson)

    init {
        reload()
    }

    fun reload() {
        rewardScreenConfigHandler.reload()
        config = rewardScreenConfigHandler.config
    }

    data class RewardScreenConfig(
        val title: String = "<blue>Reward Storage",
        val emptySlots: List<Int> = listOf(20, 21, 22, 23, 24),
        val emptySlotStack: ConfigHandler.SerializedItemStack = ConfigHandler.SerializedItemStack(
            "", "minecraft:gray_stained_glass_pane", 1, null, mutableListOf(
            )
        ),
        val rewardSlots: List<Int> = (0..17).toList(),
        val backSlots: List<Int> = listOf(19),
        val backSlotStack: ConfigHandler.SerializedItemStack = ConfigHandler.SerializedItemStack(
            "<gray>Back", "minecraft:gray_wool", 1, null, mutableListOf(
                "Click to go back!"
            )
        ),
        val navPrevSlots: List<Int> = listOf(18),
        val navPrevSlotStack: ConfigHandler.SerializedItemStack = ConfigHandler.SerializedItemStack(
            "<gray>Previous Page", "minecraft:arrow", 1, null, mutableListOf(
                "Click to go to the previous page!"
            )
        ),
        val navNextSlots: List<Int> = listOf(26),
        val navNextSlotStack: ConfigHandler.SerializedItemStack = ConfigHandler.SerializedItemStack(
            "<gray>Next Page", "minecraft:arrow", 1, null, mutableListOf(
                "Click to go to the next page!"
            )
        ),
        val expSlots: List<Int> = listOf(25),
        val expSlotStack: ConfigHandler.SerializedItemStack = ConfigHandler.SerializedItemStack(
            "<gray>Exp", "minecraft:experience_bottle", 1, null, mutableListOf(
                "Click to claim exp!", "<blue>Exp: <aqua>{exp}"
            )
        ),
    )
}