package tech.sethi.pebbles.cobbledhunters.data

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClients
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import dev.architectury.event.events.common.PlayerEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tech.sethi.pebbles.cobbledhunters.config.datastore.DatastoreConfig
import tech.sethi.pebbles.cobbledhunters.config.exp.ExpConfigLoader
import tech.sethi.pebbles.cobbledhunters.hunt.type.*
import java.util.*

class MongoDBHandler : DatabaseHandlerInterface {

    val config = DatastoreConfig.mongoDbConfig

    val mongoClientSettings =
        MongoClientSettings.builder().applyConnectionString(ConnectionString(config.connectionString)).build()

    val mongoClient = MongoClients.create(mongoClientSettings)
    val database = mongoClient.getDatabase(config.database)

    val globalHuntCollection = database.getCollection(config.globalHuntCollection, Hunt::class.java)
    val globalHuntPoolCollection = database.getCollection(config.globalHuntPoolCollection, HuntPool::class.java)

    val globalHuntSessionCollection =
        database.getCollection(config.globalHuntSessionCollection, GlobalHuntSession::class.java)

    val rewardCollection = database.getCollection(config.rewardCollection, HuntReward::class.java)

    val playerRewardStorageCollection =
        database.getCollection(config.playerRewardStorageCollection, RewardStorage::class.java)

    init {
        CoroutineScope(Dispatchers.IO).launch {
            if (globalHuntCollection.countDocuments() == 0L) {
                spiderHuntList.forEach { globalHuntCollection.insertOne(it) }
            }
            if (globalHuntPoolCollection.countDocuments() == 0L) {
                globalHuntPoolCollection.insertOne(arachnidPool)
            }

            if (rewardCollection.countDocuments() == 0L) {
                rewardList.forEach { rewardCollection.insertOne(it) }
            }
        }

        PlayerEvent.PLAYER_JOIN.register { player ->
            CoroutineScope(Dispatchers.IO).launch {
                initPlayerRewardStorage(player.uuid.toString(), player.name.string)
            }
        }
    }

    override fun reload() {
        // Do nothing
    }

    override fun getRewards(): List<HuntReward> {
        return rewardCollection.find().toList()
    }

    override fun getReward(id: String): HuntReward? {
        return rewardCollection.find(Filters.eq("id", id)).first()
    }

    override fun getGlobalHunts(): List<Hunt> {
        return globalHuntCollection.find().toList()
    }

    override fun getGlobalHuntPools(): List<HuntPool> {
        return globalHuntPoolCollection.find().toList()
    }

    override fun getGlobalHuntSessions(): Map<String, GlobalHuntSession> {
        return globalHuntSessionCollection.find().toList().associateBy { it.huntPoolId }
    }

    override fun addGlobalHuntSession(huntSession: GlobalHuntSession): Boolean =
        globalHuntSessionCollection.insertOne(huntSession).wasAcknowledged()

    override fun updateGlobalHuntSession(huntSession: GlobalHuntSession) {
        globalHuntSessionCollection.updateOne(
            Filters.eq("id", huntSession.id), Updates.combine(
                Updates.set("completed", huntSession.completed), Updates.set("winner", huntSession.winner)
            )
        )
    }

    override fun getPersonalHunts(): List<Hunt> {
        TODO("Not yet implemented")
    }

    override fun getPersonalHuntSessions(): Map<String, PersonalHuntSession> {
        TODO("Not yet implemented")
    }

    override fun initPlayerRewardStorage(playerUUID: String, playerName: String) {
        if (playerRewardStorageCollection.countDocuments(Filters.eq("playerUUID", playerUUID)) == 0L) {
            playerRewardStorageCollection.insertOne(RewardStorage(playerUUID, playerName))
        }
    }

    override fun getPlayerRewardStorage(playerUUID: String): RewardStorage? {
        return playerRewardStorageCollection.find(Filters.eq("playerUUID", playerUUID)).first()
    }

    override fun addPlayerRewards(playerUUID: String, rewards: List<HuntReward>, exp: Int) {
        playerRewardStorageCollection.updateOne(
            Filters.eq("playerUUID", playerUUID),
            Updates.pushEach("rewards", rewards.map { it.copy(id = UUID.randomUUID().toString()) }),
        )
        playerRewardStorageCollection.updateOne(
            Filters.eq("playerUUID", playerUUID), Updates.inc("exp", exp)
        )
    }

    override fun removePlayerRewards(playerUUID: String, uuids: List<String>) {
        playerRewardStorageCollection.updateOne(
            Filters.eq("playerUUID", playerUUID), Updates.pullAll("rewards", uuids)
        )
    }

    override fun removePlayerExp(playerUUID: String) {
        TODO("Not yet implemented")
    }

    override fun initPlayerExpProgress(playerUUID: String, playerName: String) {
        TODO("Not yet implemented")
    }

    override fun getPlayerExpProgress(playerUUID: String): ExpConfigLoader.ExpProgress? {
        TODO("Not yet implemented")
    }

    override fun addPlayerExp(playerUUID: String, exp: Int) {
        TODO("Not yet implemented")
    }

    override fun ping() {
        println("Pebble's Cobbled Hunter pinging MongoDB")
        mongoClient.listDatabaseNames().forEach { println(it) }
    }

    override fun close() {
        mongoClient.close()
    }
}