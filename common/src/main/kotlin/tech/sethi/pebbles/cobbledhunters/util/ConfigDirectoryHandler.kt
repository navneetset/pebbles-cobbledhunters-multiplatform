package tech.sethi.pebbles.cobbledhunters.util

import com.google.gson.Gson
import java.io.File

class ConfigDirectoryHandler<T>(
    private val clazz: Class<T>,
    private val directory: File,
    private val gson: Gson
) {
    var configs: List<T> = listOf()

    init {
        reload()
    }

    fun reload() {
        directory.mkdirs()
        configs = directory.listFiles()?.map { file ->
            val configString = file.readText()
            gson.fromJson(configString, clazz)
        } ?: listOf()
    }

    fun save() {
        configs.forEach { config ->
            val configString = gson.toJson(config)
            val file = File(directory, "${config.hashCode()}.json")
            file.writeText(configString)
        }
    }
}