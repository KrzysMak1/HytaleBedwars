package com.hytale.bedwars.storage.repository

import com.hytale.bedwars.core.stats.PlayerStats
import java.util.UUID

interface PlayerStatsRepository {
    fun load(playerId: UUID): PlayerStats

    fun save(
        playerId: UUID,
        stats: PlayerStats,
    )
}
