package com.example.together_watch.ui.person

import android.os.Build
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
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
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.together_watch.R
import com.example.together_watch.promise.DateBlock
import com.example.together_watch.promise.PromiseCompletionModel
import com.example.together_watch.schedule.updateAndDelete.UpdateAndDeleteModel
import com.example.together_watch.schedule.updateAndDelete.UpdateAndDeleteScheduleDialog
import com.example.together_watch.schedule.updateAndDelete.UpdateAndDeleteSchedulePresenter
import com.example.together_watch.ui.Destinations

import com.example.together_watch.ui.MainViewModel
import com.example.together_watch.ui.home.TimeBoundary
import com.example.together_watch.ui.home.TimePickerScreen

import com.example.together_watch.ui.theme.Black
import com.example.together_watch.ui.theme.Blue
import com.example.together_watch.ui.theme.DarkGray
import com.example.together_watch.ui.theme.Gray
import com.example.together_watch.ui.theme.KakaoYellow
import kotlinx.coroutines.delay
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalTime

// 약속 수락
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ConfirmPromiseScreen(navController: NavHostController, viewModel: MainViewModel) {

    val timeFunctionModel = PromiseCompletionModel()
    val currentScreen = remember { mutableIntStateOf(1) }
    val nextScreen = { currentScreen.intValue++ }
    val previousScreen = { currentScreen.intValue-- }
    val complete = { navController.navigate(Destinations.HomeScreen.route)}
    val saveSchedules = { viewModel.savePromiseSchedule() }
    val areValidTimes =  { start: String, end: String -> viewModel.isValidTimeRange(start, end)}
    var promiseTimeDialog by remember { mutableStateOf(false) }
    var promiseTimeSelectDialog by remember{ mutableStateOf(false) }
    var getTimeClicked by remember { mutableStateOf(false) }
    var blocks by remember { mutableStateOf<List<DateBlock>>(emptyList()) }
    var elapsedTimeMillis by remember{ mutableLongStateOf(0) }
    var isTimeSelected by remember{ mutableStateOf(false) }
    val backHandler = {
        if (currentScreen.intValue > 1) {
            previousScreen()
        } else {
            navController.popBackStack()
        }
        viewModel.selectedBlock=null
    }

//    fun createEvent() {
//        val apiService = ApiClient.create(TalkCalendarService::class.java)
//        val sharedPreferences =
//            navController.context.getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE)
//        val accessToken = sharedPreferences.getString("access_token", null)
//        val request = EventReq(
//            calendar_id = "primary",
//                event = EventDetails(
//                    title = "일정 제목",
//                    time = TimeDetails(
//                        start_at = "2022-10-29T03:00:00Z",
//                        end_at = "2022-10-29T06:00:00Z",
//                        time_zone = "Asia/Seoul",
//                        all_day = false,
//                        lunar = false
//                    ),
//                rrlue = "FREQ=DAILY;UNTIL=20221031T000000Z",
//                description = "일정 설명",
//                location = LocationDetails(
//                    name = "카카오",
//                    location_id = 18577297,
//                    address = "경기 성남시 분당구 판교역로 166",
//                    latitude = 37.39570088983171,
//                    longitude = 127.1104335101161
//                ),
//                reminders = listOf(15, 30),
//                color = "ROYAL_BLUE"
//            )
//        )
//        try {
//            apiService.createEvent("Bearer $accessToken", request).enqueue(object : Callback<EventRes> {
//                override fun onResponse(call: Call<EventRes>, response: Response<EventRes>) {
//                    if (response.isSuccessful) {
//                        Log.d("kakao-calendar", "Event 생성 성공 ${response.body()}")
//                    }
//                    else {
//                        Log.e("kakao-calendar","Event 생성 실패 ${response}")
//                    }
//                }
//
//                override fun onFailure(call: Call<EventRes>, t: Throwable) {
//                    Log.e("kakao-calendar", "Event 생성 실패 ${t}")
//                    t.printStackTrace()
//                }
//
//            })
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Log.e("kakao-calendar", "Event 생성 실패 ${e}")
//        }
//    }

    LaunchedEffect(getTimeClicked) {
        if (getTimeClicked) {
            // 시간 측정 시작
            val startTime = System.currentTimeMillis()
            viewModel.selectedPromise?.let {
                blocks = timeFunctionModel.makeSchedule(it)
                getTimeClicked = false
            }
            // 시간 측정 종료 및 결과
            val endTime = System.currentTimeMillis()
            elapsedTimeMillis = endTime - startTime
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
                    2 -> isTimeSelected=ConfirmPromiseSecondScreen(viewModel, blocks,elapsedTimeMillis)
                    3 -> {
                        ConfirmTimePickScreen(
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
                        {
                            if(viewModel.selectedBlock==null) promiseTimeSelectDialog=true
                            /*if(!isTimeSelected){
                                promiseTimeSelectDialog=true
                            }*/
                            else nextScreen()
                            Log.d("chaeae","star time: ${viewModel.selectedBlock?.startTime.toString()}")
                        }
                    }
                    3 -> {
                        {
                            if (areValidTimes(viewModel.confirmedStartTime, viewModel.confirmedEndTime)) {
                                Log.d("promise-completion", "조건 만족")
                                saveSchedules()
                                nextScreen()
                            } else {
                                Log.d("promise-completion", "조건 만족하지 않음")
                                promiseTimeDialog = true
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
            @Composable
            fun makeDialog(title:String, text: String,showDialog:Int){
                AlertDialog(
                    title = { Text(title) },
                    text = { Text(text) },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                when(showDialog){
                                    1->promiseTimeSelectDialog=false
                                    2->promiseTimeDialog=false
                                }
                            }
                        ) {
                            Text("확인")
                        }
                    },
                    onDismissRequest = {
                        when(showDialog){
                            1->promiseTimeSelectDialog=false
                            2->promiseTimeDialog=false
                        }
                    }
                )
            }

            if (promiseTimeDialog) {
                makeDialog(title = "입력 시간이 유효하지 않음",
                    text = "입력한 시간이 선택한 시간대에 포함되는지, 종료시각이 시작시각이 올바르게 입력됐는지 확인하세요.",
                    showDialog = 2 )
            }
            if(promiseTimeSelectDialog){
                makeDialog(title ="약속 시간이 선택되지 않음" , text = "멤버들이 만날 시간을 선택해 주세요", showDialog =1 )
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
    val context = LocalContext.current

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
                model = ContextCompat.getString(context, R.string.https) + user?.photoURL.toString().split(":")[1],
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
fun ConfirmPromiseSecondScreen(viewModel: MainViewModel, blocks: List<DateBlock>,elapsedTimeMillis:Long) :Boolean  {
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


        // 로딩 상태를 나타내는 변수
        var isLoading by remember { mutableStateOf(true) }

        // LaunchedEffect를 사용하여 로딩 스피너를 표시
        LaunchedEffect(isLoading) {
            // 로딩이 완료되면 isLoading 값을 false로 변경
            Log.d("chaeae","LaunchedEffect(isLoading : $elapsedTimeMillis")
            delay(elapsedTimeMillis+100)
            isLoading = false

        }

        // 로딩 중이면 로딩 스피너를 표시
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(80.dp)
                        .padding(16.dp),
                    color = Blue
                )
            }
        } else {
            // 로딩이 완료되면 각 카드를 만들기 위한 반복문 실행
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
    return true
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
fun ConfirmTimePickScreen(
    viewModel: MainViewModel,
    boundary: TimeBoundary,
    onTimeRangeSelected: (String, String) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 30.dp)
    ) {
        Text(
            "약속시간을 확정해볼까요?",
            modifier = Modifier.padding(bottom = 5.dp),
            style = TextStyle(fontSize = 20.sp),
            fontWeight = FontWeight.Bold
        )
        SelectedTimeScreen(viewModel = viewModel)
        Spacer(Modifier.height(10.dp))
        TimePickerScreen(viewModel, boundary, onTimeRangeSelected)
    }
}

@Composable
fun SelectedTimeScreen(viewModel: MainViewModel) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Text(
                text = "아까 선택한 시간대 안에서 입력해주세요.",
                modifier = Modifier.padding(bottom = 5.dp),
                fontSize = 15.sp
            )
        }
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .size(width = 240.dp, height = 50.dp)
                .padding(vertical = 4.dp)
        ) {
            Text(
                text = viewModel.selectedBlock.toString(),
                modifier = Modifier.padding(start = 10.dp, top = 10.dp, bottom = 10.dp),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
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
            "약속 시간에 맞춰 미리\n같이와치가 리마인드 해드릴게요!",
            textAlign = TextAlign.Center // 텍스트 중앙 정렬
        )
    }
}