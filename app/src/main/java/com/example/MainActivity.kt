package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.DebuggerScreen
import com.example.ui.screens.EditorScreen
import com.example.ui.screens.ExplorerScreen
import com.example.ui.screens.GitScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.NotebookScreen
import com.example.ui.screens.OutputScreen
import com.example.ui.screens.PackagesScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TemplatesScreen
import com.example.ui.screens.TerminalScreen
import com.example.ui.screens.WelcomeScreen
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.PrimaryAccent
import com.example.ui.theme.PythonIdeTheme
import com.example.ui.theme.TextPrimary
import com.example.ui.viewmodel.IdeScreen
import com.example.ui.viewmodel.IdeViewModel
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val ideViewModel: IdeViewModel = viewModel()
            val settings by ideViewModel.settings.collectAsState()
            val currentScreen by ideViewModel.currentScreen.collectAsState()
            val toastMessage by ideViewModel.toastMessage.collectAsState()

            val layoutDirection = if (settings.language.isRtl) {
                LayoutDirection.Rtl
            } else {
                LayoutDirection.Ltr
            }

            PythonIdeTheme {
                CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        when (currentScreen) {
                            is IdeScreen.Welcome -> WelcomeScreen(ideViewModel)
                            is IdeScreen.Home -> HomeScreen(ideViewModel)
                            is IdeScreen.Explorer -> ExplorerScreen(ideViewModel)
                            is IdeScreen.Editor -> EditorScreen(ideViewModel)
                            is IdeScreen.Output -> OutputScreen(ideViewModel)
                            is IdeScreen.Terminal -> TerminalScreen(ideViewModel)
                            is IdeScreen.Packages -> PackagesScreen(ideViewModel)
                            is IdeScreen.Git -> GitScreen(ideViewModel)
                            is IdeScreen.Debugger -> DebuggerScreen(ideViewModel)
                            is IdeScreen.Templates -> TemplatesScreen(ideViewModel)
                            is IdeScreen.Settings -> SettingsScreen(ideViewModel)
                            is IdeScreen.About -> AboutScreen(ideViewModel)
                            is IdeScreen.Notebook -> NotebookScreen(ideViewModel)
                        }

                        // Floating Toast Message
                        AnimatedVisibility(
                            visible = toastMessage != null,
                            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .navigationBarsPadding()
                                .padding(bottom = 75.dp)
                        ) {
                            toastMessage?.let { msg ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(DarkSurfaceElevated)
                                        .padding(horizontal = 20.dp, vertical = 10.dp)
                                ) {
                                    Text(
                                        text = msg,
                                        color = TextPrimary,
                                        fontSize = 13.sp
                                    )
                                }
                                LaunchedEffect(msg) {
                                    delay(2500)
                                    ideViewModel.clearMessage()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
