package tech.sethi.pebbles.cobbledhunters.quilt

import tech.sethi.pebbles.cobbledhunters.fabriclike.CobbledHuntersFabricLike
import org.quiltmc.loader.api.ModContainer
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer

object CobbledHuntersQuilt: ModInitializer {
    override fun onInitialize(mod: ModContainer?) {
        CobbledHuntersFabricLike.init()
    }
}