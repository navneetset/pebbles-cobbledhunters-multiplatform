package tech.sethi.pebbles.cobbledhunters.data

import com.cobblemon.mod.common.item.PokemonItem
import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.item.ItemStack
import tech.sethi.pebbles.cobbledhunters.hunt.type.*
import tech.sethi.pebbles.cobbledhunters.util.PM

interface DatabaseHandlerInterface {
    fun reload()
    fun getRewards(): List<HuntReward>
    fun getReward(id: String): HuntReward?
    fun getGlobalHunts(): List<Hunt>
    fun getGlobalHuntPools(): List<HuntPool>
    fun getGlobalHuntSessions(): Map<String, GlobalHuntSession>
    fun getPersonalHunts(): List<Hunt>
    fun getPersonalHuntSessions(): Map<String, PersonalHuntSession>

    fun addGlobalHuntSession(huntSession: GlobalHuntSession): Boolean
    fun updateGlobalHuntSession(huntSession: GlobalHuntSession)

    fun initPlayerRewardStorage(playerUUID: String, playerName: String)
    fun getPlayerRewardStorage(playerUUID: String): RewardStorage?
    fun addPlayerReward(playerUUID: String, reward: HuntReward)
    fun removePlayerReward(playerUUID: String, index: Int)

    fun ping()
    fun close()
}

fun Pokemon.toHuntDisplayStack(hunt: Hunt): ItemStack {
    val pokemonStack = PokemonItem.from(this)
    val species = this.species.translatedName.string

    val huntballType = pokeballMap[hunt.huntFeature.ball]
    var huntballName: String? = "Any Ball"

    if (huntballType != null) {
        huntballName = PM.getLocaleText(PM.getItem(huntballType).translationKey)
    }

    val levelRangeString =
        hunt.huntFeature.levelRange.min.toString() + " - " + hunt.huntFeature.levelRange.max.toString()

    val placeholderMap = mapOf(
        "{huntball}" to huntballName,
        "{shiny}" to if (hunt.huntFeature.shiny) "<gold>★</gold>" else "",
        "{level_range}" to levelRangeString,
        "{ability}" to hunt.huntFeature.ability?.let { PM.getLocaleText(it) },
        "{gender}" to hunt.huntFeature.gender.name,
        "{form}" to hunt.huntFeature.form,
    )

    return pokemonStack
}

data class PlayerRewards(
    val playerUUID: String, var playerName: String?, var rewards: MutableList<HuntReward> = mutableListOf()
)