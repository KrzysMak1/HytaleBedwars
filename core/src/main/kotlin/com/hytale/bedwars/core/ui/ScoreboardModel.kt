package com.hytale.bedwars.core.ui

data class ScoreboardTeamStatus(
    val teamName: String,
    val indicator: String,
)

data class GeneratorPhaseStatus(
    val label: String,
    val secondsUntilNext: Int,
)

data class ScoreboardModel(
    val title: String,
    val matchTimeSeconds: Int,
    val phases: List<GeneratorPhaseStatus>,
    val teamStatuses: List<ScoreboardTeamStatus>,
    val footerLines: List<String>,
)
