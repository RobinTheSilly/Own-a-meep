package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.ThoughtType
import com.example.ui.screens.AccountDialog
import com.example.ui.screens.CinemaScreen
import com.example.ui.screens.CookingScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MeepManagerDialog
import com.example.ui.screens.ParkScreen
import com.example.ui.screens.ShopScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MeepViewModel

enum class AppScreen {
    HOME,
    KITCHEN,
    SHOP,
    PARK,
    CINEMA
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppContent(
                    onExitApp = { finish() }
                )
            }
        }
    }
}

@Composable
fun MainAppContent(
    viewModel: MeepViewModel = viewModel(),
    onExitApp: () -> Unit
) {
    // Request notification permission on Android 13+
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    var currentScreen by remember { mutableStateOf(AppScreen.HOME) }
    var showMeepManager by remember { mutableStateOf(false) }
    var showAccountDialog by remember { mutableStateOf(false) }

    val activeMeep by viewModel.activeMeep.collectAsStateWithLifecycle()
    val allMeeps by viewModel.allMeeps.collectAsStateWithLifecycle()
    val userAccount by viewModel.userAccount.collectAsStateWithLifecycle()
    val ownedItems by viewModel.ownedItems.collectAsStateWithLifecycle()
    val timeOfDay by viewModel.timeOfDay.collectAsStateWithLifecycle()
    val currentThought by viewModel.currentThought.collectAsStateWithLifecycle()
    val isWalking by viewModel.isWalking.collectAsStateWithLifecycle()
    val speechText by viewModel.speechText.collectAsStateWithLifecycle()
    val parkMeeps by viewModel.parkMeeps.collectAsStateWithLifecycle()
    val activeParkInteraction by viewModel.activeParkInteraction.collectAsStateWithLifecycle()

    Surface(modifier = Modifier.fillMaxSize()) {
        when (currentScreen) {
            AppScreen.HOME -> {
                HomeScreen(
                    activeMeep = activeMeep,
                    allMeeps = allMeeps,
                    userAccount = userAccount,
                    timeOfDay = timeOfDay,
                    currentThought = currentThought,
                    speechText = speechText,
                    isWalking = isWalking,
                    onPetMeep = { viewModel.petMeep() },
                    onToggleSleep = { viewModel.toggleSleep() },
                    onMoveMeep = { x, y -> viewModel.setMeepPosition(x, y) },
                    onThoughtClick = { thought ->
                        when (thought) {
                            ThoughtType.CRAVE_BREAKFAST,
                            ThoughtType.CRAVE_LUNCH,
                            ThoughtType.CRAVE_DINNER -> currentScreen = AppScreen.KITCHEN
                            ThoughtType.CRAVE_SLEEP -> viewModel.toggleSleep()
                            ThoughtType.CRAVE_MOVIE -> currentScreen = AppScreen.CINEMA
                            ThoughtType.CRAVE_PARK -> currentScreen = AppScreen.PARK
                            ThoughtType.CRAVE_PLAY,
                            ThoughtType.CRAVE_HUG -> viewModel.petMeep()
                        }
                    },
                    onNavigateKitchen = { currentScreen = AppScreen.KITCHEN },
                    onNavigateShop = { currentScreen = AppScreen.SHOP },
                    onNavigatePark = { currentScreen = AppScreen.PARK },
                    onNavigateCinema = { currentScreen = AppScreen.CINEMA },
                    onOpenMeepManager = { showMeepManager = true },
                    onOpenAccount = { showAccountDialog = true },
                    onSaveAndExit = {
                        viewModel.saveAndLeave {
                            onExitApp()
                        }
                    }
                )
            }

            AppScreen.KITCHEN -> {
                BackHandler { currentScreen = AppScreen.HOME }
                CookingScreen(
                    activeMeep = activeMeep,
                    timeOfDay = timeOfDay,
                    onBack = { currentScreen = AppScreen.HOME },
                    onFeedMeal = { recipe -> viewModel.feedMeepWithRecipe(recipe) }
                )
            }

            AppScreen.SHOP -> {
                BackHandler { currentScreen = AppScreen.HOME }
                ShopScreen(
                    activeMeep = activeMeep,
                    meepPoints = userAccount?.meepPoints ?: 0,
                    ownedItems = ownedItems,
                    onBack = { currentScreen = AppScreen.HOME },
                    onEquip = { category, itemId -> viewModel.equipAccessory(category, itemId) },
                    onBuy = { item, callback -> viewModel.buyItem(item, callback) }
                )
            }

            AppScreen.PARK -> {
                BackHandler { currentScreen = AppScreen.HOME }
                ParkScreen(
                    activeMeep = activeMeep,
                    parkMeeps = parkMeeps,
                    meepPoints = userAccount?.meepPoints ?: 0,
                    activeBannerText = activeParkInteraction,
                    onBack = { currentScreen = AppScreen.HOME },
                    onInteract = { targetMeep, interaction, callback ->
                        viewModel.interactInPark(targetMeep, interaction, callback)
                    },
                    onSendEmote = { emote ->
                        viewModel.sayMeep(emote)
                    }
                )
            }

            AppScreen.CINEMA -> {
                BackHandler { currentScreen = AppScreen.HOME }
                CinemaScreen(
                    activeMeep = activeMeep,
                    onBack = { currentScreen = AppScreen.HOME },
                    onWatchMovie = { movie ->
                        viewModel.watchMovie(movie) {}
                    },
                    onFeedPopcorn = {
                        viewModel.feedQuickSnack()
                    }
                )
            }
        }
    }

    if (showMeepManager) {
        MeepManagerDialog(
            allMeeps = allMeeps,
            activeMeepId = activeMeep?.id,
            onDismiss = { showMeepManager = false },
            onSelectMeep = { id -> viewModel.switchMeep(id) },
            onCreateMeep = { name, colorHex, personality ->
                viewModel.createMeep(name, colorHex, personality) {}
            },
            onDeleteMeep = { id -> viewModel.deleteMeep(id) }
        )
    }

    if (showAccountDialog) {
        AccountDialog(
            currentEmail = userAccount?.email ?: "",
            currentUsername = userAccount?.username ?: "",
            isOnline = userAccount?.isOnline == true,
            onDismiss = { showAccountDialog = false },
            onSignIn = { email, username ->
                viewModel.signInAccount(email, username)
            }
        )
    }
}
