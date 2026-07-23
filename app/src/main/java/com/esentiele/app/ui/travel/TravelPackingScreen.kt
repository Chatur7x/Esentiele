package com.esentiele.app.ui.travel

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FlightTakeoff
import androidx.compose.material3.*
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

private val ChampagneGold = Color(0xFFC9A96E)
private val DarkSurface = Color(0xFF1A1A1A)
private val DarkBackground = Color(0xFF0D0D0D)
private val IvoryText = Color(0xFFF5F0E8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TravelPackingScreen() {
    val context = LocalContext.current
    val dao = remember { EsentieleDatabase.getInstance(context).clothingDao() }
    val allItems by dao.getAll().collectAsState(initial = emptyList())

    var destination by remember { mutableStateOf("") }
    var selectedDays by remember { mutableIntStateOf(7) }
    var selectedWeather by remember { mutableStateOf("Mild") }
    var selectedTripType by remember { mutableStateOf("Vacation") }
    var showResults by remember { mutableStateOf(false) }

    val daysOptions = listOf(3, 5, 7, 10, 14)
    val weatherOptions = listOf("Hot", "Mild", "Cold", "Rainy")
    val tripTypes = listOf("Business", "Vacation", "Adventure")

    // The generated packing list from wardrobe
    var packingItems by remember { mutableStateOf<List<ClothingItemEntity>>(emptyList()) }
    val checkedItems = remember { mutableStateListOf<String>() }

    fun generatePackingList() {
        if (allItems.isEmpty()) {
            packingItems = emptyList()
            return
        }

        // Map weather to season roughly to filter real items
        val seasonFilters = when (selectedWeather) {
            "Hot" -> listOf("Summer", "Spring", "All Season")
            "Cold" -> listOf("Winter", "Autumn", "Fall", "All Season")
            "Rainy" -> listOf("Autumn", "Fall", "All Season", "Spring")
            else -> listOf("Spring", "Autumn", "Fall", "All Season", "Summer", "Winter")
        }

        val filteredWardrobe = allItems.filter { item ->
            seasonFilters.any { item.season.contains(it, ignoreCase = true) } || item.season.isBlank()
        }
        
        val tops = filteredWardrobe.filter { it.category.equals("Tops", ignoreCase = true) }.shuffled().take((selectedDays).coerceAtMost(5))
        val bottoms = filteredWardrobe.filter { it.category.equals("Bottoms", ignoreCase = true) }.shuffled().take((selectedDays / 2).coerceAtLeast(1).coerceAtMost(3))
        val shoes = filteredWardrobe.filter { it.category.equals("Shoes", ignoreCase = true) }.shuffled().take(2)
        val accessories = filteredWardrobe.filter { it.category.equals("Accessories", ignoreCase = true) }.shuffled().take(2)
        val outerwear = filteredWardrobe.filter { it.category.equals("Outerwear", ignoreCase = true) }.shuffled().take(if (selectedWeather == "Cold") 2 else 1)
        
        packingItems = tops + bottoms + shoes + accessories + outerwear
        checkedItems.clear()
        showResults = true
    }

    val packedCount = checkedItems.size
    val totalCount = packingItems.size
    val progressAnim by animateFloatAsState(
        targetValue = if (totalCount > 0) packedCount.toFloat() / totalCount else 0f,
        animationSpec = tween(500), label = "progress"
    )

    Scaffold(containerColor = DarkBackground) { paddingValues ->
        if (allItems.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Outlined.FlightTakeoff, contentDescription = null, tint = ChampagneGold, modifier = Modifier.size(64.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text("Your Closet is Empty", color = IvoryText, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Add items to your Closet first to generate packing lists.", color = Color.Gray, fontSize = 14.sp, textAlign = TextAlign.Center)
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))

                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Outlined.FlightTakeoff, contentDescription = null, tint = ChampagneGold, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Travel Packing", color = IvoryText, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Destination field
            item {
                OutlinedTextField(
                    value = destination,
                    onValueChange = { destination = it },
                    label = { Text("Destination", color = Color(0xFF9B8E7E)) },
                    placeholder = { Text("e.g. Paris, Tokyo, New York", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ChampagneGold,
                        unfocusedBorderColor = Color(0xFF3D3D3D),
                        focusedTextColor = IvoryText,
                        unfocusedTextColor = IvoryText,
                        cursorColor = ChampagneGold
                    ),
                    singleLine = true
                )
            }

            // Days selector
            item {
                Text("Duration", color = Color.Gray, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    daysOptions.forEach { days ->
                        val isSelected = selectedDays == days
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) ChampagneGold else DarkSurface)
                                .border(1.dp, if (isSelected) ChampagneGold else Color(0xFF3D3D3D), CircleShape)
                                .clickable { selectedDays = days },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("$days", color = if (isSelected) Color.Black else Color.Gray, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                    Text("days", color = Color.Gray, fontSize = 13.sp, modifier = Modifier.align(Alignment.CenterVertically))
                }
            }

            // Weather chips
            item {
                Text("Weather", color = Color.Gray, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    weatherOptions.forEach { weather ->
                        val isSelected = selectedWeather == weather
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedWeather = weather },
                            label = { Text(weather, color = if (isSelected) Color.Black else Color.Gray) },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = DarkSurface,
                                selectedContainerColor = ChampagneGold
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            // Trip type chips
            item {
                Text("Trip Type", color = Color.Gray, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    tripTypes.forEach { type ->
                        val isSelected = selectedTripType == type
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedTripType = type },
                            label = { Text(type, color = if (isSelected) Color.Black else Color.Gray) },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = DarkSurface,
                                selectedContainerColor = ChampagneGold
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            // Pack button
            item {
                Button(
                    onClick = { generatePackingList() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ChampagneGold, contentColor = Color.Black)
                ) {
                    Text("Pack My Bag ✨", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            // Results
            if (showResults && packingItems.isNotEmpty()) {
                // Summary
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                "${selectedDays}-day $selectedWeather $selectedTripType in ${destination.ifEmpty { "Your Destination" }}",
                                color = ChampagneGold, fontSize = 18.sp, fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("$totalCount items generated from your wardrobe", color = IvoryText, fontSize = 14.sp)
                        }
                    }
                }

                // Progress bar
                item {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("$packedCount/$totalCount items packed", color = Color.Gray, fontSize = 13.sp)
                            Text("${(progressAnim * 100).toInt()}%", color = ChampagneGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { progressAnim },
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                            color = ChampagneGold,
                            trackColor = Color(0xFF2A2A2A)
                        )
                    }
                }

                // Day-by-day schedule
                item {
                    Text("Day-by-Day Schedule", color = IvoryText, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(selectedDays) { day ->
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                                modifier = Modifier.width(140.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Day ${day + 1}", color = ChampagneGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    
                                    val tops = packingItems.filter { it.category.equals("Tops", ignoreCase = true) }
                                    val bottoms = packingItems.filter { it.category.equals("Bottoms", ignoreCase = true) }
                                    val shoes = packingItems.filter { it.category.equals("Shoes", ignoreCase = true) }
                                    
                                    val topColor = if (tops.isNotEmpty()) tops[day % tops.size].primaryColor.ifEmpty { "#FFFFFF" } else "#FAFAFA"
                                    val bottomColor = if (bottoms.isNotEmpty()) bottoms[day % bottoms.size].primaryColor.ifEmpty { "#D2B48C" } else "#D2B48C"
                                    val shoeColor = if (shoes.isNotEmpty()) shoes[day % shoes.size].primaryColor.ifEmpty { "#000000" } else "#000000"
                                    
                                    Box(modifier = Modifier.fillMaxWidth().height(24.dp).clip(RoundedCornerShape(6.dp)).background(Color(android.graphics.Color.parseColor(topColor))))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Box(modifier = Modifier.fillMaxWidth().height(30.dp).clip(RoundedCornerShape(6.dp)).background(Color(android.graphics.Color.parseColor(bottomColor))))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Box(modifier = Modifier.fillMaxWidth().height(16.dp).clip(RoundedCornerShape(6.dp)).background(Color(android.graphics.Color.parseColor(shoeColor))))
                                }
                            }
                        }
                    }
                }

                // Packing list by category
                val categories = listOf("Tops", "Bottoms", "Outerwear", "Shoes", "Accessories")
                categories.forEach { category ->
                    val categoryItems = packingItems.filter { it.category.equals(category, ignoreCase = true) }
                    if (categoryItems.isNotEmpty()) {
                        item {
                            Text(category, color = ChampagneGold, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        }
                        items(categoryItems) { packItem ->
                            val isPacked = checkedItems.contains(packItem.id)
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = DarkSurface)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = isPacked,
                                        onCheckedChange = { checked ->
                                            if (checked) checkedItems.add(packItem.id) else checkedItems.remove(packItem.id)
                                        },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = ChampagneGold,
                                            uncheckedColor = Color(0xFF3D3D3D),
                                            checkmarkColor = Color.Black
                                        )
                                    )
                                    val parsedColor = try { android.graphics.Color.parseColor(packItem.primaryColor.ifEmpty { "#FFFFFF" }) } catch(e: Exception) { android.graphics.Color.WHITE }
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clip(CircleShape)
                                            .background(Color(parsedColor))
                                            .border(1.dp, Color(0xFF3D3D3D), CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            packItem.subCategory.ifEmpty { packItem.category },
                                            color = if (isPacked) Color.Gray else IvoryText,
                                            fontWeight = FontWeight.Medium
                                        )
                                        if (packItem.material.isNotEmpty()) {
                                            Text(
                                                packItem.material,
                                                color = Color.Gray,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        }
    }
}
