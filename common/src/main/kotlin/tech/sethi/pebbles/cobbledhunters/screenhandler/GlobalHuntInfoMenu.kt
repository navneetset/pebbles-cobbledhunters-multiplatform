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
import tech.sethi.pebbles.cobbledhunters.config.economy.EconomyConfig
import tech.sethi.pebbles.cobbledhunters.config.reward.RewardConfigLoader
import tech.sethi.pebbles.cobbledhunters.config.screenhandler.GlobalHuntDetailScreenConfig
import tech.sethi.pebbles.cobbledhunters.hunt.global.JSONGlobalHuntHandler
import tech.sethi.pebbles.cobbledhunters.hunt.type.*
import tech.sethi.pebbles.cobbledhunters.util.PM
import tech.sethi.pebbles.cobbledhunters.util.UnvalidatedSound

class GlobalHuntInfoMenu(
    syncId: Int, val player: ServerPlayerEntity, private val tracker: GlobalHuntTracker, private val poolId: String
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


    var timeStack: ItemStack? = null

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

        ScreenRefresher.addGlobalHuntInfoMenu(player.uuidAsString, this)
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
            val lore = participants.map { "${it.name}: ${it.progress}" }.toMutableList()
            participantsSerializedStack.lore = lore.subList(0, lore.size.coerceAtMost(5))
            inventory.setStack(slot, participantsSerializedStack.toItemStack())
        }

        progressSlots.forEach { slot ->
            val progressSerializedStack = config.progressSlotStack.deepCopy()
            val progress = tracker.getProgress()
            progressSerializedStack.displayName =
                progressSerializedStack.displayName?.replace("{progress}", "$progress/${hunt.amount}")
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

        refreshTimeStack()

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
            rankRewardLore.add("EXP: ${rankRewards.experience}")

            inventory.setStack(rankSlot.slot, rankSlot.itemStack.toItemStack(newLore = rankRewardLore))
        }

        emptySlots.forEach { slot ->
            inventory.setStack(slot, config.emptySlotItemStack.toItemStack())
        }
    }

    fun refreshTimeStack() {
        timeRemainingSlots.forEach { slot ->
            val timeLimitSerializedStack = config.timeRemainingSlotStack.deepCopy()
            val remainingTime = tracker.expireTime.minus(System.currentTimeMillis())
            timeLimitSerializedStack.displayName =
                timeLimitSerializedStack.displayName?.replace("{refresh_time}", PM.formatTime(remainingTime))
            timeStack = timeLimitSerializedStack.toItemStack()
            inventory.setStack(slot, timeStack)
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

            in joinSlots -> {
                JSONGlobalHuntHandler.joinHunt(player, poolId)
                player.closeHandledScreen()
            }

//            in cancelSlots -> {
//                if (tracker.active) {
//                    JSONPersonalHuntHandler.cancelHunt(
//                        player.uuidAsString, player.name.string, tracker.hunt.difficulty
//                    )
//                    player.closeHandledScreen()
//                }
//            }

            in backSlots -> {
                ScreenRefresher.removeGlobalHuntInfoMenu(player.uuidAsString)
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
            2.0,
            player as ServerPlayerEntity
        )

        ScreenRefresher.removeGlobalHuntInfoMenu(player.uuidAsString)
        super.onClosed(player)
    }
}

fun globalHuntInfoMenuScreenHandlerFactory(
    player: PlayerEntity, tracker: GlobalHuntTracker, poolId: String
): SimpleNamedScreenHandlerFactory {
    val screenTitle = GlobalHuntDetailScreenConfig.config.title
    return SimpleNamedScreenHandlerFactory({ syncId, playerInventory, _ ->
        GlobalHuntInfoMenu(syncId, player as ServerPlayerEntity, tracker, poolId)
    }, PM.returnStyledText(screenTitle.replace("{hunt_name}", tracker.hunt.name)))
}