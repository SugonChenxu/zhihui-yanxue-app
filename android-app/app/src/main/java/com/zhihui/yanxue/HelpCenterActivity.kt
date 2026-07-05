package com.zhihui.yanxue

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView

/**
 * 帮助中心页面
 * 功能：静态展示联系方式，支持长按复制手机号
 * 交互：纯前端模拟，无后端接口
 * 路由：/help/faq
 */
class HelpCenterActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_center)

        setupBackButton()
        setupPhoneCopy()
    }

    /**
     * 设置返回按钮
     */
    private fun setupBackButton() {
        findViewById<TextView>(R.id.txt_back_help).setOnClickListener {
            finish()
        }
    }

    /**
     * 设置手机号长按复制功能
     * 功能：长按手机号文本，复制到剪贴板
     */
    private fun setupPhoneCopy() {
        val phone1 = findViewById<TextView>(R.id.txt_phone1)
        val phone2 = findViewById<TextView>(R.id.txt_phone2)
        val phone3 = findViewById<TextView>(R.id.txt_phone3)

        // 设置长按复制功能
        setupLongPressCopy(phone1, "13111111111")
        setupLongPressCopy(phone2, "13111112222")
        setupLongPressCopy(phone3, "13111113333")
    }

    /**
     * 设置长按复制功能
     * @param textView 文本视图
     * @param phoneNumber 手机号
     */
    private fun setupLongPressCopy(textView: TextView, phoneNumber: String) {
        textView.setOnLongClickListener {
            // 复制手机号到剪贴板
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip = android.content.ClipData.newPlainText("手机号", phoneNumber)
            clipboard.setPrimaryClip(clip)

            // 弹窗提示已复制
            android.widget.Toast.makeText(this, "已复制手机号：$phoneNumber", android.widget.Toast.LENGTH_SHORT).show()
            true
        }
    }
}
