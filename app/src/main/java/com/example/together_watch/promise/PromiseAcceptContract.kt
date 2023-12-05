package com.example.together_watch.promise

import android.content.Context
import androidx.compose.runtime.Composable
import com.example.together_watch.data.Promise

interface PromiseAcceptContract {

    interface View {
        @Composable
        fun PromiseAcceptScreen()
        @Composable
        fun Buttons(context: Context)
    }

    interface Model {
        fun getGroupPromiseInfo(ownerId: String, groupId: String): Promise
    }
}