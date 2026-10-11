package com.grimoire.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grimoire.app.ui.components.DiceRollerBottomSheet
import com.grimoire.app.ui.components.GrimoireTopBar
import com.grimoire.app.ui.screens.*
import com.grimoire.app.ui.theme.GrimoireBg
import com.grimoire.app.ui.theme.GrimoireCard
import com.grimoire.app.ui.theme.GrimoireGold
import com.grimoire.app.ui.theme.GrimoireTheme
import com.grimoire.app.ui.theme.TextMuted
import com.grimoire.app.viewmodel.AppDestination
import com.grimoire.app.viewmodel.GrimoireViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: GrimoireViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            GrimoireTheme {
                GrimoireApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun GrimoireApp(viewModel: GrimoireViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var showDiceSheet by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(GrimoireBg)) {
        val isWideScreen = maxWidth >= 700.dp

        Row(modifier = Modifier.fillMaxSize()) {
            // Navigation Rail for Wide Screens / Tablets
            if (isWideScreen) {
                NavigationRail(
                    containerColor = GrimoireCard,
                    contentColor = GrimoireGold,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    AppDestination.entries.forEach { dest ->
                        NavigationRailItem(
                            selected = uiState.currentDestination == dest,
                            onClick = { viewModel.navigateTo(dest) },
                            icon = { Text(dest.icon, fontSize = 20.sp) },
                            label = { Text(dest.label, fontSize = 10.sp, maxLines = 1) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = GrimoireGold,
                                selectedTextColor = GrimoireGold,
                                unselectedTextColor = TextMuted
                            )
                        )
                    }
                }
            }

            Scaffold(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                containerColor = GrimoireBg,
                topBar = {
                    GrimoireTopBar(
                        uiState = uiState,
                        onOpenDiceRoller = { showDiceSheet = true },
                        onToggleSoundMute = { viewModel.toggleMute() }
                    )
                },
                bottomBar = {
                    if (!isWideScreen) {
                        NavigationBar(
                            containerColor = GrimoireCard,
                            tonalElevation = 6.dp,
                            modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                        ) {
                            AppDestination.entries.forEach { dest ->
                                val isSelected = uiState.currentDestination == dest
                                NavigationBarItem(
                                    selected = isSelected,
                                    onClick = { viewModel.navigateTo(dest) },
                                    icon = { Text(dest.icon, fontSize = 18.sp) },
                                    label = { Text(dest.label, fontSize = 9.sp, maxLines = 1) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = GrimoireGold,
                                        selectedTextColor = GrimoireGold,
                                        unselectedTextColor = TextMuted,
                                        indicatorColor = GrimoireGold.copy(alpha = 0.2f)
                                    )
                                )
                            }
                        }
                    }
                },
                snackbarHost = { SnackbarHost(snackbarHostState) }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    Crossfade(
                        targetState = uiState.currentDestination,
                        label = "ScreenTransition"
                    ) { destination ->
                        when (destination) {
                            AppDestination.VTT -> VttScreen(viewModel = viewModel)
                            AppDestination.COMBAT -> CombatScreen(viewModel = viewModel)
                            AppDestination.GRIMOIRE -> GrimoireScreen(viewModel = viewModel)
                            AppDestination.HERBORISTE -> HerboristeScreen(viewModel = viewModel)
                            AppDestination.TOOLKIT -> ToolkitScreen(viewModel = viewModel)
                            AppDestination.SOUNDSCAPE -> SoundscapeScreen(viewModel = viewModel)
                            AppDestination.PLAYER_HUB -> PlayerHubScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }

        // Dice Roller Bottom Sheet
        if (showDiceSheet) {
            DiceRollerBottomSheet(
                viewModel = viewModel,
                onDismiss = { showDiceSheet = false }
            )
        }
    }
}
