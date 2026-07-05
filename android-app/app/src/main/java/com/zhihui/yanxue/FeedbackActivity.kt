package com.zhihui.yanxue

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog

/**
 * 意见反馈页面
 * 功能：多行文本输入框、字数统计、提交反馈
 * 交互：纯前端模拟，无需后端接口
 * 路由：/feedback/add
 */
class FeedbackActivity : BaseActivity() {

    private lateinit var etFeedback: EditText
    private lateinit var txtCharCount: TextView
    private lateinit var btnSubmit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)

        initViews()
        setupBackButton()
        setupEditText()
        setupSubmitButton()
    }

    /**
     * 初始化视图
     */
    private fun initViews() {
        etFeedback = findViewById(R.id.et_feedback)
        txtCharCount = findViewById(R.id.txt_char_count)
        btnSubmit = findViewById(R.id.btn_submit_feedback)
    }

    /**
     * 设置返回按钮
     */
    private fun setupBackButton() {
        findViewById<TextView>(R.id.txt_back_feedback).setOnClickListener {
            finish()
        }
    }

    /**
     * 设置输入框监听
     * 功能：实时统计输入字数，更新显示
     */
    private fun setupEditText() {
        etFeedback.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val count = s?.length ?: 0
                txtCharCount.text = "$count/500"
            }
        })
    }

    /**
     * 设置提交按钮点击事件
     * 交互逻辑：
     * 1. 输入为空 → 弹窗提示"请填写反馈内容后再提交"
     * 2. 输入不为空 → 弹窗提示"我们已经收到您的反馈，感谢您的建议！"
     * 3. 弹窗关闭后自动清空输入框
     */
    private fun setupSubmitButton() {
        btnSubmit.setOnClickListener {
            val content = etFeedback.text.toString().trim()

            if (content.isEmpty()) {
                // 输入为空，弹窗提示
                showEmptyContentDialog()
            } else {
                // 输入不为空，提交成功
                showSubmitSuccessDialog()
            }
        }
    }

    /**
     * 显示空内容提示弹窗
     * 场景：输入框为空时点击提交
     */
    private fun showEmptyContentDialog() {
        AlertDialog.Builder(this)
            .setTitle("提示")
            .setMessage("请填写反馈内容后再提交")
            .setPositiveButton("确定", null)
            .show()
    }

    /**
     * 显示提交成功弹窗
     * 场景：输入不为空时点击提交
     * 后续操作：弹窗关闭后自动清空输入框
     */
    private fun showSubmitSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("提交成功")
            .setMessage("我们已经收到您的反馈，感谢您的建议！")
            .setPositiveButton("确定") { _, _ ->
                // 弹窗确认关闭后，自动清空文本输入框
                etFeedback.setText("")
            }
            .show()
    }
}
