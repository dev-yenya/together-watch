package com.example.together_watch.schedule.create

import android.util.Log
import com.example.together_watch.data.RepeatType
import com.example.together_watch.data.Schedule
import com.example.together_watch.data.toMap
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import java.time.LocalDate
import java.time.format.DateTimeFormatter




class CreateScheduleModel(
    private val forceRefresh: () -> Unit
) : CreateScheduleContract.Model {
    override fun saveSchedule(schedule: Schedule) {
        val userId = Firebase.auth.currentUser?.uid.toString()
        val userRef = Firebase.firestore.collection("users")

        userRef.document(userId)
            .collection("schedules")
            .add(schedule.toMap())
            .addOnSuccessListener {
                Log.d("personal schedule", "스케줄 업로드 성공 : ${schedule.name}" )
                forceRefresh()
            }
            .addOnFailureListener { Log.e("personal schedule", "스케줄 업로드 실패") }
    }

    override fun saveRepeatSchedule(schedule: Schedule, repeatType: RepeatType, endDate: LocalDate) {
        var startDate = LocalDate.parse(schedule.date, DateTimeFormatter.ISO_LOCAL_DATE)
        val repetitionFunction: (LocalDate) -> LocalDate = when (repeatType) {
            RepeatType.WEEKLY -> { it -> it.plusDays(7) }
            RepeatType.MONTHLY -> { it -> it.plusMonths(1) }
        }

        while (!endDate.isBefore(startDate)) {
            val updatedSchedule = schedule.copy(date = startDate.toString())
            saveSchedule(updatedSchedule)
            startDate = repetitionFunction(startDate)
        }
    }
}