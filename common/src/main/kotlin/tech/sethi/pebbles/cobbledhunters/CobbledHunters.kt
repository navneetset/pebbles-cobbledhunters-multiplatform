package tech.sethi.pebbles.cobbledhunters

import com.cobblemon.mod.common.api.events.CobblemonEvents
import dev.architectury.event.events.common.CommandRegistrationEvent
import dev.architectury.event.events.common.LifecycleEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.minecraft.server.MinecraftServer
import org.apache.logging.log4j.LogManager
import tech.sethi.pebbles.cobbledhunters.command.HuntCommand
import tech.sethi.pebbles.cobbledhunters.config.ConfigHandler
import tech.sethi.pebbles.cobbledhunters.hunt.PersonalHuntHandler

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
        }

        CoroutineScope(Dispatchers.IO).launch {
            CobblemonEvents.POKEMON_CAPTURED.subscribe { event ->
                PersonalHuntHandler.onPokemonCaptured(event.player, event.pokemon)
            }
        }

    }
}