package tech.sethi.pebbles.cobbledhunters.forge

import dev.architectury.platform.forge.EventBuses
import tech.sethi.pebbles.cobbledhunters.CobbledHunters
import net.minecraftforge.fml.common.Mod
import thedarkcolour.kotlinforforge.forge.MOD_BUS

@Mod(CobbledHunters.MOD_ID)
object CobbledHuntersForge {
    init {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(CobbledHunters.MOD_ID, MOD_BUS)
        CobbledHunters.init()
    }
}