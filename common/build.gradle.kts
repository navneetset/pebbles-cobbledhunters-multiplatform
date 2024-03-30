architectury {
    common(rootProject.property("enabled_platforms").toString().split(","))
}

loom {
    accessWidenerPath.set(file("src/main/resources/cobbledhunters.accesswidener"))
}

repositories {
    flatDir {
        dirs("libs")
    }
}

dependencies {

    // We depend on fabric loader here to use the fabric @Environment annotations and get the mixin dependencies
    // Do NOT use other classes from fabric loader
    modImplementation("net.fabricmc:fabric-loader:${rootProject.property("fabric_loader_version")}")
    // Remove the next line if you don't want to depend on the API
    modApi("dev.architectury:architectury:${rootProject.property("architectury_version")}")

    modImplementation("com.cobblemon:mod:1.4.0+1.20.1")

    implementation("net.kyori:adventure-api:${property("minimessage_version")}")
    implementation("net.kyori:adventure-text-minimessage:${property("minimessage_version")}")
    implementation("net.kyori:adventure-text-serializer-gson:${property("minimessage_version")}")


    implementation("io.ktor:ktor-client-core:${rootProject.property("ktor_version")}")
    implementation("io.ktor:ktor-client-cio:${rootProject.property("ktor_version")}")
    implementation("io.ktor:ktor-client-serialization:${rootProject.property("ktor_version")}")
    implementation("io.ktor:ktor-client-websockets:${rootProject.property("ktor_version")}")
    implementation("io.ktor:ktor-client-core-jvm:${rootProject.property("ktor_version")}")
    implementation("io.ktor:ktor-client-cio-jvm:${rootProject.property("ktor_version")}")
    implementation("com.squareup.okhttp3:okhttp:4.9.2")

    implementation("org.mongodb:mongodb-driver-core:${property("mongo_version")}")
    implementation("org.mongodb:mongodb-driver-sync:${property("mongo_version")}")
    implementation(files("libs/mariadb-jdbc-3.1.4+20230506-all.jar"))

    modImplementation(files("libs/pebbles-economy-1.0.0.jar"))
    modImplementation(files("libs/Impactor-Fabric-5.0.0+1.19.2.jar"))
    modImplementation(files("libs/forgevaultbridge-1.0-SNAPSHOT.jar"))
    modImplementation(files("libs/pebbles-partyapi-1.0.0.jar"))

    modImplementation("net.fabricmc:fabric-language-kotlin:${rootProject.property("fabric_kotlin_version")}")

    implementation("redis.clients:jedis:5.1.0")

    compileOnly("net.luckperms:api:5.4")
}