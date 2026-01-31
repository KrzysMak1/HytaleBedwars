package com.hytale.bedwars.core.bed

import com.hytale.bedwars.core.player.PlayerSession
import com.hytale.bedwars.core.player.PlayerState
import com.hytale.bedwars.core.team.Team

class BedService {
    fun destroyBed(team: Team, players: Collection<PlayerSession>): Boolean {
        if (team.bedState == BedState.DESTROYED) {
            return false
        }
        team.bedState = BedState.DESTROYED
        players.filter { it.teamId == team.id && it.state == PlayerState.RESPAWNING }
            .forEach { it.pendingRespawn = false }
        return true
    }
}
