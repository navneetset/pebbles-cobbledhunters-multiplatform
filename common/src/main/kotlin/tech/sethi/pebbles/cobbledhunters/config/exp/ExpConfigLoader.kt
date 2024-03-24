package tech.sethi.pebbles.cobbledhunters.config.exp

import kotlinx.coroutines.*
import tech.sethi.pebbles.cobbledhunters.config.ConfigHandler
import java.io.File

object ExpConfigLoader {

    val gson = ConfigHandler.gson

    val expDirectory = File(ConfigHandler.configDirectory, "exp")

    init {
        expDirectory.mkdirs()
    }

    fun createExpProgress(playerUUID: String, playerName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val file = File(expDirectory, "$playerUUID.json")
            if (!file.exists()) {
                val expProgress = ExpProgress(playerUUID, playerName, 0)
                save(expProgress)
            }
        }
    }

    @Synchronized
    fun save(exp: ExpProgress) {
        CoroutineScope(Dispatchers.IO).launch {
            val file = File(expDirectory, "${exp.playerUUID}.json")
            val expString = gson.toJson(exp)
            file.writeText(expString)
        }
    }

    fun getExp(playerUUID: String): ExpProgress {
        val file = File(expDirectory, "$playerUUID.json")
        val expString = file.readText()
        return gson.fromJson(expString, ExpProgress::class.java)
    }

    data class ExpProgress(
        val playerUUID: String, val playerName: String, var exp: Int
    )
}