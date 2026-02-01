package com.hytale.bedwars.platform.command

import com.hytale.bedwars.core.match.MatchMode
import com.hytale.bedwars.platform.plugin.MatchOrchestrator
import java.util.UUID

class BedWarsCommandHandler(private val orchestrator: MatchOrchestrator) {
    fun handle(
        command: String,
        args: List<String>,
        senderId: UUID,
    ) {
        if (command != "/bw") {
            return
        }
        if (args.size >= 4 && args[0] == "admin" && args[1] == "start") {
            val mapId = args[2]
            val mode = MatchMode.valueOf(args[3])
            orchestrator.createAndStart("admin-${System.currentTimeMillis()}", mapId, mode, listOf(senderId))
            return
        }
        if (args.size >= 2 && args[0] == "admin" && args[1] == "stop") {
            return
        }
        if (args.size >= 2 && args[0] == "admin" && args[1] == "reload") {
            return
        }
        if (args.size >= 3 && args[0] == "admin" && args[1] == "dumpstate") {
            return
        }
    }
}
