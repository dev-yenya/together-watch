package com.example.together_watch.promise

import android.content.Context
import androidx.compose.runtime.Composable
import com.example.together_watch.ui.Destinations

interface PromiseAcceptContract {

    interface View {
        @Composable
        fun PromiseAcceptScreen()
        @Composable
        fun Buttons(context: Context)
    }

    interface Model {
        fun getGroupPromiseInfo(groupPromiseId: Long)
    }
}