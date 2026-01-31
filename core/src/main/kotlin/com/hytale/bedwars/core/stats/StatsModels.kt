package com.hytale.bedwars.core.stats

data class PlayerStats(
    var wins: Int = 0,
    var losses: Int = 0,
    var kills: Int = 0,
    var deaths: Int = 0,
    var bedsBroken: Int = 0,
    var finalKills: Int = 0,
    var gamesPlayed: Int = 0,
    var winstreak: Int = 0,
)

class SessionStats {
    val stats = PlayerStats()
}
