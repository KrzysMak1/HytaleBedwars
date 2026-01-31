package com.hytale.bedwars.platform.gui

import com.hytale.bedwars.core.upgrade.TeamUpgrade

class UpgradeMenu {
    fun open(playerId: java.util.UUID, upgrades: List<TeamUpgrade>) {
        println("[UpgradeMenu] Opening upgrades for $playerId with ${upgrades.size} upgrades")
    }
}
