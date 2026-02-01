package com.hytale.bedwars.core.block

import com.hytale.bedwars.core.map.Region

class BlockRules(
    private val tracker: PlacedBlocksTracker,
    private val breakableMapBlocks: Set<BlockPosition>,
) {
    fun canPlace(
        position: BlockPosition,
        buildRegion: Region,
        protectedRegions: List<Region>,
    ): Boolean {
        if (!RegionBounds.contains(buildRegion, position)) {
            return false
        }
        return protectedRegions.none { RegionBounds.contains(it, position) }
    }

    fun canBreak(position: BlockPosition): Boolean {
        return tracker.isPlaced(position) || breakableMapBlocks.contains(position)
    }
}
