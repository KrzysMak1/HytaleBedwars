package com.hytale.bedwars.core.upgrade

enum class UpgradeType {
    SHARPENED_SWORDS,
    REINFORCED_ARMOR,
    HASTE,
    HEAL_POOL,
    TRAP,
    FORGE,
}

data class TeamUpgrade(val type: UpgradeType, var level: Int = 0, val maxLevel: Int = 1)
