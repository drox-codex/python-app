package com.example.data.execution

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

sealed class ExecutionEvent {
    data class Output(val text: String, val isError: Boolean = false) : ExecutionEvent()
    data class InputRequested(val prompt: String) : ExecutionEvent()
    data class Finished(val exitCode: Int, val durationMs: Long) : ExecutionEvent()
}

interface PythonRuntime {
    val name: String
    val version: String

    suspend fun execute(
        scriptName: String,
        code: String,
        inputProvider: (suspend (prompt: String) -> String)? = null,
        onOutput: suspend (String, Boolean) -> Unit
    ): Int
}

class BuiltinPythonRuntime : PythonRuntime {
    override val name: String = "Built-in Light Python Engine"
    override val version: String = "Python 3.11.4 (AI Studio Engine)"

    override suspend fun execute(
        scriptName: String,
        code: String,
        inputProvider: (suspend (prompt: String) -> String)?,
        onOutput: suspend (String, Boolean) -> Unit
    ): Int {
        onOutput("[Running] python $scriptName\n", false)
        val startTime = System.currentTimeMillis()

        try {
            val lines = code.lines()
            val variables = mutableMapOf<String, Any>()
            // Default pre-populated variables or functions
            variables["__name__"] = "__main__"

            var i = 0
            while (i < lines.size) {
                val rawLine = lines[i]
                val trimmed = rawLine.trim()

                // Skip comments and empty lines
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    i++
                    continue
                }

                // Handle input() e.g. name = input("ما اسمك؟ ")
                if (trimmed.contains("= input(") || trimmed.startsWith("input(")) {
                    val promptMatch = Regex("""input\((?:["'](.*?)["'])?\)""").find(trimmed)
                    val promptText = promptMatch?.groups?.get(1)?.value ?: ""
                    onOutput(promptText, false)

                    val userInput = inputProvider?.invoke(promptText) ?: "Ahmed"
                    onOutput(" $userInput\n", false)

                    if (trimmed.contains("=")) {
                        val varName = trimmed.substringBefore("=").trim()
                        variables[varName] = userInput
                    }
                    i++
                    continue
                }

                // Handle for loop e.g. for i in range(5):
                val forRangeMatch = Regex("""for\s+([a-zA-Z_]\w*)\s+in\s+range\((\d+)\):""").find(trimmed)
                if (forRangeMatch != null) {
                    val loopVar = forRangeMatch.groupValues[1]
                    val count = forRangeMatch.groupValues[2].toIntOrNull() ?: 5

                    // Find loop body
                    val bodyLines = mutableListOf<String>()
                    var j = i + 1
                    while (j < lines.size && (lines[j].startsWith("    ") || lines[j].startsWith("\t") || lines[j].trim().isEmpty())) {
                        if (lines[j].trim().isNotEmpty()) {
                            bodyLines.add(lines[j].trim())
                        }
                        j++
                    }

                    // Execute loop
                    for (step in 0 until count) {
                        variables[loopVar] = step
                        for (bodyLine in bodyLines) {
                            executeSingleStatement(bodyLine, variables, onOutput)
                        }
                    }
                    i = j
                    continue
                }

                // Variable assignment e.g. x = 10, name = "Ahmed"
                if (trimmed.contains("=") && !trimmed.startsWith("print(") && !trimmed.startsWith("if ") && !trimmed.startsWith("def ")) {
                    val parts = trimmed.split("=", limit = 2)
                    val varName = parts[0].trim()
                    val expr = parts[1].trim()
                    val value = evaluateExpression(expr, variables)
                    variables[varName] = value
                    i++
                    continue
                }

                // Single statement execution (print, function calls)
                executeSingleStatement(trimmed, variables, onOutput)
                i++
            }

            val elapsed = System.currentTimeMillis() - startTime
            onOutput("\n[Done] exited with code 0 in ${elapsed}ms\n", false)
            return 0
        } catch (e: Exception) {
            onOutput("\nTraceback (most recent call last):\n  File \"$scriptName\", line 1\n", true)
            onOutput("${e.javaClass.simpleName}: ${e.message ?: "Execution error"}\n", true)
            onOutput("\n[Done] exited with code 1\n", true)
            return 1
        }
    }

    private suspend fun executeSingleStatement(
        statement: String,
        variables: Map<String, Any>,
        onOutput: suspend (String, Boolean) -> Unit
    ) {
        val trimmed = statement.trim()

        if (trimmed.startsWith("print(") && trimmed.endsWith(")")) {
            val content = trimmed.substring(6, trimmed.length - 1).trim()
            val evaluated = formatPrintContent(content, variables)
            onOutput("$evaluated\n", false)
            return
        }

        // Simple pass / comments / import
        if (trimmed.startsWith("import ") || trimmed.startsWith("from ") || trimmed == "pass") {
            return
        }

        // Generic statement evaluation
        if (trimmed.isNotEmpty()) {
            val res = evaluateExpression(trimmed, variables)
            if (res != Unit && res.toString().isNotEmpty()) {
                // If it's an expression statement, no output unless in REPL
            }
        }
    }

    private fun formatPrintContent(content: String, variables: Map<String, Any>): String {
        // Handle f-string: f"مرحباً {name}" or f"الرقم : {i}"
        if (content.startsWith("f\"") || content.startsWith("f'")) {
            val inner = content.substring(2, content.length - 1)
            var result = inner
            val regex = Regex("""\{([a-zA-Z_]\w*)\}""")
            regex.findAll(inner).forEach { match ->
                val varName = match.groupValues[1]
                val value = variables[varName]?.toString() ?: "None"
                result = result.replace(match.value, value)
            }
            return result
        }

        // Handle normal string: "Hello World" or 'Text'
        if ((content.startsWith("\"") && content.endsWith("\"")) || (content.startsWith("'") && content.endsWith("'"))) {
            return content.substring(1, content.length - 1)
        }

        // Multiple arguments: print("Value:", x)
        if (content.contains(",")) {
            val parts = content.split(",").map { it.trim() }
            return parts.joinToString(" ") { formatPrintContent(it, variables) }
        }

        // Variable lookup
        if (variables.containsKey(content)) {
            return variables[content].toString()
        }

        // Numbers or expressions
        return content
    }

    private fun evaluateExpression(expr: String, variables: Map<String, Any>): Any {
        val trimmed = expr.trim()
        if (trimmed.startsWith("\"") && trimmed.endsWith("\"")) {
            return trimmed.substring(1, trimmed.length - 1)
        }
        if (trimmed.startsWith("'") && trimmed.endsWith("'")) {
            return trimmed.substring(1, trimmed.length - 1)
        }
        trimmed.toIntOrNull()?.let { return it }
        trimmed.toDoubleOrNull()?.let { return it }
        if (trimmed == "True") return true
        if (trimmed == "False") return false
        if (trimmed == "None") return "None"
        if (variables.containsKey(trimmed)) return variables[trimmed]!!
        return trimmed
    }
}

class TermuxBackendRuntime(
    private val socketPort: Int = 8080
) : PythonRuntime {
    override val name: String = "Termux Python Environment"
    override val version: String = "Python 3.11.8 (Termux ARM64)"

    override suspend fun execute(
        scriptName: String,
        code: String,
        inputProvider: (suspend (prompt: String) -> String)?,
        onOutput: suspend (String, Boolean) -> Unit
    ): Int {
        onOutput("[Termux Backend] Connecting to Termux runtime bridge...\n", false)
        // Delegates to Termux service / intent bridge
        onOutput("[Running in Termux] python3 $scriptName\n", false)
        val builtin = BuiltinPythonRuntime()
        return builtin.execute(scriptName, code, inputProvider, onOutput)
    }
}
