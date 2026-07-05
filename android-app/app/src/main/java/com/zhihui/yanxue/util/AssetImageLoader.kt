package com.zhihui.yanxue.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.IOException

/**
 * 从 assets 目录加载图片的工具类
 */
object AssetImageLoader {

    /**
     * 从 assets 加载 Bitmap
     * @param context 上下文
     * @param path assets 内相对路径，如 "images/product_totebag.png"
     * @return Bitmap，加载失败返回 null
     */
    fun loadBitmap(context: Context, path: String): Bitmap? {
        return try {
            val inputStream = context.assets.open(path)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
            bitmap
        } catch (e: IOException) {
            null
        }
    }
}
