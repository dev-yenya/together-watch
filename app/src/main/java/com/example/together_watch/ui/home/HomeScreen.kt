package com.example.together_watch.ui.home

import BottomSheetCreateDialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.together_watch.ui.Destinations

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    navController: NavController
) {
    var events by remember { mutableStateOf(listOf<String>()) }
    var showAddEvent by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {

        FloatingActionButton(
            onClick = { showAddEvent = !showAddEvent },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(if (showAddEvent) Icons.Filled.Close
                else Icons.Filled.Add, "Toggle Buttons")
        }

        // 추가 버튼들 표시
        AnimatedVisibility(
            visible = showAddEvent,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 82.dp, end = 26.dp)
        ) {
            Column(horizontalAlignment = Alignment.End) {
                ButtonRow("약속 일정 추가", onClick = { navController.navigate(Destinations.CreatePromiseScreen.route) })
                ButtonRow("개인 일정 추가", onClick = {
                    val context = navController.context
                    val bottomSheetCreateDialog = BottomSheetCreateDialog(context)
                    bottomSheetCreateDialog.showBottomSheet()
                })
            }
        }
    }
}

@Composable
fun ButtonRow(text: String, onClick: () -> Unit) {
    Spacer(modifier = Modifier.height(10.dp))
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = text,
            modifier = Modifier.padding(end = 8.dp)
        )
        Button(
            onClick = onClick,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(Icons.Filled.ArrowForward, contentDescription = text)
        }
    }
}



@Composable
fun EventsList(events: List<String>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        events.forEach { event ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp), // 직접적으로 backgroundColor를 지정
                colors = CardDefaults.cardColors(
                    containerColor = Color.White,
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 5.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(text = "Event Date and Time", style = MaterialTheme.typography.headlineMedium)
                    Text(text = "Event Title: ", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Event Details", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}




