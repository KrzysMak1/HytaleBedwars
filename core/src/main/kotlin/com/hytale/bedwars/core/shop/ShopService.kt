package com.hytale.bedwars.core.shop

import com.hytale.bedwars.core.economy.EconomyService
import com.hytale.bedwars.core.economy.Price
import com.hytale.bedwars.core.economy.Wallet

class ShopService(private val economyService: EconomyService) {
    fun purchase(
        wallet: Wallet,
        item: ShopItem,
    ): Boolean {
        return economyService.charge(wallet, Price(item.price.currency, item.price.amount))
    }
}
