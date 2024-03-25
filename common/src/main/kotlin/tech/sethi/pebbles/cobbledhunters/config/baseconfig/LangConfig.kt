package tech.sethi.pebbles.cobbledhunters.config.baseconfig

import tech.sethi.pebbles.cobbledhunters.config.ConfigHandler
import tech.sethi.pebbles.cobbledhunters.util.ConfigFileHandler
import java.io.File

object LangConfig {
    val gson = ConfigHandler.gson
    val langConfigFile = File("config/pebbles-cobbledhunters/lang.json")

    private val langConfigHandler = ConfigFileHandler(Lang::class.java, langConfigFile, gson)

    var langConfig = Lang()

    init {
        reload()
    }

    fun reload() {
        langConfigHandler.reload()
        langConfig = langConfigHandler.config
    }

    data class Lang(
        val partyLeaveCancelHunt: String = "<gold>[CobbledHunters] <red>Hunt has been cancelled due to lost of party status",
        val huntCompleted: String = "<gold>[CobbledHunters] <green>Hunt has been completed. Well done! Reward has been sent to your hunt reward storage. <aqua>/hunt</aqua> to redeem",
        val expGained: String = "<gold>[CobbledHunters] <green>You have gained <gold>{exp} <green>exp",
        val partyJoinActiveHunt: String = "<gold>[CobbledHunters] <green>You have joined a party with an active hunt. You will not be able to start a hunt until the current hunt is completed. If you had an active hunt, it has been cancelled",
        val huntActivated: String = "<gold>[CobbledHunters] <green>Hunt started. Good luck!",
        val huntActivationFailed: String = "<gold>[CobbledHunters] <red>Hunt activation failed.",
        val huntLevelRequirement: String = "<gold>[CobbledHunters] <red>You do not have the required level to start this hunt",
        val splitRewardLore: String = "<gold>Reward shared with party <aqua>[{party_size}x split]<aqua>",
        val rewardAdded: String = "<gold>[CobbledHunters] <green>Reward added to your reward storage",
        val huntCancelled: String = "<gold>[CobbledHunters] <red>Hunt has been cancelled",
        val huntAlreadyActive: String = "<gold>[CobbledHunters] <red>You already have an active hunt. Either finish or cancel it",
        val huntExpired: String = "<gold>[CobbledHunters] <red>Hunt has already expired. Please pick another one.",
        val notEnoughBalance: String = "<gold>[CobbledHunters] <red>You do not have enough <yellow>{currency}</yellow> to start this hunt",
        val levelUp: String = "<gold>[CobbledHunters] <green>You have leveled up your hunting mastery to level <gold>{level}",
        val huntTimeEnded: String = "<gold>[CobbledHunters] <red>Oh no! You have run out of time to complete the hunt. The hunt has been cancelled",
        val huntProgressIncrease: String = "<gold>[CobbledHunters] <green>Hunt progress: <gold>{progress}",
        val globalHuntExpired: String = "<gold>[CobbledHunters] <red>Global hunt has failed due to time limit. Better luck next time!",
        val globalPoolRefreshAnnouncement: String = "<gold>[CobbledHunters] <green>Global hunt pool {pool} has been refreshed. <aqua>/hunt</aqua> to start a new hunt",
    )
}