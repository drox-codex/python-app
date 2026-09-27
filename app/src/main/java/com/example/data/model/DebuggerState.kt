package com.example.data.model

data class DebugVariable(
    val name: String,
    val value: String,
    val type: String
)

data class Breakpoint(
    val fileName: String,
    val lineNumber: Int,
    val isEnabled: Boolean = true
)

data class StackFrame(
    val functionName: String,
    val fileName: String,
    val lineNumber: Int
)

data class DebuggerSession(
    val isRunning: Boolean = false,
    val isPaused: Boolean = true,
    val currentLine: Int = 4,
    val currentFile: String = "main.py",
    val variables: List<DebugVariable> = listOf(
        DebugVariable("x", "10", "int"),
        DebugVariable("y", "20", "int"),
        DebugVariable("z", "30", "int"),
        DebugVariable("name", "\"Ahmed\"", "str"),
        DebugVariable("status", "True", "bool")
    ),
    val breakpoints: List<Breakpoint> = listOf(
        Breakpoint("main.py", 4),
        Breakpoint("main.py", 6),
        Breakpoint("utils.py", 12)
    ),
    val callStack: List<StackFrame> = listOf(
        StackFrame("<module>", "main.py", 4),
        StackFrame("process_data()", "utils.py", 18),
        StackFrame("calculate()", "utils.py", 25)
    )
)
