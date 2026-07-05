package com.zhihui.yanxue

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity

/**
 * 基础Activity，所有Activity继承此类
 * 功能：全局字号调整（通过fontScale实现）
 */
open class BaseActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context?) {
        if (newBase == null) {
            super.attachBaseContext(null)
            return
        }

        val prefs = newBase.getSharedPreferences("settings", MODE_PRIVATE)
        val fontSize = prefs.getInt("font_size", 0)

        val scale = when (fontSize) {
            0 -> 1.0f
            1 -> 1.2f
            2 -> 1.4f
            else -> 1.0f
        }

        if (scale == 1.0f) {
            super.attachBaseContext(newBase)
            return
        }

        // 应用字号缩放
        val config = Configuration(newBase.resources.configuration)
        config.fontScale = scale
        val context = newBase.createConfigurationContext(config)
        super.attachBaseContext(context)
    }
}
