package tech.sethi.pebbles.cobbledhunters.hunt.global

import com.cobblemon.mod.common.api.scheduling.after
import com.cobblemon.mod.common.pokemon.Pokemon
import com.google.gson.Gson
import dev.architectury.event.events.common.LifecycleEvent
import kotlinx.coroutines.*
import net.minecraft.entity.boss.ServerBossBar
import net.minecraft.server.network.ServerPlayerEntity
import tech.sethi.pebbles.cobbledhunters.config.baseconfig.LangConfig
import tech.sethi.pebbles.cobbledhunters.data.WebSocketHandler
import tech.sethi.pebbles.cobbledhunters.data.WebSocketHandler.GlobalHuntJoinHunt
import tech.sethi.pebbles.cobbledhunters.economy.EconomyHandler
import tech.sethi.pebbles.cobbledhunters.hunt.type.*
import tech.sethi.pebbles.cobbledhunters.util.PM
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors

object MongoGlobalHuntHandler : AbstractGlobalHuntHandler() {
    override val globalHuntPools: ConcurrentHashMap<String, GlobalHuntTracker?> = ConcurrentHashMap()
    override val activeBossbars: ConcurrentHashMap<String, ServerBossBar> = ConcurrentHashMap()

    val globalHuntWorker = Executors.newSingleThreadExecutor()

    val gson = Gson()

    init {
        onLoad()
    }

    fun onLoad() {

        LifecycleEvent.SERVER_STOPPING.register {
            globalHuntWorker.shutdownNow()
        }

    }

    override fun joinHunt(player: ServerPlayerEntity, poolId: String) {
        val balance = EconomyHandler.economy.getBalance(player.uuid)
        val joinHuntMessage = GlobalHuntJoinHunt(poolId, player.uuidAsString, player.name.string, balance)
        val socketMessage = WebSocketHandler.SocketMessage(
            WebSocketHandler.SocketMessageType.GLOBAL_HUNT_JOIN_HUNT, gson.toJson(joinHuntMessage)
        )

        WebSocketHandler.sendToWebSocket(socketMessage)
    }

    fun onPokemonAction(player: ServerPlayerEntity, pokemon: Pokemon, goal: HuntGoals) {
        val participant = Participant(player.uuidAsString, player.name.string)
        val types = listOf(pokemon.primaryType.name.toUpperCase(), pokemon.secondaryType?.name?.toUpperCase())
        val feature = PokemonFeature(
            pokemon.species.resourceIdentifier.path,
            types.filterNotNull().map { PokemonTypes.valueOf(it) },
            pokemon.level,
            pokemon.shiny,
            PokemonNatures.valueOf(pokemon.nature.name.path.toUpperCase()),
            HuntGender.valueOf(pokemon.gender.name.toUpperCase()),
            pokemon.ability.name,
            pokeballMap.filterValues { pokemon.caughtBall.name.toString() == it }.keys.firstOrNull(),
            pokemon.form.name,
            goal,
            pokemon.isWild()
        )

        val socketMessage = WebSocketHandler.SocketMessage(
            WebSocketHandler.SocketMessageType.GLOBAL_HUNT_POKEMON_ACTION,
            gson.toJson(WebSocketHandler.PokemonAction(participant, feature))
        )

        WebSocketHandler.sendToWebSocket(socketMessage)
    }

    data class PokemonFeature(
        val species: String,
        val types: List<PokemonTypes>,
        val level: Int,
        val shiny: Boolean,
        val nature: PokemonNatures,
        val gender: HuntGender,
        val ability: String,
        val ball: HuntBalls?,
        val form: String,
        val goal: HuntGoals,
        val wild: Boolean
    )
}