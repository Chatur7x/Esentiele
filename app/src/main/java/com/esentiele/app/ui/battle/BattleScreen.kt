package com.esentiele.app.ui.battle

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.esentiele.app.data.local.ClothingItemEntity
import com.esentiele.app.data.local.EsentieleDatabase
import com.esentiele.app.data.remote.BattleResult
import com.esentiele.app.data.remote.LocalStylingEngine
import com.esentiele.app.ui.components.PremiumButton

@Composable
fun BattleScreen() {
    val context = LocalContext.current
    val dao = remember { EsentieleDatabase.getInstance(context).clothingDao() }
    val stylingEngine = remember { LocalStylingEngine() }
    val allItems by dao.getAll().collectAsState(initial = emptyList())

    var battleResult by remember { mutableStateOf<BattleResult?>(null) }
    var outfit1 by remember { mutableStateOf<List<ClothingItemEntity>>(emptyList()) }
    var outfit2 by remember { mutableStateOf<List<ClothingItemEntity>>(emptyList()) }

    fun shuffleOutfits() {
        if (allItems.size >= 4) {
            val tops = allItems.filter { it.category.contains("top", true) || it.category.contains("shirt", true) }.shuffled()
            val bottoms = allItems.filter { it.category.contains("bottom", true) || it.category.contains("pant", true) }.shuffled()
            val shoes = allItems.filter { it.category.contains("shoe", true) || it.category.contains("footwear", true) }.shuffled()
            
            val o1 = mutableListOf<ClothingItemEntity>()
            val o2 = mutableListOf<ClothingItemEntity>()
            
            if (tops.size >= 2 && bottoms.size >= 2 && shoes.size >= 2) {
                o1.addAll(listOf(tops[0], bottoms[0], shoes[0]))
                o2.addAll(listOf(tops[1], bottoms[1], shoes[1]))
            } else {
                val pool = allItems.shuffled().toMutableList()
                o1.addAll(pool.take(minOf(3, pool.size)))
                pool.removeAll(o1)
                o2.addAll(pool.take(minOf(3, pool.size)))
            }
            outfit1 = o1
            outfit2 = o2
            battleResult = null
        }
    }

    LaunchedEffect(allItems) {
        if (allItems.size >= 4 && outfit1.isEmpty()) {
            shuffleOutfits()
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    fun parseColor(hex: String): Color {
        return try {
            if (hex.startsWith("#")) Color(android.graphics.Color.parseColor(hex)) else Color.Gray
        } catch (e: Exception) {
            Color.Gray
        }
    }

    fun generateDescription(outfit: List<ClothingItemEntity>): String {
        return outfit.joinToString(", ") { "${it.material} ${it.subCategory.ifEmpty { it.category }}" }
    }

    Scaffold(
        containerColor = Color(0xFF0D0D0D)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "⚔️ Outfit Battle",
                color = Color(0xFFF5F0E8),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            if (allItems.size < 4) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Add at least 4 items to your Closet to start outfit battles.",
                        color = Color(0xFFF5F0E8),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    // Outfit 1
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .border(
                                    width = if (battleResult?.winner == 1) 3.dp else 0.dp,
                                    color = if (battleResult?.winner == 1) Color(0xFFC9A96E) else Color.Transparent,
                                    shape = RoundedCornerShape(16.dp)
                                ),
                            color = Color(0xFF1A1A1A)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                outfit1.forEach { item ->
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(parseColor(item.primaryColor))
                                        )
                                        Column(modifier = Modifier.padding(start = 8.dp)) {
                                            Text(text = item.subCategory.ifEmpty { item.category }, color = Color(0xFFF5F0E8), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                            Text(text = item.material, color = Color.Gray, fontSize = 10.sp)
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Outfit 1",
                            color = Color(0xFFF5F0E8),
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Text(
                        text = "VS",
                        color = Color(0xFFC9A96E),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(top = 40.dp)
                            .scale(scale)
                    )

                    // Outfit 2
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .border(
                                    width = if (battleResult?.winner == 2) 3.dp else 0.dp,
                                    color = if (battleResult?.winner == 2) Color(0xFFC9A96E) else Color.Transparent,
                                    shape = RoundedCornerShape(16.dp)
                                ),
                            color = Color(0xFF1A1A1A)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                outfit2.forEach { item ->
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(parseColor(item.primaryColor))
                                        )
                                        Column(modifier = Modifier.padding(start = 8.dp)) {
                                            Text(text = item.subCategory.ifEmpty { item.category }, color = Color(0xFFF5F0E8), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                            Text(text = item.material, color = Color.Gray, fontSize = 10.sp)
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Outfit 2",
                            color = Color(0xFFF5F0E8),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    PremiumButton(
                        text = "Shuffle",
                        onClick = { shuffleOutfits() },
                        modifier = Modifier.weight(1f)
                    )
                    
                    if (battleResult == null) {
                        PremiumButton(
                            text = "Battle!",
                            onClick = {
                                stylingEngine.roastBattle(
                                    outfit1Desc = generateDescription(outfit1),
                                    outfit2Desc = generateDescription(outfit2)
                                ).onSuccess {
                                    battleResult = it
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                AnimatedVisibility(
                    visible = battleResult != null,
                    enter = fadeIn(tween(600)) + scaleIn(tween(600))
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth().padding(top = 24.dp)
                    ) {
                        battleResult?.let { result ->
                            Text(
                                text = "🎉 Outfit ${result.winner} Wins! 🎉",
                                color = Color(0xFFC9A96E),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 24.dp)
                            )

                            Surface(
                                color = Color(0xFF1A1A1A),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp)
                                ) {
                                    Text(
                                        text = "AI Roast",
                                        color = Color(0xFFE57373),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(bottom = 12.dp)
                                    )
                                    
                                    Text(
                                        text = "Outfit 1:",
                                        color = Color(0xFFF5F0E8),
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    Text(
                                        text = result.roast1,
                                        color = Color(0xFFAAAAAA),
                                        fontSize = 14.sp,
                                        modifier = Modifier.padding(bottom = 16.dp)
                                    )

                                    Text(
                                        text = "Outfit 2:",
                                        color = Color(0xFFF5F0E8),
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    Text(
                                        text = result.roast2,
                                        color = Color(0xFFAAAAAA),
                                        fontSize = 14.sp,
                                        modifier = Modifier.padding(bottom = 16.dp)
                                    )

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color(0xFF2A2A2A), RoundedCornerShape(8.dp))
                                            .padding(12.dp)
                                    ) {
                                        Text(
                                            text = "Verdict: ${result.verdict}",
                                            color = Color(0xFFC9A96E),
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
