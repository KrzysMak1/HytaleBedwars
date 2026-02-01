package com.hytale.bedwars.platform.command

import com.hytale.bedwars.core.api.PlatformBridge
import com.hytale.bedwars.core.match.MatchMode
import com.hytale.bedwars.platform.plugin.MatchOrchestrator
import java.util.UUID

class BedWarsCommandHandler(
    private val orchestrator: MatchOrchestrator,
    private val platformBridge: PlatformBridge,
    private val languageService: LanguageService = LanguageService(),
) {
    fun handle(
        command: String,
        args: List<String>,
        senderId: UUID,
    ) {
        when (command) {
            "/bw" -> handleBedWarsCommand(args, senderId)
            "/language" -> handleLanguageCommand(args, senderId)
        }
    }

    private fun handleBedWarsCommand(
        args: List<String>,
        senderId: UUID,
    ) {
        if (args.size >= 4 && args[0] == "admin" && args[1] == "start") {
            val mapId = args[2]
            val mode = MatchMode.valueOf(args[3])
            orchestrator.createAndStart("admin-${System.currentTimeMillis()}", mapId, mode, listOf(senderId))
            return
        }
        if (args.size >= 2 && args[0] == "admin" && args[1] == "stop") {
            return
        }
        if (args.size >= 2 && args[0] == "admin" && args[1] == "reload") {
            return
        }
        if (args.size >= 3 && args[0] == "admin" && args[1] == "dumpstate") {
            return
        }
    }

    private fun handleLanguageCommand(
        args: List<String>,
        senderId: UUID,
    ) {
        if (args.isEmpty()) {
            val languages = languageService.listLanguages().joinToString { "${it.code} (${it.displayName})" }
            platformBridge.sendMessage(
                senderId,
                "Użycie: /language <kod>. Dostępne języki: $languages",
            )
            return
        }
        val code = args[0].lowercase()
        val language = languageService.resolve(code)
        if (language == null) {
            val languages = languageService.listLanguages().joinToString { it.code }
            platformBridge.sendMessage(
                senderId,
                "Nieznany język '$code'. Wybierz jeden z: $languages",
            )
            return
        }
        languageService.setLanguage(senderId, language.code)
        platformBridge.sendMessage(senderId, "Ustawiono język na ${language.displayName} (${language.code}).")
    }
}
