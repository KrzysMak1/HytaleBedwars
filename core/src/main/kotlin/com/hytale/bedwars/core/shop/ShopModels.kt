package com.hytale.bedwars.core.shop

import com.hytale.bedwars.core.economy.Price

enum class ShopCategory {
    BLOCKS,
    WEAPONS,
    ARMOR,
    TOOLS,
    UTILITIES,
}

data class ShopItem(
    val id: String,
    val name: String,
    val category: ShopCategory,
    val price: Price,
    val tier: Int = 1,
)
