package com.example.bledproject.bluetooth

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
	var inputValue by remember { mutableStateOf("") }
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
					Text(
						text = "Current Value: ${bluetoothViewModel.currentValue.value}",
						style = MaterialTheme.typography.titleMedium
					)

					Spacer(modifier = Modifier.height(16.dp))

					Button(
						modifier = Modifier.fillMaxWidth(),
						onClick = { bluetoothViewModel.getValue() }
					) {
						Text("Refresh Value")
					}

					Spacer(modifier = Modifier.height(16.dp))

					OutlinedTextField(
						value = inputValue,
						onValueChange = { inputValue = it },
						label = { Text("New Value") },
						keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
						modifier = Modifier.fillMaxWidth()
					)

					Spacer(modifier = Modifier.height(8.dp))

					Button(
						modifier = Modifier.fillMaxWidth(),
						onClick = {
							if (inputValue.isNotEmpty()) {
								bluetoothViewModel.setValue(inputValue)
								inputValue = ""
							}
						}
					) {
						Text("Set Value")
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
