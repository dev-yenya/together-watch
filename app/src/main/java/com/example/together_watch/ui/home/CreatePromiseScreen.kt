package com.example.together_watch.ui.home

import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
//import androidx.compose.foundation.gestures.ModifierLocalScrollableContainerProvider.value
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.together_watch.promise.PromiseInfo
import com.example.together_watch.promise.shareInvitation
import com.example.together_watch.ui.MainViewModel
import com.example.together_watch.ui.theme.Black
import com.example.together_watch.ui.theme.Blue
import com.example.together_watch.ui.theme.DarkGray
import com.example.together_watch.ui.theme.Gray
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreatePromiseScreen(
    navController: NavHostController,
    viewModel: MainViewModel

) {
    val currentScreen = remember { mutableIntStateOf(1) }
    val nextScreen = { currentScreen.intValue++ }
    val previousScreen = { currentScreen.intValue-- }
    val promises = remember { mutableListOf<PromiseInfo>() }
    val savePromise = {
        viewModel.savePromise().whenComplete { result, _ ->
            promises.add(PromiseInfo(result.ownerId, result.docId))
        }
    }

    //var showDialog by remember { mutableStateOf(false) }
    var promiseNameDialog by remember { mutableStateOf(false) }
    var promisePlaceDialog by remember { mutableStateOf(false) }
    var promiseDateDialog by remember { mutableStateOf(false) }
    var promiseTimeDialog by remember { mutableStateOf(false) }
    val areValidTimes = { start: String, end: String -> viewModel.isValidTime(start, end) }
    val context = LocalContext.current
    val complete = {
        shareInvitation(context, promises[0])
    }

    val backHandler = {
        if (currentScreen.intValue > 1) {
            previousScreen()
        } else {
            navController.popBackStack()
            initViewModel("", "", emptyList(), "", "", viewModel)
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
                ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (currentScreen.intValue < 5) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = if (currentScreen.intValue == 1) {
                                "약속 이름"
                            } else if (currentScreen.intValue == 2) {
                                "장소"
                            } else if (currentScreen.intValue == 3) {
                                "날짜"
                            } else {
                                "시간"
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

                if (currentScreen.intValue < 5) {
                    LinearProgressIndicator(
                        progress = currentScreen.intValue * 0.25f,
                        modifier = Modifier.fillMaxWidth(),
                        color = Blue
                    )
                }

                when (currentScreen.intValue) {
                    1 -> FirstScreen(viewModel) { text ->
                        viewModel.promiseName = text
                    }

                    2 -> SecondScreen(viewModel) { text ->
                        viewModel.promisePlace = text
                    }

                    3 -> ThirdScreen(viewModel) { dates ->
                        viewModel.selectedDates = dates
                    }

                    4 -> TimePickScreen(viewModel) { text1, text2 ->
                        viewModel.startTime = text1
                        viewModel.endTime = text2
                    }

                    5 -> CompleteScreen()
                }
            }

            Button(
                onClick = if (currentScreen.intValue < 2) {
                    {
                        if (viewModel.promiseName == "") {
                            promiseNameDialog = true

                        } else {
                            initViewModel(viewModel.promiseName, "", emptyList(), "", "", viewModel)
                            nextScreen()
                        }

                    }
                } else if (currentScreen.intValue < 3) {
                    {
                        if (viewModel.promisePlace == "") {
                            promisePlaceDialog = true
                        } else {
                            initViewModel(
                                viewModel.promiseName,
                                viewModel.promisePlace,
                                emptyList(),
                                "",
                                "",
                                viewModel
                            )
                            nextScreen()
                        }
                    }
                } else if (currentScreen.intValue < 4) {
                    {
                        if (viewModel.selectedDates.isEmpty()) {
                            promiseDateDialog = true
                        } else {
                            initViewModel(
                                viewModel.promiseName,
                                viewModel.promisePlace,
                                viewModel.selectedDates,
                                "",
                                "",
                                viewModel
                            )
                            nextScreen()
                        }
                    }
                } else if (currentScreen.intValue < 5) {
                    {
                        if (areValidTimes(
                                viewModel.confirmedStartTime,
                                viewModel.confirmedEndTime
                            )
                        ) {
                            savePromise()
                            nextScreen()
                        } else {
                            promiseTimeDialog = true
                        }
                    }
                } else {
                    { complete() }
                },
                colors = ButtonDefaults.buttonColors(Blue),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RectangleShape
            ) {
                Text(
                    if (currentScreen.intValue < 5) "다음" else "친구 초대하기",
                    color = Black,
                    fontWeight = FontWeight.Bold
                )
            }

            @Composable
            fun makeDialog(title: String, text: String, showDialog: Int) {
                AlertDialog(
                    title = { Text(title) },
                    text = { Text(text) },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                when (showDialog) {
                                    1 -> promiseNameDialog = false
                                    2 -> promisePlaceDialog = false
                                    3 -> promiseDateDialog = false
                                    4 -> promiseTimeDialog = false
                                }
                            }
                        ) {
                            Text("확인")
                        }
                    },
                    onDismissRequest = {
                        when (showDialog) {
                            1 -> promiseNameDialog = false
                            2 -> promisePlaceDialog = false
                            3 -> promiseDateDialog = false
                            4 -> promiseTimeDialog = false
                        }
                    }
                )
            }
            if (promiseNameDialog) {
                makeDialog(title = "약속 이름이 유효하지 않음", text = "약속 이름을 올바르게 입력해주세요", showDialog = 1)
            }
            if (promisePlaceDialog) {
                makeDialog(title = "약속 장소가 유효하지 않음", text = "약속 장소를 올바르게 입력해 주세요", showDialog = 2)
            }
            if (promiseDateDialog) {
                makeDialog(title = "약속 날짜가 유효하지 않음", text = "약속 날짜를 선택해 주세요", showDialog = 3)
            }
            if (promiseTimeDialog) {
                makeDialog(title = "약속 시간이 유효하지 않음", text = "올바른 약속 시간을 입력해 주세요", showDialog = 4)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun FirstScreen(viewModel: MainViewModel, onNameChanged: (String) -> Unit) {
    var isInputValid by remember { mutableStateOf(true) }
    var text by remember { mutableStateOf(viewModel.promiseName) } // 사용자 입력을 저장하기 위한 상태
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 30.dp)
    ) {
        Text(
            "약속을 잡아볼까요?",
            modifier = Modifier.padding(bottom = 5.dp),
            style = TextStyle(fontSize = 20.sp),
            fontWeight = FontWeight.Bold
        )
        Text(
            "먼저, 약속 이름이 필요해요.",
            modifier = Modifier.padding(bottom = 10.dp),
            style = TextStyle(fontSize = 15.sp)
        )
        OutlinedTextField(
            value = text, // 텍스트 필드의 값
            onValueChange = { newText ->
                text = newText // 사용자가 입력한 새로운 텍스트로 업데이트
                isInputValid = newText.isNotBlank()
                onNameChanged(newText) // viewModel에 값 저장

            },
            singleLine = true,
            placeholder = { Text("12자 내의 이름을 입력해주세요") },

            colors = TextFieldDefaults.outlinedTextFieldColors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent,
            ),
        )
        Divider()
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecondScreen(viewModel: MainViewModel, onPlaceChanged: (String) -> Unit) {
    var text by remember { mutableStateOf(viewModel.promisePlace) } // 사용자 입력을 저장하기 위한 상태
    var isInputValid by remember { mutableStateOf(true) }
    val context = LocalContext.current
    Column(

        modifier = Modifier.padding(horizontal = 20.dp, vertical = 30.dp)
    ) {
        Text(
            "어디서 보나요?",
            modifier = Modifier.padding(bottom = 5.dp),
            style = TextStyle(fontSize = 20.sp),
            fontWeight = FontWeight.Bold
        )
        Text(
            "약속 장소를 잡아주세요",
            modifier = Modifier.padding(bottom = 10.dp),
            style = TextStyle(fontSize = 15.sp)
        )
        OutlinedTextField(

            value = text, // 텍스트 필드의 값
            onValueChange = { newText ->
                text = newText // 사용자가 입력한 새로운 텍스트로 업데이트
                isInputValid = newText.isNotBlank()
                onPlaceChanged(newText)

            },
            singleLine = true,
            placeholder = { Text("12자 내의 약속 장소를 입력해주세요") },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent,
            )
        )
        /*if (!isInputValid) {
            // 입력값이 비어 있을 때 토스트 메시지 표시
            Toast.makeText(context, "약속 장소를 입력해주세요", Toast.LENGTH_SHORT).show()
        }*/

        Divider()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ThirdScreen(viewModel: MainViewModel, onDateSelected: (List<String>) -> Unit) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedDates by remember { mutableStateOf(viewModel.selectedDates.toMutableList()) }
    var dates by remember { mutableStateOf(viewModel.selectedDates) }


    LazyColumn(modifier = Modifier.padding(10.dp)) {
        item {
            Spacer(Modifier.height(10.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "원하시는 날짜를 전부 선택해 주세요",
                style = TextStyle(fontSize = 20.sp),
                textAlign = TextAlign.Start
            )
            Spacer(Modifier.height(10.dp))
            CalendarHeader(selectedDate = selectedDate, onDateChanged = { newDate ->
                selectedDate = newDate
            })
            WeekDaysHeader()
        }

        items((0 until 6).toList()) { week ->
            val yearMonth = YearMonth.from(selectedDate)
            val totalDays = yearMonth.lengthOfMonth()
            val firstDayOfMonth = yearMonth.atDay(1)
            val daysOffset = firstDayOfMonth.dayOfWeek.value % 7
            var isSelectedEffect by remember { mutableStateOf(true) }
            var clickedDate by remember { mutableStateOf<LocalDate?>(null) }

            WeekRow(
                week,
                daysOffset,
                totalDays,
                selectedDates,
                clickedDate,
                yearMonth,
                isSelectedEffect = isSelectedEffect
            ) { date ->
                selectedDate = date
                val isDateSelectable =
                    date.isAfter(LocalDate.now()) || date.isEqual(LocalDate.now())
                if (isDateSelectable) {
                    isSelectedEffect = true
                    selectedDate = date
                    val selectedDate = selectedDates.find { it == date.toString() }
                    if (selectedDate == null) {
                        selectedDates.add(date.toString())
                        dates = selectedDates.toList() // 상태 업데이트
                        onDateSelected(dates) // 선택된 날짜를 viewModel으로 전달
                    } else {
                        selectedDates.remove(selectedDate)
                        dates = selectedDates.toList() // 상태 업데이트
                        onDateSelected(dates)
                    }
                } else {
                    isSelectedEffect = false
                }
            }
        }

        item {
            Divider()
            Spacer(Modifier.height(20.dp))
        }

        items(
            items = dates,
            itemContent = { EventsList(it) }
        )
    }
}


fun initViewModel(
    promiseName: String, promisePlace: String, selectedDates: List<String>,
    endTime: String, startTime: String, viewModel: MainViewModel
) {
    viewModel.promiseName = promiseName
    viewModel.promisePlace = promisePlace
    viewModel.selectedDates = selectedDates
    viewModel.endTime = endTime
    viewModel.startTime = startTime

}

@Composable
fun EventsList(date: String) {
    val date = LocalDate.parse(date)

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd (E)", Locale.KOREAN)
    val formattedDate = date.format(formatter)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 16.dp,
                top = 4.dp,
                end = 16.dp,
                bottom = 4.dp
            )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp), // 직접적으로 backgroundColor를 지정
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
                    text = formattedDate,
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkGray
                )
            }
        }
    }
}

