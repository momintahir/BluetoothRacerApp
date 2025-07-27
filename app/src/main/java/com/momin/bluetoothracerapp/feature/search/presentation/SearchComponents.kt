package com.momin.bluetoothracerapp.feature.search.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.momin.bluetoothracerapp.R
import com.momin.bluetoothracerapp.feature.search.domain.BluetoothDeviceDomain

@Composable
fun DeviceItem(device: BluetoothDeviceDomain, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .height(70.dp)
            .clickable { onClick() }
            .fillMaxWidth()
            .border(1.dp, Color.DarkGray)
            .padding(start = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(painter = painterResource(R.drawable.ic_mobile), contentDescription = "", modifier = Modifier.size(30.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = device.name?.uppercase() ?: "Unnamed Device",
                style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black, fontWeight = FontWeight.Bold)
            )
            Text(
                text = device.address,
                style = MaterialTheme.typography.bodySmall.copy(color = Color.Black)
            )
        }
    }
}

@Composable
fun Button(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
        modifier = Modifier
            .height(50.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        Text(text, fontWeight = FontWeight.Bold)
    }
}
