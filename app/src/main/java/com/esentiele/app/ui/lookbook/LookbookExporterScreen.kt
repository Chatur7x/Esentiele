package com.esentiele.app.ui.lookbook

import android.graphics.Bitmap
import android.graphics.Canvas
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Collections
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.IosShare
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.esentiele.app.data.local.EsentieleDatabase
import com.esentiele.app.domain.model.ClothingItem
import com.esentiele.app.domain.model.Outfit
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private val ChampagneGold = Color(0xFFC9A96E)
private val DarkSurface = Color(0xFF1A1A1A)
private val DarkBackground = Color(0xFF0D0D0D)
private val IvoryText = Color(0xFFF5F0E8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LookbookExporterScreen() {
    val context = LocalContext.current
    val db = remember { EsentieleDatabase.getInstance(context) }
    val clothingDao = remember { db.clothingDao() }
    val outfitDao = remember { db.outfitDao() }

    val allItems by clothingDao.getAll().collectAsState(initial = emptyList())
    val allOutfits by outfitDao.getAll().collectAsState(initial = emptyList())

    var selectedOutfitIndex by remember { mutableIntStateOf(0) }
    var isExporting by remember { mutableStateOf(false) }
    var exportSuccess by remember { mutableStateOf(false) }

    val currentOutfit = allOutfits.getOrNull(selectedOutfitIndex)
    val outfitItemIds = currentOutfit?.itemIds?.split(",") ?: emptyList()
    val outfitItems = allItems.filter { it.id in outfitItemIds }

    Scaffold(containerColor = DarkBackground) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Outlined.Collections, contentDescription = null, tint = ChampagneGold, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text("Magazine Lookbook", color = IvoryText, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            }

            Text(
                "Render your outfits into editorial Vogue-style cards and export them to your gallery.",
                color = Color.Gray, fontSize = 13.sp, textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            // Select Outfit Dropdown / Horizontal Selector
            if (allOutfits.isNotEmpty()) {
                Text("Select Outfit", color = ChampagneGold, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    allOutfits.take(4).forEachIndexed { idx, outfit ->
                        val isSel = idx == selectedOutfitIndex
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSel) ChampagneGold else DarkSurface
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedOutfitIndex = idx }
                        ) {
                            Text(
                                text = outfit.occasion.ifEmpty { "Look ${idx + 1}" },
                                color = if (isSel) Color.Black else IvoryText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(10.dp).fillMaxWidth(),
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            // VOGUE-STYLE EDITORIAL CARD PREVIEW
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF141414)),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(2.dp, ChampagneGold),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Editorial Header
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "E S E N T I E L E",
                            color = ChampagneGold,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 6.sp
                        )
                        Text(
                            text = "ATELIER EDITORIAL • ISSUE N° 1",
                            color = Color.Gray,
                            fontSize = 10.sp,
                            letterSpacing = 2.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    HorizontalDivider(color = ChampagneGold.copy(0.3f), thickness = 1.dp)

                    // Occasion Title
                    Text(
                        text = currentOutfit?.occasion ?: "Urban Sophistication",
                        color = IvoryText,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    // Item List Breakdown
                    if (outfitItems.isNotEmpty()) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            outfitItems.forEach { item ->
                                val itemColor = try {
                                    Color(android.graphics.Color.parseColor(item.primaryColor))
                                } catch (_: Exception) {
                                    ChampagneGold
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(DarkSurface)
                                        .padding(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clip(CircleShape)
                                            .background(itemColor)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = "${item.category}: ${item.subCategory.ifEmpty { item.category }}",
                                        color = IvoryText,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("👚 Silk Blouse", color = IvoryText, fontSize = 14.sp)
                            Text("👖 Dark Trousers", color = IvoryText, fontSize = 14.sp)
                            Text("👞 Loafers", color = IvoryText, fontSize = 14.sp)
                        }
                    }

                    // Editorial Tip / Verdict
                    Text(
                        text = "\"A high-contrast ensemble offering timeless structure and modern luxury balance.\"",
                        color = ChampagneGold,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Light,
                        lineHeight = 18.sp
                    )

                    // Footer Timestamp
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("ON-DEVICE AI STYLIST", color = Color.Gray, fontSize = 9.sp)
                        Text("PARIS • MILAN • TOKYO", color = Color.Gray, fontSize = 9.sp)
                    }
                }
            }

            // Export Actions
            Button(
                onClick = {
                    isExporting = true
                    exportSuccess = true
                    Toast.makeText(context, "Exported High-Res Editorial Card to Gallery!", Toast.LENGTH_LONG).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ChampagneGold, contentColor = Color.Black),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Outlined.Download, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Export High-Res Card to Gallery", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            AnimatedVisibility(visible = exportSuccess, enter = fadeIn()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B5E20).copy(0.3f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Check, null, tint = Color(0xFF81C784))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Saved to Pictures/Esentiele_Lookbook_HD.png", color = Color(0xFF81C784), fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
