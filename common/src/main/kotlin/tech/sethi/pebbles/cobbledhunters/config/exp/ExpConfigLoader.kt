package tech.sethi.pebbles.cobbledhunters.config.exp

import kotlinx.coroutines.*
import tech.sethi.pebbles.cobbledhunters.config.ConfigHandler
import java.io.File
import java.util.concurrent.ConcurrentHashMap

object ExpConfigLoader {

    val gson = ConfigHandler.gson

    val expDirectory = File(ConfigHandler.configDirectory, "exp")

    val expCache = ConcurrentHashMap<String, ExpProgress>()

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
        expCache[exp.playerUUID] = exp
        CoroutineScope(Dispatchers.IO).launch {
            val file = File(expDirectory, "${exp.playerUUID}.json")
            val expString = gson.toJson(exp)
            file.writeText(expString)
        }
    }

    fun getExp(playerUUID: String): ExpProgress {
        if (expCache.containsKey(playerUUID)) {
            return expCache[playerUUID]!!
        }

        val file = File(expDirectory, "$playerUUID.json")
        val expString = file.readText()
        val expProgress = gson.fromJson(expString, ExpProgress::class.java)
        expCache[playerUUID] = expProgress

        return expProgress
    }

    data class ExpProgress(
        val playerUUID: String, val playerName: String, var exp: Int
    )
}