package tech.sethi.pebbles.cobbledhunters.hunt.global

import tech.sethi.pebbles.cobbledhunters.config.datastore.DatastoreConfig

object GlobalHuntHandler {
    var handler: AbstractGlobalHuntHandler? = null

    init {
        when (DatastoreConfig.config.datastore) {
            DatastoreConfig.DatastoreType.JSON -> handler = JSONGlobalHuntHandler
            DatastoreConfig.DatastoreType.MONGODB -> handler = MongoGlobalHuntHandler
        }
    }
}