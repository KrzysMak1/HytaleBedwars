package com.hytale.bedwars.core.block

data class BlockPosition(val x: Int, val y: Int, val z: Int)

class PlacedBlocksTracker {
    private val placedBlocks = mutableSetOf<BlockPosition>()

    fun track(position: BlockPosition) {
        placedBlocks.add(position)
    }

    fun untrack(position: BlockPosition) {
        placedBlocks.remove(position)
    }

    fun isPlaced(position: BlockPosition): Boolean = placedBlocks.contains(position)
}
