package tech.sethi.pebbles.cobbledhunters.config.screenhandler

import tech.sethi.pebbles.cobbledhunters.config.ConfigHandler
import tech.sethi.pebbles.cobbledhunters.util.ConfigFileHandler
import java.io.File

object PersonalStatsScreenConfig {

    val gson = ConfigHandler.gson
    val personalHuntScreenConfigFile = File("config/pebbles-cobbledhunters/screens/personal-stats-screen.json")

    var config = PersonalStatsScreen()
    val personalStatsFileHandler =
        ConfigFileHandler(PersonalStatsScreen::class.java, personalHuntScreenConfigFile, gson)

    init {
        reload()
    }

    fun reload() {
        personalStatsFileHandler.reload()
        config = personalStatsFileHandler.config
    }

    data class PersonalStatsScreen(
        val title: String = "<blue>Personal Stats",
        val playerHeadSlot: Int = 12,
        val levelSlots: List<Int> = listOf(14),
        val levelSlotStack: ConfigHandler.SerializedItemStack = ConfigHandler.SerializedItemStack(
            displayName = "<gray>Level",
            material = "minecraft:experience_bottle",
            amount = 1,
            nbt = null,
            lore = mutableListOf(
                "<gray>Level: <yellow>{level}",
                "<gray>Experience: <light_purple>{exp}",
            )
        ),
        val backSlots: List<Int> = listOf(18),
        val backSlotStack: ConfigHandler.SerializedItemStack = ConfigHandler.SerializedItemStack(
            displayName = "<gray>Back", material = "minecraft:gray_wool", amount = 1, nbt = null, lore = mutableListOf(
                "Click to go back!"
            )
        ),
        val emptySlots: List<Int> = listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 19, 20, 21, 22, 23, 24, 25, 26),
        val emptySlotStack: ConfigHandler.SerializedItemStack = ConfigHandler.SerializedItemStack(
            displayName = "<gray>",
            material = "minecraft:blue_stained_glass_pane",
            amount = 1,
            nbt = null,
            lore = mutableListOf(
                " "
            )
        )
    )
}