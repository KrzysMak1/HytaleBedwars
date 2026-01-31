package com.hytale.bedwars.platform.mapio

import java.io.File

class MapValidator(private val loader: MapYmlLoader) {
    fun validateMap(mapId: String) {
        val map = loader.load(mapId)
        require(map.teams.isNotEmpty()) { "Map ${map.id} has no teams" }
        map.teams.forEach { team ->
            require(team.spawn.x.isFinite()) { "Team ${team.name} spawn invalid" }
            require(team.baseRegion.min.x.isFinite()) { "Team ${team.name} baseRegion invalid" }
        }
        require(map.generators.isNotEmpty()) { "Map ${map.id} has no generators" }
    }

    fun validateAll(mapsRoot: File) {
        mapsRoot.listFiles()?.filter { it.isDirectory }?.forEach { dir ->
            val mapFile = File(dir, "map.yml")
            if (mapFile.exists()) {
                validateMap(dir.name)
            }
        }
    }
}
