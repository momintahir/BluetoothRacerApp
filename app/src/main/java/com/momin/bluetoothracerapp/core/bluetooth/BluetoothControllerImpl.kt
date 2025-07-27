package com.momin.bluetoothracerapp.core.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.UUID
import kotlin.concurrent.thread

class BluetoothControllerImpl(private val context: Context):BluetoothController {

    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
        manager?.adapter
    }

    companion object {
        // Use a fallback UUID if the device doesn't advertise one
        private val DEFAULT_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    }
    private val bluetoothDevices = MutableStateFlow<MutableList<BluetoothDevice>>(mutableListOf())
    private var bluetoothSocket: BluetoothSocket? = null

    private val _connectionSuccessFlow = MutableSharedFlow<Boolean>()
    override val connectionSuccessFlow: SharedFlow<Boolean> = _connectionSuccessFlow


    private val _onMessageReceivedFlow = MutableSharedFlow<String>()
    override val onMessageReceivedFlow: SharedFlow<String> = _onMessageReceivedFlow

    private val _onDeviceConnectedFlow = MutableSharedFlow<Boolean>()
    override val onDeviceConnectedFlow: SharedFlow<Boolean> = _onDeviceConnectedFlow

    private val receiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val foundDevice = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    foundDevice?.takeIf { !it.name.isNullOrEmpty() }?.let { device ->
                        val existingDevices = bluetoothDevices.value
                        val isAlreadyDiscovered = existingDevices.any { it.address == device.address }

                        if (!isAlreadyDiscovered) {
                            bluetoothDevices.value = existingDevices.toMutableList().apply { add(device) }
                        }
                    }
                }
            }
        }
    }

    private val bondReceiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if (action == BluetoothDevice.ACTION_BOND_STATE_CHANGED) {
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                val bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1)
                val prevBondState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, -1)

                if (bondState == BluetoothDevice.BOND_BONDED && prevBondState == BluetoothDevice.BOND_BONDING) {
                    println("Bonded with ${device?.name}")
                    device?.let { connectToDevice(it) }
                }
            }
        }
    }

    override fun registerBondReceiver() {
        val filter = IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        context.registerReceiver(bondReceiver, filter)
    }

    override fun unRegisterBondReceiver() {
        context.unregisterReceiver(bondReceiver)
    }


    @SuppressLint("MissingPermission")
    override fun startScan() {
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        context.registerReceiver(receiver, filter)
        bluetoothAdapter?.startDiscovery()
    }

    @SuppressLint("MissingPermission")
    override fun stopScan() {
        bluetoothAdapter?.cancelDiscovery()
        runCatching {
            context.unregisterReceiver(receiver)
        }
    }

    @SuppressLint("MissingPermission")
    override fun connectToDevice(device: BluetoothDevice) {

        val uuid = DEFAULT_UUID
        val socket = device.createRfcommSocketToServiceRecord(uuid)
        bluetoothSocket = socket
        CoroutineScope(Dispatchers.IO).launch {
            try {
                socket.connect()
                println("Connected successfully to ${device.name}")
                CoroutineScope(Dispatchers.Main).launch {
                    _connectionSuccessFlow.emit(true)
                }
            } catch (e: IOException) {
                println("Connection failed: ${e.message}")
                try {
                    socket.close()
                } catch (_: IOException) { }
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun listenConnections() {
        val serverSocket = bluetoothAdapter?.listenUsingRfcommWithServiceRecord("MyGame", DEFAULT_UUID)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val socket = serverSocket?.accept() // Waits until another device connects
                bluetoothSocket = socket
                println("Incoming connection accepted from ${socket?.remoteDevice?.name}")
                withContext(Dispatchers.Main) {
                    _onDeviceConnectedFlow.emit(true) // ← This will trigger navigation
                }
                } catch (e: IOException) {
                println("Server accept failed: ${e.message}")
            } finally {
                try {
                    serverSocket?.close()
                } catch (_: IOException) {}
            }
        }
    }

    override fun disconnect() {
        try {
            bluetoothSocket?.close()
            bluetoothSocket = null
            println("🔌 Disconnected")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @SuppressLint("MissingPermission")
    override fun pairAndRegisterDevice(device: BluetoothDevice) {
        if (device.bondState != BluetoothDevice.BOND_BONDED) {
            device.createBond()
        } else {
            connectToDevice(device)
        }
    }

    override fun sendMessage(message: Int) {
        try {
            val outputStream = bluetoothSocket?.outputStream
            outputStream?.write(message.toString().toByteArray())
            outputStream?.flush()
        } catch (e: IOException) {
            Log.e("Bluetooth", "Error sending message", e)
        }
    }

    override fun listenForMessages() {
        thread {
            try {
                val inputStream = bluetoothSocket?.inputStream
                val buffer = ByteArray(1024)
                var bytes: Int

                while (true) {
                    bytes = inputStream?.read(buffer)!!
                    val message = String(buffer, 0, bytes)
                    CoroutineScope(Dispatchers.Main).launch {
                        _onMessageReceivedFlow.emit(message)
                    }
                }
            } catch (e: IOException) {
                Log.e("Bluetooth", "Error reading message", e)
            }
        }
    }

    override fun observeMessages(): Flow<String> {
        return flowOf("")
    }

    override fun getBluetoothDevices() = bluetoothDevices
}