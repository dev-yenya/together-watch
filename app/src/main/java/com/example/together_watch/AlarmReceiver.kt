package com.example.together_watch

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.constraintlayout.widget.Constraints
import androidx.core.app.NotificationCompat
import com.example.together_watch.data.FetchedPromise
import com.example.together_watch.data.FetchedSchedule
import com.example.together_watch.data.Promise
import com.example.together_watch.data.Schedule
import com.example.together_watch.data.Status
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat

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
    // notification 클릭 시 MainActivity 실행
    val intent = Intent(context, MainActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
    val pendingIntent = PendingIntent.getActivity(
        context, 0, intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // notification의 channel id를 정의
    val channelId = "channel1"
    val channelName = "channel name"

    // notification를 정의
    val notificationBuilder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.icon_notification)
        .setContentTitle(messageTitle)
        .setContentText(messageBody)
        .setStyle(NotificationCompat.BigTextStyle().bigText(messageBody))
        .setAutoCancel(true)   // 알람 클릭 시 삭림제
        .setSound(null)
        .setContentIntent(pendingIntent)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setOngoing(true)   // 알람이 계속 뜬 상태로 있게

    // 정의한 내용과 channel을 사용하여 notification을 생성
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // Android SDK 26 이상에서는 notification을 만들 때 channel을 지정
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)
    }

    // notification 띄우기
    notificationManager.notify(100, notificationBuilder.build())
}
