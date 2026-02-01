package com.hytale.bedwars.platform.match

import com.hytale.bedwars.core.api.PlatformBridge
import com.hytale.bedwars.core.block.BlockPosition
import com.hytale.bedwars.core.combat.KillAttributionService
import com.hytale.bedwars.core.map.MapTemplate
import com.hytale.bedwars.core.match.Match
import com.hytale.bedwars.core.match.MatchConfig
import com.hytale.bedwars.core.match.MatchEngine
import com.hytale.bedwars.core.player.PlayerSession
import com.hytale.bedwars.core.shop.ShopItem
import com.hytale.bedwars.core.team.Team
import com.hytale.bedwars.core.ui.ScoreboardProvider
import com.hytale.bedwars.core.upgrade.TeamUpgrade
import com.hytale.bedwars.core.util.TaskScheduler
import com.hytale.bedwars.platform.adapter.HytaleItemGrantor
import com.hytale.bedwars.platform.gui.ShopMenu
import com.hytale.bedwars.platform.gui.UpgradeMenu
import com.hytale.bedwars.platform.ui.ScoreboardRenderer
import com.hytale.bedwars.platform.world.WorldAdapter
import java.util.UUID

class MatchRuntime(
    private val platformBridge: PlatformBridge,
    private val match: Match,
    private val map: MapTemplate,
    private val worldAdapter: WorldAdapter,
    private val shopMenu: ShopMenu,
    private val upgradeMenu: UpgradeMenu,
    private val shopItems: List<ShopItem>,
    private val teamUpgrades: Map<String, List<TeamUpgrade>>,
    private val matchConfig: MatchConfig,
    private val taskScheduler: TaskScheduler,
    private val killAttributionService: KillAttributionService,
) {
    private val scoreboardRenderer = ScoreboardRenderer(platformBridge)
    private val scoreboardProvider = ScoreboardProvider()
    private val engine =
        MatchEngine(
            match = match,
            map = map,
            config = matchConfig,
            platformBridge = platformBridge,
            worldBridge = worldAdapter,
            taskScheduler = taskScheduler,
            itemGrantor = HytaleItemGrantor(platformBridge, worldAdapter),
            killAttributionService = killAttributionService,
        )

    fun start(nowMillis: Long) {
        engine.startMatch(nowMillis)
        match.players.keys.forEach { playerId ->
            scoreboardRenderer.start(playerId) {
                scoreboardProvider.build(match, map, platformBridge.currentTimeMillis())
            }
        }
    }

    fun handleBlockPlace(
        playerId: UUID,
        position: BlockPosition,
    ): Boolean {
        return engine.handleBlockPlace(playerId, position)
    }

    fun handleBlockBreak(
        playerId: UUID,
        position: BlockPosition,
    ): Boolean {
        return engine.handleBlockBreak(playerId, position)
    }

    fun handleBedBreak(
        breakerId: UUID,
        team: Team,
    ) {
        engine.handleBedBreak(breakerId, team.color)
    }

    fun teleportTeamsToSpawns() {
        map.teams.forEach { template ->
            val team = match.teams.firstOrNull { it.color == template.color } ?: return@forEach
            team.playerIds.forEach { playerId ->
                worldAdapter.teleport(playerId, template.spawn)
            }
        }
    }

    fun handleDamage(
        victim: PlayerSession,
        attackerId: UUID?,
        nowMillis: Long,
    ) {
        engine.handleDamage(victim, attackerId, nowMillis)
    }

    fun handleDeath(
        victim: PlayerSession,
        nowMillis: Long,
    ) {
        engine.handleDeath(victim, nowMillis)
    }

    fun handleMove(
        playerId: UUID,
        location: com.hytale.bedwars.core.map.Location,
    ) {
        engine.handleMove(playerId, location)
    }

    fun openShop(playerId: UUID) {
        shopMenu.open(playerId, shopItems)
    }

    fun purchaseShopItem(
        playerId: UUID,
        item: ShopItem,
    ): Boolean {
        return engine.openShop(playerId, item)
    }

    fun openUpgrades(
        playerId: UUID,
        team: Team,
    ) {
        val upgrades = teamUpgrades[team.id].orEmpty()
        upgradeMenu.open(playerId, upgrades)
    }

    fun canInteract(playerId: UUID): Boolean = engine.canInteract(playerId)

    fun canPickup(playerId: UUID): Boolean = engine.canPickup(playerId)

    fun canCollide(playerId: UUID): Boolean = engine.canCollide(playerId)
}
