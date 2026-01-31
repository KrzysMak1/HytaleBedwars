package com.hytale.bedwars.api

import com.hytale.bedwars.core.match.Match
import com.hytale.bedwars.core.shop.ShopItem
import java.util.UUID

class BedwarsAPI {
    private val listeners = mutableSetOf<Any>()
    private val customShopItems = mutableListOf<ShopItem>()

    fun getMatchOfPlayer(playerId: UUID): Match? = null

    fun getAllMatches(): List<Match> = emptyList()

    fun addCustomShopItem(item: ShopItem) {
        customShopItems.add(item)
    }

    fun registerListener(listener: Any) {
        listeners.add(listener)
    }
}
