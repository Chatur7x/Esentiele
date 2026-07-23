package com.esentiele.app.ui.wardrobe

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.esentiele.app.data.local.ClothingItemEntity
import com.esentiele.app.data.local.EsentieleDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID

private val ChampagneGold = Color(0xFFC9A96E)
private val DarkSurface = Color(0xFF1A1A1A)
private val DarkBg = Color(0xFF0D0D0D)
private val IvoryText = Color(0xFFF5F0E8)

private val categoryEmojis = mapOf(
    "Tops" to "👚", "Bottoms" to "👖", "Shoes" to "👞",
    "Outerwear" to "🧥", "Accessories" to "💍", "Dresses" to "👗"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WardrobeScreen(navController: NavHostController) {
    val context = LocalContext.current
    val db = remember { EsentieleDatabase.getInstance(context) }
    val dao = remember { db.clothingDao() }
    val scope = rememberCoroutineScope()

    val allItems by dao.getAll().collectAsState(initial = emptyList())

    val categories = listOf("All", "Tops", "Bottoms", "Shoes", "Outerwear", "Accessories", "Dresses")
    var selectedCategory by remember { mutableStateOf("All") }
    var showAddDialog by remember { mutableStateOf(false) }

    val filteredItems = remember(selectedCategory, allItems) {
        if (selectedCategory == "All") allItems else allItems.filter { it.category == selectedCategory }
    }

    Scaffold(
        containerColor = DarkBg,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = ChampagneGold,
                contentColor = Color.Black,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, "Add Item")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text("My Closet", color = IvoryText, fontSize = 28.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Text("${allItems.size} items", color = Color(0xFF9B8E7E), fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Category chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category, color = if (selectedCategory == category) Color.Black else Color.Gray) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = DarkSurface, selectedContainerColor = ChampagneGold
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredItems.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("✨", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Your closet awaits", color = ChampagneGold, fontSize = 20.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Tap + to add your first piece", color = Color.Gray, fontSize = 14.sp)
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredItems, key = { it.id }) { item ->
                        WardrobeItemCard(
                            item = item,
                            onFavorite = {
                                scope.launch { dao.updateFavorite(item.id, !item.isFavorite) }
                            },
                            onDelete = {
                                scope.launch { dao.delete(item) }
                            },
                            onWear = {
                                scope.launch { dao.updateWearCount(item.id, item.timesWorn + 1, System.currentTimeMillis()) }
                            },
                            onCardClick = {
                                navController.navigate(com.esentiele.app.ui.navigation.Screen.ItemFinder.route)
                            }
                        )
                    }
                }
            }
        }
    }

    // Add Item Dialog
    if (showAddDialog) {
        AddItemDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { name, category, color, material, price, formality ->
                scope.launch {
                    dao.insert(
                        ClothingItemEntity(
                            id = UUID.randomUUID().toString(),
                            localImageUri = "",
                            category = category,
                            subCategory = name,
                            primaryColor = color,
                            secondaryColor = null,
                            material = material,
                            pattern = "Solid",
                            season = "All Season",
                            formality = formality,
                            price = price,
                            dateAdded = System.currentTimeMillis(),
                            isFavorite = false
                        )
                    )
                    showAddDialog = false
                }
            }
        )
    }
}

