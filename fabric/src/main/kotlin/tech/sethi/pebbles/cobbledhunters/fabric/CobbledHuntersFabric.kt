package tech.sethi.pebbles.cobbledhunters.fabric

import tech.sethi.pebbles.cobbledhunters.fabriclike.CobbledHuntersFabricLike
import net.fabricmc.api.ModInitializer


object CobbledHuntersFabric: ModInitializer {
    override fun onInitialize() {
        CobbledHuntersFabricLike.init()
    }
}
