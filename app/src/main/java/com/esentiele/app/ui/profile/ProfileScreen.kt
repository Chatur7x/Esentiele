package com.esentiele.app.ui.profile

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.esentiele.app.data.local.EsentieleDatabase
import com.esentiele.app.data.local.UserPreferences
import com.esentiele.app.ui.navigation.Screen
import kotlinx.coroutines.launch

private val ChampagneGold = Color(0xFFC9A96E)
private val DarkSurface = Color(0xFF1A1A1A)
private val DarkBg = Color(0xFF0D0D0D)
private val IvoryText = Color(0xFFF5F0E8)

@Composable
fun ProfileScreen(navController: NavHostController) {
    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }
    val db = remember { EsentieleDatabase.getInstance(context) }
    val scope = rememberCoroutineScope()

    val userName by userPrefs.userName.collectAsState(initial = "")
    val userEmail by userPrefs.userEmail.collectAsState(initial = "")
    val colorSeason by userPrefs.colorSeason.collectAsState(initial = "")
    val stylePrefs by userPrefs.stylePreferences.collectAsState(initial = "")
    val profileInitial by userPrefs.profileInitial.collectAsState(initial = "E")
    val budget by userPrefs.monthlyBudget.collectAsState(initial = 5000)

    val allItems by db.clothingDao().getAll().collectAsState(initial = emptyList())
    val outfits by db.outfitDao().getAll().collectAsState(initial = emptyList())

    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(containerColor = DarkBg) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Profile Header
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(ChampagneGold.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(profileInitial, fontSize = 40.sp, color = ChampagneGold, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    userName.ifEmpty { "Esentiele User" },
                    color = IvoryText, fontSize = 24.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(userEmail, color = Color(0xFF9B8E7E), fontSize = 13.sp)

                Spacer(modifier = Modifier.height(8.dp))

                if (colorSeason.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(ChampagneGold.copy(alpha = 0.1f))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text("$colorSeason Season", color = ChampagneGold, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(value = "${allItems.size}", label = "Items")
                StatItem(value = "${outfits.size}", label = "Outfits")
                StatItem(value = "₹${budget / 1000}K", label = "Budget")
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Style Tags
            if (stylePrefs.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("My Style DNA", color = IvoryText, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                            Icon(Icons.Outlined.AutoAwesome, null, tint = ChampagneGold, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            stylePrefs.split(",").filter { it.isNotBlank() }.forEach { style ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(ChampagneGold.copy(alpha = 0.1f))
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(style.trim(), color = ChampagneGold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Color Palette Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("My Color Palette", color = IvoryText, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        Icon(Icons.Outlined.Palette, null, tint = ChampagneGold)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        ColorSwatch(Color(0xFF8D6E63))
                        ColorSwatch(Color(0xFFD84315))
                        ColorSwatch(Color(0xFFF9A825))
                        ColorSwatch(Color(0xFF558B2F))
                        ColorSwatch(Color(0xFF283593))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = { navController.navigate(Screen.ColorAnalysis.route) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ChampagneGold),
                        border = BorderStroke(1.dp, ChampagneGold)
                    ) {
                        Text("Analyze My Colors")
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Navigation Settings
            Text("Navigate", color = Color(0xFF666666), fontSize = 13.sp, fontWeight = FontWeight.Medium, letterSpacing = 0.5.sp)
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Column {
                    SettingsItem(icon = Icons.Outlined.Insights, title = "Closet Insights") {
                        navController.navigate(Screen.ClosetInsights.route)
                    }
                    HorizontalDivider(color = Color(0xFF2A2A2A), modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsItem(icon = Icons.Outlined.FlightTakeoff, title = "Travel Planner") {
                        navController.navigate(Screen.TravelPacking.route)
                    }
                    HorizontalDivider(color = Color(0xFF2A2A2A), modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsItem(icon = Icons.Outlined.CleaningServices, title = "Photo Clean Up") {
                        navController.navigate(Screen.MaskEditor.route)
                    }
                    HorizontalDivider(color = Color(0xFF2A2A2A), modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsItem(icon = Icons.Outlined.Nfc, title = "Smart Tags") {
                        navController.navigate(Screen.NfcTags.route)
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Logout
            OutlinedButton(
                onClick = { showLogoutDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE57373)),
                border = BorderStroke(1.dp, Color(0xFFE57373).copy(alpha = 0.3f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.AutoMirrored.Outlined.Logout, null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sign Out")
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    // Logout Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            containerColor = DarkSurface,
            title = { Text("Sign Out", color = IvoryText) },
            text = { Text("Your wardrobe data will be preserved.", color = Color(0xFF9B8E7E)) },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            userPrefs.logout()
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE57373), contentColor = Color.White)
                ) { Text("Sign Out") }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text("Cancel", color = Color.Gray) }
            }
        )
    }
}

@Composable
private fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, color = IvoryText, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, color = Color(0xFF9B8E7E), fontSize = 12.sp)
    }
}

@Composable
private fun ColorSwatch(color: Color) {
    Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(color))
}

@Composable
private fun SettingsItem(icon: ImageVector, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = title, tint = ChampagneGold.copy(0.7f), modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, color = IvoryText, fontSize = 15.sp, modifier = Modifier.weight(1f))
        Icon(Icons.Outlined.ChevronRight, null, tint = Color(0xFF3D3D3D))
    }
}
