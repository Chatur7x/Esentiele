package com.esentiele.app.ui.itemfinder

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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

@Composable
fun ItemFinderScreen() {
    val context = LocalContext.current
    val dao = remember { EsentieleDatabase.getInstance(context).clothingDao() }
    val allItems by dao.getAll().collectAsState(initial = emptyList())
    
    var hasUploaded by remember { mutableStateOf(false) }

    fun parseColor(hex: String): Color {
        return try {
            if (hex.startsWith("#")) Color(android.graphics.Color.parseColor(hex)) else Color.Gray
        } catch (e: Exception) {
            Color.Gray
        }
    }
    
    fun getEmojiForCategory(category: String): String {
        val cat = category.lowercase()
        return when {
            cat.contains("top") || cat.contains("shirt") -> "👚"
            cat.contains("bottom") || cat.contains("pant") || cat.contains("jean") -> "👖"
            cat.contains("shoe") || cat.contains("footwear") -> "👟"
            cat.contains("dress") -> "👗"
            cat.contains("outerwear") || cat.contains("jacket") -> "🧥"
            else -> "👕"
        }
    }

    Scaffold(
        containerColor = Color(0xFF0D0D0D)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color(0xFFC9A96E),
                    modifier = Modifier.size(28.dp)
                )
                Text(
                    text = "Item Finder",
                    color = Color(0xFFF5F0E8),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF1A1A1A))
                    .border(
                        width = 2.dp,
                        color = Color(0xFF333333),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .clickable { hasUploaded = true },
                contentAlignment = Alignment.Center
            ) {
                if (hasUploaded) {
                    Text(
                        text = "Photo Processing...",
                        color = Color(0xFFC9A96E),
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Upload",
                            tint = Color(0xFF666666),
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Upload a photo of any item you like",
                            color = Color(0xFFAAAAAA),
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            AnimatedVisibility(
                visible = hasUploaded,
                enter = fadeIn(tween(500)) + expandVertically(tween(500))
            ) {
                if (allItems.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().padding(top = 32.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Add items to your Closet to see wardrobe matches.",
                            color = Color(0xFFF5F0E8),
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            color = Color(0xFF1A1A1A),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 24.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF2A2A2A)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val matchPercent = (allItems.size * 10).coerceAtMost(95)
                                    Text(
                                        text = "$matchPercent%",
                                        color = Color(0xFF81C784),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }
                                Column(
                                    modifier = Modifier.padding(start = 16.dp)
                                ) {
                                    Text(
                                        text = "Wardrobe Match",
                                        color = Color(0xFFF5F0E8),
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = "Highly compatible with your current closet",
                                        color = Color(0xFFAAAAAA),
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }

                        Text(
                            text = "Similar items in your closet",
                            color = Color(0xFFC9A96E),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        LazyColumn {
                            items(allItems) { item ->
                                Surface(
                                    color = Color(0xFF1A1A1A),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(60.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(parseColor(item.primaryColor)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = getEmojiForCategory(item.category),
                                                fontSize = 24.sp
                                            )
                                        }
                                        
                                        Column(
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(horizontal = 16.dp)
                                        ) {
                                            Text(
                                                text = item.subCategory.ifEmpty { item.category },
                                                color = Color(0xFFF5F0E8),
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 15.sp
                                            )
                                            Text(
                                                text = item.material,
                                                color = Color(0xFFAAAAAA),
                                                fontSize = 13.sp
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
}
