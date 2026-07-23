package com.esentiele.app.ui.maskeditor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.AutoFixHigh
import androidx.compose.material.icons.outlined.Brush
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Undo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val ChampagneGold = Color(0xFFC9A96E)
private val DarkSurface = Color(0xFF1A1A1A)
private val DarkBackground = Color(0xFF0D0D0D)
private val IvoryText = Color(0xFFF5F0E8)

data class DrawPoint(val offset: Offset, val isEraser: Boolean, val radius: Float)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaskEditorScreen() {
    var isEraserMode by remember { mutableStateOf(true) }
    var brushSize by remember { mutableFloatStateOf(24f) }
    val drawPoints = remember { mutableStateListOf<DrawPoint>() }
    var showSaved by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = DarkBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Outlined.AutoFixHigh, contentDescription = null, tint = ChampagneGold, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text("Clean Up", color = IvoryText, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Use the eraser to remove background artifacts. Use restore to bring back accidentally erased areas.",
                color = Color(0xFF9B8E7E), fontSize = 13.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Tool selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Eraser tool
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { isEraserMode = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isEraserMode) ChampagneGold.copy(alpha = 0.2f) else DarkSurface
                    ),
                    border = if (isEraserMode) androidx.compose.foundation.BorderStroke(2.dp, ChampagneGold) else null
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Outlined.Delete, contentDescription = null, tint = if (isEraserMode) ChampagneGold else Color.Gray)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Eraser", color = if (isEraserMode) ChampagneGold else Color.Gray, fontWeight = FontWeight.Medium)
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Restore tool
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { isEraserMode = false },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (!isEraserMode) ChampagneGold.copy(alpha = 0.2f) else DarkSurface
                    ),
                    border = if (!isEraserMode) androidx.compose.foundation.BorderStroke(2.dp, ChampagneGold) else null
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Outlined.Brush, contentDescription = null, tint = if (!isEraserMode) ChampagneGold else Color.Gray)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Restore", color = if (!isEraserMode) ChampagneGold else Color.Gray, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Brush size slider
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Brush:", color = Color.Gray, fontSize = 13.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Slider(
                    value = brushSize,
                    onValueChange = { brushSize = it },
                    valueRange = 8f..60f,
                    modifier = Modifier.weight(1f),
                    colors = SliderDefaults.colors(
                        thumbColor = ChampagneGold,
                        activeTrackColor = ChampagneGold,
                        inactiveTrackColor = Color(0xFF3D3D3D)
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size((brushSize / 2).dp)
                        .clip(CircleShape)
                        .background(if (isEraserMode) Color.Red.copy(alpha = 0.5f) else ChampagneGold.copy(alpha = 0.5f))
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Canvas editing area
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A))
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(isEraserMode, brushSize) {
                                detectDragGestures { change, _ ->
                                    drawPoints.add(
                                        DrawPoint(
                                            offset = change.position,
                                            isEraser = isEraserMode,
                                            radius = brushSize
                                        )
                                    )
                                }
                            }
                    ) {
                        // Draw mock garment (a navy blue rectangle in the center)
                        val garmentLeft = size.width * 0.15f
                        val garmentTop = size.height * 0.1f
                        val garmentWidth = size.width * 0.7f
                        val garmentHeight = size.height * 0.8f

                        drawRoundRect(
                            color = Color(0xFF1B2A4A),
                            topLeft = Offset(garmentLeft, garmentTop),
                            size = androidx.compose.ui.geometry.Size(garmentWidth, garmentHeight),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(24f, 24f)
                        )

                        // Mock background noise at edges
                        drawCircle(color = Color(0xFF555555), radius = 30f, center = Offset(garmentLeft - 10f, garmentTop + 50f))
                        drawCircle(color = Color(0xFF666666), radius = 25f, center = Offset(garmentLeft + garmentWidth + 8f, garmentTop + 100f))
                        drawCircle(color = Color(0xFF4A4A4A), radius = 20f, center = Offset(garmentLeft + 20f, garmentTop + garmentHeight + 5f))

                        // Draw user interactions
                        for (point in drawPoints) {
                            if (point.isEraser) {
                                drawCircle(
                                    color = Color(0xFF2A2A2A),
                                    radius = point.radius,
                                    center = point.offset
                                )
                            } else {
                                drawCircle(
                                    color = Color(0xFF1B2A4A).copy(alpha = 0.8f),
                                    radius = point.radius,
                                    center = point.offset
                                )
                            }
                        }
                    }

                    // Crosshair hint
                    if (drawPoints.isEmpty()) {
                        Text(
                            "Touch & drag to erase background artifacts",
                            color = Color.Gray.copy(alpha = 0.6f),
                            fontSize = 13.sp,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action buttons
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = { if (drawPoints.isNotEmpty()) drawPoints.removeAt(drawPoints.lastIndex) },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Gray),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3D3D3D))
                ) {
                    Icon(Icons.Outlined.Undo, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Undo")
                }

                Button(
                    onClick = { showSaved = true },
                    modifier = Modifier
                        .weight(2f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ChampagneGold, contentColor = Color.Black)
                ) {
                    Text("Save Clean Image", fontWeight = FontWeight.Bold)
                }
            }

            // Save confirmation
            AnimatedVisibility(visible = showSaved, enter = fadeIn()) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B5E20).copy(alpha = 0.3f))
                ) {
                    Text(
                        "✓ Image saved to your wardrobe",
                        color = Color(0xFF81C784),
                        modifier = Modifier.padding(16.dp),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
