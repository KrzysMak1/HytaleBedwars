package com.hytale.bedwars.core.economy

class Wallet(private val balances: MutableMap<Currency, Int> = mutableMapOf()) {
    fun balance(currency: Currency): Int = balances[currency] ?: 0

    fun add(
        currency: Currency,
        amount: Int,
    ) {
        require(amount >= 0) { "Amount must be non-negative" }
        balances[currency] = balance(currency) + amount
    }

    fun remove(
        currency: Currency,
        amount: Int,
    ): Boolean {
        require(amount >= 0) { "Amount must be non-negative" }
        val current = balance(currency)
        return if (current >= amount) {
            balances[currency] = current - amount
            true
        } else {
            false
        }
    }
}
