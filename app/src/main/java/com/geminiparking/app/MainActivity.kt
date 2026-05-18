package com.geminiparking.app

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

private object UiText {
    const val waitingForPhoto = "Waiting for photo..."
    const val idle = "Idle"
    const val photoCaptured = "Photo captured"
    const val recognizing = "Recognizing..."
    const val dialing = "Dialing..."
    const val useDoorbell = "No phone on file, please use the doorbell."
    const val requestingPermission = "Requesting camera permission..."
    const val permissionDenied = "Error: camera permission denied"
    const val photoCanceled = "Photo capture canceled"
    const val noPhoto = "No photo captured"
    const val cameraFailed = "Error: camera capture failed"
    const val temporaryExternal = "Error: temporary external vehicle, contact unavailable!"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                ParkingAssistantScreen(service = MockGemma4ParkingAssistantService())
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ParkingAssistantScreen(service: Gemma4ParkingAssistantService) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var plateStatus by remember { mutableStateOf(UiText.waitingForPhoto) }
    var ownerInfo by remember { mutableStateOf<OwnerLookupResult?>(null) }
    var operationStatus by remember { mutableStateOf(UiText.idle) }
    var operationStatusIsError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var showExternalVehicleError by remember { mutableStateOf(false) }

    fun processPhoto(photo: Bitmap) {
        isLoading = true
        plateStatus = UiText.photoCaptured
        ownerInfo = null
        operationStatus = UiText.recognizing
        operationStatusIsError = false

        scope.launch {
            val match = service.getOwnerInfoByImage(photo)
            isLoading = false
            if (match == null) {
                operationStatus = UiText.temporaryExternal
                operationStatusIsError = true
                showExternalVehicleError = true
                return@launch
            }

            ownerInfo = match
            if (match.phone.isNullOrBlank()) {
                operationStatus = UiText.useDoorbell
                operationStatusIsError = false
                return@launch
            }

            operationStatus = UiText.dialing
            operationStatusIsError = false
            val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                data = android.net.Uri.parse("tel:${match.phone}")
            }
            context.startActivity(dialIntent)
            operationStatus = UiText.idle
        }
    }

    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            if (result.resultCode != Activity.RESULT_OK) {
                plateStatus = UiText.photoCanceled
                operationStatus = UiText.idle
                operationStatusIsError = false
                return@rememberLauncherForActivityResult
            }

            val photo = result.data?.extras?.get("data") as? Bitmap
            if (photo == null) {
                plateStatus = UiText.noPhoto
                operationStatus = UiText.cameraFailed
                operationStatusIsError = true
            } else {
                processPhoto(photo)
            }
        }
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                photoLauncher.launch(cameraIntent)
            } else {
                operationStatus = UiText.permissionDenied
                operationStatusIsError = true
            }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Gemma4 Smart Move-Car Assistant") })
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Current Plate Status: $plateStatus", style = MaterialTheme.typography.bodyLarge)
                        Text(
                            "Owner Phone: ${
                                when {
                                    ownerInfo == null -> "Not matched"
                                    ownerInfo.phone.isNullOrBlank() -> "Not provided"
                                    else -> ownerInfo.phone
                                }
                            }",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text("门洞号: ${ownerInfo?.building ?: "Not matched"}", style = MaterialTheme.typography.bodyLarge)
                        Text("Room: ${ownerInfo?.room ?: "Not matched"}", style = MaterialTheme.typography.bodyLarge)
                        Text(
                            text = "Operation Status: $operationStatus",
                            color = if (operationStatusIsError) Color.Red else MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    FloatingActionButton(
                        onClick = {
                            operationStatus = UiText.requestingPermission
                            operationStatusIsError = false
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        },
                        shape = CircleShape,
                        modifier = Modifier.size(120.dp),
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            Icon(Icons.Default.CameraAlt, contentDescription = "Camera")
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Photo & Move")
                        }
                    }
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.35f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }

    if (showExternalVehicleError) {
        AlertDialog(
            onDismissRequest = { showExternalVehicleError = false },
            title = { Text("Recognition Failed") },
            text = { Text("Error: This vehicle is a temporary external vehicle and Gemma4 cannot retrieve a contact number.") },
            confirmButton = {
                TextButton(onClick = { showExternalVehicleError = false }) {
                    Text("OK")
                }
            }
        )
    }
}
