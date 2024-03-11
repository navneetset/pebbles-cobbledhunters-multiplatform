package tech.sethi.pebbles.cobbledhunters.config.screenhandler

import tech.sethi.pebbles.cobbledhunters.config.ConfigHandler
import tech.sethi.pebbles.cobbledhunters.hunt.type.HuntDifficulties
import tech.sethi.pebbles.cobbledhunters.util.ConfigFileHandler
import java.io.File

object PersonalHuntScreenConfig {

    val gson = ConfigHandler.gson
    val personalHuntScreenConfigFile = File("config/pebbles-cobbledhunters/screens/personalhunt-screen.json")

    var config = SelectionScreenConfig()
    val selectionScreenFileHandler =
        ConfigFileHandler(SelectionScreenConfig::class.java, personalHuntScreenConfigFile, gson)

    init {
        reload()
    }

    fun reload() {
        selectionScreenFileHandler.reload()
        config = selectionScreenFileHandler.config
    }

    data class SelectionScreenConfig(
        val title: String = "<blue>Personal Hunts", val slots: List<SlotConfig> = listOf(
            SlotConfig(
                slot = 0, difficulty = HuntDifficulties.EASY, itemStack = ConfigHandler.SerializedItemStack(
                    displayName = "<green>Easy Hunt",
                    material = "minecraft:lime_wool",
                    amount = 1,
                    nbt = null,
                    lore = listOf(
                        "<gray>Difficulty: <green>Easy",
                        "<gray>Minimum Level: <green>0",
                        "Refreshing: {refreshing_time}",
                        "Click to view rewards!",
                        "",
                        "{ongoing_hunt}"
                    )
                )
            ), SlotConfig(
                slot = 2, difficulty = HuntDifficulties.MEDIUM, itemStack = ConfigHandler.SerializedItemStack(
                    displayName = "<yellow>Medium Hunt",
                    material = "minecraft:yellow_wool",
                    amount = 1,
                    nbt = null,
                    lore = listOf(
                        "<gray>Difficulty: <yellow>Medium",
                        "<gray>Minimum Level: <yellow>20",
                        "Refreshing: {refreshing_time}",
                        "Click to view rewards!",
                        "",
                        "{ongoing_hunt}"
                    )
                )
            ), SlotConfig(
                slot = 4, difficulty = HuntDifficulties.HARD, itemStack = ConfigHandler.SerializedItemStack(
                    displayName = "<red>Hard Hunt",
                    material = "minecraft:red_wool",
                    amount = 1,
                    nbt = null,
                    lore = listOf(
                        "<gray>Difficulty: <red>Hard",
                        "<gray>Minimum Level: <red>50",
                        "Refreshing: {refreshing_time}",
                        "Click to view rewards!",
                        "",
                        "{ongoing_hunt}"
                    )
                )
            ), SlotConfig(
                slot = 6, difficulty = HuntDifficulties.LEGENDARY, itemStack = ConfigHandler.SerializedItemStack(
                    displayName = "<dark_red>Legendary Hunt",
                    material = "minecraft:black_wool",
                    amount = 1,
                    nbt = null,
                    lore = listOf(
                        "<gray>Difficulty: <dark_red>Extreme",
                        "<gray>Minimum Level: <dark_red>100",
                        "Refreshing: {refreshing_time}",
                        "Click to view rewards!",
                        "",
                        "{ongoing_hunt}"
                    )
                )
            ), SlotConfig(
                slot = 8, difficulty = HuntDifficulties.GODLIKE, itemStack = ConfigHandler.SerializedItemStack(
                    displayName = "<light_purple>Godlike Hunt",
                    material = "minecraft:purple_wool",
                    amount = 1,
                    nbt = null,
                    lore = listOf(
                        "<gray>Difficulty: <light_purple>Godlike",
                        "<gray>Minimum Level: <light_purple>200",
                        "Refreshing: {refreshing_time}",
                        "Click to view rewards!",
                        "",
                        "{ongoing_hunt}"
                    )
                )
            )
        ), val emptySlotItemStack: ConfigHandler.SerializedItemStack = ConfigHandler.SerializedItemStack(
            displayName = "<gray>",
            material = "minecraft:gray_stained_glass_pane",
            amount = 1,
            nbt = null,
            lore = listOf(
                " "
            )
        ), val emptySlots: List<Int> = listOf(1, 3, 5, 7)
    )

    data class SlotConfig(
        val slot: Int,
        val difficulty: HuntDifficulties,
        val itemStack: ConfigHandler.SerializedItemStack,
    )
}