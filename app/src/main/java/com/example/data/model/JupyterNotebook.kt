package com.example.data.model

data class NotebookCell(
    val id: String,
    val cellType: CellType,
    val source: String,
    var output: String = "",
    var isExecuting: Boolean = false,
    var executionCount: Int? = null
)

enum class CellType {
    CODE,
    MARKDOWN
}

data class JupyterNotebook(
    val title: String,
    val filePath: String,
    val cells: List<NotebookCell>,
    val kernelName: String = "python3"
)
