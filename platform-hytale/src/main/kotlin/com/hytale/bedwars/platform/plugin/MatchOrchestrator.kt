package com.hytale.bedwars.platform.plugin

import com.hytale.bedwars.core.api.PlatformBridge
import com.hytale.bedwars.core.match.Match
import com.hytale.bedwars.core.match.MatchConfig
import com.hytale.bedwars.core.match.MatchMode
import com.hytale.bedwars.core.match.MatchService
import com.hytale.bedwars.core.match.MatchState
import com.hytale.bedwars.core.player.PlayerSession
import com.hytale.bedwars.core.shop.ShopItem
import com.hytale.bedwars.core.upgrade.TeamUpgrade
import com.hytale.bedwars.platform.gui.ShopMenu
import com.hytale.bedwars.platform.gui.UpgradeMenu
import com.hytale.bedwars.platform.match.MatchRuntime
import com.hytale.bedwars.platform.mapio.MapInstanceManager
import com.hytale.bedwars.platform.mapio.MapYmlLoader
import com.hytale.bedwars.platform.world.WorldAdapter
import java.util.UUID
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class MatchOrchestrator(
    private val platformBridge: PlatformBridge,
    private val mapLoader: MapYmlLoader,
    private val mapInstanceManager: MapInstanceManager,
    private val matchConfig: MatchConfig,
    private val worldAdapter: WorldAdapter,
    private val shopItems: List<ShopItem>,
    private val upgradeTemplates: List<TeamUpgrade>,
    private val matchService: MatchService = MatchService(),
) {
    private val scheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private val runtimes = mutableMapOf<String, MatchRuntime>()
    private val templates = mutableMapOf<String, com.hytale.bedwars.core.map.MapTemplate>()

    fun createAndStart(matchId: String, mapId: String, mode: MatchMode, players: List<UUID>): Match {
        val map = mapLoader.load(mapId)
        templates[matchId] = map
        val teamTemplates = map.teams.map { it.color to it.name }
        val match = matchService.createMatch(matchId, mode, mapId, players, teamTemplates)
        transition(match, MatchState.LOBBY)
        startCountdown(match)
        return match
    }

    private fun transition(match: Match, state: MatchState) {
        matchService.transition(match, state)
        platformBridge.sendMessage(
            match.players.keys.firstOrNull() ?: return,
            "[${match.matchId}] state -> $state",
        )
    }

    private fun startCountdown(match: Match) {
        transition(match, MatchState.STARTING)
        scheduler.schedule({
            loadMap(match)
        }, matchConfig.startingCountdownSeconds.toLong(), TimeUnit.SECONDS)
    }

    private fun loadMap(match: Match) {
        transition(match, MatchState.LOADING_MAP)
        val instanceId = mapInstanceManager.cloneTemplate(match.mapTemplateId, match.matchId)
        match.mapInstanceId = instanceId
        scheduler.schedule({
            teleportPlayers(match)
        }, 1, TimeUnit.SECONDS)
    }

    private fun teleportPlayers(match: Match) {
        transition(match, MatchState.TELEPORTING)
        scheduler.schedule({
            beginMatch(match)
        }, 1, TimeUnit.SECONDS)
    }

    private fun beginMatch(match: Match) {
        transition(match, MatchState.INGAME)
        val template = templates[match.matchId] ?: return
        val upgradesByTeam = match.teams.associate { team ->
            team.id to upgradeTemplates.map { it.copy() }
        }
        val runtime = MatchRuntime(
            platformBridge = platformBridge,
            match = match,
            map = template,
            tracker = match.placedBlocksTracker,
            worldAdapter = worldAdapter,
            shopMenu = ShopMenu(),
            upgradeMenu = UpgradeMenu(),
            shopItems = shopItems,
            teamUpgrades = upgradesByTeam,
            killAttributionService = com.hytale.bedwars.core.combat.KillAttributionService(matchConfig.assistWindowSeconds),
        )
        runtime.teleportTeamsToSpawns()
        runtimes[match.matchId] = runtime
    }

    fun endMatch(match: Match) {
        transition(match, MatchState.ENDING)
        scheduler.schedule({
            transition(match, MatchState.DESTROYING)
            match.mapInstanceId?.let { mapInstanceManager.destroyInstance(it) }
        }, 3, TimeUnit.SECONDS)
    }
}
