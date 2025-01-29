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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat

@Composable
fun TestBluetoothScreen(bluetoothViewModel: BluetoothViewModel) {
	var inputValue by remember { mutableStateOf("") }

	LaunchedEffect(key1 = bluetoothViewModel.connected.value) {
		if (bluetoothViewModel.connected.value) {
			Toast.makeText(
				bluetoothViewModel.context,
				"Connected to ${bluetoothViewModel.connectedDevice.value}",
				Toast.LENGTH_SHORT
			).show()
		} else {
			Toast.makeText(
				bluetoothViewModel.context,
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

		LazyColumn(
			modifier = Modifier.weight(1f)
		) {
			items(bluetoothViewModel.devices.toList()) { device ->
				if (ActivityCompat.checkSelfPermission(
						bluetoothViewModel.context,
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

		if (bluetoothViewModel.connected.value) {
			Spacer(modifier = Modifier.height(16.dp))

			Card(
				modifier = Modifier.fillMaxWidth()
			) {
				Column(
					modifier = Modifier.padding(16.dp)
				) {
					Row(
						modifier = Modifier.fillMaxWidth(),
						horizontalArrangement = Arrangement.SpaceBetween
					) {
						Text("Connected to:")
						Text(text = bluetoothViewModel.connectedDevice.value)
					}

					Spacer(modifier = Modifier.height(16.dp))

					Row(
						modifier = Modifier.fillMaxWidth(),
						horizontalArrangement = Arrangement.spacedBy(8.dp)
					) {
						// Left side: Current value
						Column(
							modifier = Modifier.weight(1f)
						) {
							Text("Current: ${bluetoothViewModel.receivedData.value}")
						}

						// Right side: New value input
						Column(
							modifier = Modifier.weight(1f)
						) {
							OutlinedTextField(
								value = inputValue,
								onValueChange = { inputValue = it },
								label = { Text("New Value") },
								keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
								modifier = Modifier.fillMaxWidth()
							)
						}
					}

					Spacer(modifier = Modifier.height(8.dp))

					// Buttons row
					Row(
						modifier = Modifier.fillMaxWidth(),
						horizontalArrangement = Arrangement.spacedBy(8.dp)
					) {
						Button(
							modifier = Modifier.weight(1f),
							onClick = { bluetoothViewModel.writeCharacteristic("GET") }
						) {
							Text("Get")
						}
						Button(
							modifier = Modifier.weight(1f),
							onClick = {
								if (inputValue.isNotEmpty()) {
									bluetoothViewModel.writeCharacteristic("SET $inputValue")
									inputValue = ""
								}
							}
						) {
							Text("Set")
						}
					}
					Spacer(modifier = Modifier.height(8.dp))
					Row(
						modifier = Modifier.fillMaxWidth(),
						horizontalArrangement = Arrangement.spacedBy(8.dp)
					) {
						// Left side: Current value
						Column(
							modifier = Modifier.weight(1f)
						) {
							Text("Voltage: ${bluetoothViewModel.receivedData.value}")
						}

						// Right side: New value input
						Column(
							modifier = Modifier.weight(1f)
						) {
							OutlinedTextField(
								value = inputValue,
								onValueChange = { inputValue = it },
								label = { Text("New Value") },
								keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
								modifier = Modifier.fillMaxWidth()
							)
						}
					}
					Spacer(modifier = Modifier.height(8.dp))
					Row(
						modifier = Modifier.fillMaxWidth(),
						horizontalArrangement = Arrangement.spacedBy(8.dp)
					) {
						Button(
							modifier = Modifier.weight(1f),
							onClick = { bluetoothViewModel.writeCharacteristic("GET") }
						) {
							Text("Get")
						}
						Button(
							modifier = Modifier.weight(1f),
							onClick = {
								if (inputValue.isNotEmpty()) {
									bluetoothViewModel.writeCharacteristic("SET $inputValue")
									inputValue = ""
								}
							}
						) {
							Text("Set")
						}
					}
					Spacer(modifier = Modifier.height(8.dp))
					Row(
						modifier = Modifier.fillMaxWidth(),
						horizontalArrangement = Arrangement.spacedBy(8.dp)
					) {
						// Left side: Current value
						Column(
							modifier = Modifier.weight(1f)
						) {
							Text("Frequency: ${bluetoothViewModel.receivedData.value}")
						}

						// Right side: New value input
						Column(
							modifier = Modifier.weight(1f)
						) {
							OutlinedTextField(
								value = inputValue,
								onValueChange = { inputValue = it },
								label = { Text("New Value") },
								keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
								modifier = Modifier.fillMaxWidth()
							)
						}
					}
					Spacer(modifier = Modifier.height(8.dp))
					Row(
						modifier = Modifier.fillMaxWidth(),
						horizontalArrangement = Arrangement.spacedBy(8.dp)
					) {
						Button(
							modifier = Modifier.weight(1f),
							onClick = { bluetoothViewModel.writeCharacteristic("GET") }
						) {
							Text("Get ")
						}
						Button(
							modifier = Modifier.weight(1f),
							onClick = {
								if (inputValue.isNotEmpty()) {
									bluetoothViewModel.writeCharacteristic("SET $inputValue")
									inputValue = ""
								}
							}
						) {
							Text("Set")
						}
					}
				}
			}
		}
	}
}