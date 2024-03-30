package tech.sethi.pebbles.cobbledhunters.config.datastore

import tech.sethi.pebbles.cobbledhunters.config.ConfigHandler
import tech.sethi.pebbles.cobbledhunters.util.ConfigFileHandler
import java.io.File

object DatastoreConfig {
    val gson = ConfigHandler.gson
    val datastoreConfigFile = File("config/pebbles-cobbledhunters/datastore/datastore.json")
    val mongoDbConfigFile = File("config/pebbles-cobbledhunters/datastore/mongodb.json")
    val jsonConfigFile = File("config/pebbles-cobbledhunters/datastore/json.json")
    val webSocketConfigFile = File("config/pebbles-cobbledhunters/datastore/websocket.json")

    private val datastoreConfigHandler = ConfigFileHandler(DatastoreConfig::class.java, datastoreConfigFile, gson)
    private val mongoDbConfigHandler = ConfigFileHandler(MongoDBConfig::class.java, mongoDbConfigFile, gson)
    private val jsonConfigHandler = ConfigFileHandler(JsonConfig::class.java, jsonConfigFile, gson)
    private val webSocketConfigHandler = ConfigFileHandler(WebSocketConfig::class.java, webSocketConfigFile, gson)

    var config = DatastoreConfig()
    var mongoDbConfig = MongoDBConfig()
    var jsonConfig = JsonConfig()
    var webSocketConfig = WebSocketConfig()

    init {
        reload()
        DatastoreReadMe
    }

    fun reload() {
        datastoreConfigHandler.reload()
        config = datastoreConfigHandler.config
        mongoDbConfigHandler.reload()
        mongoDbConfig = mongoDbConfigHandler.config
        jsonConfigHandler.reload()
        jsonConfig = jsonConfigHandler.config
        webSocketConfigHandler.reload()
        webSocketConfig = webSocketConfigHandler.config
    }


    enum class DatastoreType {
        JSON, MONGODB
    }

    data class DatastoreConfig(
        val datastore: DatastoreType = DatastoreType.JSON, val triggerAndRewardServer: Boolean = true
    )

    data class JsonConfig(
        val enableBackup: Boolean = true, val backupIntervalMinutes: Int = 60, val maxBackups: Int = 24
    )

    data class MongoDBConfig(
        val connectionString: String = "mongodb://localhost:27017",
        val database: String = "pebbles_cobbledhunters",
        val globalHuntCollection: String = "GlobalHunts",
        val globalHuntPoolCollection: String = "GlobalHuntPools",
        val globalHuntSessionCollection: String = "GlobalHuntSessions",
        val personalHuntCollection: String = "PersonalHunts",
        val personalHuntPoolCollection: String = "PersonalHuntPools",
        val personalHuntSessionCollection: String = "PersonalHuntSessions",
        val rolledHuntTrackerCollection: String = "RolledHuntTrackers",
        val playerHuntCollection: String = "PlayerHunts",
        val rewardCollection: String = "Rewards",
        val rewardPoolCollection: String = "RewardPools",
        val playerRewardStorageCollection: String = "PlayerRewardStorage",
        val playerExpProgressCollection: String = "PlayerExpProgress"
    )

    data class WebSocketConfig(
        val secret: String = "RANDOMSECRETHERE",
        val webSocket: String = "ws://localhost:9999/cobbled-hunters",
    )
}