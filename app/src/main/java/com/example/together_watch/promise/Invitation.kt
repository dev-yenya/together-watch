package com.example.together_watch.promise

import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import com.kakao.sdk.share.ShareClient

fun shareInvitation(context: Context) {
    val templateId: Long = 101298
    if (ShareClient.instance.isKakaoTalkSharingAvailable(context)) {
        ShareClient.instance.shareCustom(
            context = context,
            templateId = templateId,
            // TODO: group-id 전달
            // templateArgs = mutableMapOf("group-id" to groupId.toString())
        ) { sharingResult, error ->
            if (error != null) {
                Log.e("kakao-share-api", "카카오톡 공유 실패", error)
            }
            else if (sharingResult != null) {
                Log.d("kakao-share-api", "카카오톡 공유 성공 ${sharingResult.intent}")
                startActivity(context, sharingResult.intent, null)

                Log.w("kakao-share-api", "Warning Msg: ${sharingResult.warningMsg}")
                Log.w("kakao-share-api", "Argument Msg: ${sharingResult.argumentMsg}")
            }
        }
    } else {
        // 마켓플레이스로 이동
    }
}