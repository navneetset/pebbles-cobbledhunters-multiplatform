package tech.sethi.pebbles.cobbledhunters.config.screenhandler

import tech.sethi.pebbles.cobbledhunters.config.ConfigHandler
import tech.sethi.pebbles.cobbledhunters.util.ConfigFileHandler
import java.io.File

object GlobalHuntScreenConfig {

    val gson = ConfigHandler.gson
    val selectionScreenConfigFile = File("config/pebbles-cobbledhunters/screens/globalhunt-screen.json")

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
        val title: String = "<blue>Global Hunts", val slots: List<SlotConfig> = listOf(
            SlotConfig(
                slot = 0, huntPoolId = "arachnid_pool", itemStack = ConfigHandler.SerializedItemStack(
                    displayName = "<light_purple>Arachnids",
                    material = "minecraft:spider_spawn_egg",
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
                "<gray>There is no global hunt in this slot.", "<gray>Check back later!"
            )
        )
    )

    data class SlotConfig(
        val slot: Int,
        val huntPoolId: String,
        val itemStack: ConfigHandler.SerializedItemStack,
    )
}