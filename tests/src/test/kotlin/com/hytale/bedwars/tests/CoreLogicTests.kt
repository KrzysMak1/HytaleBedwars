package com.hytale.bedwars.tests

import com.hytale.bedwars.core.bed.BedService
import com.hytale.bedwars.core.bed.BedState
import com.hytale.bedwars.core.block.BlockPosition
import com.hytale.bedwars.core.block.BlockRules
import com.hytale.bedwars.core.block.PlacedBlocksTracker
import com.hytale.bedwars.core.combat.KillAttributionService
import com.hytale.bedwars.core.economy.Currency
import com.hytale.bedwars.core.economy.CurrencyInventory
import com.hytale.bedwars.core.economy.CurrencyStack
import com.hytale.bedwars.core.economy.EconomyService
import com.hytale.bedwars.core.economy.Price
import com.hytale.bedwars.core.economy.Wallet
import com.hytale.bedwars.core.map.Location
import com.hytale.bedwars.core.map.Region
import com.hytale.bedwars.core.match.Match
import com.hytale.bedwars.core.match.MatchMode
import com.hytale.bedwars.core.match.MatchState
import com.hytale.bedwars.core.player.PlayerSession
import com.hytale.bedwars.core.player.PlayerState
import com.hytale.bedwars.core.player.RespawnService
import com.hytale.bedwars.core.team.Team
import com.hytale.bedwars.core.team.TeamState
import com.hytale.bedwars.core.trap.TrapQueue
import com.hytale.bedwars.core.trap.TrapService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.UUID

class CoreLogicTests {
    @Test
    fun winnerCalculationReturnsSingleActiveTeam() {
        val teamA = Team("A", "RED", "Red", mutableSetOf())
        val teamB = Team("B", "BLUE", "Blue", mutableSetOf(), state = TeamState.ELIMINATED)
        val playerId = UUID.randomUUID()
        teamA.playerIds.add(playerId)
        val sessions = mutableMapOf(playerId to PlayerSession(playerId))
        val match =
            Match(
                matchId = "match-1",
                mode = MatchMode.SOLO,
                mapTemplateId = "map-1",
                mapInstanceId = "inst-1",
                state = MatchState.INGAME,
                teams = mutableListOf(teamA, teamB),
                players = sessions,
                placedBlocksTracker = PlacedBlocksTracker(),
                generators = mutableListOf(),
            )

        val winner = match.determineWinner()
        assertNotNull(winner)
        assertEquals("A", winner?.id)
    }

    @Test
    fun respawnAllowedOnlyWhenBedAlive() {
        val respawnService = RespawnService()
        assertTrue(respawnService.canRespawn(BedState.ALIVE))
        assertFalse(respawnService.canRespawn(BedState.DESTROYED))
    }

    @Test
    fun pendingRespawnCanceledOnBedBreak() {
        val team = Team("A", "RED", "Red", mutableSetOf())
        val playerId = UUID.randomUUID()
        team.playerIds.add(playerId)
        val session = PlayerSession(playerId, teamId = "A", state = PlayerState.RESPAWNING, pendingRespawn = true)

        val bedService = BedService()
        val destroyed = bedService.destroyBed(team, listOf(session))

        assertTrue(destroyed)
        assertFalse(session.pendingRespawn)
        assertEquals(BedState.DESTROYED, team.bedState)
    }

    @Test
    fun economyChargeHandlesZeroOrNegativeAmounts() {
        val economyService = EconomyService()
        val wallet = Wallet(mutableMapOf(Currency.IRON to 5))

        assertTrue(economyService.charge(wallet, Price(Currency.IRON, 0)))
        assertTrue(economyService.charge(wallet, Price(Currency.IRON, -5)))
        assertEquals(5, wallet.balance(Currency.IRON))
        assertFalse(economyService.charge(wallet, Price(Currency.IRON, 10)))
        assertEquals(5, wallet.balance(Currency.IRON))
    }

    @Test
    fun economyChargeRemovesFromMultipleStacks() {
        val inventory =
            CurrencyInventory(
                listOf(
                    CurrencyStack(Currency.IRON, 3),
                    CurrencyStack(Currency.IRON, 2),
                    CurrencyStack(Currency.GOLD, 5),
                ),
            )
        val economyService = EconomyService()

        assertTrue(economyService.chargeInventory(inventory, Price(Currency.IRON, 5)))
        assertEquals(0, inventory.total(Currency.IRON))
        assertEquals(5, inventory.total(Currency.GOLD))
    }

