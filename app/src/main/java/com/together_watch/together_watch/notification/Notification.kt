package com.together_watch.together_watch.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.together_watch.together_watch.MainActivity
import com.together_watch.together_watch.R
import java.security.MessageDigest
import java.util.UUID

class Notification {
    // notification의 channel id를 정의
    private val channelId = "HEADS_UP_NOTIFICATIONS"
    private val channelName = "TOGETHER_WATCH"

    fun showNotification(context: Context, messageTitle: String, messageBody: String) {
        // notification 클릭 시 MainActivity 실행
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        // notification를 정의
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.icon_notification)
            .setContentTitle(messageTitle)
            .setContentText(messageBody)
            .setStyle(NotificationCompat.BigTextStyle().bigText(messageBody))
            .setAutoCancel(true)   // 알람 클릭 시 삭제
            .setSound(null)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)   // 알람이 계속 뜬 상태로 있게

        // 고유한 알림 ID 생성
        val notificationId = randomUuidInt()

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
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    fun randomUuidInt(): Int {
        // UUID 생성
        val uuid = UUID.randomUUID()

        // UUID를 MD5 해시로 변환
        val md5Hash = MessageDigest.getInstance("MD5").digest(uuid.toString().toByteArray())

        // MD5 해시를 int로 변환
        var result = 0
        for (i in 0 until 4) {
            result = result shl 8 or (md5Hash[i].toInt() and 0xFF)
        }

        return result
    }
}