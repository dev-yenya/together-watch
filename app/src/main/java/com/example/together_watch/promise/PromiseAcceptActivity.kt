package com.example.together_watch.promise

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.together_watch.MainActivity
import com.example.together_watch.data.Promise
import com.example.together_watch.data.Status
import com.example.together_watch.ui.theme.Together_watchTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class PromiseAcceptActivity : ComponentActivity(), PromiseAcceptContract.View {
    private lateinit var path: Uri
    private lateinit var ownerId: String
    private lateinit var groupId: String
    private val model = PromiseAcceptModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val data = intent?.data
        path = Uri.parse(data.toString())
        Log.d("kakao-share-api", "초대장 수락자 intent.data: ${path.getQueryParameter("ownerId")}")
        ownerId = path.getQueryParameter("ownerId") as String
        groupId = path.getQueryParameter("groupId") as String
        setContent {
            Together_watchTheme {
                Wrapper(this)
            }
        }
    }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
     fun Wrapper(context: Context) {
        var data: Promise by remember { mutableStateOf(
            Promise("", "", listOf(),Status.ONPROGRESS, listOf(), "", "", "")
        ) }

        LaunchedEffect(Unit) {
            data = model.getGroupPromise(ownerId, groupId)
        }

        Scaffold(
            bottomBar = { Buttons(context) }
        ) {
            Column(
                modifier = Modifier.padding(it)
            ) {
                PromiseAcceptScreen(data)
            }
        }

    }

    @Composable
    override fun PromiseAcceptScreen(promise: Promise) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 30.dp)
        ) {
            Text(
                "새로운 약속이 도착했어요!",
                modifier = Modifier.padding(bottom = 5.dp),
                style = TextStyle(fontSize = 20.sp)
            )
            Text(
                "약속에 참가하려면, 확인 버튼을 눌러주세요.",
                modifier = Modifier.padding(bottom = 10.dp),
                style = TextStyle(fontSize = 15.sp)
            )
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
                    Text(
                        text = "약속시간은 모두가 괜찮은 시간대로 정해볼게요.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(text = promise.name, style = MaterialTheme.typography.headlineMedium)
                    Text(text = promise.place, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }

    @Composable
    override fun Buttons(context: Context) {
        Row {
            Button(
                onClick = { refuseAction(context) },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RectangleShape
            ) {
                Text("취소")
            }
            Button(
                onClick = { acceptAction(context) },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RectangleShape
            ) {
                Text("확인")
            }
        }
    }

    private fun acceptAction(context: Context) {
        val loginUser = Firebase.auth.currentUser
        model.addPromiseMember(loginUser?.uid, ownerId)
        AlertDialog.Builder(context)
            .setTitle("약속 참가 성공!")
            .setMessage("약속 일정이 확정되면 캘린더에 추가됩니다.")
            .setPositiveButton("확인") { _, _ ->
                startActivity(Intent(context, MainActivity::class.java))
            }
            .create()
            .show()
    }

    private fun refuseAction(context: Context) {
        AlertDialog.Builder(context)
            .setMessage("정말 취소하시겠습니까?")
            .setPositiveButton("확인") { _, _ ->
                Toast.makeText(context, "초대를 거절했습니다.", Toast.LENGTH_LONG).show()
                startActivity(Intent(context, MainActivity::class.java))
            }
            .setNegativeButton("취소") { _, _ -> }
            .create()
            .show()
    }
}