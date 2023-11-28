package com.example.together_watch.promise

import androidx.compose.runtime.Composable
import com.example.together_watch.ui.Destinations

interface PromiseAcceptContract {

    interface View {
        @Composable
        fun PromiseAcceptScreen()
        @Composable
        fun Buttons()
    }

    interface Model {
        fun getGroupPromiseInfo(groupPromiseId: Long)
    }
}