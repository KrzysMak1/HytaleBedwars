package com.hytale.bedwars.platform.plugin

import org.yaml.snakeyaml.Yaml
import java.io.File

class ConfigValidator {
    private val yaml = Yaml()

    fun validateConfig(configFile: File) {
        val config = yaml.load<Map<String, Any>>(configFile.readText())
        val requiredKeys =
            listOf(
                "configVersion",
                "minPlayers",
                "startingCountdownSeconds",
                "respawnDelaySeconds",
                "spawnProtectionSeconds",
                "assistWindowSeconds",
                "enableSpectatorJoin",
                "actionbarEnabled",
                "maxItemsOnGround",
                "mergeRadius",
                "cleanupItemsIntervalSeconds",
                "blockPlacePerSecondLimit",
                "breakableMapBlocks",
                "bedProtectionEnabled",
                "forgeLevels",
                "currencyDropOnDeath",
            )
        requireMissing(config, requiredKeys, "config.yml")
    }

    fun validateShop(shopFile: File) {
        val shop = yaml.load<Map<String, Any>>(shopFile.readText())
        require(shop.containsKey("categories")) { "shop.yml missing categories" }
    }

    fun validateUpgrades(upgradesFile: File) {
        val upgrades = yaml.load<Map<String, Any>>(upgradesFile.readText())
        require(upgrades.containsKey("upgrades")) { "upgrades.yml missing upgrades" }
    }

    fun validateMessages(messagesFile: File) {
        val messages = yaml.load<Map<String, Any>>(messagesFile.readText())
        require(messages.containsKey("prefix")) { "messages.yml missing prefix" }
    }

    private fun requireMissing(
        config: Map<String, Any>,
        keys: List<String>,
        name: String,
    ) {
        val missing = keys.filterNot { config.containsKey(it) }
        require(missing.isEmpty()) { "Missing keys in $name: ${missing.joinToString()}" }
    }
}
