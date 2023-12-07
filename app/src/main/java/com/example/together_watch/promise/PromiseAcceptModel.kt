package com.example.together_watch.promise

import android.util.Log
import com.example.together_watch.data.Promise
import com.example.together_watch.data.Status
import com.google.firebase.Firebase
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

class PromiseAcceptModel : PromiseAcceptContract.Model {

    private val db = Firebase.firestore
    private lateinit var promiseId: String

    override suspend fun getGroupPromise(ownerId: String, groupId: String): Promise = coroutineScope {

        val promiseRef = db.collection("users").document(ownerId)
            .collection("promises").document(groupId)
        Log.d("kakao-share-api", "그룹 정보 조회 쿼리 사용")
        var promise = mapOf<String, Any?>()
        promiseRef.get().addOnCompleteListener{ task ->
            if (task.isSuccessful) {
                promise = task.result.data as Map<String, Any?>
                Log.d("group-info", "그룹 정보 가져오기 성공")
            } else {
                Log.d("group-info", "관련 그룹 정보를 찾아올 수 없습니다.")
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

    fun addPromiseMember(memberId: String?, ownerId: String) {
        val promiseRef = db.collection("users").document(ownerId)
            .collection("promises").document(promiseId)
        val result = promiseRef.get().result.data as Map<String, Any?>
        val users = result["users"] as MutableList<String>
        if (memberId != null)
            users.add(memberId)
        promiseRef.set(result, SetOptions.merge())
        Log.d("kakao-share-api", "그룹 정보 조회 쿼리 사용")
    }
}