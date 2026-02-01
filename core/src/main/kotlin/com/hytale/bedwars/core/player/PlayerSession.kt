package com.hytale.bedwars.core.player

import com.hytale.bedwars.core.economy.CurrencyInventory
import com.hytale.bedwars.core.map.Location
import com.hytale.bedwars.core.stats.SessionStats
import com.hytale.bedwars.core.util.CancelableTask
import java.util.UUID

class PlayerSession(
    val playerId: UUID,
    var teamId: String? = null,
    var state: PlayerState = PlayerState.ALIVE,
    var lastAttackerId: UUID? = null,
    var lastDamageTimeMillis: Long = 0,
    var pendingRespawn: Boolean = false,
    var respawnProtectionUntilMillis: Long = 0,
    var respawnTask: CancelableTask? = null,
    var lastBaseRegionTeamId: String? = null,
    var lastKnownLocation: Location? = null,
    val sessionStats: SessionStats = SessionStats(),
    val currencyInventory: CurrencyInventory = CurrencyInventory(emptyList()),
) {
    fun markDamage(
        attackerId: UUID?,
        now: Long,
    ) {
        lastAttackerId = attackerId
        lastDamageTimeMillis = now
    }

    fun isSpawnProtected(nowMillis: Long): Boolean {
        return nowMillis < respawnProtectionUntilMillis
    }
}
