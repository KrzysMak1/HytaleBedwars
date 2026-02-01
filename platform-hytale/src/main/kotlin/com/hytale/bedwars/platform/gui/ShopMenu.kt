package com.hytale.bedwars.platform.gui

import com.hytale.bedwars.core.shop.ShopItem

class ShopMenu {
    fun open(
        playerId: java.util.UUID,
        items: List<ShopItem>,
    ) {
        println("[ShopMenu] Opening shop for $playerId with ${items.size} items")
    }
}
