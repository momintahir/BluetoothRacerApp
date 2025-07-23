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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.UUID

class BluetoothControllerImpl(private val context: Context):BluetoothController {

    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
        manager?.adapter
    }

    companion object {
        // Use a fallback UUID if the device doesn't advertise one
        private val DEFAULT_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    }
    private val discoveredDevices = MutableStateFlow<MutableList<BluetoothDevice>>(mutableListOf())
    private var bluetoothSocket: BluetoothSocket? = null

    private val _connectionSuccessFlow = MutableSharedFlow<Boolean>()
    override val connectionSuccessFlow: SharedFlow<Boolean> = _connectionSuccessFlow

    private val _onDeviceConnectedFlow = MutableSharedFlow<Boolean>()
    override val onDeviceConnectedFlow: SharedFlow<Boolean> = _onDeviceConnectedFlow

    private val receiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    device?.let {
                        val current = discoveredDevices.value
                        if (current.none { it.address == device.address }) {
                            println("device found ${device.name}")
                            val updated = current.toList().toMutableList().apply { add(device) }
                            discoveredDevices.value = updated
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
                    println("✅ Bonded with ${device?.name}")
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
        println("start discovery called")
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

        val uuid =  DEFAULT_UUID
        val socket = device.createRfcommSocketToServiceRecord(uuid)
        bluetoothSocket = socket
        Thread {
            try {
                socket.connect()
                println("✅ Connected successfully to ${device.name}")
                CoroutineScope(Dispatchers.Main).launch {
                    _connectionSuccessFlow.emit(true)
                }
            } catch (e: IOException) {
                println("❌ Connection failed: ${e.message}")
                try {
                    socket.close()
                } catch (_: IOException) { }
            }
        }.start()
    }

    @SuppressLint("MissingPermission")
    override fun startServer() {
        val serverSocket = bluetoothAdapter?.listenUsingRfcommWithServiceRecord("MyGame", DEFAULT_UUID)

        Thread {
            try {
                val socket = serverSocket?.accept() // ⏳ Waits until another device connects
                bluetoothSocket = socket
                println("✅ Incoming connection accepted from ${socket?.remoteDevice?.name}")
                CoroutineScope(Dispatchers.Main).launch {
                    _onDeviceConnectedFlow.emit(true) // ← This will trigger navigation
                }
                } catch (e: IOException) {
                println("❌ Server accept failed: ${e.message}")
            } finally {
                try {
                    serverSocket?.close()
                } catch (_: IOException) {}
            }
        }.start()
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
    override fun pairDevice(device: BluetoothDevice) {
        if (device.bondState != BluetoothDevice.BOND_BONDED) {
            device.createBond()
        } else {
            connectToDevice(device)
        }
    }

    override fun sendMessage(message: String) {

    }

    override fun observeMessages(): Flow<String> {
        return flowOf("")
    }

    override fun observeConnectedDevices() = discoveredDevices
}