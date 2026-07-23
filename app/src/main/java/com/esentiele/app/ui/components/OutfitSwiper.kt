package com.esentiele.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

private val ChampagneGold = Color(0xFFC9A96E)
private val DarkSurface = Color(0xFF1A1A1A)

data class SwipeableOutfit(
    val id: String,
    val name: String,
    val topItem: String,
    val topColor: Long,
    val bottomItem: String,
    val bottomColor: Long,
    val shoesItem: String,
    val shoesColor: Long,
    val accessory: String,
    val stylingTip: String
)

val mockSwipeOutfits = listOf(
    SwipeableOutfit("1", "Evening Elegance", "Silk Ivory Blouse", 0xFFF5F0E8, "Velvet Black Trousers", 0xFF1A1A1A, "Patent Stilettos", 0xFF2C2C2C, "Gold Cuff Bracelet", "Monochrome palette with metallic accent creates timeless evening sophistication."),
    SwipeableOutfit("2", "Weekend Explorer", "Sage Linen Shirt", 0xFF8FBC8F, "Tan Chinos", 0xFFD2B48C, "White Leather Sneakers", 0xFFF5F5F5, "Woven Belt", "Earth tones and natural fabrics for effortless weekend style."),
    SwipeableOutfit("3", "Office Power", "Navy Blazer", 0xFF1B2A4A, "Charcoal Slim Pants", 0xFF36454F, "Oxford Brogues", 0xFF5C4033, "Silk Pocket Square", "Dark tonal layers project authority while remaining approachable."),
    SwipeableOutfit("4", "Date Night", "Burgundy Cashmere V-Neck", 0xFF722F37, "Dark Indigo Jeans", 0xFF1C1C3A, "Chelsea Boots", 0xFF3B2F2F, "Silver Watch", "Rich jewel tone paired with dark denim signals effortless confidence."),
    SwipeableOutfit("5", "Coastal Brunch", "Cream Knit Polo", 0xFFFFF8DC, "Relaxed Linen Shorts", 0xFFE8DCC8, "Espadrilles", 0xFFC9A96E, "Aviator Sunglasses", "Tonal neutrals create a luxe Mediterranean aesthetic."),
    SwipeableOutfit("6", "Gallery Opening", "Black Turtleneck", 0xFF0D0D0D, "Tailored Wide-Leg Pants", 0xFF2A2A2A, "Pointed Loafers", 0xFF1A1A1A, "Minimalist Gold Chain", "Head-to-toe black with gold accent channels modern art-world chic.")
)

@Composable
fun OutfitSwiper(
    outfits: List<SwipeableOutfit> = mockSwipeOutfits,
    onSwipeRight: (SwipeableOutfit) -> Unit = {},
    onSwipeLeft: (SwipeableOutfit) -> Unit = {}
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    var showResult by remember { mutableStateOf<String?>(null) }

    val animatedOffset by animateFloatAsState(
        targetValue = if (isDragging) offsetX else 0f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 300f),
        label = "swipe_offset"
    )

    val rotation = (animatedOffset / 30f).coerceIn(-15f, 15f)
    val threshold = with(LocalDensity.current) { 150.dp.toPx() }

    LaunchedEffect(showResult) {
        if (showResult != null) {
            delay(600)
            showResult = null
            if (currentIndex < outfits.size - 1) currentIndex++
        }
    }

    Box(
        modifier = Modifier.fillMaxWidth().height(480.dp),
        contentAlignment = Alignment.Center
    ) {
        if (currentIndex >= outfits.size) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("✨", fontSize = 48.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text("No more outfits!", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Generate more looks in the Stylist tab.", color = Color.Gray, fontSize = 14.sp)
            }
        } else {
            val outfit = outfits[currentIndex]

            // Swipe direction indicator
            AnimatedVisibility(
                visible = animatedOffset > 50f,
                enter = fadeIn(), exit = fadeOut(),
                modifier = Modifier.align(Alignment.CenterStart).padding(start = 24.dp)
            ) {
                Box(
                    modifier = Modifier.size(64.dp).clip(CircleShape).background(Color(0xFF2E7D32)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                }
            }

            AnimatedVisibility(
                visible = animatedOffset < -50f,
                enter = fadeIn(), exit = fadeOut(),
                modifier = Modifier.align(Alignment.CenterEnd).padding(end = 24.dp)
            ) {
                Box(
                    modifier = Modifier.size(64.dp).clip(CircleShape).background(Color(0xFFC62828)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                }
            }

            // Main card
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight()
                    .offset { IntOffset(animatedOffset.roundToInt(), 0) }
                    .graphicsLayer { rotationZ = rotation }
                    .pointerInput(currentIndex) {
                        detectHorizontalDragGestures(
                            onDragStart = { isDragging = true },
                            onDragEnd = {
                                isDragging = false
                                if (offsetX.absoluteValue > threshold) {
                                    if (offsetX > 0) {
                                        showResult = "loved"
                                        onSwipeRight(outfit)
                                    } else {
                                        showResult = "skipped"
                                        onSwipeLeft(outfit)
                                    }
                                }
                                offsetX = 0f
                            },
                            onDragCancel = { isDragging = false; offsetX = 0f },
                            onHorizontalDrag = { _, dragAmount -> offsetX += dragAmount }
                        )
                    },
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                elevation = CardDefaults.cardElevation(12.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(outfit.name, color = ChampagneGold, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(20.dp))

                    // Top item
                    Box(
                        modifier = Modifier.fillMaxWidth().height(80.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(outfit.topColor))
                            .border(1.dp, Color(0xFF3D3D3D), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) { Text(outfit.topItem, color = if (outfit.topColor > 0xFF888888) Color.Black else Color.White, fontWeight = FontWeight.Medium, fontSize = 14.sp) }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Bottom item
                    Box(
                        modifier = Modifier.fillMaxWidth().height(90.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(outfit.bottomColor))
                            .border(1.dp, Color(0xFF3D3D3D), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) { Text(outfit.bottomItem, color = if (outfit.bottomColor > 0xFF888888) Color.Black else Color.White, fontWeight = FontWeight.Medium, fontSize = 14.sp) }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Shoes
                    Box(
                        modifier = Modifier.fillMaxWidth().height(60.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(outfit.shoesColor))
                            .border(1.dp, Color(0xFF3D3D3D), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) { Text(outfit.shoesItem, color = if (outfit.shoesColor > 0xFF888888) Color.Black else Color.White, fontWeight = FontWeight.Medium, fontSize = 14.sp) }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("🪩 ${outfit.accessory}", color = ChampagneGold, fontSize = 13.sp)

                    Spacer(modifier = Modifier.weight(1f))

                    // Styling tip
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D0D0D)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "💡 ${outfit.stylingTip}",
                            color = Color(0xFF9B8E7E),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(12.dp),
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                        Text("← SKIP", color = Color(0xFFC62828), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(48.dp))
                        Text("LOVE IT →", color = Color(0xFF2E7D32), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Result overlay
            AnimatedVisibility(visible = showResult != null, enter = fadeIn(), exit = fadeOut()) {
                Box(
                    modifier = Modifier.size(120.dp).clip(CircleShape)
                        .background(if (showResult == "loved") Color(0xFF2E7D32) else Color(0xFFC62828)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (showResult == "loved") Icons.Default.Check else Icons.Default.Close,
                        contentDescription = null, tint = Color.White, modifier = Modifier.size(64.dp)
                    )
                }
            }
        }
    }
}
