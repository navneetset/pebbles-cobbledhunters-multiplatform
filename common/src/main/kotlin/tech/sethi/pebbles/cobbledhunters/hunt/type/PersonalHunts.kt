package tech.sethi.pebbles.cobbledhunters.hunt.type

import tech.sethi.pebbles.cobbledhunters.hunt.personal.PersonalHuntHandler

data class PersonalHunts(
    val playerUUID: String,
    val playerName: String,
    var easyHunt: String?,
    var mediumHunt: String?,
    var hardHunt: String?,
    var legendaryHunt: String?,
    var godlikeHunt: String?
) {
    fun getHuntByDifficulty(difficulty: HuntDifficulties): HuntTracker? {
        return when (difficulty) {
            HuntDifficulties.EASY -> easyHunt?.let { PersonalHuntHandler.handler!!.rolledHunts[it] }
            HuntDifficulties.MEDIUM -> mediumHunt?.let { PersonalHuntHandler.handler!!.rolledHunts[it] }
            HuntDifficulties.HARD -> hardHunt?.let { PersonalHuntHandler.handler!!.rolledHunts[it] }
            HuntDifficulties.LEGENDARY -> legendaryHunt?.let { PersonalHuntHandler.handler!!.rolledHunts[it] }
            HuntDifficulties.GODLIKE -> godlikeHunt?.let { PersonalHuntHandler.handler!!.rolledHunts[it] }
        }
    }

    fun setHuntByDifficulty(difficulty: HuntDifficulties, huntTracker: HuntTracker?) {
        when (difficulty) {
            HuntDifficulties.EASY -> easyHunt = huntTracker?.uuid
            HuntDifficulties.MEDIUM -> mediumHunt = huntTracker?.uuid
            HuntDifficulties.HARD -> hardHunt = huntTracker?.uuid
            HuntDifficulties.LEGENDARY -> legendaryHunt = huntTracker?.uuid
            HuntDifficulties.GODLIKE -> godlikeHunt = huntTracker?.uuid
        }

        huntTracker?.let { PersonalHuntHandler.handler!!.rolledHunts[it.uuid] = it }
    }

    fun getHunts(): List<HuntTracker?> {
        return listOf(
            easyHunt, mediumHunt, hardHunt, legendaryHunt, godlikeHunt
        ).map { it?.let { PersonalHuntHandler.handler!!.rolledHunts[it] } }
    }

    fun getActiveHunt(): HuntTracker? {
        return getHunts().firstOrNull { it?.active ?: false }
    }
}

data class HuntTracker(
    val uuid: String,
    val hunt: Hunt,
    val rolledTime: Long,
    val expireTime: Long,
    var startTime: Long?,
    var endTime: Long?,
    var active: Boolean,
    var success: Boolean?,
    var progress: Int = 0,
    val participants: MutableList<String> = mutableListOf(),
    var rewarded: Boolean = false
)