package com.example.together_watch.schedule.update

import android.util.Log
import com.example.together_watch.data.FetchedSchedule
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class UpdateScheduleModel(
    private val forceRefresh: () -> Unit,
    private val getClickedSchedule: () -> Unit
) : UpdateScheduleContract.Model {
    override fun updateSchedule(selectedSchedule: FetchedSchedule) {
        val scheduleId = selectedSchedule.id
        val userId = Firebase.auth.currentUser?.uid.toString()
        val userRef = Firebase.firestore.collection("users")
        var isUpdated = false

        userRef.document(userId)
            .collection("schedules")
            .document(scheduleId)
            .set(selectedSchedule.schedule)
            .addOnSuccessListener {
                Log.d("schedule", "스케줄 업데이트 성공 : ${scheduleId}" )
                isUpdated = true
                forceRefresh() // HomeScreen 함수 내의 triggerForceRefresh 호출
                getClickedSchedule()
            }
            .addOnFailureListener{
                Log.e("schedule", "스케줄 업데이트 실패" )
            }

//        return isUpdated
    }
}