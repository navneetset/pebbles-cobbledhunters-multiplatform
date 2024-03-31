package tech.sethi.pebbles.cobbledhunters.data

import com.google.gson.Gson
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import tech.sethi.pebbles.cobbledhunters.CobbledHunters
import tech.sethi.pebbles.cobbledhunters.config.datastore.DatastoreConfig
import tech.sethi.pebbles.cobbledhunters.hunt.global.GlobalHuntHandler
import tech.sethi.pebbles.cobbledhunters.hunt.global.MongoGlobalHuntHandler
import tech.sethi.pebbles.cobbledhunters.hunt.type.GlobalHuntTracker
import tech.sethi.pebbles.cobbledhunters.hunt.type.Participant
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
            while (retries < 20) {
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
            send(Frame.Text(DatastoreConfig.webSocketConfig.secret))
            while (true) {
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
                        CobbledHunters.LOGGER.info("Received Global Hunt Tracker Update")
                        val trackerUpdate = gson.fromJson(socketMessage.json, GlobalHuntTrackerUpdate::class.java)
                        GlobalHuntHandler.handler!!.globalHuntPools[trackerUpdate.poolId] = trackerUpdate.tracker
                    }

                    SocketMessageType.GLOBA_HUNT_POOLS_REFRESH -> {
                        CobbledHunters.LOGGER.info("Received Global Hunt Pools Refresh")
                        val poolsRefresh = gson.fromJson(socketMessage.json, GlobalHuntPoolsRefresh::class.java)
                        val pools = poolsRefresh.pools
                        val concurrentHashmapPools = mutableMapOf<String, GlobalHuntTracker?>()
                        pools.forEach { (poolId, tracker) ->
                            concurrentHashmapPools[poolId] = tracker
                        }

                        GlobalHuntHandler.handler!!.globalHuntPools =
                            concurrentHashmapPools as ConcurrentHashMap<String, GlobalHuntTracker?>
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
        GLOBAL_HUNT_JOIN_HUNT, GLOBAL_HUNT_POKEMON_ACTION, GLOBAL_HUNT_TRACKER_UPDATE, GLOBA_HUNT_POOLS_REFRESH
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

}