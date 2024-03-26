package tech.sethi.pebbles.cobbledhunters.screenhandler

import com.cobblemon.mod.common.api.scheduling.afterOnMain
import com.cobblemon.mod.common.util.server
import dev.architectury.event.events.common.LifecycleEvent
import java.lang.Thread.sleep
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors

object ScreenRefresher {
    val activeGlobalHuntMenus = ConcurrentHashMap<String, GlobalHuntMenu>()
    val activePersonalHuntMenus = ConcurrentHashMap<String, PersonalHuntMenu>()

    val activeGlobalHuntInfoMenu = ConcurrentHashMap<String, GlobalHuntInfoMenu>()

    val refreshThread = Executors.newSingleThreadExecutor()

    init {
        refreshThread.submit {
            while (server() != null && server()!!.isRunning) {
                activeGlobalHuntMenus.forEach { (_, globalHuntMenu) ->
                    afterOnMain(0) { globalHuntMenu.setupPage() }
                }
                activePersonalHuntMenus.forEach { (_, personalHuntMenu) ->
                    afterOnMain(0) { personalHuntMenu.setupPage() }
                }

                activeGlobalHuntInfoMenu.forEach { (_, globalHuntInfoMenu) ->
                    afterOnMain(0) { globalHuntInfoMenu.refreshTimeStack() }
                }

                sleep(1000)
            }
        }

        LifecycleEvent.SERVER_STOPPING.register {
            refreshThread.shutdownNow()
        }
    }

    fun addGlobalHuntMenu(playerUUID: String, globalHuntMenu: GlobalHuntMenu) {
        activeGlobalHuntMenus[playerUUID] = globalHuntMenu
    }

    fun addPersonalHuntMenu(playerUUID: String, personalHuntMenu: PersonalHuntMenu) {
        activePersonalHuntMenus[playerUUID] = personalHuntMenu
    }

    fun addGlobalHuntInfoMenu(playerUUID: String, globalHuntInfoMenu: GlobalHuntInfoMenu) {
        activeGlobalHuntInfoMenu[playerUUID] = globalHuntInfoMenu
    }

    fun removeGlobalHuntMenu(playerUUID: String) {
        activeGlobalHuntMenus.remove(playerUUID)
    }

    fun removePersonalHuntMenu(playerUUID: String) {
        activePersonalHuntMenus.remove(playerUUID)
    }

    fun removeGlobalHuntInfoMenu(playerUUID: String) {
        activeGlobalHuntInfoMenu.remove(playerUUID)
    }
}