package com.nutritrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nutritrack.ui.screens.FoodIntakeQuestionnaireScreen
import com.nutritrack.ui.screens.HomeScreen
import com.nutritrack.ui.screens.InsightsScreen
import com.nutritrack.ui.screens.LoginScreen
import com.nutritrack.ui.screens.WelcomeScreen
import com.nutritrack.ui.theme.NutriTrackTheme
import com.nutritrack.viewModel.MainViewModel
import com.nutritrack.ui.screens.RegisterScreen
import com.nutritrack.ui.screens.NutriCoachScreen
import com.nutritrack.ui.screens.ClinicianLoginScreen
import com.nutritrack.ui.screens.ClinicianDashboardScreen
import com.nutritrack.ui.screens.SettingsScreen

class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels {
        MainViewModel.MainViewModelFactory(
            (application as NutriTrackApplication).repository,
            application
        )
    }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AuthManager.init(applicationContext)

        setContent {
            NutriTrackTheme {
                val windowSizeClass = calculateWindowSizeClass(activity = this)
                NutriTrackApp(mainViewModel, windowSizeClass, navController = rememberNavController())
            }
        }
    }
}

@Composable
fun NutriTrackApp(
    mainViewModel: MainViewModel,
    windowSizeClass: WindowSizeClass,
    navController: NavHostController
) {
    LaunchedEffect(Unit) {
        mainViewModel.performInitialDatabaseSeed()
    }

    val startDestination = if (AuthManager.isLoggedIn()) "home" else "welcome"

    NavHost(navController, startDestination = startDestination) {
        composable("welcome") {
            WelcomeScreen(
                onNavigateToLogin = { navController.navigate("login") },
                windowSizeClass = windowSizeClass
            )
        }
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("welcome") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }
        composable("register") {
            RegisterScreen(
                navController = navController,
                onNavigateToLogin = { navController.popBackStack() }
            )
        }
        composable("questionnaire") {
            if (!AuthManager.isLoggedIn()) {
                navController.navigate("login") { popUpTo("questionnaire") { inclusive = true } }
            } else {
                FoodIntakeQuestionnaireScreen(
                    onBack = { navController.popBackStack() },
                    onSaveComplete = {
                        navController.navigate("home") {
                            popUpTo("questionnaire") { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
        composable("home") {
            if (!AuthManager.isLoggedIn()) {
                navController.navigate("login") { popUpTo("home") { inclusive = true } }
            } else {
                HomeScreen(
                    navController = navController,
                    windowSizeClass = windowSizeClass,
                    userName = AuthManager.currentUserName ?: "User",
                    userId = AuthManager.currentUserId ?: "",
                    phoneNumber = AuthManager.currentUserPhone ?: ""
                )
            }
        }
        composable("insights") {
            if (!AuthManager.isLoggedIn()) {
                navController.navigate("login") { popUpTo("insights") { inclusive = true } }
            } else {
                InsightsScreen(
                    navController = navController,
                    userId = AuthManager.currentUserId ?: "",
                    phoneNumber = AuthManager.currentUserPhone ?: ""
                )
            }
        }
        composable("settings") {
            if (!AuthManager.isLoggedIn()) {
                navController.navigate("login") { popUpTo("settings") { inclusive = true } }
            } else {
                SettingsScreen(navController = navController)
            }
        }
        composable("nutricoach") {
            if (!AuthManager.isLoggedIn()) {
                navController.navigate("login") { popUpTo("nutricoach") { inclusive = true } }
            } else {
                NutriCoachScreen(
                    navController = navController,
                    userId = AuthManager.currentUserId ?: ""
                )
            }
        }
        composable("clinician_login") {
            ClinicianLoginScreen(navController = navController)
        }
        composable("clinician_dashboard") {
            ClinicianDashboardScreen(navController = navController)
        }
    }
}