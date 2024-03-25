package tech.sethi.pebbles.cobbledhunters.screenhandler

import com.cobblemon.mod.common.api.scheduling.afterOnMain
import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentHashMap

object ScreenRefresher {
    val activeGlobalHuntMenus = ConcurrentHashMap<String, GlobalHuntMenu>()
    val activePersonalHuntMenus = ConcurrentHashMap<String, PersonalHuntMenu>()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            while (this.isActive) {
                activeGlobalHuntMenus.forEach { (_, globalHuntMenu) ->
                    afterOnMain(0) { globalHuntMenu.setupPage() }

                }
                activePersonalHuntMenus.forEach { (_, personalHuntMenu) ->
                    afterOnMain(0) { personalHuntMenu.setupPage() }
                }
                delay(1000)
            }
        }
    }

    fun addGlobalHuntMenu(playerUUID: String, globalHuntMenu: GlobalHuntMenu) {
        activeGlobalHuntMenus[playerUUID] = globalHuntMenu
    }

    fun addPersonalHuntMenu(playerUUID: String, personalHuntMenu: PersonalHuntMenu) {
        activePersonalHuntMenus[playerUUID] = personalHuntMenu
    }

    fun removeGlobalHuntMenu(playerUUID: String) {
        activeGlobalHuntMenus.remove(playerUUID)
    }

    fun removePersonalHuntMenu(playerUUID: String) {
        activePersonalHuntMenus.remove(playerUUID)
    }
}