package com.hytale.bedwars.core.match

import com.hytale.bedwars.core.block.PlacedBlocksTracker
import com.hytale.bedwars.core.generator.Generator
import com.hytale.bedwars.core.player.PlayerSession
import com.hytale.bedwars.core.player.PlayerState
import com.hytale.bedwars.core.team.Team
import com.hytale.bedwars.core.team.TeamState
import java.util.UUID

class MatchService(private val stateMachine: MatchStateMachine = MatchStateMachine()) {
    fun createMatch(
        matchId: String,
        mode: MatchMode,
        mapTemplateId: String,
        players: List<UUID>,
        teamTemplates: List<Pair<String, String>>,
    ): Match {
        val teams =
            teamTemplates.mapIndexed { index, template ->
                Team(
                    id = "$mapTemplateId-$index",
                    color = template.first,
                    name = template.second,
                    playerIds = mutableSetOf(),
                    state = TeamState.ACTIVE,
                )
            }.toMutableList()
        val sessions =
            players.associateWith { playerId ->
                PlayerSession(playerId = playerId, state = PlayerState.ALIVE)
            }.toMutableMap()

        assignTeams(teams, sessions)

        return Match(
            matchId = matchId,
            mode = mode,
            mapTemplateId = mapTemplateId,
            mapInstanceId = null,
            state = MatchState.LOBBY,
            teams = teams,
            players = sessions,
            placedBlocksTracker = PlacedBlocksTracker(),
            generators = mutableListOf(),
        )
    }

    fun transition(
        match: Match,
        next: MatchState,
    ) {
        require(stateMachine.canTransition(match.state, next)) {
            "Invalid transition ${match.state} -> $next"
        }
        match.state = next
    }

    fun assignTeams(
        teams: MutableList<Team>,
        sessions: MutableMap<UUID, PlayerSession>,
    ) {
        val players = sessions.values.toList()
        if (teams.isEmpty()) {
            return
        }
        players.forEachIndexed { index, session ->
            val team = teams[index % teams.size]
            team.playerIds.add(session.playerId)
            session.teamId = team.id
        }
    }

    fun markEliminatedIfNeeded(
        team: Team,
        sessions: Map<UUID, PlayerSession>,
    ) {
        if (team.alivePlayers(sessions).isEmpty()) {
            team.state = TeamState.ELIMINATED
        }
    }

    fun registerGenerators(
        match: Match,
        generators: List<Generator>,
    ) {
        match.generators.clear()
        match.generators.addAll(generators)
    }
}
