package com.hytale.bedwars.platform.plugin

import com.hytale.bedwars.core.economy.Currency
import com.hytale.bedwars.core.economy.Price
import com.hytale.bedwars.core.upgrade.TeamUpgrade
import com.hytale.bedwars.core.upgrade.UpgradeType
import org.yaml.snakeyaml.Yaml
import java.io.File

class UpgradeConfigLoader {
    private val yaml = Yaml()

    fun load(file: File): Map<UpgradeType, Pair<TeamUpgrade, Map<Int, Price>>> {
        val data = yaml.load<Map<String, Any>>(file.readText())
        val upgrades = data["upgrades"] as? Map<*, *> ?: return emptyMap()
        return upgrades.mapNotNull { (key, value) ->
            val type = UpgradeType.valueOf(key.toString())
            val config = value as? Map<*, *> ?: return@mapNotNull null
            val maxLevel = config["maxLevel"].toString().toInt()
            val costs = mutableMapOf<Int, Price>()
            val costEntry = config["cost"] as? Map<*, *>
            if (costEntry != null) {
                costs[1] = Price(
                    currency = Currency.valueOf(costEntry["currency"].toString()),
                    amount = costEntry["amount"].toString().toInt(),
                )
            }
            val costMap = config["costs"] as? Map<*, *>
            costMap?.forEach { (levelKey, costValue) ->
                val level = levelKey.toString().toInt()
                val cost = costValue as? Map<*, *> ?: return@forEach
                costs[level] = Price(
                    currency = Currency.valueOf(cost["currency"].toString()),
                    amount = cost["amount"].toString().toInt(),
                )
            }
            type to (TeamUpgrade(type, level = 0, maxLevel = maxLevel) to costs)
        }.toMap()
    }
}
