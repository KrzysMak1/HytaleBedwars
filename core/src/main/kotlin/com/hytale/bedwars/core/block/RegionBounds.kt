package com.hytale.bedwars.core.block

import com.hytale.bedwars.core.map.Region

object RegionBounds {
    fun contains(region: Region, position: BlockPosition): Boolean {
        val minX = minOf(region.min.x, region.max.x)
        val maxX = maxOf(region.min.x, region.max.x)
        val minY = minOf(region.min.y, region.max.y)
        val maxY = maxOf(region.min.y, region.max.y)
        val minZ = minOf(region.min.z, region.max.z)
        val maxZ = maxOf(region.min.z, region.max.z)
        return position.x in minX.toInt()..maxX.toInt() &&
            position.y in minY.toInt()..maxY.toInt() &&
            position.z in minZ.toInt()..maxZ.toInt()
    }
}
