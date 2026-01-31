package com.hytale.bedwars.platform.world

import com.hytale.bedwars.core.map.Location
import java.util.UUID

interface WorldAdapter {
    fun teleport(playerId: UUID, location: Location)
    fun dropItem(location: Location, itemId: String, amount: Int)
    fun playSound(playerId: UUID, sound: String)
    fun setSpectator(playerId: UUID, enabled: Boolean)
}
