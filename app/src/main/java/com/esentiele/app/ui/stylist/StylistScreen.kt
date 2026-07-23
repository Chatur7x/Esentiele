package com.esentiele.app.ui.stylist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.esentiele.app.data.local.EsentieleDatabase
import com.esentiele.app.data.local.OutfitEntity
import com.esentiele.app.data.remote.LocalStylingEngine
import com.esentiele.app.domain.model.ClothingItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID

private val ChampagneGold = Color(0xFFC9A96E)
private val DarkSurface = Color(0xFF1A1A1A)
private val DarkBackground = Color(0xFF0D0D0D)
private val IvoryText = Color(0xFFF5F0E8)

private fun parseColorSafe(colorString: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(colorString))
    } catch (e: Exception) {
        Color.Gray
    }
}

private fun getTextColorForBackground(bgColor: Color): Color {
    val luminance = 0.299 * bgColor.red + 0.587 * bgColor.green + 0.114 * bgColor.blue
    return if (luminance > 0.5) Color.Black else Color.White
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StylistScreen() {
    val context = LocalContext.current
    val db = remember { EsentieleDatabase.getInstance(context) }
    val clothingDao = db.clothingDao()
    val outfitDao = db.outfitDao()
    val stylingEngine = remember { LocalStylingEngine() }
    val coroutineScope = rememberCoroutineScope()

    val allItems by clothingDao.getAll()
        .map { list -> list.map { it.toModel() } }
        .collectAsState(initial = emptyList())

    val occasions = listOf("Casual", "Office", "Date Night", "Weekend", "Travel", "Party")
    var selectedOccasion by remember { mutableStateOf(occasions[0]) }
    
    var isGenerating by remember { mutableStateOf(false) }
    var currentOutfit by remember { mutableStateOf<LocalStylingEngine.OutfitCombination?>(null) }
    var showOutfit by remember { mutableStateOf(false) }
    var reasonExpanded by remember { mutableStateOf(false) }
    
    val weather = "24°C Sunny"
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = DarkBackground,
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "AI",
                    tint = ChampagneGold,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "AI Stylist",
                    color = IvoryText,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurface)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🌡️ 24°C  |  ☀️ Sunny  |  💧 45% Humidity", color = Color.LightGray, fontSize = 14.sp)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("Select Occasion", color = IvoryText, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(12.dp))
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(occasions) { occasion ->
                    FilterChip(
                        selected = selectedOccasion == occasion,
                        onClick = { selectedOccasion = occasion },
                        label = { Text(occasion, color = if (selectedOccasion == occasion) Color.Black else IvoryText) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = DarkSurface,
                            selectedContainerColor = ChampagneGold
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = if (selectedOccasion == occasion) ChampagneGold else Color.DarkGray,
                            enabled = true,
                            selected = selectedOccasion == occasion
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            AnimatedVisibility(visible = showOutfit && currentOutfit != null, enter = fadeIn()) {
                currentOutfit?.let { outfit ->
                    Column {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            elevation = CardDefaults.cardElevation(8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(outfit.styleName, color = ChampagneGold, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(24.dp))
                                
                                val itemsToDisplay = listOfNotNull(
                                    outfit.outerwear,
                                    outfit.top,
                                    outfit.bottom,
                                    outfit.shoes,
                                    outfit.accessory
                                )
                                
                                itemsToDisplay.forEach { item ->
                                    val itemColor = parseColorSafe(item.primaryColor)
                                    val textColor = getTextColorForBackground(itemColor)
                                    val itemName = item.subCategory.ifEmpty { item.category }
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(80.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(itemColor)
                                            .border(1.dp, Color.DarkGray.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "$itemName (${item.material})",
                                            color = textColor,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateContentSize()
                                .clickable { reasonExpanded = !reasonExpanded },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
                            border = borderStroke()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = ChampagneGold, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Why this works", color = IvoryText, fontWeight = FontWeight.Medium)
                                    }
                                    Icon(
                                        imageVector = if (reasonExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                        contentDescription = null,
                                        tint = Color.Gray
                                    )
                                }
                                if (reasonExpanded) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = outfit.stylingTip,
                                        color = Color.LightGray,
                                        fontSize = 14.sp,
                                        lineHeight = 20.sp
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Button(
                            onClick = {
                                val itemIds = outfit.allItems().map { it.id }
                                val outfitEntity = OutfitEntity(
                                    id = UUID.randomUUID().toString(),
                                    itemIds = itemIds.joinToString(","),
                                    occasion = selectedOccasion,
                                    rating = null,
                                    aiScore = null,
                                    aiFeedback = null,
                                    createdAt = System.currentTimeMillis()
                                )
                                coroutineScope.launch {
                                    outfitDao.insert(outfitEntity)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .border(1.dp, ChampagneGold, RoundedCornerShape(28.dp)),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = ChampagneGold),
                            shape = RoundedCornerShape(28.dp)
                        ) {
                            Icon(Icons.Default.FavoriteBorder, contentDescription = "Save", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Save This Look", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            
            if (allItems.isEmpty() && !showOutfit) {
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "Add items to your Closet to generate AI-powered outfits from your real wardrobe.",
                    color = Color.Gray,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = {
                    if (allItems.isNotEmpty()) {
                        isGenerating = true
                        showOutfit = false
                        coroutineScope.launch {
                            delay(500)
                            currentOutfit = stylingEngine.generateRealOutfitFromWardrobe(allItems, weather, selectedOccasion)
                            isGenerating = false
                            showOutfit = true
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(
                        brush = Brush.horizontalGradient(listOf(ChampagneGold, Color(0xFFF9E596))),
                        shape = RoundedCornerShape(28.dp)
                    ),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.Black),
                contentPadding = PaddingValues(0.dp),
                enabled = !isGenerating && allItems.isNotEmpty()
            ) {
                Text(if (isGenerating) "Generating..." else "Generate New Look", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun borderStroke() = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF333333))
