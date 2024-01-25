package com.together_watch.together_watch.schedule.updateAndDelete

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.together_watch.together_watch.data.FetchedSchedule
import com.together_watch.together_watch.data.Schedule

class UpdateAndDeleteModel(
    private val selectedFetchedSchedule: FetchedSchedule,
    private val forceRefresh: () -> Unit,
    private val getClickedSchedule: () -> Unit
) : UpdateAndDeleteScheduleContract.Model {
    override fun getSchedule(): Schedule {
        return selectedFetchedSchedule.schedule
    }

    override fun getFetchedSchedule(): FetchedSchedule {
        return selectedFetchedSchedule
    }

    override fun deleteAndReturnIsDeleted() : Boolean{
        val scheduleId = selectedFetchedSchedule.id
        val userId = Firebase.auth.currentUser?.uid.toString()
        val db = Firebase.firestore
        val userRef = db.collection("users")
        var isDeleted = false

        userRef.document(userId)
            .collection("schedules")
            .document(scheduleId)
            .delete()
            .addOnSuccessListener {
                Log.d("schedule", "스케줄 삭제 성공 : ${scheduleId}" )
                isDeleted = true
                forceRefresh() // HomeScreen 함수 내의 triggerForceRefresh 호출
                getClickedSchedule() // EventsList 업데이트
            }
            .addOnFailureListener{
                Log.e("schedule", "스케줄 삭제 실패" )
            }

        return isDeleted
    }
}