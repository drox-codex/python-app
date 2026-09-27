package com.example.data.model

import java.io.File

data class ProjectFile(
    val name: String,
    val path: String,
    val isDirectory: Boolean,
    val sizeBytes: Long = 0,
    val sizeFormatted: String = "",
    val lastModifiedFormatted: String = "",
    val extension: String = "",
    val children: List<ProjectFile> = emptyList()
) {
    val file: File
        get() = File(path)

    val isPythonFile: Boolean
        get() = extension.equals("py", ignoreCase = true)

    val isMarkdown: Boolean
        get() = extension.equals("md", ignoreCase = true)

    val isConfig: Boolean
        get() = extension in listOf("json", "txt", "yaml", "yml", "toml", "ini", "cfg")
}
