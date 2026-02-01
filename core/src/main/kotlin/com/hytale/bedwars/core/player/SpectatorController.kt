package com.hytale.bedwars.core.player

class SpectatorController {
    fun isSpectator(session: PlayerSession): Boolean {
        return session.state == PlayerState.SPECTATOR
    }

    fun shouldCancelDamage(session: PlayerSession): Boolean = isSpectator(session)

    fun shouldCancelPickup(session: PlayerSession): Boolean = isSpectator(session)

    fun shouldCancelInteract(session: PlayerSession): Boolean = isSpectator(session)

    fun shouldCancelCollision(session: PlayerSession): Boolean = isSpectator(session)
}
