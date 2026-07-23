package com.esentiele.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.FactCheck
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector?) {
    object Login : Screen("login", "Login", null)
    object SignUp : Screen("signup", "Sign Up", null)
    object Onboarding : Screen("onboarding", "Onboarding", null)
    object Dashboard : Screen("dashboard", "Home", Icons.Outlined.Home)
    object Wardrobe : Screen("wardrobe", "Closet", Icons.Outlined.Checkroom)
    object Stylist : Screen("stylist", "Stylist", Icons.Outlined.AutoAwesome)
    object OutfitCheck : Screen("outfit_check", "Check", Icons.AutoMirrored.Outlined.FactCheck)
    object GlowUp : Screen("glow_up", "Glow Up", Icons.Outlined.Face)
    object Battle : Screen("battle", "Battle", Icons.Outlined.Compare)
    object ColorAnalysis : Screen("color_analysis", "Colors", Icons.Outlined.Palette)
    object ItemFinder : Screen("item_finder", "Finder", Icons.Outlined.Search)
    object Profile : Screen("profile", "Profile", Icons.Outlined.Person)
    object MaskEditor : Screen("mask_editor", "Clean Up", Icons.Outlined.CleaningServices)
    object TravelPacking : Screen("travel_packing", "Travel", Icons.Outlined.FlightTakeoff)
    object ClosetInsights : Screen("closet_insights", "Insights", Icons.Outlined.Insights)
    object NfcTags : Screen("nfc_tags", "Tags", Icons.Outlined.Nfc)
    object OutfitCalendar : Screen("outfit_calendar", "Calendar", Icons.Outlined.CalendarMonth)
    object LookbookExporter : Screen("lookbook_exporter", "Lookbook", Icons.Outlined.Collections)
}
