package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CellType
import com.example.data.model.NotebookCell
import com.example.ui.components.PythonSyntaxHighlighter
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.PrimaryAccent
import com.example.ui.theme.PythonYellow
import com.example.ui.theme.RunGreen
import com.example.ui.theme.TerminalBackground
import com.example.ui.theme.TerminalText
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.IdeScreen
import com.example.ui.viewmodel.IdeViewModel

@Composable
fun NotebookScreen(viewModel: IdeViewModel) {
    BackHandler {
        viewModel.navigateBack()
    }

    val notebook by viewModel.activeNotebook.collectAsState()

    Scaffold(
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("notebook_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                    Column {
                        Text(
                            text = notebook?.title?.let { "$it.ipynb" } ?: "Jupyter Notebook",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextPrimary
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(PythonYellow.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 1.dp)
                            ) {
                                Text("Jupyter Kernel: Python 3", color = PythonYellow, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.runAllNotebookCells() },
                        modifier = Modifier.testTag("notebook_run_all")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.FastForward,
                            contentDescription = "Run All",
                            tint = RunGreen
                        )
                    }
                    IconButton(
                        onClick = { viewModel.saveCurrentNotebook() },
                        modifier = Modifier.testTag("notebook_save")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Save,
                            contentDescription = "Save",
                            tint = PrimaryAccent
                        )
                    }
                }
            }

            // Quick Cell Actions (Add Code, Add Markdown)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurfaceElevated)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { viewModel.addNotebookCell(CellType.CODE) },
                    modifier = Modifier.height(34.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccent),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("+ Code Cell", fontSize = 12.sp)
                }

                Button(
                    onClick = { viewModel.addNotebookCell(CellType.MARKDOWN) },
                    modifier = Modifier.height(34.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurface),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("+ Markdown", fontSize = 12.sp, color = TextPrimary)
                }
            }

            HorizontalDivider(thickness = 1.dp, color = DarkBorder)

            // Cells List
            val cells = notebook?.cells ?: emptyList()
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(top = 14.dp, bottom = 40.dp)
            ) {
                itemsIndexed(cells, key = { _, cell -> cell.id }) { index, cell ->
                    NotebookCellCard(
                        cell = cell,
                        cellIndex = index + 1,
                        onRunCell = { viewModel.runNotebookCell(cell) },
                        onUpdateSource = { newSource -> viewModel.updateNotebookCellSource(cell.id, newSource) },
                        onDeleteCell = { viewModel.deleteNotebookCell(cell.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun NotebookCellCard(
    cell: NotebookCell,
    cellIndex: Int,
    onRunCell: () -> Unit,
    onUpdateSource: (String) -> Unit,
    onDeleteCell: () -> Unit
) {
    var isEditingMarkdown by remember { mutableStateOf(cell.source.isEmpty()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurface)
            .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
            .testTag("notebook_cell_$cellIndex")
    ) {
        // Cell Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurfaceElevated)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if (cell.cellType == CellType.CODE) {
                        "[${cell.executionCount ?: " "}]"
                    } else {
                        "Markdown"
                    },
                    color = if (cell.cellType == CellType.CODE) PrimaryAccent else PythonYellow,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Text(
                    text = "Cell #$cellIndex",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (cell.cellType == CellType.CODE) {
                    IconButton(
                        onClick = onRunCell,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "Run Cell",
                            tint = RunGreen,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                } else {
                    IconButton(
                        onClick = { isEditingMarkdown = !isEditingMarkdown },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Edit Markdown",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                IconButton(
                    onClick = onDeleteCell,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete Cell",
                        tint = ErrorRed,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Cell Body
        if (cell.cellType == CellType.CODE) {
            // Code Input
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkBackground)
                    .padding(12.dp)
            ) {
                BasicTextField(
                    value = cell.source,
                    onValueChange = onUpdateSource,
                    textStyle = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
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
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Code Output (if any)
            if (cell.output.isNotEmpty() || cell.isExecuting) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(TerminalBackground)
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Output:",
                        fontSize = 11.sp,
                        color = TextMuted,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (cell.isExecuting) "Executing cell in Python kernel..." else cell.output,
                        color = TerminalText,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                }
            }
        } else {
            // Markdown Display or Edit
            if (isEditingMarkdown) {
                OutlinedTextField(
                    value = cell.source,
                    onValueChange = onUpdateSource,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryAccent,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    placeholder = { Text("Markdown text (e.g. # Title, **bold**)...", fontSize = 12.sp) }
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isEditingMarkdown = true }
                        .padding(14.dp)
                ) {
                    Text(
                        text = cell.source.ifEmpty { "(Empty markdown cell. Tap to edit)" },
                        color = if (cell.source.startsWith("#")) TextPrimary else TextSecondary,
                        fontSize = if (cell.source.startsWith("#")) 17.sp else 14.sp,
                        fontWeight = if (cell.source.startsWith("#")) FontWeight.Bold else FontWeight.Normal,
                        lineHeight = 22.sp
                    )
                }
            }
        }
    }
}
