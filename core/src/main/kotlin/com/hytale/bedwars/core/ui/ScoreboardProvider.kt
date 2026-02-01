package com.hytale.bedwars.core.ui

import com.hytale.bedwars.core.bed.BedState
import com.hytale.bedwars.core.map.MapTemplate
import com.hytale.bedwars.core.match.Match
import com.hytale.bedwars.core.team.TeamState

class ScoreboardProvider {
    fun build(
        match: Match,
        map: MapTemplate,
        nowMillis: Long,
    ): ScoreboardModel {
        val elapsedSeconds = ((nowMillis - match.startTimeMillis) / 1000).toInt().coerceAtLeast(0)
        val teamStatuses =
            match.teams.map { team ->
                val indicator =
                    when {
                        team.state == TeamState.ELIMINATED -> "✖"
                        team.bedState == BedState.DESTROYED -> "◯"
                        else -> "✔"
                    }
                ScoreboardTeamStatus(teamName = team.name, indicator = indicator)
            }
        return ScoreboardModel(
            title = "BedWars - ${map.name}",
            matchTimeSeconds = elapsedSeconds,
            phases = emptyList(),
            teamStatuses = teamStatuses,
            footerLines = listOf("Map: ${map.name}"),
        )
    }
}
