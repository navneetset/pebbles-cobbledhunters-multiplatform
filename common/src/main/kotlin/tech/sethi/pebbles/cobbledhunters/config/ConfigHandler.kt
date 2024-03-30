package tech.sethi.pebbles.cobbledhunters.config

import com.google.gson.GsonBuilder
import net.minecraft.item.ItemStack
import tech.sethi.pebbles.cobbledhunters.config.baseconfig.BaseConfig
import tech.sethi.pebbles.cobbledhunters.config.baseconfig.LangConfig
import tech.sethi.pebbles.cobbledhunters.config.datastore.DatastoreConfig
import tech.sethi.pebbles.cobbledhunters.config.economy.EconomyConfig
import tech.sethi.pebbles.cobbledhunters.config.screenhandler.*
import tech.sethi.pebbles.cobbledhunters.data.DatabaseHandler
import tech.sethi.pebbles.cobbledhunters.util.PM

object ConfigHandler {

    val gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
    val configDirectory = "config/pebbles-cobbledhunters/"

    init {
        EconomyConfig
        BaseConfig
        LangConfig

        DatastoreConfig
        DatabaseHandler

        SelectionScreenConfig
        GlobalHuntScreenConfig
        PersonalHuntScreenConfig
        PersonalHuntDetailScreenConfig
        RewardScreenConfig


        ScreensReadMe
    }

    fun reload() {
        BaseConfig.reload()
        LangConfig.reload()
        EconomyConfig.reload()

        DatastoreConfig.reload()
        DatabaseHandler.reload()

        SelectionScreenConfig.reload()
        GlobalHuntScreenConfig.reload()
        PersonalHuntScreenConfig.reload()
        PersonalHuntDetailScreenConfig.reload()
        RewardScreenConfig.reload()
    }

    data class SerializedItemStack(
        var displayName: String?,
        val material: String,
        val amount: Int,
        val nbt: String?,
        var lore: MutableList<String> = mutableListOf()
    ) {
        fun toItemStack(newLore: List<String>? = null, playerName: String? = null): ItemStack {
            val item = PM.getItem(material)
            val lore = newLore ?: lore
            // replace {player_name} with the player's name in nbt
            val nbt = nbt?.replace("{player_name}", playerName ?: "")
            val itemStack = PM.createItemStack(item = item, count = amount, lore = lore, nbtString = nbt)
            if (displayName != null) {
                itemStack.setCustomName(PM.returnStyledText(displayName!!))
            }
            return itemStack
        }

            fun deepCopy(): SerializedItemStack {
                return SerializedItemStack(
                    displayName, material, amount, nbt, lore.toMutableList()
                )
            }
    }

}