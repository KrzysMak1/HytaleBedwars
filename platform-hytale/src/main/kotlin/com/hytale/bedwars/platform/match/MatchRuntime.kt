package com.hytale.bedwars.platform.match

import com.hytale.bedwars.core.api.PlatformBridge
import com.hytale.bedwars.core.bed.BedService
import com.hytale.bedwars.core.block.BlockPosition
import com.hytale.bedwars.core.block.BlockRules
import com.hytale.bedwars.core.block.PlacedBlocksTracker
import com.hytale.bedwars.core.combat.KillAttributionService
import com.hytale.bedwars.core.map.MapTemplate
import com.hytale.bedwars.core.match.Match
import com.hytale.bedwars.core.player.PlayerSession
import com.hytale.bedwars.core.player.PlayerState
import com.hytale.bedwars.core.player.RespawnService
import com.hytale.bedwars.core.shop.ShopItem
import com.hytale.bedwars.core.team.Team
import com.hytale.bedwars.core.trap.TrapService
import com.hytale.bedwars.core.upgrade.TeamUpgrade
import com.hytale.bedwars.platform.gui.ShopMenu
import com.hytale.bedwars.platform.gui.UpgradeMenu
import com.hytale.bedwars.platform.world.WorldAdapter
import java.util.UUID

class MatchRuntime(
    private val platformBridge: PlatformBridge,
    private val match: Match,
    private val map: MapTemplate,
    private val tracker: PlacedBlocksTracker,
    private val worldAdapter: WorldAdapter,
    private val shopMenu: ShopMenu,
    private val upgradeMenu: UpgradeMenu,
    private val shopItems: List<ShopItem>,
    private val teamUpgrades: Map<String, List<TeamUpgrade>>,
    private val bedService: BedService = BedService(),
    private val respawnService: RespawnService = RespawnService(),
    private val killAttributionService: KillAttributionService,
    private val trapService: TrapService = TrapService(),
) {
    private val rules = BlockRules(tracker, breakableMapBlocks = emptySet())

    fun handleBlockPlace(playerId: UUID, position: BlockPosition): Boolean {
        val protectedRegions = map.teams.mapNotNull { it.bedRegion }
        val allowed = rules.canPlace(position, map.buildRegion, protectedRegions)
        if (allowed) {
            tracker.track(position)
        }
        return allowed
    }

    fun handleBlockBreak(playerId: UUID, position: BlockPosition): Boolean {
        val allowed = rules.canBreak(position)
        if (allowed) {
            tracker.untrack(position)
        }
        return allowed
    }

    fun handleBedBreak(breakerId: UUID, team: Team) {
        if (bedService.destroyBed(team, match.players.values)) {
            platformBridge.sendMessage(breakerId, "[${match.matchId}] Bed destroyed for ${team.name}")
        }
    }

    fun teleportTeamsToSpawns() {
        map.teams.forEach { template ->
            val team = match.teams.firstOrNull { it.color == template.color } ?: return@forEach
            team.playerIds.forEach { playerId ->
                worldAdapter.teleport(playerId, template.spawn)
            }
        }
    }

    fun handleDamage(victim: PlayerSession, attackerId: UUID?, nowMillis: Long) {
        victim.markDamage(attackerId, nowMillis)
    }

    fun handleDeath(victim: PlayerSession, nowMillis: Long) {
        val killer = killAttributionService.resolveKiller(victim, nowMillis)
        val team = match.teams.firstOrNull { it.id == victim.teamId }
        if (team == null) {
            return
        }
        if (respawnService.canRespawn(team.bedState)) {
            victim.state = PlayerState.RESPAWNING
            victim.pendingRespawn = true
        } else {
            victim.state = PlayerState.SPECTATOR
            worldAdapter.setSpectator(victim.playerId, true)
            platformBridge.sendMessage(victim.playerId, "[${match.matchId}] Final death")
        }
        if (killer != null) {
            platformBridge.sendMessage(killer, "[${match.matchId}] You killed ${victim.playerId}")
        }
    }

    fun handleTrapTrigger(intruderId: UUID, team: Team) {
        val effect = trapService.trigger(team.trapQueue, intruderId.toString()) ?: return
        platformBridge.showTitle(intruderId, effect.alarmMessage, effect.debuff)
    }

    fun openShop(playerId: UUID) {
        shopMenu.open(playerId, shopItems)
    }

    fun openUpgrades(playerId: UUID, team: Team) {
        val upgrades = teamUpgrades[team.id].orEmpty()
        upgradeMenu.open(playerId, upgrades)
    }
}
