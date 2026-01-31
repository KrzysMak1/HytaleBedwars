package com.hytale.bedwars.storage.mysql

import com.hytale.bedwars.storage.repository.CachedPlayerStatsRepository
import com.hytale.bedwars.storage.repository.JdbcPlayerStatsRepository
import com.hytale.bedwars.storage.repository.PlayerStatsRepository

class MySqlPlayerStatsRepository(
    host: String,
    database: String,
    user: String,
    password: String,
) : PlayerStatsRepository {
    private val cached = CachedPlayerStatsRepository(
        JdbcPlayerStatsRepository("jdbc:mysql://$host/$database", user, password),
    )

    override fun load(playerId: java.util.UUID) = cached.load(playerId)

    override fun save(playerId: java.util.UUID, stats: com.hytale.bedwars.core.stats.PlayerStats) {
        cached.save(playerId, stats)
    }

    fun shutdown() {
        cached.shutdown()
    }
}
