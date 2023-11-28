package com.example.together_watch.ui.home

import BottomSheetCreateDialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.together_watch.ui.Destinations
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    navController: NavController
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var events by remember { mutableStateOf(listOf<String>()) }
    var showAddEvent by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            CalendarHeader(selectedDate = selectedDate, onDateChanged = { newDate ->
                selectedDate = newDate
            })
            CalendarGrid(selectedDate = selectedDate, onDateSelected = { date ->
                selectedDate = date
                // 날짜에 맞는 이벤트를 로드
                events = listOf("Event 1", "Event 2") // 예시
            })
            EventsList(events)
        }
        FloatingActionButton(
            onClick = { showAddEvent = !showAddEvent },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(if (showAddEvent) Icons.Filled.Close else Icons.Filled.Add, "Toggle Buttons")
        }

        // 추가 버튼들 표시
        AnimatedVisibility(
            visible = showAddEvent,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 82.dp, end = 26.dp)
        ) {
            Column(horizontalAlignment = Alignment.End) {
                ButtonRow("약속 일정 추가", onClick = { navController.navigate(Destinations.CreatePromiseScreen.route) })
                ButtonRow("개인 일정 추가", onClick = {
                    val context = navController.context
                    val bottomSheetCreateDialog = BottomSheetCreateDialog(context)
                    bottomSheetCreateDialog.showBottomSheet()
                })
            }
        }
    }
}

@Composable
fun ButtonRow(text: String, onClick: () -> Unit) {
    Spacer(modifier = Modifier.height(10.dp))
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = text,
            modifier = Modifier.padding(end = 8.dp)
        )
        Button(
            onClick = onClick,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(Icons.Filled.ArrowForward, contentDescription = text)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarHeader(selectedDate: LocalDate, onDateChanged: (LocalDate) -> Unit) {
    // 월과 년도 표시
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {
            // 이전 달로 이동
            onDateChanged(selectedDate.minusMonths(1))
        }) {
            Icon(Icons.Filled.ArrowBack, "Previous Month")
        }
        Text(text = selectedDate.format(DateTimeFormatter.ofPattern("MMMM yyyy")))
        IconButton(onClick = {
            // 다음 달로 이동
            onDateChanged(selectedDate.plusMonths(1))
        }) {
            Icon(Icons.Filled.ArrowForward, "Next Month")
        }
    }
}

@Composable
fun EventsList(events: List<String>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        events.forEach { event ->
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
                    Text(text = "Event Date and Time", style = MaterialTheme.typography.headlineMedium)
                    Text(text = "Event Title: ", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Event Details", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarGrid(selectedDate: LocalDate, onDateSelected: (LocalDate) -> Unit) {
    val yearMonth = YearMonth.from(selectedDate)
    val totalDays = yearMonth.lengthOfMonth()
    val firstDayOfMonth = yearMonth.atDay(1)
    val daysOffset = firstDayOfMonth.dayOfWeek.value % 7

    Column {
        // 요일 헤더
        WeekDaysHeader()

        LazyColumn {
            items((0 until 6).toList()) { week ->
                WeekRow(week, daysOffset, totalDays, selectedDate, yearMonth, onDateSelected)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeekDaysHeader() {
    LazyRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        items(DayOfWeek.values()) { dayOfWeek ->
            Text(
                text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                modifier = Modifier
                    .padding(8.dp)
                    .width(40.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeekRow(
    week: Int,
    daysOffset: Int,
    totalDays: Int,
    selectedDate: LocalDate,
    yearMonth: YearMonth,
    onDateSelected: (LocalDate) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly // 균일한 간격으로 날짜 배치
    ) {
        // 주간 날짜를 표시하는 루프
        (0 until 7).forEach { dayOfWeek ->
            val dayOfMonth = week * 7 + dayOfWeek - daysOffset + 1
            if (dayOfMonth in 1..totalDays) {
                // 유효한 날짜일 경우 날짜 표시
                DateBox(dayOfMonth, yearMonth, onDateSelected)
            } else {
                // 유효하지 않은 날짜일 경우 빈 공간 표시
                EmptyBox()
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateBox(
    dayOfMonth: Int,
    yearMonth: YearMonth,
    onDateSelected: (LocalDate) -> Unit
) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .size(40.dp), // 박스 크기 지정
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = dayOfMonth.toString(),
            modifier = Modifier.clickable { onDateSelected(yearMonth.atDay(dayOfMonth)) }
        )
    }
}

@Composable
fun EmptyBox() {
    Spacer(
        modifier = Modifier
            .padding(8.dp)
            .size(40.dp)
    ) // 빈 박스 크기 지정
}