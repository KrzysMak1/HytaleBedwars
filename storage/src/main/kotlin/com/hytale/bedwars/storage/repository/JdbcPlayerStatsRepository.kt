package com.hytale.bedwars.storage.repository

import com.hytale.bedwars.core.stats.PlayerStats
import java.sql.Connection
import java.sql.DriverManager
import java.util.UUID

class JdbcPlayerStatsRepository(
    private val jdbcUrl: String,
    private val user: String? = null,
    private val password: String? = null,
) : PlayerStatsRepository {
    init {
        createSchema()
    }

    override fun load(playerId: UUID): PlayerStats {
        connection().use { conn ->
            conn.prepareStatement(
                "SELECT wins,losses,kills,deaths,bedsBroken,finalKills,gamesPlayed,winstreak FROM bw_player_stats WHERE playerId = ?",
            ).use { stmt ->
                stmt.setString(1, playerId.toString())
                val rs = stmt.executeQuery()
                return if (rs.next()) {
                    PlayerStats(
                        wins = rs.getInt(1),
                        losses = rs.getInt(2),
                        kills = rs.getInt(3),
                        deaths = rs.getInt(4),
                        bedsBroken = rs.getInt(5),
                        finalKills = rs.getInt(6),
                        gamesPlayed = rs.getInt(7),
                        winstreak = rs.getInt(8),
                    )
                } else {
                    PlayerStats().also { save(playerId, it) }
                }
            }
        }
    }

    override fun save(
        playerId: UUID,
        stats: PlayerStats,
    ) {
        val statement =
            if (jdbcUrl.startsWith("jdbc:mysql")) {
                "INSERT INTO bw_player_stats " +
                    "(playerId,wins,losses,kills,deaths,bedsBroken,finalKills,gamesPlayed,winstreak) " +
                    "VALUES (?,?,?,?,?,?,?,?,?) " +
                    "ON DUPLICATE KEY UPDATE " +
                    "wins=VALUES(wins), " +
                    "losses=VALUES(losses), " +
                    "kills=VALUES(kills), " +
                    "deaths=VALUES(deaths), " +
                    "bedsBroken=VALUES(bedsBroken), " +
                    "finalKills=VALUES(finalKills), " +
                    "gamesPlayed=VALUES(gamesPlayed), " +
                    "winstreak=VALUES(winstreak)"
            } else {
                "INSERT INTO bw_player_stats " +
                    "(playerId,wins,losses,kills,deaths,bedsBroken,finalKills,gamesPlayed,winstreak) " +
                    "VALUES (?,?,?,?,?,?,?,?,?) " +
                    "ON CONFLICT(playerId) DO UPDATE SET " +
                    "wins=excluded.wins, " +
                    "losses=excluded.losses, " +
                    "kills=excluded.kills, " +
                    "deaths=excluded.deaths, " +
                    "bedsBroken=excluded.bedsBroken, " +
                    "finalKills=excluded.finalKills, " +
                    "gamesPlayed=excluded.gamesPlayed, " +
                    "winstreak=excluded.winstreak"
            }
        connection().use { conn ->
            conn.prepareStatement(statement).use { stmt ->
                stmt.setString(1, playerId.toString())
                stmt.setInt(2, stats.wins)
                stmt.setInt(3, stats.losses)
                stmt.setInt(4, stats.kills)
                stmt.setInt(5, stats.deaths)
                stmt.setInt(6, stats.bedsBroken)
                stmt.setInt(7, stats.finalKills)
                stmt.setInt(8, stats.gamesPlayed)
                stmt.setInt(9, stats.winstreak)
                stmt.executeUpdate()
            }
        }
    }

    private fun connection(): Connection {
        return if (user == null) {
            DriverManager.getConnection(jdbcUrl)
        } else {
            DriverManager.getConnection(jdbcUrl, user, password)
        }
    }

    private fun createSchema() {
        connection().use { conn ->
            conn.createStatement().use { stmt ->
                stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS bw_player_stats (" +
                        "playerId VARCHAR(36) PRIMARY KEY, " +
                        "wins INTEGER NOT NULL, " +
                        "losses INTEGER NOT NULL, " +
                        "kills INTEGER NOT NULL, " +
                        "deaths INTEGER NOT NULL, " +
                        "bedsBroken INTEGER NOT NULL, " +
                        "finalKills INTEGER NOT NULL, " +
                        "gamesPlayed INTEGER NOT NULL, " +
                        "winstreak INTEGER NOT NULL" +
                        ")",
                )
            }
        }
    }
}
