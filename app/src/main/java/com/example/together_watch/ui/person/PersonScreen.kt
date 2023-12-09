package com.example.together_watch.ui.person

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.together_watch.ui.Destinations
import com.example.together_watch.ui.theme.Black
import com.example.together_watch.ui.theme.Green
import com.example.together_watch.ui.theme.LightGreen


@Composable
fun PersonScreen(
    navController: NavController
) {
    var selectedButton by remember { mutableStateOf(1) } // 선택된 버튼 상태 관리

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
                isSelected = selectedButton == 1,
                onSelected = { selectedButton = 1 }
            )

            // 두 번째 버튼
            CustomButton(
                text = "초대 진행 중",
                isSelected = selectedButton == 2,
                onSelected = { selectedButton = 2 }
            )
        }
        Spacer(modifier = Modifier.height(25.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {

                    if(selectedButton==2) navController.navigate(Destinations.PromiseAcceptScreen.route)
                }
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
                Text(text = "Event Date and Time", style = MaterialTheme.typography.headlineSmall)
                Text(text = "Event Title: ", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Event Details", style = MaterialTheme.typography.bodySmall)
            }
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
