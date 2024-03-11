package tech.sethi.pebbles.cobbledhunters.economy

import java.util.*


interface EconomyInterface {
    fun getBalance(playerUUID: UUID): Double
    fun withdraw(playerUUID: UUID, amount: Double)
    fun deposit(playerUUID: UUID, amount: Double)
}
