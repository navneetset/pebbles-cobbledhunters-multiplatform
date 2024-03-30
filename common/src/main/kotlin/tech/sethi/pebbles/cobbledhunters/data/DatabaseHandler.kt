package tech.sethi.pebbles.cobbledhunters.data

import tech.sethi.pebbles.cobbledhunters.CobbledHunters
import tech.sethi.pebbles.cobbledhunters.config.datastore.DatastoreConfig

object DatabaseHandler {
    var db: DatabaseHandlerInterface? = null

    init {
        reload()
    }

    fun reload() {
        db = when (DatastoreConfig.config.datastore) {
            DatastoreConfig.DatastoreType.MONGODB -> MongoDBHandler()
            DatastoreConfig.DatastoreType.JSON -> JSONHandler()
        }

        CobbledHunters.LOGGER.info("Pebble's Cobbled Hunters: Using ${DatastoreConfig.config.datastore} as the datastore")
    }
}