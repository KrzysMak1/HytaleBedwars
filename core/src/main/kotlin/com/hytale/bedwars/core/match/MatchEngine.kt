package com.hytale.bedwars.core.match

import com.hytale.bedwars.core.api.ItemGrantor
import com.hytale.bedwars.core.api.PlatformBridge
import com.hytale.bedwars.core.api.WorldBridge
import com.hytale.bedwars.core.bed.BedService
import com.hytale.bedwars.core.block.BlockPosition
import com.hytale.bedwars.core.block.BlockRateLimiter
import com.hytale.bedwars.core.block.BlockRules
import com.hytale.bedwars.core.block.RegionBounds
import com.hytale.bedwars.core.combat.KillAttributionService
import com.hytale.bedwars.core.economy.EconomyService
import com.hytale.bedwars.core.generator.GeneratorEngine
import com.hytale.bedwars.core.map.Location
import com.hytale.bedwars.core.map.MapTemplate
import com.hytale.bedwars.core.player.PlayerSession
import com.hytale.bedwars.core.player.PlayerState
import com.hytale.bedwars.core.player.RespawnService
import com.hytale.bedwars.core.player.SpectatorController
import com.hytale.bedwars.core.shop.ShopItem
import com.hytale.bedwars.core.shop.ShopTransactionService
import com.hytale.bedwars.core.team.Team
import com.hytale.bedwars.core.trap.TrapService
import com.hytale.bedwars.core.util.TaskScheduler
import java.util.UUID

