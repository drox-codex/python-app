package com.example.data.execution

import com.example.data.repository.PackageManagerRepository
import com.example.data.repository.ProjectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

data class TerminalLine(
    val text: String,
    val isPrompt: Boolean = false,
    val isError: Boolean = false,
    val isSuccess: Boolean = false
)

class TerminalSession(
    private val projectRepository: ProjectRepository,
    private val packageRepository: PackageManagerRepository,
    private val pythonRuntime: PythonRuntime = BuiltinPythonRuntime()
) {
    private val _lines = MutableStateFlow<List<TerminalLine>>(
        listOf(
            TerminalLine("$ python --version", isPrompt = true),
            TerminalLine("Python 3.11.4"),
            TerminalLine(""),
            TerminalLine("$ pip install requests", isPrompt = true),
            TerminalLine("Collecting requests"),
            TerminalLine("Downloading requests-2.31.0..."),
            TerminalLine("Installing collected packages: requests"),
            TerminalLine("Successfully installed requests-2.31.0", isSuccess = true),
            TerminalLine(""),
            TerminalLine("$ python main.py", isPrompt = true),
            TerminalLine("ما اسمك؟ Ahmed"),
            TerminalLine("مرحباً Ahmed"),
            TerminalLine("")
        )
    )
    val lines: StateFlow<List<TerminalLine>> = _lines.asStateFlow()

    private var currentDirectory: String = "/storage/emulated/0/MyProject"
    private val commandHistory = mutableListOf<String>()

    fun setWorkingDirectory(path: String) {
        currentDirectory = path
    }

    suspend fun executeCommand(commandStr: String) {
        val trimmed = commandStr.trim()
        if (trimmed.isEmpty()) return

        commandHistory.add(trimmed)
        appendLine(TerminalLine("$ $trimmed", isPrompt = true))

        val parts = trimmed.split(Regex("\\s+"))
        val cmd = parts[0]
        val args = parts.drop(1)

        when (cmd) {
            "clear" -> {
                _lines.value = emptyList()
            }
            "python", "python3" -> {
                if (args.isEmpty() || args[0] == "-i") {
                    appendLine(TerminalLine("Python 3.11.4 (default, Jun 2024)\nType \"help\", \"copyright\" or \"license\" for more information."))
                } else if (args[0] == "--version" || args[0] == "-V") {
                    appendLine(TerminalLine("Python 3.11.4"))
                } else {
                    val fileName = args[0]
                    val project = projectRepository.getProjects().firstOrNull { it.directoryPath == currentDirectory }
                    val scriptFile = File(currentDirectory, fileName)
                    if (scriptFile.exists()) {
                        val content = scriptFile.readText()
                        pythonRuntime.execute(
                            scriptName = fileName,
                            code = content,
                            inputProvider = { "Ahmed" },
                            onOutput = { text, isErr ->
                                appendLine(TerminalLine(text.trimEnd(), isError = isErr))
                            }
                        )
                    } else {
                        appendLine(TerminalLine("python: can't open file '$fileName': [Errno 2] No such file or directory", isError = true))
                    }
                }
            }
            "pip", "pip3" -> {
                if (args.isEmpty()) {
                    appendLine(TerminalLine("Usage: pip <command> [options]\nCommands: install, list, show, uninstall"))
                } else when (args[0]) {
                    "install" -> {
                        if (args.size > 1) {
                            val pkg = args[1]
                            appendLine(TerminalLine("Collecting $pkg"))
                            appendLine(TerminalLine("Downloading $pkg..."))
                            appendLine(TerminalLine("Installing collected packages: $pkg"))
                            packageRepository.installPackage(pkg)
                            appendLine(TerminalLine("Successfully installed $pkg", isSuccess = true))
                        } else {
                            appendLine(TerminalLine("ERROR: You must give at least one requirement to install.", isError = true))
                        }
                    }
                    "list" -> {
                        appendLine(TerminalLine("Package          Version"))
                        appendLine(TerminalLine("---------------- -------"))
                        packageRepository.getInstalledPackages().forEach {
                            appendLine(TerminalLine(String.format("%-16s %s", it.name, it.version)))
                        }
                    }
                    "uninstall" -> {
                        if (args.size > 1) {
                            val pkg = args[1]
                            packageRepository.uninstallPackage(pkg)
                            appendLine(TerminalLine("Successfully uninstalled $pkg", isSuccess = true))
                        }
                    }
                    else -> appendLine(TerminalLine("pip: unknown command '${args[0]}'", isError = true))
                }
            }
            "ls" -> {
                val files = projectRepository.getFiles(currentDirectory)
                if (files.isEmpty()) {
                    appendLine(TerminalLine("(empty directory)"))
                } else {
                    val formatted = files.joinToString("   ") { if (it.isDirectory) "${it.name}/" else it.name }
                    appendLine(TerminalLine(formatted))
                }
            }
            "pwd" -> {
                appendLine(TerminalLine(currentDirectory))
            }
            "cat" -> {
                if (args.isNotEmpty()) {
                    val file = File(currentDirectory, args[0])
                    if (file.exists() && file.isFile) {
                        appendLine(TerminalLine(file.readText()))
                    } else {
                        appendLine(TerminalLine("cat: ${args[0]}: No such file or directory", isError = true))
                    }
                }
            }
            "echo" -> {
                appendLine(TerminalLine(args.joinToString(" ")))
            }
            "help" -> {
                appendLine(TerminalLine("Available built-in commands:"))
                appendLine(TerminalLine("  python <file.py>    Execute Python script"))
                appendLine(TerminalLine("  python --version    Show Python version"))
                appendLine(TerminalLine("  pip install <pkg>   Install package"))
                appendLine(TerminalLine("  pip list            List installed packages"))
                appendLine(TerminalLine("  ls, pwd, cat        File exploration"))
                appendLine(TerminalLine("  clear               Clear console"))
                appendLine(TerminalLine("Note: Connect Termux Backend in Settings for full native Linux shell commands."))
            }
            else -> {
                appendLine(TerminalLine("bash: $cmd: command not found.", isError = true))
                appendLine(TerminalLine("Hint: For full native shell, configure Termux integration in Settings.", isError = false))
            }
        }
    }

    private fun appendLine(line: TerminalLine) {
        val current = _lines.value.toMutableList()
        current.add(line)
        // Keep max 500 lines
        if (current.size > 500) {
            current.removeAt(0)
        }
        _lines.value = current
    }

    fun clear() {
        _lines.value = emptyList()
    }
}
