package com.example.ui.components

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import com.example.ui.theme.SyntaxBuiltin
import com.example.ui.theme.SyntaxComment
import com.example.ui.theme.SyntaxFunction
import com.example.ui.theme.SyntaxKeyword
import com.example.ui.theme.SyntaxNumber
import com.example.ui.theme.SyntaxString
import com.example.ui.theme.TextPrimary

object PythonSyntaxHighlighter {

    private val KEYWORDS = setOf(
        "def", "class", "return", "if", "elif", "else", "while", "for", "in",
        "try", "except", "finally", "raise", "import", "from", "as", "with",
        "pass", "break", "continue", "lambda", "yield", "global", "nonlocal",
        "assert", "async", "await", "and", "or", "not", "is", "True", "False", "None"
    )

    private val BUILTINS = setOf(
        "print", "len", "range", "input", "int", "str", "float", "list", "dict",
        "set", "tuple", "bool", "enumerate", "zip", "min", "max", "sum",
        "open", "type", "isinstance", "help", "iter", "next", "super", "format"
    )

    fun highlight(code: String): AnnotatedString {
        return buildAnnotatedString {
            append(code)

            var i = 0
            val len = code.length

            while (i < len) {
                val c = code[i]

                // Comments: # until end of line
                if (c == '#') {
                    val lineEnd = code.indexOf('\n', i).let { if (it == -1) len else it }
                    addStyle(SpanStyle(color = SyntaxComment), i, lineEnd)
                    i = lineEnd
                    continue
                }

                // Strings: '...', "...", f"...", f'...'
                if (c == '"' || c == '\'') {
                    val quote = c
                    val start = i
                    i++
                    var escaped = false
                    while (i < len) {
                        val ch = code[i]
                        if (ch == '\\' && !escaped) {
                            escaped = true
                            i++
                            continue
                        }
                        if (ch == quote && !escaped) {
                            i++
                            break
                        }
                        escaped = false
                        if (ch == '\n') break
                        i++
                    }
                    addStyle(SpanStyle(color = SyntaxString), start, i)
                    continue
                }

                // Numbers: digits
                if (c.isDigit()) {
                    val start = i
                    while (i < len && (code[i].isDigit() || code[i] == '.' || code[i] == 'x' || code[i] == 'b' || code[i] in 'a'..'f' || code[i] in 'A'..'F')) {
                        i++
                    }
                    addStyle(SpanStyle(color = SyntaxNumber), start, i)
                    continue
                }

                // Words: identifiers, keywords, builtins
                if (c.isLetter() || c == '_') {
                    val start = i
                    while (i < len && (code[i].isLetterOrDigit() || code[i] == '_')) {
                        i++
                    }
                    val word = code.substring(start, i)

                    when {
                        KEYWORDS.contains(word) -> {
                            addStyle(SpanStyle(color = SyntaxKeyword), start, i)
                        }
                        BUILTINS.contains(word) -> {
                            addStyle(SpanStyle(color = SyntaxBuiltin), start, i)
                        }
                        // Check if function definition: def function_name
                        start > 4 && code.substring(0, start).trimEnd().endsWith("def") -> {
                            addStyle(SpanStyle(color = SyntaxFunction), start, i)
                        }
                        else -> {
                            addStyle(SpanStyle(color = TextPrimary), start, i)
                        }
                    }
                    continue
                }

                i++
            }
        }
    }
}
