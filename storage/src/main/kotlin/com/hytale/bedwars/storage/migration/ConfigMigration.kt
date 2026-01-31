package com.hytale.bedwars.storage.migration

class ConfigMigration(val fromVersion: Int, val toVersion: Int, val description: String)

class ConfigMigrator(private val migrations: List<ConfigMigration>) {
    fun migrate(currentVersion: Int, targetVersion: Int): List<ConfigMigration> {
        if (currentVersion >= targetVersion) {
            return emptyList()
        }
        return migrations
            .filter { it.fromVersion >= currentVersion && it.toVersion <= targetVersion }
            .sortedBy { it.fromVersion }
    }
}
