package com.hytale.bedwars.core.api

import java.util.UUID

interface PlatformBridge {
    fun currentTimeMillis(): Long
    fun sendMessage(playerId: UUID, message: String)
    fun showTitle(playerId: UUID, title: String, subtitle: String)
    fun playSound(playerId: UUID, sound: String)
    fun showScoreboard(playerId: UUID, lines: List<String>)
}
