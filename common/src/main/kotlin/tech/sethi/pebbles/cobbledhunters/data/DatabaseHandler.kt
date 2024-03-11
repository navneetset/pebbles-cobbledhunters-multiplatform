package tech.sethi.pebbles.cobbledhunters.data

import tech.sethi.pebbles.cobbledhunters.CobbledHunters
import tech.sethi.pebbles.cobbledhunters.config.datastore.DatastoreConfig

object DatabaseHandler {
    var db: DatabaseHandlerInterface? = null

    init {
        reload()
    }

    fun reload() {
        db = when (DatastoreConfig.datastoreConfig.datastore) {
            DatastoreConfig.DatastoreType.MONGODB -> MongoDBHandler()
            DatastoreConfig.DatastoreType.MYSQL -> MongoDBHandler()
            DatastoreConfig.DatastoreType.JSON -> JSONHandler()
        }

        CobbledHunters.LOGGER.info("Pebble's Cobbled Hunters: Using ${DatastoreConfig.datastoreConfig.datastore} as the datastore")
    }
}