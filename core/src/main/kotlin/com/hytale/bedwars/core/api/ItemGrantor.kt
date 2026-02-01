package com.hytale.bedwars.core.api

import com.hytale.bedwars.core.map.Location
import java.util.UUID

interface ItemGrantor {
    fun hasSpace(
        playerId: UUID,
        itemId: String,
        amount: Int,
    ): Boolean

    fun giveItem(
        playerId: UUID,
        itemId: String,
        amount: Int,
    ): Boolean

    fun dropItem(
        playerId: UUID,
        itemId: String,
        amount: Int,
        location: Location,
    )
}
