package com.example.together_watch.ui.person

import android.os.Build
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.together_watch.promise.DateBlock
import com.example.together_watch.promise.PromiseCompletionModel

import com.example.together_watch.ui.MainViewModel
import com.example.together_watch.ui.home.TimeBoundary
import com.example.together_watch.ui.home.TimePickScreen

import com.example.together_watch.ui.theme.Black
import com.example.together_watch.ui.theme.Blue
import com.example.together_watch.ui.theme.DarkGray
import com.example.together_watch.ui.theme.Gray


// 약속 수락
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ConfirmPromiseScreen(navController: NavHostController, viewModel: MainViewModel) {

    val timeFunctionModel = PromiseCompletionModel()
    val context = LocalContext.current
    val currentScreen = remember { mutableIntStateOf(1) }
    val nextScreen = { currentScreen.intValue++ }
    val previousScreen = { currentScreen.intValue-- }
    val complete = { /* TODO 톡캘린더 함수 연결 */}
    val saveSchedules = { viewModel.savePromiseSchedule() }
    val areValidTimes =  { start: String, end: String -> viewModel.isValidTimeRange(start, end)}
    var showDialog by remember { mutableStateOf(false) }
    var getTimeClicked by remember { mutableStateOf(false) }
    var blocks by remember { mutableStateOf<List<DateBlock>>(emptyList()) }
    var flag = 0
    val backHandler = {
        if (currentScreen.intValue > 1) {
            previousScreen()
        } else {
            navController.popBackStack()
        }
    }

    LaunchedEffect(getTimeClicked) {
        if (getTimeClicked) {
            viewModel.selectedPromise?.let {
                blocks = timeFunctionModel.makeSchedule(it)
                getTimeClicked = false
            }
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
                modifier = Modifier.weight(
                    1f,
                    true
                ), // Takes up all available space except for the button
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

                if (currentScreen.intValue < 3) {
                    LinearProgressIndicator(
                        progress = currentScreen.intValue * 0.33f,
                        modifier = Modifier.fillMaxWidth(),
                        color = Blue
                    )
                }

                when (currentScreen.intValue) {
                    1 -> ConfirmPromiseFirstScreen(viewModel)
                    2 -> ConfirmPromiseSecondScreen(viewModel, blocks)
                    3 -> {
                        TimePickScreen(
                            viewModel,
                            TimeBoundary(viewModel.selectedBlock!!.startTime, viewModel.selectedBlock!!.endTime)
                        ) { text1, text2 ->
                            viewModel.confirmedStartTime = text1
                            viewModel.confirmedEndTime = text2
                        }
                    }
                    4 -> ConfirmPromiseCompleteScreen()
                }
            }
            Button(
                onClick = when (currentScreen.intValue) {
                    1 -> {
                        { getTimeClicked = true
                            nextScreen() }
                    }
                    2 -> {
                        { nextScreen() }
                    }
                    3 -> {
                        {
                            if (areValidTimes(viewModel.confirmedStartTime, viewModel.confirmedEndTime)) {
                                Log.d("promise-completion", "조건 만족")
                                saveSchedules()
                                nextScreen()
                            } else {
                                Log.d("promise-completion", "조건 만족하지 않음")
                                showDialog = true
                            }
                        }
                    }
                    else -> {
                        { complete() }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
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

            if (showDialog) {
                AlertDialog(
                    title = { Text("입력 시간이 유효하지 않음") },
                    text = { Text("입력한 시간이 선택한 시간대에 포함되는지, 종료시각이 시작시각이 올바르게 입력됐는지 확인하세요.") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showDialog = false
                            }
                        ) {
                            Text("확인")
                        }
                    },
                    onDismissRequest = {
                        showDialog = false
                    }
                )
            }
        }
    }
}

@Composable
fun ConfirmPromiseFirstScreen(viewModel: MainViewModel) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 30.dp)
    ) {
        Text(
            "약속을 확정해 볼까요?",
            modifier = Modifier.padding(bottom = 5.dp),
            style = TextStyle(fontSize = 20.sp),
            fontWeight = FontWeight.Bold
        )
        Text(
            "모든 멤버들이 약속에 참여했나요?",
            modifier = Modifier.padding(bottom = 10.dp),
            style = TextStyle(fontSize = 15.sp)
        )
        Spacer(modifier = Modifier.height(20.dp))
        viewModel.selectedPromise?.let { fetchedPromise ->
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
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkGray
                    )
                    Text(
                        text = "${fetchedPromise.promise.name}",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = "${fetchedPromise.promise.place}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            "약속에 참여한 멤버들",
            modifier = Modifier.padding(bottom = 5.dp),
            style = TextStyle(fontSize = 20.sp),
            fontWeight = FontWeight.Bold
        )
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
fun ConfirmPromiseSecondScreen(viewModel: MainViewModel, blocks: List<DateBlock>) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 30.dp)
    ) {
        Text(
            "시간을 선택해 주세요",
            modifier = Modifier.padding(bottom = 5.dp),
            style = TextStyle(fontSize = 20.sp),
            fontWeight = FontWeight.Bold
        )
        Text(
            "멤버들이 모두 가능한 시간대를 골라봤어요.",
            modifier = Modifier.padding(bottom = 10.dp),
            style = TextStyle(fontSize = 15.sp)
        )
        Spacer(Modifier.height(10.dp))

        // 클릭된 카드의 인덱스를 추적하기 위한 상태 변수
        var selectedCardIndex by remember { mutableStateOf(-1) }

        // 각 카드를 만들기 위한 반복문
        for (index in blocks.indices) {
            ClickableCard(
                text = blocks[index].toString(),
                isSelected = index == selectedCardIndex,
                onClick = {
                    selectedCardIndex = index
                    viewModel.selectedBlock = blocks[selectedCardIndex]
                }
            )
        }
    }
}

@Composable
fun ClickableCard(text: String, isSelected: Boolean, onClick: () -> Unit) {
    val cardColors = if (isSelected) {
        CardDefaults.cardColors(containerColor = Blue.copy(alpha = 0.7f))
    } else {
        CardDefaults.cardColors(containerColor = Color.White)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 10.dp)
            .clickable {
                onClick()
            },
        colors = cardColors,
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 5.dp
        )
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            color = Color.Black
        )
    }
}

@Composable
fun ConfirmPromiseCompleteScreen() {
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 30.dp)
            .fillMaxWidth(), // Column을 가로로 채우도록 설정
        horizontalAlignment = Alignment.CenterHorizontally // 내용을 가로 중앙에 배치설정
    ) {
        Spacer(modifier = Modifier.height(30.dp))
        Text(
            "약속 확정이 완료되었어요!",
            modifier = Modifier
                .padding(bottom = 5.dp)
                .align(Alignment.Start),
            style = TextStyle(fontSize = 20.sp),
            fontWeight = FontWeight.Bold
        )
        Text(
            "이제 모두의 캘린더에 약속이 추가되었어요",
            modifier = Modifier
                .padding(bottom = 10.dp)
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