class MatchEngine(
    private val match: Match,
    private val map: MapTemplate,
    private val config: MatchConfig,
    private val platformBridge: PlatformBridge,
    private val worldBridge: WorldBridge,
    private val taskScheduler: TaskScheduler,
    private val itemGrantor: ItemGrantor,
    private val economyService: EconomyService = EconomyService(),
    private val bedService: BedService = BedService(),
    private val respawnService: RespawnService = RespawnService(),
    private val killAttributionService: KillAttributionService,
    private val trapService: TrapService = TrapService(),
    private val spectatorController: SpectatorController = SpectatorController(),
    private val blockRateLimiter: BlockRateLimiter = BlockRateLimiter(config.blockPlacePerSecondLimit),
    private val generatorEngine: GeneratorEngine = GeneratorEngine(),
) {
    private val rules = BlockRules(match.placedBlocksTracker, config.breakableMapBlocks)
    private val shopService = ShopTransactionService(economyService)

    fun startMatch(nowMillis: Long) {
        match.startTimeMillis = nowMillis
        generatorEngine.start(match, map, config, taskScheduler, worldBridge)
    }

    fun stopMatch() {
        match.scheduledTasks.cancelAll()
        generatorEngine.stop()
    }

    fun handleMove(
        playerId: UUID,
        location: Location,
    ) {
        val session = match.players[playerId] ?: return
        session.lastKnownLocation = location
        if (location.y <= map.voidY) {
            handleDeath(session, platformBridge.currentTimeMillis())
            return
        }
        handleTrapRegions(session, location)
    }

    fun handleBlockPlace(
        playerId: UUID,
        position: BlockPosition,
    ): Boolean {
        val now = platformBridge.currentTimeMillis()
        if (!blockRateLimiter.allow(playerId, now)) {
            return false
        }
        val protectedRegions = map.teams.mapNotNull { it.bedRegion } + map.teams.map { it.baseRegion }
        val allowed = rules.canPlace(position, map.buildRegion, protectedRegions)
        if (allowed) {
            match.placedBlocksTracker.track(position)
        }
        return allowed
    }

    fun handleBlockBreak(
        playerId: UUID,
        position: BlockPosition,
    ): Boolean {
        val now = platformBridge.currentTimeMillis()
        if (!blockRateLimiter.allow(playerId, now)) {
            return false
        }
        val bedTeam = map.teams.firstOrNull { team -> team.bedRegion?.let { RegionBounds.contains(it, position) } == true }
        if (bedTeam != null) {
            return handleBedBreak(playerId, bedTeam.color)
        }
        val allowed = rules.canBreak(position)
        if (allowed) {
            match.placedBlocksTracker.untrack(position)
        }
        return allowed
    }

    fun handleDamage(
        victim: PlayerSession,
        attackerId: UUID?,
        nowMillis: Long,
    ) {
        if (spectatorController.shouldCancelDamage(victim) || victim.isSpawnProtected(nowMillis)) {
            return
        }
        victim.markDamage(attackerId, nowMillis)
    }

    fun handleDeath(
        victim: PlayerSession,
        nowMillis: Long,
    ) {
        if (victim.state == PlayerState.SPECTATOR) {
            return
        }
        val team = match.teams.firstOrNull { it.id == victim.teamId } ?: return
        val killer = killAttributionService.resolveKiller(victim, nowMillis)
        victim.state = PlayerState.DEAD
        victim.sessionStats.stats.deaths += 1
        dropCurrencyOnDeath(victim)
        if (respawnService.canRespawn(team.bedState)) {
            victim.state = PlayerState.RESPAWNING
            respawnService.scheduleRespawn(victim, team, config, taskScheduler, platformBridge::currentTimeMillis) { session, respawnTeam ->
                respawnPlayer(session, respawnTeam)
            }
            platformBridge.sendMessage(victim.playerId, "Respawning in ${config.respawnDelaySeconds}s")
        } else {
            victim.state = PlayerState.SPECTATOR
            worldBridge.setSpectator(victim.playerId, true)
            platformBridge.sendMessage(victim.playerId, "Final death")
        }
        if (killer != null) {
            val killerSession = match.players[killer]
            if (killerSession != null) {
                killerSession.sessionStats.stats.kills += 1
                if (team.bedState == com.hytale.bedwars.core.bed.BedState.DESTROYED) {
                    killerSession.sessionStats.stats.finalKills += 1
                }
            }
            platformBridge.sendMessage(killer, "You killed ${victim.playerId}")
        }
    }

    fun handleBedBreak(
        breakerId: UUID,
        teamColor: String,
    ): Boolean {
        val breaker = match.players[breakerId] ?: return false
        val targetTeam = match.teams.firstOrNull { it.color == teamColor } ?: return false
        if (breaker.teamId == targetTeam.id && config.bedProtectionEnabled) {
            return false
        }
        if (!bedService.destroyBed(targetTeam, match.players.values)) {
            return false
        }
        breaker.sessionStats.stats.bedsBroken += 1
        match.players.values.forEach { player ->
            platformBridge.sendMessage(player.playerId, "Bed destroyed for ${targetTeam.name}")
        }
        return true
    }

    fun openShop(
        playerId: UUID,
        item: ShopItem,
    ): Boolean {
        val session = match.players[playerId] ?: return false
        val location = session.lastKnownLocation ?: map.teams.firstOrNull()?.spawn ?: Location(0.0, 0.0, 0.0)
        return shopService.purchase(playerId, session.currencyInventory, item, itemGrantor, location)
    }

    fun canInteract(playerId: UUID): Boolean {
        val session = match.players[playerId] ?: return false
        return !spectatorController.shouldCancelInteract(session)
    }

    fun canPickup(playerId: UUID): Boolean {
        val session = match.players[playerId] ?: return false
        return !spectatorController.shouldCancelPickup(session)
    }

    fun canCollide(playerId: UUID): Boolean {
        val session = match.players[playerId] ?: return false
        return !spectatorController.shouldCancelCollision(session)
    }

    private fun respawnPlayer(
        session: PlayerSession,
        team: Team,
    ) {
        val spawn = map.teams.firstOrNull { it.color == team.color }?.spawn ?: return
        worldBridge.teleport(session.playerId, spawn)
        worldBridge.setSpectator(session.playerId, false)
        platformBridge.playSound(session.playerId, "respawn")
    }

    private fun handleTrapRegions(
        session: PlayerSession,
        location: Location,
    ) {
        val playerTeamId = session.teamId ?: return
        map.teams.forEach { teamTemplate ->
            val team = match.teams.firstOrNull { it.color == teamTemplate.color } ?: return@forEach
            if (team.id == playerTeamId) {
                return@forEach
            }
            val inside = RegionBounds.contains(teamTemplate.baseRegion, location.toBlockPosition())
            val last = session.lastBaseRegionTeamId
            if (inside && last != team.id) {
                session.lastBaseRegionTeamId = team.id
                val effect = trapService.trigger(team.trapQueue, session.playerId.toString()) ?: return@forEach
                platformBridge.showTitle(session.playerId, effect.alarmMessage, effect.debuff)
                team.playerIds.forEach { teammate ->
                    platformBridge.sendMessage(teammate, "Trap triggered by ${session.playerId}")
                }
            } else if (!inside && last == team.id) {
                session.lastBaseRegionTeamId = null
            }
        }
    }

    private fun dropCurrencyOnDeath(session: PlayerSession) {
        val location = session.lastKnownLocation ?: return
        val drops = session.currencyInventory.drain()
        drops.forEach { stack ->
            if (config.currencyDropOnDeath[stack.currency] == true && stack.amount > 0) {
                worldBridge.dropItem(location, stack.currency.name.lowercase(), stack.amount, config.mergeRadius)
            }
        }
    }
}

private fun Location.toBlockPosition(): BlockPosition {
    return BlockPosition(x.toInt(), y.toInt(), z.toInt())
}
