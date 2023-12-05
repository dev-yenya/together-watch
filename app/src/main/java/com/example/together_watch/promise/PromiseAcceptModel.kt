package com.example.together_watch.promise

import android.util.Log
import com.example.together_watch.data.Promise
import com.example.together_watch.data.Status
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class PromiseAcceptModel : PromiseAcceptContract.Model {

    val db = Firebase.firestore

    override fun getGroupPromiseInfo(ownerId: String, groupId: String): Promise {
        val promiseRef = db.collection("users").document(ownerId)
            .collection("promises").document(groupId)
        var promise = mutableMapOf<String, Any?>()
        promiseRef.get().addOnSuccessListener { document ->
            if (document != null) {
                promise = document.data as MutableMap<String, Any?>
                Log.d("group-info", "그룹 정보 가져오기 성공")
            } else {
                Log.d("group-info", "관련 그룹 정보를 찾아올 수 없습니다.")
            }
        }
        return Promise(
            promise["name"] as String,
            promise["ownerId"] as String,
            promise["users"] as List<String>,
            promise["status"] as Status,
            promise["dates"] as List<String>,
            promise["startTime"] as String,
            promise["endDate"] as String,
            promise["place"] as String
        )
    }
}