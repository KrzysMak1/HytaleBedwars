package com.hytale.bedwars.core.shop

import com.hytale.bedwars.core.api.ItemGrantor
import com.hytale.bedwars.core.economy.CurrencyInventory
import com.hytale.bedwars.core.economy.EconomyService
import com.hytale.bedwars.core.economy.Price
import java.util.UUID

class ShopTransactionService(private val economyService: EconomyService) {
    fun purchase(
        playerId: UUID,
        inventory: CurrencyInventory,
        item: ShopItem,
        itemGrantor: ItemGrantor,
        dropLocation: com.hytale.bedwars.core.map.Location,
    ): Boolean {
        val price = Price(item.price.currency, item.price.amount)
        if (!economyService.chargeInventory(inventory, price)) {
            return false
        }
        val hasSpace = itemGrantor.hasSpace(playerId, item.id, 1)
        val given = if (hasSpace) itemGrantor.giveItem(playerId, item.id, 1) else false
        if (!given) {
            itemGrantor.dropItem(playerId, item.id, 1, dropLocation)
        }
        return true
    }
}
