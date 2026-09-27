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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.execution.RuntimeEngineType
import com.example.ui.components.AppBottomNavBar
import com.example.ui.localization.AppLanguage
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.PrimaryAccent
import com.example.ui.theme.RunGreen
import com.example.ui.theme.SecondaryAccent
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.IdeScreen
import com.example.ui.viewmodel.IdeViewModel

@Composable
fun SettingsScreen(viewModel: IdeViewModel) {
    BackHandler {
        viewModel.navigateBack()
    }

    val settings by viewModel.settings.collectAsState()
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showFontSizeDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }
    var showEngineDialog by remember { mutableStateOf(false) }
    var showTermuxPortDialog by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = { AppBottomNavBar(viewModel = viewModel, currentScreen = IdeScreen.Settings) },
        containerColor = DarkBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 840.dp)
            ) {
                // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(DarkSurface)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateBack() },
                    modifier = Modifier.testTag("settings_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
                Text(
                    text = viewModel.tr("settings_title"),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextPrimary
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
            ) {
                // Section 1: General (عام)
                item {
                    Text(
                        text = viewModel.tr("sec_general"),
                        color = TextMuted,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(DarkSurface)
                            .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
                    ) {
                        SettingsRow(
                            icon = Icons.Filled.DarkMode,
                            title = viewModel.tr("theme_label"),
                            subtitle = viewModel.tr("theme_val"),
                            onClick = {
                                viewModel.showMessage("Dark Mode is active by default for professional developer UI")
                            }
                        )
                        SettingsDivider()
                        SettingsRow(
                            icon = Icons.Filled.Language,
                            title = viewModel.tr("language_label"),
                            subtitle = settings.language.displayName,
                            onClick = { showLanguageDialog = true }
                        )
                        SettingsDivider()
                        SettingsRow(
                            icon = Icons.Filled.Folder,
                            title = viewModel.tr("projects_folder_label"),
                            subtitle = settings.defaultProjectsDirectory,
                            onClick = {
                                viewModel.showMessage("Storage: Local App Sandbox /projects")
                            }
                        )
                        SettingsDivider()
                        SettingsRow(
                            icon = Icons.Filled.Code,
                            title = "Python Runtime Engine",
                            subtitle = settings.activeEngine.displayName,
                            onClick = { showEngineDialog = true }
                        )
                        SettingsDivider()
                        SettingsRow(
                            icon = Icons.Filled.Terminal,
                            title = "Termux Direct Socket Bridge",
                            subtitle = "${settings.termuxHost}:${settings.termuxPort} • ${if (settings.termuxIntegrationEnabled) "Active" else "Disabled"}",
                            onClick = { showTermuxPortDialog = true },
                            showArrow = false,
                            trailing = {
                                Switch(
                                    checked = settings.termuxIntegrationEnabled,
                                    onCheckedChange = { viewModel.setTermuxIntegration(it) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = PrimaryAccent,
                                        uncheckedThumbColor = TextMuted,
                                        uncheckedTrackColor = DarkSurfaceElevated
                                    )
                                )
                            }
                        )
                        SettingsDivider()
                        SettingsRow(
                            icon = Icons.Filled.Folder,
                            title = "Git Backend Core",
                            subtitle = settings.gitBackend,
                            onClick = { viewModel.showMessage("Git Core: Native Libgit2 / JGit Integration") }
                        )
                        SettingsDivider()
                        SettingsRow(
                            icon = Icons.Filled.Code,
                            title = "Jupyter Notebook Support",
                            subtitle = "Interactive .ipynb Mobile Runner & Kernel",
                            onClick = { viewModel.showMessage("Jupyter Notebook: Kernel Ready") }
                        )
                    }
                }

                // Section 2: Editor (محرر الأكواد)
                item {
                    Text(
                        text = viewModel.tr("sec_editor"),
                        color = TextMuted,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 10.dp, bottom = 6.dp)
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(DarkSurface)
                            .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
                    ) {
                        SettingsRow(
                            icon = Icons.Filled.Code,
                            title = viewModel.tr("font_size_label"),
                            subtitle = "${settings.fontSizeSp} sp",
                            onClick = { showFontSizeDialog = true }
                        )
                        SettingsDivider()
                        SettingsRow(
                            icon = Icons.Filled.Code,
                            title = viewModel.tr("line_numbers_label"),
                            subtitle = if (settings.showLineNumbers) "Enabled" else "Disabled",
                            showArrow = false,
                            trailing = {
                                Switch(
                                    checked = settings.showLineNumbers,
                                    onCheckedChange = { viewModel.setLineNumbers(it) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.Black,
                                        checkedTrackColor = PrimaryAccent
                                    )
                                )
                            }
                        )
                        SettingsDivider()
                        SettingsRow(
                            icon = Icons.Filled.Code,
                            title = viewModel.tr("auto_save_label"),
                            subtitle = if (settings.autoSave) "Enabled" else "Disabled",
                            showArrow = false,
                            trailing = {
                                Switch(
                                    checked = settings.autoSave,
                                    onCheckedChange = { viewModel.setAutoSave(it) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.Black,
                                        checkedTrackColor = PrimaryAccent
                                    )
                                )
                            }
                        )
                    }
                }

                // Section 3: Other (أخرى)
                item {
                    Text(
                        text = viewModel.tr("sec_other"),
                        color = TextMuted,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 10.dp, bottom = 6.dp)
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(DarkSurface)
                            .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
                    ) {
                        SettingsRow(
                            icon = Icons.Filled.Info,
                            title = viewModel.tr("about_label"),
                            subtitle = "Python IDE v1.0 • Modern Mobile Developer Suite",
                            onClick = { viewModel.navigateTo(IdeScreen.About) },
                            testTag = "settings_about_row"
                        )
                        SettingsDivider()
                        SettingsRow(
                            icon = Icons.Filled.HelpOutline,
                            title = viewModel.tr("help_label"),
                            subtitle = "Documentation, tutorials & community",
                            onClick = { showHelpDialog = true }
                        )
                    }
                }
            }
        }
    }
}

    // Language Toggle Dialog
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text(viewModel.tr("language_label"), color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    AppLanguage.values().forEach { lang ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (lang == settings.language) PrimaryAccent.copy(alpha = 0.2f) else DarkSurface)
                                .clickable {
                                    viewModel.setLanguage(lang)
                                    showLanguageDialog = false
                                }
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = lang.displayName,
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = if (lang == settings.language) FontWeight.Bold else FontWeight.Normal
                            )
                            if (lang == settings.language) {
                                Text("✓", color = PrimaryAccent, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text("Close", color = PrimaryAccent)
                }
            },
            containerColor = DarkSurfaceElevated
        )
    }

    // Font Size Dialog
    if (showFontSizeDialog) {
        var tempSize by remember { mutableStateOf(settings.fontSizeSp.toFloat()) }
        AlertDialog(
            onDismissRequest = { showFontSizeDialog = false },
            title = { Text(viewModel.tr("font_size_label"), color = TextPrimary) },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${tempSize.toInt()} sp",
                        color = PrimaryAccent,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Slider(
                        value = tempSize,
                        onValueChange = { tempSize = it },
                        valueRange = 10f..26f,
                        steps = 7,
                        colors = SliderDefaults.colors(
                            thumbColor = PrimaryAccent,
                            activeTrackColor = PrimaryAccent
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "def hello_world():\n    print('Code Preview')",
                        fontFamily = FontFamily.Monospace,
                        fontSize = tempSize.sp,
                        color = TextSecondary
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.setFontSize(tempSize.toInt())
                        showFontSizeDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccent)
                ) {
                    Text("Apply")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFontSizeDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = DarkSurfaceElevated
        )
    }

    // Help Dialog
    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            title = { Text(viewModel.tr("help_label"), color = TextPrimary) },
            text = {
                Text(
                    text = "Python IDE provides a complete mobile programming environment.\n\n• Code Editor: Edit Python files with full syntax highlighting\n• Run / Output: Execute Python scripts directly\n• Terminal: Interactive shell & command runner\n• Packages: Install pip packages\n• Git: Version control and branching\n• Debugger: Inspect variables & step through code\n\nNeed support? Check out github.com or reach out via settings.",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { showHelpDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccent)
                ) {
                    Text("OK")
                }
            },
            containerColor = DarkSurfaceElevated
        )
    }

    // Python Runtime Engine Dialog (Chaquopy, Termux Bridge, Built-in)
    if (showEngineDialog) {
        AlertDialog(
            onDismissRequest = { showEngineDialog = false },
            title = { Text("Select Python Execution Engine", color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    RuntimeEngineType.values().forEach { engine ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (engine == settings.activeEngine) PrimaryAccent.copy(alpha = 0.2f) else DarkSurface)
                                .border(1.dp, if (engine == settings.activeEngine) PrimaryAccent else DarkBorder, RoundedCornerShape(10.dp))
                                .clickable {
                                    viewModel.setRuntimeEngine(engine)
                                    showEngineDialog = false
                                }
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = engine.displayName,
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = engine.versionString,
                                    color = TextMuted,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            if (engine == settings.activeEngine) {
                                Text("✓", color = PrimaryAccent, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showEngineDialog = false }) {
                    Text("Close", color = PrimaryAccent)
                }
            },
            containerColor = DarkSurfaceElevated
        )
    }

    // Termux Socket Port Dialog
    if (showTermuxPortDialog) {
        var tempPort by remember { mutableStateOf(settings.termuxPort.toString()) }
        AlertDialog(
            onDismissRequest = { showTermuxPortDialog = false },
            title = { Text("Termux Direct Socket Bridge", color = TextPrimary) },
            text = {
                Column {
                    Text(
                        text = "Configures direct TCP bridge to Termux runtime running on localhost (${settings.termuxHost}).",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = tempPort,
                        onValueChange = { tempPort = it },
                        label = { Text("TCP Port (e.g. 8080 or 8022)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryAccent,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val p = tempPort.toIntOrNull() ?: 8080
                        viewModel.setTermuxPort(p)
                        showTermuxPortDialog = false
                        viewModel.showMessage("Termux bridge configured to port $p")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccent)
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTermuxPortDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = DarkSurfaceElevated
        )
    }
}

@Composable
fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: (() -> Unit)? = null,
    showArrow: Boolean = true,
    trailing: (@Composable () -> Unit)? = null,
    testTag: String = ""
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .then(if (testTag.isNotEmpty()) Modifier.testTag(testTag) else Modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PrimaryAccent,
                modifier = Modifier.size(22.dp)
            )

            Column {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                if (subtitle.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
            }
        }

        if (trailing != null) {
            trailing()
        } else if (showArrow) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(13.dp)
            )
        }
    }
}

@Composable
fun SettingsDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(DarkBorder)
    )
}
