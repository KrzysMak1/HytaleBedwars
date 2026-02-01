package com.hytale.bedwars.core.api

import com.hytale.bedwars.core.map.Location
import java.util.UUID

interface WorldBridge {
    fun teleport(
        playerId: UUID,
        location: Location,
    )

    fun dropItem(
        location: Location,
        itemId: String,
        amount: Int,
        mergeRadius: Int,
    )

    fun setSpectator(
        playerId: UUID,
        enabled: Boolean,
    )
}
