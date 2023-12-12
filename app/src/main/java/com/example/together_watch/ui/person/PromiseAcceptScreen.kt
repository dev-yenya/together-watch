package com.example.together_watch.ui.person

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.together_watch.R
import com.example.together_watch.data.FetchedPromise
import com.example.together_watch.data.Status
import com.example.together_watch.ui.Destinations

import com.example.together_watch.ui.MainViewModel

import com.example.together_watch.ui.theme.Black
import com.example.together_watch.ui.theme.Blue
import com.example.together_watch.ui.theme.DarkGray
import com.example.together_watch.ui.theme.Gray



// 약속 수락
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PromiseAcceptScreen(navController: NavHostController, viewModel: MainViewModel) {

    val currentScreen = remember { mutableIntStateOf(1) }
    val nextScreen = { currentScreen.intValue++ }
    val previousScreen = { currentScreen.intValue-- }
    val complete = { /* 완료 액션 구현 */ }
    var showDialog by remember { mutableStateOf(false) }
    var flag = 0
    val backHandler = {
        if (currentScreen.intValue > 1) {
            previousScreen()
        } else {
            navController.popBackStack()
        }
    }

    BackHandler {
        backHandler()
    }


    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween

        ) {
            Column(
                modifier = Modifier.weight(1f, true), // Takes up all available space except for the button
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (currentScreen.intValue < 4) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = if (currentScreen.intValue == 1) {
                                "약속 수락"
                            } else if (currentScreen.intValue == 2) {
                                "약속 확정"
                            } else {
                                "약속 시간"
                            },
                            modifier = Modifier.align(Alignment.Center),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(
                            onClick = { backHandler() },
                            modifier = Modifier.align(Alignment.CenterStart)
                        ) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = null)
                        }
                    }
                }

                if (currentScreen.intValue < 4) {
                    LinearProgressIndicator(
                        progress = currentScreen.intValue * 0.33f,
                        modifier = Modifier.fillMaxWidth(),
                        color = Blue
                    )
                }

                when (currentScreen.intValue) {
                    1 -> PromiseFirstScreen()
                    2 -> PromiseSecondScreen(viewModel)
                    3 -> PromiseThirdScreen()
                    4 -> PromiseCompleteScreen()
                }
            }

            if (currentScreen.intValue == 1) {
                Row {
                    Button(
                        onClick = {
                            flag = 1
                            showDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(Gray),
                        modifier = Modifier
                            .height(50.dp)
                            .weight(1f)
                            .fillMaxWidth(),
                        shape = RectangleShape,
                    ) {
                        Text(
                            text = "취소",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkGray)
                    }
                    Button(
                        onClick = {
                            flag = 2
                            showDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(Blue),
                        modifier = Modifier
                            .height(50.dp)
                            .weight(1f)
                            .fillMaxWidth(),
                        shape = RectangleShape
                    ) {
                        Text(
                            text = "확인",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Black
                            )
                    }
                }
            } else {
                Button(
                    onClick = if (currentScreen.intValue < 4) {
                        { nextScreen() }
                    } else {
                        { complete() }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(Blue),
                    shape = RectangleShape,
                ) {
                    Text(
                        text = if (currentScreen.intValue < 4) "다음" else "캘린더에서 일정 확인하기",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Black
                    )
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = if (flag == 1) "" else "약속 참가 성공!") },
            text = { Text(text = if (flag == 1) "정말 탈퇴하시겠습니까?" else "약속 일정이 확정되면 캘린더에 추가 됩니다.") },
            confirmButton = {
                Button(onClick = {
                    showDialog = false
                    if (flag != 1) {
                        nextScreen()
                    }
                },
                    colors = ButtonDefaults.buttonColors(Blue)
                ) {
                    Text(text = "확인", color = Black)
                }
            },
            dismissButton = {
                if (flag == 1) {
                    Button(onClick = { showDialog = false }, colors = ButtonDefaults.buttonColors(Blue)) {
                        Text("취소", color = DarkGray)
                    }
                }
            }
        )
    }
}


