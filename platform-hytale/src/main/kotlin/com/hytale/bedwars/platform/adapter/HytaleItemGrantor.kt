package com.hytale.bedwars.platform.adapter

import com.hytale.bedwars.core.api.ItemGrantor
import com.hytale.bedwars.core.api.PlatformBridge
import com.hytale.bedwars.core.api.WorldBridge
import java.util.UUID

class HytaleItemGrantor(
    private val platformBridge: PlatformBridge,
    private val worldBridge: WorldBridge,
) : ItemGrantor {
    override fun hasSpace(
        playerId: UUID,
        itemId: String,
        amount: Int,
    ): Boolean {
        return true
    }

    override fun giveItem(
        playerId: UUID,
        itemId: String,
        amount: Int,
    ): Boolean {
        platformBridge.sendMessage(playerId, "Received $itemId x$amount")
        return true
    }

    override fun dropItem(
        playerId: UUID,
        itemId: String,
        amount: Int,
        location: com.hytale.bedwars.core.map.Location,
    ) {
        platformBridge.sendMessage(playerId, "Inventory full, dropping $itemId")
        worldBridge.dropItem(location, itemId, amount, mergeRadius = 0)
    }
}
