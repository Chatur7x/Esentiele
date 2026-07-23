package com.esentiele.app.ui.glowup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.esentiele.app.ui.components.PremiumButton
import kotlin.math.roundToInt

@Composable
fun GlowUpScreen() {
    var sliderPosition by remember { mutableFloatStateOf(0.5f) }
    var containerSize by remember { mutableStateOf(Size.Zero) }

    Scaffold(
        containerColor = Color(0xFF121212)
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
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Sparkle",
                    tint = Color(0xFFC9A96E),
                    modifier = Modifier.size(28.dp)
                )
                Text(
                    text = "Outfit Glow Up",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .onGloballyPositioned { layoutCoordinates ->
                        containerSize = layoutCoordinates.size.toSize()
                    }
                    .clipToBounds()
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            val newPosition = sliderPosition + (dragAmount.x / containerSize.width)
                            sliderPosition = newPosition.coerceIn(0f, 1f)
                        }
                    }
            ) {
                // Before Image
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF3E3E3E))
                ) {
                    Text(
                        text = "Before",
                        color = Color.White,
                        modifier = Modifier.padding(16.dp).align(Alignment.TopStart),
                        fontWeight = FontWeight.Bold
                    )
                }

                // After Image (clipped)
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(sliderPosition)
                        .background(Color(0xFF2E4057)) // Vibrant mock color
                ) {
                    Text(
                        text = "After",
                        color = Color.White,
                        modifier = Modifier.padding(16.dp).align(Alignment.TopStart),
                        fontWeight = FontWeight.Bold
                    )
                }

                // Slider Divider
                val dividerOffset = if (containerSize.width > 0) {
                    (sliderPosition * containerSize.width).roundToInt()
                } else 0
                
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(3.dp)
                        .offset { IntOffset(dividerOffset, 0) }
                        .background(Color.White)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .align(Alignment.Center)
                            .offset(x = (-14).dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(2.dp, Color(0xFFC9A96E), CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Surface(
                color = Color(0xFF1E1E1E),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "What Changed",
                        color = Color(0xFFC9A96E),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    val changes = listOf(
                        "Tucked shirt for cleaner silhouette",
                        "Swapped sneakers for chelsea boots",
                        "Added gold watch for accent"
                    )
                    
                    changes.forEach { change ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFC9A96E).copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color(0xFFC9A96E),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Text(
                                text = change,
                                color = Color.White,
                                fontSize = 15.sp,
                                modifier = Modifier.padding(start = 12.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            PremiumButton(
                text = "Try Another Look",
                onClick = { sliderPosition = 0.5f },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
