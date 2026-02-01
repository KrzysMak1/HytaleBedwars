package com.hytale.bedwars.platform.listener

import com.hytale.bedwars.core.block.BlockPosition
import com.hytale.bedwars.core.map.Location
import com.hytale.bedwars.core.player.PlayerSession
import java.util.UUID

class HytaleEventBus {
    private val blockPlaceListeners = mutableListOf<(UUID, BlockPosition) -> Boolean>()
    private val blockBreakListeners = mutableListOf<(UUID, BlockPosition) -> Boolean>()
    private val moveListeners = mutableListOf<(UUID, Location) -> Unit>()
    private val damageListeners = mutableListOf<(PlayerSession, UUID?, Long) -> Unit>()
    private val deathListeners = mutableListOf<(PlayerSession, Long) -> Unit>()
    private val interactListeners = mutableListOf<(UUID) -> Unit>()
    private val pickupListeners = mutableListOf<(UUID) -> Boolean>()
    private val collisionListeners = mutableListOf<(UUID) -> Boolean>()

    fun onBlockPlace(listener: (UUID, BlockPosition) -> Boolean) {
        blockPlaceListeners.add(listener)
    }

    fun onBlockBreak(listener: (UUID, BlockPosition) -> Boolean) {
        blockBreakListeners.add(listener)
    }

    fun onMove(listener: (UUID, Location) -> Unit) {
        moveListeners.add(listener)
    }

    fun onDamage(listener: (PlayerSession, UUID?, Long) -> Unit) {
        damageListeners.add(listener)
    }

    fun onDeath(listener: (PlayerSession, Long) -> Unit) {
        deathListeners.add(listener)
    }

    fun onInteract(listener: (UUID) -> Unit) {
        interactListeners.add(listener)
    }

    fun onPickup(listener: (UUID) -> Boolean) {
        pickupListeners.add(listener)
    }

    fun onCollision(listener: (UUID) -> Boolean) {
        collisionListeners.add(listener)
    }

    fun emitBlockPlace(
        playerId: UUID,
        position: BlockPosition,
    ): Boolean {
        return blockPlaceListeners.all { it(playerId, position) }
    }

    fun emitBlockBreak(
        playerId: UUID,
        position: BlockPosition,
    ): Boolean {
        return blockBreakListeners.all { it(playerId, position) }
    }

    fun emitMove(
        playerId: UUID,
        location: Location,
    ) {
        moveListeners.forEach { it(playerId, location) }
    }

    fun emitDamage(
        victim: PlayerSession,
        attackerId: UUID?,
        nowMillis: Long,
    ) {
        damageListeners.forEach { it(victim, attackerId, nowMillis) }
    }

    fun emitDeath(
        victim: PlayerSession,
        nowMillis: Long,
    ) {
        deathListeners.forEach { it(victim, nowMillis) }
    }

    fun emitInteract(playerId: UUID) {
        interactListeners.forEach { it(playerId) }
    }

    fun emitPickup(playerId: UUID): Boolean {
        return pickupListeners.all { it(playerId) }
    }

    fun emitCollision(playerId: UUID): Boolean {
        return collisionListeners.all { it(playerId) }
    }
}
