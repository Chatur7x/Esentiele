package com.esentiele.app.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.FactCheck
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.esentiele.app.data.local.UserPreferences
import com.esentiele.app.data.local.EsentieleDatabase
import com.esentiele.app.data.local.OutfitEntity
import com.esentiele.app.ui.navigation.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.UUID
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.style.TextAlign

private val ChampagneGold = Color(0xFFC9A96E)
private val DarkSurface = Color(0xFF1A1A1A)
private val DarkBg = Color(0xFF0D0D0D)
private val IvoryText = Color(0xFFF5F0E8)

@Composable
fun DashboardScreen(navController: NavHostController) {
    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }
    val db = remember { EsentieleDatabase.getInstance(context) }
    val dao = remember { db.clothingDao() }
    val outfitDao = remember { db.outfitDao() }
    val scope = rememberCoroutineScope()
    
    val userName by userPrefs.userName.collectAsState(initial = "")
    val colorSeason by userPrefs.colorSeason.collectAsState(initial = "")
    val stylePrefs by userPrefs.stylePreferences.collectAsState(initial = "")

    val items by dao.getAll().collectAsState(initial = emptyList())

    var isVisible by remember { mutableStateOf(false) }
    var showWearConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in 0..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            else -> "Good Evening"
        }
    }

    // Simulated weather based on time
    val weatherInfo = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when {
            hour in 6..10 -> Triple("22°C", "Fresh Morning", "🌅")
            hour in 11..15 -> Triple("31°C", "Sunny & Warm", "☀️")
            hour in 16..19 -> Triple("27°C", "Golden Hour", "🌇")
            else -> Triple("20°C", "Cool Night", "🌙")
        }
    }

    Scaffold(containerColor = DarkBg) { paddingValues ->
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(tween(600)) + slideInVertically(initialOffsetY = { 50 })
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Greeting
                Text(text = "$greeting,", color = Color(0xFF9B8E7E), fontSize = 15.sp, letterSpacing = 0.5.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = userName.ifEmpty { "Stylist" },
                    color = ChampagneGold, fontSize = 28.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp
                )
                if (colorSeason.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("$colorSeason Season", color = Color(0xFF666666), fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Weather Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(weatherInfo.third, fontSize = 32.sp)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(weatherInfo.first, color = IvoryText, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                            Text(weatherInfo.second, color = Color(0xFF9B8E7E), fontSize = 13.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Today's Look
                Text("Today's Look", color = IvoryText, fontSize = 20.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                Spacer(modifier = Modifier.height(14.dp))

                if (items.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("✨", fontSize = 32.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Your Atelier is empty.", color = IvoryText, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                            Text("Add pieces in Closet to generate AI outfits.", color = Color.Gray, fontSize = 13.sp, textAlign = TextAlign.Center)
                        }
                    }
                } else {
                    val top = items.firstOrNull { it.category == "Tops" }
                    val bottom = items.firstOrNull { it.category == "Bottoms" }
                    val shoes = items.firstOrNull { it.category == "Shoes" }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                if (top != null) {
                                    val color = try { Color(android.graphics.Color.parseColor(top.primaryColor)) } catch(e:Exception) { Color(0xFFF5F5DC) }
                                    val textColor = if (color.luminance() > 0.5f) Color.Black else Color.White
                                    Box(modifier = Modifier.fillMaxWidth().height(50.dp).clip(RoundedCornerShape(12.dp)).background(color), contentAlignment = Alignment.Center) { 
                                        Text("👚 ${top.subCategory.ifEmpty { top.category }}", fontSize = 13.sp, color = textColor) 
                                    }
                                }
                                if (bottom != null) {
                                    val color = try { Color(android.graphics.Color.parseColor(bottom.primaryColor)) } catch(e:Exception) { Color(0xFF212121) }
                                    val textColor = if (color.luminance() > 0.5f) Color.Black else Color.White
                                    Box(modifier = Modifier.fillMaxWidth().height(65.dp).clip(RoundedCornerShape(12.dp)).background(color), contentAlignment = Alignment.Center) { 
                                        Text("👖 ${bottom.subCategory.ifEmpty { bottom.category }}", fontSize = 13.sp, color = textColor) 
                                    }
                                }
                                if (shoes != null) {
                                    val color = try { Color(android.graphics.Color.parseColor(shoes.primaryColor)) } catch(e:Exception) { Color(0xFF5D4037) }
                                    val textColor = if (color.luminance() > 0.5f) Color.Black else Color.White
                                    Box(modifier = Modifier.fillMaxWidth().height(35.dp).clip(RoundedCornerShape(12.dp)).background(color), contentAlignment = Alignment.Center) { 
                                        Text("👞 ${shoes.subCategory.ifEmpty { shoes.category }}", fontSize = 13.sp, color = textColor) 
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.width(20.dp))
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Urban Minimal", color = ChampagneGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = { 
                                        scope.launch {
                                            val time = System.currentTimeMillis()
                                            val outfitItemIds = mutableListOf<String>()
                                            
                                            if (top != null) {
                                                dao.updateWearCount(top.id, top.timesWorn + 1, time)
                                                outfitItemIds.add(top.id)
                                            }
                                            if (bottom != null) {
                                                dao.updateWearCount(bottom.id, bottom.timesWorn + 1, time)
                                                outfitItemIds.add(bottom.id)
                                            }
                                            if (shoes != null) {
                                                dao.updateWearCount(shoes.id, shoes.timesWorn + 1, time)
                                                outfitItemIds.add(shoes.id)
                                            }
                                            
                                            if (outfitItemIds.isNotEmpty()) {
                                                outfitDao.insert(
                                                    OutfitEntity(
                                                        id = UUID.randomUUID().toString(),
                                                        itemIds = outfitItemIds.joinToString(","),
                                                        occasion = "Everyday",
                                                        rating = null,
                                                        aiScore = 95,
                                                        aiFeedback = "Great combination!",
                                                        createdAt = time
                                                    )
                                                )
                                            }
                                            showWearConfirm = true
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = ChampagneGold, contentColor = Color.Black),
                                    shape = RoundedCornerShape(12.dp)
                                ) { Text("Wear This", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                                AnimatedVisibility(visible = showWearConfirm) {
                                    Text("✓ Logged!", color = Color(0xFF81C784), fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Quick Actions — Row 1
                Text("Quick Actions", color = IvoryText, fontSize = 20.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                Spacer(modifier = Modifier.height(14.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    QuickActionItem("Outfit Check", Icons.AutoMirrored.Outlined.FactCheck) { navController.navigate(Screen.OutfitCheck.route) }
                    QuickActionItem("Glow Up", Icons.Outlined.Face) { navController.navigate(Screen.GlowUp.route) }
                    QuickActionItem("Battle", Icons.Outlined.Compare) { navController.navigate(Screen.Battle.route) }
                    QuickActionItem("Find Item", Icons.Outlined.Search) { navController.navigate(Screen.ItemFinder.route) }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Quick Actions — Row 2 (new features)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    QuickActionItem("Travel", Icons.Outlined.FlightTakeoff) { navController.navigate(Screen.TravelPacking.route) }
                    QuickActionItem("Insights", Icons.Outlined.Insights) { navController.navigate(Screen.ClosetInsights.route) }
                    QuickActionItem("Clean Up", Icons.Outlined.CleaningServices) { navController.navigate(Screen.MaskEditor.route) }
                    QuickActionItem("Tags", Icons.Outlined.Nfc) { navController.navigate(Screen.NfcTags.route) }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Style tips based on preferences
                if (stylePrefs.isNotEmpty()) {
                    Text("Your Style", color = IvoryText, fontSize = 20.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        stylePrefs.split(",").filter { it.isNotBlank() }.forEach { style ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = ChampagneGold.copy(alpha = 0.1f)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    style.trim(), color = ChampagneGold, fontWeight = FontWeight.Medium, fontSize = 13.sp,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun QuickActionItem(label: String, icon: ImageVector, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(DarkSurface),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = ChampagneGold, modifier = Modifier.size(26.dp))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = label, color = Color(0xFF9B8E7E), fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}
