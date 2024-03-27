package tech.sethi.pebbles.cobbledhunters.data

import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPubSub
import redis.clients.jedis.exceptions.JedisConnectionException
import tech.sethi.pebbles.cobbledhunters.CobbledHunters
import tech.sethi.pebbles.cobbledhunters.hunt.personal.MongoPersonalHuntHandler
import tech.sethi.pebbles.cobbledhunters.hunt.type.HuntDifficulties
import tech.sethi.pebbles.partyapi.util.ConfigHandler

object RedisHandler {
    var jedisPool: JedisPool? = null
    var jedisSubscriber: Jedis? = null

    val gson = Gson()

    init {
        try {
            jedisPool = JedisPool(ConfigHandler.config.mongoDbConfig.redisUrl)
            jedisSubscriber = jedisPool?.resource

            CoroutineScope(Dispatchers.IO).launch {
                while (true) {
                    try {
                        subscribe()
                    } catch (e: JedisConnectionException) {
                        CobbledHunters.LOGGER.error("Pebble's Cobbled Hunters Redis connection lost")
                        throw e
                    }
                    delay(1000)
                }
            }
        } catch (e: Exception) {
            throw RuntimeException("Failed to connect to Redis", e)
        }
    }

    val jedisPubSub = object : JedisPubSub() {
        override fun onMessage(channel: String, message: String) {
            if (channel == "cobbled-hunters") {
                val redisMessage = gson.fromJson(message, RedisMessage::class.java)
                when (redisMessage.type) {
                    RedisMessageType.PH_STARTED -> {
//                        val phHuntStarted = gson.fromJson(redisMessage.json, PHHuntStarted::class.java)

                    }

                    RedisMessageType.PH_EXPIRED -> {
                        val phHuntExpired = gson.fromJson(redisMessage.json, HuntCancellation::class.java)
                        MongoPersonalHuntHandler.cancelHunt(
                            phHuntExpired.playerUUID, phHuntExpired.playerName, phHuntExpired.difficulty
                        )
                    }

                    RedisMessageType.PH_COMPLETED -> {
//                        val phHuntCompleted = gson.fromJson(redisMessage.json, PHHuntCompleted::class.java)
                    }
                }
            }
        }
    }

    fun subscribe() {
        try {
            jedisSubscriber?.subscribe(jedisPubSub, "cobbled-hunters")
        } catch (e: JedisConnectionException) {
            CobbledHunters.LOGGER.error("Redis connection lost")
            throw e
        }
    }

    fun publish(message: RedisMessage) {
        jedisPool?.resource?.publish("cobbled-hunters", gson.toJson(message))
    }

    fun close() {
        jedisSubscriber?.close()
        jedisPool?.close()
    }

    enum class RedisMessageType {
        PH_STARTED, PH_EXPIRED, PH_COMPLETED
    }

    data class RedisMessage(
        val type: RedisMessageType, val json: String
    ) {
        companion object {
            fun phStarted(hunt: String): RedisMessage {
                return RedisMessage(RedisMessageType.PH_STARTED, hunt)
            }

            fun phExpired(cancelMessage: HuntCancellation): RedisMessage {
                return RedisMessage(RedisMessageType.PH_EXPIRED, gson.toJson(cancelMessage))
            }
        }
    }

    data class HuntCancellation(
        val playerUUID: String, val playerName: String, val difficulty: HuntDifficulties
    )
}