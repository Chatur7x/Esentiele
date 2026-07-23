package com.esentiele.app.ui.color

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.esentiele.app.data.local.EsentieleDatabase
import com.esentiele.app.ui.components.PremiumButton
import kotlinx.coroutines.launch

@Composable
fun ColorAnalysisScreen() {
    val context = LocalContext.current
    val dao = remember { EsentieleDatabase.getInstance(context).clothingDao() }
    val allItems by dao.getAll().collectAsState(initial = emptyList())
    
    var isAnalyzed by remember { mutableStateOf(false) }
    var savedSeason by remember { mutableStateOf<String?>(null) }
    
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    fun parseColor(hex: String): Color {
        return try {
            if (hex.startsWith("#")) Color(android.graphics.Color.parseColor(hex)) else Color.Gray
        } catch (e: Exception) {
            Color.Gray
        }
    }

    Scaffold(
        containerColor = Color(0xFF0D0D0D),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = "Palette",
                    tint = Color(0xFFC9A96E),
                    modifier = Modifier.size(28.dp)
                )
                Text(
                    text = "Color Analysis",
                    color = Color(0xFFF5F0E8),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }

            Text(
                text = "Take a selfie in natural light to discover your color season",
                color = Color(0xFFAAAAAA),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF1A1A1A)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Camera",
                    tint = Color(0xFF444444),
                    modifier = Modifier.size(64.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (!isAnalyzed) {
                PremiumButton(
                    text = "Analyze My Colors",
                    onClick = { isAnalyzed = true },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            AnimatedVisibility(
                visible = isAnalyzed,
                enter = fadeIn(tween(500)) + expandVertically(tween(500))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Warm Autumn",
                        color = Color(0xFFC9A96E),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    Text(
                        text = "You have rich, warm undertones. Earthy, muted, and deep colors will harmonize best with your complexion.",
                        color = Color(0xFFDDDDDD),
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(bottom = 32.dp)
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
                                text = "Your Best Colors",
                                color = Color(0xFFF5F0E8),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            
                            val bestColors = listOf(
                                Color(0xFF8B4513) to "#8B4513",
                                Color(0xFF556B2F) to "#556B2F",
                                Color(0xFFCD853F) to "#CD853F",
                                Color(0xFFB8860B) to "#B8860B",
                                Color(0xFF800000) to "#800000",
                                Color(0xFFD2691E) to "#D2691E",
                                Color(0xFF2E8B57) to "#2E8B57",
                                Color(0xFFF4A460) to "#F4A460"
                            )
                            
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(4),
                                modifier = Modifier.height(140.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                userScrollEnabled = false
                            ) {
                                items(bestColors) { colorPair ->
                                    Box(
                                        modifier = Modifier
                                            .size(60.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(colorPair.first)
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            Text(
                                text = "Colors to Avoid",
                                color = Color(0xFFF5F0E8),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            
                            val avoidColors = listOf(
                                Color(0xFF0000FF) to "#0000FF",
                                Color(0xFFFF00FF) to "#FF00FF",
                                Color(0xFF00FFFF) to "#00FFFF",
                                Color(0xFFC0C0C0) to "#C0C0C0"
                            )
                            
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(4),
                                modifier = Modifier.height(60.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                userScrollEnabled = false
                            ) {
                                items(avoidColors) { colorPair ->
                                    Box(
                                        modifier = Modifier
                                            .size(60.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(colorPair.first)
                                    )
                                }
                            }
                        }
                    }

                    if (allItems.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Surface(
                            color = Color(0xFF1A1A1A),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp)
                            ) {
                                Text(
                                    text = "Colors in Your Closet",
                                    color = Color(0xFFC9A96E),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )
                                
                                val allHexes = allItems.map { it.primaryColor }.distinct()
                                
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(5),
                                    modifier = Modifier.height(if (allHexes.size > 5) 120.dp else 60.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    userScrollEnabled = false
                                ) {
                                    items(allHexes) { hex ->
                                        Box(
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(parseColor(hex))
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                Text(
                                    text = "Your Most Used Colors",
                                    color = Color(0xFFC9A96E),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )

                                val topColors = allItems.groupingBy { it.primaryColor }
                                    .eachCount()
                                    .toList()
                                    .sortedByDescending { it.second }
                                    .take(5)

                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(5),
                                    modifier = Modifier.height(60.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    userScrollEnabled = false
                                ) {
                                    items(topColors) { (hex, _) ->
                                        Box(
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(parseColor(hex))
                                        )
                                    }
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    PremiumButton(
                        text = if (savedSeason != null) "Saved to Profile!" else "Save to Profile",
                        onClick = { 
                            savedSeason = "Warm Autumn"
                            scope.launch {
                                snackbarHostState.showSnackbar("Color season saved to your profile!")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
