package com.hytale.bedwars.platform.listener

import com.hytale.bedwars.core.block.BlockPosition
import com.hytale.bedwars.core.map.Location
import com.hytale.bedwars.core.player.PlayerSession
import com.hytale.bedwars.core.team.Team
import com.hytale.bedwars.platform.match.MatchRuntime
import java.util.UUID

class HytaleEventGateway(private val runtime: MatchRuntime, private val eventBus: HytaleEventBus) {
    fun registerAll() {
        eventBus.onBlockPlace { playerId, position ->
            runtime.handleBlockPlace(playerId, position)
        }
        eventBus.onBlockBreak { playerId, position ->
            runtime.handleBlockBreak(playerId, position)
        }
        eventBus.onMove { playerId, location ->
            runtime.handleMove(playerId, location)
        }
        eventBus.onDamage { victim, attackerId, nowMillis ->
            runtime.handleDamage(victim, attackerId, nowMillis)
        }
        eventBus.onDeath { victim, nowMillis ->
            runtime.handleDeath(victim, nowMillis)
        }
        eventBus.onInteract { playerId ->
            if (runtime.canInteract(playerId)) {
                runtime.openShop(playerId)
            }
        }
        eventBus.onPickup { playerId ->
            runtime.canPickup(playerId)
        }
        eventBus.onCollision { playerId ->
            runtime.canCollide(playerId)
        }
    }

    fun onBlockPlace(
        playerId: UUID,
        position: BlockPosition,
    ): Boolean {
        return runtime.handleBlockPlace(playerId, position)
    }

    fun onBlockBreak(
        playerId: UUID,
        position: BlockPosition,
    ): Boolean {
        return runtime.handleBlockBreak(playerId, position)
    }

    fun onDamage(
        victim: PlayerSession,
        attackerId: UUID?,
        nowMillis: Long,
    ) {
        runtime.handleDamage(victim, attackerId, nowMillis)
    }

    fun onDeath(
        victim: PlayerSession,
        nowMillis: Long,
    ) {
        runtime.handleDeath(victim, nowMillis)
    }

    fun onShopInteract(playerId: UUID) {
        runtime.openShop(playerId)
    }

    fun onUpgradeInteract(
        playerId: UUID,
        team: Team,
    ) {
        runtime.openUpgrades(playerId, team)
    }

    fun onMove(
        playerId: UUID,
        location: Location,
    ) {
        runtime.handleMove(playerId, location)
    }
}
