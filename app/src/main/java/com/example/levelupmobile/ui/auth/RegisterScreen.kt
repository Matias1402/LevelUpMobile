package cl.duoc.levelupmobile.ui.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.levelupmobile.ui.theme.*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var referralCode by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val registerResult by viewModel.registerResult.observeAsState()

    LaunchedEffect(registerResult) {
        registerResult?.onSuccess { message ->
            successMessage = message
            showSuccessDialog = true
        }?.onFailure { error ->
            errorMessage = error.message ?: "Error desconocido"
            showError = true
        }
    }

// Success Dialog
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { },
            icon = {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen)
            },
            title = { Text("¡Registro exitoso!") },
            text = { Text(successMessage) },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onRegisterSuccess()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
                ) {
                    Text("IR A LOGIN")
                }
            },
            containerColor = DarkGray
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Logo/Título con animación
        AnimatedVisibility(
            visible = true,
            enter = fadeIn() + slideInVertically()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = null,
                    tint = NeonGreen,
                    modifier = Modifier.size(60.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "LEVEL-UP GAMER",
                    style = MaterialTheme.typography.displayMedium,
                    color = ElectricBlue
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Crea tu cuenta",
                    style = MaterialTheme.typography.bodyLarge,
                    color = LightGray
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Error message
        AnimatedVisibility(visible = showError) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = ErrorRed.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Error, contentDescription = null, tint = ErrorRed)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(errorMessage, color = ErrorRed)
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = { showError = false }) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = ErrorRed)
                    }
                }
            }
        }

        // Campo Nombre
        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                showError = false
            },
            label = { Text("Nombre completo") },
            leadingIcon = {
                Icon(Icons.Default.Person, contentDescription = null, tint = ElectricBlue)
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ElectricBlue,
                unfocusedBorderColor = LightGray,
                focusedTextColor = White,
                unfocusedTextColor = White,
                focusedLabelColor = ElectricBlue,
                unfocusedLabelColor = LightGray
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo Email
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                showError = false
            },
            label = { Text("Correo electrónico") },
            leadingIcon = {
                Icon(Icons.Default.Email, contentDescription = null, tint = ElectricBlue)
            },
            supportingText = {
                if (email.endsWith("@duocuc.cl", ignoreCase = true)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = NeonGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("¡20% descuento de por vida!", color = NeonGreen)
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ElectricBlue,
                unfocusedBorderColor = LightGray,
                focusedTextColor = White,
                unfocusedTextColor = White,
                focusedLabelColor = ElectricBlue,
                unfocusedLabelColor = LightGray
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo Edad
        OutlinedTextField(
            value = age,
            onValueChange = {
                if (it.all { char -> char.isDigit() } && it.length <= 3) {
                    age = it
                    showError = false
                }
            },
            label = { Text("Edad") },
            leadingIcon = {
                Icon(Icons.Default.Cake, contentDescription = null, tint = ElectricBlue)
            },
            supportingText = {
                Text("Debes ser mayor de 18 años", color = LightGray)
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ElectricBlue,
                unfocusedBorderColor = LightGray,
                focusedTextColor = White,
                unfocusedTextColor = White,
                focusedLabelColor = ElectricBlue,
                unfocusedLabelColor = LightGray
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo Contraseña
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                showError = false
            },
            label = { Text("Contraseña") },
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = null, tint = ElectricBlue)
            },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null,
                        tint = ElectricBlue
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            supportingText = {
                Text("Mínimo 6 caracteres", color = LightGray)
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ElectricBlue,
                unfocusedBorderColor = LightGray,
                focusedTextColor = White,
                unfocusedTextColor = White,
                focusedLabelColor = ElectricBlue,
                unfocusedLabelColor = LightGray
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo Código de Referido (Opcional)
        OutlinedTextField(
            value = referralCode,
            onValueChange = { referralCode = it.uppercase() },
            label = { Text("Código de referido (opcional)") },
            leadingIcon = {
                Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = NeonGreen)
            },
            supportingText = {
                Text("Gana puntos LevelUp", color = LightGray)
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonGreen,
                unfocusedBorderColor = LightGray,
                focusedTextColor = White,
                unfocusedTextColor = White,
                focusedLabelColor = NeonGreen,
                unfocusedLabelColor = LightGray
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Botón Registrarse con animación
        var isPressed by remember { mutableStateOf(false) }
        val scale by animateFloatAsState(
            targetValue = if (isPressed) 0.95f else 1f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
            label = "button_scale"
        )

        Button(
            onClick = {
                isPressed = true
                viewModel.register(name, email, age, password, referralCode)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                },
            colors = ButtonDefaults.buttonColors(
                containerColor = NeonGreen,
                contentColor = Black
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp,
                pressedElevation = 8.dp
            )
        ) {
            Icon(Icons.Default.HowToReg, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("REGISTRARSE", style = MaterialTheme.typography.titleMedium)
        }

        LaunchedEffect(isPressed) {
            if (isPressed) {
                kotlinx.coroutines.delay(200)
                isPressed = false
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Link a Login
        TextButton(
            onClick = onNavigateToLogin,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "¿Ya tienes cuenta? Inicia sesión",
                color = ElectricBlue,
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}