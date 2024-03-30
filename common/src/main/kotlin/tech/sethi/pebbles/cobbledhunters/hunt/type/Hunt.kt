package tech.sethi.pebbles.cobbledhunters.hunt.type

import java.util.Date
import java.util.UUID

data class Hunt(
    val id: String,
    val name: String,
    val difficulty: HuntDifficulties = HuntDifficulties.EASY,
    val amount: Int = 1,
    val huntFeature: HuntFeature = grassTypeFeature,
    val description: List<String> = listOf(),
    val guaranteedRewardId: List<String> = listOf(),
    val rewardPools: List<Pool> = listOf(),
    val timeLimitMinutes: Int = 120,
    val cost: Int = 0,
    val experience: Int = 0
)

data class GlobalHunt(
    val id: String,
    val name: String,
    val amount: Int = 1,
    val huntFeature: HuntFeature = grassTypeFeature,
    val description: List<String> = listOf(),
    val guaranteedRewardId: List<String> = listOf(),
    val rewardPools: List<Pool> = listOf(),
    val extraRankingRewards: List<RankingRewards> = listOf(),
    val timeLimitMinutes: Int = 120,
    val cost: Int = 50,
    val experience: Int = 50
)

data class RankingRewards(
    val rank: Int, val guaranteedRewardId: List<String>, val rewardPools: List<Pool>, val experience: Int
)

data class Pool(
    val rewards: List<PoolReward>,
) {
    var reward = getRolledReward()

    fun getRolledReward(): PoolReward {
        val totalWeight = rewards.sumOf { it.weight }
        val random = (0..totalWeight).random()
        var currentWeight = 0
        for (reward in rewards) {
            currentWeight += reward.weight
            if (random <= currentWeight) {
                return reward
            }
        }
        return rewards[0]
    }
}

data class PoolReward(
    val rewardId: String,
    val weight: Int,
)

data class GlobalHuntSession(
    val id: String = UUID.randomUUID().toString(),
    val huntPoolId: String,
    val hunt: Hunt,
    val startTime: Date,
    val endTime: Date,
    var completed: Boolean,
    var winner: MutableList<Winner> = mutableListOf(),
)

data class Winner(
    val playerUUID: String,
    val playerName: String,
)