package tech.sethi.pebbles.cobbledhunters.economy

import tech.sethi.pebbles.cobbledhunters.config.economy.EconomyConfig

object EconomyHandler {

    lateinit var economy: EconomyInterface

    init {
        reload()
    }

    fun reload() {
        val economyConfig = EconomyConfig.economyConfig
        economy = when (economyConfig.economy) {
            EconomyConfig.EconomyType.PEBBLES -> PebblesEconomy()
            EconomyConfig.EconomyType.VAULT -> VaultEconomy()
            EconomyConfig.EconomyType.IMPACTOR -> ImpactorEconomy()
        }
    }
}