@Composable
fun PromiseFirstScreen() {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 30.dp)
    ) {
        Text("새로운 약속이 도착했어요!", modifier = Modifier.padding(bottom = 5.dp), style = TextStyle(fontSize = 20.sp), fontWeight = FontWeight.Bold)
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
fun PromiseSecondScreen(viewModel: MainViewModel) {

    Column(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 30.dp)
    ) {
        Text("약속을 확정해 볼까요?", modifier = Modifier.padding(bottom = 5.dp), style = TextStyle(fontSize = 20.sp), fontWeight = FontWeight.Bold)
        Text("모든 멤버들이 약속에 참여했나요?", modifier = Modifier.padding(bottom = 10.dp), style = TextStyle(fontSize = 15.sp))
        Spacer(modifier = Modifier.height(20.dp))
        viewModel.selectedPromise?.let { fetchedPromise ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)

            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "${fetchedPromise.promise.dates?.joinToString("\n")}",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = "Time: ${fetchedPromise.promise.startTime} ~ ${fetchedPromise.promise.endTime}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Event Title: ${fetchedPromise.promise.name}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text("약속에 참여한 멤버들", modifier = Modifier.padding(bottom = 5.dp), style = TextStyle(fontSize = 20.sp), fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))
        LazyColumn {
            items(viewModel.selectedPromise?.promise?.users ?: listOf()) { userId ->
                ProfileCard(userId, viewModel)
            }
        }
    }
}

@Composable
fun ProfileCard(userId: String, viewModel: MainViewModel) {
    val user = viewModel.users.find { it.uid == userId }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 5.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),

        ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            AsyncImage(
                model = user?.photoURL ?: "",
                contentDescription = "프로필 이미지",
                modifier = Modifier
                    .size(40.dp)
                    .border(2.dp, Gray, shape = CircleShape)
                    .clip(CircleShape),
            )

            Spacer(modifier = Modifier.width(8.dp))
            Text(text = user?.displayName ?: "", style = MaterialTheme.typography.bodyMedium)
        }
    }
}


@Composable
fun PromiseThirdScreen() {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 30.dp)
    ) {
        Text("언제 만날까요?", modifier = Modifier.padding(bottom = 5.dp), style = TextStyle(fontSize = 20.sp), fontWeight = FontWeight.Bold)
        Text("약속 시간대를 입력해 주세요.", modifier = Modifier.padding(bottom = 10.dp), style = TextStyle(fontSize = 15.sp))
        Spacer(Modifier.height(10.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp, horizontal = 10.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White,
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 5.dp
            )
        ) {
            Text(text = "2022.11.02 (목) 14:00 ~ 18:00", Modifier.padding(horizontal = 10.dp, vertical = 5.dp))
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp, horizontal = 10.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White,
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 5.dp
            )
        ) {
            Text(text = "2022.11.02 (목) 14:00 ~ 18:00", Modifier.padding(horizontal = 10.dp, vertical = 5.dp))
        }
    }
}

@Composable
fun PromiseCompleteScreen() {
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 30.dp)
            .fillMaxWidth(), // Column을 가로로 채우도록 설정
        horizontalAlignment = Alignment.CenterHorizontally // 내용을 가로 중앙에 배치설정
    ) {
        Spacer(Modifier.height(25.dp))
        Text(
            "약속 확정이 완료되었어요!",
            modifier = Modifier.padding(bottom = 5.dp)
                .align(Alignment.Start),
            style = TextStyle(fontSize = 20.sp),
            fontWeight = FontWeight.Bold
        )
        Text(
            "이제 모두의 캘린더에 약속이 추가되었어요",
            modifier = Modifier.padding(bottom = 10.dp)
                .align(Alignment.Start),
            style = TextStyle(fontSize = 15.sp),
        )
        Spacer(Modifier.height(100.dp))
        Icon(
            Icons.Default.DateRange,
            contentDescription = null,
            modifier = Modifier.size(150.dp) // 아이콘 크기 조정
        )
        Spacer(Modifier.height(30.dp))
        Text(
            "약속 시간에 맞춰 미리\n톡캘린더로 리마인드 해드릴게요!",
            textAlign = TextAlign.Center // 텍스트 중앙 정렬
        )
    }
}