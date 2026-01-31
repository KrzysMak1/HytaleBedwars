package com.hytale.bedwars.platform.mapio

import com.hytale.bedwars.core.economy.Currency
import com.hytale.bedwars.core.map.Location
import com.hytale.bedwars.core.map.MapGenerator
import com.hytale.bedwars.core.map.MapTeamTemplate
import com.hytale.bedwars.core.map.MapTemplate
import com.hytale.bedwars.core.map.Region
import org.yaml.snakeyaml.Yaml
import java.io.File

class MapYmlLoader(private val mapsRoot: File) {
    fun load(mapId: String): MapTemplate {
        val mapDir = File(mapsRoot, mapId)
        val mapFile = File(mapDir, "map.yml")
        require(mapFile.exists()) { "Map file not found: ${mapFile.path}" }

        val yaml = Yaml()
        val data = yaml.load<Map<String, Any>>(mapFile.readText())

        val teams = (data["teams"] as? List<*>)?.mapNotNull { it as? Map<*, *> }
            ?: error("Map is missing teams")

        val teamModels = teams.map { team ->
            MapTeamTemplate(
                color = team["color"].toString(),
                name = team["name"].toString(),
                spawn = readLocation(team["spawn"]),
                bed = readLocation(team["bed"]),
                bedRegion = readRegion(team["bedRegion"]),
                baseRegion = requireNotNull(readRegion(team["baseRegion"])) { "baseRegion missing" },
                shopNpc = readLocation(team["shopNpc"]),
                upgradeNpc = readLocation(team["upgradeNpc"]),
            )
        }

        val generators = (data["generators"] as? List<*>)?.mapNotNull { it as? Map<*, *> }.orEmpty().map { gen ->
            MapGenerator(
                type = Currency.valueOf(gen["type"].toString()),
                location = readLocation(gen["location"]),
                teamColor = gen["teamColor"]?.toString(),
            )
        }

        return MapTemplate(
            id = data["id"].toString(),
            name = data["name"].toString(),
            author = data["author"].toString(),
            supportedModes = (data["supportedModes"] as? List<*>)?.map { it.toString() }.orEmpty(),
            teams = teamModels,
            generators = generators,
            voidY = data["voidY"].toString().toInt(),
            buildRegion = requireNotNull(readRegion(data["buildRegion"])) { "buildRegion missing" },
        )
    }

    private fun readLocation(value: Any?): Location {
        val map = value as? Map<*, *> ?: error("Expected location map, got $value")
        return Location(
            x = map["x"].toString().toDouble(),
            y = map["y"].toString().toDouble(),
            z = map["z"].toString().toDouble(),
            yaw = map["yaw"]?.toString()?.toFloat() ?: 0f,
            pitch = map["pitch"]?.toString()?.toFloat() ?: 0f,
        )
    }

    private fun readRegion(value: Any?): Region? {
        val map = value as? Map<*, *> ?: return null
        return Region(
            min = readLocation(map["min"]),
            max = readLocation(map["max"]),
        )
    }
}
