package com.esentiele.app.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.esentiele.app.ui.auth.LoginScreen
import com.esentiele.app.ui.auth.SignUpScreen
import com.esentiele.app.ui.battle.BattleScreen
import com.esentiele.app.ui.calendar.CalendarScreen
import com.esentiele.app.ui.color.ColorAnalysisScreen
import com.esentiele.app.ui.dashboard.DashboardScreen
import com.esentiele.app.ui.glowup.GlowUpScreen
import com.esentiele.app.ui.insights.ClosetInsightsScreen
import com.esentiele.app.ui.itemfinder.ItemFinderScreen
import com.esentiele.app.ui.lookbook.LookbookExporterScreen
import com.esentiele.app.ui.maskeditor.MaskEditorScreen
import com.esentiele.app.ui.nfc.NfcTagScreen
import com.esentiele.app.ui.onboarding.OnboardingScreen
import com.esentiele.app.ui.outfitcheck.OutfitCheckScreen
import com.esentiele.app.ui.profile.ProfileScreen
import com.esentiele.app.ui.stylist.StylistScreen
import com.esentiele.app.ui.travel.TravelPackingScreen
import com.esentiele.app.ui.wardrobe.WardrobeScreen

@Composable
fun EsentieleNavHost(
    navController: NavHostController,
    paddingValues: PaddingValues,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier.padding(paddingValues),
        enterTransition = { fadeIn(tween(300)) + slideInHorizontally(tween(300)) { it / 4 } },
        exitTransition = { fadeOut(tween(200)) },
        popEnterTransition = { fadeIn(tween(300)) + slideInHorizontally(tween(300)) { -it / 4 } },
        popExitTransition = { fadeOut(tween(200)) }
    ) {
        // Auth screens
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToSignUp = {
                    navController.navigate(Screen.SignUp.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.SignUp.route) {
            SignUpScreen(
                onNavigateToLogin = { navController.popBackStack() },
                onSignUpSuccess = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Onboarding.route) {
            OnboardingScreen(onFinish = {
                navController.navigate(Screen.Dashboard.route) {
                    popUpTo(Screen.Onboarding.route) { inclusive = true }
                }
            })
        }

        // Main screens — all receive navController
        composable(Screen.Dashboard.route) { DashboardScreen(navController = navController) }
        composable(Screen.Wardrobe.route) { WardrobeScreen(navController = navController) }
        composable(Screen.Stylist.route) { StylistScreen() }
        composable(Screen.OutfitCheck.route) { OutfitCheckScreen() }
        composable(Screen.GlowUp.route) { GlowUpScreen() }
        composable(Screen.Battle.route) { BattleScreen() }
        composable(Screen.ColorAnalysis.route) { ColorAnalysisScreen() }
        composable(Screen.ItemFinder.route) { ItemFinderScreen() }
        composable(Screen.Profile.route) { ProfileScreen(navController = navController) }

        // Atelier Suite screens
        composable(Screen.MaskEditor.route) { MaskEditorScreen() }
        composable(Screen.TravelPacking.route) { TravelPackingScreen() }
        composable(Screen.ClosetInsights.route) { ClosetInsightsScreen() }
        composable(Screen.NfcTags.route) { NfcTagScreen() }
        composable(Screen.OutfitCalendar.route) { CalendarScreen() }
        composable(Screen.LookbookExporter.route) { LookbookExporterScreen() }
    }
}
