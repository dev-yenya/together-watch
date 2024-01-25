package com.together_watch.together_watch

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import com.together_watch.together_watch.BuildConfig.KAKAO_API_KEY_STRING

class GlobalApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        // KaKao SDK  초기화
        KakaoSdk.init(this, KAKAO_API_KEY_STRING)
    }
}