package tech.sethi.pebbles.cobbledhunters.config.datastore

import tech.sethi.pebbles.cobbledhunters.config.ConfigHandler
import tech.sethi.pebbles.cobbledhunters.util.ConfigFileHandler
import java.io.File

object DatastoreConfig {
    val gson = ConfigHandler.gson
    val datastoreConfigFile = File("config/pebbles-cobbledhunters/datastore/datastore.json")
    val mongoDbConfigFile = File("config/pebbles-cobbledhunters/datastore/mongodb.json")
    val mySQLConfigFile = File("config/pebbles-cobbledhunters/datastore/mysql.json")
    val jsonConfigFile = File("config/pebbles-cobbledhunters/datastore/json.json")

    private val datastoreConfigHandler = ConfigFileHandler(DatastoreConfig::class.java, datastoreConfigFile, gson)
    private val mongoDbConfigHandler = ConfigFileHandler(MongoDBConfig::class.java, mongoDbConfigFile, gson)
    private val mySQLConfigHandler = ConfigFileHandler(MySQLConfig::class.java, mySQLConfigFile, gson)
    private val jsonConfigHandler = ConfigFileHandler(JsonConfig::class.java, jsonConfigFile, gson)

    var datastoreConfig = DatastoreConfig()
    var mongoDbConfig = MongoDBConfig()
    var mySQLConfig = MySQLConfig()
    var jsonConfig = JsonConfig()

    init {
        reload()
        DatastoreReadMe
    }

    fun reload() {
        datastoreConfigHandler.reload()
        datastoreConfig = datastoreConfigHandler.config
        mongoDbConfigHandler.reload()
        mongoDbConfig = mongoDbConfigHandler.config
        mySQLConfigHandler.reload()
        mySQLConfig = mySQLConfigHandler.config
        jsonConfigHandler.reload()
        jsonConfig = jsonConfigHandler.config
    }



    enum class DatastoreType {
        JSON, MONGODB, MYSQL
    }

    data class DatastoreConfig(
        val datastore: DatastoreType = DatastoreType.MONGODB,
        val triggerAndRewardServer: Boolean = true
    )

    data class JsonConfig(
        val enableBackup: Boolean = true,
        val backupIntervalMinutes: Int = 60,
        val maxBackups: Int = 24
    )

    data class MongoDBConfig(
        val connectionString: String = "mongodb://localhost:27017",
        val database: String = "pebbles_cobbledhunters",
        val globalHuntCollection: String = "GlobalHunts",
        val globalHuntPoolCollection: String = "GlobalHuntPools",
        val personalHuntCollection: String = "PersonalHunts",
        val personalHuntPoolCollection: String = "PersonalHuntPools",
        val playerHuntCollection: String = "PlayerHunts",
        val rewardCollection: String = "Rewards",
        val rewardPoolCollection: String = "RewardPools",
        val globalHuntSessionCollection: String = "GlobalHuntSessions",
        val personalHuntSessionCollection: String = "PersonalHuntSessions",
        val playerRewardStorageCollection: String = "PlayerRewardStorage"
    )

    data class MySQLConfig(
        val connectionString: String = "jdbc:mysql://root@localhost/pebbles_cobbledhunters",
        val database: String = "pebbles_cobbledhunters",
        val globalHuntTable: String = "GlobalHunts",
        val huntPoolTable: String = "HuntPools",
        val playerHuntTable: String = "PlayerHunts"
    )
}