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
        val huntActivationFailed: String = "<gold>[CobbledHunters] <red>Hunt activation failed. You may have an active hunt already",
        val splitRewardLore: String = "<gold>Split Reward with Party ({party_size}x split)",
        val rewardAdded: String = "<gold>[CobbledHunters] <green>Reward added to your reward storage",
        val huntCancelled: String = "<gold>[CobbledHunters] <red>Hunt has been cancelled",
    )
}