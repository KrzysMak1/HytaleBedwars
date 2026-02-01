package com.hytale.bedwars.platform.world

import com.hytale.bedwars.core.api.PlatformBridge
import com.hytale.bedwars.core.map.Location
import java.util.UUID

class HytaleWorldAdapter(private val platformBridge: PlatformBridge) : WorldAdapter {
    override fun teleport(
        playerId: UUID,
        location: Location,
    ) {
        platformBridge.sendMessage(playerId, "Teleport to ${location.x},${location.y},${location.z}")
    }

    override fun dropItem(
        location: Location,
        itemId: String,
        amount: Int,
        mergeRadius: Int,
    ) {
        println(
            "[WorldAdapter] Drop $itemId x$amount at ${location.x},${location.y},${location.z} merge=$mergeRadius",
        )
    }

    override fun setSpectator(
        playerId: UUID,
        enabled: Boolean,
    ) {
        platformBridge.sendMessage(playerId, "Spectator=$enabled")
    }
}
