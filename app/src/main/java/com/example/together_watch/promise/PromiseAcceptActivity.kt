package com.example.together_watch.promise

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.together_watch.ui.Destinations
import com.example.together_watch.ui.theme.Together_watchTheme

class PromiseAcceptActivity: ComponentActivity(), PromiseAcceptContract.View {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            Together_watchTheme {
                Wrapper()
            }
        }

        val action: String? = intent?.action
        val data: Uri? = intent?.data
    }

    @Preview
    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    fun Wrapper() {
        Scaffold(
            bottomBar = { Buttons() }
        ) {
            Column (
                modifier = Modifier.padding(it)
            ){
                PromiseAcceptScreen()
            }
        }

    }

    @Composable
    override fun PromiseAcceptScreen() {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 30.dp)
        ) {
            Text("새로운 약속이 도착했어요!", modifier = Modifier.padding(bottom = 5.dp), style = TextStyle(fontSize = 20.sp))
            Text("약속에 참가하려면, 확인 버튼을 눌러주세요.", modifier = Modifier.padding(bottom = 10.dp), style = TextStyle(fontSize = 15.sp))
            Spacer(modifier = Modifier.height(20.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
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

    @Composable
    override fun Buttons() {
        Row {
            Button(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RectangleShape
            ) {
                Text("취소")
            }
            Button(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RectangleShape
            ) {
                Text("확인")
            }
        }
    }
}