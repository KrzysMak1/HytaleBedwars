package com.hytale.bedwars.core.team

import com.hytale.bedwars.core.bed.BedState
import com.hytale.bedwars.core.player.PlayerSession
import com.hytale.bedwars.core.player.PlayerState
import com.hytale.bedwars.core.trap.TrapQueue
import java.util.UUID

class Team(
    val id: String,
    val color: String,
    val name: String,
    val playerIds: MutableSet<UUID>,
    var state: TeamState = TeamState.ACTIVE,
    var bedState: BedState = BedState.ALIVE,
    val trapQueue: TrapQueue = TrapQueue(maxTraps = 3),
) {
    fun alivePlayers(sessions: Map<UUID, PlayerSession> = emptyMap()): List<PlayerSession> {
        return playerIds.mapNotNull { sessions[it] }
            .filter { it.state == PlayerState.ALIVE || it.state == PlayerState.RESPAWNING }
    }
}
