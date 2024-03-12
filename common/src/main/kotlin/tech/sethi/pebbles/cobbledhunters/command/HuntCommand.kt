package tech.sethi.pebbles.cobbledhunters.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import tech.sethi.pebbles.cobbledhunters.config.ConfigHandler
import tech.sethi.pebbles.cobbledhunters.data.DatabaseHandler
import tech.sethi.pebbles.cobbledhunters.economy.EconomyHandler
import tech.sethi.pebbles.cobbledhunters.screenhandler.selectionMenuScreenHandlerFactory
import tech.sethi.pebbles.cobbledhunters.util.PM
import tech.sethi.pebbles.cobbledhunters.util.PermUtil

object HuntCommand {

    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {

        val huntCommand =
            literal("hunt")
                .executes { context: CommandContext<ServerCommandSource> ->
                    val player = context.source.player ?: return@executes 1.also {
                        context.source.sendFeedback(
                            { PM.returnStyledText("<red>Only players can use this command</red>") }, false
                        )
                    }

                    player.openHandledScreen(selectionMenuScreenHandlerFactory(player))

                    1
                }

        val reloadCommand =
            literal("reload").requires { PermUtil.commandRequiresPermission(it, "pebbles.cobbledhunts.reload") }
                .executes { context: CommandContext<ServerCommandSource> ->
                    ConfigHandler.reload()

                    EconomyHandler.reload()

                    DatabaseHandler.reload()

                    context.source.sendFeedback(
                        { PM.returnStyledText("<blue>Reloaded Cobbled Hunters config!") }, false
                    )
                    1
                }


        huntCommand.then(reloadCommand)

        dispatcher.register(huntCommand)
    }

}