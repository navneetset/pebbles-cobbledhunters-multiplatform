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
import tech.sethi.pebbles.cobbledhunters.config.screenhandler.SelectionScreenConfig
import tech.sethi.pebbles.cobbledhunters.util.PM
import tech.sethi.pebbles.cobbledhunters.util.UnvalidatedSound

class SelectionMenu(
    syncId: Int, val player: ServerPlayerEntity
) : GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X1, syncId, player.inventory, SimpleInventory(9 * 1), 1) {

    val config = SelectionScreenConfig.config

    val emptySlots = config.emptySlots
    val globalHuntsSlots = config.globalHuntsSlots
    val personalHuntsSlots = config.personalHuntsSlots
    val statsSlots = config.statsSlots
    val rewardInventorySlots = config.rewardInventorySlots

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
    }

    fun setupPage() {
        emptySlots.forEach { slot ->
            inventory.setStack(slot, config.emptySlotStack.toItemStack())
        }

        globalHuntsSlots.forEach { slot ->
            inventory.setStack(slot, config.globalHuntsStack.toItemStack())
        }

        personalHuntsSlots.forEach { slot ->
            inventory.setStack(slot, config.personalHuntsStack.toItemStack())
        }

        statsSlots.forEach { slot ->
            inventory.setStack(slot, config.personalStatsStack.toItemStack())
        }

        rewardInventorySlots.forEach { slot ->
            inventory.setStack(slot, config.rewardInventory.toItemStack(playerName = player.name.string))
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
            8.0,
            player as ServerPlayerEntity
        )

        when (slotIndex) {
            in globalHuntsSlots -> {
                player.openHandledScreen(globalHuntMenuScreenHandlerFactory(player))
                return
            }

            in personalHuntsSlots -> {

                return
            }

            in statsSlots -> {

                return
            }

            in rewardInventorySlots -> {

                return
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
            8.0,
            player as ServerPlayerEntity
        )
        super.onClosed(player)
    }

}

fun selectionMenuScreenHandlerFactory(player: PlayerEntity) =
    SimpleNamedScreenHandlerFactory({ syncId, playerInventory, _ ->
        SelectionMenu(syncId, player as ServerPlayerEntity)
    }, PM.returnStyledText(SelectionScreenConfig.config.title))