fun MyTimePicker(
    context: Context,
    boundary: TimeBoundary?,
    result: (String) -> Unit
) {
    val calendar = Calendar.getInstance()
    TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            var roundedMinute = minute
            val selectedTime = LocalTime.of(hourOfDay, roundedMinute)
            // boundary가 설정되어 있지 않을 경우 처음 약속 생성하는 상황
            if (boundary == null) {
                // boundary가 null = 약속 생성 시점 -> 입력값은 반드시 30분 단위여야 함
                roundedMinute = (minute / 30) * 30
                result(String.format("%02d:%02d", hourOfDay, roundedMinute))
            } else if (isValidTime(selectedTime, boundary)) {
                // boundary가 null이 아님 = 약속 확정 시점
                result(String.format("%02d:%02d", hourOfDay, roundedMinute))
            } else {
                Toast.makeText(context, "잘못된 입력값입니다.", Toast.LENGTH_SHORT).show()
            }
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        false // 24시간 형식 사용 여부
    ).show()
}

fun isValidTime(time: LocalTime, boundary: TimeBoundary?): Boolean {
    return if (boundary != null) {
        (boundary.min <= time) || (boundary.max >= time)
    } else {
        true
    }
}

data class TimeBoundary(val min: LocalTime, val max: LocalTime)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickScreen(
    viewModel: MainViewModel,
    boundary: TimeBoundary? = null,
    onTimeRangeSelected: (String, String) -> Unit
) {

    var text1 by remember { mutableStateOf(viewModel.startTime) }
    var text2 by remember { mutableStateOf(viewModel.endTime) }
    val context = LocalContext.current

    Column(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 30.dp)
    ) {
        Text(
            "언제쯤 만날까요?",
            modifier = Modifier.padding(bottom = 5.dp),
            style = TextStyle(fontSize = 20.sp),
            fontWeight = FontWeight.Bold
        )
        if (boundary != null) {
            SelectedTimeScreen(viewModel = viewModel)
        } else {
            Text(
                "약속 가능한 시간대를 입력해 주세요.",
                modifier = Modifier.padding(bottom = 10.dp),
                style = TextStyle(fontSize = 15.sp)
            )
        }
        Spacer(Modifier.height(10.dp))
        Row {
            TextField(
                value = text1,
                onValueChange = { text1 = it },
                interactionSource = remember { MutableInteractionSource() }
                    .also { interactionSource ->
                        LaunchedEffect(interactionSource) {
                            interactionSource.interactions.collect {
                                if (it is PressInteraction.Release) {
                                    MyTimePicker(context, boundary) {
                                        text1 = it
                                        viewModel.confirmedStartTime = text1
                                        onTimeRangeSelected(text1, text2)
                                    }
                                }
                            }
                        }
                    },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp)
                    .border(1.dp, Color.Gray, RoundedCornerShape(5.dp)),
                textStyle = TextStyle(textAlign = TextAlign.Start),
                trailingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.White,
                    textColor = Color.Gray,
                    cursorColor = Color.Gray,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Gray
                ),
                shape = RoundedCornerShape(5.dp),
                label = { Text("시작 시간") },
                readOnly = true
            )

            TextField(
                value = text2,
                onValueChange = { text2 = it },
                interactionSource = remember { MutableInteractionSource() }
                    .also { interactionSource ->
                        LaunchedEffect(interactionSource) {
                            interactionSource.interactions.collect {
                                if (it is PressInteraction.Release) {
                                    MyTimePicker(context, boundary) {
                                        text2 = it
                                        viewModel.confirmedEndTime = text2
                                        onTimeRangeSelected(text1, text2)
                                    }
                                }
                            }
                        }
                    },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
                    .border(1.dp, Color.Gray, RoundedCornerShape(5.dp)),
                textStyle = TextStyle(textAlign = TextAlign.Start),
                trailingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.White,
                    textColor = Color.Gray,
                    cursorColor = Color.Gray,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Gray
                ),
                shape = RoundedCornerShape(5.dp),
                label = { Text("종료 시간") },
                readOnly = true
            )
        }
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
fun CompleteScreen() {
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 30.dp)
            .fillMaxWidth(), // Column을 가로로 채우도록 설정
        horizontalAlignment = Alignment.CenterHorizontally // 내용을 가로 중앙에 배치
    ) {
        Spacer(Modifier.height(30.dp))
        Text(
            "함께할 친구들을 초대해 볼까요?",
            modifier = Modifier
                .padding(bottom = 5.dp)
                .align(Alignment.Start),
            style = TextStyle(fontSize = 20.sp),
            fontWeight = FontWeight.Bold
        )
        Text(
            "초대장이 만들어졌어요",
            modifier = Modifier
                .padding(bottom = 10.dp)
                .align(Alignment.Start),
            style = TextStyle(fontSize = 15.sp),
        )
        Spacer(Modifier.height(100.dp))
        Icon(
            Icons.Default.Email,
            contentDescription = null,
            modifier = Modifier.size(150.dp) // 아이콘 크기 조정
        )
        Spacer(Modifier.height(30.dp))
        Text(
            "친구들이 초대를 수락하면\n모두가 만날 수 있는 시간을 골라드려요.",
            textAlign = TextAlign.Center // 텍스트 중앙 정렬
        )
    }
}