package com.example.together_watch.ui.setting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AccountManagementScreen() {
    var showDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(PaddingValues(all = 20.dp))) {
        // 계정 관리
        Text(
            text = "계정 관리",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(15.dp))

        // 로그아웃
        Text(
            text = "로그아웃",
            fontSize = 18.sp,
            color = Color.Black,
            modifier = Modifier.clickable { /* 로그아웃 처리 */ }
        )
        Spacer(modifier = Modifier.height(12.dp))
        // 탈퇴
        Text(
            text = "탈퇴",
            fontSize = 18.sp,
            color = Color.Gray,
            modifier = Modifier.clickable { showDialog = true }
        )
    }

    // 탈퇴 다이얼로그
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "탈퇴 확인") },
            text = { Text(text = "정말 탈퇴하시겠습니까?") },
            confirmButton = {
                Button(onClick = {
                    // 탈퇴 처리
                    showDialog = false
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