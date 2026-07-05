package com.zhihui.yanxue

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.zhihui.yanxue.data.MockCourseRepository
import com.zhihui.yanxue.data.model.Course

/**
 * 课程详情页 — 显示除视频外的所有课程信息
 */
class CourseDetailActivity : AppCompatActivity() {

    private var course: Course? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_detail)

        val courseId = intent.getStringExtra("course_id") ?: return finish()
        course = MockCourseRepository.getCourseById(courseId) ?: return finish()

        setupToolbar()
        displayCourseInfo()
    }

    private fun setupToolbar() {
        val btnBack = findViewById<View>(R.id.btn_back)
        btnBack?.setOnClickListener { finish() }
    }

    private fun displayCourseInfo() {
        val c = course ?: return

        // 封面图
        val ivCover = findViewById<android.widget.ImageView>(R.id.iv_detail_cover)
        val resId = resources.getIdentifier(c.imagePath, "drawable", packageName)
        if (resId != 0) {
            ivCover.setImageResource(resId)
        }

        // 标题
        findViewById<android.widget.TextView>(R.id.tv_detail_title).text = c.title

        // 讲师（根据分类自动生成）
        val instructor = when (c.category) {
            "地标" -> "纪念馆资深讲解员"
            "人物" -> "党史研究专家"
            "文物" -> "文物博物馆研究员"
            "事件" -> "历史系教授"
            else -> "红色教育讲师"
        }
        findViewById<android.widget.TextView>(R.id.tv_detail_instructor).text = "讲师：$instructor"

        // 学时
        findViewById<android.widget.TextView>(R.id.tv_detail_duration).text = "${c.duration}分钟"

        // 学习人数
        findViewById<android.widget.TextView>(R.id.tv_detail_students).text = "${c.studentCount}人学习"

        // 评分
        findViewById<android.widget.TextView>(R.id.tv_detail_rating).text = "★ ${c.rating}"

        // 适合年龄段
        findViewById<android.widget.TextView>(R.id.tv_detail_age_group).text = c.ageGroup

        // 免费/付费标签
        val priceTag = findViewById<android.widget.TextView>(R.id.tv_detail_price_tag)
        if (c.isFree) {
            priceTag.text = "免费"
            priceTag.visibility = View.VISIBLE
        } else {
            priceTag.text = "¥${c.price.toInt()}"
            priceTag.visibility = View.VISIBLE
        }

        // 标签
        val tagsLayout = findViewById<android.view.ViewGroup>(R.id.layout_detail_tags)
        tagsLayout.removeAllViews()
        c.tags.forEach { tag ->
            val tagView = android.widget.TextView(this).apply {
                text = tag
                textSize = 11f
                setTextColor(ContextCompat.getColor(this@CourseDetailActivity, R.color.primary_red))
                background = ContextCompat.getDrawable(this@CourseDetailActivity, R.drawable.bg_tag_pill)
                setPadding(12, 4, 12, 4)
                val params = android.view.ViewGroup.MarginLayoutParams(
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 0, 8, 0)
                layoutParams = params
            }
            tagsLayout.addView(tagView)
        }

        // 课程描述
        findViewById<android.widget.TextView>(R.id.tv_detail_description).text = c.description
    }
}
