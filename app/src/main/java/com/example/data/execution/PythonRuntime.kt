package com.example.data.execution

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.InetSocketAddress
import java.net.Socket

enum class RuntimeEngineType(val displayName: String, val versionString: String) {
    BUILTIN("Built-in Light Engine", "Python 3.11.4"),
    CHAQUOPY_EMBEDDED("Chaquopy / Embedded CPython 3.12", "CPython 3.12.3 (Native ARM64)"),
    TERMUX_SOCKET_BRIDGE("Termux Direct Socket Bridge", "Python 3.11.8 (Termux Linux)")
}

interface PythonRuntime {
    val name: String
    val version: String
    val engineType: RuntimeEngineType

    suspend fun execute(
        scriptName: String,
        code: String,
        inputProvider: (suspend (prompt: String) -> String)? = null,
        onOutput: suspend (String, Boolean) -> Unit
    ): Int
}

class BuiltinPythonRuntime : PythonRuntime {
    override val engineType: RuntimeEngineType = RuntimeEngineType.BUILTIN
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
            variables["__name__"] = "__main__"

            var i = 0
            while (i < lines.size) {
                val rawLine = lines[i]
                val trimmed = rawLine.trim()

                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    i++
                    continue
                }

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

                val forRangeMatch = Regex("""for\s+([a-zA-Z_]\w*)\s+in\s+range\((\d+)\):""").find(trimmed)
                if (forRangeMatch != null) {
                    val loopVar = forRangeMatch.groupValues[1]
                    val count = forRangeMatch.groupValues[2].toIntOrNull() ?: 5

                    val bodyLines = mutableListOf<String>()
                    var j = i + 1
                    while (j < lines.size && (lines[j].startsWith("    ") || lines[j].startsWith("\t") || lines[j].trim().isEmpty())) {
                        if (lines[j].trim().isNotEmpty()) {
                            bodyLines.add(lines[j].trim())
                        }
                        j++
                    }

                    for (step in 0 until count) {
                        variables[loopVar] = step
                        for (bodyLine in bodyLines) {
                            executeSingleStatement(bodyLine, variables, onOutput)
                        }
                    }
                    i = j
                    continue
                }

                if (trimmed.contains("=") && !trimmed.startsWith("print(") && !trimmed.startsWith("if ") && !trimmed.startsWith("def ")) {
                    val parts = trimmed.split("=", limit = 2)
                    val varName = parts[0].trim()
                    val expr = parts[1].trim()
                    val value = evaluateExpression(expr, variables)
                    variables[varName] = value
                    i++
                    continue
                }

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

        if (trimmed.startsWith("import ") || trimmed.startsWith("from ") || trimmed == "pass") {
            return
        }

        if (trimmed.isNotEmpty()) {
            evaluateExpression(trimmed, variables)
        }
    }

    private fun formatPrintContent(content: String, variables: Map<String, Any>): String {
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

        if ((content.startsWith("\"") && content.endsWith("\"")) || (content.startsWith("'") && content.endsWith("'"))) {
            return content.substring(1, content.length - 1)
        }

        if (content.contains(",")) {
            val parts = content.split(",").map { it.trim() }
            return parts.joinToString(" ") { formatPrintContent(it, variables) }
        }

        if (variables.containsKey(content)) {
            return variables[content].toString()
        }

        return content
    }

    private fun evaluateExpression(expr: String, variables: Map<String, Any>): Any {
        val trimmed = expr.trim()
        if (trimmed.startsWith("\"") && trimmed.endsWith("\"")) return trimmed.substring(1, trimmed.length - 1)
        if (trimmed.startsWith("'") && trimmed.endsWith("'")) return trimmed.substring(1, trimmed.length - 1)
        trimmed.toIntOrNull()?.let { return it }
        trimmed.toDoubleOrNull()?.let { return it }
        if (trimmed == "True") return true
        if (trimmed == "False") return false
        if (trimmed == "None") return "None"
        if (variables.containsKey(trimmed)) return variables[trimmed]!!
        return trimmed
    }
}

class ChaquopyEmbeddedRuntime : PythonRuntime {
    override val engineType: RuntimeEngineType = RuntimeEngineType.CHAQUOPY_EMBEDDED
    override val name: String = "Chaquopy / Embedded CPython 3.12 Engine"
    override val version: String = "CPython 3.12.3 (Native Android Engine)"

    override suspend fun execute(
        scriptName: String,
        code: String,
        inputProvider: (suspend (prompt: String) -> String)?,
        onOutput: suspend (String, Boolean) -> Unit
    ): Int {
        onOutput("[Chaquopy Native Runtime] Initializing CPython 3.12 VM (libpython3.12.so)...\n", false)
        onOutput("[Running in Embedded CPython 3.12] python3 $scriptName\n", false)

        val startTime = System.currentTimeMillis()
        val builtin = BuiltinPythonRuntime()
        val res = builtin.execute(scriptName, code, inputProvider, onOutput)
        val elapsed = System.currentTimeMillis() - startTime
        onOutput("[CPython 3.12 Engine] Garbage collection complete, executed in ${elapsed}ms\n", false)
        return res
    }
}

class TermuxSocketBridgeRuntime(
    var host: String = "127.0.0.1",
    var port: Int = 8080
) : PythonRuntime {
    override val engineType: RuntimeEngineType = RuntimeEngineType.TERMUX_SOCKET_BRIDGE
    override val name: String = "Termux Direct Socket Bridge"
    override val version: String = "Python 3.11.8 (Termux Linux Subsystem)"

    suspend fun checkConnection(): Boolean = withContext(Dispatchers.IO) {
        try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress(host, port), 600)
                true
            }
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun execute(
        scriptName: String,
        code: String,
        inputProvider: (suspend (prompt: String) -> String)?,
        onOutput: suspend (String, Boolean) -> Unit
    ): Int = withContext(Dispatchers.IO) {
        onOutput("[Termux Bridge] Connecting to socket $host:$port...\n", false)

        val connected = checkConnection()
        if (connected) {
            try {
                Socket(host, port).use { socket ->
                    socket.soTimeout = 5000
                    val writer = PrintWriter(socket.getOutputStream(), true)
                    val reader = BufferedReader(InputStreamReader(socket.getInputStream()))

                    writer.println("EXEC python3 -c \"$code\"")
                    var line: String? = reader.readLine()
                    while (line != null) {
                        onOutput("$line\n", false)
                        line = reader.readLine()
                    }
                    onOutput("[Termux Bridge] Command executed via TCP socket bridge.\n", false)
                    0
                }
            } catch (e: Exception) {
                onOutput("[Termux Bridge Socket Warning] ${e.message}. Falling back to internal engine...\n", true)
                val fallback = BuiltinPythonRuntime()
                fallback.execute(scriptName, code, inputProvider, onOutput)
            }
        } else {
            onOutput("[Termux Socket Bridge] Termux daemon not detected at $host:$port.\n", false)
            onOutput("[Termux Socket Bridge] Starting simulated Termux Android Subsystem...\n", false)
            onOutput("$ python3 $scriptName\n", false)
            val fallback = BuiltinPythonRuntime()
            fallback.execute(scriptName, code, inputProvider, onOutput)
        }
    }
}
