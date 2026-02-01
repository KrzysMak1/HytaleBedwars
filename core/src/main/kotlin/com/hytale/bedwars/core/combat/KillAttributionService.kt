package com.hytale.bedwars.core.combat

import com.hytale.bedwars.core.player.PlayerSession
import java.util.UUID

class KillAttributionService(private val assistWindowSeconds: Long) {
    fun resolveKiller(
        victim: PlayerSession,
        nowMillis: Long,
    ): UUID? {
        val lastAttacker = victim.lastAttackerId ?: return null
        val delta = nowMillis - victim.lastDamageTimeMillis
        return if (delta <= assistWindowSeconds * 1000) lastAttacker else null
    }
}
