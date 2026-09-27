package com.example.data.model

import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

object JupyterNotebookParser {

    fun parse(jsonContent: String, filePath: String): JupyterNotebook {
        return try {
            val root = JSONObject(jsonContent)
            val cellsArray = root.optJSONArray("cells") ?: JSONArray()
            val cells = mutableListOf<NotebookCell>()

            for (i in 0 until cellsArray.length()) {
                val cellObj = cellsArray.getJSONObject(i)
                val typeStr = cellObj.optString("cell_type", "code")
                val cellType = if (typeStr == "markdown") CellType.MARKDOWN else CellType.CODE

                // source can be array of strings or single string
                val source = parseSource(cellObj.opt("source"))

                // outputs
                val outputsArray = cellObj.optJSONArray("outputs")
                val outputText = StringBuilder()
                if (outputsArray != null) {
                    for (j in 0 until outputsArray.length()) {
                        val out = outputsArray.getJSONObject(j)
                        val text = parseSource(out.opt("text"))
                        if (text.isNotEmpty()) outputText.append(text).append("\n")
                    }
                }

                val execCount = if (cellObj.has("execution_count") && !cellObj.isNull("execution_count")) {
                    cellObj.optInt("execution_count")
                } else null

                cells.add(
                    NotebookCell(
                        id = UUID.randomUUID().toString(),
                        cellType = cellType,
                        source = source,
                        output = outputText.toString().trim(),
                        executionCount = execCount
                    )
                )
            }

            val metadata = root.optJSONObject("metadata")
            val kernelName = metadata?.optJSONObject("kernelspec")?.optString("name") ?: "python3"
            val title = filePath.substringAfterLast("/").removeSuffix(".ipynb")

            JupyterNotebook(title = title, filePath = filePath, cells = cells, kernelName = kernelName)
        } catch (e: Exception) {
            // Fallback for empty or corrupted notebook
            createEmptyNotebook(filePath)
        }
    }

    fun serialize(notebook: JupyterNotebook): String {
        val root = JSONObject()
        val cellsArray = JSONArray()

        notebook.cells.forEach { cell ->
            val cellObj = JSONObject()
            cellObj.put("cell_type", if (cell.cellType == CellType.MARKDOWN) "markdown" else "code")
            cellObj.put("source", JSONArray(cell.source.lines()))
            cellObj.put("metadata", JSONObject())

            if (cell.cellType == CellType.CODE) {
                cellObj.put("execution_count", cell.executionCount ?: JSONObject.NULL)
                val outputsArray = JSONArray()
                if (cell.output.isNotEmpty()) {
                    val outObj = JSONObject()
                    outObj.put("output_type", "stream")
                    outObj.put("name", "stdout")
                    outObj.put("text", JSONArray(cell.output.lines()))
                    outputsArray.put(outObj)
                }
                cellObj.put("outputs", outputsArray)
            }
            cellsArray.put(cellObj)
        }

        root.put("cells", cellsArray)
        root.put("metadata", JSONObject().apply {
            put("language_info", JSONObject().apply { put("name", "python") })
        })
        root.put("nbformat", 4)
        root.put("nbformat_minor", 5)

        return root.toString(2)
    }

    fun createEmptyNotebook(filePath: String): JupyterNotebook {
        val title = filePath.substringAfterLast("/").removeSuffix(".ipynb")
        val defaultCells = listOf(
            NotebookCell(
                id = "c1",
                cellType = CellType.MARKDOWN,
                source = "# $title\n\nJupyter Notebook on Android powered by **Python IDE**."
            ),
            NotebookCell(
                id = "c2",
                cellType = CellType.CODE,
                source = "import math\n\ndef calculate():\n    return [math.sqrt(i) for i in range(1, 6)]\n\nprint(calculate())"
            )
        )
        return JupyterNotebook(title = title, filePath = filePath, cells = defaultCells)
    }

    private fun parseSource(sourceObj: Any?): String {
        return when (sourceObj) {
            is JSONArray -> {
                val sb = StringBuilder()
                for (i in 0 until sourceObj.length()) {
                    sb.append(sourceObj.getString(i))
                }
                sb.toString()
            }
            is String -> sourceObj
            else -> ""
        }
    }
}
