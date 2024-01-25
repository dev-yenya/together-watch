package com.together_watch.together_watch.notification

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class PushNotificationService() : FirebaseMessagingService(), Parcelable {

    constructor(parcel: Parcel) : this() {
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        updateFcmTokenAfterLogin()
    }
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val title = message.notification?.title
        val body = message.notification?.body
        Notification().showNotification(
            baseContext,
            title.toString(),
            body.toString()
        )
    }

    fun getToken(callback: (String) -> Unit){
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("fcm-token", "Fetching FCM registration token failed", task.exception)
                callback("")
                return@OnCompleteListener
            } else {
                val token = task.result
                Log.d("fcm-token", token)
                callback(token ?: "")  // 토큰이 null이면 빈 문자열 전달
            }
        })
            .addOnFailureListener { e ->
                Log.e("fcm-token", "Fetching FCM registration token failed", e)
                callback("")
            }
    }

    fun updateFcmTokenAfterLogin() {
        val userId = Firebase.auth.currentUser?.uid.toString()
        if (userId != null) {
            val tokenData = HashMap<String, Any>()
            getToken { token ->
                tokenData.put("fcmToken", token)
                Firebase.firestore.collection("users")
                    .document(userId)
                    .update(tokenData)
                    .addOnSuccessListener {
                        Log.d("fcm-token", "토큰 저장 완료")
                    }
                    .addOnFailureListener {e ->
                        Log.e("fcm-token", "토큰 저장 불가 ${e}")
                    }
            }
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PushNotificationService> {
        override fun createFromParcel(parcel: Parcel): PushNotificationService {
            return PushNotificationService(parcel)
        }

        override fun newArray(size: Int): Array<PushNotificationService?> {
            return arrayOfNulls(size)
        }
    }
}