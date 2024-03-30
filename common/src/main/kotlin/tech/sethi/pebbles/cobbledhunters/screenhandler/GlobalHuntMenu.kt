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
import tech.sethi.pebbles.cobbledhunters.config.screenhandler.GlobalHuntScreenConfig
import tech.sethi.pebbles.cobbledhunters.hunt.global.GlobalHuntHandler
import tech.sethi.pebbles.cobbledhunters.util.PM
import tech.sethi.pebbles.cobbledhunters.util.UnvalidatedSound

class GlobalHuntMenu(
    syncId: Int, player: ServerPlayerEntity
) : GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X6, syncId, player.inventory, SimpleInventory(9 * 6), 6) {

    val config = GlobalHuntScreenConfig.config

    val huntSlots = config.slots

    val backSlots = config.backSlots

    init {
        setupPage()

        UnvalidatedSound.playToPlayer(
            Identifier("cobblemon", "pc.on"),
            SoundCategory.MASTER,
            0.5f,
            1.0f,
            player.blockPos,
            player.world,
            2.0,
            player
        )

        ScreenRefresher.addGlobalHuntMenu(player.uuidAsString, this)
    }

    fun setupPage() {
        huntSlots.forEach { huntSlot ->
            // modify {ongoing_hunt} in lore to show the current hunt and remaining time
            val lore = huntSlot.itemStack.lore.toMutableList()
            val tracker = GlobalHuntHandler.handler!!.globalHuntPools[huntSlot.huntPoolId] ?: return
            val timeRemaining = tracker.expireTime.minus(System.currentTimeMillis())
            val timeRemainingString = PM.formatTime(timeRemaining)
            lore.replaceAll { it.replace("{refresh_time}", timeRemainingString) }
            lore.replaceAll { it.replace("{hunt_name}", tracker.hunt.name) }
            lore.replaceAll { it.replace("{progress}", "${tracker.getProgress()}/${tracker.hunt.amount}") }
            lore.replaceAll { it.replace("{participants}", tracker.participants.size.toString()) }
            val status = if (tracker.isCompleted()) config.huntStatus.completed else config.huntStatus.ongoing
            lore.replaceAll { it.replace("{status}", status) }

            inventory.setStack(huntSlot.slot, huntSlot.itemStack.toItemStack(newLore = lore))
        }

        backSlots.forEach { backSlot ->
            inventory.setStack(backSlot, config.backStack.toItemStack())
        }

        config.emptySlots.forEach { emptySlot ->
            inventory.setStack(emptySlot, config.emptySlotItemStack.toItemStack())
        }
    }


    override fun onSlotClick(slotIndex: Int, button: Int, actionType: SlotActionType?, player: PlayerEntity?) {

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

        when {
            huntSlots.any { it.slot == slotIndex } -> {
                val huntSlot = huntSlots.find { it.slot == slotIndex } ?: return
                val tracker = GlobalHuntHandler.handler!!.globalHuntPools[huntSlot.huntPoolId] ?: return
                ScreenRefresher.removeGlobalHuntMenu(player.uuidAsString)
                player.openHandledScreen(globalHuntInfoMenuScreenHandlerFactory(player, tracker, huntSlot.huntPoolId))
            }

            backSlots.contains(slotIndex) -> {
                ScreenRefresher.removeGlobalHuntMenu(player.uuidAsString)
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
            8.0,
            player as ServerPlayerEntity
        )

        ScreenRefresher.removeGlobalHuntMenu(player.uuidAsString)
        super.onClosed(player)
    }
}

fun globalHuntMenuScreenHandlerFactory(player: PlayerEntity) =
    SimpleNamedScreenHandlerFactory({ syncId, playerInventory, _ ->
        GlobalHuntMenu(syncId, player as ServerPlayerEntity)
    }, PM.returnStyledText(GlobalHuntScreenConfig.config.title))