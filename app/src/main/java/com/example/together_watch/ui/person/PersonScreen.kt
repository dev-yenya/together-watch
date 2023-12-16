package com.example.together_watch.ui.person

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.together_watch.data.Status
import com.example.together_watch.ui.Destinations
import com.example.together_watch.ui.MainViewModel
import com.example.together_watch.ui.theme.Black
import com.example.together_watch.ui.theme.Green
import com.example.together_watch.ui.theme.LightGreen


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PersonScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    val apiPromiseData by viewModel.apiPromiseData.observeAsState()
    var selectedButton by remember { mutableStateOf(Status.COMPLETED) } // 선택된 버튼 상태 관리
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchPromisesData() // 화면이 처음 그려질 때 API 호출
    }

    apiPromiseData?.let {
        val filteredPromises = (apiPromiseData?.filter {
            it.promise.status == selectedButton && it.promise.ownerId == viewModel.myUid
        } ?: emptyList()) as MutableList

        Log.e("apiPromiseData", it.toString())
        // 전체 패딩 설정
        Column(modifier = Modifier.padding(horizontal = 25.dp, vertical = 30.dp)) {
            // 상단 텍스트
            Text(
                text = "내가 만든 약속들은\n이런 것들이 있어요.",
                fontSize = 22.sp,
                lineHeight = 30.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(20.dp))

            // 버튼을 위한 Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 첫 번째 버튼
                CustomButton(
                    text = "확정",
                    isSelected = selectedButton == Status.COMPLETED,
                    onSelected = { selectedButton = Status.COMPLETED }
                )

                // 두 번째 버튼
                CustomButton(
                    text = "초대 진행 중",
                    isSelected = selectedButton == Status.ONPROGRESS,
                    onSelected = { selectedButton = Status.ONPROGRESS }
                )
            }
            Spacer(modifier = Modifier.height(25.dp))

            LazyColumn {
                items(items = filteredPromises) { fetchedPromise ->
                    // 여기에 각 약속에 대한 UI를 구성합니다.
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .combinedClickable(
                                onClick = {
                                    if (selectedButton == Status.ONPROGRESS) {
                                        viewModel.selectedPromise = fetchedPromise
                                        navController.navigate(Destinations.ConfirmPromiseScreen.route)
                                    }
                                },
                                onLongClick = {
                                    if (selectedButton == Status.COMPLETED) {
                                        viewModel.selectedPromise = fetchedPromise
                                        showDialog = true
                                    }
                                }
                            ),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)

                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = if (fetchedPromise.promise.status == Status.ONPROGRESS) "약속시간은 모두가 괜찮은 시간대로 정해볼게요."
                                        else "${fetchedPromise.promise.dates?.joinToString(" ")} ${fetchedPromise.promise.startTime} ~ ${fetchedPromise.promise.endTime}",
                                style = MaterialTheme.typography.bodyMedium
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
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = {
                    showDialog = false
                },
                title = { Text("삭제 확인") },
                text = { Text("이 항목을 삭제하시겠습니까?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDialog = false
                            // 삭제 처리 로직
                            viewModel.deletePromise(viewModel.selectedPromise) {
                                viewModel.fetchPromisesData()
                            }
                        }
                    ) {
                        Text("예")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDialog = false
                        }
                    ) {
                        Text("아니요")
                    }
                }
            )
        }
    }
}

@Composable
fun CustomButton(text: String, isSelected: Boolean, onSelected: () -> Unit) {
    Button(
        onClick = onSelected,
        shape = RoundedCornerShape(30),
        colors = ButtonDefaults.buttonColors(containerColor = if (isSelected) Green else LightGray),
        border = if (isSelected) BorderStroke(2.dp, Green) else BorderStroke(1.dp, LightGray),
        modifier = Modifier.padding(horizontal = 1.dp, vertical = 2.dp)
    ) {
        Icon(Icons.Filled.CheckCircle, contentDescription = null)
        Spacer(Modifier.width(5.dp))
        Text(text = text)
    }
}
