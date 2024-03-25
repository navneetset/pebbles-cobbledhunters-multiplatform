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
        val emptySlots: List<Int> = listOf(1, 3, 5, 7),
        val emptySlotStack: ConfigHandler.SerializedItemStack = ConfigHandler.SerializedItemStack(
            "", "minecraft:gray_stained_glass_pane", 1, null, mutableListOf(
            )
        ),
        val globalHuntsSlots: List<Int> = listOf(0),
        val globalHuntsStack: ConfigHandler.SerializedItemStack = ConfigHandler.SerializedItemStack(
            "<light_purple>Global Hunts", "minecraft:map", 1, null, mutableListOf(
                "<yellow>Compete in global hunts to earn rewards!",
                "<yellow>Click on a hunt to view its details.",
                "",
                "<gray>Be the first to complete the hunt to earn rewards!",
                "<red>[Coming Soon]"
            )
        ),
        val personalHuntsSlots: List<Int> = listOf(2),
        val personalHuntsStack: ConfigHandler.SerializedItemStack = ConfigHandler.SerializedItemStack(
            "<yellow>Personal Hunts", "minecraft:book", 1, null, mutableListOf(
                "<yellow>Complete personal hunts to earn rewards!",
                "<yellow>Click on a hunt to view its details.",
                "",
                "<gray>Personal hunts are specific to you or",
                "<gray>form a hunt party to complete them!",
                "",
                "<gray>Complete the hunt to earn rewards!",
                "<gray>Rewards are evenly distributed to",
                "<gray>you and your party members.",
                "<aqua>/party create",
                "<aqua>/party join <party>",
                "<aqua>/party leave",
                "<aqua>/party disband",
                "<aqua>/party invite <player>",
                "<aqua>/party chat <message>"
            )
        ),
        val leaderboardSlot: List<Int> = listOf(4),
        val leaderboardStack: ConfigHandler.SerializedItemStack = ConfigHandler.SerializedItemStack(
            "<gold>Leaderboard", "minecraft:paper", 1, null, mutableListOf("<yellow>Coming Soon!")
        ),
        val personalStatsSlot: List<Int> = listOf(6),
        val personalStatsStack: ConfigHandler.SerializedItemStack = ConfigHandler.SerializedItemStack(
            "<gold>Personal Stats", "minecraft:oak_hanging_sign", 1, null, mutableListOf()
        ),
        val rewardInventorySlots: List<Int> = listOf(8),
        val rewardInventory: ConfigHandler.SerializedItemStack = ConfigHandler.SerializedItemStack(
            "<gold>Reward Inventory", "minecraft:player_head", 1, "{SkullOwner:{player_name}}", mutableListOf()
        )
    )
}