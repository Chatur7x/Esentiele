package com.esentiele.app.ui.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.esentiele.app.data.local.UserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private val ChampagneGold = Color(0xFFC9A96E)
private val DarkBg = Color(0xFF0D0D0D)
private val DarkSurface = Color(0xFF1A1A1A)
private val IvoryText = Color(0xFFF5F0E8)

@Composable
fun OnboardingScreen(onFinish: () -> Unit = {}) {
    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }
    val scope = rememberCoroutineScope()

    var currentPage by remember { mutableIntStateOf(0) }
    val selectedStyles = remember { mutableStateListOf<String>() }
    var selectedSeason by remember { mutableStateOf("") }
    var selectedBodyType by remember { mutableStateOf("") }
    var budget by remember { mutableFloatStateOf(5000f) }
    var userName by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        userName = userPrefs.userName.first()
    }

    val totalPages = 5

    Scaffold(containerColor = DarkBg) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Progress dots
            if (currentPage in 1..3) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(4) { i ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(if (i == currentPage - 1) 24.dp else 8.dp, 8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (i <= currentPage - 1) ChampagneGold else Color(0xFF3D3D3D))
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Page content with animation
            AnimatedContent(
                targetState = currentPage,
                transitionSpec = {
                    (fadeIn(tween(400)) + slideInHorizontally(tween(400)) { it / 3 })
                        .togetherWith(fadeOut(tween(200)) + slideOutHorizontally(tween(200)) { -it / 3 })
                },
                label = "page",
                modifier = Modifier.weight(1f)
            ) { page ->
                when (page) {
                    0 -> WelcomePage(userName)
                    1 -> StylePreferencePage(selectedStyles)
                    2 -> ColorSeasonPage(selectedSeason) { selectedSeason = it }
                    3 -> BodyBudgetPage(selectedBodyType, budget, { selectedBodyType = it }, { budget = it })
                    4 -> FinishPage(userName, selectedStyles, selectedSeason)
                }
            }

            // Navigation buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (currentPage > 0 && currentPage < totalPages - 1) {
                    TextButton(onClick = { currentPage-- }) {
                        Text("Back", color = Color(0xFF9B8E7E), fontSize = 15.sp)
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                Button(
                    onClick = {
                        if (currentPage < totalPages - 1) {
                            currentPage++
                        } else {
                            scope.launch {
                                userPrefs.completeOnboarding(
                                    styles = selectedStyles.toList(),
                                    season = selectedSeason.ifEmpty { "Warm Autumn" },
                                    bodyType = selectedBodyType.ifEmpty { "Athletic" },
                                    budget = budget.toInt()
                                )
                                onFinish()
                            }
                        }
                    },
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ChampagneGold, contentColor = Color.Black),
                    modifier = Modifier.height(52.dp)
                ) {
                    Text(
                        when (currentPage) {
                            0 -> "Let's Begin"
                            totalPages - 1 -> "Enter the Atelier ✨"
                            else -> "Continue"
                        },
                        fontWeight = FontWeight.Bold, fontSize = 15.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun WelcomePage(userName: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Outlined.AutoAwesome, null, tint = ChampagneGold, modifier = Modifier.size(64.dp))
        Spacer(modifier = Modifier.height(32.dp))
        Text("Welcome, ${userName.ifEmpty { "Stylist" }}", color = ChampagneGold, fontSize = 28.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Let's personalize your experience.\nWe'll ask a few questions to curate\nyour perfect digital wardrobe.",
            color = Color(0xFF9B8E7E), fontSize = 15.sp, textAlign = TextAlign.Center, lineHeight = 24.sp
        )
    }
}

@Composable
private fun StylePreferencePage(selectedStyles: MutableList<String>) {
    val styles = listOf(
        "Minimal" to "🤍", "Classic" to "👔", "Streetwear" to "🧢",
        "Bohemian" to "🌸", "Avant-Garde" to "🎭", "Sporty" to "🏃"
    )
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Text("What's your style vibe?", color = IvoryText, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        Text("Pick 1–3 styles that resonate with you", color = Color(0xFF9B8E7E), fontSize = 14.sp)
        Spacer(modifier = Modifier.height(24.dp))

        styles.chunked(2).forEach { row ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { (styleName, emoji) ->
                    val isSelected = styleName in selectedStyles
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(120.dp)
                            .clickable {
                                if (isSelected) selectedStyles.remove(styleName)
                                else if (selectedStyles.size < 3) selectedStyles.add(styleName)
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) ChampagneGold.copy(alpha = 0.15f) else DarkSurface
                        ),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, ChampagneGold) else null
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(emoji, fontSize = 32.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(styleName, color = if (isSelected) ChampagneGold else IvoryText, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun ColorSeasonPage(selectedSeason: String, onSelect: (String) -> Unit) {
    val seasons = listOf(
        Triple("Spring", "Warm, clear, bright", listOf(Color(0xFFFFC107), Color(0xFFFF7043), Color(0xFF66BB6A), Color(0xFF42A5F5))),
        Triple("Summer", "Cool, muted, soft", listOf(Color(0xFF90CAF9), Color(0xFFCE93D8), Color(0xFF80CBC4), Color(0xFFEF9A9A))),
        Triple("Autumn", "Warm, deep, rich", listOf(Color(0xFF8D6E63), Color(0xFFFF8F00), Color(0xFF558B2F), Color(0xFFBF360C))),
        Triple("Winter", "Cool, vivid, clear", listOf(Color(0xFF1565C0), Color(0xFFC62828), Color(0xFF00695C), Color(0xFF4A148C)))
    )
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Text("Your Color Season", color = IvoryText, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        Text("Which palette feels most like you?", color = Color(0xFF9B8E7E), fontSize = 14.sp)
        Spacer(modifier = Modifier.height(24.dp))

        seasons.forEach { (name, desc, colors) ->
            val isSelected = selectedSeason == name
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(name) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) ChampagneGold.copy(alpha = 0.15f) else DarkSurface
                ),
                border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, ChampagneGold) else null
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(name, color = if (isSelected) ChampagneGold else IvoryText, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(desc, color = Color(0xFF9B8E7E), fontSize = 12.sp)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        colors.forEach { c ->
                            Box(modifier = Modifier.size(28.dp).clip(CircleShape).background(c))
                        }
                    }
                    if (isSelected) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Outlined.CheckCircle, null, tint = ChampagneGold, modifier = Modifier.size(24.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun BodyBudgetPage(
    selectedBody: String, budget: Float,
    onBodySelect: (String) -> Unit, onBudgetChange: (Float) -> Unit
) {
    val bodyTypes = listOf("Petite", "Athletic", "Curvy", "Tall", "Plus-Size")
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Text("About You", color = IvoryText, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        Text("This helps us tailor outfit suggestions", color = Color(0xFF9B8E7E), fontSize = 14.sp)

        Spacer(modifier = Modifier.height(28.dp))
        Text("Body Type", color = Color.Gray, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            bodyTypes.forEach { body ->
                val isSelected = selectedBody == body
                FilterChip(
                    selected = isSelected,
                    onClick = { onBodySelect(body) },
                    label = { Text(body, fontSize = 12.sp, color = if (isSelected) Color.Black else Color.Gray) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = DarkSurface, selectedContainerColor = ChampagneGold
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text("Monthly Fashion Budget", color = Color.Gray, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text("₹${budget.toInt()}", color = ChampagneGold, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        Slider(
            value = budget,
            onValueChange = onBudgetChange,
            valueRange = 1000f..50000f,
            steps = 9,
            colors = SliderDefaults.colors(
                thumbColor = ChampagneGold, activeTrackColor = ChampagneGold,
                inactiveTrackColor = Color(0xFF3D3D3D)
            )
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("₹1,000", color = Color.Gray, fontSize = 11.sp)
            Text("₹50,000", color = Color.Gray, fontSize = 11.sp)
        }
    }
}

@Composable
private fun FinishPage(userName: String, styles: List<String>, season: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("✨", fontSize = 56.sp)
        Spacer(modifier = Modifier.height(24.dp))
        Text("Your Atelier is Ready", color = ChampagneGold, fontSize = 26.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(24.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Style Profile", color = Color.Gray, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(userName.ifEmpty { "Stylist" }, color = IvoryText, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                if (styles.isNotEmpty()) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        styles.forEach { style ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(ChampagneGold.copy(0.15f))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(style, color = ChampagneGold, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
                if (season.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Season: $season", color = Color(0xFF9B8E7E), fontSize = 13.sp)
                }
            }
        }
    }
}
