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

    val rewardCollection = database.getCollection(config.rewardCollection, HuntReward::class.java)

    val globalHuntCollection = database.getCollection(config.globalHuntCollection, GlobalHunt::class.java)
    val globalHuntPoolCollection = database.getCollection(config.globalHuntPoolCollection, HuntPool::class.java)
    val globalHuntSessionCollection =
        database.getCollection(config.globalHuntSessionCollection, GlobalHuntSession::class.java)

    val personalHuntCollection = database.getCollection(config.personalHuntCollection, Hunt::class.java)
    val personalHuntSessionCollection =
        database.getCollection(config.personalHuntSessionCollection, PersonalHunts::class.java)
    val rolledHuntTrackerCollection =
        database.getCollection(config.rolledHuntTrackerCollection, HuntTracker::class.java)

    val playerRewardStorageCollection =
        database.getCollection(config.playerRewardStorageCollection, RewardStorage::class.java)
    val playerExpProgressCollection =
        database.getCollection(config.playerExpProgressCollection, ExpConfigLoader.ExpProgress::class.java)

    var personalHuntsCache: List<Hunt> = personalHuntCollection.find().toList()
    var globalHuntPoolsCache: List<HuntPool> = globalHuntPoolCollection.find().toList()

    var cachedRewards: List<HuntReward> = rewardCollection.find().toList()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            if (globalHuntCollection.countDocuments() == 0L) spiderHuntList.forEach { globalHuntCollection.insertOne(it) }
            if (globalHuntPoolCollection.countDocuments() == 0L) globalHuntPoolCollection.insertOne(arachnidPool)
            if (rewardCollection.countDocuments() == 0L) rewardList.forEach { rewardCollection.insertOne(it) }
            if (personalHuntCollection.countDocuments() == 0L) personalHuntList.forEach {
                personalHuntCollection.insertOne(
                    it
                )
            }
        }

        PlayerEvent.PLAYER_JOIN.register { player ->
            CoroutineScope(Dispatchers.IO).launch {
                initPlayerRewardStorage(player.uuid.toString(), player.name.string)
                initPlayerExpProgress(player.uuid.toString(), player.name.string)
            }
        }
    }

    override fun reload() {
        personalHuntsCache = personalHuntCollection.find().toList()
        cachedRewards = rewardCollection.find().toList()
    }

    override fun getRewards(): List<HuntReward> = cachedRewards

    override fun getReward(id: String): HuntReward? = cachedRewards.find { it.id == id }

    override fun getGlobalHunts(): List<GlobalHunt> = globalHuntCollection.find().toList()

    override fun getGlobalHuntPools(): List<HuntPool> = globalHuntPoolCollection.find().toList()

    override fun getGlobalHuntSessions(): Map<String, GlobalHuntSession> =
        globalHuntSessionCollection.find().toList().associateBy { it.huntPoolId }

    override fun addGlobalHuntSession(huntSession: GlobalHuntSession): Boolean =
        globalHuntSessionCollection.insertOne(huntSession).wasAcknowledged()

    override fun updateGlobalHuntSession(huntSession: GlobalHuntSession) {
        globalHuntSessionCollection.updateOne(
            Filters.eq("id", huntSession.id), Updates.combine(
                Updates.set("completed", huntSession.completed), Updates.set("winner", huntSession.winner)
            )
        )
    }

    override fun getPersonalHunts(): List<Hunt> = personalHuntsCache

    override fun getPersonalHuntSessions(): Map<String, PersonalHunts> =
        personalHuntSessionCollection.find().toList().associateBy { it.playerUUID }

    fun getPlayerPersonalHuntSessions(playerUUID: String): PersonalHunts? =
        personalHuntSessionCollection.find(Filters.eq("playerUUID", playerUUID)).first()

    fun addPlayerPersonalHuntSession(playerUUID: String, personalHunts: PersonalHunts) {
        personalHuntSessionCollection.insertOne(personalHunts)
    }

    fun updatePlayerPersonalHuntSession(playerUUID: String, personalHunts: PersonalHunts) {
        personalHuntSessionCollection.updateOne(
            Filters.eq("playerUUID", playerUUID), Updates.combine(
                Updates.set("playerName", personalHunts.playerName),
                Updates.set("easyHunt", personalHunts.easyHunt),
                Updates.set("mediumHunt", personalHunts.mediumHunt),
                Updates.set("hardHunt", personalHunts.hardHunt),
                Updates.set("legendaryHunt", personalHunts.legendaryHunt),
                Updates.set("godlikeHunt", personalHunts.godlikeHunt)
            )
        )
    }

    fun getRolledHuntTracker(uuid: String): HuntTracker? =
        rolledHuntTrackerCollection.find(Filters.eq("uuid", uuid)).first()

    fun addRolledHuntTracker(huntTracker: HuntTracker): Boolean =
        rolledHuntTrackerCollection.insertOne(huntTracker).wasAcknowledged()

    fun updateRolledHuntTracker(huntTracker: HuntTracker) {
        rolledHuntTrackerCollection.updateOne(
            Filters.eq("uuid", huntTracker.uuid), Updates.combine(
                Updates.set("hunt", huntTracker.hunt),
                Updates.set("rolledTime", huntTracker.rolledTime),
                Updates.set("expireTime", huntTracker.expireTime),
                Updates.set("startTime", huntTracker.startTime),
                Updates.set("endTime", huntTracker.endTime),
                Updates.set("active", huntTracker.active),
                Updates.set("success", huntTracker.success),
                Updates.set("progress", huntTracker.progress),
                Updates.set("participants", huntTracker.participants),
                Updates.set("rewarded", huntTracker.rewarded)
            )
        )
    }

    override fun initPlayerRewardStorage(playerUUID: String, playerName: String) {
        if (playerRewardStorageCollection.countDocuments(Filters.eq("playerUUID", playerUUID)) == 0L) {
            playerRewardStorageCollection.insertOne(RewardStorage(playerUUID, playerName))
        }
    }

    override fun getPlayerRewardStorage(playerUUID: String): RewardStorage? =
        playerRewardStorageCollection.find(Filters.eq("playerUUID", playerUUID)).first()

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
        val pullCondition = Updates.pull("rewards", Filters.`in`("uuid", uuids))
        playerRewardStorageCollection.updateOne(Filters.eq("playerUUID", playerUUID), pullCondition)
    }


    override fun removePlayerExp(playerUUID: String) {
        playerRewardStorageCollection.updateOne(
            Filters.eq("playerUUID", playerUUID), Updates.set("exp", 0)
        )
    }

    override fun initPlayerExpProgress(playerUUID: String, playerName: String) {
        if (playerExpProgressCollection.countDocuments(Filters.eq("playerUUID", playerUUID)) == 0L) {
            playerExpProgressCollection.insertOne(ExpConfigLoader.ExpProgress(playerUUID, playerName, 0))
        }
    }

    override fun getPlayerExpProgress(playerUUID: String): ExpConfigLoader.ExpProgress? =
        playerExpProgressCollection.find(Filters.eq("playerUUID", playerUUID)).first()

    override fun addPlayerExp(playerUUID: String, exp: Int) {
        playerExpProgressCollection.updateOne(
            Filters.eq("playerUUID", playerUUID), Updates.inc("exp", exp)
        )
    }

    fun getRewardById(id: String): HuntReward? {
        return rewardCollection.find(Filters.eq("id", id)).first()
    }

    override fun playerLevel(playerUUID: String): Int {
        val expProgress = playerExpProgressCollection.find(Filters.eq("playerUUID", playerUUID)).first()
        return expProgress?.exp?.div(100) ?: 0
    }

    override fun ping() {
        println("Pebble's Cobbled Hunter pinging MongoDB")
        mongoClient.listDatabaseNames().forEach { println(it) }
    }

    override fun close() {
        mongoClient.close()
    }
}