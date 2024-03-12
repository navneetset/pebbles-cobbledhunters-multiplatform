package tech.sethi.pebbles.cobbledhunters.hunt.type

data class PersonalHunts(
    val playerUUID: String,
    val playerName: String,
    var easyHunt: HuntTracker?,
    var mediumHunt: HuntTracker?,
    var hardHunt: HuntTracker?,
    var legendaryHunt: HuntTracker?,
    var godlikeHunt: HuntTracker?
) {
    fun getHuntByDifficulty(difficulty: HuntDifficulties): HuntTracker? {
        return when (difficulty) {
            HuntDifficulties.EASY -> easyHunt
            HuntDifficulties.MEDIUM -> mediumHunt
            HuntDifficulties.HARD -> hardHunt
            HuntDifficulties.LEGENDARY -> legendaryHunt
            HuntDifficulties.GODLIKE -> godlikeHunt
        }
    }

    fun setHuntByDifficulty(difficulty: HuntDifficulties, huntTracker: HuntTracker?) {
        when (difficulty) {
            HuntDifficulties.EASY -> easyHunt = huntTracker
            HuntDifficulties.MEDIUM -> mediumHunt = huntTracker
            HuntDifficulties.HARD -> hardHunt = huntTracker
            HuntDifficulties.LEGENDARY -> legendaryHunt = huntTracker
            HuntDifficulties.GODLIKE -> godlikeHunt = huntTracker
        }
    }

    fun getHunts(): List<HuntTracker?> {
        return listOf(easyHunt, mediumHunt, hardHunt, legendaryHunt, godlikeHunt)
    }
}

data class HuntTracker(
    val hunt: Hunt,
    val rolledTime: Long,
    var startTime: Long?,
    var endTime: Long?,
    var active: Boolean,
    var success: Boolean?,
    var progress: Int = 0
)