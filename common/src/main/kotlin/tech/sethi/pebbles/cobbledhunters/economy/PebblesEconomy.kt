package tech.sethi.pebbles.cobbledhunters.economy

import tech.sethi.pebbleseconomy.PebblesEconomyInitializer
import java.util.*

class PebblesEconomy : EconomyInterface {
    private val pEconomy = PebblesEconomyInitializer.economy

    override fun getBalance(playerUUID: UUID): Double {
        return pEconomy.getBalance(playerUUID)
    }

    override fun withdraw(playerUUID: UUID, amount: Double) {
        pEconomy.withdraw(playerUUID, amount)
    }

    override fun deposit(playerUUID: UUID, amount: Double) {
        pEconomy.deposit(playerUUID, amount)
    }
}