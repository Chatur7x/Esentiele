package com.esentiele.app.ui.nfc

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Nfc
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.esentiele.app.data.local.ClothingItemEntity
import com.esentiele.app.data.local.EsentieleDatabase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val ChampagneGold = Color(0xFFC9A96E)
private val DarkSurface = Color(0xFF1A1A1A)
private val DarkBackground = Color(0xFF0D0D0D)
private val IvoryText = Color(0xFFF5F0E8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NfcTagScreen() {
    val context = LocalContext.current
    val dao = remember { EsentieleDatabase.getInstance(context).clothingDao() }
    val allItems by dao.getAll().collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()

    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedItemId by remember { mutableStateOf("") }
    
    // Automatically select the first item if available
    LaunchedEffect(allItems) {
        if (selectedItemId.isEmpty() && allItems.isNotEmpty()) {
            selectedItemId = allItems.first().id
        }
    }
    
    var isWriting by remember { mutableStateOf(false) }
    var writeSuccess by remember { mutableStateOf(false) }
    
    var isScanning by remember { mutableStateOf(false) }
    var detectedItem by remember { mutableStateOf<ClothingItemEntity?>(null) }
    var showMarkedWorn by remember { mutableStateOf(false) }

    // Animation scale for scanning/writing pulse
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_scale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_alpha"
    )

    LaunchedEffect(isWriting) {
        if (isWriting) {
            delay(2000)
            val newTagId = "NFC_${(1000..9999).random()}"
            if (selectedItemId.isNotEmpty()) {
                dao.updateNfcTag(selectedItemId, newTagId)
            }
            isWriting = false
            writeSuccess = true
            delay(2000)
            writeSuccess = false
        }
    }

    LaunchedEffect(isScanning) {
        if (isScanning) {
            delay(2500)
            isScanning = false
            val taggedItems = allItems.filter { it.nfcTagId.isNotEmpty() }
            detectedItem = taggedItems.randomOrNull()
        }
    }

    Scaffold(containerColor = DarkBackground) { paddingValues ->
        if (allItems.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Outlined.Nfc, contentDescription = null, tint = ChampagneGold, modifier = Modifier.size(64.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text("Your Closet is Empty", color = IvoryText, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Add items to your Closet first to use Smart Hangers.", color = Color.Gray, fontSize = 14.sp, textAlign = TextAlign.Center)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Outlined.Nfc, contentDescription = null, tint = ChampagneGold, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text("Smart Hangers", color = IvoryText, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            }

            Text(
                "Link physical clothes using cheap NFC tags or printed codes to view their status instantly.",
                color = Color.Gray, fontSize = 13.sp, textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            // Tabs
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
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0; detectedItem = null }, text = { Text("Write Hanger Tag") })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1; writeSuccess = false }, text = { Text("Scan Hanger Tag") })
            }

            if (selectedTab == 0) {
                // WRITE TAG MODE
                Text("Select Wardrobe Item", color = IvoryText, fontSize = 15.sp, fontWeight = FontWeight.Medium)

                var expanded by remember { mutableStateOf(false) }
                val selectedItemDisplay = allItems.find { it.id == selectedItemId }?.let { it.subCategory.ifEmpty { it.category } } ?: "Select Item"

                Box {
                    Button(
                        onClick = { expanded = true },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkSurface, contentColor = IvoryText)
                    ) {
                        Text(selectedItemDisplay, fontWeight = FontWeight.Medium)
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(DarkSurface)
                    ) {
                        allItems.forEach { item ->
                            val itemName = item.subCategory.ifEmpty { item.category }
                            DropdownMenuItem(
                                text = { Text(itemName, color = IvoryText) },
                                onClick = {
                                    selectedItemId = item.id
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    if (isWriting) {
                        Box(contentAlignment = Alignment.Center) {
                            Box(
                                modifier = Modifier
                                    .size(150.dp)
                                    .graphicsLayer {
                                        scaleX = pulseScale
                                        scaleY = pulseScale
                                        alpha = pulseAlpha
                                    }
                                    .clip(CircleShape)
                                    .background(ChampagneGold)
                            )
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .background(ChampagneGold),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Outlined.Nfc, contentDescription = null, tint = Color.Black, modifier = Modifier.size(48.dp))
                            }
                        }
                    } else if (writeSuccess) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = Color(0xFF81C784), modifier = Modifier.size(64.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Tag Programmed Successfully!", color = ChampagneGold, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    } else {
                        Button(
                            onClick = { 
                                if (selectedItemId.isNotEmpty()) {
                                    isWriting = true 
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ChampagneGold, contentColor = Color.Black),
                            modifier = Modifier.size(120.dp),
                            shape = CircleShape
                        ) {
                            Text("Write\nNFC Tag", textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                // READ SCAN TAG MODE
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    if (isScanning) {
                        Box(contentAlignment = Alignment.Center) {
                            Box(
                                modifier = Modifier
                                    .size(150.dp)
                                    .graphicsLayer {
                                        scaleX = pulseScale
                                        scaleY = pulseScale
                                        alpha = pulseAlpha
                                    }
                                    .clip(CircleShape)
                                    .background(ChampagneGold)
                            )
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .background(ChampagneGold),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Outlined.Nfc, contentDescription = null, tint = Color.Black, modifier = Modifier.size(48.dp))
                            }
                        }
                    } else if (detectedItem != null) {
                        // Scan result card
                        val item = detectedItem!!
                        Card(
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                val parsedColor = try { android.graphics.Color.parseColor(item.primaryColor.ifEmpty { "#FFFFFF" }) } catch(e: Exception) { android.graphics.Color.WHITE }
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(Color(parsedColor))
                                        .border(2.dp, ChampagneGold, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Outlined.Nfc, contentDescription = null, tint = if (Color(parsedColor).luminance() > 0.5) Color.Black else Color.White, modifier = Modifier.size(24.dp))
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(item.subCategory.ifEmpty { item.category }, color = IvoryText, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                                Text("ID: ${item.nfcTagId}", color = Color.Gray, fontSize = 13.sp)
                                Spacer(modifier = Modifier.height(20.dp))

                                Button(
                                    onClick = { 
                                        coroutineScope.launch {
                                            dao.updateWearCount(item.id, item.timesWorn + 1, System.currentTimeMillis())
                                            showMarkedWorn = true 
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = ChampagneGold, contentColor = Color.Black),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Mark as Worn Today", fontWeight = FontWeight.Bold)
                                }

                                AnimatedVisibility(visible = showMarkedWorn) {
                                    Text("✓ Closet stats updated!", color = Color(0xFF81C784), fontSize = 13.sp, modifier = Modifier.padding(top = 10.dp))
                                }
                            }
                        }
                    } else {
                        Button(
                            onClick = { isScanning = true; showMarkedWorn = false },
                            colors = ButtonDefaults.buttonColors(containerColor = ChampagneGold, contentColor = Color.Black),
                            modifier = Modifier.size(120.dp),
                            shape = CircleShape
                        ) {
                            Text("Scan\nHanger", textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Tagged List history
            val taggedHangers = allItems.filter { it.nfcTagId.isNotEmpty() }
            if (taggedHangers.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Recently Tagged Items", color = IvoryText, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.height(150.dp)
                    ) {
                        items(taggedHangers) { hanger ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(hanger.subCategory.ifEmpty { hanger.category }, color = IvoryText, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                                        Text(hanger.nfcTagId, color = Color.Gray, fontSize = 11.sp)
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

// Extension to determine luminance for icon color
fun Color.luminance(): Float {
    val r = this.red
    val g = this.green
    val b = this.blue
    return 0.2126f * r + 0.7152f * g + 0.0722f * b
}
