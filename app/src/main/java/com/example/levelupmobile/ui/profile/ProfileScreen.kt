package com.example.levelupmobile.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.levelupmobile.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToLogin: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkGray,
                    titleContentColor = White
                )
            )
        },
        containerColor = Black
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                // MUESTRA ESTO SI ESTÁ CARGANDO (En vez de negro)
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = ElectricBlue
                )
            } else if (currentUser == null) {
                // MUESTRA ESTO SI NO HAY DATOS (En vez de negro)
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.ErrorOutline,
                        contentDescription = null,
                        tint = ErrorRed,
                        modifier = Modifier.size(60.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No se pudo cargar el perfil",
                        color = LightGray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.loadUserProfile() }) {
                        Text("REINTENTAR")
                    }
                }
            } else {
                // MUESTRA EL PERFIL REAL
                currentUser?.let { user ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Avatar
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(ElectricBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = user.name.take(1).uppercase(),
                                style = MaterialTheme.typography.displayMedium,
                                color = Black,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Nombre y Email
                        Text(
                            text = user.name,
                            style = MaterialTheme.typography.headlineMedium,
                            color = White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = user.email,
                            style = MaterialTheme.typography.bodyLarge,
                            color = LightGray
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Tarjeta de Puntos
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = DarkGray),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(24.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Mis Puntos LevelUp", color = LightGray)
                                    Text(
                                        "${user.levelUpPoints} PTS",
                                        style = MaterialTheme.typography.headlineMedium,
                                        color = NeonGreen,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Icon(
                                    Icons.Default.Stars,
                                    contentDescription = null,
                                    tint = NeonGreen,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // Botón Cerrar Sesión
                        Button(
                            onClick = {
                                viewModel.logout()
                                onNavigateToLogin()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ErrorRed,
                                contentColor = White
                            )
                        ) {
                            Icon(Icons.Default.Logout, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("CERRAR SESIÓN")
                        }
                    }
                }
            }
        }
    }
}