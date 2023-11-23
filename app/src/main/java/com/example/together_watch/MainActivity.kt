package com.example.together_watch

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.together_watch.bottomsheet.BottomSheetActivity
import com.example.together_watch.ui.theme.Together_watchTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Together_watchTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.size(300.dp, 200.dp),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                    BottomSheetButton {
                        Intent(applicationContext, BottomSheetActivity::class.java).also {
                            startActivity(it)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        Together_watchTheme {
            Greeting("Android")
            BottomSheetButton {
            }
        }
    }

    @Composable
    fun BottomSheetButton(onClick: () -> Unit) {
        Button(
            onClick = {
                onClick()
            },
            modifier = Modifier
                .width(250.dp)
                .height(70.dp)
                .padding(16.dp)
        ) {
            Text("bottomsheet")
        }
    }
}