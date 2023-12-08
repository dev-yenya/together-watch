package com.example.together_watch.promise

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat.startActivity
import com.example.together_watch.MainActivity
import com.example.together_watch.data.Promise
import com.example.together_watch.data.Status
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

class PromiseAcceptModel : PromiseAcceptContract.Model {

    private val db = Firebase.firestore
    private lateinit var promiseId: String
    private lateinit var promiseRef: DocumentReference

    override suspend fun getGroupPromise(ownerId: String, groupId: String): Promise =
        coroutineScope {

            promiseRef = db.collection("users").document(ownerId)
                .collection("promises").document(groupId)
            Log.d("invitation", "그룹 정보 조회 쿼리 사용")
            var promise = mapOf<String, Any?>()
            promiseRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    promise = task.result.data as Map<String, Any?>
                    Log.d("invitation", "그룹 정보 가져오기 성공")
                } else {
                    Log.d("invitation", "관련 그룹 정보를 찾아올 수 없습니다.")
                }
            }.await()
            promiseId = groupId
            Promise(
                promise["name"] as String,
                promise["ownerId"] as String,
                promise["users"] as List<String>,
                Status.valueOf(promise["status"] as String),
                promise["dates"] as List<String>,
                promise["startTime"] as String,
                promise["endTime"] as String,
                promise["place"] as String
            )
        }

    fun addPromiseMember(memberId: String?): Boolean {
        var value = false
        promiseRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("invitation", "그룹 정보 가져오기 성공")
                val users = task.result.data?.get("users") as List<String>
                if (! users.contains(memberId)) {
                    promiseRef.update("users", FieldValue.arrayUnion(memberId))
                    value = true
                }
            }
        }.addOnFailureListener {
            Log.d("invitation", "그룹정보 가져오기 실패")
        }

        return value;
    }
}