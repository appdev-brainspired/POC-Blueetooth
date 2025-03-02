package com.example.bledproject.bluetooth

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import kotlin.math.abs

@Composable
fun FrequencyPresetButton(
	text: String,
	isSelected: Boolean,
	onClick: () -> Unit
) {
	Button(
		onClick = onClick,
		colors = ButtonDefaults.buttonColors(
			containerColor = if (isSelected) Color(0xFF43A047) else Color(0xFF1E88E5)
		),
		modifier = Modifier.padding(horizontal = 4.dp)
	) {
		Text(text)
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdjustableValueRow(
	label: String,
	value: String,
	onValueChange: (String) -> Unit,
	onGet: () -> Unit,
	onSet: () -> Unit,
	onIncrement: () -> Unit,
	onDecrement: () -> Unit,
	currentValue: String,
	stepSize: String,
	onStepSizeChange: (String) -> Unit
) {
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.padding(4.dp),
		colors = CardDefaults.cardColors(containerColor = Color(0xFF1E88E5))
	) {
		Column(
			modifier = Modifier
				.padding(8.dp)
				.fillMaxWidth()
		) {
			Text(
				text = label,
				color = Color.White,
				fontSize = 16.sp,
				fontWeight = FontWeight.Bold
			)

			Spacer(modifier = Modifier.height(4.dp))

			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(4.dp)
			) {
				TextField(
					value = value,
					onValueChange = onValueChange,
					modifier = Modifier.weight(1f),
					colors = TextFieldDefaults.colors(
						focusedContainerColor = Color(0xFF1565C0),
						unfocusedContainerColor = Color(0xFF1565C0),
						focusedTextColor = Color.White,
						unfocusedTextColor = Color.White
					)
				)

				TextField(
					value = stepSize,
					onValueChange = onStepSizeChange,
					label = { Text("Step Size", color = Color.White) },
					modifier = Modifier.weight(1f),
					colors = TextFieldDefaults.colors(
						focusedContainerColor = Color(0xFF1565C0),
						unfocusedContainerColor = Color(0xFF1565C0),
						focusedTextColor = Color.White,
						unfocusedTextColor = Color.White
					)
				)
			}

			Spacer(modifier = Modifier.height(8.dp))

			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween
			) {
				Button(
					onClick = onGet,
					colors = ButtonDefaults.buttonColors(containerColor = Color.White)
				) {
					Text("Get", color = Color(0xFF1E88E5))
				}
				Button(
					onClick = onSet,
					colors = ButtonDefaults.buttonColors(containerColor = Color.White)
				) {
					Text("Set", color = Color(0xFF1E88E5))
				}
				Button(
					onClick = onIncrement,
					colors = ButtonDefaults.buttonColors(containerColor = Color.White)
				) {
					Text("+", color = Color(0xFF1E88E5))
				}
				Button(
					onClick = onDecrement,
					colors = ButtonDefaults.buttonColors(containerColor = Color.White)
				) {
					Text("-", color = Color(0xFF1E88E5))
				}
			}

			if (currentValue.isNotEmpty()) {
				Spacer(modifier = Modifier.height(8.dp))
				Text(
					text = "Current Set Value: $currentValue",
					color = Color.White,
					fontSize = 14.sp
				)
			}
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestBluetoothScreen(bluetoothViewModel: BluetoothViewModel) {
	var currentValue by remember { mutableStateOf("0") }
	var voltageValue by remember { mutableStateOf("0") }
	var frequencyValue by remember { mutableStateOf("0") }

	var currentSet by remember { mutableStateOf("") }
	var voltageSet by remember { mutableStateOf("") }
	var frequencySet by remember { mutableStateOf("") }

	var currentStepSize by remember { mutableStateOf("0.5") }
	var voltageStepSize by remember { mutableStateOf("10") }
	var frequencyStepSize by remember { mutableStateOf("2") }

	var selectedPreset by remember { mutableStateOf("") }

	// Audio section state variables
	var audioFrequencyL by remember { mutableStateOf("0") }
	var audioFrequencyR by remember { mutableStateOf("0") }
	var audioIntensityL by remember { mutableStateOf("0") }
	var audioIntensityR by remember { mutableStateOf("0") }
	var audioIntensityC by remember { mutableStateOf("") }

	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(8.dp)
			.background(Color(0xFFF5F5F5)),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Text(
			text = "Bluetooth Device Control",
			fontSize = 18.sp,
			fontWeight = FontWeight.Bold,
			color = Color(0xFF1E88E5)
		)

		Spacer(modifier = Modifier.height(8.dp))

		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.SpaceEvenly
		) {
			Button(
				onClick = {
					if (bluetoothViewModel.scanning.value) {
						bluetoothViewModel.stopScan()
					} else {
						bluetoothViewModel.startScan()
					}
				},
				colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047))
			) {
				Text(if (bluetoothViewModel.scanning.value) "Stop Scan" else "Start Scan")
			}

			if (bluetoothViewModel.connected.value) {
				Button(
					onClick = { bluetoothViewModel.disconnect() },
					colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
				) {
					Text("Disconnect")
				}
			}
		}

		Spacer(modifier = Modifier.height(8.dp))

		// Use LazyColumn for the entire scrollable content
		LazyColumn(
			modifier = Modifier
				.fillMaxWidth()
				.weight(1f)
		) {
			// Display bluetooth devices
			item {
				Text(
					text = "Available Devices",
					fontSize = 16.sp,
					fontWeight = FontWeight.Bold,
					color = Color(0xFF1E88E5),
					modifier = Modifier.padding(vertical = 4.dp)
				)
			}

			items(bluetoothViewModel.devices.toList()) { device ->
				if (ActivityCompat.checkSelfPermission(
						bluetoothViewModel.context,
						Manifest.permission.BLUETOOTH_CONNECT
					) == PackageManager.PERMISSION_GRANTED
				) {
					Card(
						modifier = Modifier
							.fillMaxWidth()
							.padding(vertical = 4.dp),
						colors = CardDefaults.cardColors()
					) {
						Row(
							modifier = Modifier
								.fillMaxWidth()
								.padding(8.dp),
							horizontalArrangement = Arrangement.SpaceBetween,
							verticalAlignment = Alignment.CenterVertically
						) {
							Column {
								Text(device.name ?: "Unnamed device", fontWeight = FontWeight.Bold)
								Text(
									text = device.address,
									style = MaterialTheme.typography.bodySmall
								)
							}
							Button(
								onClick = { bluetoothViewModel.connectToDevice(device) },
								colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5))
							) {
								Text("Connect")
							}
						}
					}
				}
			}

			// Display controls when connected
			if (bluetoothViewModel.connected.value) {
				item {
					Spacer(modifier = Modifier.height(8.dp))

					Card(
						modifier = Modifier.fillMaxWidth(),
						colors = CardDefaults.cardColors(containerColor = Color.White)
					) {
						Column(
							modifier = Modifier.padding(8.dp)
						) {
							Text(
								text = "Connected to: ${bluetoothViewModel.connectedDevice.value}",
								fontSize = 16.sp,
								fontWeight = FontWeight.Bold,
								color = Color(0xFF43A047)
							)

							Spacer(modifier = Modifier.height(8.dp))

							// Frequency preset buttons in a single row
							Row(
								modifier = Modifier.fillMaxWidth(),
								horizontalArrangement = Arrangement.SpaceEvenly
							) {
								FrequencyPresetButton(
									text = "Theta",
									isSelected = selectedPreset == "theta",
									onClick = {
										frequencyValue = "4"
										selectedPreset = "theta"
										bluetoothViewModel.writeCharacteristic("SET FREQUENCY 6")
										frequencySet = "4"
									}
								)
								FrequencyPresetButton(
									text = "Alpha",
									isSelected = selectedPreset == "alpha",
									onClick = {
										frequencyValue = "8"
										selectedPreset = "alpha"
										bluetoothViewModel.writeCharacteristic("SET FREQUENCY 10")
										frequencySet = "8"
									}
								)
								FrequencyPresetButton(
									text = "Beta",
									isSelected = selectedPreset == "beta",
									onClick = {
										frequencyValue = "13"
										selectedPreset = "beta"
										bluetoothViewModel.writeCharacteristic("SET FREQUENCY 20")
										frequencySet = "13"
									}
								)
							}

							Spacer(modifier = Modifier.height(16.dp))

							AdjustableValueRow(
								label = "Current",
								value = currentValue,
								onValueChange = { currentValue = it },
								onGet = { bluetoothViewModel.writeCharacteristic("GET CURRENT") },
								onSet = {
									bluetoothViewModel.writeCharacteristic("SET CURRENT $currentValue")
									currentSet = currentValue
								},
								onIncrement = {
									currentValue = (currentValue.toFloat() + currentStepSize.toFloat()).toString()
								},
								onDecrement = {
									currentValue = (currentValue.toFloat() - currentStepSize.toFloat()).toString()
								},
								currentValue = currentSet,
								stepSize = currentStepSize,
								onStepSizeChange = { currentStepSize = it }
							)

							AdjustableValueRow(
								label = "Voltage",
								value = voltageValue,
								onValueChange = { voltageValue = it },
								onGet = { bluetoothViewModel.writeCharacteristic("GET VOLTAGE") },
								onSet = {
									bluetoothViewModel.writeCharacteristic("SET VOLTAGE $voltageValue")
									voltageSet = voltageValue
								},
								onIncrement = {
									voltageValue = (voltageValue.toFloat() + voltageStepSize.toFloat()).toString()
								},
								onDecrement = {
									voltageValue = (voltageValue.toFloat() - voltageStepSize.toFloat()).toString()
								},
								currentValue = voltageSet,
								stepSize = voltageStepSize,
								onStepSizeChange = { voltageStepSize = it }
							)

							AdjustableValueRow(
								label = "Frequency",
								value = frequencyValue,
								onValueChange = {
									frequencyValue = it
									selectedPreset = ""
								},
								onGet = { bluetoothViewModel.writeCharacteristic("GET FREQUENCY") },
								onSet = {
									bluetoothViewModel.writeCharacteristic("SET FREQUENCY $frequencyValue")
									frequencySet = frequencyValue
								},
								onIncrement = {
									frequencyValue = (frequencyValue.toFloat() + frequencyStepSize.toFloat()).toString()
									selectedPreset = ""
								},
								onDecrement = {
									frequencyValue = (frequencyValue.toFloat() - frequencyStepSize.toFloat()).toString()
									selectedPreset = ""
								},
								currentValue = frequencySet,
								stepSize = frequencyStepSize,
								onStepSizeChange = { frequencyStepSize = it }
							)

							// Audio Section Title
							Spacer(modifier = Modifier.height(16.dp))
							Text(
								text = "Audio Section",
								fontSize = 16.sp,
								fontWeight = FontWeight.Bold,
								color = Color(0xFF1E88E5)
							)

							Spacer(modifier = Modifier.height(8.dp))

							// Audio Frequency Section
							Card(
								modifier = Modifier
									.fillMaxWidth()
									.padding(4.dp),
								colors = CardDefaults.cardColors(containerColor = Color(0xFF1E88E5))
							) {
								Column(
									modifier = Modifier
										.padding(8.dp)
										.fillMaxWidth()
								) {
									Text(
										text = "Frequency",
										color = Color.White,
										fontSize = 16.sp,
										fontWeight = FontWeight.Bold
									)

									Spacer(modifier = Modifier.height(4.dp))

									Row(
										modifier = Modifier.fillMaxWidth(),
										horizontalArrangement = Arrangement.spacedBy(4.dp)
									) {
										// L field
										Column(modifier = Modifier.weight(1f)) {
											Text("L", color = Color.White)
											TextField(
												value = audioFrequencyL,
												onValueChange = { audioFrequencyL = it },
												colors = TextFieldDefaults.colors(
													focusedContainerColor = Color(0xFF1565C0),
													unfocusedContainerColor = Color(0xFF1565C0),
													focusedTextColor = if (audioFrequencyL.toFloatOrNull() ?: 0f > audioFrequencyR.toFloatOrNull() ?: 0f) Color.Red else Color.White,
													unfocusedTextColor = if (audioFrequencyL.toFloatOrNull() ?: 0f > audioFrequencyR.toFloatOrNull() ?: 0f) Color.Red else Color.White
												),
												modifier = Modifier.fillMaxWidth()
											)
											Row(
												modifier = Modifier.fillMaxWidth(),
												horizontalArrangement = Arrangement.SpaceEvenly
											) {
												Button(
													onClick = {
														val current = audioFrequencyL.toFloatOrNull() ?: 0f
														audioFrequencyL = (current + 1).toString()
													},
													colors = ButtonDefaults.buttonColors(containerColor = Color.White)
												) {
													Text("+", color = Color(0xFF1E88E5))
												}
												Button(
													onClick = {
														val current = audioFrequencyL.toFloatOrNull() ?: 0f
														audioFrequencyL = (current - 1).toString()
													},
													colors = ButtonDefaults.buttonColors(containerColor = Color.White)
												) {
													Text("-", color = Color(0xFF1E88E5))
												}
											}
										}

										// R field
										Column(modifier = Modifier.weight(1f)) {
											Text("R", color = Color.White)
											TextField(
												value = audioFrequencyR,
												onValueChange = { audioFrequencyR = it },
												colors = TextFieldDefaults.colors(
													focusedContainerColor = Color(0xFF1565C0),
													unfocusedContainerColor = Color(0xFF1565C0),
													focusedTextColor = if (audioFrequencyR.toFloatOrNull() ?: 0f > audioFrequencyL.toFloatOrNull() ?: 0f) Color.Red else Color.White,
													unfocusedTextColor = if (audioFrequencyR.toFloatOrNull() ?: 0f > audioFrequencyL.toFloatOrNull() ?: 0f) Color.Red else Color.White
												),
												modifier = Modifier.fillMaxWidth()
											)
											Row(
												modifier = Modifier.fillMaxWidth(),
												horizontalArrangement = Arrangement.SpaceEvenly
											) {
												Button(
													onClick = {
														val current = audioFrequencyR.toFloatOrNull() ?: 0f
														audioFrequencyR = (current + 1).toString()
													},
													colors = ButtonDefaults.buttonColors(containerColor = Color.White)
												) {
													Text("+", color = Color(0xFF1E88E5))
												}
												Button(
													onClick = {
														val current = audioFrequencyR.toFloatOrNull() ?: 0f
														audioFrequencyR = (current - 1).toString()
													},
													colors = ButtonDefaults.buttonColors(containerColor = Color.White)
												) {
													Text("-", color = Color(0xFF1E88E5))
												}
											}
										}
									}

									// Difference display
									Spacer(modifier = Modifier.height(8.dp))
									val freqDiff = abs((audioFrequencyL.toFloatOrNull() ?: 0f) - (audioFrequencyR.toFloatOrNull() ?: 0f))
									Text(
										text = "Difference: |L - R| = $freqDiff",
										color = Color.White,
										fontSize = 14.sp
									)
								}
							}

							// Audio Intensity Section
							Card(
								modifier = Modifier
									.fillMaxWidth()
									.padding(4.dp),
								colors = CardDefaults.cardColors(containerColor = Color(0xFF1E88E5))
							) {
								Column(
									modifier = Modifier
										.padding(8.dp)
										.fillMaxWidth()
								) {
									Text(
										text = "Intensity",
										color = Color.White,
										fontSize = 16.sp,
										fontWeight = FontWeight.Bold
									)

									Spacer(modifier = Modifier.height(4.dp))

									// Special C input field
									Row(
										modifier = Modifier.fillMaxWidth(),
										horizontalArrangement = Arrangement.spacedBy(4.dp)
									) {
										Column(modifier = Modifier.fillMaxWidth()) {
											Text("C (Sets both L and R)", color = Color.White)
											TextField(
												value = audioIntensityC,
												onValueChange = {
													audioIntensityC = it
													if (it.isNotEmpty()) {
														audioIntensityL = it
														audioIntensityR = it
													}
												},
												colors = TextFieldDefaults.colors(
													focusedContainerColor = Color(0xFF1565C0),
													unfocusedContainerColor = Color(0xFF1565C0),
													focusedTextColor = Color.White,
													unfocusedTextColor = Color.White
												),
												modifier = Modifier.fillMaxWidth()
											)
										}
									}

									Spacer(modifier = Modifier.height(8.dp))

									Row(
										modifier = Modifier.fillMaxWidth(),
										horizontalArrangement = Arrangement.spacedBy(4.dp)
									) {
										// L field
										Column(modifier = Modifier.weight(1f)) {
											Text("L", color = Color.White)
											TextField(
												value = audioIntensityL,
												onValueChange = { audioIntensityL = it },
												colors = TextFieldDefaults.colors(
													focusedContainerColor = Color(0xFF1565C0),
													unfocusedContainerColor = Color(0xFF1565C0),
													focusedTextColor = if (audioIntensityL.toFloatOrNull() ?: 0f > audioIntensityR.toFloatOrNull() ?: 0f) Color.Red else Color.White,
													unfocusedTextColor = if (audioIntensityL.toFloatOrNull() ?: 0f > audioIntensityR.toFloatOrNull() ?: 0f) Color.Red else Color.White
												),
												modifier = Modifier.fillMaxWidth()
											)
											Row(
												modifier = Modifier.fillMaxWidth(),
												horizontalArrangement = Arrangement.SpaceEvenly
											) {
												Button(
													onClick = {
														val current = audioIntensityL.toFloatOrNull() ?: 0f
														audioIntensityL = (current + 1).toString()
													},
													colors = ButtonDefaults.buttonColors(containerColor = Color.White)
												) {
													Text("+", color = Color(0xFF1E88E5))
												}
												Button(
													onClick = {
														val current = audioIntensityL.toFloatOrNull() ?: 0f
														audioIntensityL = (current - 1).toString()
													},
													colors = ButtonDefaults.buttonColors(containerColor = Color.White)
												) {
													Text("-", color = Color(0xFF1E88E5))
												}
											}
										}

										// R field
										Column(modifier = Modifier.weight(1f)) {
											Text("R", color = Color.White)
											TextField(
												value = audioIntensityR,
												onValueChange = { audioIntensityR = it },
												colors = TextFieldDefaults.colors(
													focusedContainerColor = Color(0xFF1565C0),
													unfocusedContainerColor = Color(0xFF1565C0),
													focusedTextColor = if (audioIntensityR.toFloatOrNull() ?: 0f > audioIntensityL.toFloatOrNull() ?: 0f) Color.Red else Color.White,
													unfocusedTextColor = if (audioIntensityR.toFloatOrNull() ?: 0f > audioIntensityL.toFloatOrNull() ?: 0f) Color.Red else Color.White
												),
												modifier = Modifier.fillMaxWidth()
											)
											Row(
												modifier = Modifier.fillMaxWidth(),
												horizontalArrangement = Arrangement.SpaceEvenly
											) {
												Button(
													onClick = {
														val current = audioIntensityR.toFloatOrNull() ?: 0f
														audioIntensityR = (current + 1).toString()
													},
													colors = ButtonDefaults.buttonColors(containerColor = Color.White)
												) {
													Text("+", color = Color(0xFF1E88E5))
												}
												Button(
													onClick = {
														val current = audioIntensityR.toFloatOrNull() ?: 0f
														audioIntensityR = (current - 1).toString()
													},
													colors = ButtonDefaults.buttonColors(containerColor = Color.White)
												) {
													Text("-", color = Color(0xFF1E88E5))
												}
											}
										}
									}

									// Difference display
									Spacer(modifier = Modifier.height(8.dp))
									val intensityDiff = abs((audioIntensityL.toFloatOrNull() ?: 0f) - (audioIntensityR.toFloatOrNull() ?: 0f))
									Text(
										text = "Difference: |L - R| = $intensityDiff",
										color = Color.White,
										fontSize = 14.sp
									)

									// Set button for Audio section
									Spacer(modifier = Modifier.height(16.dp))
									Button(
										onClick = {
											// Send all audio values to the device
											bluetoothViewModel.writeCharacteristic("SET AUDIO_FREQ_L ${audioFrequencyL}")
											bluetoothViewModel.writeCharacteristic("SET AUDIO_FREQ_R ${audioFrequencyR}")
											bluetoothViewModel.writeCharacteristic("SET AUDIO_INTENSITY_L ${audioIntensityL}")
											bluetoothViewModel.writeCharacteristic("SET AUDIO_INTENSITY_R ${audioIntensityR}")
										},
										colors = ButtonDefaults.buttonColors(containerColor = Color.White),
										modifier = Modifier.fillMaxWidth()
									) {
										Text("Set Audio Values", color = Color(0xFF1E88E5))
									}
								}
							}
						}
					}
				}
			}
		}
	}
}
