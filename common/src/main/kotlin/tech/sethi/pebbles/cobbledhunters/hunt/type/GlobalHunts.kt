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

    fun getParticipant(uuid: String): Participant? {
        return participants[uuid]
    }

    fun isParticipant(uuid: String): Boolean {
        return participants.containsKey(uuid)
    }

    fun getParticipants(): List<Participant> {
        return participants.values.toList()
    }

    fun getRequiredProgress(): Int {
        return hunt.amount
    }

    fun getProgress(): Int {
        return participants.values.sumOf { it.progress }
    }

    fun isCompleted(): Boolean {
        return getProgress() >= getRequiredProgress()
    }

    fun expired(): Boolean {
        return System.currentTimeMillis() > expireTime
    }

    fun getRanking(): List<Participant> {
        return participants.values.sortedByDescending { it.progress }
    }

    fun getRankingReward(): RankingReward? {
        val ranking = getRanking()
        for (reward in hunt.extraRankingRewards) {
            if (ranking.size >= reward.rank) {
                return reward
            }
        }
        return null
    }
}

data class Participant(
    val uuid: String, val playerName: String, var progress: Int = 0
)