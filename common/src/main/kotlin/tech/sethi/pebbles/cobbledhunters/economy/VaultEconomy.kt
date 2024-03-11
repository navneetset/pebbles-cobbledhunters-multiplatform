package tech.sethi.pebbles.cobbledhunters.economy

import tech.sethi.pebbles.forgevaultbridge.Forgevaultbridge
import java.util.*

class VaultEconomy : EconomyInterface {
    private val vEconomy = Forgevaultbridge.getEconomy()
    override fun getBalance(playerUUID: UUID): Double {
        val playerName = Forgevaultbridge.getPlayerName(playerUUID)
        return vEconomy.getBalance(playerName)
    }

    override fun withdraw(playerUUID: UUID, amount: Double) {
        val playerName = Forgevaultbridge.getPlayerName(playerUUID)
        vEconomy.withdrawPlayer(playerName, amount)
    }

    override fun deposit(playerUUID: UUID, amount: Double) {
        val playerName = Forgevaultbridge.getPlayerName(playerUUID)
        vEconomy.depositPlayer(playerName, amount)
    }
}