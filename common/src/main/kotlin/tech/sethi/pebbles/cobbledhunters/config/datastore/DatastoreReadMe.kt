package tech.sethi.pebbles.cobbledhunters.config.datastore

import java.io.File

object DatastoreReadMe {
    private val readmeFile = File("config/pebbles-cobbledhunters/datastore/readme.md")


    private val instructions = """
# Datastore Configuration Guide for Pebble's Cobbled Hunters

This guide will help you configure the datastore for Pebble's CobbledHunters, including options for JSON, MongoDB, and MySQL databases.

## Overview

The datastore configuration is crucial for determining where and how the data for the Cobbled Hunters are stored and managed. You have the option to choose between a simple JSON file, MongoDB, or MySQL depending on your needs and setup.

## Configuration Files

- **datastore.json**: Main configuration file to select the type of datastore.
- **mongodb.json**: Specific configuration for MongoDB connections.
- **mysql.json**: Specific configuration for MySQL connections.

## Configuring Datastore Type

In `datastore.json`, you will specify the type of datastore you wish to use. The available options are:

- `JSON`: Stores data in a simple JSON file. Good for smaller or test environments.
- `MONGODB`: Utilizes a MongoDB database. Preferred for scalable and production environments.
- `MYSQL`: Utilizes a MySQL database. A common choice for relational database management.

## MongoDB Configuration

To use MongoDB as your datastore, configure the following in `mongodb.json`:

- `connectionString`: The connection string for your MongoDB instance.
- `database`: The name of the database to use.
- `globalHuntCollection`: The collection name for global hunts.
- `huntPoolCollection`: The collection name for hunt pools.
- `playerHuntCollection`: The collection name for player progress.

## MySQL Configuration

To use MySQL as your datastore, configure the following in `mysql.json`:

- `connectionString`: The JDBC connection string for your MySQL database.
- `database`: The name of the database to use.
- `globalHuntTable`: The table name for global hunts.
- `huntPoolTable`: The table name for hunt pools.
- `playerHuntTable`: The table name for player progress.

## Example Configuration

Here's an example of what your `datastore.json` might look like when using MongoDB:

```json
{
    "datastore": "MONGODB"
}
```
""".trimMargin()

    init {
        if (!readmeFile.exists()) {
            readmeFile.parentFile.mkdirs()
            readmeFile.createNewFile()
            readmeFile.writeText(instructions)
        }
    }
}