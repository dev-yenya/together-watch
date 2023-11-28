import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.together_watch.R
import com.example.together_watch.ui.MainViewModel

// 로그인 화면
@Composable
fun LoginScreen(
    viewModel: MainViewModel = MainViewModel()
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = Modifier.padding(start = 20.dp, top = 50.dp),
            text = "바쁜 일정도, 약속도\n더이상 고민하지 마세요.",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Start
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(id = R.drawable.ic_launcher_background), // 중간 이미지 (로고)
                contentDescription = "Logo"
            )
        }

        Button(
            modifier = Modifier
                .padding(bottom = 50.dp, start = 20.dp, end = 20.dp)
                .wrapContentWidth(Alignment.CenterHorizontally)
                .fillMaxWidth()
                .height(50.dp),
            onClick = { /* 카카오톡 로그인 처리 */ },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow)
        ) {
            Text("카카오톡 로그인", color = Color.Black)
        }
    }
}

@Preview
@Composable
fun PreviewLoginScreen() {
    LoginScreen()
}