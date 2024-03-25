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
import tech.sethi.pebbles.cobbledhunters.config.economy.EconomyConfig
import tech.sethi.pebbles.cobbledhunters.config.reward.RewardConfigLoader
import tech.sethi.pebbles.cobbledhunters.config.screenhandler.GlobalHuntDetailScreenConfig
import tech.sethi.pebbles.cobbledhunters.hunt.type.*
import tech.sethi.pebbles.cobbledhunters.util.PM
import tech.sethi.pebbles.cobbledhunters.util.UnvalidatedSound

class GlobalHuntInfoMenu(
    syncId: Int, val player: ServerPlayerEntity, private val tracker: GlobalHuntTracker
) : GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X6, syncId, player.inventory, SimpleInventory(9 * 6), 6) {

    val config = GlobalHuntDetailScreenConfig.config

    val rewardSlots = config.rewardSlots
    val joinSlots = config.joinSlots
    val participantsSlots = config.participantsSlots
    val progressSlots = config.progressSlots
    val backSlots = config.backSlots
    val huntInfoSlots = config.huntInfoSlots
    val expSlots = config.expSlots
    val timeRemainingSlots = config.timeRemainingSlots
    val costSlots = config.costSlots
    val rankingRewardSlots = config.rankingRewardSlots
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

    fun setupPage() {
        val hunt = tracker.hunt

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

                inventory.setStack(slot, rewardSerializedStack.toItemStack())
            }
        }

        joinSlots.forEach { slot ->
            val joinStack = config.joinSlotStack.toItemStack()
            if (tracker.isParticipant(player.uuidAsString)) joinStack.setCustomName(PM.returnStyledText(config.alreadyJoinedText))
            inventory.setStack(slot, config.joinSlotStack.toItemStack())
        }

        participantsSlots.forEach { slot ->
            val participantsSerializedStack = config.participantsSlotStack.deepCopy()
            val participantSize = tracker.participants.size
            participantsSerializedStack.displayName =
                participantsSerializedStack.displayName?.replace("{participants}", participantSize.toString())
            val participants = tracker.getRanking()
            val lore = participants.map { "${it.playerName}: ${it.progress}" }.toMutableList()
            participantsSerializedStack.lore = lore.subList(0, lore.size.coerceAtMost(5))
            inventory.setStack(slot, participantsSerializedStack.toItemStack())
        }

        progressSlots.forEach { slot ->
            val progressSerializedStack = config.progressSlotStack.deepCopy()
            val progress = tracker.getProgress()
            progressSerializedStack.displayName =
                progressSerializedStack.displayName?.replace("{progress}", progress.toString())
            inventory.setStack(slot, progressSerializedStack.toItemStack())
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

        timeRemainingSlots.forEach { slot ->
            val timeLimitSerializedStack = config.timeRemainingSlotStack.deepCopy()
            val remainingTime = tracker.expireTime.minus(System.currentTimeMillis())
            timeLimitSerializedStack.displayName =
                timeLimitSerializedStack.displayName?.replace("{refresh_time}", PM.formatTime(remainingTime))
            inventory.setStack(slot, timeLimitSerializedStack.toItemStack())
        }

        costSlots.forEach { slot ->
            val costSerializedStack = config.costSlotStack.deepCopy()
            costSerializedStack.displayName =
                costSerializedStack.displayName?.replace("{cost}", hunt.cost.toString())?.replace(
                    "{currency_symbol}", EconomyConfig.config.currencySymbol
                )?.replace("{currency}", EconomyConfig.config.currencyName)
            inventory.setStack(slot, costSerializedStack.toItemStack())
        }

        val totalExtraRankRewards = tracker.getRankingRewardCount()

        for (i in 0 until totalExtraRankRewards) {
            val rankSlot = rankingRewardSlots[i]
            val rewardSerializedStack = rolledRewardsList[i].displayItem.deepCopy()
            val rankRewards = tracker.getRankingRewardAt(rankSlot.rank) ?: continue

            val guaranteedRankRewardIds = rankRewards.guaranteedRewardId
            val guaranteedRankRewards =
                guaranteedRankRewardIds.map { RewardConfigLoader.rewards.first { reward -> reward.id == it } }

            val rankRewardPools = rankRewards.rewardPools
            val rolledRankRewards = rankRewardPools.map { it.reward }
            val rolledRankRewardIds = rolledRankRewards.map { it.rewardId }
            val rolledRankRewardsList =
                rolledRankRewardIds.map { RewardConfigLoader.rewards.first { reward -> reward.id == it } }

            val allRankRewards = guaranteedRankRewards + rolledRankRewardsList
            val allRankRewardsNames = allRankRewards.map { it.name }

            val rankRewardLore = allRankRewardsNames.toMutableList()
            val slotStack = rankSlot.itemStack.toItemStack()
            slotStack.orCreateNbt.putInt("HideFlags", 63)
            PM.setLore(slotStack, rankRewardLore)

            inventory.setStack(rankSlot.slot, slotStack)
        }

        emptySlots.forEach { slot ->
            inventory.setStack(slot, config.emptySlotItemStack.toItemStack())
        }
    }


    override fun onSlotClick(slotIndex: Int, button: Int, actionType: SlotActionType?, player: PlayerEntity?) {

        val allSlots =
            rewardSlots + joinSlots + participantsSlots + progressSlots + backSlots + huntInfoSlots + expSlots + timeRemainingSlots + costSlots + rankingRewardSlots.map { it.slot } + emptySlots
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

//            in joinSlots -> {
//                if (tracker.active.not()) {
//                    // check if hunt is expired
//                    val activated = JSONPersonalHuntHandler.activateHunt(
//                        player.uuidAsString, player.name.string, tracker.hunt.difficulty
//                    )
//                    if (activated) {
//                        PM.sendText(player, LangConfig.langConfig.huntActivated)
//                    } else PM.sendText(player, LangConfig.langConfig.huntActivationFailed)
//                    player.closeHandledScreen()
//                }
//            }
//
//            in cancelSlots -> {
//                if (tracker.active) {
//                    JSONPersonalHuntHandler.cancelHunt(
//                        player.uuidAsString, player.name.string, tracker.hunt.difficulty
//                    )
//                    player.closeHandledScreen()
//                }
//            }
//
            in backSlots -> {
                player.openHandledScreen(globalHuntMenuScreenHandlerFactory(player))
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

fun globalHuntInfoMenuScreenHandlerFactory(
    player: PlayerEntity, tracker: GlobalHuntTracker
): SimpleNamedScreenHandlerFactory {
    val screenTitle = GlobalHuntDetailScreenConfig.config.title
    return SimpleNamedScreenHandlerFactory({ syncId, playerInventory, _ ->
        GlobalHuntInfoMenu(syncId, player as ServerPlayerEntity, tracker)
    }, PM.returnStyledText(screenTitle.replace("{hunt_name}", tracker.hunt.name)))
}