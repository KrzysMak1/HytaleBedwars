package com.hytale.bedwars.core.match

import com.hytale.bedwars.core.economy.Currency

/**
 * Configuration values needed by core match logic, independent of platform.
 */
data class MatchConfig(
    val minPlayers: Int,
    val startingCountdownSeconds: Int,
    val respawnDelaySeconds: Int,
    val spawnProtectionSeconds: Int,
    val assistWindowSeconds: Long,
    val enableSpectatorJoin: Boolean,
    val maxItemsOnGround: Int,
    val mergeRadius: Int,
    val blockPlacePerSecondLimit: Int,
    val bedProtectionEnabled: Boolean,
    val currencyDropOnDeath: Map<Currency, Boolean>,
)
