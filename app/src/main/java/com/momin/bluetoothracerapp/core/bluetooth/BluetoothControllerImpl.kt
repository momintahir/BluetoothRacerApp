package com.momin.bluetoothracerapp.core.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

class BluetoothControllerImpl(private val context: Context):BluetoothController {

    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
        manager?.adapter
    }
    private val discoveredDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    device?.let {
                        val current = discoveredDevices.value
                        if (current.none { it.address == device.address }) {
                            discoveredDevices.value = current + device
                        }
                    }
                }
            }
        }
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

    override fun connectToDevice(device: BluetoothDevice) {

    }

    override fun disconnect() {

    }

    override fun sendMessage(message: String) {

    }

    override fun observeMessages(): Flow<String> {
        return flowOf("")
    }

    override fun observeConnectedDevices(): Flow<List<BluetoothDevice>> {
        return discoveredDevices
    }
}