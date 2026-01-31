package com.hytale.bedwars.core.admin

import com.hytale.bedwars.core.match.MatchMode

data class AdminStartRequest(val mapId: String, val mode: MatchMode)

data class AdminStopRequest(val matchId: String)
