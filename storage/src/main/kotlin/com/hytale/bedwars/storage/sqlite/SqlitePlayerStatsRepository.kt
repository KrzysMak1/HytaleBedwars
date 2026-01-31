package com.hytale.bedwars.storage.sqlite

import com.hytale.bedwars.storage.repository.CachedPlayerStatsRepository
import com.hytale.bedwars.storage.repository.JdbcPlayerStatsRepository
import com.hytale.bedwars.storage.repository.PlayerStatsRepository

class SqlitePlayerStatsRepository(dbPath: String) : PlayerStatsRepository {
    private val cached = CachedPlayerStatsRepository(JdbcPlayerStatsRepository("jdbc:sqlite:$dbPath"))

    override fun load(playerId: java.util.UUID) = cached.load(playerId)

    override fun save(playerId: java.util.UUID, stats: com.hytale.bedwars.core.stats.PlayerStats) {
        cached.save(playerId, stats)
    }

    fun shutdown() {
        cached.shutdown()
    }
}
