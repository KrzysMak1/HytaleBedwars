package com.hytale.bedwars.core.player

import com.hytale.bedwars.core.bed.BedState
import com.hytale.bedwars.core.match.MatchConfig
import com.hytale.bedwars.core.team.Team
import com.hytale.bedwars.core.util.TaskScheduler

class RespawnService {
    fun canRespawn(bedState: BedState): Boolean = bedState == BedState.ALIVE

    fun scheduleRespawn(
        session: PlayerSession,
        team: Team,
        config: MatchConfig,
        scheduler: TaskScheduler,
        nowProvider: () -> Long,
        onRespawn: (PlayerSession, Team) -> Unit,
    ) {
        session.pendingRespawn = true
        session.respawnTask?.cancel()
        session.respawnTask =
            scheduler.schedule(config.respawnDelaySeconds * 1000L) {
                session.pendingRespawn = false
                session.state = PlayerState.ALIVE
                session.respawnProtectionUntilMillis = nowProvider() + config.spawnProtectionSeconds * 1000L
                onRespawn(session, team)
            }
    }
}
