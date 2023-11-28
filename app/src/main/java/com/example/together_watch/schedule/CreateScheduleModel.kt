package com.example.together_watch.schedule

import android.util.Log
import com.example.together_watch.ui.schedule.Schedule
import com.example.together_watch.ui.schedule.toMap
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class CreateScheduleModel : CreateScheduleContract.Model {
    override fun saveSchedule(schedule: Schedule) {
        val user = Firebase.auth.currentUser
        val db = Firebase.firestore
        val userRef = db.collection("users")

        userRef.document(user?.uid.toString())
            .collection("schedules")
            .add(schedule.toMap())

        Log.d("personal schedule", "스케줄 업로드 성공 : ${schedule.name}")
    }
}