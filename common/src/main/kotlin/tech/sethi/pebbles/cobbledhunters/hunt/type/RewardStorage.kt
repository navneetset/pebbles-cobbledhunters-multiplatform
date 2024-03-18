package tech.sethi.pebbles.cobbledhunters.hunt.type

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

data class RewardStorage(
    val playerUUID: String, val playerName: String, val rewards: MutableMap<String, HuntReward> = mutableMapOf()
)

class RewardsDeserializer : JsonDeserializer<RewardStorage> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): RewardStorage {
        val jsonObject = json.asJsonObject

        val playerUUID = jsonObject.get("playerUUID").asString
        val playerName = jsonObject.get("playerName").asString

        val rewards = mutableMapOf<String, HuntReward>()
        val rewardsJson = jsonObject.getAsJsonObject("rewards")
        rewardsJson.entrySet().forEach { entry ->
            val reward = context.deserialize<HuntReward>(entry.value, HuntReward::class.java)
            rewards[entry.key] = reward
        }

        return RewardStorage(playerUUID, playerName, rewards)
    }
}

//class NodeMapDeserializer : JsonDeserializer<AnimationLoader.NodeMap> {
//    override fun deserialize(
//        json: JsonElement, typeOfT: Type, context: JsonDeserializationContext
//    ): AnimationLoader.NodeMap {
//        val nodes = mutableMapOf<String, AnimationLoader.Node>()
//        json.asJsonObject.entrySet().forEach { entry ->
//            val node = context.deserialize<AnimationLoader.Node>(entry.value, AnimationLoader.Node::class.java)
//            nodes[entry.key] = node
//        }
//        return AnimationLoader.NodeMap(nodes)
//    }
//}