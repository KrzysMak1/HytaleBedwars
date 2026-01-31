package com.hytale.bedwars.platform.plugin

import com.hytale.bedwars.storage.migration.ConfigMigration
import com.hytale.bedwars.storage.migration.ConfigMigrator

class ConfigMigrationRunner {
    private val migrator = ConfigMigrator(
        listOf(
            ConfigMigration(1, 2, "Add actionbarEnabled toggle"),
        ),
    )

    fun run(currentVersion: Int, targetVersion: Int): List<ConfigMigration> {
        return migrator.migrate(currentVersion, targetVersion)
    }
}
