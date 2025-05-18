package com.example.bluecontrol.bluetooth


import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.compose.ui.text.font.FontFamily



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
    currentDisplayValue: String,
    icon: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = BluetoothInputContainer
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon with circular background
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(BluetoothPrimary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }


            Spacer(modifier = Modifier.width(12.dp))


            Text(
                text = label,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF455A64), // Dark Blue-Gray
                modifier = Modifier.width(90.dp)
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
                ),
                shape = RoundedCornerShape(10.dp)
            )


            if (currentDisplayValue.isNotEmpty()) {
                Spacer(modifier = Modifier.width(10.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = BluetoothPrimary.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = currentDisplayValue,
                        fontWeight = FontWeight.Bold,
                        color = BluetoothPrimary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Bluetooth,
                            contentDescription = "Bluetooth Icon",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Bluetooth Control Device",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp,
                                fontFamily = FontFamily.SansSerif,
                                        fontSize = 18.sp
                            ),
                            color = Color.White,
                                    modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF303F9F)
                ),
                actions = {
                    IconButton(onClick = { /* Info action */ }) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = "Info",
                            tint = Color.White
                        )
                    }
                },
                modifier = Modifier.height(64.dp)
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
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (bluetoothViewModel.connected.value)
                            Icons.Filled.BluetoothConnected
                        else
                            Icons.Filled.BluetoothDisabled,
                        contentDescription = if (bluetoothViewModel.connected.value) "Connected" else "Disconnected",
                        tint = if (bluetoothViewModel.connected.value) BluetoothConnected else BluetoothError,
                        modifier = Modifier.size(36.dp)
                    )


                    Spacer(modifier = Modifier.width(16.dp))


                    Column {
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
                    shape = RoundedCornerShape(10.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp
                    ),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Icon(
                        imageVector = if (bluetoothViewModel.scanning.value)
                            Icons.Filled.Stop
                        else
                            Icons.Filled.Search,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = if (bluetoothViewModel.scanning.value) "Stop Scan" else "Start Scan")
                }


                if (bluetoothViewModel.connected.value) {
                    Button(
                        onClick = { bluetoothViewModel.disconnect() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BluetoothError
                        ),
                        shape = RoundedCornerShape(10.dp),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 4.dp
                        ),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Disconnect")
                    }
                }
            }


            Spacer(modifier = Modifier.height(16.dp))


            // Device List
            if (!bluetoothViewModel.connected.value) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Devices,
                        contentDescription = "Devices",
                        tint = Color(0xFF263238),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Available Devices",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF263238) // Very Dark Blue-Gray
                    )
                }


                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFE1F5FE)) // Very Light Blue
                        .padding(12.dp),
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
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Bluetooth,
                                            contentDescription = "Bluetooth Device",
                                            tint = BluetoothPrimary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
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
                                    }
                                    FilledTonalButton(
                                        onClick = { bluetoothViewModel.connectToDevice(device) },
                                        colors = ButtonDefaults.filledTonalButtonColors(
                                            containerColor = BluetoothSecondary,
                                            contentColor = Color.White
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Link,
                                            contentDescription = "Connect",
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = "Connect")
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
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    if (bluetoothViewModel.scanning.value) {
                                        CircularProgressIndicator(
                                            color = BluetoothPrimary,
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                    } else {
                                        Icon(
                                            imageVector = Icons.Filled.BluetoothSearching,
                                            contentDescription = "No Devices",
                                            tint = Color(0xFF78909C),
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                    Text(
                                        text = if (bluetoothViewModel.scanning.value)
                                            "Scanning for devices..."
                                        else
                                            "No devices found. Press Start Scan to begin searching.",
                                        color = Color(0xFF78909C), // Light Blue-Gray
                                        textAlign = TextAlign.Center
                                    )
                                }
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
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        // Current values display
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = "Control Values",
                                tint = BluetoothPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Control Values",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = BluetoothPrimary
                            )
                        }


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
                                    currentDisplayValue = bluetoothViewModel.currentValue.value,
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Rounded.ElectricBolt,
                                            contentDescription = "Current",
                                            tint = BluetoothPrimary
                                        )
                                    }
                                )
                            }


                            item {
                                ValueControlRow(
                                    label = "Voltage:",
                                    value = voltageValue,
                                    onValueChange = { voltageValue = it },
                                    currentDisplayValue = bluetoothViewModel.voltageValue.value,
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Rounded.BatteryChargingFull,
                                            contentDescription = "Voltage",
                                            tint = BluetoothPrimary
                                        )
                                    }
                                )
                            }


                            item {
                                ValueControlRow(
                                    label = "Frequency:",
                                    value = frequencyValue,
                                    onValueChange = { frequencyValue = it },
                                    currentDisplayValue = bluetoothViewModel.frequencyValue.value,
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Rounded.GraphicEq,
                                            contentDescription = "Frequency",
                                            tint = BluetoothPrimary
                                        )
                                    }
                                )
                            }


                            item {
                                ValueControlRow(
                                    label = "L Frequency:",
                                    value = lFreqValue,
                                    onValueChange = { lFreqValue = it },
                                    currentDisplayValue = bluetoothViewModel.lFreqValue.value,
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Rounded.KeyboardArrowLeft,
                                            contentDescription = "Left Frequency",
                                            tint = BluetoothPrimary
                                        )
                                    }
                                )
                            }


                            item {
                                ValueControlRow(
                                    label = "R Frequency:",
                                    value = rFreqValue,
                                    onValueChange = { rFreqValue = it },
                                    currentDisplayValue = bluetoothViewModel.rFreqValue.value,
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Rounded.KeyboardArrowRight,
                                            contentDescription = "Right Frequency",
                                            tint = BluetoothPrimary
                                        )
                                    }
                                )
                            }


                            item {
                                ValueControlRow(
                                    label = "Volume:",
                                    value = volumeValue,
                                    onValueChange = { volumeValue = it },
                                    currentDisplayValue = bluetoothViewModel.volumeValue.value,
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Rounded.VolumeUp,
                                            contentDescription = "Volume",
                                            tint = BluetoothPrimary
                                        )
                                    }
                                )
                            }
                        }


                        Spacer(modifier = Modifier.height(16.dp))

                        // Action buttons
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                // Create a map of values to send
                                val valuesToSend = mutableMapOf<String, String>()

                                // Update local display immediately with entered values
                                if (currentValue.isNotEmpty()) {
                                    valuesToSend["current"] = currentValue
                                    bluetoothViewModel.currentValue.value = currentValue // Add this line
                                }
                                if (voltageValue.isNotEmpty()) {
                                    valuesToSend["voltage"] = voltageValue
                                    bluetoothViewModel.voltageValue.value = voltageValue // Add this line
                                }
                                if (frequencyValue.isNotEmpty()) {
                                    valuesToSend["frequency"] = frequencyValue
                                    bluetoothViewModel.frequencyValue.value = frequencyValue // Add this line
                                }
                                if (lFreqValue.isNotEmpty()) {
                                    valuesToSend["l_freq"] = lFreqValue
                                    bluetoothViewModel.lFreqValue.value = lFreqValue // Add this line
                                }
                                if (rFreqValue.isNotEmpty()) {
                                    valuesToSend["r_freq"] = rFreqValue
                                    bluetoothViewModel.rFreqValue.value = rFreqValue // Add this line
                                }
                                if (volumeValue.isNotEmpty()) {
                                    valuesToSend["volume"] = volumeValue
                                    bluetoothViewModel.volumeValue.value = volumeValue // Add this line
                                }

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
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BluetoothSecondary
                            ),
                            // Rest of your button code remains the same
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Send,
                                contentDescription = "Send",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Send All Values")
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
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(vertical = 12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = "Refresh",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Get All Values")
                        }


                        // Last received data with animation
                        AnimatedVisibility(
                            visible = bluetoothViewModel.receivedData.value.isNotEmpty(),
                            enter = fadeIn(animationSpec = tween(300)) +
                                    slideInVertically(animationSpec = tween(300)) { it },
                            exit = fadeOut(animationSpec = tween(300))
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFE1F5FE) // Very Light Blue
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Info,
                                        contentDescription = "Last Received",
                                        tint = Color(0xFF0D47A1),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Last received: ${bluetoothViewModel.receivedData.value}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF0D47A1) // Dark Blue
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

