package tech.sethi.pebbles.cobbledhunters.config.screenhandler

import tech.sethi.pebbles.cobbledhunters.config.ConfigHandler
import tech.sethi.pebbles.cobbledhunters.util.ConfigFileHandler
import java.io.File

object SelectionScreenConfig {

    val gson = ConfigHandler.gson
    val selectionScreenConfigFile = File("config/pebbles-cobbledhunters/screens/selection-screen.json")

    var config = SelectionScreenConfig()
    val selectionScreenConfigHandler =
        ConfigFileHandler(SelectionScreenConfig::class.java, selectionScreenConfigFile, gson)

    init {
        reload()
    }

    fun reload() {
        selectionScreenConfigHandler.reload()
        config = selectionScreenConfigHandler.config
    }

    data class SelectionScreenConfig(
        val title: String = "<blue>Pebble's Cobbled Hunters",
        val emptySlots: List<Int> = listOf(0, 2, 4, 6, 8),
        val emptySlotStack: ConfigHandler.SerializedItemStack = ConfigHandler.SerializedItemStack(
            "", "minecraft:gray_stained_glass_pane", 1, null, listOf(
            )
        ),
        val globalHuntsSlots: List<Int> = listOf(1),
        val globalHuntsStack: ConfigHandler.SerializedItemStack = ConfigHandler.SerializedItemStack(
            "<light_purple>Global Hunts", "minecraft:map", 1, null, listOf(
                "<yellow>Compete in global hunts to earn rewards!",
                "<yellow>Click on a hunt to view its details.",
                "",
                "<gray>Be the first to complete the hunt to earn rewards!",
            )
        ),
        val personalHuntsSlots: List<Int> = listOf(3),
        val personalHuntsStack: ConfigHandler.SerializedItemStack = ConfigHandler.SerializedItemStack(
            "<yellow>Personal Hunts", "minecraft:book", 1, null, listOf(
                "<yellow>Complete personal hunts to earn rewards!",
                "<yellow>Click on a hunt to view its details.",
                "",
                "<gray>Personal hunts are specific to you or",
                "<gray>form a hunt party to complete them!",
                "",
                "<gray>Complete the hunt to earn rewards!",
                "<gray>Rewards are evenly distributed to",
                "<gray>you and your party members.",
                "<aqua>/hunt party create",
                "<aqua>/hunt party join <player>",
                "<aqua>/hunt party leave",
                "<aqua>/hunt party disband",
                "<aqua>/hunt party invite <player>",
            )
        ),
        val statsSlots: List<Int> = listOf(5),
        val personalStatsStack: ConfigHandler.SerializedItemStack = ConfigHandler.SerializedItemStack(
            "<gold>Leaderboard", "minecraft:paper", 1, null, listOf()
        ),
        val rewardInventorySlots: List<Int> = listOf(7),
        val rewardInventory: ConfigHandler.SerializedItemStack = ConfigHandler.SerializedItemStack(
            "<gold>Reward Inventory", "minecraft:player_head", 1, "{SkullOwner:{player_name}}", listOf()
        )
    )
}