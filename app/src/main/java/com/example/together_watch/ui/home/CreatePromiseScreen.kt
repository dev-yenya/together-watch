package com.example.together_watch.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.*
import com.example.together_watch.R
import com.example.together_watch.ui.Destinations
import java.time.LocalDate


// 약속 생성

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreatePromiseScreen() {
    val currentScreen = remember { mutableIntStateOf(1) }
    val nextScreen = { currentScreen.intValue++ }
    val complete = { /* 완료 액션 구현 */ }

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
<<<<<<< HEAD
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
                            textAlign = TextAlign.Center
                        )
                        IconButton(
                            onClick = { /* 뒤로가기 기능 구현 */ },
                            modifier = Modifier.align(Alignment.CenterStart)
                        ) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = null)
                        }
                    }
                }

                if (currentScreen.intValue < 5) {
                    LinearProgressIndicator(
                        progress = currentScreen.intValue * 0.25f,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                when (currentScreen.intValue) {
                    1 -> FirstScreen()
                    2 -> SecondScreen()
                    3 -> ThirdScreen()
                    4 -> FourthScreen()
                    5 -> CompleteScreen()
                }
            }

            Button(
                onClick = if (currentScreen.intValue < 5) {
                    { nextScreen() }
                } else {
                    { complete() }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RectangleShape
            ) {
                Text(if (currentScreen.intValue < 5) "다음" else "친구 초대하기")
=======
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    IconButton(
                        onClick = { /* 뒤로가기 기능 구현 */ },
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = null)
                    }
                }

            }

            Button(
                onClick ={},    //버튼 눌렀을 때 화면 바뀌도록 구현
                modifier = Modifier.fillMaxWidth(),
                shape = RectangleShape
            ) {
                Text( "다음")
>>>>>>> cbf7b786bcab743add0b973df15e1ecc939a074e
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FirstScreen() {
    var text by remember { mutableStateOf("") } // 사용자 입력을 저장하기 위한 상태

    Column(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 30.dp)
    ) {
        Text("약속을 잡아볼까요?", modifier = Modifier.padding(bottom = 5.dp), style = TextStyle(fontSize = 20.sp))
        Text("먼저, 약속 이름이 필요해요.", modifier = Modifier.padding(bottom = 10.dp), style = TextStyle(fontSize = 15.sp))
        OutlinedTextField(
            value = text, // 텍스트 필드의 값
            onValueChange = { newText ->
                text = newText // 사용자가 입력한 새로운 텍스트로 업데이트
            },
            placeholder = { Text("12자 내의 이름을 입력해주세요") },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent,
            )
        )
        Divider()
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecondScreen() {
    var text by remember { mutableStateOf("") } // 사용자 입력을 저장하기 위한 상태

    Column(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 30.dp)
    ) {
        Text("어디서 보나요?", modifier = Modifier.padding(bottom = 5.dp), style = TextStyle(fontSize = 20.sp))
        Text("약속 장소를 잡아주세요", modifier = Modifier.padding(bottom = 10.dp), style = TextStyle(fontSize = 15.sp))
        OutlinedTextField(
            value = text, // 텍스트 필드의 값
            onValueChange = { newText ->
                text = newText // 사용자가 입력한 새로운 텍스트로 업데이트
            },
            placeholder = { Text("12자 내의 약속 장소를 입력해주세요") },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent,
            )
        )
        Divider()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ThirdScreen() {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    Column {
        Spacer(Modifier.height(10.dp))
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "원하시는 날짜를 전부 선택해 주세요",
            style = TextStyle(fontSize = 20.sp),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(10.dp))
<<<<<<< HEAD
        CalendarHeader(selectedDate = selectedDate, onDateChanged = { newDate ->
            selectedDate = newDate
        })
        CalendarGrid(selectedDate = selectedDate, onDateSelected = { date ->
            selectedDate = date
        })
=======
>>>>>>> cbf7b786bcab743add0b973df15e1ecc939a074e
        Divider()
        Spacer(Modifier.height(20.dp))
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "아직 선택된 날짜가 없어요",
            style = TextStyle(fontSize = 20.sp),
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FourthScreen() {
    var text1 by remember { mutableStateOf("시작시간") }
    var text2 by remember { mutableStateOf("종료시간") }

    Column(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 30.dp)
    ) {
        Text("언제 만날까요?", modifier = Modifier.padding(bottom = 5.dp), style = TextStyle(fontSize = 20.sp))
        Text("약속 시간대를 입력해 주세요.", modifier = Modifier.padding(bottom = 10.dp), style = TextStyle(fontSize = 15.sp))
        Spacer(Modifier.height(10.dp))
<<<<<<< HEAD
        Row {
            TextField(
                value = text1,
                onValueChange = { text1 = it },
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
                shape = RoundedCornerShape(5.dp)
            )

            TextField(
                value = text2,
                onValueChange = { text2 = it },
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
                shape = RoundedCornerShape(5.dp)
            )
        }
=======
        //시간 선택 기능 구현 필요
>>>>>>> cbf7b786bcab743add0b973df15e1ecc939a074e
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