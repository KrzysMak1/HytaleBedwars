package com.hytale.bedwars.platform.mapio

import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class MapInstanceManager(private val mapsRoot: File) {
    private val instancesDir = File(mapsRoot, "instances")

    init {
        if (!instancesDir.exists()) {
            instancesDir.mkdirs()
        }
    }

    fun cloneTemplate(mapId: String, matchId: String): String {
        val templateDir = File(mapsRoot, mapId)
        require(templateDir.exists()) { "Map template not found: ${templateDir.path}" }
        val instanceId = "$mapId-$matchId-${System.currentTimeMillis()}"
        val instanceDir = File(instancesDir, instanceId)
        copyDirectory(templateDir, instanceDir)
        return instanceId
    }

    fun destroyInstance(instanceId: String) {
        val instanceDir = File(instancesDir, instanceId)
        if (instanceDir.exists()) {
            instanceDir.deleteRecursively()
        }
    }

    fun cleanupOrphansOnStartup() {
        if (!instancesDir.exists()) {
            return
        }
        instancesDir.listFiles()?.forEach { it.deleteRecursively() }
    }

    private fun copyDirectory(source: File, target: File) {
        if (source.isDirectory) {
            if (!target.exists()) {
                target.mkdirs()
            }
            source.listFiles()?.forEach { child ->
                copyDirectory(child, File(target, child.name))
            }
        } else {
            Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING)
        }
    }
}
