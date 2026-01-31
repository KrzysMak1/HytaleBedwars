package com.hytale.bedwars.platform.listener

import com.hytale.bedwars.core.block.BlockPosition
import com.hytale.bedwars.core.player.PlayerSession
import com.hytale.bedwars.core.team.Team
import com.hytale.bedwars.platform.match.MatchRuntime
import java.util.UUID

class HytaleEventGateway(private val runtime: MatchRuntime) {
    fun registerAll() {
        // Replace with Hytale SDK event registration.
    }

    fun onBlockPlace(playerId: UUID, position: BlockPosition): Boolean {
        return runtime.handleBlockPlace(playerId, position)
    }

    fun onBlockBreak(playerId: UUID, position: BlockPosition): Boolean {
        return runtime.handleBlockBreak(playerId, position)
    }

    fun onDamage(victim: PlayerSession, attackerId: UUID?, nowMillis: Long) {
        runtime.handleDamage(victim, attackerId, nowMillis)
    }

    fun onDeath(victim: PlayerSession, nowMillis: Long) {
        runtime.handleDeath(victim, nowMillis)
    }

    fun onShopInteract(playerId: UUID) {
        runtime.openShop(playerId)
    }

    fun onUpgradeInteract(playerId: UUID, team: Team) {
        runtime.openUpgrades(playerId, team)
    }
}
