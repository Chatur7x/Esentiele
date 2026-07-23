package com.esentiele.app.ui.calendar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.esentiele.app.data.local.ClothingItemEntity
import com.esentiele.app.data.local.EsentieleDatabase
import com.esentiele.app.data.local.OutfitEntity
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

private val ChampagneGold = Color(0xFFC9A96E)
private val DarkSurface = Color(0xFF1A1A1A)
private val DarkBackground = Color(0xFF0D0D0D)
private val IvoryText = Color(0xFFF5F0E8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen() {
    val context = LocalContext.current
    val db = remember { EsentieleDatabase.getInstance(context) }
    val clothingDao = remember { db.clothingDao() }
    val outfitDao = remember { db.outfitDao() }
    val scope = rememberCoroutineScope()

    val allItems by clothingDao.getAll().collectAsState(initial = emptyList())
    val allOutfits by outfitDao.getAll().collectAsState(initial = emptyList())

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Calendar, 1: Capsule Builder

    // Calendar State
    var currentMonthCalendar by remember { mutableStateOf(Calendar.getInstance()) }
    var selectedDayOfMonth by remember { mutableIntStateOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) }

    // Capsule State
    val selectedCapsuleItemIds = remember { mutableStateListOf<String>() }
    var generatedCapsuleLookCount by remember { mutableIntStateOf(0) }
    var showCapsuleSuccess by remember { mutableStateOf(false) }

    Scaffold(containerColor = DarkBackground) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Outlined.CalendarMonth, contentDescription = null, tint = ChampagneGold, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text("Style Planner", color = IvoryText, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            }

            Text(
                "Schedule outfits by date & create minimalist 10-piece capsule collections.",
                color = Color.Gray, fontSize = 13.sp, textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            // Tab Selector
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = DarkSurface,
                contentColor = ChampagneGold,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = ChampagneGold
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Outfit Calendar", fontWeight = FontWeight.SemiBold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Capsule Builder", fontWeight = FontWeight.SemiBold) }
                )
            }

            if (selectedTab == 0) {
                // CALENDAR SCHEDULER TAB
                val monthYearFormat = remember { SimpleDateFormat("MMMM yyyy", Locale.getDefault()) }
                
                // Month Navigation Header
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            val newCal = currentMonthCalendar.clone() as Calendar
                            newCal.add(Calendar.MONTH, -1)
                            currentMonthCalendar = newCal
                        }) {
                            Icon(Icons.Default.ChevronLeft, contentDescription = "Prev Month", tint = ChampagneGold)
                        }

                        Text(
                            text = monthYearFormat.format(currentMonthCalendar.time),
                            color = IvoryText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )

                        IconButton(onClick = {
                            val newCal = currentMonthCalendar.clone() as Calendar
                            newCal.add(Calendar.MONTH, 1)
                            currentMonthCalendar = newCal
                        }) {
                            Icon(Icons.Default.ChevronRight, contentDescription = "Next Month", tint = ChampagneGold)
                        }
                    }
                }

                // Days Grid (1..30/31)
                val daysInMonth = currentMonthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                val daysList = (1..daysInMonth).toList()

                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.height(240.dp)
                ) {
                    items(daysList) { day ->
                        val isSelected = day == selectedDayOfMonth
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) ChampagneGold else DarkSurface)
                                .border(1.dp, if (isSelected) ChampagneGold else Color(0xFF333333), CircleShape)
                                .clickable { selectedDayOfMonth = day },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day.toString(),
                                color = if (isSelected) Color.Black else IvoryText,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                // Selected Date Detail & Outfits
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().weight(1f)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Scheduled for ${monthYearFormat.format(currentMonthCalendar.time).split(" ")[0]} $selectedDayOfMonth",
                            color = ChampagneGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        if (allOutfits.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    "No outfits created yet.\nGo to Stylist to generate and save your looks!",
                                    color = Color.Gray,
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                items(allOutfits) { outfit ->
                                    val itemIds = outfit.itemIds.split(",")
                                    val matchingItems = allItems.filter { it.id in itemIds }

                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFF242424)),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(outfit.occasion, color = IvoryText, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                                Text(
                                                    matchingItems.joinToString(", ") { it.subCategory.ifEmpty { it.category } }.ifEmpty { "Outfit (${itemIds.size} pieces)" },
                                                    color = Color.Gray,
                                                    fontSize = 12.sp
                                                )
                                            }

                                            Button(
                                                onClick = {
                                                    scope.launch {
                                                        val scheduledCal = currentMonthCalendar.clone() as Calendar
                                                        scheduledCal.set(Calendar.DAY_OF_MONTH, selectedDayOfMonth)
                                                        outfitDao.updateScheduledDate(outfit.id, scheduledCal.timeInMillis)
                                                    }
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = ChampagneGold, contentColor = Color.Black),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text("Schedule", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // CAPSULE BUILDER TAB
                Text(
                    text = "Select 6 to 15 garments from your closet to generate a minimalist Capsule Wardrobe.",
                    color = IvoryText,
                    fontSize = 14.sp
                )

                if (allItems.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Add items to your Closet first to build capsule collections.", color = Color.Gray, fontSize = 14.sp)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(allItems) { item ->
                            val isSelected = selectedCapsuleItemIds.contains(item.id)

                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) ChampagneGold.copy(0.15f) else DarkSurface
                                ),
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, ChampagneGold) else null,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().clickable {
                                    if (isSelected) selectedCapsuleItemIds.remove(item.id)
                                    else selectedCapsuleItemIds.add(item.id)
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) ChampagneGold else Color(0xFF333333)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isSelected) {
                                            Icon(Icons.Default.Check, null, tint = Color.Black, modifier = Modifier.size(14.dp))
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(item.subCategory.ifEmpty { item.category }, color = IvoryText, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                        Text("${item.category} • ${item.material}", color = Color.Gray, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }

                    Button(
                        onClick = {
                            val selectedItems = allItems.filter { it.id in selectedCapsuleItemIds }
                            val tops = selectedItems.filter { it.category == "Tops" }
                            val bottoms = selectedItems.filter { it.category == "Bottoms" }
                            val shoes = selectedItems.filter { it.category == "Shoes" }

                            val count = (tops.size * bottoms.size * maxOf(shoes.size, 1)).coerceAtLeast(1)
                            generatedCapsuleLookCount = count

                            scope.launch {
                                // Insert a capsule outfit summary into DB
                                outfitDao.insert(
                                    OutfitEntity(
                                        id = UUID.randomUUID().toString(),
                                        itemIds = selectedCapsuleItemIds.joinToString(","),
                                        occasion = "Capsule Wardrobe ($count combinations)",
                                        rating = 5,
                                        aiScore = 95,
                                        aiFeedback = "Minimalist capsule wardrobe with high inter-item versatility.",
                                        createdAt = System.currentTimeMillis(),
                                        isCapsule = true
                                    )
                                )
                                showCapsuleSuccess = true
                            }
                        },
                        enabled = selectedCapsuleItemIds.size >= 3,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ChampagneGold, contentColor = Color.Black),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Build Capsule (${selectedCapsuleItemIds.size} Selected)", fontWeight = FontWeight.Bold)
                    }

                    AnimatedVisibility(visible = showCapsuleSuccess) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1B5E20).copy(0.3f)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "✓ Capsule Collection Created! Generated $generatedCapsuleLookCount versatile outfits.",
                                color = Color(0xFF81C784),
                                modifier = Modifier.padding(14.dp),
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