@Composable
private fun WardrobeItemCard(
    item: ClothingItemEntity,
    onFavorite: () -> Unit,
    onDelete: () -> Unit,
    onWear: () -> Unit,
    onCardClick: () -> Unit = {}
) {
    val emoji = categoryEmojis[item.category] ?: "👔"
    val itemColor = try {
        Color(android.graphics.Color.parseColor(item.primaryColor))
    } catch (_: Exception) {
        Color(0xFF666666)
    }

    Card(
        modifier = Modifier.fillMaxWidth().aspectRatio(0.8f).clickable { onCardClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f).clip(RoundedCornerShape(12.dp)).background(Color(0xFF2A2A2A)),
                contentAlignment = Alignment.Center
            ) {
                Text(emoji, fontSize = 48.sp)

                // Delete button (Top Left)
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.align(Alignment.TopStart).size(32.dp)
                ) {
                    Icon(
                        Icons.Outlined.Delete,
                        null, tint = Color.Gray.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Favorite button (Top Right)
                IconButton(
                    onClick = onFavorite,
                    modifier = Modifier.align(Alignment.TopEnd).size(32.dp)
                ) {
                    Icon(
                        if (item.isFavorite) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                        null, tint = if (item.isFavorite) Color(0xFFE57373) else Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        item.subCategory.ifEmpty { item.category },
                        color = IvoryText, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 1
                    )
                    Text(
                        if (item.subCategory.isNotEmpty() && item.subCategory != item.category) "${item.category} • ${item.material}" else item.material,
                        color = Color.Gray, fontSize = 11.sp, maxLines = 1
                    )
                    Text("Worn: ${item.timesWorn}x", color = Color.Gray, fontSize = 11.sp)
                    if (item.price > 0) {
                        Text("₹${item.price}", color = ChampagneGold, fontSize = 11.sp)
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    IconButton(onClick = onWear, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Check, contentDescription = "Wear", tint = ChampagneGold, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(modifier = Modifier.size(14.dp).clip(CircleShape).background(itemColor))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddItemDialog(onDismiss: () -> Unit, onAdd: (String, String, String, String, Double, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Tops") }
    var selectedColor by remember { mutableStateOf("#1A1A1A") }
    var material by remember { mutableStateOf("") }
    var priceStr by remember { mutableStateOf("") }
    var formality by remember { mutableStateOf("Casual") }

    val formalities = listOf("Casual", "Smart Casual", "Formal")

    val cats = listOf("Tops", "Bottoms", "Shoes", "Outerwear", "Accessories", "Dresses")
    val colors = listOf(
        "#F5F5F5" to "White", "#1A1A1A" to "Black", "#1B2A4A" to "Navy",
        "#5D4037" to "Brown", "#C9A96E" to "Gold", "#C62828" to "Red",
        "#558B2F" to "Green", "#1565C0" to "Blue"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = { Text("Add to Closet", color = IvoryText, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = name, onValueChange = { name = it },
                    label = { Text("Item Name", color = Color(0xFF9B8E7E)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ChampagneGold, unfocusedBorderColor = Color(0xFF3D3D3D),
                        focusedTextColor = IvoryText, unfocusedTextColor = IvoryText, cursorColor = ChampagneGold
                    ),
                    singleLine = true
                )

                // Category
                Text("Category", color = Color.Gray, fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                    cats.take(3).forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, fontSize = 11.sp, color = if (selectedCategory == cat) Color.Black else Color.Gray) },
                            colors = FilterChipDefaults.filterChipColors(containerColor = Color(0xFF2A2A2A), selectedContainerColor = ChampagneGold),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                    cats.drop(3).forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, fontSize = 11.sp, color = if (selectedCategory == cat) Color.Black else Color.Gray) },
                            colors = FilterChipDefaults.filterChipColors(containerColor = Color(0xFF2A2A2A), selectedContainerColor = ChampagneGold),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Color picker
                Text("Color", color = Color.Gray, fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    colors.forEach { (hex, _) ->
                        val c = try { Color(android.graphics.Color.parseColor(hex)) } catch (_: Exception) { Color.Gray }
                        Box(
                            modifier = Modifier
                                .size(32.dp).clip(CircleShape).background(c)
                                .then(
                                    if (selectedColor == hex) Modifier.padding(2.dp) else Modifier
                                )
                                .clickable { selectedColor = hex }
                                .then(
                                    if (selectedColor == hex) Modifier.background(c, CircleShape) else Modifier
                                )
                        ) {
                            if (selectedColor == hex) {
                                Box(modifier = Modifier.fillMaxSize().clip(CircleShape).background(c))
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = material, onValueChange = { material = it },
                    label = { Text("Material", color = Color(0xFF9B8E7E)) },
                    placeholder = { Text("e.g. Cotton, Silk, Wool", color = Color(0xFF555555)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ChampagneGold, unfocusedBorderColor = Color(0xFF3D3D3D),
                        focusedTextColor = IvoryText, unfocusedTextColor = IvoryText, cursorColor = ChampagneGold
                    ),
                    singleLine = true
                )

                OutlinedTextField(
                    value = priceStr, onValueChange = { priceStr = it },
                    label = { Text("Price (₹)", color = Color(0xFF9B8E7E)) },
                    placeholder = { Text("e.g. 1500", color = Color(0xFF555555)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ChampagneGold, unfocusedBorderColor = Color(0xFF3D3D3D),
                        focusedTextColor = IvoryText, unfocusedTextColor = IvoryText, cursorColor = ChampagneGold
                    ),
                    singleLine = true
                )

                Text("Formality", color = Color.Gray, fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                    formalities.forEach { form ->
                        FilterChip(
                            selected = formality == form,
                            onClick = { formality = form },
                            label = { Text(form, fontSize = 11.sp, color = if (formality == form) Color.Black else Color.Gray) },
                            colors = FilterChipDefaults.filterChipColors(containerColor = Color(0xFF2A2A2A), selectedContainerColor = ChampagneGold),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onAdd(name.ifBlank { selectedCategory }, selectedCategory, selectedColor, material.ifBlank { "Mixed" }, priceStr.toDoubleOrNull() ?: 0.0, formality) },
                colors = ButtonDefaults.buttonColors(containerColor = ChampagneGold, contentColor = Color.Black)
            ) { Text("Add Item", fontWeight = FontWeight.Bold) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = Color.Gray) }
        }
    )
}
