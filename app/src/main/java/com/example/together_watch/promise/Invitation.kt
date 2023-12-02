package com.example.together_watch.promise

import android.content.ActivityNotFoundException
import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import com.kakao.sdk.common.util.KakaoCustomTabsClient
import com.kakao.sdk.share.ShareClient
import com.kakao.sdk.share.WebSharerClient

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
        val sharerUrl = WebSharerClient.instance.makeCustomUrl(templateId)

        try {
            KakaoCustomTabsClient.openWithDefault(context, sharerUrl)
        } catch(e: UnsupportedOperationException) {
            Log.d("kakao-share-api", "CustomTabsServiceConnection 지원 브라우저가 설치되어 있지 않습니다. 미지원 브라우저를 탐색합니다.")
        }

        try {
            KakaoCustomTabsClient.open(context, sharerUrl)
        } catch (e: ActivityNotFoundException) {
            Log.d("kakao-share-api", "지원브라우저가 없습니다.")
        }
    }
}