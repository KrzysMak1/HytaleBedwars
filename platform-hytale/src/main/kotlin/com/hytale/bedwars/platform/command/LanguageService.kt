package com.hytale.bedwars.platform.command

import java.util.UUID

class LanguageService {
    data class Language(
        val code: String,
        val displayName: String,
    )

    private val supportedLanguages =
        listOf(
            Language("en", "English"),
            Language("zh", "中文"),
            Language("hi", "हिन्दी"),
            Language("es", "Español"),
            Language("fr", "Français"),
            Language("ar", "العربية"),
            Language("bn", "বাংলা"),
            Language("ru", "Русский"),
            Language("pt", "Português"),
            Language("pl", "Polski"),
        )

    private val playerLanguages = mutableMapOf<UUID, String>()

    fun listLanguages(): List<Language> = supportedLanguages

    fun resolve(code: String): Language? = supportedLanguages.firstOrNull { it.code == code }

    fun setLanguage(
        playerId: UUID,
        code: String,
    ) {
        playerLanguages[playerId] = code
    }
}
