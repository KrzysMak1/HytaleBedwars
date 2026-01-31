package com.hytale.bedwars.platform.plugin

import com.hytale.bedwars.core.economy.Currency
import com.hytale.bedwars.core.economy.Price
import com.hytale.bedwars.core.shop.ShopCategory
import com.hytale.bedwars.core.shop.ShopItem
import org.yaml.snakeyaml.Yaml
import java.io.File

class ShopConfigLoader {
    private val yaml = Yaml()

    fun load(file: File): List<ShopItem> {
        val data = yaml.load<Map<String, Any>>(file.readText())
        val categories = data["categories"] as? Map<*, *> ?: return emptyList()
        return categories.flatMap { (categoryKey, itemsValue) ->
            val category = ShopCategory.valueOf(categoryKey.toString())
            val items = (itemsValue as? List<*>)?.mapNotNull { it as? Map<*, *> }.orEmpty()
            items.map { item ->
                ShopItem(
                    id = item["id"].toString(),
                    name = item["name"].toString(),
                    category = category,
                    price = Price(
                        currency = Currency.valueOf(item["currency"].toString()),
                        amount = item["amount"].toString().toInt(),
                    ),
                    tier = item["tier"]?.toString()?.toInt() ?: 1,
                )
            }
        }
    }
}
