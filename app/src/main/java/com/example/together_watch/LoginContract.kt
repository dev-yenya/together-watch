package com.example.together_watch

import androidx.compose.runtime.Composable

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
        fun loginButtonClick()
        fun homeButtonClick()
    }
}