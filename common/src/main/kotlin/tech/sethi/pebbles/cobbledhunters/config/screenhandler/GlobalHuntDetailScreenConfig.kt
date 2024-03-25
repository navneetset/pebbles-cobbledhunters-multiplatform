package tech.sethi.pebbles.cobbledhunters.config.screenhandler

import tech.sethi.pebbles.cobbledhunters.config.ConfigHandler
import tech.sethi.pebbles.cobbledhunters.util.ConfigFileHandler
import java.io.File

object GlobalHuntDetailScreenConfig {

    val gson = ConfigHandler.gson
    val globalHuntScreenConfigFile = File("config/pebbles-cobbledhunters/screens/globalhunt-detail-screen.json")

    var config = GlobalHuntDetailConfig()
    val globalHuntDetailFileHandler =
        ConfigFileHandler(GlobalHuntDetailConfig::class.java, globalHuntScreenConfigFile, gson)

    init {
        reload()
    }

    fun reload() {
        globalHuntDetailFileHandler.reload()
        config = globalHuntDetailFileHandler.config
    }

    data class GlobalHuntDetailConfig(
        val title: String = "{hunt_name}",
        val alreadyJoinedText: String = "<red>Already Participating</red>",
        val rewardSlots: List<Int> = (0..26).toList(),
        val joinSlots: List<Int> = listOf(53),
        val joinSlotStack: ConfigHandler.SerializedItemStack = ConfigHandler.SerializedItemStack(
            displayName = "<green>Start Hunt",
            material = "minecraft:lime_wool",
            amount = 1,
            nbt = null,
            lore = mutableListOf(
                "Click to start the hunt!"
            )
        ),
        val participantsSlots: List<Int> = listOf(51),
        val participantsSlotStack: ConfigHandler.SerializedItemStack = ConfigHandler.SerializedItemStack(
            displayName = "<gray>Participants: <light_purple>{participants}",
            material = "minecraft:player_head",
            amount = 1,
            nbt = null
        ),
        val progressSlots: List<Int> = listOf(52),
        val progressSlotStack: ConfigHandler.SerializedItemStack = ConfigHandler.SerializedItemStack(
            displayName = "<gray>Progress: <light_purple>{progress}",
            material = "minecraft:oak_hanging_sign",
            amount = 1,
            nbt = null
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
        val expSlots: List<Int> = listOf(46),
        val expSlotStack: ConfigHandler.SerializedItemStack = ConfigHandler.SerializedItemStack(
            displayName = "<gray>EXP: <light_purple>{exp}",
            material = "minecraft:experience_bottle",
            amount = 1,
            nbt = null,
            lore = mutableListOf()
        ),
        val timeRemainingSlots: List<Int> = listOf(47),
        val timeRemainingSlotStack: ConfigHandler.SerializedItemStack = ConfigHandler.SerializedItemStack(
            displayName = "<gray>Refreshing in: <yellow>{refresh_time}</yellow>",
            material = "minecraft:clock",
            amount = 1,
            nbt = null
        ),
        val costSlots: List<Int> = listOf(48),
        val costSlotStack: ConfigHandler.SerializedItemStack = ConfigHandler.SerializedItemStack(
            displayName = "<gray>Cost: <yellow>{cost}</yellow> {currency_symbol}",
            material = "minecraft:emerald",
            amount = 1,
            nbt = null
        ),
        val rankingRewardSlots: List<rankSlot> = listOf(
            rankSlot(
                rank = 1, slot = 30, itemStack = ConfigHandler.SerializedItemStack(
                    displayName = "<gold>1st Place Extra Rewards",
                    material = "cobblemon:master_ball",
                    amount = 1,
                    nbt = null
                )
            ), rankSlot(
                rank = 2, slot = 31, itemStack = ConfigHandler.SerializedItemStack(
                    displayName = "<red>2nd Place Extra Rewards",
                    material = "cobblemon:ultra_ball",
                    amount = 1,
                    nbt = null
                )
            ), rankSlot(
                rank = 3, slot = 32, itemStack = ConfigHandler.SerializedItemStack(
                    displayName = "<green>3rd Place Extra Rewards",
                    material = "cobblemon:great_ball",
                    amount = 1,
                    nbt = null
                )
            )
        ),
        val emptySlots: List<Int> = listOf(),
        val emptySlotItemStack: ConfigHandler.SerializedItemStack = ConfigHandler.SerializedItemStack(
            displayName = "<gray>",
            material = "minecraft:gray_stained_glass_pane",
            amount = 1,
            nbt = null,
            lore = mutableListOf()
        )
    )

    data class rankSlot(
        val rank: Int,
        val slot: Int,
        val itemStack: ConfigHandler.SerializedItemStack,
    )
}