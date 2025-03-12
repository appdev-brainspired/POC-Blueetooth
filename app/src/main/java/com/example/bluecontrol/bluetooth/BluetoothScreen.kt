package com.example.bluecontrol.bluetooth

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat


@Composable
fun BluetoothScreen(bluetoothViewModel: BluetoothViewModel) {
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Status Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = if (bluetoothViewModel.connected.value) "Connected" else "Disconnected",
                    style = MaterialTheme.typography.titleMedium
                )
                if (bluetoothViewModel.connected.value) {
                    Text(
                        text = "Device: ${bluetoothViewModel.connectedDevice.value}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Scan and Disconnect buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    if (bluetoothViewModel.scanning.value) {
                        bluetoothViewModel.stopScan()
                    } else {
                        bluetoothViewModel.startScan()
                    }
                }
            ) {
                Text(if (bluetoothViewModel.scanning.value) "Stop Scan" else "Start Scan")
            }

            if (bluetoothViewModel.connected.value) {
                Button(onClick = { bluetoothViewModel.disconnect() }) {
                    Text("Disconnect")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Device List
        if (!bluetoothViewModel.connected.value) {
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(bluetoothViewModel.devices.toList()) { device ->
                    if (ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.BLUETOOTH_CONNECT
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(device.name ?: "Unnamed device")
                                    Text(
                                        text = device.address,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                Button(onClick = { bluetoothViewModel.connectToDevice(device) }) {
                                    Text("Connect")
                                }
                            }
                        }
                    }
                }
            }
        }

        // Connected Device Controls
        if (bluetoothViewModel.connected.value) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Current values display
                    Text(
                        text = "Current Values:",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Current: ${bluetoothViewModel.currentValue.value}")
                        Text(text = "Voltage: ${bluetoothViewModel.voltageValue.value}")
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Frequency: ${bluetoothViewModel.frequencyValue.value}")
                        Text(text = "Volume: ${bluetoothViewModel.volumeValue.value}")
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "L_Frequency: ${bluetoothViewModel.lFreqValue.value}")
                        Text(text = "R_Frequency: ${bluetoothViewModel.rFreqValue.value}")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Input fields - now in rows with labels
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Current:", modifier = Modifier.width(100.dp))
                        OutlinedTextField(
                            value = currentValue,
                            onValueChange = { currentValue = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Voltage:", modifier = Modifier.width(100.dp))
                        OutlinedTextField(
                            value = voltageValue,
                            onValueChange = { voltageValue = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Frequency:", modifier = Modifier.width(100.dp))
                        OutlinedTextField(
                            value = frequencyValue,
                            onValueChange = { frequencyValue = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("L_Frequency:", modifier = Modifier.width(100.dp))
                        OutlinedTextField(
                            value = lFreqValue,
                            onValueChange = { lFreqValue = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("R_Frequency:", modifier = Modifier.width(100.dp))
                        OutlinedTextField(
                            value = rFreqValue,
                            onValueChange = { rFreqValue = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Volume:", modifier = Modifier.width(100.dp))
                        OutlinedTextField(
                            value = volumeValue,
                            onValueChange = { volumeValue = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Single Set button
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        // Replace the existing onClick logic
                        onClick = {
                            // Create a map of values to send
                            val valuesToSend = mutableMapOf<String, String>()

                            if (currentValue.isNotEmpty()) valuesToSend["current"] = currentValue
                            if (voltageValue.isNotEmpty()) valuesToSend["voltage"] = voltageValue
                            if (frequencyValue.isNotEmpty()) valuesToSend["frequency"] = frequencyValue
                            if (lFreqValue.isNotEmpty()) valuesToSend["l_freq"] = lFreqValue
                            if (rFreqValue.isNotEmpty()) valuesToSend["r_freq"] = rFreqValue
                            if (volumeValue.isNotEmpty()) valuesToSend["volume"] = volumeValue

                            if (valuesToSend.isNotEmpty()) {
                                // Send all values at once as a single JSON
                                bluetoothViewModel.setValue("all", "")

                                // Clear the input fields
                                currentValue = ""
                                voltageValue = ""
                                frequencyValue = ""
                                lFreqValue = ""
                                rFreqValue = ""
                                volumeValue = ""
                            }
                        }
                    ) {
                        Text("Send All Values")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            // Request all values from the device
                            bluetoothViewModel.getValue("current")
                            bluetoothViewModel.getValue("voltage")
                            bluetoothViewModel.getValue("frequency")
                            bluetoothViewModel.getValue("l_freq")
                            bluetoothViewModel.getValue("r_freq")
                            bluetoothViewModel.getValue("volume")
                        }
                    ) {
                        Text("Get All Values")
                    }

                    // Last received data
                    if (bluetoothViewModel.receivedData.value.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Last received: ${bluetoothViewModel.receivedData.value}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}