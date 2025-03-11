package com.example.bluecontrol.bluetooth

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat

// Custom color scheme
private val BluetoothPrimary = Color(0xFF3F51B5) // Indigo
private val BluetoothPrimaryVariant = Color(0xFF303F9F) // Darker Indigo
private val BluetoothSecondary = Color(0xFF03A9F4) // Light Blue
private val BluetoothBackground = Color(0xFFF5F5F7) // Light Gray
private val BluetoothSurface = Color(0xFFFFFFFF) // White
private val BluetoothError = Color(0xFFE53935) // Red
private val BluetoothConnected = Color(0xFF4CAF50) // Green
private val BluetoothConnectedContainer = Color(0xFFE8F5E9) // Light Green
private val BluetoothErrorContainer = Color(0xFFFFEBEE) // Light Red
private val BluetoothInputContainer = Color(0xFFF0F7FF) // Very Light Blue

@Composable
fun ValueControlRow(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    currentDisplayValue: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = BluetoothInputContainer
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF455A64), // Dark Blue-Gray
                modifier = Modifier.width(110.dp)
            )

            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color(0xFF263238), // Very Dark Blue-Gray
                    unfocusedTextColor = Color(0xFF263238), // Very Dark Blue-Gray
                    focusedBorderColor = BluetoothSecondary,
                    unfocusedBorderColor = Color(0xFFBDBDBD) // Gray
                )
            )

            if (currentDisplayValue.isNotEmpty()) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = currentDisplayValue,
                    fontWeight = FontWeight.Bold,
                    color = BluetoothPrimary,
                    modifier = Modifier.width(60.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestBluetoothScreen(bluetoothViewModel: BluetoothViewModel) {
    var currentValue by remember { mutableStateOf("") }
    var voltageValue by remember { mutableStateOf("") }
    var frequencyValue by remember { mutableStateOf("") }
    var lFreqValue by remember { mutableStateOf("") }
    var rFreqValue by remember { mutableStateOf("") }
    var volumeValue by remember { mutableStateOf("") }

    val context = LocalContext.current

    // Show toast when connected
    LaunchedEffect(key1 = bluetoothViewModel.connected.value) {
        if (bluetoothViewModel.connected.value) {
            Toast.makeText(
                context,
                "Connected to ${bluetoothViewModel.connectedDevice.value}",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                context,
                "Disconnected",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Bluetooth Control",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BluetoothPrimary
                )
            )
        },
        containerColor = BluetoothBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Status Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (bluetoothViewModel.connected.value)
                        BluetoothConnectedContainer
                    else
                        BluetoothErrorContainer
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 3.dp
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = if (bluetoothViewModel.connected.value) "Connected" else "Disconnected",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (bluetoothViewModel.connected.value)
                            BluetoothConnected
                        else
                            BluetoothError
                    )
                    if (bluetoothViewModel.connected.value) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Device: ${bluetoothViewModel.connectedDevice.value}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF33691E) // Dark Green
                        )
                    }
                }
            }

            // Scan and Disconnect buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        if (bluetoothViewModel.scanning.value) {
                            bluetoothViewModel.stopScan()
                        } else {
                            bluetoothViewModel.startScan()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (bluetoothViewModel.scanning.value)
                            BluetoothError
                        else
                            BluetoothPrimary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp
                    )
                ) {
                    Text(if (bluetoothViewModel.scanning.value) "Stop Scan" else "Start Scan")
                }

                if (bluetoothViewModel.connected.value) {
                    Button(
                        onClick = { bluetoothViewModel.disconnect() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BluetoothError
                        ),
                        shape = RoundedCornerShape(8.dp),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 4.dp
                        )
                    ) {
                        Text("Disconnect")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Device List
            if (!bluetoothViewModel.connected.value) {
                Text(
                    text = "Available Devices",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF263238), // Very Dark Blue-Gray
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE1F5FE)) // Very Light Blue
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(bluetoothViewModel.devices.toList()) { device ->
                        if (ActivityCompat.checkSelfPermission(
                                context,
                                Manifest.permission.BLUETOOTH_CONNECT
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 2.dp
                                ),
                                colors = CardDefaults.cardColors(
                                    containerColor = BluetoothSurface
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = device.name ?: "Unnamed device",
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF263238) // Very Dark Blue-Gray
                                        )
                                        Text(
                                            text = device.address,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF607D8B) // Blue-Gray
                                        )
                                    }
                                    FilledTonalButton(
                                        onClick = { bluetoothViewModel.connectToDevice(device) },
                                        colors = ButtonDefaults.filledTonalButtonColors(
                                            containerColor = BluetoothSecondary,
                                            contentColor = Color.White
                                        )
                                    ) {
                                        Text("Connect")
                                    }
                                }
                            }
                        }
                    }

                    if (bluetoothViewModel.devices.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (bluetoothViewModel.scanning.value)
                                        "Scanning for devices..."
                                    else
                                        "No devices found. Press Start Scan to begin searching.",
                                    color = Color(0xFF78909C), // Light Blue-Gray
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            // Connected Device Controls
            if (bluetoothViewModel.connected.value) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = BluetoothSurface
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 3.dp
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        // Current values display
                        Text(
                            text = "Current Values",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = BluetoothPrimary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Divider(
                            color = Color(0xFFE0E0E0), // Light Gray
                            thickness = 1.dp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item {
                                ValueControlRow(
                                    label = "Current:",
                                    value = currentValue,
                                    onValueChange = { currentValue = it },
                                    currentDisplayValue = bluetoothViewModel.currentValue.value
                                )
                            }

                            item {
                                ValueControlRow(
                                    label = "Voltage:",
                                    value = voltageValue,
                                    onValueChange = { voltageValue = it },
                                    currentDisplayValue = bluetoothViewModel.voltageValue.value
                                )
                            }

                            item {
                                ValueControlRow(
                                    label = "Frequency:",
                                    value = frequencyValue,
                                    onValueChange = { frequencyValue = it },
                                    currentDisplayValue = bluetoothViewModel.frequencyValue.value
                                )
                            }

                            item {
                                ValueControlRow(
                                    label = "L_Frequency:",
                                    value = lFreqValue,
                                    onValueChange = { lFreqValue = it },
                                    currentDisplayValue = bluetoothViewModel.lFreqValue.value
                                )
                            }

                            item {
                                ValueControlRow(
                                    label = "R_Frequency:",
                                    value = rFreqValue,
                                    onValueChange = { rFreqValue = it },
                                    currentDisplayValue = bluetoothViewModel.rFreqValue.value
                                )
                            }

                            item {
                                ValueControlRow(
                                    label = "Volume:",
                                    value = volumeValue,
                                    onValueChange = { volumeValue = it },
                                    currentDisplayValue = bluetoothViewModel.volumeValue.value
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Action buttons
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                if (currentValue.isNotEmpty()) {
                                    bluetoothViewModel.setValue("current", currentValue)
                                    currentValue = "" // Clear the field after sending
                                }
                                if (voltageValue.isNotEmpty()) {
                                    bluetoothViewModel.setValue("voltage", voltageValue)
                                    voltageValue = "" // Clear the field after sending
                                }
                                if (frequencyValue.isNotEmpty()) {
                                    bluetoothViewModel.setValue("frequency", frequencyValue)
                                    frequencyValue = "" // Clear the field after sending
                                }
                                if (lFreqValue.isNotEmpty()) {
                                    bluetoothViewModel.setValue("l_freq", lFreqValue)
                                    lFreqValue = "" // Clear the field after sending
                                }
                                if (rFreqValue.isNotEmpty()) {
                                    bluetoothViewModel.setValue("r_freq", rFreqValue)
                                    rFreqValue = "" // Clear the field after sending
                                }
                                if (volumeValue.isNotEmpty()) {
                                    bluetoothViewModel.setValue("volume", volumeValue)
                                    volumeValue = "" // Clear the field after sending
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BluetoothSecondary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 4.dp
                            )
                        ) {
                            Text("Send All Values")
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                // Request all values from the device
                                bluetoothViewModel.getValue("current")
                                bluetoothViewModel.getValue("voltage")
                                bluetoothViewModel.getValue("frequency")
                                bluetoothViewModel.getValue("l_freq")
                                bluetoothViewModel.getValue("r_freq")
                                bluetoothViewModel.getValue("volume")
                            },
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = androidx.compose.ui.graphics.SolidColor(BluetoothPrimary)
                            ),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = BluetoothPrimary
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Get All Values")
                        }

                        // Last received data
                        if (bluetoothViewModel.receivedData.value.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFE1F5FE) // Very Light Blue
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "Last received: ${bluetoothViewModel.receivedData.value}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF0D47A1), // Dark Blue
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}