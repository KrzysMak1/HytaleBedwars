package com.hytale.bedwars.core.party

import java.util.UUID

class Party(val leaderId: UUID) {
    private val members = mutableSetOf(leaderId)
    private val invites = mutableSetOf<UUID>()

    fun members(): Set<UUID> = members.toSet()

    fun invite(playerId: UUID) {
        invites.add(playerId)
    }

    fun accept(playerId: UUID): Boolean {
        if (!invites.remove(playerId)) {
            return false
        }
        members.add(playerId)
        return true
    }

    fun leave(playerId: UUID): Boolean {
        if (playerId == leaderId) {
            return false
        }
        return members.remove(playerId)
    }

    fun kick(playerId: UUID): Boolean {
        if (playerId == leaderId) {
            return false
        }
        return members.remove(playerId)
    }

    fun disband(): Set<UUID> {
        val copy = members.toSet()
        members.clear()
        invites.clear()
        return copy
    }
}
