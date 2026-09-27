package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FindReplace
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.EditorBottomBar
import com.example.ui.components.PythonSyntaxHighlighter
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.EditorGutterBackground
import com.example.ui.theme.EditorGutterText
import com.example.ui.theme.PrimaryAccent
import com.example.ui.theme.RunGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.IdeScreen
import com.example.ui.viewmodel.IdeViewModel

@Composable
fun EditorScreen(viewModel: IdeViewModel) {
    BackHandler {
        viewModel.navigateBack()
    }

    val activeFileName by viewModel.activeFileName.collectAsState()
    val editorCode by viewModel.editorCode.collectAsState()
    val cursorLine by viewModel.cursorLine.collectAsState()
    val cursorCol by viewModel.cursorCol.collectAsState()
    val settings by viewModel.settings.collectAsState()

    var showMenu by remember { mutableStateOf(false) }
    var showSearchReplace by remember { mutableStateOf(false) }
    var searchWord by remember { mutableStateOf("") }
    var replaceWord by remember { mutableStateOf("") }

    // Text field state with selection and cursor tracking
    var textFieldValue by remember(editorCode) {
        mutableStateOf(TextFieldValue(editorCode, TextRange(editorCode.length)))
    }

    val verticalScroll = rememberScrollState()
    val horizontalScroll = rememberScrollState()

    val lines = editorCode.lines()
    val lineCount = lines.size.coerceAtLeast(1)

    Scaffold(
        bottomBar = {
            EditorBottomBar(
                onRunClick = { viewModel.runCurrentFile() },
                onDebugClick = { viewModel.navigateTo(IdeScreen.Debugger) },
                onTerminalClick = { viewModel.navigateTo(IdeScreen.Terminal) }
            )
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(DarkSurface)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.navigateTo(IdeScreen.Explorer) },
                        modifier = Modifier.testTag("editor_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                    Text(
                        text = activeFileName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = TextPrimary
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Search and replace toggle
                    IconButton(
                        onClick = { showSearchReplace = !showSearchReplace },
                        modifier = Modifier.testTag("editor_search_toggle")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Find",
                            tint = TextSecondary
                        )
                    }

                    // Green Run Button
                    IconButton(
                        onClick = { viewModel.runCurrentFile() },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(RunGreen.copy(alpha = 0.15f))
                            .testTag("editor_run_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "Run",
                            tint = RunGreen,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // More Menu
                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.testTag("editor_more_menu")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.MoreVert,
                                contentDescription = "More",
                                tint = TextSecondary
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(viewModel.tr("save")) },
                                onClick = {
                                    showMenu = false
                                    viewModel.saveCurrentFile()
                                },
                                leadingIcon = { Icon(Icons.Filled.Save, contentDescription = null) }
                            )
                            DropdownMenuItem(
                                text = { Text(viewModel.tr("undo")) },
                                onClick = {
                                    showMenu = false
                                    viewModel.undo()
                                },
                                leadingIcon = { Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = null) }
                            )
                            DropdownMenuItem(
                                text = { Text(viewModel.tr("redo")) },
                                onClick = {
                                    showMenu = false
                                    viewModel.redo()
                                },
                                leadingIcon = { Icon(Icons.AutoMirrored.Filled.Redo, contentDescription = null) }
                            )
                        }
                    }
                }
            }

            // Search and Replace Banner
            if (showSearchReplace) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkSurfaceElevated)
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = searchWord,
                            onValueChange = { searchWord = it },
                            placeholder = { Text(viewModel.tr("find"), fontSize = 12.sp) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryAccent,
                                unfocusedBorderColor = DarkBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )
                        OutlinedTextField(
                            value = replaceWord,
                            onValueChange = { replaceWord = it },
                            placeholder = { Text(viewModel.tr("replace"), fontSize = 12.sp) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryAccent,
                                unfocusedBorderColor = DarkBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )
                        IconButton(onClick = { showSearchReplace = false }) {
                            Icon(Icons.Filled.Close, contentDescription = "Close", tint = TextMuted)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                if (searchWord.isNotEmpty()) {
                                    val newText = editorCode.replace(searchWord, replaceWord)
                                    viewModel.updateEditorCode(newText)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryAccent,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(viewModel.tr("replace_all"), fontSize = 12.sp)
                        }
                    }
                }
            }

            // Quick Coding Accessory Bar (Tabs, quotes, brackets, keywords)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(DarkSurface)
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                QuickInsertChip("Tab") {
                    val pos = textFieldValue.selection.start
                    val text = textFieldValue.text
                    val newText = text.substring(0, pos) + "    " + text.substring(pos)
                    viewModel.updateEditorCode(newText)
                }
                QuickInsertChip(":") { insertAtCursor(textFieldValue, ":") { viewModel.updateEditorCode(it) } }
                QuickInsertChip("()") { insertAtCursor(textFieldValue, "()") { viewModel.updateEditorCode(it) } }
                QuickInsertChip("[]") { insertAtCursor(textFieldValue, "[]") { viewModel.updateEditorCode(it) } }
                QuickInsertChip("{}") { insertAtCursor(textFieldValue, "{}") { viewModel.updateEditorCode(it) } }
                QuickInsertChip("\"\"") { insertAtCursor(textFieldValue, "\"\"") { viewModel.updateEditorCode(it) } }
                QuickInsertChip("=") { insertAtCursor(textFieldValue, " = ") { viewModel.updateEditorCode(it) } }
                QuickInsertChip("#") { insertAtCursor(textFieldValue, "# ") { viewModel.updateEditorCode(it) } }
                QuickInsertChip("def") { insertAtCursor(textFieldValue, "def ") { viewModel.updateEditorCode(it) } }
                QuickInsertChip("print") { insertAtCursor(textFieldValue, "print()") { viewModel.updateEditorCode(it) } }
                QuickInsertChip("for") { insertAtCursor(textFieldValue, "for i in range():") { viewModel.updateEditorCode(it) } }
            }

            HorizontalDivider(thickness = 1.dp, color = DarkBorder)

            // Main Code Editor Area
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(DarkBackground)
            ) {
                // Line Numbers Gutter
                if (settings.showLineNumbers) {
                    Column(
                        modifier = Modifier
                            .width(42.dp)
                            .fillMaxHeight()
                            .background(EditorGutterBackground)
                            .verticalScroll(verticalScroll)
                            .padding(vertical = 12.dp, horizontal = 4.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        for (i in 1..lineCount) {
                            Text(
                                text = "$i",
                                color = if (i == cursorLine) PrimaryAccent else EditorGutterText,
                                fontSize = settings.fontSizeSp.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = (settings.fontSizeSp + 6).sp,
                                textAlign = TextAlign.End
                            )
                        }
                    }
                }

                // Code Input Area
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .verticalScroll(verticalScroll)
                        .horizontalScroll(horizontalScroll)
                        .padding(12.dp)
                ) {
                    BasicTextField(
                        value = textFieldValue,
                        onValueChange = { newVal ->
                            textFieldValue = newVal
                            viewModel.updateEditorCode(newVal.text)

                            // Calculate cursor line & col
                            val cursorPos = newVal.selection.start
                            val textBeforeCursor = newVal.text.take(cursorPos)
                            val line = textBeforeCursor.count { it == '\n' } + 1
                            val lastNewline = textBeforeCursor.lastIndexOf('\n')
                            val col = if (lastNewline == -1) cursorPos + 1 else cursorPos - lastNewline
                            viewModel.updateCursorPos(line, col)
                        },
                        textStyle = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = settings.fontSizeSp.sp,
                            lineHeight = (settings.fontSizeSp + 6).sp,
                            color = TextPrimary
                        ),
                        cursorBrush = SolidColor(PrimaryAccent),
                        visualTransformation = {
                            val highlighted = PythonSyntaxHighlighter.highlight(it.text)
                            androidx.compose.ui.text.input.TransformedText(
                                highlighted,
                                androidx.compose.ui.text.input.OffsetMapping.Identity
                            )
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("code_editor_text_field")
                    )
                }
            }

            // Status Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(26.dp)
                    .background(DarkSurface)
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Ln $cursorLine, Col $cursorCol",
                    fontSize = 11.sp,
                    color = TextMuted,
                    fontFamily = FontFamily.Monospace
                )
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "Spaces: ${settings.tabSizeSpaces}",
                        fontSize = 11.sp,
                        color = TextMuted,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Python",
                        fontSize = 11.sp,
                        color = PrimaryAccent,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

private fun insertAtCursor(currentVal: TextFieldValue, textToInsert: String, onUpdate: (String) -> Unit) {
    val pos = currentVal.selection.start
    val text = currentVal.text
    val newText = text.substring(0, pos) + textToInsert + text.substring(pos)
    onUpdate(newText)
}

@Composable
fun QuickInsertChip(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(DarkSurfaceElevated)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Medium
        )
    }
}
