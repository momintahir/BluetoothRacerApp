package com.momin.bluetoothracerapp.feature.search.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.momin.bluetoothracerapp.feature.search.domain.BluetoothDeviceDomain

@Composable
fun DeviceItem(device: BluetoothDeviceDomain, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F0FE)) // light bluish
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = device.name ?: "Unknown Device",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = device.address,
                style = MaterialTheme.typography.bodySmall,
                color = Color.DarkGray
            )
        }
    }
}