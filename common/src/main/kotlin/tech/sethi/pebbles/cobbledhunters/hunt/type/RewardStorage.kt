package tech.sethi.pebbles.cobbledhunters.hunt.type

data class RewardStorage(
    val playerUUID: String,
    val playerName: String,
    val rewards: MutableList<HuntReward> = mutableListOf()
)
