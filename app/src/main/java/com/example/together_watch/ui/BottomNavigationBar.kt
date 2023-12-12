package com.example.together_watch.ui


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.together_watch.ui.home.CompleteScreen
import com.example.together_watch.ui.home.CreatePromiseScreen
import com.example.together_watch.ui.home.HomeScreen
import com.example.together_watch.ui.person.PersonScreen
import com.example.together_watch.ui.person.PromiseAcceptScreen
import com.example.together_watch.ui.setting.AccountManagementScreen
import com.example.together_watch.ui.setting.SettingScreen
import com.example.together_watch.ui.theme.Black
import com.example.together_watch.ui.theme.Blue
import com.example.together_watch.ui.theme.DarkGray
import com.example.together_watch.ui.theme.Gray


sealed class Destinations(
    val route: String,
    val icon: ImageVector? = null
) {
    object HomeScreen : Destinations(
        route = "home_screen",
        icon = Icons.Outlined.Home
    )

    object PersonScreen : Destinations(
        route = "person_screen",
        icon = Icons.Outlined.Person
    )

    object SettingScreen : Destinations(
        route = "setting_screen",
        icon = Icons.Outlined.Settings
    )
    object AccountManagementScreen : Destinations(
        route = "account_management_screen"
    )
    object CompleteScreen : Destinations(
        route = "complete_screen"
    )
    object CreatePromiseScreen : Destinations(
        route = "create_promise_screen"
    )
    object PromiseAcceptScreen : Destinations(
        route = "promise_accept_screen"
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationGraph(navController: NavHostController, viewModel: MainViewModel) {
    NavHost(navController, startDestination = Destinations.HomeScreen.route) {
        composable(Destinations.HomeScreen.route) {
            HomeScreen(navController, viewModel)
        }
        composable(Destinations.PersonScreen.route) {
            PersonScreen(navController, viewModel)
        }
        composable(Destinations.SettingScreen.route) {
            SettingScreen(navController)
        }
        composable(Destinations.AccountManagementScreen.route) {
            AccountManagementScreen()
        }
        composable(Destinations.CompleteScreen.route) {
            CompleteScreen()
        }
        composable(Destinations.CreatePromiseScreen.route) {
            CreatePromiseScreen(navController, viewModel)
        }
        composable(Destinations.PromiseAcceptScreen.route) {
            PromiseAcceptScreen(navController ,viewModel)
        }
    }
}

@Composable
fun BottomBar(
    navController: NavHostController, state: MutableState<Boolean>, modifier: Modifier = Modifier
) {
    val screens = listOf(
        Destinations.HomeScreen, Destinations.PersonScreen, Destinations.SettingScreen
    )

    NavigationBar(
        modifier = modifier,
        containerColor = Color.White,
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        screens.forEach { screen ->

            NavigationBarItem(
                icon = {
                    Icon(imageVector = screen.icon!!, contentDescription = "")
                },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = false
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    unselectedIconColor = DarkGray,
                    selectedIconColor = Black,
                    indicatorColor = Gray
                ),
            )
        }
    }
}