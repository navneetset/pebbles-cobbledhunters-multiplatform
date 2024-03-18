package tech.sethi.pebbles.cobbledhunters.config.screenhandler

import tech.sethi.pebbles.cobbledhunters.config.ConfigHandler
import tech.sethi.pebbles.cobbledhunters.hunt.type.HuntDifficulties
import tech.sethi.pebbles.cobbledhunters.util.ConfigFileHandler
import java.io.File

object PersonalHuntDetailScreenConfig {

    val gson = ConfigHandler.gson
    val personalHuntScreenConfigFile = File("config/pebbles-cobbledhunters/screens/personalhunt-detail-screen.json")

    var config = PersonalHuntDetailConfig()
    val personalHuntDetailFileHandler =
        ConfigFileHandler(PersonalHuntDetailConfig::class.java, personalHuntScreenConfigFile, gson)

    init {
        reload()
    }

    fun reload() {
        personalHuntDetailFileHandler.reload()
        config = personalHuntDetailFileHandler.config
    }

    data class PersonalHuntDetailConfig(
        val title: String = "{hunt_name}",
        val rewardSlots: List<Int> = (0..35).toList(),
        val startSlots: List<Int> = listOf(53),
        val startSlotStack: ConfigHandler.SerializedItemStack = ConfigHandler.SerializedItemStack(
            displayName = "<green>Start Hunt", material = "minecraft:lime_wool", amount = 1, nbt = null, lore = mutableListOf(
                "Click to start the hunt!"
            )
        ),
        val cancelSlots: List<Int> = listOf(52),
        val cancelSlotStack: ConfigHandler.SerializedItemStack = ConfigHandler.SerializedItemStack(
            displayName = "<red>Cancel Hunt", material = "minecraft:red_wool", amount = 1, nbt = null, lore = mutableListOf(
                "Click to cancel the hunt!"
            )
        ),
        val backSlots: List<Int> = listOf(45),
        val backSlotStack: ConfigHandler.SerializedItemStack = ConfigHandler.SerializedItemStack(
            displayName = "<gray>Back", material = "minecraft:gray_wool", amount = 1, nbt = null, lore = mutableListOf(
                "Click to go back!"
            )
        ),
        val huntInfoSlots: List<Int> = listOf(44),
        val huntInfoSlotStack: ConfigHandler.SerializedItemStack = ConfigHandler.SerializedItemStack(
            displayName = "<gray>Hunt Info", material = "minecraft:paper", amount = 1, nbt = null, lore = mutableListOf(
                "<white><b>Hunt Info", "{hunt_info}"
            )
        ),
        val emptySlots: List<Int> = listOf(),
        val emptySlotItemStack: ConfigHandler.SerializedItemStack = ConfigHandler.SerializedItemStack(
            displayName = "<gray>",
            material = "minecraft:gray_stained_glass_pane",
            amount = 1,
            nbt = null,
            lore = mutableListOf(
                " "
            )
        )
    )

    data class SlotConfig(
        val slot: Int,
        val itemStack: ConfigHandler.SerializedItemStack,
    )
}