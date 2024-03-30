package tech.sethi.pebbles.cobbledhunters

import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import dev.architectury.event.EventResult
import dev.architectury.event.events.common.CommandRegistrationEvent
import dev.architectury.event.events.common.EntityEvent
import dev.architectury.event.events.common.LifecycleEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import org.apache.logging.log4j.LogManager
import tech.sethi.pebbles.cobbledhunters.command.HuntCommand
import tech.sethi.pebbles.cobbledhunters.config.ConfigHandler
import tech.sethi.pebbles.cobbledhunters.config.datastore.DatastoreConfig
import tech.sethi.pebbles.cobbledhunters.data.WebSocketHandler
import tech.sethi.pebbles.cobbledhunters.economy.EconomyHandler
import tech.sethi.pebbles.cobbledhunters.hunt.global.GlobalHuntHandler
import tech.sethi.pebbles.cobbledhunters.hunt.global.JSONGlobalHuntHandler
import tech.sethi.pebbles.cobbledhunters.hunt.global.MongoGlobalHuntHandler
import tech.sethi.pebbles.cobbledhunters.hunt.personal.PersonalHuntHandler
import tech.sethi.pebbles.cobbledhunters.hunt.type.HuntGoals

object CobbledHunters {
    const val MOD_ID = "pebbles_cobbledhunters"
    val LOGGER = LogManager.getLogger()
    var sever: MinecraftServer? = null

    fun init() {
        LOGGER.info("Pebble's Cobbled Hunters Initialized!")

        CommandRegistrationEvent.EVENT.register { dispatcher, _, _ ->
            HuntCommand.register(dispatcher)
        }

        LifecycleEvent.SERVER_STARTING.register { server ->
            sever = server
            ConfigHandler
        }

        LifecycleEvent.SERVER_STARTED.register {
            PersonalHuntHandler
            GlobalHuntHandler
            EconomyHandler

            WebSocketHandler
        }

        CobblemonEvents.POKEMON_CAPTURED.subscribe { event ->
            CoroutineScope(Dispatchers.IO).launch {
                PersonalHuntHandler.handler!!.onPokemonAction(event.player, event.pokemon, HuntGoals.CATCH)
                if (DatastoreConfig.config.datastore == DatastoreConfig.DatastoreType.JSON) {
                    JSONGlobalHuntHandler.onPokemonAction(event.player, event.pokemon, HuntGoals.CATCH)
                } else {
                    MongoGlobalHuntHandler.onPokemonAction(event.player, event.pokemon, HuntGoals.CATCH)
                }
            }
        }

        CobblemonEvents.POKEMON_FAINTED.subscribe { event ->
            if (event.pokemon.isWild() && event.pokemon.entity != null && event.pokemon.entity!!.killer is ServerPlayerEntity) {
                val killer = event.pokemon.entity!!.killer as ServerPlayerEntity
                val dmgSource = event.pokemon.entity?.recentDamageSource
                if (dmgSource != null && dmgSource.attacker is ServerPlayerEntity) return@subscribe
                CoroutineScope(Dispatchers.IO).launch {
                    PersonalHuntHandler.handler!!.onPokemonAction(killer, event.pokemon, HuntGoals.DEFEAT)
                    if (DatastoreConfig.config.datastore == DatastoreConfig.DatastoreType.JSON) {
                        JSONGlobalHuntHandler.onPokemonAction(killer, event.pokemon, HuntGoals.DEFEAT)
                    } else {
                        MongoGlobalHuntHandler.onPokemonAction(killer, event.pokemon, HuntGoals.DEFEAT)
                    }
                }
            }
        }

        EntityEvent.LIVING_DEATH.register { entity, dmgSource ->
            if (entity is PokemonEntity && entity.pokemon.isWild()) {
                val attacker = dmgSource.attacker
                if (attacker is ServerPlayerEntity) {
                    CoroutineScope(Dispatchers.IO).launch {
                        PersonalHuntHandler.handler!!.onPokemonAction(attacker, entity.pokemon, HuntGoals.KILL)
                        if (DatastoreConfig.config.datastore == DatastoreConfig.DatastoreType.JSON) {
                            JSONGlobalHuntHandler.onPokemonAction(attacker, entity.pokemon, HuntGoals.KILL)
                        } else {
                            MongoGlobalHuntHandler.onPokemonAction(attacker, entity.pokemon, HuntGoals.KILL)
                        }
                    }
                }
            }

            EventResult.pass()
        }
    }
}