package tech.sethi.pebbles.cobbledhunters.screenhandler

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.GenericContainerScreenHandler
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.screen.SimpleNamedScreenHandlerFactory
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.util.Identifier
import tech.sethi.pebbles.cobbledhunters.config.baseconfig.LangConfig
import tech.sethi.pebbles.cobbledhunters.config.screenhandler.RewardScreenConfig
import tech.sethi.pebbles.cobbledhunters.config.screenhandler.SelectionScreenConfig
import tech.sethi.pebbles.cobbledhunters.data.DatabaseHandler
import tech.sethi.pebbles.cobbledhunters.util.PM
import tech.sethi.pebbles.cobbledhunters.util.UnvalidatedSound
import java.util.*

class RewardStorageMenu(
    syncId: Int, val player: ServerPlayerEntity
) : GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X3, syncId, player.inventory, SimpleInventory(9 * 3), 3) {

    val config = RewardScreenConfig.config

    val emptySlots = config.emptySlots
    val rewardSlots = config.rewardSlots
    val backSlots = config.backSlots
    val navPrevSlots = config.navPrevSlots
    val navNextSlots = config.navNextSlots
    val expSlots = config.expSlots

    val allSlots = rewardSlots + backSlots + navPrevSlots + navNextSlots
    val rewardStorage = DatabaseHandler.db!!.getPlayerRewardStorage(player.uuidAsString)

    val rewardsCache = rewardStorage!!.rewards

    val totalPages = (rewardsCache.size / 18) + 1
    var currentPage = 1

    val rewardsToRemove = mutableListOf<String>()

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

        rewardSlots.forEachIndexed { index, slot ->
            // generate reward item based on page
            val rewardIndex = (currentPage - 1) * 18 + index
            if (rewardsCache.size > rewardIndex) {
                val reward = rewardsCache.elementAt(rewardIndex).reward
                val rewardKey = rewardsCache.elementAt(rewardIndex).uuid
                val stack = reward.displayItem.toItemStack()
                stack.orCreateNbt.putUuid("reward_id", UUID.fromString(rewardKey))
                inventory.setStack(slot, stack)
            } else {
                inventory.setStack(slot, ItemStack.EMPTY)
            }
        }

        backSlots.forEach { slot ->
            inventory.setStack(slot, config.backSlotStack.toItemStack())
        }

        navPrevSlots.forEach { slot ->
            inventory.setStack(slot, config.navPrevSlotStack.toItemStack())
        }

        navNextSlots.forEach { slot ->
            val stack = config.navNextSlotStack.toItemStack()
            inventory.setStack(slot, stack)
        }

        expSlots.forEach { slot ->
            val serializedStack = config.expSlotStack.deepCopy()
            serializedStack.lore =
                serializedStack.lore.map { it.replace("{exp}", rewardStorage?.exp.toString()) }.toMutableList()
            val stack = serializedStack.toItemStack()

            inventory.setStack(slot, stack)
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
            in rewardSlots -> {
                val rewardIndex = (currentPage - 1) * 18 + slotIndex
                if (rewardsCache.size > rewardIndex) {
                    val clickedStack = inventory.getStack(slotIndex)
                    if (clickedStack.isEmpty) return
                    val rewardId = clickedStack.orCreateNbt.getUuid("reward_id")
                    val reward = rewardsCache[rewardIndex].reward
                    rewardsCache.removeAt(rewardIndex)
                    rewardsToRemove.add(rewardId.toString())
                    reward.executeCommands(player)
                    setupPage()
                }
            }

            in backSlots -> player.openHandledScreen(selectionMenuScreenHandlerFactory(player))
            in navPrevSlots -> {
                if (currentPage > 1) {
                    currentPage--
                    setupPage()
                }
            }

            in navNextSlots -> {
                if (currentPage < totalPages) {
                    currentPage++
                    setupPage()
                }
            }

            in expSlots -> {
                if (rewardStorage!!.exp > 0) {
                    val currentLevel = DatabaseHandler.db!!.playerLevel(player.uuidAsString)
                    DatabaseHandler.db!!.addPlayerExp(player.uuidAsString, rewardStorage.exp)
                    val newLevel = DatabaseHandler.db!!.playerLevel(player.uuidAsString)
                    if (newLevel > currentLevel) {
                        PM.sendText(player, LangConfig.config.levelUp.replace("{level}", newLevel.toString()))
                    }
                    rewardStorage.exp = 0
                    DatabaseHandler.db!!.removePlayerExp(player.uuidAsString)
                    setupPage()
                }
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

        if (rewardsToRemove.isEmpty().not()) {
            DatabaseHandler.db!!.removePlayerRewards(player.uuidAsString, rewardsToRemove)
        }

        super.onClosed(player)
    }

}

fun rewardStorageMenuScreenHandlerFactory(player: PlayerEntity) =
    SimpleNamedScreenHandlerFactory({ syncId, playerInventory, _ ->
        RewardStorageMenu(syncId, player as ServerPlayerEntity)
    }, PM.returnStyledText(SelectionScreenConfig.config.title))