package com.hytale.bedwars.platform.plugin

import com.hytale.bedwars.core.api.PlatformBridge
import com.hytale.bedwars.core.match.Match
import com.hytale.bedwars.core.match.MatchConfig
import com.hytale.bedwars.core.match.MatchMode
import com.hytale.bedwars.core.match.MatchService
import com.hytale.bedwars.core.match.MatchState
import com.hytale.bedwars.core.shop.ShopItem
import com.hytale.bedwars.core.stats.StatsService
import com.hytale.bedwars.core.upgrade.TeamUpgrade
import com.hytale.bedwars.platform.gui.ShopMenu
import com.hytale.bedwars.platform.gui.UpgradeMenu
import com.hytale.bedwars.platform.listener.HytaleEventBus
import com.hytale.bedwars.platform.listener.HytaleEventGateway
import com.hytale.bedwars.platform.mapio.MapInstanceManager
import com.hytale.bedwars.platform.mapio.MapYmlLoader
import com.hytale.bedwars.platform.match.MatchRuntime
import com.hytale.bedwars.platform.util.ExecutorTaskScheduler
import com.hytale.bedwars.platform.world.WorldAdapter
import com.hytale.bedwars.storage.repository.PlayerStatsRepository
import java.util.UUID

class MatchOrchestrator(
    private val platformBridge: PlatformBridge,
    private val mapLoader: MapYmlLoader,
    private val mapInstanceManager: MapInstanceManager,
    private val matchConfig: MatchConfig,
    private val worldAdapter: WorldAdapter,
    private val shopItems: List<ShopItem>,
    private val upgradeTemplates: List<TeamUpgrade>,
    private val playerStatsRepository: PlayerStatsRepository,
    private val eventBus: HytaleEventBus,
    private val matchService: MatchService = MatchService(),
    private val statsService: StatsService = StatsService(),
) {
    private val scheduler = ExecutorTaskScheduler()
    private val runtimes = mutableMapOf<String, MatchRuntime>()
    private val templates = mutableMapOf<String, com.hytale.bedwars.core.map.MapTemplate>()

    fun createAndStart(
        matchId: String,
        mapId: String,
        mode: MatchMode,
        players: List<UUID>,
    ): Match {
        val map = mapLoader.load(mapId)
        templates[matchId] = map
        val teamTemplates = map.teams.map { it.color to it.name }
        val match = matchService.createMatch(matchId, mode, mapId, players, teamTemplates)
        transition(match, MatchState.LOBBY)
        startCountdown(match)
        return match
    }

    private fun transition(
        match: Match,
        state: MatchState,
    ) {
        matchService.transition(match, state)
        platformBridge.sendMessage(
            match.players.keys.firstOrNull() ?: return,
            "[${match.matchId}] state -> $state",
        )
    }

    private fun startCountdown(match: Match) {
        transition(match, MatchState.STARTING)
        val countdownTask =
            scheduler.schedule(matchConfig.startingCountdownSeconds * 1000L) {
                loadMap(match)
            }
        match.scheduledTasks.register(countdownTask)
        val rollbackTask =
            scheduler.scheduleAtFixedRate(0, 1000) {
                if (match.state != MatchState.STARTING) {
                    return@scheduleAtFixedRate
                }
                if (match.players.size < matchConfig.minPlayers) {
                    match.scheduledTasks.cancelAll()
                    transition(match, MatchState.LOBBY)
                    platformBridge.sendMessage(
                        match.players.keys.firstOrNull() ?: return@scheduleAtFixedRate,
                        "[${match.matchId}] Starting cancelled, not enough players",
                    )
                }
            }
        match.scheduledTasks.register(rollbackTask)
    }

    private fun loadMap(match: Match) {
        transition(match, MatchState.LOADING_MAP)
        val instanceId = mapInstanceManager.cloneTemplate(match.mapTemplateId, match.matchId)
        match.mapInstanceId = instanceId
        val task =
            scheduler.schedule(1000) {
                teleportPlayers(match)
            }
        match.scheduledTasks.register(task)
    }

    private fun teleportPlayers(match: Match) {
        transition(match, MatchState.TELEPORTING)
        val task =
            scheduler.schedule(1000) {
                beginMatch(match)
            }
        match.scheduledTasks.register(task)
    }

    private fun beginMatch(match: Match) {
        transition(match, MatchState.INGAME)
        val template = templates[match.matchId] ?: return
        val upgradesByTeam =
            match.teams.associate { team ->
                team.id to upgradeTemplates.map { it.copy() }
            }
        val runtime =
            MatchRuntime(
                platformBridge = platformBridge,
                match = match,
                map = template,
                worldAdapter = worldAdapter,
                shopMenu = ShopMenu(),
                upgradeMenu = UpgradeMenu(),
                shopItems = shopItems,
                teamUpgrades = upgradesByTeam,
                matchConfig = matchConfig,
                taskScheduler = scheduler,
                killAttributionService = com.hytale.bedwars.core.combat.KillAttributionService(matchConfig.assistWindowSeconds),
            )
        runtime.teleportTeamsToSpawns()
        runtime.start(platformBridge.currentTimeMillis())
        runtimes[match.matchId] = runtime
        HytaleEventGateway(runtime, eventBus).registerAll()
    }

    fun endMatch(match: Match) {
        transition(match, MatchState.ENDING)
        val task =
            scheduler.schedule(3000) {
                flushStats(match)
                transition(match, MatchState.DESTROYING)
                match.mapInstanceId?.let { mapInstanceManager.destroyInstance(it) }
                match.scheduledTasks.cancelAll()
            }
        match.scheduledTasks.register(task)
    }

    private fun flushStats(match: Match) {
        match.players.forEach { (playerId, session) ->
            val total = playerStatsRepository.load(playerId)
            statsService.merge(session.sessionStats, total)
            playerStatsRepository.save(playerId, total)
        }
    }
}
