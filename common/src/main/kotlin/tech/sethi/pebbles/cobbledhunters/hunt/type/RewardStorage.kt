package tech.sethi.pebbles.cobbledhunters.hunt.type

data class RewardStorage(
    val playerUUID: String,
    val playerName: String,
    val rewards: MutableList<RewardEntry> = mutableListOf(),
    var exp: Int = 0
)

data class RewardEntry(
    val uuid: String,
    val reward: HuntReward
)