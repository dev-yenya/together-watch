package com.example.together_watch.schedule.create

import android.util.Log
import com.example.together_watch.ui.schedule.RepeatType
import com.example.together_watch.ui.schedule.Schedule
import com.example.together_watch.ui.schedule.toMap
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import java.time.LocalDate

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

    override fun saveRepeatSchedule(schedule: Schedule, repeatType: RepeatType, endDate: LocalDate) {
        var startDate = schedule.date
        val repetitionFunction: (LocalDate) -> LocalDate = when (repeatType) {
            RepeatType.WEEKLY -> { it -> it.plusDays(7) }
            RepeatType.MONTHLY -> { it -> it.plusMonths(1) }
        }

        while (!endDate.isBefore(startDate)) {
            startDate = repetitionFunction(startDate)
            val updatedSchedule = schedule.copy(date = startDate)
            saveSchedule(updatedSchedule)
        }
    }
}