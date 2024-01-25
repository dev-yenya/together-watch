package com.together_watch.together_watch.login

import androidx.compose.runtime.Composable
import com.google.android.gms.tasks.Task
import com.google.firebase.functions.HttpsCallableResult

interface LoginContract {
    interface View {
        @Composable
        fun LoginButton()

        @Composable
        fun LoginScreen()
    }

    interface Model {
    }

    interface Presenter {
        fun callKakaoLoginFunction(accessToken: String, callback: (Boolean) -> Unit)
    }
}