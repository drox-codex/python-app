package com.example.data.repository

import com.example.data.execution.RuntimeEngineType
import com.example.data.model.AppSettings
import com.example.ui.localization.AppLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepository {

    private val _settings = MutableStateFlow(AppSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    fun updateLanguage(language: AppLanguage) {
        _settings.value = _settings.value.copy(language = language)
    }

    fun updateDarkMode(isDark: Boolean) {
        _settings.value = _settings.value.copy(isDarkMode = isDark)
    }

    fun updateFontSize(sizeSp: Int) {
        _settings.value = _settings.value.copy(fontSizeSp = sizeSp)
    }

    fun updateTabSize(spaces: Int) {
        _settings.value = _settings.value.copy(tabSizeSpaces = spaces)
    }

    fun updateWordWrap(enabled: Boolean) {
        _settings.value = _settings.value.copy(wordWrap = enabled)
    }

    fun updateLineNumbers(show: Boolean) {
        _settings.value = _settings.value.copy(showLineNumbers = show)
    }

    fun updateAutoSave(enabled: Boolean) {
        _settings.value = _settings.value.copy(autoSave = enabled)
    }

    fun updateTermuxIntegration(enabled: Boolean) {
        _settings.value = _settings.value.copy(termuxIntegrationEnabled = enabled)
    }

    fun updateEngine(engine: RuntimeEngineType) {
        _settings.value = _settings.value.copy(
            activeEngine = engine,
            pythonVersion = engine.versionString
        )
    }

    fun updateTermuxPort(port: Int) {
        _settings.value = _settings.value.copy(termuxPort = port)
    }

    fun updateGitBackend(backend: String) {
        _settings.value = _settings.value.copy(gitBackend = backend)
    }
}
