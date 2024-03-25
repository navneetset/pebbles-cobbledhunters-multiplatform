package tech.sethi.pebbles.cobbledhunters.screenhandler

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.SimpleInventory
import net.minecraft.screen.GenericContainerScreenHandler
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.screen.SimpleNamedScreenHandlerFactory
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.util.Identifier
import tech.sethi.pebbles.cobbledhunters.config.baseconfig.BaseConfig
import tech.sethi.pebbles.cobbledhunters.config.economy.EconomyConfig
import tech.sethi.pebbles.cobbledhunters.config.screenhandler.GlobalHuntScreenConfig
import tech.sethi.pebbles.cobbledhunters.config.screenhandler.PersonalHuntScreenConfig
import tech.sethi.pebbles.cobbledhunters.hunt.personal.JSONPersonalHuntHandler
import tech.sethi.pebbles.cobbledhunters.hunt.type.HuntDifficulties
import tech.sethi.pebbles.cobbledhunters.hunt.type.HuntTracker
import tech.sethi.pebbles.cobbledhunters.hunt.type.PersonalHunts
import tech.sethi.pebbles.cobbledhunters.util.PM
import tech.sethi.pebbles.cobbledhunters.util.UnvalidatedSound

class PersonalHuntMenu(
    syncId: Int, private val player: ServerPlayerEntity
) : GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X1, syncId, player.inventory, SimpleInventory(9 * 1), 1) {

    val config = PersonalHuntScreenConfig.config
    val baseConfig = BaseConfig.config

    val huntSlots = config.slots

    val emptySlots = config.emptySlots

    val backSlots = config.backButtonSlots

    val allSlots = huntSlots.map { it.slot } + backSlots

    init {
        setupPage()

        UnvalidatedSound.playToPlayer(
            Identifier("cobblemon", "pc.on"),
            SoundCategory.MASTER,
            1.0f,
            1.0f,
            player.blockPos,
            player.world,
            8.0,
            player
        )

//        GlobalHuntHandler.addHuntScreen(player.uuidAsString, this)

        ScreenRefresher.addPersonalHuntMenu(player.uuidAsString, this)

    }


    fun setupPage() {
        val difficultyTimeLimit = mapOf(
            HuntDifficulties.EASY to baseConfig.easyRefreshTime * 60 * 1000,
            HuntDifficulties.MEDIUM to baseConfig.mediumRefreshTime * 60 * 1000,
            HuntDifficulties.HARD to baseConfig.hardRefreshTime * 60 * 1000,
            HuntDifficulties.LEGENDARY to baseConfig.legendaryRefreshTime * 60 * 1000,
            HuntDifficulties.GODLIKE to baseConfig.godlikeRefreshTime * 60 * 1000
        )

        huntSlots.forEach { huntSlot ->
            val lore = huntSlot.itemStack.lore.toMutableList()
            val hunt = JSONPersonalHuntHandler.personalHunts[player.uuidAsString]

            val currentHunt = getHuntByDifficulty(hunt, huntSlot.difficulty)
            val huntLore = mutableListOf<String>()
            if (hunt != null) {
                if (currentHunt != null) {
                    currentHunt.hunt.name.let { huntLore.add(it) }

                    huntLore.addAll(getHuntLoreByDifficulty(hunt, huntSlot.difficulty))
                }

            }
            if (currentHunt != null) {
                lore.replaceAll { it.replace("{ongoing_hunt}", huntLore.joinToString("\n")) }

                val rolledTime = hunt?.getHuntByDifficulty(huntSlot.difficulty)?.rolledTime
                val remainingRefreshTime =
                    difficultyTimeLimit[huntSlot.difficulty]!! - (System.currentTimeMillis() - (rolledTime ?: 0))

                val isActive = hunt?.getHuntByDifficulty(huntSlot.difficulty)?.active == true
                if (!isActive) {
                    lore.replaceAll { it.replace("{refreshing_time}", timeToPrettyString(remainingRefreshTime)) }
                } else {
                    lore.replaceAll { it.replace("{refreshing_time}", "<green>Active</green>") }
                }

                lore.replaceAll { it.replace("{cost}", currentHunt.hunt.cost.toString()) }
                lore.replaceAll { it.replace("{currency}", EconomyConfig.config.currencyName) }
                lore.replaceAll { it.replace("{currency_symbol}", EconomyConfig.config.currencySymbol) }

                val remainingTime = currentHunt.endTime?.minus(System.currentTimeMillis())
                if (remainingTime != null && remainingTime > 0) {
                    lore.add(" ")
                    lore.add(timeToPrettyString(remainingTime))
                }

                inventory.setStack(huntSlot.slot, huntSlot.itemStack.toItemStack(newLore = lore))
            }
        }

        emptySlots.forEach { slot ->
            inventory.setStack(slot, config.emptySlotItemStack.toItemStack())
        }

        backSlots.forEach { slot ->
            inventory.setStack(slot, config.backButtonStack.toItemStack())
        }
    }

    fun getHuntByDifficulty(hunt: PersonalHunts?, difficulty: HuntDifficulties): HuntTracker? {
        val playerHunts = JSONPersonalHuntHandler.getPersonalHunts(player.uuidAsString, player.name.string)
        return when (difficulty) {
            HuntDifficulties.EASY -> playerHunts.easyHunt?.let { JSONPersonalHuntHandler.rolledHunts[it] }
            HuntDifficulties.MEDIUM -> playerHunts.mediumHunt?.let { JSONPersonalHuntHandler.rolledHunts[it] }
            HuntDifficulties.HARD -> playerHunts.hardHunt?.let { JSONPersonalHuntHandler.rolledHunts[it] }
            HuntDifficulties.LEGENDARY -> playerHunts.legendaryHunt?.let { JSONPersonalHuntHandler.rolledHunts[it] }
            HuntDifficulties.GODLIKE -> playerHunts.godlikeHunt?.let { JSONPersonalHuntHandler.rolledHunts[it] }
        }
    }

    fun getHuntLoreByDifficulty(hunt: PersonalHunts?, difficulty: HuntDifficulties): MutableList<String> {
        return hunt?.getHuntByDifficulty(difficulty)?.hunt?.description?.toMutableList() ?: mutableListOf()
    }


    fun timeToPrettyString(time: Long): String {
        val seconds = time / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        // return all formatted time if exists
        return when {
            days > 0 -> "${days}d ${hours % 24}h ${minutes % 60}m"
            hours > 0 -> "${hours}h ${minutes % 60}m ${seconds % 60}s"
            minutes > 0 -> "${minutes}m ${seconds % 60}s"
            else -> "${seconds}s"
        }
    }


    override fun onSlotClick(slotIndex: Int, button: Int, actionType: SlotActionType?, player: PlayerEntity?) {

        if (!allSlots.contains(slotIndex)) return

        UnvalidatedSound.playToPlayer(
            Identifier("cobblemon", "pc.click"),
            SoundCategory.MASTER,
            0.5f,
            1.0f,
            player!!.blockPos,
            player.world,
            2.0,
            player as ServerPlayerEntity
        )

        if (backSlots.contains(slotIndex)) {
            player.openHandledScreen(selectionMenuScreenHandlerFactory(player))
            ScreenRefresher.removePersonalHuntMenu(player.uuidAsString)
        }

        // check which difficulty was clicked
        val huntSlot = huntSlots.firstOrNull { it.slot == slotIndex } ?: return
        val hunt = JSONPersonalHuntHandler.personalHunts[player.uuidAsString]

        val currentHunt = getHuntByDifficulty(hunt, huntSlot.difficulty)
        if (currentHunt != null) player.openHandledScreen(personalHuntInfoMenuScreenHandlerFactory(player, currentHunt))

        ScreenRefresher.removePersonalHuntMenu(player.uuidAsString)
        return
    }

    override fun onClosed(player: PlayerEntity?) {
        UnvalidatedSound.playToPlayer(
            Identifier("cobblemon", "pc.off"),
            SoundCategory.MASTER,
            1.0f,
            1.0f,
            player!!.blockPos,
            player.world,
            8.0,
            player as ServerPlayerEntity
        )

        ScreenRefresher.removePersonalHuntMenu(player.uuidAsString)
        super.onClosed(player)
    }

}

fun personalHuntMenuScreenHandlerFactory(player: PlayerEntity) =
    SimpleNamedScreenHandlerFactory({ syncId, playerInventory, _ ->
        PersonalHuntMenu(syncId, player as ServerPlayerEntity)
    }, PM.returnStyledText(PersonalHuntScreenConfig.config.title))