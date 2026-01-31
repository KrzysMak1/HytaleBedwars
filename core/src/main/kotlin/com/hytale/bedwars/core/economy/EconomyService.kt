package com.hytale.bedwars.core.economy

data class Price(val currency: Currency, val amount: Int)

class EconomyService {
    fun charge(wallet: Wallet, price: Price): Boolean {
        if (price.amount <= 0) {
            return true
        }
        return wallet.remove(price.currency, price.amount)
    }

    fun chargeInventory(inventory: CurrencyInventory, price: Price): Boolean {
        if (price.amount <= 0) {
            return true
        }
        return inventory.removeExact(price.currency, price.amount)
    }

    fun refund(wallet: Wallet, price: Price) {
        if (price.amount <= 0) {
            return
        }
        wallet.add(price.currency, price.amount)
    }
}
