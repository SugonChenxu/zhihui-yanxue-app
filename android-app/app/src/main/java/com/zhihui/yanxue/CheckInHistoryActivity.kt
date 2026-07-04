package com.zhihui.yanxue

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.zhihui.yanxue.data.CheckInRepository
import com.zhihui.yanxue.data.CheckInRecord
import com.zhihui.yanxue.databinding.ActivityCheckinHistoryBinding
import java.util.*

/**
 * 签到历史记录页
 * 功能：分页展示历史记录、按月筛选、倒序排列（最新在前）
 * 排序保障：所有记录渲染前均调用 sortRecordsDesc()，根治乱序
 */
class CheckInHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckinHistoryBinding
    private var filterYear: Int = 0  // 0 表示全部
    private var filterMonth: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckinHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupTopBar()
        setupMonthFilter()
        updateStats()
        loadHistory()
    }

    private fun setupTopBar() {
        binding.txtBackHistory.setOnClickListener { finish() }
    }

    private fun setupMonthFilter() {
        binding.txtFilterMonth.setOnClickListener {
            showMonthPicker()
        }
    }

    private fun showMonthPicker() {
        // 简易月份选择：弹出本月/上月/全部
        val options = arrayOf("全部", "本月", "上月")
        android.app.AlertDialog.Builder(this)
            .setTitle("选择筛选月份")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        filterYear = 0
                        filterMonth = 0
                        binding.txtFilterMonth.text = "全部"
                    }
                    1 -> {
                        val (y, m) = CheckInRepository.getCurrentYearMonth()
                        filterYear = y
                        filterMonth = m
                        binding.txtFilterMonth.text = "${y}年${m}月"
                    }
                    2 -> {
                        val cal = Calendar.getInstance()
                        cal.add(Calendar.MONTH, -1)
                        filterYear = cal.get(Calendar.YEAR)
                        filterMonth = cal.get(Calendar.MONTH) + 1
                        binding.txtFilterMonth.text = "${filterYear}年${filterMonth}月"
                    }
                }
                updateStats()
                loadHistory()
            }
            .show()
    }

    private fun updateStats() {
        val allRecords = CheckInRepository.getHistoryList(this)
        val filtered = if (filterYear == 0) allRecords else
            allRecords.filter { it.date.startsWith(String.format("%04d-%02d", filterYear, filterMonth)) }

        val totalDays = filtered.size
        val totalPoints = filtered.sumOf { it.points }
        val maxContinuous = calculateMaxContinuous(filtered)

        binding.txtTotalDays.text = totalDays.toString()
        binding.txtTotalPointsHistory.text = totalPoints.toString()
        binding.txtContinuousHistory.text = maxContinuous.toString()
    }

    private fun calculateMaxContinuous(records: List<CheckInRecord>): Int {
        if (records.isEmpty()) return 0
        var max = 0
        var current = 0
        val sorted = records.sortedBy { it.date }
        for (i in sorted.indices) {
            if (i == 0) {
                current = 1
            } else {
                val prevDate = CheckInRepository.parseDate(sorted[i - 1].date)
                val currDate = CheckInRepository.parseDate(sorted[i].date)
                // 安全处理 nullable Date
                if (prevDate != null && currDate != null) {
                    val diff = ((currDate.time - prevDate.time) / (1000 * 60 * 60 * 24)).toInt()
                    if (diff == 1) {
                        current++
                    } else {
                        max = maxOf(max, current)
                        current = 1
                    }
                } else {
                    max = maxOf(max, current)
                    current = 1
                }
            }
        }
        return maxOf(max, current)
    }

    /**
     * 加载历史记录列表
     * 排序保障：调用 CheckInRepository.sortRecordsDesc() 后再渲染
     * 根治列表乱序、月份切换数据混乱问题
     */
    private fun loadHistory() {
        binding.layoutHistoryList.removeAllViews()

        val records = if (filterYear == 0) {
            CheckInRepository.getHistoryList(this)
        } else {
            CheckInRepository.getHistoryByMonth(this, filterYear, filterMonth)
        }

        // 强制倒序（最新在前），双保险
        val sorted = CheckInRepository.sortRecordsDesc(records)

        if (sorted.isEmpty()) {
            showEmptyState()
            return
        }

        val dp12 = dpToPx(12)
        val dp8 = dpToPx(8)
        val dp4 = dpToPx(4)

        for (record in sorted) {
            val card = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                background = getDrawable(R.drawable.bg_card_white)
                setPadding(dp12, dp12, dp12, dp12)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { bottomMargin = dp8 }
            }

            // 日期 + 积分行
            val row1 = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }

            val dateTv = TextView(this).apply {
                text = record.dateDisplay
                textSize = 14f
                setTextColor(getColor(R.color.text_primary))
                setTypeface(null, android.graphics.Typeface.BOLD)
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }
            row1.addView(dateTv)

            val pointsTv = TextView(this).apply {
                text = "+${record.points} 积分"
                textSize = 14f
                setTextColor(getColor(R.color.primary_red))
                setTypeface(null, android.graphics.Typeface.BOLD)
            }
            row1.addView(pointsTv)
            card.addView(row1)

            // 连续天数
            val contTv = TextView(this).apply {
                text = "连续签到 ${record.continuousDays} 天"
                textSize = 12f
                setTextColor(getColor(R.color.text_secondary))
                setPadding(0, dp4, 0, 0)
            }
            card.addView(contTv)

            // 阶段奖励
            if (record.reward > 0) {
                val rewardTv = TextView(this).apply {
                    text = "🎉 阶段奖励 +${record.reward} 积分"
                    textSize = 11f
                    setTextColor(getColor(R.color.accent_gold))
                    setPadding(0, dp4, 0, 0)
                }
                card.addView(rewardTv)
            }

            binding.layoutHistoryList.addView(card)
        }
    }

    private fun showEmptyState() {
        val emptyTv = TextView(this).apply {
            text = "本月暂无学习签到记录\n坚持每日党史学习打卡吧！"
            textSize = 14f
            setTextColor(getColor(R.color.text_hint))
            gravity = Gravity.CENTER
            setPadding(0, dpToPx(60), 0, 0)
        }
        binding.layoutHistoryList.addView(emptyTv)
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density + 0.5f).toInt()
    }
}
