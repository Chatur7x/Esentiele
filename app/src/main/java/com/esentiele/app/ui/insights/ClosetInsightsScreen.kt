package com.esentiele.app.ui.insights

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.esentiele.app.data.local.EsentieleDatabase
import kotlin.math.max

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

@Composable
fun ClosetInsightsScreen() {
    val context = LocalContext.current
    val dao = remember { EsentieleDatabase.getInstance(context).clothingDao() }
    val allItems by dao.getAll().collectAsState(initial = emptyList())
    
    val scrollState = rememberScrollState()
    var isAnimationPlayed by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        isAnimationPlayed = true
    }

    if (allItems.isEmpty()) {
        Scaffold(containerColor = DarkBackground) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("Your closet analytics will appear here once you add items.", color = Color.Gray, fontSize = 16.sp)
            }
        }
        return
    }

    val totalItems = allItems.size
    val totalValue = allItems.sumOf { it.price }

    val colorGroups = allItems.groupBy { it.primaryColor }
    val sortedColors = colorGroups.map { (hex, list) -> 
        Pair(hex, (list.size.toFloat() / totalItems) * 100f)
    }.sortedByDescending { it.second }.take(6)

    val activeItems = allItems.filter { it.timesWorn > 0 }
    val dormantItems = allItems.filter { it.timesWorn == 0 || it.lastWorn == 0L }
    
    val costPerWearItems = allItems.filter { it.price > 0 }.map { item ->
        val cpw = item.price / max(item.timesWorn, 1)
        Pair(item, cpw)
    }.sortedBy { it.second }.take(5)

    Scaffold(
        containerColor = DarkBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Outlined.Analytics, contentDescription = null, tint = ChampagneGold, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text("Closet Insights", color = IvoryText, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = DarkSurface), shape = RoundedCornerShape(12.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Total Items", color = Color.Gray, fontSize = 13.sp)
                        Text("$totalItems", color = IvoryText, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = DarkSurface), shape = RoundedCornerShape(12.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Total Value", color = Color.Gray, fontSize = 13.sp)
                        Text("₹${totalValue.toInt()}", color = ChampagneGold, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (sortedColors.isNotEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Color Palette Distribution", color = IvoryText, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(24.dp))

                        val sweepAnimations = sortedColors.map { item ->
                            animateFloatAsState(
                                targetValue = if (isAnimationPlayed) item.second * 3.6f else 0f,
                                animationSpec = tween(durationMillis = 1000),
                                label = item.first
                            )
                        }

                        Canvas(modifier = Modifier.size(160.dp)) {
                            var startAngle = -90f
                            sweepAnimations.forEachIndexed { idx, sweepAnim ->
                                drawArc(
                                    color = parseColorSafe(sortedColors[idx].first),
                                    startAngle = startAngle,
                                    sweepAngle = sweepAnim.value,
                                    useCenter = true,
                                    size = Size(size.width, size.height)
                                )
                                startAngle += sortedColors[idx].second * 3.6f
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            sortedColors.chunked(2).forEach { pair ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    pair.forEach { item ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.width(120.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(12.dp)
                                                    .clip(RoundedCornerShape(3.dp))
                                                    .background(parseColorSafe(item.first))
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                "${item.second.toInt()}%",
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
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Closet Utilization", color = IvoryText, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Active Items", color = Color(0xFF81C784), fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("${activeItems.size} Items", color = IvoryText, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(2.dp))
                            val activePct = if (totalItems > 0) (activeItems.size.toFloat() / totalItems * 100).toInt() else 0
                            Text("$activePct% of wardrobe", color = Color.Gray, fontSize = 11.sp)
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Forgotten Items", color = Color(0xFFE57373), fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("${dormantItems.size} Items", color = IvoryText, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Not worn recently", color = Color.Gray, fontSize = 11.sp)
                        }
                    }
                }
                
                if (totalItems > 0) {
                    val activeRatio = activeItems.size.toFloat() / totalItems.toFloat()
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { activeRatio },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFF81C784),
                        trackColor = Color(0xFFE57373)
                    )
                }
            }

            if (costPerWearItems.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Cost Per Wear Analysis (Best Value)", color = IvoryText, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)

                    costPerWearItems.forEach { (item, cpw) ->
                        val progressRatio by animateFloatAsState(
                            targetValue = if (isAnimationPlayed) (item.timesWorn.toFloat() / 50f).coerceAtMost(1f) else 0f,
                            animationSpec = tween(1200),
                            label = "progress"
                        )

                        Card(
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(item.subCategory.ifEmpty { item.category }, color = IvoryText, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                                    val cpwColor = when {
                                        cpw < 1000.0 -> Color(0xFF81C784)
                                        cpw < 3000.0 -> ChampagneGold
                                        else -> Color(0xFFE57373)
                                    }
                                    Text(
                                        "₹${cpw.toInt()}/wear",
                                        color = cpwColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Original: ₹${item.price.toInt()}", color = Color.Gray, fontSize = 12.sp)
                                    Text("Worn: ${item.timesWorn}x", color = ChampagneGold, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                LinearProgressIndicator(
                                    progress = { progressRatio },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(4.dp)
                                        .clip(RoundedCornerShape(2.dp)),
                                    color = ChampagneGold,
                                    trackColor = Color(0xFF2A2A2A)
                                )
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
