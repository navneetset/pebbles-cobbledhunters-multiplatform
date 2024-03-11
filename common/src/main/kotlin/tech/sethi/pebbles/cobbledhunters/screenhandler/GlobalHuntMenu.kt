package tech.sethi.pebbles.cobbledhunters.screenhandler

import com.cobblemon.mod.common.CobblemonSounds
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
import tech.sethi.pebbles.cobbledhunters.util.PM
import tech.sethi.pebbles.cobbledhunters.util.UnvalidatedSound

class GlobalHuntMenu(
    syncId: Int, player: ServerPlayerEntity
) : GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X6, syncId, player.inventory, SimpleInventory(9 * 6), 6) {

    val config = GlobalHuntScreenConfig.config

    val huntSlots = config.slots

    val allSlots = huntSlots.map { it.slot }


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
    }

    fun refreshPage() {
        setupPage()
    }

    fun setupPage() {
//        huntSlots.forEach { huntSlot ->
//            // modify {ongoing_hunt} in lore to show the current hunt and remaining time
//            val lore = huntSlot.itemStack.lore.toMutableList()
//            val ongoingHunt = GlobalHuntHandler.activeGlobalHunts[huntSlot.huntPoolId]
//            // 1h 30m 20s
//            val timeRemaining = ongoingHunt?.endTime?.time?.minus(System.currentTimeMillis()) ?: 0
//            val timeRemainingString = PM.formatTime(timeRemaining)
//            if (ongoingHunt != null) {
//                lore[lore.indexOf("{ongoing_hunt}")] = "<gray>Current Hunt: <light_purple>${ongoingHunt.hunt.name}"
//                lore.add("<gray>Time Remaining: <yellow>$timeRemainingString")
//            } else {
//                lore[lore.indexOf("{ongoing_hunt}")] = "<gray>Current Hunt: <light_purple>None"
//            }
//
//            inventory.setStack(huntSlot.slot, huntSlot.itemStack.toItemStack(newLore = lore))
//        }
    }


    override fun onSlotClick(slotIndex: Int, button: Int, actionType: SlotActionType?, player: PlayerEntity?) {

        if (!allSlots.contains(slotIndex)) return

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

//        GlobalHuntHandler.removeHuntScreen(player.uuidAsString)
        super.onClosed(player)
    }

}

fun globalHuntMenuScreenHandlerFactory(player: PlayerEntity) =
    SimpleNamedScreenHandlerFactory({ syncId, playerInventory, _ ->
        GlobalHuntMenu(syncId, player as ServerPlayerEntity)
    }, PM.returnStyledText(GlobalHuntScreenConfig.config.title))