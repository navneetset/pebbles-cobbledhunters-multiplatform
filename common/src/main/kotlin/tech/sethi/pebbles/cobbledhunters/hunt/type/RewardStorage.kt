package tech.sethi.pebbles.cobbledhunters.hunt.type

data class RewardStorage(
    val playerUUID: String,
    val playerName: String,
    val rewards: MutableMap<String, HuntReward> = mutableMapOf(),
    var exp: Int = 0
)