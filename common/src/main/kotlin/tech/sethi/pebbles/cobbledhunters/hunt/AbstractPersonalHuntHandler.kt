package tech.sethi.pebbles.cobbledhunters.hunt

import net.minecraft.entity.boss.ServerBossBar
import tech.sethi.pebbles.cobbledhunters.hunt.type.HuntTracker
import tech.sethi.pebbles.cobbledhunters.hunt.type.PersonalHunts
import java.util.concurrent.ConcurrentHashMap

abstract class AbstractPersonalHuntHandler {
    abstract val personalHunts: ConcurrentHashMap<String, PersonalHunts>
    abstract val rolledHunts: ConcurrentHashMap<String, HuntTracker>
    abstract val activeBossbars: ConcurrentHashMap<String, ServerBossBar>
}