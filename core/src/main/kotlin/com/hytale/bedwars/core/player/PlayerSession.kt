package com.hytale.bedwars.core.player

import java.util.UUID

class PlayerSession(
    val playerId: UUID,
    var teamId: String? = null,
    var state: PlayerState = PlayerState.ALIVE,
    var lastAttackerId: UUID? = null,
    var lastDamageTimeMillis: Long = 0,
    var pendingRespawn: Boolean = false,
) {
    fun markDamage(attackerId: UUID?, now: Long) {
        lastAttackerId = attackerId
        lastDamageTimeMillis = now
    }
}
