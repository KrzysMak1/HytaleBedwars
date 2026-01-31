package com.hytale.bedwars.core.match

import com.hytale.bedwars.core.block.PlacedBlocksTracker
import com.hytale.bedwars.core.generator.Generator
import com.hytale.bedwars.core.player.PlayerSession
import com.hytale.bedwars.core.team.Team
import com.hytale.bedwars.core.team.TeamState
import com.hytale.bedwars.core.util.ScheduledTaskRegistry
import java.util.UUID

class Match(
    val matchId: String,
    val mode: MatchMode,
    val mapTemplateId: String,
    var mapInstanceId: String?,
    var state: MatchState,
    val teams: MutableList<Team>,
    val players: MutableMap<UUID, PlayerSession>,
    val placedBlocksTracker: PlacedBlocksTracker,
    val generators: MutableList<Generator>,
    val scheduledTasks: ScheduledTaskRegistry = ScheduledTaskRegistry(),
) {
    fun activeTeams(): List<Team> = teams.filter { it.state == TeamState.ACTIVE }

    fun determineWinner(): Team? {
        val aliveTeams = activeTeams().filter { team -> team.alivePlayers(players).isNotEmpty() }
        return if (aliveTeams.size == 1) aliveTeams.first() else null
    }
}
