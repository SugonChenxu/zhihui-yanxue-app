package com.zhihui.yanxue

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.zhihui.yanxue.R
import com.zhihui.yanxue.R.color

class MessageActivity : BaseActivity() {

    private var currentMsgTab = "push"
    private lateinit var layoutMsgList: LinearLayout
    private lateinit var viewTabIndicator: View
    private lateinit var tabPush: TextView
    private lateinit var tabNotify: TextView
    private lateinit var tabChat: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        layoutMsgList = findViewById(R.id.layout_msg_list)
        viewTabIndicator = findViewById(R.id.view_tab_indicator)
        tabPush = findViewById(R.id.tab_push)
        tabNotify = findViewById(R.id.tab_notify)
        tabChat = findViewById(R.id.tab_chat)
        setupBackButton()
        setupMsgTabs()
        loadMessages()
    }

    private fun setupBackButton() {
        findViewById<TextView>(R.id.txt_back_msg).setOnClickListener { finish() }
    }

    private fun setupMsgTabs() {
        tabPush.setOnClickListener { selectMsgTab("push") }
        tabNotify.setOnClickListener { selectMsgTab("notify") }
        tabChat.setOnClickListener { selectMsgTab("chat") }
        updateIndicatorPosition()
    }

    private fun selectMsgTab(tab: String) {
        currentMsgTab = tab
        val selectedColor = getColor(R.color.primary_red)
        val normalColor = getColor(R.color.text_secondary)
        tabPush.setTextColor(if (tab == "push") selectedColor else normalColor)
        tabPush.setTypeface(null, if (tab == "push") android.graphics.Typeface.BOLD else android.graphics.Typeface.NORMAL)
        tabNotify.setTextColor(if (tab == "notify") selectedColor else normalColor)
        tabNotify.setTypeface(null, if (tab == "notify") android.graphics.Typeface.BOLD else android.graphics.Typeface.NORMAL)
        tabChat.setTextColor(if (tab == "chat") selectedColor else normalColor)
        tabChat.setTypeface(null, if (tab == "chat") android.graphics.Typeface.BOLD else android.graphics.Typeface.NORMAL)
        updateIndicatorPosition()
        loadMessages()
    }

    private fun updateIndicatorPosition() {
        val tabWidth = resources.displayMetrics.widthPixels / 3
        val indicatorWidth = dpToPx(60)
        val offset = (tabWidth - indicatorWidth) / 2
        val leftMargin = when (currentMsgTab) {
            "push" -> offset
            "notify" -> tabWidth + offset
            "chat" -> 2 * tabWidth + offset
            else -> offset
        }
        val params = viewTabIndicator.layoutParams as? ViewGroup.MarginLayoutParams
        params?.leftMargin = leftMargin
        viewTabIndicator.layoutParams = params
    }

    private fun loadMessages() {
        layoutMsgList.removeAllViews()
        val messages = getMockMessages(currentMsgTab)
        val dp12 = dpToPx(12)
        val dp8 = dpToPx(8)
        val dp4 = dpToPx(4)
        for (msg in messages) {
            val card = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                background = ContextCompat.getDrawable(this@MessageActivity, R.drawable.bg_card_white)
                setPadding(dp12, dp12, dp12, dp12)
                gravity = Gravity.CENTER_VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { bottomMargin = dp8 }
            }
            // 图标
            val icon = TextView(this).apply {
                text = msg.icon
                textSize = 22f
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(dpToPx(40), dpToPx(40)).apply { rightMargin = dp12 }
            }
            card.addView(icon)
            // 文字区域
            val textLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }
            val titleRow = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
            val title = TextView(this).apply {
                text = msg.title
                textSize = 14f
                setTextColor(getColor(R.color.text_primary))
                setTypeface(null, android.graphics.Typeface.BOLD)
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }
            titleRow.addView(title)
            if (!msg.isRead) {
                val dot = View(this).apply {
                    background = ContextCompat.getDrawable(this@MessageActivity, R.drawable.bg_red_dot)
                    layoutParams = LinearLayout.LayoutParams(dpToPx(8), dpToPx(8))
                }
                titleRow.addView(dot)
            }
            textLayout.addView(titleRow)
            val content = TextView(this).apply {
                text = msg.content
                textSize = 12f
                setTextColor(getColor(R.color.text_secondary))
                setPadding(0, dp4, 0, 0)
                maxLines = 1
            }
            textLayout.addView(content)
            val time = TextView(this).apply {
                text = msg.time
                textSize = 11f
                setTextColor(getColor(R.color.text_hint))
                setPadding(0, dp4, 0, 0)
            }
            textLayout.addView(time)
            card.addView(textLayout)
            // 箭头
            val arrow = TextView(this).apply {
                text = ">"
                textSize = 16f
                setTextColor(getColor(R.color.text_hint))
                layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply { leftMargin = dp8 }
            }
            card.addView(arrow)
            layoutMsgList.addView(card)
        }
    }

    private fun getMockMessages(type: String): List<MessageItem> {
        return when (type) {
            "push" -> listOf(
                MessageItem("📢", "新课程的推荐", "平津战役专题微课已上线，快去学习吧！", "10:30", false),
                MessageItem("🎉", "签到提醒", "你已经连续签到7天，继续坚持！", "昨天", false),
                MessageItem("📚", "课程更新", "周恩来精神与天津课程新增第三章内容", "前天", true),
                MessageItem("🏆", "学习成就", "恭喜你完成「觉悟社」专题学习", "06-28", true),
            )
            "notify" -> listOf(
                MessageItem("🔔", "系统通知", "您的账号已成功绑定手机号", "06-30", false),
                MessageItem("❤️", "互动通知", "李老师点赞了您的课程笔记", "06-29", false),
                MessageItem("⭐", "收藏提醒", "您收藏的「天津红色地标地图」已更新", "06-27", true),
            )
            "chat" -> listOf(
                MessageItem("👤", "研学小助手", "同学你好！有什么可以帮你的吗？", "刚刚", false),
                MessageItem("👥", "学习小组", "张同学邀请你加入「平津战役研讨小组」", "昨天", false),
            )
            else -> emptyList()
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density + 0.5f).toInt()
    }

    data class MessageItem(
        val icon: String,
        val title: String,
        val content: String,
        val time: String,
        val isRead: Boolean
    )
}
