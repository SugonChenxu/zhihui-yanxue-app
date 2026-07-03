package com.zhihui.yanxue

import android.app.Application

class ZhiHuiYanXueApp : Application() {

    override fun onCreate() {
        super.onCreate()
        // 初始化应用
        instance = this
    }

    companion object {
        lateinit var instance: ZhiHuiYanXueApp
            private set
    }
}