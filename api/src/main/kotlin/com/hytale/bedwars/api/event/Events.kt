package com.hytale.bedwars.api.event

import com.hytale.bedwars.core.match.Match
import com.hytale.bedwars.core.team.Team
import com.hytale.bedwars.core.upgrade.UpgradeType
import java.util.UUID

class BedwarsGameStartEvent(val match: Match)
class BedwarsGameEndEvent(val match: Match, val winner: Team?)
class BedBreakEvent(val match: Match, val team: Team, val breaker: UUID)
class PlayerFinalKillEvent(val match: Match, val victim: UUID, val killer: UUID?)
class UpgradePurchaseEvent(val match: Match, val team: Team, val upgrade: UpgradeType)
class ShopPurchaseEvent(val match: Match, val buyer: UUID, val itemId: String)
