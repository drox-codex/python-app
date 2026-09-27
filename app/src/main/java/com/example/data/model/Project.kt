package com.example.data.model

import java.io.File

data class Project(
    val id: String,
    val name: String,
    val directoryPath: String,
    val lastModified: Long,
    val lastModifiedFormatted: String,
    val fileCount: Int = 0,
    val projectType: ProjectType = ProjectType.STANDARD
) {
    val file: File
        get() = File(directoryPath)
}

enum class ProjectType {
    STANDARD,
    WEB_APP,
    NETWORK,
    AI_TOOLS,
    DATA_SCIENCE,
    GAME,
    MOBILE_APP
}
