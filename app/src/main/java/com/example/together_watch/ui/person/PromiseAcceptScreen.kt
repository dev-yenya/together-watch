package com.example.together_watch.ui.person

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.together_watch.ui.Destinations
@Composable
@RequiresApi(Build.VERSION_CODES.O)
fun PromiseAcceptScreen(){

}
@Composable
fun PromiseCompleteScreen() {
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 30.dp)
            .fillMaxWidth(), // Column을 가로로 채우도록 설정
        horizontalAlignment = Alignment.CenterHorizontally // 내용을 가로 중앙에 배치
    ) {
        Text(
            "약속 확정이 완료되었어요!",
            modifier = Modifier.padding(bottom = 5.dp),
            style = TextStyle(fontSize = 20.sp),
            textAlign = TextAlign.Start // 텍스트 중앙 정렬
        )
        Text(
            "이제 모두의 캘린더에 약속이 추가되었어요",
            modifier = Modifier.padding(bottom = 10.dp),
            style = TextStyle(fontSize = 15.sp),
            textAlign = TextAlign.Center // 텍스트 중앙 정렬
        )
        Spacer(Modifier.height(50.dp))
        Icon(
            Icons.Default.DateRange,
            contentDescription = null,
            modifier = Modifier.size(200.dp) // 아이콘 크기 조정
        )
        Spacer(Modifier.height(20.dp))
        Text(
            "약속 시간에 맞춰 미리\n톡캘린더로 리마인드 해드릴게요!",
            textAlign = TextAlign.Center // 텍스트 중앙 정렬
        )
    }
}