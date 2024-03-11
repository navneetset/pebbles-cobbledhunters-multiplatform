package tech.sethi.pebbles.cobbledhunters.data

import dev.architectury.event.events.common.PlayerEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tech.sethi.pebbles.cobbledhunters.config.hunt.global.GlobalHuntConfigLoader
import tech.sethi.pebbles.cobbledhunters.config.hunt.global.GlobalHuntPoolConfigLoader
import tech.sethi.pebbles.cobbledhunters.config.hunt.personal.PersonalHuntConfigLoader
import tech.sethi.pebbles.cobbledhunters.config.reward.PlayerRewardStorageConfigLoader
import tech.sethi.pebbles.cobbledhunters.config.reward.RewardConfigLoader
import tech.sethi.pebbles.cobbledhunters.hunt.type.*

class JSONHandler : DatabaseHandlerInterface {

    var rewardLoader = RewardConfigLoader

    var globalHuntsLoader = GlobalHuntConfigLoader

    var globalHuntsPoolLoader = GlobalHuntPoolConfigLoader
    var globalHuntsSessions = mutableMapOf<String, GlobalHuntSession>()

    var personalHuntsLoader = PersonalHuntConfigLoader
    var personalHuntsSessions = mutableMapOf<String, PersonalHuntSession>()

    val rewardStorageLoader = PlayerRewardStorageConfigLoader

    init {
        CoroutineScope(Dispatchers.IO).launch {
            if (getGlobalHunts().count() == 0) spiderHuntList.forEach { globalHuntsLoader.createHunt(it) }
            if (getGlobalHuntPools().count() == 0) globalHuntsPoolLoader.createHuntPool(arachnidPool)
            if (rewardLoader.rewards.count() == 0) rewardList.forEach { rewardLoader.createReward(it) }
            if (personalHuntsLoader.personalHunts.count() == 0) personalHuntList.forEach {
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
        rewardStorageLoader.reload()
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

    override fun getPlayerRewardStorage(playerUUID: String): RewardStorage? {
        return rewardStorageLoader.rewardStorages.find { it.playerUUID == playerUUID }
    }

    override fun addPlayerReward(playerUUID: String, reward: HuntReward) {
        val rewardStorage = rewardStorageLoader.rewardStorages.find { it.playerUUID == playerUUID } ?: return
        rewardStorage.rewards.add(reward)
        rewardStorageLoader.save(playerUUID)
    }

    override fun removePlayerReward(playerUUID: String, index: Int) {
        val rewardStorage = rewardStorageLoader.rewardStorages.find { it.playerUUID == playerUUID } ?: return
        rewardStorage.rewards.removeAt(index)
        rewardStorageLoader.save(playerUUID)
    }

    override fun ping() {
        // do nothing
    }

    override fun close() {
        // do nothing
    }
}