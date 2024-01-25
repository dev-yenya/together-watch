package com.together_watch.together_watch.ui.setting

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.kakao.sdk.user.UserApiClient
import com.together_watch.together_watch.account.callQuitFunction

@Composable
fun AccountManagementScreen(navController: NavController) {
    var showDialog by remember { mutableStateOf(false) }
    var quit by remember { mutableStateOf(false) }

    LaunchedEffect(quit) {
        if (quit) {
            callQuitFunction()
            quit = false
            Log.d("quit", "로그인화면으로 이동해야")
        }
    }

    fun logout() {
        // 카카오 로그아웃
        UserApiClient.instance.logout { error ->
            if (error != null) {
                Log.e("kakao-logout", "로그아웃 실패. SDK에서 토큰 삭제됨", error)
            }
            else {
                Log.i("kakao-logout", "로그아웃 성공. SDK에서 토큰 삭제됨")
            }

            // Firebase 로그아웃
            Firebase.auth.signOut()
            navController.navigate("login_screen")
            val user = Firebase.auth.currentUser
            Log.e("logout", user?.uid.toString())
        }
    }

    Column(modifier = Modifier.padding(PaddingValues(all = 25.dp))) {
        Spacer(modifier = Modifier.height(5.dp))

        // 계정 관리
        Text(
            text = "계정 관리",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(20.dp))

        // 로그아웃
        Text(
            text = "로그아웃",
            fontSize = 15.sp,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { logout() }
        )

        Spacer(modifier = Modifier.height(20.dp))
        // 탈퇴
        Text(
            text = "탈퇴",
            fontSize = 15.sp,
            color = Color.Gray,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    showDialog = true
                }
        )
    }

    // 탈퇴 다이얼로그
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "탈퇴 확인") },
            text = { Text(text = "탈퇴하면 내가 저장한 일정들이 모두 삭제되고, 내가 참여한 모든 약속들의 참여자 목록에서 제외됩니다.정말 탈퇴하시겠습니까?") },
            confirmButton = {
                Button(onClick = {
                    // 탈퇴 처리
                    quit = true
                    showDialog = false
                    logout()    // 탈퇴 후 자동 로그아웃
                }) {
                    Text("확인")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("취소")
                }
            }
        )
    }
}

