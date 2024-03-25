package tech.sethi.pebbles.cobbledhunters.screenhandler

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.screen.GenericContainerScreenHandler
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.screen.SimpleNamedScreenHandlerFactory
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.util.Identifier
import tech.sethi.pebbles.cobbledhunters.config.screenhandler.PersonalStatsScreenConfig
import tech.sethi.pebbles.cobbledhunters.data.DatabaseHandler
import tech.sethi.pebbles.cobbledhunters.util.PM
import tech.sethi.pebbles.cobbledhunters.util.UnvalidatedSound

class PersonalStatsMenu(
    syncId: Int, val player: ServerPlayerEntity
) : GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X3, syncId, player.inventory, SimpleInventory(9 * 3), 3) {

    val config = PersonalStatsScreenConfig.config

    val emptySlots = config.emptySlots
    val playerHeadSlot = config.playerHeadSlot
    val levelSlots = config.levelSlots
    val backSlots = config.backSlots

    init {
        setupPage()

        UnvalidatedSound.playToPlayer(
            Identifier("cobblemon", "pc.on"),
            SoundCategory.MASTER,
            1.0f,
            1.0f,
            player.blockPos,
            player.world,
            2.0,
            player
        )
    }

    fun setupPage() {
        emptySlots.forEach { slot ->
            inventory.setStack(slot, config.emptySlotStack.toItemStack())
        }

        val playerHead = ItemStack(Items.PLAYER_HEAD)
        playerHead.setCustomName(PM.returnStyledText("<yellow>${player.name.string}</yellow>"))
        playerHead.orCreateNbt.putString("SkullOwner", player.name.string)
        inventory.setStack(playerHeadSlot, playerHead)

        levelSlots.forEach { slot ->
            val serializedLevelStack = config.levelSlotStack.deepCopy()
            serializedLevelStack.lore = serializedLevelStack.lore.map {
                it.replace("{level}", "<yellow>${DatabaseHandler.db!!.playerLevel(player.uuidAsString)}</yellow>")
                    .replace(
                        "{exp}",
                        "<yellow>${DatabaseHandler.db!!.getPlayerExpProgress(player.uuidAsString)?.exp.toString()}</yellow>"
                    )
            }.toMutableList()
            inventory.setStack(slot, serializedLevelStack.toItemStack())
        }

        backSlots.forEach { slot ->
            inventory.setStack(slot, config.backSlotStack.toItemStack())
        }
    }


    override fun onSlotClick(slotIndex: Int, button: Int, actionType: SlotActionType?, player: PlayerEntity?) {

        if (emptySlots.contains(slotIndex)) return

        UnvalidatedSound.playToPlayer(
            Identifier("cobblemon", "pc.click"),
            SoundCategory.MASTER,
            1.0f,
            1.0f,
            player!!.blockPos,
            player.world,
            2.0,
            player as ServerPlayerEntity
        )

        when (slotIndex) {
            in backSlots -> {
                player.openHandledScreen(selectionMenuScreenHandlerFactory(player))
            }
        }

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
            2.0,
            player as ServerPlayerEntity
        )
        super.onClosed(player)
    }

}

fun personalStatsMenuScreenHandlerFactory(player: PlayerEntity) =
    SimpleNamedScreenHandlerFactory({ syncId, playerInventory, _ ->
        PersonalStatsMenu(syncId, player as ServerPlayerEntity)
    }, PM.returnStyledText(PersonalStatsScreenConfig.config.title))