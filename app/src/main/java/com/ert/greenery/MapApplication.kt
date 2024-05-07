package com.ert.greenery

import android.app.Application
import android.util.Log
import com.kakao.vectormap.KakaoMapSdk

class MapApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        KakaoMapSdk.init(this, "34241e07f7686ac80f7daa6ba4ddaac2")
        Log.d("test", "초기화됨")
    }
}