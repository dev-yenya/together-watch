package com.together_watch.together_watch.promise

import android.content.ActivityNotFoundException
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import com.together_watch.together_watch.BuildConfig
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.util.KakaoCustomTabsClient
import com.kakao.sdk.share.ShareClient
import com.kakao.sdk.share.WebSharerClient

data class PromiseInfo(
    val ownerId: String,
    val docId: String
)

fun shareInvitation(context: Context, group: PromiseInfo) {
    val templateId: Long = 101298
    val argumentsMap = mapOf("ownerId" to group.ownerId, "groupId" to group.docId)
    if (ShareClient.instance.isKakaoTalkSharingAvailable(context)) {
        ShareClient.instance.shareCustom(
            context = context,
            templateId = templateId,
            templateArgs = argumentsMap
        ) { sharingResult, error ->
            if (error != null) {
                Log.e("invitation", "카카오톡 공유 실패", error)
            }
            else if (sharingResult != null) {
                Log.d("invitation", "카카오톡 공유 성공 ${sharingResult.intent.data?.query}")
                Log.d("invitation", argumentsMap.toString())
                startActivity(context, sharingResult.intent, null)

                Log.w("invitation", "Warninag Msg: ${sharingResult.warningMsg}")
                Log.w("invitation", "Argument Msg: ${sharingResult.argumentMsg}")
            }
        }
    } else {
        val sharerUrl = WebSharerClient.instance.makeCustomUrl(templateId = templateId, templateArgs = argumentsMap)

        try {
            KakaoCustomTabsClient.openWithDefault(context, sharerUrl)
        } catch(e: UnsupportedOperationException) {
            Log.d("invitation", "CustomTabsServiceConnection 지원 브라우저가 설치되어 있지 않습니다. 미지원 브라우저를 탐색합니다.")
        }

        try {
            KakaoCustomTabsClient.open(context, sharerUrl)
        } catch (e: ActivityNotFoundException) {
            Log.d("invitation", "지원 브라우저가 없습니다.")
        }
    }
}