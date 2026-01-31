package com.hytale.bedwars.core.generator

import com.hytale.bedwars.core.economy.Currency

class Generator(
    val id: String,
    val type: Currency,
    var dropIntervalTicks: Int,
    val cap: Int,
    val mergeRadius: Int,
    var phaseLevel: Int = 1,
    val teamColor: String? = null,
)
