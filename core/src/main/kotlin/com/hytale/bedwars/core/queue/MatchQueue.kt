package com.hytale.bedwars.core.queue

import com.hytale.bedwars.core.match.MatchMode
import java.util.UUID

class MatchQueue(val mode: MatchMode) {
    private val queuedPlayers = ArrayDeque<UUID>()

    fun enqueue(playerId: UUID) {
        if (!queuedPlayers.contains(playerId)) {
            queuedPlayers.addLast(playerId)
        }
    }

    fun dequeue(playerId: UUID) {
        queuedPlayers.remove(playerId)
    }

    fun drain(count: Int): List<UUID> {
        val result = mutableListOf<UUID>()
        repeat(count) {
            val next = queuedPlayers.removeFirstOrNull() ?: return result
            result.add(next)
        }
        return result
    }

    fun size(): Int = queuedPlayers.size
}
