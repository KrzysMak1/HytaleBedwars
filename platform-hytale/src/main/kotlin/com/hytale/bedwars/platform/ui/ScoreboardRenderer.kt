package com.hytale.bedwars.platform.ui

import com.hytale.bedwars.core.api.PlatformBridge
import com.hytale.bedwars.core.ui.ScoreboardModel
import java.util.UUID
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class ScoreboardRenderer(private val platformBridge: PlatformBridge) {
    private val scheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()

    fun start(
        playerId: UUID,
        provider: () -> ScoreboardModel,
    ) {
        scheduler.scheduleAtFixedRate({
            val model = provider()
            val lines = mutableListOf<String>()
            lines.add(model.title)
            lines.add("Time: ${model.matchTimeSeconds}s")
            model.phases.forEach { phase ->
                lines.add("${phase.label}: ${phase.secondsUntilNext}s")
            }
            model.teamStatuses.forEach { status ->
                lines.add("${status.indicator} ${status.teamName}")
            }
            lines.addAll(model.footerLines)
            platformBridge.showScoreboard(playerId, lines)
        }, 0, 1, TimeUnit.SECONDS)
    }

    fun stop() {
        scheduler.shutdown()
    }
}
