package com.example.bluecontrol.bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import com.example.bluecontrol.R
import com.example.bluecontrol.data.UserStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

class BluetoothViewModel(
    myContext: Context,
    bluetoothAdapter: BluetoothAdapter,
    val userStore: UserStore,
    val messageHandler: (String) -> Unit
) {
    val devices = mutableStateListOf<BluetoothDevice>()
    val scanning = mutableStateOf(false)
    val context = myContext

    // UUIDs for BLE characteristics (Current, Voltage, Frequency)
    private val CHARACTERISTIC_READ_CURRENT_UUID = UUID.fromString("0000FFA2-0000-1000-8000-00805F9B34FB")
    private val CHARACTERISTIC_READ_VOLTAGE_UUID = UUID.fromString("0000FFB2-0000-1000-8000-00805F9B34FB")
    private val CHARACTERISTIC_READ_FREQUENCY_UUID = UUID.fromString("0000FFC2-0000-1000-8000-00805F9B34FB")
    // Add these new UUIDs below your existing UUIDs
    private val CHARACTERISTIC_READ_L_FREQ_UUID = UUID.fromString("0000FFD2-0000-1000-8000-00805F9B34FB")
    private val CHARACTERISTIC_READ_R_FREQ_UUID = UUID.fromString("0000FFE2-0000-1000-8000-00805F9B34FB")
    private val CHARACTERISTIC_READ_VOLUME_UUID = UUID.fromString("0000FFF2-0000-1000-8000-00805F9B34FB")

    private val CHARACTERISTIC_WRITE_UUID = UUID.fromString("0000FFA1-0000-1000-8000-00805F9B34FB")
    private val DESCRIPTOR_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

    private val manuallyDisconnected = mutableStateOf(false)

    // Connection state
    var connected = mutableStateOf(false)
    var connectedDevice = mutableStateOf("")

    // Value state
    var receivedData = mutableStateOf("")
    var currentValue = mutableStateOf("0")
    var voltageValue = mutableStateOf("0")
    var frequencyValue = mutableStateOf("0")
    // Add these state variables with your other value states
    var lFreqValue = mutableStateOf("0")
    var rFreqValue = mutableStateOf("0")
    var volumeValue = mutableStateOf("0")

    var thisGatt: BluetoothGatt? = null
    var writeCharacteristic: BluetoothGattCharacteristic? = null

    private val bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val device = result.device
            if (!devices.contains(device)) devices.add(device)
        }
    }

    val gattObject = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        Log.d("GattCallback", "Successfully connected to device")
                        connected.value = true
                        manuallyDisconnected.value = false
                        saveDevice(gatt.device)
                        if (ActivityCompat.checkSelfPermission(
                                context,
                                Manifest.permission.BLUETOOTH_CONNECT
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            return
                        }
                        // Add delay before discovering services
                        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                            gatt.discoverServices()
                        }, 600)
                    } else {
                        Log.e("GattCallback", "Error $status connecting to device")
                        gatt.close()
                    }
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    Log.d("GattCallback", "Disconnected from device")
                    connected.value = false
                    connectedDevice.value = ""
                    if (!manuallyDisconnected.value) {
                        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                            connectToDevice(gatt.device)
                        }, 1000)
                    }
                    gatt.close()
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d("GattCallback", "Discovered Services")
                thisGatt = gatt

                gatt?.services?.forEach { service ->
                    Log.d("GattCallback", "Service: ${service.uuid}")
                    service.characteristics.forEach { characteristic ->
                        when (characteristic.uuid) {
                            // Existing characteristics
                            CHARACTERISTIC_READ_CURRENT_UUID -> {
                                Log.d("GattCallback", "Found current read characteristic")
                                setupReadCharacteristic(gatt, characteristic, "current")
                            }
                            CHARACTERISTIC_READ_VOLTAGE_UUID -> {
                                Log.d("GattCallback", "Found voltage read characteristic")
                                setupReadCharacteristic(gatt, characteristic, "voltage")
                            }
                            CHARACTERISTIC_READ_FREQUENCY_UUID -> {
                                Log.d("GattCallback", "Found frequency read characteristic")
                                setupReadCharacteristic(gatt, characteristic, "frequency")
                            }
                            // New characteristics
                            CHARACTERISTIC_READ_L_FREQ_UUID -> {
                                Log.d("GattCallback", "Found L_freq read characteristic")
                                setupReadCharacteristic(gatt, characteristic, "l_freq")
                            }
                            CHARACTERISTIC_READ_R_FREQ_UUID -> {
                                Log.d("GattCallback", "Found R_freq read characteristic")
                                setupReadCharacteristic(gatt, characteristic, "r_freq")
                            }
                            CHARACTERISTIC_READ_VOLUME_UUID -> {
                                Log.d("GattCallback", "Found volume read characteristic")
                                setupReadCharacteristic(gatt, characteristic, "volume")
                            }
                            CHARACTERISTIC_WRITE_UUID -> {
                                Log.d("GattCallback", "Found write characteristic")
                                writeCharacteristic = characteristic
                            }
                        }
                    }
                }
            } else {
                Log.e("GattCallback", "Failed to discover services: $status")
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            when (characteristic.uuid) {
                // Existing characteristics
                CHARACTERISTIC_READ_CURRENT_UUID -> {
                    currentValue.value = String(value, Charsets.UTF_8).trim()
                    Log.d("BluetoothScreen", "Received current: ${currentValue.value}")
                    messageHandler(currentValue.value)
                }
                CHARACTERISTIC_READ_VOLTAGE_UUID -> {
                    voltageValue.value = String(value, Charsets.UTF_8).trim()
                    Log.d("BluetoothScreen", "Received voltage: ${voltageValue.value}")
                    messageHandler(voltageValue.value)
                }
                CHARACTERISTIC_READ_FREQUENCY_UUID -> {
                    frequencyValue.value = String(value, Charsets.UTF_8).trim()
                    Log.d("BluetoothScreen", "Received frequency: ${frequencyValue.value}")
                    messageHandler(frequencyValue.value)
                }
                // New characteristics
                CHARACTERISTIC_READ_L_FREQ_UUID -> {
                    lFreqValue.value = String(value, Charsets.UTF_8).trim()
                    Log.d("BluetoothScreen", "Received L_freq: ${lFreqValue.value}")
                    messageHandler(lFreqValue.value)
                }
                CHARACTERISTIC_READ_R_FREQ_UUID -> {
                    rFreqValue.value = String(value, Charsets.UTF_8).trim()
                    Log.d("BluetoothScreen", "Received R_freq: ${rFreqValue.value}")
                    messageHandler(rFreqValue.value)
                }
                CHARACTERISTIC_READ_VOLUME_UUID -> {
                    volumeValue.value = String(value, Charsets.UTF_8).trim()
                    Log.d("BluetoothScreen", "Received volume: ${volumeValue.value}")
                    messageHandler(volumeValue.value)
                }
            }
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d("GattCallback", "Write successful")
            } else {
                Log.e("GattCallback", "Write failed with status: $status")
            }
        }
    }

    private fun setupReadCharacteristic(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, parameter: String) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        // Enable notifications
        val notifyResult = gatt.setCharacteristicNotification(characteristic, true)
        Log.d("GattCallback", "Set notification result: $notifyResult")

        // Get descriptor
        val descriptor = characteristic.getDescriptor(DESCRIPTOR_UUID)
        if (descriptor == null) {
            Log.e("GattCallback", "Descriptor not found")
            return
        }

        // Write descriptor to enable notifications
        descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
        val writeResult = gatt.writeDescriptor(descriptor)
        Log.d("GattCallback", "Write descriptor result: $writeResult")
    }

    fun getValue(parameter: String) {
        Log.d("BluetoothViewModel", "Getting value for $parameter...")
        writeCharacteristic("GET $parameter")
    }

    fun setValue(parameter: String, value: String) {
        Log.d("BluetoothViewModel", "Setting value for $parameter: $value")
        writeCharacteristic("SET $parameter $value")
        when (parameter) {
            "current" -> currentValue.value = value
            "voltage" -> voltageValue.value = value
            "frequency" -> frequencyValue.value = value
            "l_freq" -> lFreqValue.value = value
            "r_freq" -> rFreqValue.value = value
            "volume" -> volumeValue.value = value
        }
    }

    fun writeCharacteristic(message: String) {
        val characteristic = writeCharacteristic ?: run {
            Log.e("BluetoothViewModel", "Write characteristic not found")
            return
        }

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        try {
            // Extract the numeric value from the message (e.g., "SET current 15" -> "15")
            val numericValue = message.split(" ").lastOrNull()

            // Check if the numeric value exists and is a valid number
            if (numericValue != null && numericValue.toFloatOrNull() != null) {
                // Send the full message as a byte array (changed from only sending numeric value)
                characteristic.value = message.toByteArray(Charsets.UTF_8)
                val writeSuccess = thisGatt?.writeCharacteristic(characteristic) ?: false
                Log.d("BluetoothViewModel", "Write attempt: $message, success: $writeSuccess")
            } else {
                Log.e("BluetoothViewModel", "Invalid numeric value in message: $message")
            }
        } catch (e: Exception) {
            Log.e("BluetoothViewModel", "Error writing characteristic", e)
        }
    }

    fun startScan() {
        scanning.value = true
        val scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()
        val scanFilter = ScanFilter.Builder().build()
        val scanFilters = listOf(scanFilter)

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        bluetoothLeScanner.startScan(scanFilters, scanSettings, scanCallback)
    }

    fun stopScan() {
        scanning.value = false
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        bluetoothLeScanner.stopScan(scanCallback)
    }

    fun connectToDevice(device: BluetoothDevice) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        device.connectGatt(context, false, gattObject, BluetoothDevice.TRANSPORT_LE)
        connectedDevice.value = device.address
    }

    fun disconnect() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        manuallyDisconnected.value = true
        thisGatt?.disconnect()
    }

    fun saveDevice(device: BluetoothDevice) {
        CoroutineScope(Dispatchers.IO).launch {
            device.address?.let {
                userStore.saveToken(
                    context.getString(R.string.bluetoothDeviceAddress),
                    it
                )
            }
        }
    }
}