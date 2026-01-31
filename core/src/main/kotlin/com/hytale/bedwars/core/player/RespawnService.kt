package com.hytale.bedwars.core.player

import com.hytale.bedwars.core.bed.BedState

class RespawnService {
    fun canRespawn(bedState: BedState): Boolean = bedState == BedState.ALIVE
}
