package com.esentiele.app.ui.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.esentiele.app.data.local.UserPreferences
import kotlinx.coroutines.launch

private val ChampagneGold = Color(0xFFC9A96E)
private val DarkBg = Color(0xFF0D0D0D)
private val DarkSurface = Color(0xFF1A1A1A)
private val IvoryText = Color(0xFFF5F0E8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    onNavigateToLogin: () -> Unit,
    onSignUpSuccess: () -> Unit
) {
    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { isVisible = true }

    Scaffold(containerColor = DarkBg) { padding ->
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(tween(600)) + slideInVertically(tween(600)) { 40 }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(60.dp))

                Icon(Icons.Outlined.AutoAwesome, null, tint = ChampagneGold, modifier = Modifier.size(48.dp))

                Spacer(modifier = Modifier.height(16.dp))

                Text("Create Account", color = IvoryText, fontSize = 28.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text("Join the Esentiele atelier", color = Color(0xFF9B8E7E), fontSize = 14.sp, letterSpacing = 1.sp)

                Spacer(modifier = Modifier.height(40.dp))

                // Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; errorMessage = null },
                    label = { Text("Full Name", color = Color(0xFF9B8E7E)) },
                    leadingIcon = { Icon(Icons.Outlined.Person, null, tint = ChampagneGold.copy(0.7f)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ChampagneGold, unfocusedBorderColor = Color(0xFF2A2A2A),
                        focusedTextColor = IvoryText, unfocusedTextColor = IvoryText,
                        cursorColor = ChampagneGold, focusedContainerColor = DarkSurface, unfocusedContainerColor = DarkSurface
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; errorMessage = null },
                    label = { Text("Email", color = Color(0xFF9B8E7E)) },
                    leadingIcon = { Icon(Icons.Outlined.Email, null, tint = ChampagneGold.copy(0.7f)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ChampagneGold, unfocusedBorderColor = Color(0xFF2A2A2A),
                        focusedTextColor = IvoryText, unfocusedTextColor = IvoryText,
                        cursorColor = ChampagneGold, focusedContainerColor = DarkSurface, unfocusedContainerColor = DarkSurface
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Password
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; errorMessage = null },
                    label = { Text("Password", color = Color(0xFF9B8E7E)) },
                    leadingIcon = { Icon(Icons.Outlined.Lock, null, tint = ChampagneGold.copy(0.7f)) },
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(if (showPassword) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility, null, tint = Color(0xFF666666))
                        }
                    },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ChampagneGold, unfocusedBorderColor = Color(0xFF2A2A2A),
                        focusedTextColor = IvoryText, unfocusedTextColor = IvoryText,
                        cursorColor = ChampagneGold, focusedContainerColor = DarkSurface, unfocusedContainerColor = DarkSurface
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Confirm Password
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it; errorMessage = null },
                    label = { Text("Confirm Password", color = Color(0xFF9B8E7E)) },
                    leadingIcon = { Icon(Icons.Outlined.Lock, null, tint = ChampagneGold.copy(0.7f)) },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ChampagneGold, unfocusedBorderColor = Color(0xFF2A2A2A),
                        focusedTextColor = IvoryText, unfocusedTextColor = IvoryText,
                        cursorColor = ChampagneGold, focusedContainerColor = DarkSurface, unfocusedContainerColor = DarkSurface
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                )

                AnimatedVisibility(visible = errorMessage != null) {
                    Text(errorMessage ?: "", color = Color(0xFFE57373), fontSize = 13.sp, modifier = Modifier.padding(top = 12.dp))
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Sign Up Button
                Button(
                    onClick = {
                        when {
                            name.isBlank() || email.isBlank() || password.isBlank() -> {
                                errorMessage = "Please fill in all fields"
                            }
                            !email.contains("@") -> {
                                errorMessage = "Please enter a valid email"
                            }
                            password.length < 6 -> {
                                errorMessage = "Password must be at least 6 characters"
                            }
                            password != confirmPassword -> {
                                errorMessage = "Passwords do not match"
                            }
                            else -> {
                                isLoading = true
                                scope.launch {
                                    userPrefs.signUp(name.trim(), email.trim(), password)
                                    isLoading = false
                                    onSignUpSuccess()
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ChampagneGold, contentColor = Color.Black),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.Black, strokeWidth = 2.dp)
                    } else {
                        Text("Create Account", fontWeight = FontWeight.Bold, fontSize = 16.sp, letterSpacing = 1.sp)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                    Text("Already have an account? ", color = Color(0xFF9B8E7E), fontSize = 14.sp)
                    Text("Sign In", color = ChampagneGold, fontSize = 14.sp, fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onNavigateToLogin() })
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
