package com.zhihui.yanxue

import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import com.zhihui.yanxue.R

class SearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setupHotTags()
        setupHistory()
        findViewById<TextView>(R.id.txt_back).setOnClickListener { finish() }
    }

    private fun setupHotTags() {
        val layout = findViewById<LinearLayout>(R.id.layout_hot_tags)
        val hotTags = listOf("周恩来", "平津战役", "觉悟社", "天津解放", "红色地标", "VR体验")
        val dp8 = (8 * resources.displayMetrics.density).toInt()
        for (tag in hotTags) {
            val tv = TextView(this).apply {
                text = tag
                textSize = 13f
                setTextColor(ContextCompat.getColor(this@SearchActivity, R.color.primary_red))
                background = ContextCompat.getDrawable(this@SearchActivity, R.drawable.bg_tag_pill)
                setPadding(dp8, dp8 / 2, dp8, dp8 / 2)
                gravity = Gravity.CENTER
            }
            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                rightMargin = dp8
                bottomMargin = dp8
            }
            tv.layoutParams = params
            layout.addView(tv)
        }
    }

    private fun setupHistory() {
        val layout = findViewById<LinearLayout>(R.id.layout_history)
        val history = listOf("周恩来纪念馆", "平津战役", "红色旅游")
        val dp8 = (8 * resources.displayMetrics.density).toInt()
        for (item in history) {
            val tv = TextView(this).apply {
                text = "🕒 " + item
                textSize = 14f
                setTextColor(ContextCompat.getColor(this@SearchActivity, R.color.text_primary))
                setPadding(0, dp8, 0, dp8)
            }
            layout.addView(tv)
        }
    }
}
