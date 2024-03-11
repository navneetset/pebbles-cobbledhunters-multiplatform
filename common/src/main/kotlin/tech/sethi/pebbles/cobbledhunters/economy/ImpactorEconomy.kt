package tech.sethi.pebbles.cobbledhunters.economy

import net.impactdev.impactor.api.economy.EconomyService
import java.util.*

class ImpactorEconomy : EconomyInterface {

    private val iEconomy: EconomyService = EconomyService.instance()

    override fun getBalance(playerUUID: UUID): Double {
        return iEconomy.account(playerUUID).get().balance().toDouble()
    }

    override fun withdraw(playerUUID: UUID, amount: Double) {
        val account = iEconomy.account(playerUUID).get()
        account.withdraw(amount.toBigDecimal())
    }


    override fun deposit(playerUUID: UUID, amount: Double) {
        val account = iEconomy.account(playerUUID).get()
        account.deposit(amount.toBigDecimal())
    }
}