package com.example.levelupmobile.ui.camera

import android.Manifest
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.*
import com.example.levelupmobile.ui.theme.Black
import com.example.levelupmobile.ui.theme.DarkGray
import com.example.levelupmobile.ui.theme.ElectricBlue
import com.example.levelupmobile.ui.theme.LightGray
import com.example.levelupmobile.ui.theme.WarningYellow
import com.example.levelupmobile.ui.theme.White
import java.io.File
import java.util.concurrent.Executor

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    onPhotoTaken: (Uri) -> Unit,
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
        ) {
            when {
                cameraPermissionState.status.isGranted -> {
                    CameraPreviewContent(onPhotoTaken = onPhotoTaken)
                }
                cameraPermissionState.status.shouldShowRationale -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
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
                            textAlign = TextAlign.Center
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
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
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
                            textAlign = TextAlign.Center
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

@Composable
fun CameraPreviewContent(
    onPhotoTaken: (Uri) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    val previewView = remember { PreviewView(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }

    LaunchedEffect(cameraProviderFuture) {
        val cameraProvider = cameraProviderFuture.get()
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
        } catch (exc: Exception) {
            Log.e("CameraScreen", "Fallo al conectar casos de uso", exc)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())

        IconButton(
            onClick = {
                takePhoto(
                    context = context,
                    imageCapture = imageCapture,
                    onSuccess = { uri ->
                        Log.i("CameraScreen", "Foto tomada: $uri")
                        onPhotoTaken(uri)
                    },
                    onError = {
                        Log.e("CameraScreen", "Error al tomar la foto", it)
                    }
                )
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .size(72.dp)
                .background(White.copy(alpha = 0.7f), CircleShape)
                .border(2.dp, ElectricBlue, CircleShape)
        ) {
            Icon(
                Icons.Default.Camera,
                contentDescription = "Tomar Foto",
                tint = Black,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

private fun takePhoto(
    context: Context,
    imageCapture: ImageCapture,
    onSuccess: (Uri) -> Unit,
    onError: (ImageCaptureException) -> Unit
) {
    val outputFileOptions = ImageCapture.OutputFileOptions.Builder(
        File(
            context.cacheDir,
            "temp_profile_pic.jpg"
        )
    ).build()

    val executor: Executor = ContextCompat.getMainExecutor(context)

    imageCapture.takePicture(
        outputFileOptions,
        executor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val savedUri = outputFileResults.savedUri ?: Uri.EMPTY
                onSuccess(savedUri)
            }

            override fun onError(exception: ImageCaptureException) {
                onError(exception)
            }
        }
    )
}