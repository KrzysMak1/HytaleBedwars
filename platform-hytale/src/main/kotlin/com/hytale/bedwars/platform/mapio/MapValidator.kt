package com.hytale.bedwars.platform.mapio

import java.io.File

class MapValidator(private val loader: MapYmlLoader) {
    fun validateMap(mapId: String) {
        val map = loader.load(mapId)
        require(map.teams.isNotEmpty()) { "Map ${map.id} has no teams" }
        require(map.voidY < map.buildRegion.max.y) { "Map ${map.id} voidY must be below buildRegion maxY" }
        validateRegion(map.buildRegion, "buildRegion")
        map.teams.forEach { team ->
            require(team.spawn.x.isFinite()) { "Team ${team.name} spawn invalid" }
            validateRegion(team.baseRegion, "Team ${team.name} baseRegion")
            team.bedRegion?.let { validateRegion(it, "Team ${team.name} bedRegion") }
            require(contains(team.baseRegion, team.spawn)) { "Team ${team.name} spawn outside baseRegion" }
            require(contains(team.baseRegion, team.bed)) { "Team ${team.name} bed outside baseRegion" }
            require(contains(team.baseRegion, team.shopNpc)) { "Team ${team.name} shopNpc outside baseRegion" }
            require(contains(team.baseRegion, team.upgradeNpc)) { "Team ${team.name} upgradeNpc outside baseRegion" }
        }
        require(map.generators.isNotEmpty()) { "Map ${map.id} has no generators" }
        map.generators.forEach { gen ->
            require(gen.location.y > map.voidY) { "Generator below voidY" }
        }
    }

    fun validateAll(mapsRoot: File) {
        mapsRoot.listFiles()?.filter { it.isDirectory }?.forEach { dir ->
            val mapFile = File(dir, "map.yml")
            if (mapFile.exists()) {
                validateMap(dir.name)
            }
        }
    }

    private fun validateRegion(
        region: com.hytale.bedwars.core.map.Region,
        label: String,
    ) {
        val min = region.min
        val max = region.max
        require(min.x.isFinite() && min.y.isFinite() && min.z.isFinite()) { "$label min invalid" }
        require(max.x.isFinite() && max.y.isFinite() && max.z.isFinite()) { "$label max invalid" }
        require(min.x <= max.x && min.y <= max.y && min.z <= max.z) { "$label min/max inverted" }
    }

    private fun contains(
        region: com.hytale.bedwars.core.map.Region,
        location: com.hytale.bedwars.core.map.Location,
    ): Boolean {
        val minX = minOf(region.min.x, region.max.x)
        val maxX = maxOf(region.min.x, region.max.x)
        val minY = minOf(region.min.y, region.max.y)
        val maxY = maxOf(region.min.y, region.max.y)
        val minZ = minOf(region.min.z, region.max.z)
        val maxZ = maxOf(region.min.z, region.max.z)
        return location.x in minX..maxX && location.y in minY..maxY && location.z in minZ..maxZ
    }
}
