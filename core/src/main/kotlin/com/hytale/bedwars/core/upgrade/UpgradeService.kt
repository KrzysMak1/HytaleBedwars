package com.hytale.bedwars.core.upgrade

import com.hytale.bedwars.core.economy.EconomyService
import com.hytale.bedwars.core.economy.Price
import com.hytale.bedwars.core.economy.Wallet

class UpgradeService(private val economyService: EconomyService) {
    fun purchase(
        wallet: Wallet,
        upgrade: TeamUpgrade,
        price: Price,
    ): Boolean {
        if (upgrade.level >= upgrade.maxLevel) {
            return false
        }
        if (!economyService.charge(wallet, price)) {
            return false
        }
        upgrade.level += 1
        return true
    }
}
