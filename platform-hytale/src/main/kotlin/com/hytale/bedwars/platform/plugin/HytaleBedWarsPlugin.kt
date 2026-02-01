package com.hytale.bedwars.platform.plugin

import com.hytale.bedwars.core.api.PlatformBridge
import com.hytale.bedwars.core.match.MatchMode
import com.hytale.bedwars.platform.adapter.HytalePlatformBridge
import com.hytale.bedwars.platform.listener.HytaleEventBus
import com.hytale.bedwars.platform.mapio.MapInstanceManager
import com.hytale.bedwars.platform.mapio.MapValidator
import com.hytale.bedwars.platform.mapio.MapYmlLoader
import com.hytale.bedwars.platform.world.HytaleWorldAdapter
import com.hytale.bedwars.storage.sqlite.SqlitePlayerStatsRepository
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
    private val eventBus = HytaleEventBus()
    private var statsRepository: SqlitePlayerStatsRepository? = null

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
        val statsDb = File("storage/bedwars.db")
        statsDb.parentFile?.mkdirs()
        statsRepository = SqlitePlayerStatsRepository(statsDb.path)

        val worldAdapter = HytaleWorldAdapter(platformBridge)
        val orchestrator =
            MatchOrchestrator(
                platformBridge,
                mapLoader,
                mapInstanceManager,
                config,
                worldAdapter,
                shopItems,
                upgradeTemplates,
                requireNotNull(statsRepository) { "Stats repository not initialized" },
                eventBus,
            )

        val match =
            orchestrator.createAndStart(
                matchId = "match-1",
                mapId = "example_map",
                mode = MatchMode.SOLO,
                players = listOf(UUID.randomUUID(), UUID.randomUUID()),
            )
        platformBridge.sendMessage(match.players.keys.first(), "Match ${match.matchId} started")
    }

    fun onDisable() {
        statsRepository?.shutdown()
        statsRepository = null
    }
}
