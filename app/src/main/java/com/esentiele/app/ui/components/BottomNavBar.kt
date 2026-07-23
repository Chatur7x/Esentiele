package com.esentiele.app.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.esentiele.app.ui.navigation.Screen

private val ChampagneGold = Color(0xFFC9A96E)
private val DarkSurface = Color(0xFF1A1A1A)
private val DarkBg = Color(0xFF0D0D0D)
private val UnselectedColor = Color(0xFF888888)
private val IvoryText = Color(0xFFF5F0E8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavBar(navController: NavHostController) {
    val mainItems = listOf(
        Screen.Dashboard,
        Screen.Wardrobe,
        Screen.Stylist,
        Screen.Battle
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var showMoreSheet by remember { mutableStateOf(false) }

    NavigationBar(
        containerColor = DarkSurface,
        contentColor = ChampagneGold,
        tonalElevation = 8.dp
    ) {
        mainItems.forEach { screen ->
            val isSelected = currentRoute == screen.route
            
            val scale by animateFloatAsState(
                targetValue = if (isSelected) 1.15f else 1.0f,
                animationSpec = tween(
                    durationMillis = 300,
                    easing = FastOutSlowInEasing
                ), label = "icon_scale"
            )

            NavigationBarItem(
                icon = {
                    screen.icon?.let {
                        Icon(
                            imageVector = it,
                            contentDescription = screen.title,
                            modifier = Modifier.scale(scale)
                        )
                    }
                },
                label = {
                    Text(text = screen.title, fontSize = 11.sp)
                },
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Black,
                    unselectedIconColor = UnselectedColor,
                    selectedTextColor = ChampagneGold,
                    unselectedTextColor = UnselectedColor,
                    indicatorColor = ChampagneGold
                )
            )
        }

        // "More" / Atelier overflow menu
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Widgets,
                    contentDescription = "Atelier Menu"
                )
            },
            label = {
                Text(text = "Atelier", fontSize = 11.sp)
            },
            selected = showMoreSheet,
            onClick = { showMoreSheet = true },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
                unselectedIconColor = UnselectedColor,
                selectedTextColor = ChampagneGold,
                unselectedTextColor = UnselectedColor,
                indicatorColor = ChampagneGold
            )
        )
    }

    if (showMoreSheet) {
        ModalBottomSheet(
            onDismissRequest = { showMoreSheet = false },
            containerColor = DarkSurface,
            scrimColor = Color.Black.copy(alpha = 0.6f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Luxe Atelier Suite",
                            color = IvoryText,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Advanced tools & AI features",
                            color = Color(0xFF9B8E7E),
                            fontSize = 13.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(ChampagneGold.copy(0.15f))
                            .clickable {
                                showMoreSheet = false
                                navController.navigate(Screen.Profile.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.Person, null, tint = ChampagneGold, modifier = Modifier.size(20.dp))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                val suiteItems = listOf(
                    Triple(Screen.OutfitCalendar, "Outfit Calendar & Capsule", "Schedule looks by date & build 10-piece capsules"),
                    Triple(Screen.LookbookExporter, "Magazine Lookbook", "Render & export Vogue-style editorial outfit cards"),
                    Triple(Screen.TravelPacking, "Travel Packing Assistant", "Smart AI packing based on destination & weather"),
                    Triple(Screen.ClosetInsights, "Closet Analytics", "Wear frequency, cost-per-wear & sustainability stats"),
                    Triple(Screen.MaskEditor, "Background Clean-Up", "AI mask editor for studio-grade clothing shots"),
                    Triple(Screen.NfcTags, "NFC Hanger Tags", "Physical-to-digital wardrobe tracking simulation"),
                    Triple(Screen.ColorAnalysis, "Color Palette AI", "Discover your seasonal colors with camera sampling"),
                    Triple(Screen.ItemFinder, "Visual Item Finder", "Search & match garments across your digital closet")
                )

                suiteItems.forEach { (screen, title, subtitle) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable {
                                showMoreSheet = false
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF242424))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(ChampagneGold.copy(0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                screen.icon?.let {
                                    Icon(imageVector = it, contentDescription = title, tint = ChampagneGold, modifier = Modifier.size(22.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = title, color = IvoryText, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(text = subtitle, color = Color(0xFF9B8E7E), fontSize = 12.sp)
                            }
                            Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = Color(0xFF666666))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
