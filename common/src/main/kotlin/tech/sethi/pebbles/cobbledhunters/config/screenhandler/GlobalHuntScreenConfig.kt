package tech.sethi.pebbles.cobbledhunters.config.screenhandler

import tech.sethi.pebbles.cobbledhunters.config.ConfigHandler
import tech.sethi.pebbles.cobbledhunters.util.ConfigFileHandler
import java.io.File

object GlobalHuntScreenConfig {

    val gson = ConfigHandler.gson
    val selectionScreenConfigFile = File("config/pebbles-cobbledhunters/screens/globalhunt-screen.json")

    var config = GlobalHuntScreenData()
    val selectionScreenFileHandler =
        ConfigFileHandler(GlobalHuntScreenData::class.java, selectionScreenConfigFile, gson)

    init {
        reload()
    }

    fun reload() {
        selectionScreenFileHandler.reload()
        config = selectionScreenFileHandler.config
    }

    data class GlobalHuntScreenData(
        val title: String = "<blue>Global Hunts",
        val huntStatus: HuntStatus = HuntStatus(),
        val slots: List<SlotConfig> = listOf(
            SlotConfig(
                slot = 10, huntPoolId = "arachnid_pool", itemStack = ConfigHandler.SerializedItemStack(
                    displayName = "<light_purple>Arachnids <yellow>Hunt Pool",
                    material = "minecraft:spider_spawn_egg",
                    amount = 1,
                    nbt = null,
                    lore = mutableListOf(
                        "<gray>Features: <light_purple>Arachnids Pokémon",
                        "<gray>Refreshing in: <aqua>{refresh_time}",
                        "<aqua>Click to view hunt details and rewards!",
                        "",
                        "<gray>Ongoing: {hunt_name}",
                        "<gray>Progress: {progress}",
                        "<gray>Participants: {participants}",
                        "<gray>Status: {status}"
                    )
                )
            )
        ),
        val backSlots: List<Int> = listOf(45),
        val backStack: ConfigHandler.SerializedItemStack = ConfigHandler.SerializedItemStack(
            displayName = "<gray>Back", material = "minecraft:gray_wool", amount = 1, nbt = null, lore = mutableListOf()
        ),
        val emptySlotItemStack: ConfigHandler.SerializedItemStack = ConfigHandler.SerializedItemStack(
            displayName = "<gray>",
            material = "minecraft:gray_stained_glass_pane",
            amount = 1,
            nbt = null,
            lore = mutableListOf(),
        ),
        val emptySlots: List<Int> = listOf(
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 44, 46, 47, 48, 49, 50, 51, 52, 53
        )
    )

    data class SlotConfig(
        val slot: Int,
        val huntPoolId: String,
        val itemStack: ConfigHandler.SerializedItemStack,
    )

    data class HuntStatus(
        val ongoing: String = "<green>Ongoing",
        val completed: String = "<red>Completed",
    )
}