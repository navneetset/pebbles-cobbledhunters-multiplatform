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
import tech.sethi.pebbles.cobbledhunters.CobbledHunters
import tech.sethi.pebbles.cobbledhunters.config.baseconfig.BaseConfig
import tech.sethi.pebbles.cobbledhunters.config.baseconfig.LangConfig
import tech.sethi.pebbles.cobbledhunters.config.economy.EconomyConfig
import tech.sethi.pebbles.cobbledhunters.config.reward.RewardConfigLoader
import tech.sethi.pebbles.cobbledhunters.config.screenhandler.PersonalHuntDetailScreenConfig
import tech.sethi.pebbles.cobbledhunters.hunt.JSONPersonalHuntHandler
import tech.sethi.pebbles.cobbledhunters.hunt.type.*
import tech.sethi.pebbles.cobbledhunters.util.PM
import tech.sethi.pebbles.cobbledhunters.util.UnvalidatedSound

class PersonalHuntInfoMenu(
    syncId: Int, player: ServerPlayerEntity, private val huntTracker: HuntTracker
) : GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X6, syncId, player.inventory, SimpleInventory(9 * 6), 6) {

    val config = PersonalHuntDetailScreenConfig.config

    val rewardSlots = config.rewardSlots
    val startSlots = config.startSlots
    val cancelSlots = config.cancelSlots
    val backSlots = config.backSlots
    val huntInfoSlots = config.huntInfoSlots
    val expSlots = config.expSlots
    val timeLimitSlots = config.timeLimitSlots
    val costSlots = config.costSlots
    val emptySlots = config.emptySlots

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
        val hunt = huntTracker.hunt

        val guaranteedRewardIds = hunt.guaranteedRewardId
        val guaranteedRewards =
            guaranteedRewardIds.map { RewardConfigLoader.rewards.first { reward -> reward.id == it } }

        val rewardPools = hunt.rewardPools
        val rolledRewards = rewardPools.map { it.reward }
        val rolledRewardIds = rolledRewards.map { it.rewardId }
        val rolledRewardsList = rolledRewardIds.map { RewardConfigLoader.rewards.first { reward -> reward.id == it } }


        val huntInfo = hunt.description

        val allRewards = guaranteedRewards + rolledRewardsList


        rewardSlots.forEach { slot ->
            if (allRewards.size > slot) {
                val reward = allRewards[slot]
                val rewardSerializedStack = reward.displayItem.deepCopy()
                if (BaseConfig.config.enablePartyHunts && reward.splitable == true) {
                    rewardSerializedStack.displayName = rewardSerializedStack.displayName + " " + config.splittableText
                }

                inventory.setStack(slot, rewardSerializedStack.toItemStack())
            }
        }

        if (huntTracker.active.not()) {
            startSlots.forEach { slot ->
                inventory.setStack(slot, config.startSlotStack.toItemStack())
            }
        } else {
            startSlots.forEach { slot ->
                inventory.setStack(slot, config.startSlotStack.toItemStack(newLore = listOf(config.huntStartedText)))
            }
            cancelSlots.forEach { slot ->
                inventory.setStack(slot, config.cancelSlotStack.toItemStack())
            }
        }

        backSlots.forEach { slot ->
            inventory.setStack(slot, config.backSlotStack.toItemStack())
        }

        huntInfoSlots.forEach { slot ->
            val huntInfoStack = config.huntInfoSlotStack.toItemStack(newLore = huntInfo)
            hunt.name.let { huntInfoStack.setCustomName(PM.returnStyledText(it)) }
            inventory.setStack(slot, huntInfoStack)
        }

        expSlots.forEach { slot ->
            val serializedExpStack = config.expSlotStack.deepCopy()
            serializedExpStack.lore =
                serializedExpStack.lore.map { it.replace("{exp}", hunt.experience.toString()) }.toMutableList()
            serializedExpStack.displayName =
                serializedExpStack.displayName?.replace("{exp}", hunt.experience.toString())
            val expStack = serializedExpStack.toItemStack()
            inventory.setStack(slot, expStack)
        }

        timeLimitSlots.forEach { slot ->
            val timeLimitSerializedStack = config.timeLimitSlotStack.deepCopy()
            timeLimitSerializedStack.displayName =
                timeLimitSerializedStack.displayName?.replace("{time_limit}", hunt.timeLimitMinutes.toString())
            inventory.setStack(slot, timeLimitSerializedStack.toItemStack())
        }

        costSlots.forEach { slot ->
            val costSerializedStack = config.costSlotStack.deepCopy()
            costSerializedStack.displayName =
                costSerializedStack.displayName?.replace("{cost}", hunt.cost.toString())?.replace(
                    "{currency_symbol}", EconomyConfig.economyConfig.currencySymbol
                )?.replace("{currency}", EconomyConfig.economyConfig.currencyName)
            inventory.setStack(slot, costSerializedStack.toItemStack())
        }

        emptySlots.forEach { slot ->
            inventory.setStack(slot, config.emptySlotItemStack.toItemStack())
        }
    }


    override fun onSlotClick(slotIndex: Int, button: Int, actionType: SlotActionType?, player: PlayerEntity?) {

        val allSlots = rewardSlots + startSlots + cancelSlots + backSlots + huntInfoSlots + emptySlots
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

        when (slotIndex) {
            in rewardSlots -> {
                return
            }

            in startSlots -> {
                if (huntTracker.active.not()) {
                    // check if hunt is expired
                    val activated = JSONPersonalHuntHandler.activateHunt(
                        player.uuidAsString, player.name.string, huntTracker.hunt.difficulty
                    )
                    if (activated) {
                        PM.sendText(player, LangConfig.langConfig.huntActivated)
                    } else PM.sendText(player, LangConfig.langConfig.huntActivationFailed)
                    player.closeHandledScreen()
                }
            }

            in cancelSlots -> {
                if (huntTracker.active) {
                    JSONPersonalHuntHandler.cancelHunt(
                        player.uuidAsString, player.name.string, huntTracker.hunt.difficulty
                    )
                    player.closeHandledScreen()
                }
            }

            in backSlots -> {
                player.openHandledScreen(personalHuntMenuScreenHandlerFactory(player))
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
}

fun personalHuntInfoMenuScreenHandlerFactory(
    player: PlayerEntity, huntTracker: HuntTracker
): SimpleNamedScreenHandlerFactory {
    val screenTitle = PersonalHuntDetailScreenConfig.config.title
    return SimpleNamedScreenHandlerFactory({ syncId, playerInventory, _ ->
        PersonalHuntInfoMenu(syncId, player as ServerPlayerEntity, huntTracker)
    }, PM.returnStyledText(screenTitle.replace("{hunt_name}", huntTracker.hunt.name)))
}