package com.together_watch.together_watch.account

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.functions
import kotlinx.coroutines.tasks.await

suspend fun callQuitFunction() {
    val functions: FirebaseFunctions = Firebase.functions("asia-northeast3")
    val user = Firebase.auth.currentUser

    val data = mapOf("uid" to user?.uid)

    functions.getHttpsCallable("quit")
        .call(data)
        .addOnCompleteListener { task ->
            Log.d("quit", "탈퇴 함수 호출 성공")
        }.await()
}