package com.hytale.bedwars.core.generator

import com.hytale.bedwars.core.economy.Currency

enum class GeneratorPhase(val level: Int) {
    I(1),
    II(2),
    III(3),
}

data class PhaseEntry(
    val type: Currency,
    val phase: GeneratorPhase,
    val startSeconds: Int,
    val dropIntervalTicks: Int,
)

class PhaseScheduler(private val entries: List<PhaseEntry>) {
    fun phaseFor(type: Currency, elapsedSeconds: Int): PhaseEntry? {
        return entries
            .filter { it.type == type && elapsedSeconds >= it.startSeconds }
            .maxByOrNull { it.startSeconds }
    }
}
