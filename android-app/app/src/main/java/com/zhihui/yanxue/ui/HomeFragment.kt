package com.zhihui.yanxue.ui

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.zhihui.yanxue.R
import kotlin.math.roundToInt

class HomeFragment : Fragment() {

    private var _binding: com.zhihui.yanxue.databinding.FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = com.zhihui.yanxue.databinding.FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCategories()
        setupRecommendedCourses()
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).roundToInt()
    }

    private fun setupCategories() {
        val grid = binding.gridCategories
        val categories = listOf(
            Triple("红色地标", "🏛️", R.drawable.bg_category_landmark),
            Triple("英雄人物", "⭐", R.drawable.bg_category_person),
            Triple("革命文物", "🏺", R.drawable.bg_category_relic),
            Triple("历史事件", "📜", R.drawable.bg_category_event)
        )

        val dp4 = dpToPx(4)
        val dp8 = dpToPx(8)
        val dp12 = dpToPx(12)
        val dp16 = dpToPx(16)

        for ((index, triple) in categories.withIndex()) {
            val (name, icon, bgRes) = triple

            val card = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                background = ContextCompat.getDrawable(requireContext(), bgRes)
                setPadding(dp12, dp16, dp12, dp16)
            }

            val params = GridLayout.LayoutParams().apply {
                columnSpec = GridLayout.spec(index % 2, 1f)
                rowSpec = GridLayout.spec(index / 2, 1f)
                width = 0
                height = GridLayout.LayoutParams.WRAP_CONTENT
                bottomMargin = dp8
                leftMargin = if (index % 2 == 0) 0 else dp4
                rightMargin = if (index % 2 == 0) dp4 else 0
            }
            card.layoutParams = params

            val tvIcon = TextView(requireContext()).apply {
                text = icon
                textSize = 24f
            }
            card.addView(tvIcon)

            val tvName = TextView(requireContext()).apply {
                text = name
                textSize = 13f
                setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                setPadding(0, dp8, 0, 0)
            }
            card.addView(tvName)

            grid.addView(card)
        }
    }

    private fun setupRecommendedCourses() {
        val layoutCourses = binding.layoutCourses
        val courses = listOf(
            CourseItem("周恩来精神与天津", "走进周邓纪念馆，感悟伟人品格与革命情怀", "地标·慕课", "1.2万人已学"),
            CourseItem("平津战役全纪实", "从战略部署到胜利会师，全景式回顾平津战役", "事件·微课", "9800人已学"),
            CourseItem("觉悟社里的青春", "探寻周恩来、马骏等青年志士的革命初心", "人物·直播", "7500人已学"),
            CourseItem("天津解放的那一天", "1949年1月15日，天津迎来新生", "事件·VR", "6300人已学")
        )

        val dp8 = dpToPx(8)
        val dp12 = dpToPx(12)
        val dp16 = dpToPx(16)
        val dp64 = dpToPx(56)

        for (course in courses) {
            val card = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_card_white)
                setPadding(dp12, dp12, dp12, dp12)
            }
            val cardParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp8
            }
            card.layoutParams = cardParams

            // Thumbnail placeholder
            val thumb = View(requireContext()).apply {
                setBackgroundResource(R.drawable.bg_category_landmark)
            }
            val thumbParams = LinearLayout.LayoutParams(dp64, dp64).apply {
                rightMargin = dp12
            }
            thumb.layoutParams = thumbParams
            card.addView(thumb)

            // Text content
            val textLayout = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER_VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val tvTitle = TextView(requireContext()).apply {
                text = course.title
                textSize = 15f
                setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary))
                setTypeface(null, android.graphics.Typeface.BOLD)
            }
            textLayout.addView(tvTitle)

            val tvDesc = TextView(requireContext()).apply {
                text = course.desc
                textSize = 12f
                setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary))
                setPadding(0, dp4, 0, 0)
                maxLines = 2
            }
            textLayout.addView(tvDesc)

            val tvTag = TextView(requireContext()).apply {
                text = course.tag
                textSize = 11f
                setTextColor(ContextCompat.getColor(requireContext(), R.color.primary_red))
                setPadding(0, dp4, 0, 0)
            }
            textLayout.addView(tvTag)

            card.addView(textLayout)

            // Study count
            val tvCount = TextView(requireContext()).apply {
                text = course.count
                textSize = 11f
                setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary))
            }
            val countParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                leftMargin = dp8
                gravity = Gravity.CENTER_VERTICAL
            }
            tvCount.layoutParams = countParams
            card.addView(tvCount)

            layoutCourses.addView(card)
        }
    }

    private data class CourseItem(val title: String, val desc: String, val tag: String, val count: String)

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
