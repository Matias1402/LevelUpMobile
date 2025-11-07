package cl.duoc.levelupmobile.ui.camera

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.*
import cl.duoc.levelupmobile.ui.theme.*

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    onNavigateBack: () -> Unit
) {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cámara") },
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when {
                cameraPermissionState.status.isGranted -> {
                    // Permiso concedido
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = ElectricBlue,
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Cámara activada",
                        style = MaterialTheme.typography.titleLarge,
                        color = White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Funcionalidad de cámara implementada",
                        style = MaterialTheme.typography.bodyMedium,
                        color = LightGray
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        "Aquí podrías capturar una foto de perfil o escanear productos",
                        style = MaterialTheme.typography.bodySmall,
                        color = LightGray
                    )
                }
                cameraPermissionState.status.shouldShowRationale -> {
                    // Mostrar razón del permiso
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = WarningYellow,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Permiso de cámara necesario",
                            style = MaterialTheme.typography.titleLarge,
                            color = White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Necesitamos acceso a la cámara para que puedas tomar fotos de perfil",
                            style = MaterialTheme.typography.bodyMedium,
                            color = LightGray,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { cameraPermissionState.launchPermissionRequest() },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
                        ) {
                            Text("CONCEDER PERMISO")
                        }
                    }
                }
                else -> {
                    // Solicitar permiso
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.NoPhotography,
                            contentDescription = null,
                            tint = LightGray,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Se requiere permiso de cámara",
                            style = MaterialTheme.typography.titleLarge,
                            color = White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Para usar esta función, concede el permiso de cámara",
                            style = MaterialTheme.typography.bodyMedium,
                            color = LightGray,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { cameraPermissionState.launchPermissionRequest() },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("ACTIVAR CÁMARA")
                        }
                    }
                }
            }
        }
    }
}