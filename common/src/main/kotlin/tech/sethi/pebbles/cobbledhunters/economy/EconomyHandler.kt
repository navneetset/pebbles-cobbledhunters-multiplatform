package tech.sethi.pebbles.cobbledhunters.economy

import tech.sethi.pebbles.cobbledhunters.CobbledHunters
import tech.sethi.pebbles.cobbledhunters.config.economy.EconomyConfig

object EconomyHandler {

    lateinit var economy: EconomyInterface

    init {
        reload()
    }

    fun reload() {
        val economyConfig = EconomyConfig.economyConfig
        try {
            economy = when (economyConfig.economy) {
                EconomyConfig.EconomyType.PEBBLES -> PebblesEconomy()
                EconomyConfig.EconomyType.VAULT -> VaultEconomy()
                EconomyConfig.EconomyType.IMPACTOR -> ImpactorEconomy()
            }
        } catch (e: NoClassDefFoundError) {
            CobbledHunters.LOGGER.error("Failed to load economy, please configure economy in config file.")
            throw e
        }
    }
}