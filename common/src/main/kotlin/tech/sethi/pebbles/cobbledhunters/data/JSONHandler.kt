package tech.sethi.pebbles.cobbledhunters.data

import dev.architectury.event.events.common.PlayerEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tech.sethi.pebbles.cobbledhunters.config.hunt.global.GlobalHuntConfigLoader
import tech.sethi.pebbles.cobbledhunters.config.hunt.global.GlobalHuntPoolConfigLoader
import tech.sethi.pebbles.cobbledhunters.config.hunt.personal.PersonalHuntConfigLoader
import tech.sethi.pebbles.cobbledhunters.config.reward.RewardStorageConfigLoader
import tech.sethi.pebbles.cobbledhunters.config.reward.RewardConfigLoader
import tech.sethi.pebbles.cobbledhunters.hunt.type.*

class JSONHandler : DatabaseHandlerInterface {

    var rewardLoader = RewardConfigLoader

    var globalHuntsLoader = GlobalHuntConfigLoader

    var globalHuntsPoolLoader = GlobalHuntPoolConfigLoader
    var globalHuntsSessions = mutableMapOf<String, GlobalHuntSession>()

    var personalHuntsLoader = PersonalHuntConfigLoader
    var personalHuntsSessions = mutableMapOf<String, PersonalHuntSession>()

    val rewardStorageLoader = RewardStorageConfigLoader

    init {
        CoroutineScope(Dispatchers.IO).launch {
            if (getGlobalHunts().isEmpty()) spiderHuntList.forEach { globalHuntsLoader.createHunt(it) }
            if (getGlobalHuntPools().isEmpty()) globalHuntsPoolLoader.createHuntPool(arachnidPool)
            if (rewardLoader.rewards.isEmpty()) rewardList.forEach { rewardLoader.createReward(it) }
            if (personalHuntsLoader.personalHunts.isEmpty()) personalHuntList.forEach {
                personalHuntsLoader.createHunt(
                    it
                )
            }
        }

        PlayerEvent.PLAYER_JOIN.register { player ->
            CoroutineScope(Dispatchers.IO).launch {
                initPlayerRewardStorage(player.uuid.toString(), player.name.string)
            }
        }
    }

    override fun reload() {
        rewardLoader.reload()
        globalHuntsLoader.reload()
        globalHuntsPoolLoader.reload()
    }

    override fun getRewards(): List<HuntReward> {
        return rewardLoader.rewards
    }

    override fun getReward(id: String): HuntReward? {
        return rewardLoader.rewards.find { it.id == id }
    }

    override fun getGlobalHunts(): List<Hunt> {
        return globalHuntsLoader.globalHunts
    }

    override fun getGlobalHuntPools(): List<HuntPool> {
        return globalHuntsPoolLoader.globalHuntsPool
    }

    override fun getGlobalHuntSessions(): Map<String, GlobalHuntSession> {
        return globalHuntsSessions
    }

    override fun addGlobalHuntSession(huntSession: GlobalHuntSession): Boolean {
        globalHuntsSessions[huntSession.huntPoolId] = huntSession
        return true
    }

    override fun updateGlobalHuntSession(huntSession: GlobalHuntSession) {
        globalHuntsSessions[huntSession.huntPoolId] = huntSession
    }

    override fun getPersonalHunts(): List<Hunt> {
        return personalHuntsLoader.personalHunts
    }

    override fun getPersonalHuntSessions(): Map<String, PersonalHuntSession> {
        return personalHuntsSessions
    }

    override fun initPlayerRewardStorage(playerUUID: String, playerName: String) {
        rewardStorageLoader.createRewardStorage(playerUUID, playerName)
    }

    override fun getPlayerRewardStorage(playerUUID: String): RewardStorage {
        return rewardStorageLoader.getRewardStorage(playerUUID)
    }

    override fun addPlayerReward(playerUUID: String, reward: HuntReward) {
        val rewardStorage = rewardStorageLoader.getRewardStorage(playerUUID)
        rewardStorage.rewards.add(reward)
        rewardStorageLoader.save(rewardStorage)
    }

    override fun removePlayerReward(playerUUID: String, index: Int) {
        val rewardStorage = rewardStorageLoader.getRewardStorage(playerUUID)
        rewardStorage.rewards.removeAt(index)
        rewardStorageLoader.save(rewardStorage)
    }

    override fun ping() {
        // do nothing
    }

    override fun close() {
        // do nothing
    }
}