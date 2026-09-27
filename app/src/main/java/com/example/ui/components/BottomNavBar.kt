package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DebugPurple
import com.example.ui.theme.PrimaryAccent
import com.example.ui.theme.RunGreen
import com.example.ui.theme.SecondaryAccent
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.IdeScreen
import com.example.ui.viewmodel.IdeViewModel

@Composable
fun AppBottomNavBar(
    viewModel: IdeViewModel,
    currentScreen: IdeScreen
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkSurface)
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        HorizontalDivider(thickness = 1.dp, color = DarkBorder)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(
                icon = if (currentScreen is IdeScreen.Home) Icons.Filled.Home else Icons.Outlined.Home,
                label = viewModel.tr("nav_home"),
                isSelected = currentScreen is IdeScreen.Home,
                onClick = { viewModel.navigateTo(IdeScreen.Home) },
                testTag = "nav_home"
            )
            NavItem(
                icon = if (currentScreen is IdeScreen.Explorer) Icons.Filled.Folder else Icons.Outlined.Folder,
                label = viewModel.tr("nav_projects"),
                isSelected = currentScreen is IdeScreen.Explorer,
                onClick = { viewModel.navigateTo(IdeScreen.Explorer) },
                testTag = "nav_projects"
            )
            NavItem(
                icon = if (currentScreen is IdeScreen.Packages) Icons.Filled.Inventory2 else Icons.Outlined.Inventory2,
                label = viewModel.tr("nav_packages"),
                isSelected = currentScreen is IdeScreen.Packages,
                onClick = { viewModel.navigateTo(IdeScreen.Packages) },
                testTag = "nav_packages"
            )
            NavItem(
                icon = if (currentScreen is IdeScreen.Settings) Icons.Filled.Settings else Icons.Outlined.Settings,
                label = viewModel.tr("nav_settings"),
                isSelected = currentScreen is IdeScreen.Settings,
                onClick = { viewModel.navigateTo(IdeScreen.Settings) },
                testTag = "nav_settings"
            )
        }
    }
}

@Composable
private fun NavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val contentColor = if (isSelected) PrimaryAccent else TextSecondary

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            color = contentColor,
            fontSize = 11.sp,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
fun EditorBottomBar(
    onRunClick: () -> Unit,
    onDebugClick: () -> Unit,
    onTerminalClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkSurface)
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        HorizontalDivider(thickness = 1.dp, color = DarkBorder)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Run action
            ActionItem(
                icon = Icons.Filled.PlayArrow,
                label = "Run",
                color = RunGreen,
                onClick = onRunClick,
                testTag = "bottom_run_action"
            )
            // Debug action
            ActionItem(
                icon = Icons.Outlined.BugReport,
                label = "Debug",
                color = DebugPurple,
                onClick = onDebugClick,
                testTag = "bottom_debug_action"
            )
            // Terminal action
            ActionItem(
                icon = Icons.Filled.Terminal,
                label = "Terminal",
                color = SecondaryAccent,
                onClick = onTerminalClick,
                testTag = "bottom_terminal_action"
            )
        }
    }
}

@Composable
private fun ActionItem(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = label,
            color = color,
            fontSize = 13.sp,
            style = MaterialTheme.typography.labelLarge
        )
    }
}
