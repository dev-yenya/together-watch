package com.together_watch.together_watch.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.together_watch.together_watch.data.FetchedSchedule
import com.together_watch.together_watch.data.Schedule
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import com.together_watch.together_watch.notification.Notification

// Add these imports at the beginning of your file
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AlarmReceiver : BroadcastReceiver() {
    var myUid = Firebase.auth.currentUser?.uid.toString()
    var mySchedules = listOf<FetchedSchedule>()
    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    // 오늘 할 일
    override fun onReceive(context: Context?, intent: Intent?) {
        getTodayScheduleMessage { message ->
            makeAlarmNotification(context!!, "오늘 할 일", message)
        }
    }

    fun getTodayScheduleMessage(callback: (String) -> Unit) {
        var message = ""
        Firebase.firestore.collection("users")
            .document(myUid)
            .collection("schedules")
            .whereEqualTo("date", today)
            .get()
            .addOnSuccessListener { documents ->
                mySchedules = documents.map {
                    FetchedSchedule(
                        id = it.id,
                        schedule = Schedule(
                            name = it.get("name").toString(),
                            place = it.get("place").toString(),
                             date = it.get("date").toString(),
                            startTime = it.get("startTime").toString(),
                            endTime = it.get("endTime").toString(),
                            isGroup = it.get("isGroup").toString() == "true",
                        )
                    )
                }
                if (mySchedules.isEmpty()) {
                    message = "일정이 없어요.\n새로운 일정을 만들어 볼까요?"
                } else {
                    val scheduleMessages = mySchedules.map {
                        "${it.schedule.name} ${it.schedule.startTime} ~ ${it.schedule.endTime}"
                    }
                    message = "${scheduleMessages.joinToString("\n")}"
                }
                Log.d("today-noti", message)
                callback(message)
            }
    }
}

fun scheduleDailyAlarm(context: Context, hour: Int, minute: Int) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, AlarmReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

    val calendar = Calendar.getInstance().apply {
        timeInMillis = System.currentTimeMillis()
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0) // 초를 0으로 설정해야 정확한 시간에 알람이 울립니다.
        set(Calendar.MILLISECOND, 0)
    }

    // 이미 지난 시간이라면 현재 시간으로 설정
    if (calendar.timeInMillis <= System.currentTimeMillis()) {
        calendar.timeInMillis += AlarmManager.INTERVAL_DAY
    }

    // 정확한 시간에 알람을 실행하는 작업 예약
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    } else {
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }

}

fun makeAlarmNotification(context: Context, messageTitle: String, messageBody: String) {
    Notification().showNotification(
        context,
        messageTitle,
        messageBody
    )
}
