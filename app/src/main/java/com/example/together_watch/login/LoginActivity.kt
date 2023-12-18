package com.example.together_watch.login

import LoginScreen
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.together_watch.MainActivity
import com.example.together_watch.R
import com.example.together_watch.ui.theme.*
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseUser

class LoginActivity: ComponentActivity(), LoginContract.View {
    var user = Firebase.auth.currentUser
    lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
//        Log.e("hash", Utility.getKeyHash(this))

        val sharedPreferences = getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        setContent {
            LoginScreen()
        }
    }

    public override fun onStart() {
        super.onStart()
        user = Firebase.auth.currentUser
        if (user != null)
            startMainActivity(this)
    }

    @Composable
    override fun LoginScreen() {
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
                    .height(400.dp),
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

                // 카카오 웹 로그인
                val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                    if (error != null) {
                        Log.e("kakao-sdk", "카카오 계정으로 로그인 실패", error)
                    } else if (token != null) {
                        Log.d("kakao-sdk", "로그인 성공 ${token.accessToken}")
                        LoginPresenter().callKakaoLoginFunction(token.accessToken) {
                            if (it) {
                                editor.putString("access_token", token.accessToken)
                                editor.apply()
                                startMainActivity(context)
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
                                    startMainActivity(context)
                                }
                            }
                        }
                    }
                } else {
                    UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
                }
                UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
                    if (error != null) {
                        Log.e("kakao-token", "토큰 정보 보기 실패", error)
                    }
                    else if (tokenInfo != null) {
                        Log.i("kakao-token", "토큰 정보 보기 성공" +
                                "\n회원번호: ${tokenInfo.id}" +
                                "\n만료시간: ${tokenInfo.expiresIn} 초")
                    }
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

    private fun startMainActivity(context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        context.startActivity(intent)
        finishAffinity()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}