    @Test
    fun killAttributionUsesAssistWindowWithVoid() {
        val victim = PlayerSession(UUID.randomUUID())
        val attackerId = UUID.randomUUID()
        victim.markDamage(attackerId, now = 1000)

        val attribution = KillAttributionService(assistWindowSeconds = 5)
        assertEquals(attackerId, attribution.resolveKiller(victim, 5000))
        assertNull(attribution.resolveKiller(victim, 7000))
    }

    @Test
    fun blockBreakRulesAllowPlacedOrWhitelisted() {
        val tracker = PlacedBlocksTracker()
        val placed = BlockPosition(1, 64, 1)
        val whitelist = BlockPosition(2, 64, 2)
        tracker.track(placed)
        val rules = BlockRules(tracker, setOf(whitelist))

        assertTrue(rules.canBreak(placed))
        assertTrue(rules.canBreak(whitelist))
        assertFalse(rules.canBreak(BlockPosition(3, 64, 3)))
    }

    @Test
    fun blockPlaceRulesRespectBuildAndProtectedRegions() {
        val tracker = PlacedBlocksTracker()
        val rules = BlockRules(tracker, emptySet())
        val buildRegion = Region(Location(0.0, 0.0, 0.0), Location(10.0, 10.0, 10.0))
        val protectedRegion = Region(Location(4.0, 0.0, 4.0), Location(6.0, 10.0, 6.0))

        assertTrue(rules.canPlace(BlockPosition(1, 1, 1), buildRegion, listOf(protectedRegion)))
        assertFalse(rules.canPlace(BlockPosition(5, 1, 5), buildRegion, listOf(protectedRegion)))
        assertFalse(rules.canPlace(BlockPosition(20, 1, 20), buildRegion, listOf(protectedRegion)))
    }

    @Test
    fun trapQueueConsumesOnTriggerAndEffectReturned() {
        val queue = TrapQueue(maxTraps = 2)
        val trapService = TrapService()
        queue.enqueue("alarm")
        val effect = trapService.trigger(queue, "intruder")

        assertNotNull(effect)
        assertEquals("alarm", effect?.debuff)
        assertTrue(effect?.revealTag?.contains("intruder") == true)
    }

    @Test
    fun winnerCalculationIgnoresEliminatedTeams() {
        val teamA = Team("A", "RED", "Red", mutableSetOf())
        val teamB = Team("B", "BLUE", "Blue", mutableSetOf(), state = TeamState.ELIMINATED)
        val playerId = UUID.randomUUID()
        teamA.playerIds.add(playerId)
        val sessions = mutableMapOf(playerId to PlayerSession(playerId))
        val match =
            Match(
                matchId = "match-2",
                mode = MatchMode.SOLO,
                mapTemplateId = "map-1",
                mapInstanceId = "inst-2",
                state = MatchState.INGAME,
                teams = mutableListOf(teamA, teamB),
                players = sessions,
                placedBlocksTracker = PlacedBlocksTracker(),
                generators = mutableListOf(),
            )

        assertEquals("A", match.determineWinner()?.id)
    }

    @Test
    fun winnerCalculationDoesNotAutoEliminateBedDestroyedTeams() {
        val teamA = Team("A", "RED", "Red", mutableSetOf())
        val teamB = Team("B", "BLUE", "Blue", mutableSetOf())
        teamB.bedState = BedState.DESTROYED
        val playerId = UUID.randomUUID()
        teamA.playerIds.add(playerId)
        val sessions = mutableMapOf(playerId to PlayerSession(playerId))
        val match =
            Match(
                matchId = "match-3",
                mode = MatchMode.SOLO,
                mapTemplateId = "map-1",
                mapInstanceId = "inst-3",
                state = MatchState.INGAME,
                teams = mutableListOf(teamA, teamB),
                players = sessions,
                placedBlocksTracker = PlacedBlocksTracker(),
                generators = mutableListOf(),
            )

        assertEquals("A", match.determineWinner()?.id)
    }
}
