package com.example.together_watch

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.together_watch.ui.theme.Together_watchTheme

class LoginActivity: ComponentActivity(), LoginContract.View {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContent {
            Together_watchTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ShowTestButton()
                }
            }
        }
    }

    @Preview
    @Composable
    fun ShowTestButton() {
        Column {
            ShowLoginButton()
            ShowHomeButton()
        }
    }

    @Composable
    override fun ShowLoginButton() {
        Button(
            onClick = { Log.d("jihyun", "카카오 로그인")}
        ) {
            Text(text = "카카오로 로그인")
        }
    }

    @Composable
    override fun ShowHomeButton() {
        Button(
            onClick = { Log.d("jihyun", "홈으로 가기")}
        ) {
            Text(text = "홈으로 바로 가기")
        }
    }


}
