package com.esentiele.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.esentiele.app.data.local.UserPreferences
import com.esentiele.app.ui.components.BottomNavBar
import com.esentiele.app.ui.navigation.EsentieleNavHost
import com.esentiele.app.ui.navigation.Screen
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val userPrefs = UserPreferences(this)
        val isLoggedIn = runBlocking { userPrefs.isLoggedIn.first() }
        val hasOnboarded = runBlocking { userPrefs.hasCompletedOnboarding.first() }

        val startDest = when {
            !isLoggedIn -> Screen.Login.route
            !hasOnboarded -> Screen.Onboarding.route
            else -> Screen.Dashboard.route
        }

        setContent {
            EsentieleTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                // Hide bottom nav on auth/onboarding screens
                val hideBottomBar = currentRoute in listOf(
                    Screen.Login.route, Screen.SignUp.route, Screen.Onboarding.route
                )

                Scaffold(
                    containerColor = Color(0xFF0D0D0D),
                    bottomBar = {
                        if (!hideBottomBar) {
                            BottomNavBar(navController = navController)
                        }
                    }
                ) { innerPadding ->
                    EsentieleNavHost(
                        navController = navController,
                        paddingValues = innerPadding,
                        startDestination = startDest
                    )
                }
            }
        }
    }
}

@Composable
fun EsentieleTheme(content: @Composable () -> Unit) {
    val colorScheme = darkColorScheme(
        primary = Color(0xFFC9A96E),
        background = Color(0xFF0D0D0D),
        surface = Color(0xFF1A1A1A),
        onPrimary = Color.Black,
        onBackground = Color(0xFFF5F0E8),
        onSurface = Color(0xFFF5F0E8)
    )
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
