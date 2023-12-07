package com.example.together_watch.ui.setting

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.together_watch.R
import com.example.together_watch.ui.Destinations
import com.example.together_watch.ui.theme.DarkGray
import com.google.firebase.Firebase
import com.google.firebase.auth.auth


@Composable
fun SettingScreen(
    navController: NavHostController
) {
    val user = Firebase.auth.currentUser
    val photoUrl = user?.photoUrl ?: ""
    val name = user?.displayName ?: ""

    Column(modifier = Modifier.padding(24.dp)) {
        Spacer(modifier = Modifier.height(20.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            // 프로필 이미지
            AsyncImage(
                model = photoUrl,
                contentDescription = "프로필 이미지",
                modifier = Modifier
                    .size(50.dp)
                    .border(2.dp, DarkGray, shape = CircleShape)
                    .clip(CircleShape),
            )
            Spacer(modifier = Modifier.width(15.dp))

            // 이름과 이메일
            Column {
                Text(text = name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Divider(color = DarkGray, modifier = Modifier.fillMaxWidth().width(2.dp))

        Spacer(modifier = Modifier.height(30.dp))
        Text(text = "서비스 설정", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(20.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "계정 관리",
                fontSize = 18.sp,
                color = Color.Black,
                modifier = Modifier.clickable {
                    navController.navigate(Destinations.AccountManagementScreen.route)
                }
            )
            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "화살표")
        }
    }
}

