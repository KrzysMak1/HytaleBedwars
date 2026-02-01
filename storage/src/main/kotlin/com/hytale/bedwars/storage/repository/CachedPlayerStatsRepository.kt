package com.hytale.bedwars.storage.repository

import com.hytale.bedwars.core.stats.PlayerStats
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class CachedPlayerStatsRepository(
    private val delegate: PlayerStatsRepository,
) : PlayerStatsRepository {
    private val cache = ConcurrentHashMap<UUID, PlayerStats>()
    private val pendingWrites = ConcurrentHashMap.newKeySet<UUID>()
    private val executor = Executors.newSingleThreadScheduledExecutor()

    init {
        executor.scheduleAtFixedRate({ flush() }, 5, 5, TimeUnit.SECONDS)
    }

    override fun load(playerId: UUID): PlayerStats {
        return cache.computeIfAbsent(playerId) { delegate.load(playerId) }
    }

    override fun save(
        playerId: UUID,
        stats: PlayerStats,
    ) {
        cache[playerId] = stats
        pendingWrites.add(playerId)
    }

    fun flush() {
        pendingWrites.forEach { playerId ->
            cache[playerId]?.let { delegate.save(playerId, it) }
        }
        pendingWrites.clear()
    }

    fun shutdown() {
        flush()
        executor.shutdown()
    }
}
