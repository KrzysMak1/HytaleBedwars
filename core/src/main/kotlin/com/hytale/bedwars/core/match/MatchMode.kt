package com.hytale.bedwars.core.match

enum class MatchMode(val teamsCount: Int, val teamSize: Int) {
    SOLO(8, 1),
    DOUBLES(8, 2),
    FOURS(4, 4),
    CUSTOM(0, 0),
}
