package cl.duoc.levelupmobile.ui.profile

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import cl.duoc.levelupmobile.ui.theme.*
import coil.compose.rememberAsyncImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCamera: () -> Unit,
    onLogout: () -> Unit,
    navBackStackEntry: NavBackStackEntry,
    viewModel: ProfileViewModel = viewModel()
) {
    val currentUser by viewModel.currentUser.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }

    var profilePicUri by remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(navBackStackEntry) {
        val savedStateHandle = navBackStackEntry.savedStateHandle
        val newPicUriString = savedStateHandle.get<String>("new_profile_pic_uri")

        if (newPicUriString != null) {
            profilePicUri = Uri.parse(newPicUriString)

            // Opcional: Avisa a tu ViewModel que guarde esta URI
            // viewModel.saveProfilePicUri(newPicUriString)

            savedStateHandle.remove<String>("new_profile_pic_uri")
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon = {
                Icon(Icons.Default.Logout, contentDescription = null, tint = ErrorRed)
            },
            title = { Text("Cerrar sesión") },
            text = { Text("¿Estás seguro que deseas cerrar sesión?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.logout()
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) {
                    Text("CERRAR SESIÓN")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("CANCELAR", color = LightGray)
                }
            },
            containerColor = DarkGray
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkGray,
                    titleContentColor = White,
                    navigationIconContentColor = ElectricBlue
                )
            )
        },
        containerColor = Black
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            currentUser?.let { user ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkGray),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(ElectricBlue.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (profilePicUri != null) {
                                Image(
                                    painter = rememberAsyncImagePainter(model = profilePicUri),
                                    contentDescription = "Foto de Perfil",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    tint = ElectricBlue,
                                    modifier = Modifier.size(60.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        IconButton(onClick = onNavigateToCamera) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.CameraAlt,
                                    contentDescription = "Cambiar foto",
                                    tint = ElectricBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "Cambiar foto",
                                    color = ElectricBlue,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            user.name,
                            style = MaterialTheme.typography.headlineMedium,
                            color = White,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            user.email,
                            style = MaterialTheme.typography.bodyMedium,
                            color = LightGray
                        )

                        if (user.isDuocStudent) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                color = NeonGreen.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(
                                        horizontal = 16.dp,
                                        vertical = 8.dp
                                    ),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Star,
                                        contentDescription = null,
                                        tint = NeonGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        "Estudiante Duoc - 20% OFF",
                                        color = NeonGreen,
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = ElectricBlue.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Puntos LevelUp",
                                style = MaterialTheme.typography.titleMedium,
                                color = White
                            )
                            Text(
                                "Canjea por descuentos",
                                style = MaterialTheme.typography.bodySmall,
                                color = LightGray
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = NeonGreen,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                user.levelUpPoints.toString(),
                                style = MaterialTheme.typography.headlineLarge,
                                color = NeonGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkGray),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            "Información personal",
                            style = MaterialTheme.typography.titleMedium,
                            color = White,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        ProfileInfoRow(
                            icon = Icons.Default.Person,
                            label = "Nombre",
                            value = user.name
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        ProfileInfoRow(
                            icon = Icons.Default.Email,
                            label = "Correo",
                            value = user.email
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        ProfileInfoRow(
                            icon = Icons.Default.Cake, label = "Edad",
                            value = "${user.age} años"
                        )
                        if (user.referralCode.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))

                            ProfileInfoRow(
                                icon = Icons.Default.CardGiftcard,
                                label = "Código de referido",
                                value = user.referralCode
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ErrorRed,
                        contentColor = White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "CERRAR SESIÓN",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun ProfileInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = ElectricBlue,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                label,
                style = MaterialTheme.typography.bodySmall,
                color = LightGray
            )
            Text(
                value,
                style = MaterialTheme.typography.bodyLarge,
                color = White
            )
        }
    }
}