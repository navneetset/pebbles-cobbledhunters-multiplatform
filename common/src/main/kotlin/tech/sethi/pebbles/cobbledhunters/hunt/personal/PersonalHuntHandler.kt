package tech.sethi.pebbles.cobbledhunters.hunt.personal

import tech.sethi.pebbles.cobbledhunters.config.datastore.DatastoreConfig

object PersonalHuntHandler {

    var handler: AbstractPersonalHuntHandler? = null

    init {
        handler = when (DatastoreConfig.config.datastore) {
            DatastoreConfig.DatastoreType.MONGODB -> MongoPersonalHuntHandler
            DatastoreConfig.DatastoreType.JSON -> JSONPersonalHuntHandler
        }
    }
}