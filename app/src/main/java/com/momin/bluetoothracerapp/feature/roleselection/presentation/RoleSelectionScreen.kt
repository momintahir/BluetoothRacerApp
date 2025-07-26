package com.momin.bluetoothracerapp.feature.roleselection.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.momin.bluetoothracerapp.core.navigation.Screen

@Composable
fun RoleSelectionScreen(
    navController: NavController) {
    var player1Name by remember { mutableStateOf("") }
    var player2Name by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Enter Your Name",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = player1Name,
            placeholder = {
                Text("Enter Player 1 Name")
            },
            onValueChange = { player1Name = it },
            label = { "Player 1 Name" },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        Button(
            onClick = {
                navController.navigate(Screen.Game.createRoute(isHost = true)) // Treat Player 1 as host
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            enabled = player1Name.isNotBlank()
        ) {
            Text("Start as Player 1")
        }

        OutlinedTextField(
            value = player2Name,
            onValueChange = { player2Name = it },
            label = { "Player 2 Name" },
            placeholder = {
                Text("Enter Player 2 Name")
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        Button(
            onClick = {
                navController.navigate(Screen.Game.createRoute(isHost = false)) // Treat Player 2 as guest
            },
            modifier = Modifier
                .fillMaxWidth(),
            enabled = player2Name.isNotBlank()
        ) {
            Text("Start as Player 2")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Player 1 will host the game.\nPlayer 2 will connect to Player 1.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}
