package com.example.data.model

import com.example.data.execution.RuntimeEngineType
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
    val activeEngine: RuntimeEngineType = RuntimeEngineType.BUILTIN,
    val termuxIntegrationEnabled: Boolean = true,
    val termuxHost: String = "127.0.0.1",
    val termuxPort: Int = 8080,
    val gitBackend: String = "Native JGit / Libgit2",
    val defaultProjectsDirectory: String = "/storage/emulated/0/PythonProjects"
)
