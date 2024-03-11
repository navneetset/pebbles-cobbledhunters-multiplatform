package tech.sethi.pebbles.cobbledhunters.config.screenhandler

import tech.sethi.pebbles.cobbledhunters.config.ConfigHandler
import tech.sethi.pebbles.cobbledhunters.hunt.type.HuntDifficulties
import tech.sethi.pebbles.cobbledhunters.util.ConfigFileHandler
import java.io.File

object PersonalHuntScreenConfig {

    val gson = ConfigHandler.gson
    val selectionScreenConfigFile = File("config/pebbles-cobbledhunters/screens/personalhunt-screen.json")

    var config = SelectionScreenConfig()
    val selectionScreenFileHandler =
        ConfigFileHandler(SelectionScreenConfig::class.java, selectionScreenConfigFile, gson)

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
                slot = 0,
                difficulty = HuntDifficulties.EASY,
                itemStack = ConfigHandler.SerializedItemStack(
                    displayName = "<green>Easy Hunt",
                    material = "minecraft:green_wool",
                    amount = 1,
                    nbt = null,
                    lore = listOf(
                        "<gray>Difficulty: <light_purple>Easy",
                        "<gray>Features: <light_purple>Arachnids Pokémon",
                        "<gray>Time Limit: <light_purple>2 Hours",
                        "<gray>Start Time: <light_purple>Every 2 Hours or on completion",
                        "<aqua>Click to view rewards!",
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
        )
    )

    data class SlotConfig(
        val slot: Int,
        val difficulty: HuntDifficulties,
        val itemStack: ConfigHandler.SerializedItemStack,
    )
}