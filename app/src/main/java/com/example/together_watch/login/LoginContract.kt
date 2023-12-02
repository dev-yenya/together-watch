package com.example.together_watch.login

import androidx.compose.runtime.Composable
import com.google.android.gms.tasks.Task
import com.google.firebase.functions.HttpsCallableResult

interface LoginContract {
    interface View {
        @Composable
        fun ShowLoginButton()
        @Composable
        fun ShowHomeButton()
    }

    interface Model {
    }

    interface Presenter {
        fun callKakaoLoginFunction(accessToken: String, callback: (Boolean) -> Unit)
    }
}