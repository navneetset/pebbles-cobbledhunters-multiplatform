package tech.sethi.pebbles.cobbledhunters.data

import com.cobblemon.mod.common.util.server
import com.google.gson.Gson
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import net.minecraft.sound.SoundCategory
import net.minecraft.util.Identifier
import tech.sethi.pebbles.cobbledhunters.CobbledHunters
import tech.sethi.pebbles.cobbledhunters.config.datastore.DatastoreConfig
import tech.sethi.pebbles.cobbledhunters.hunt.global.GlobalHuntHandler
import tech.sethi.pebbles.cobbledhunters.hunt.global.MongoGlobalHuntHandler
import tech.sethi.pebbles.cobbledhunters.hunt.type.GlobalHuntTracker
import tech.sethi.pebbles.cobbledhunters.hunt.type.Participant
import tech.sethi.pebbles.cobbledhunters.util.PM
import tech.sethi.pebbles.cobbledhunters.util.UnvalidatedSound
import java.util.concurrent.ConcurrentHashMap

object WebSocketHandler {
    val job = Job()
    val wsScope = CoroutineScope(Dispatchers.IO + job)
    private var webSocketSession: DefaultWebSocketSession? = null

    val client = HttpClient(CIO) {
        install(WebSockets)
    }

    init {
        wsScope.launch {
            var retries = 0 // Initialize retry counter
            while (retries < 20 && server() != null && server()!!.isRunning) {
                try {
                    connectWebSocket() // Function to connect to WebSocket
                    retries = 0 // Reset retry counter upon successful connection
                } catch (e: Exception) {
                    // Log the exception (or handle it appropriately)
                    CobbledHunters.LOGGER.warn("Cobbled Hunters Socket: Exception while connecting: $e")
                    retries++ // Increment retry counter
                    CobbledHunters.LOGGER.info("Retrying in 3 seconds...")
                    Thread.sleep(3000) // Wait for 3 seconds before retrying
                }
            }
        }
    }


    val gson = Gson()

    val db = DatabaseHandler.db!! as MongoDBHandler

    suspend fun connectWebSocket() {
        client.webSocket(DatastoreConfig.webSocketConfig.webSocket) {
            webSocketSession = this // Set the webSocketSession for later use
            var connected = false
            if (server() != null && server()!!.isRunning) {
                send(Frame.Text(DatastoreConfig.webSocketConfig.secret))
            } else {
                close()
            }

            while (server() != null && server()!!.isRunning) {
                if (!connected) {
                    CobbledHunters.LOGGER.info("Cobbled Hunters: Connected to WebSocket!")
                    connected = true
                }
                val message = incoming.receive() as? Frame.Text ?: continue
                val text = message.readText()

                val socketMessage = try {
                    gson.fromJson(text, SocketMessage::class.java)
                } catch (e: Exception) {
                    CobbledHunters.LOGGER.error("Failed to parse message: $text")
                    continue
                }

                when (socketMessage.type) {
                    SocketMessageType.GLOBAL_HUNT_TRACKER_UPDATE -> {
                        val trackerUpdate = gson.fromJson(socketMessage.json, GlobalHuntTrackerUpdate::class.java)
                        GlobalHuntHandler.handler!!.globalHuntPools[trackerUpdate.poolId] = trackerUpdate.tracker
                    }

                    SocketMessageType.GLOBA_HUNT_POOLS_REFRESH -> {
                        val poolsRefresh = gson.fromJson(socketMessage.json, GlobalHuntPoolsRefresh::class.java)
                        val pools = poolsRefresh.pools
                        val concurrentHashmapPools = mutableMapOf<String, GlobalHuntTracker?>()
                        pools.forEach { (poolId, tracker) ->
                            concurrentHashmapPools[poolId] = tracker
                        }

                        GlobalHuntHandler.handler!!.globalHuntPools =
                            concurrentHashmapPools as ConcurrentHashMap<String, GlobalHuntTracker?>
                    }

                    SocketMessageType.PLAY_SOUND -> {
                        val playSound = gson.fromJson(socketMessage.json, PlaySound::class.java)
                        val player = PM.getPlayer(playSound.uuid) ?: continue
                        UnvalidatedSound.playToPlayer(
                            Identifier(playSound.sound.split(":")[0], playSound.sound.split(":")[1]),
                            SoundCategory.MASTER,
                            playSound.volume,
                            playSound.pitch,
                            player.blockPos,
                            player.world,
                            playSound.radius,
                            player
                        )
                    }

                    else -> {
                        // Do nothing
                    }
                }
            }
        }
    }

    fun sendToWebSocket(message: SocketMessage) {
        wsScope.launch {
            webSocketSession?.send(Frame.Text(gson.toJson(message)))
        }
    }

    enum class SocketMessageType {
        GLOBAL_HUNT_JOIN_HUNT, GLOBAL_HUNT_POKEMON_ACTION, GLOBAL_HUNT_TRACKER_UPDATE, GLOBA_HUNT_POOLS_REFRESH, PLAY_SOUND
    }

    data class SocketMessage(
        val type: SocketMessageType, val json: String
    )

    data class GlobalHuntJoinHunt(
        val poolId: String, val playerUUID: String, val playerName: String, val balance: Double
    )

    data class GlobalHuntTrackerUpdate(
        val poolId: String, val tracker: GlobalHuntTracker
    )

    data class PokemonAction(
        val participant: Participant, val feature: MongoGlobalHuntHandler.PokemonFeature
    )

    data class GlobalHuntPoolsRefresh(
        val pools: MutableMap<String, GlobalHuntTracker?>
    )


    data class PlaySound(
        val uuid: String, val sound: String, val volume: Float = 1.0f, val pitch: Float = 1.0f, val radius: Double = 2.0
    )
}