package com.example.together_watch.login

import LoginScreen
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.together_watch.MainActivity
import com.example.together_watch.R
import com.example.together_watch.ui.theme.*
import com.example.together_watch.ui.theme.Together_watchTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.common.util.Utility
import androidx.compose.foundation.layout.Row
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity: ComponentActivity(), LoginContract.View {
    val user = Firebase.auth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        Log.e("hash", Utility.getKeyHash(this))
        setContent {
            LoginScreen()
        }
    }

    public override fun onStart() {
        super.onStart()
        FirebaseApp.initializeApp(this)
        startMainActivity(this, user)
    }

    @Composable
    override fun LoginScreen() {
        val context = LocalContext.current
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier.padding(start = 40.dp, top = 80.dp),
                text = "바쁜 일정도, 약속도\n더이상 고민하지 마세요.",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.Bold
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = painterResource(id = R.drawable.icon_app_splash), // 중간 이미지 (로고)
                    contentDescription = "Logo"
                )
            }
            LoginButton()
        }
    }

    @Composable
    override fun LoginButton() {
        val context = LocalContext.current
        Button(
            modifier = Modifier
                .padding(bottom = 70.dp, start = 20.dp, end = 20.dp)
                .fillMaxWidth()
                .height(50.dp),
            onClick = {
                Log.d("kakao-sdk", "카카오 로그인")
                val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                    if (error != null) {
                        Log.e("kakao-sdk", "카카오 계정으로 로그인 실패", error)
                    } else if (token != null) {
                        LoginPresenter().callKakaoLoginFunction(token.accessToken) {
                            if (it) {
                                startMainActivity(context, user)
                            }
                        }
                    }
                }

                if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                    // 카카오톡 로그인
                    UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                        // 로그인 실패 부분
                        if (error != null) {
                            Log.e("kakao-login-sdk", "로그인 실패 $error")
                            // 사용자가 취소
                            if (error is ClientError && error.reason == ClientErrorCause.Cancelled ) {
                                return@loginWithKakaoTalk
                            }
                            // 다른 오류
                            else {
                                UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
                            }
                        }
                        // 로그인 성공 부분
                        else if (token != null) {
                            Log.d("kakao-login-sdk", "로그인 성공 ${token.accessToken}")
                            LoginPresenter().callKakaoLoginFunction(token.accessToken) {
                                if (it) {
                                    startMainActivity(context, user)
                                }
                            }
                        }
                    }
                } else {
                    UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = KakaoYellow)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_kakao),
                    contentDescription = "Login With Kakao",
                    modifier =  Modifier
                        .size(30.dp) // 아이콘 크기 조절
                        .padding(end = 10.dp), // 아이콘과 텍스트 간격 조절,
                    tint = com.example.together_watch.ui.theme.Black
                )
                Text("카카오톡으로 1초만에 로그인", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }

    private fun startMainActivity(context: Context, user:  FirebaseUser?) {
        val intent = Intent(context, MainActivity::class.java).apply { /* 유저 정보 */}
        if (user != null) {
            Log.d("user-info", user.uid+", "+user.displayName+", "+user.photoUrl)
            context.startActivity(intent)
        }
    }
}