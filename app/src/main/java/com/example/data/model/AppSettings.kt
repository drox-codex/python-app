package com.example.data.model

import com.example.ui.localization.AppLanguage

data class AppSettings(
    val language: AppLanguage = AppLanguage.ARABIC,
    val isDarkMode: Boolean = true,
    val fontSizeSp: Int = 14,
    val tabSizeSpaces: Int = 4,
    val wordWrap: Boolean = false,
    val showLineNumbers: Boolean = true,
    val autoSave: Boolean = true,
    val pythonVersion: String = "Python 3.11.4",
    val termuxIntegrationEnabled: Boolean = true,
    val defaultProjectsDirectory: String = "/storage/emulated/0/PythonProjects"
)
