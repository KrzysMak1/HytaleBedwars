package com.hytale.bedwars.core.stats

class StatsService {
    fun merge(
        session: SessionStats,
        total: PlayerStats,
    ) {
        total.wins += session.stats.wins
        total.losses += session.stats.losses
        total.kills += session.stats.kills
        total.deaths += session.stats.deaths
        total.bedsBroken += session.stats.bedsBroken
        total.finalKills += session.stats.finalKills
        total.gamesPlayed += session.stats.gamesPlayed
        total.winstreak = session.stats.winstreak
    }
}
