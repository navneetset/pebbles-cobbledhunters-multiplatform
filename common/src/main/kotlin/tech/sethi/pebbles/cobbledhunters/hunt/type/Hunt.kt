package tech.sethi.pebbles.cobbledhunters.hunt.type

import java.util.Date
import java.util.UUID

data class Hunt(
    val id: String,
    val name: String,
    val difficulty: HuntDifficulties = HuntDifficulties.EASY,
    val huntFeature: HuntFeature = grassTypeFeature,
    val description: List<String> = listOf(),
    val guaranteedRewardId: List<String> = listOf(),
    val rewardPools: List<Pool> = listOf(),
    val timeLimitMinutes: Int = 120,
    val maxPlayers: Int = 1,
    val cost: Int = 0,
)

data class Pool(
    val rewards: List<PoolReward>,
)

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