package com.hytale.bedwars.platform.plugin

import com.hytale.bedwars.core.api.PlatformBridge
import com.hytale.bedwars.core.match.MatchMode
import com.hytale.bedwars.platform.adapter.HytalePlatformBridge
import com.hytale.bedwars.platform.mapio.MapInstanceManager
import com.hytale.bedwars.platform.mapio.MapValidator
import com.hytale.bedwars.platform.mapio.MapYmlLoader
import com.hytale.bedwars.platform.world.HytaleWorldAdapter
import java.io.File
import java.util.UUID

class HytaleBedWarsPlugin {
    private val platformBridge: PlatformBridge = HytalePlatformBridge()
    private val mapsRoot = File("maps")
    private val mapLoader = MapYmlLoader(mapsRoot)
    private val mapValidator = MapValidator(mapLoader)
    private val mapInstanceManager = MapInstanceManager(mapsRoot)
    private val configValidator = ConfigValidator()
    private val configLoader = ConfigLoader()
    private val migrationRunner = ConfigMigrationRunner()
    private val shopConfigLoader = ShopConfigLoader()
    private val upgradeConfigLoader = UpgradeConfigLoader()

    fun onEnable() {
        val configFile = File("config/config.yml")
        val configVersion = configLoader.readVersion(configFile)
        migrationRunner.run(configVersion, 2)
        val config = configLoader.loadConfig(configFile)

        configValidator.validateConfig(configFile)
        configValidator.validateShop(File("config/shop.yml"))
        configValidator.validateUpgrades(File("config/upgrades.yml"))
        configValidator.validateMessages(File("config/messages.yml"))
        mapInstanceManager.cleanupOrphansOnStartup()
        mapValidator.validateAll(mapsRoot)

        val shopItems = shopConfigLoader.load(File("config/shop.yml"))
        val upgradeTemplates = upgradeConfigLoader.load(File("config/upgrades.yml")).values.map { it.first }

        val worldAdapter = HytaleWorldAdapter(platformBridge)
        val orchestrator = MatchOrchestrator(
            platformBridge,
            mapLoader,
            mapInstanceManager,
            config,
            worldAdapter,
            shopItems,
            upgradeTemplates,
        )

        val match = orchestrator.createAndStart(
            matchId = "match-1",
            mapId = "example_map",
            mode = MatchMode.SOLO,
            players = listOf(UUID.randomUUID(), UUID.randomUUID()),
        )
        platformBridge.sendMessage(match.players.keys.first(), "Match ${match.matchId} started")
    }
}
