package com.together_watch.together_watch
import LoginScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.together_watch.together_watch.ui.BottomBar
import com.together_watch.together_watch.ui.MainViewModel
import com.together_watch.together_watch.notification.scheduleDailyAlarm
import com.together_watch.together_watch.ui.NavigationGraph
import com.together_watch.together_watch.ui.theme.Together_watchTheme
import android.Manifest
import android.os.Build
import android.widget.Toast

class MainActivity : ComponentActivity() {
    companion object {
        const val DENIED = "denied"
        const val EXPLAINED = "explained"
    }

    private val registerForActivityResult = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        val deniedPermissionList = permissions.filter { !it.value }.map { it.key }
        when {
            deniedPermissionList.isNotEmpty() -> {
                val map = deniedPermissionList.groupBy { permission ->
                    if (shouldShowRequestPermissionRationale(permission)) DENIED else EXPLAINED
                }
                map[DENIED]?.let {
                    // 단순히 권한이 거부 되었을 때
                    Toast.makeText(applicationContext, "서비스의 필요한 권한입니다.\n권한에 동의해주세요.", Toast.LENGTH_SHORT).show()
                    finish()
                }
                map[EXPLAINED]?.let {
                    // 권한 요청이 완전히 막혔을 때(주로 앱 상세 창 열기)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        setContent {
            Together_watchTheme {
                //        val isLoggedIn by loginViewModel.isLoggedIn.observeAsState(false)
                if (true) { // isLoggedIn
                    MainScreen(mainViewModel) // 로그인 후 메인 화면
                } else {
                    LoginScreen() // 로그인 화면
                }
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerForActivityResult.launch(
                arrayOf(Manifest.permission.POST_NOTIFICATIONS)
            )
        }
        scheduleDailyAlarm(this, hour = 9, minute = 0)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val navController: NavHostController = rememberNavController()
    val buttonsVisible = remember { mutableStateOf(true) }

    Scaffold(
        bottomBar = {
            BottomBar(
                navController = navController,
                state = buttonsVisible,
                modifier = Modifier
            )
        }) { paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues)
        ) {
            NavigationGraph(navController = navController, viewModel)
        }
    }
}