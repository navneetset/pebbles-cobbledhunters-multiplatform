package tech.sethi.pebbles.cobbledhunters.hunt.type

import java.util.concurrent.ConcurrentHashMap

data class GlobalHuntTracker(
    val hunt: GlobalHunt,
    val rolledTime: Long,
    val expireTime: Long,
    var success: Boolean? = null,
    val participants: ConcurrentHashMap<String, Participant> = ConcurrentHashMap(),
    var rewarded: Boolean = false
) {
    fun addParticipant(participant: Participant) {
        participants[participant.uuid] = participant
    }

    fun removeParticipant(participant: Participant) {
        participants.remove(participant.uuid)
    }

    fun getParticipant(uuid: String): Participant? = participants[uuid]

    fun isParticipant(uuid: String): Boolean = participants.containsKey(uuid)

    fun getParticipants(): List<Participant> = participants.values.toList()

    fun getRequiredProgress(): Int = hunt.amount

    fun getProgress(): Int = participants.values.sumOf { it.progress }

    fun addProgress(uuid: String, amount: Int) {
        participants[uuid]?.progress = (participants[uuid]?.progress ?: 0) + amount
        participants[uuid]?.updateTime = System.currentTimeMillis()
    }

    fun isCompleted(): Boolean {
        return getProgress() >= getRequiredProgress()
    }

    fun expired(): Boolean = System.currentTimeMillis() > expireTime

    fun getRanking(): List<Participant> =
        participants.values.sortedWith(compareByDescending<Participant> { it.progress }.thenBy { it.updateTime })

    fun getRankingReward(): RankingRewards? {
        val ranking = getRanking()
        for (reward in hunt.extraRankingRewards) {
            if (ranking.size >= reward.rank) {
                return reward
            }
        }
        return null
    }

    fun getRankingRewardAt(rank: Int): RankingRewards? = hunt.extraRankingRewards.find { it.rank == rank }

    fun getRankingRewardCount(): Int = hunt.extraRankingRewards.size
}

data class Participant(
    val uuid: String, val name: String, var progress: Int = 0, var updateTime: Long = 0
)