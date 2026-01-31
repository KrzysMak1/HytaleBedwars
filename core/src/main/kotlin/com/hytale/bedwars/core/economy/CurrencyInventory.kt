package com.hytale.bedwars.core.economy

data class CurrencyStack(val currency: Currency, var amount: Int)

class CurrencyInventory(stacks: List<CurrencyStack> = emptyList()) {
    private val stacks: MutableList<CurrencyStack> = stacks.toMutableList()

    fun add(currency: Currency, amount: Int) {
        require(amount >= 0) { "Amount must be non-negative" }
        stacks.add(CurrencyStack(currency, amount))
    }

    fun total(currency: Currency): Int = stacks.filter { it.currency == currency }.sumOf { it.amount }

    fun removeExact(currency: Currency, amount: Int): Boolean {
        require(amount >= 0) { "Amount must be non-negative" }
        if (total(currency) < amount) {
            return false
        }
        var remaining = amount
        val iterator = stacks.iterator()
        while (iterator.hasNext() && remaining > 0) {
            val stack = iterator.next()
            if (stack.currency != currency) {
                continue
            }
            val take = minOf(stack.amount, remaining)
            stack.amount -= take
            remaining -= take
            if (stack.amount == 0) {
                iterator.remove()
            }
        }
        return true
    }

    fun snapshot(): List<CurrencyStack> = stacks.map { CurrencyStack(it.currency, it.amount) }
}
