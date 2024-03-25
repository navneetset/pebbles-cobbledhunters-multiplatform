package tech.sethi.pebbles.cobbledhunters.data

import dev.architectury.event.events.common.PlayerEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tech.sethi.pebbles.cobbledhunters.config.baseconfig.BaseConfig
import tech.sethi.pebbles.cobbledhunters.config.exp.ExpConfigLoader
import tech.sethi.pebbles.cobbledhunters.config.hunt.global.GlobalHuntConfigLoader
import tech.sethi.pebbles.cobbledhunters.config.hunt.global.GlobalHuntPoolConfigLoader
import tech.sethi.pebbles.cobbledhunters.config.hunt.personal.PersonalHuntConfigLoader
import tech.sethi.pebbles.cobbledhunters.config.reward.RewardConfigLoader
import tech.sethi.pebbles.cobbledhunters.config.reward.RewardStorageConfigLoader
import tech.sethi.pebbles.cobbledhunters.hunt.type.*
import java.util.*

class JSONHandler : DatabaseHandlerInterface {

    var rewardLoader = RewardConfigLoader

    var globalHuntsLoader = GlobalHuntConfigLoader

    var globalHuntsPoolLoader = GlobalHuntPoolConfigLoader
    var globalHuntsSessions = mutableMapOf<String, GlobalHuntSession>()

    var personalHuntsLoader = PersonalHuntConfigLoader
    var personalHuntsSessions = mutableMapOf<String, PersonalHuntSession>()

    val rewardStorageLoader = RewardStorageConfigLoader
    val expProgressLoader = ExpConfigLoader

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
                initPlayerExpProgress(player.uuid.toString(), player.name.string)
            }
        }
    }

    override fun reload() {
        rewardLoader.reload()
        globalHuntsLoader.reload()
        globalHuntsPoolLoader.reload()
        personalHuntsLoader.reload()
    }

    override fun getRewards(): List<HuntReward> {
        return rewardLoader.rewards
    }

    override fun getReward(id: String): HuntReward? {
        return rewardLoader.rewards.find { it.id == id }
    }

    override fun getGlobalHunts(): List<GlobalHunt> {
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

    override fun addPlayerRewards(playerUUID: String, rewards: List<HuntReward>, exp: Int) {
        val rewardStorage = rewardStorageLoader.getRewardStorage(playerUUID)
        rewardStorage.exp += exp
        rewards.forEach { rewardStorage.rewards[UUID.randomUUID().toString()] = it }
        rewardStorageLoader.save(rewardStorage)
    }

    override fun removePlayerRewards(playerUUID: String, uuids: List<String>) {
        val rewardStorage = rewardStorageLoader.getRewardStorage(playerUUID)
        uuids.forEach { rewardStorage.rewards.remove(it) }
        rewardStorageLoader.save(rewardStorage)
    }

    override fun removePlayerExp(playerUUID: String) {
        val rewardStorage = rewardStorageLoader.getRewardStorage(playerUUID)
        rewardStorage.exp = 0
        rewardStorageLoader.save(rewardStorage)
    }

    override fun initPlayerExpProgress(playerUUID: String, playerName: String) {
        expProgressLoader.createExpProgress(playerUUID, playerName)
    }

    override fun getPlayerExpProgress(playerUUID: String): ExpConfigLoader.ExpProgress {
        return expProgressLoader.getExp(playerUUID)
    }

    override fun addPlayerExp(playerUUID: String, exp: Int) {
        val expProgress = expProgressLoader.getExp(playerUUID)
        expProgress.exp += exp
        expProgressLoader.save(expProgress)
    }

    override fun playerLevel(playerUUID: String): Int {
        val expProgress = expProgressLoader.getExp(playerUUID)
        return expProgress.exp / BaseConfig.config.expPerLevel
    }

    override fun ping() {
        // do nothing
    }

    override fun close() {
        // do nothing
    }
}