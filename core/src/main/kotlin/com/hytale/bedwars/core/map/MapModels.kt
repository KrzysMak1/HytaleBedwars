package com.hytale.bedwars.core.map

import com.hytale.bedwars.core.economy.Currency

data class Location(val x: Double, val y: Double, val z: Double, val yaw: Float = 0f, val pitch: Float = 0f)

data class Region(val min: Location, val max: Location)

data class MapTeamTemplate(
    val color: String,
    val name: String,
    val spawn: Location,
    val bed: Location,
    val bedRegion: Region?,
    val baseRegion: Region,
    val shopNpc: Location,
    val upgradeNpc: Location,
)

data class MapGenerator(
    val type: Currency,
    val location: Location,
    val teamColor: String?,
)

data class MapTemplate(
    val id: String,
    val name: String,
    val author: String,
    val supportedModes: List<String>,
    val teams: List<MapTeamTemplate>,
    val generators: List<MapGenerator>,
    val voidY: Int,
    val buildRegion: Region,
)
