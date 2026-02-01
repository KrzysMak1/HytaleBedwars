package com.hytale.bedwars.platform.plugin

import com.hytale.bedwars.core.block.BlockPosition
import com.hytale.bedwars.core.economy.Currency
import com.hytale.bedwars.core.match.MatchConfig
import org.yaml.snakeyaml.Yaml
import java.io.File

class ConfigLoader {
    private val yaml = Yaml()

    fun readVersion(file: File): Int {
        val data = yaml.load<Map<String, Any>>(file.readText())
        return data["configVersion"].toString().toInt()
    }

    fun loadConfig(file: File): MatchConfig {
        val data = yaml.load<Map<String, Any>>(file.readText())
        val currencyMap =
            (data["currencyDropOnDeath"] as? Map<*, *>).orEmpty().mapKeys { (k, _) ->
                Currency.valueOf(k.toString())
            }.mapValues { (_, v) ->
                v.toString().toBoolean()
            }
        val breakableBlocks =
            (data["breakableMapBlocks"] as? List<*>)?.mapNotNull { entry ->
                val map = entry as? Map<*, *> ?: return@mapNotNull null
                val x = map["x"]?.toString()?.toIntOrNull() ?: return@mapNotNull null
                val y = map["y"]?.toString()?.toIntOrNull() ?: return@mapNotNull null
                val z = map["z"]?.toString()?.toIntOrNull() ?: return@mapNotNull null
                BlockPosition(x, y, z)
            }?.toSet().orEmpty()
        return MatchConfig(
            minPlayers = data["minPlayers"].toString().toInt(),
            startingCountdownSeconds = data["startingCountdownSeconds"].toString().toInt(),
            respawnDelaySeconds = data["respawnDelaySeconds"].toString().toInt(),
            spawnProtectionSeconds = data["spawnProtectionSeconds"].toString().toInt(),
            assistWindowSeconds = data["assistWindowSeconds"].toString().toLong(),
            enableSpectatorJoin = data["enableSpectatorJoin"].toString().toBoolean(),
            maxItemsOnGround = data["maxItemsOnGround"].toString().toInt(),
            mergeRadius = data["mergeRadius"].toString().toInt(),
            blockPlacePerSecondLimit = data["blockPlacePerSecondLimit"].toString().toInt(),
            breakableMapBlocks = breakableBlocks,
            bedProtectionEnabled = data["bedProtectionEnabled"].toString().toBoolean(),
            currencyDropOnDeath = currencyMap,
        )
    }
}
