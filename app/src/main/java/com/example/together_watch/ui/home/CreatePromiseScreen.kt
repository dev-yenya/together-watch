package com.example.together_watch.ui.home

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.SemanticsProperties.ImeAction
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

// 약속 생성

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreatePromiseScreen(
    navController: NavHostController,

    viewModel:MainViewModel

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
    val context = LocalContext.current
    val complete = {
        shareInvitation(context, promises[0])
    }

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
                    4 -> FourthScreen(viewModel) {text1, text2 ->
                        viewModel.startTime = text1
                        viewModel.endTime = text2
                    }
                    5 -> CompleteScreen()

                }
            }

            Button(
                onClick = if (currentScreen.intValue < 4 ) {
                    { nextScreen() }
                } else if (currentScreen.intValue < 5) { {
                    savePromise()
                    nextScreen()
                } } else {

                    { complete() }
                },
                colors = ButtonDefaults.buttonColors(Blue),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RectangleShape
            ) {
                Text(if (currentScreen.intValue < 5) "다음" else "친구 초대하기", color = Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun FirstScreen(viewModel: MainViewModel, onNameChanged: (String) -> Unit) {
    var isInputValid by remember { mutableStateOf(true) }
    var text by remember { mutableStateOf("") } // 사용자 입력을 저장하기 위한 상태
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
                isInputValid=newText.isNotBlank()
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
    var text by remember { mutableStateOf("") } // 사용자 입력을 저장하기 위한 상태
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
                isInputValid=newText.isNotBlank()
                onPlaceChanged(newText)

            },
            singleLine = true,
            placeholder = { Text("12자 내의 약속 장소를 입력해주세요") },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent,
            )
        )
        if (!isInputValid) {
            // 입력값이 비어 있을 때 토스트 메시지 표시
            Toast.makeText(context, "약속 장소를 입력해주세요", Toast.LENGTH_SHORT).show()
        }

        Divider()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ThirdScreen(viewModel: MainViewModel, onDateSelected: (List<String>) -> Unit) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val selectedDates = mutableListOf<String>()
    var dates by remember { mutableStateOf(listOf<String>()) }


    LazyColumn(modifier = Modifier.padding(10.dp)) {
        item {
            Spacer(Modifier.height(10.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "원하시는 날짜를 전부 선택해 주세요",
                style = TextStyle(fontSize = 20.sp),
                textAlign = TextAlign.Center
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
            var isSelectedEffect by remember { mutableStateOf(false) }
            var clickedDate by remember { mutableStateOf<LocalDate?>(null) }

            WeekRow(week, daysOffset, totalDays, clickedDate, yearMonth, isSelectedEffect=isSelectedEffect) { date ->
                selectedDate = date
                selectedDate = date
                val isDateSelectable = date.isAfter(LocalDate.now()) || date.isEqual(LocalDate.now())
                if (isDateSelectable) {
                    isSelectedEffect=true
                    selectedDate = date
                    if (!selectedDates.contains(date.toString())) {
                        selectedDates.add(date.toString())
                        dates = selectedDates.toList() // 상태 업데이트
                        onDateSelected(dates) // 선택된 날짜를 viewModel으로 전달
                    }
                }
                else isSelectedEffect=false


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
                Text(text = formattedDate,
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkGray
                )
            }
        }
    }
}

fun MyTimePicker(context: Context, result: ((String) -> Unit)) {
    val calendar = Calendar.getInstance()
    TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            // 선택된 시간을 "HH:mm" 형식으로 저장합니다.
            result(String.format("%02d:%02d", hourOfDay, minute))
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true // 24시간 형식 사용 여부
    ).show()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FourthScreen(viewModel: MainViewModel, onTimeRangeSelected: (String, String) -> Unit) {

    var text1 by remember { mutableStateOf("") }
    var text2 by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 30.dp)
    ) {
        Text(
            "언제 만날까요?",
            modifier = Modifier.padding(bottom = 5.dp),
            style = TextStyle(fontSize = 20.sp),
            fontWeight = FontWeight.Bold
        )
        Text(
            "약속 시간대를 입력해 주세요.",
            modifier = Modifier.padding(bottom = 10.dp),
            style = TextStyle(fontSize = 15.sp)
        )
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
                                    MyTimePicker(context) {
                                        text1 = it
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
                                    MyTimePicker(context) {
                                        text2 = it
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
fun CompleteScreen() {
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 30.dp)
            .fillMaxWidth(), // Column을 가로로 채우도록 설정
        horizontalAlignment = Alignment.CenterHorizontally // 내용을 가로 중앙에 배치
    ) {
        Text(
            "함께할 친구들을 초대해 볼까요?",
            modifier = Modifier.padding(bottom = 5.dp),
            style = TextStyle(fontSize = 20.sp),
            textAlign = TextAlign.Start // 텍스트 중앙 정렬
        )
        Text(
            "초대장이 만들어졌어요",
            modifier = Modifier.padding(bottom = 10.dp),
            style = TextStyle(fontSize = 15.sp),
            textAlign = TextAlign.Center // 텍스트 중앙 정렬
        )
        Spacer(Modifier.height(30.dp))
        Icon(
            Icons.Default.Send,
            contentDescription = null,
            modifier = Modifier.size(200.dp) // 아이콘 크기 조정
        )
        Spacer(Modifier.height(20.dp))
        Text(
            "친구들이 참여하면\n모두가 참여할 수 있는 시간을 골라드려요.",
            textAlign = TextAlign.Center // 텍스트 중앙 정렬
        )
    }
}