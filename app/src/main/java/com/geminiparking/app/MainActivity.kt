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

    var plateStatus by remember { mutableStateOf("Waiting for photo...") }
    var ownerPhone by remember { mutableStateOf<String?>(null) }
    var operationStatus by remember { mutableStateOf("Idle") }
    var isLoading by remember { mutableStateOf(false) }
    var showExternalVehicleError by remember { mutableStateOf(false) }

    fun processPhoto(photo: Bitmap) {
        isLoading = true
        plateStatus = "Photo captured"
        ownerPhone = null
        operationStatus = "Recognizing..."

        scope.launch {
            val phone = service.getPhoneNumberByImage(photo)
            isLoading = false
            if (phone == null) {
                operationStatus = "Error: temporary external vehicle, contact unavailable!"
                showExternalVehicleError = true
                return@launch
            }

            ownerPhone = phone
            operationStatus = "Dialing..."
            val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                data = android.net.Uri.parse("tel:$phone")
            }
            context.startActivity(dialIntent)
        }
    }

    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            if (result.resultCode != Activity.RESULT_OK) {
                plateStatus = "Photo capture canceled"
                operationStatus = "Idle"
                return@rememberLauncherForActivityResult
            }

            val photo = result.data?.extras?.get("data") as? Bitmap
            if (photo == null) {
                plateStatus = "No photo captured"
                operationStatus = "Error: camera capture failed"
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
                operationStatus = "Error: camera permission denied"
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
                        Text("Owner Phone: ${ownerPhone ?: "Not matched"}", style = MaterialTheme.typography.bodyLarge)
                        Text(
                            text = "Operation Status: $operationStatus",
                            color = if (operationStatus.startsWith("Error")) Color.Red else MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    FloatingActionButton(
                        onClick = {
                            operationStatus = "Requesting camera permission..."
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
