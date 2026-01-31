package com.hytale.bedwars.platform.adapter

import com.hytale.bedwars.core.api.PlatformBridge
import java.util.UUID

class HytalePlatformBridge : PlatformBridge {
    override fun currentTimeMillis(): Long = System.currentTimeMillis()

    override fun sendMessage(playerId: UUID, message: String) {
        println("[HytaleBridge] Message to $playerId: ${stripColor(message)}")
    }

    override fun showTitle(playerId: UUID, title: String, subtitle: String) {
        println("[HytaleBridge] Title to $playerId: ${stripColor(title)} | ${stripColor(subtitle)}")
    }

    override fun playSound(playerId: UUID, sound: String) {
        println("[HytaleBridge] Sound to $playerId: $sound")
    }

    override fun showScoreboard(playerId: UUID, lines: List<String>) {
        val cleaned = lines.joinToString(" | ") { stripColor(it) }
        println("[HytaleBridge] Scoreboard to $playerId: $cleaned")
    }

    private fun stripColor(message: String): String {
        return message.replace(Regex("&[0-9a-fk-or]"), "").replace(Regex("<#([A-Fa-f0-9]{6})>"), "")
    }
}
