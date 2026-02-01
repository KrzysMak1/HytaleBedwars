package com.hytale.bedwars.core.block

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class BlockRateLimiter(private val limitPerSecond: Int) {
    private data class Counter(var windowStart: Long, var count: Int)

    private val counters = ConcurrentHashMap<UUID, Counter>()

    fun allow(
        playerId: UUID,
        nowMillis: Long,
    ): Boolean {
        if (limitPerSecond <= 0) {
            return true
        }
        val counter = counters.computeIfAbsent(playerId) { Counter(nowMillis, 0) }
        val elapsed = nowMillis - counter.windowStart
        if (elapsed >= 1000) {
            counter.windowStart = nowMillis
            counter.count = 0
        }
        counter.count += 1
        return counter.count <= limitPerSecond
    }
}
