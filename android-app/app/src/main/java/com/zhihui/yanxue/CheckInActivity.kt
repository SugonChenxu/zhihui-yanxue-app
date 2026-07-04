package com.zhihui.yanxue

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.zhihui.yanxue.data.CheckInRepository
import com.zhihui.yanxue.data.CheckInResult
import com.zhihui.yanxue.CheckInHistoryActivity
import com.zhihui.yanxue.databinding.ActivityCheckinBinding
import java.util.*

/**
 * 签到日历页面
 * 核心功能：月度日历展示、每日签到、连续奖励
 * 排序保障：所有日期数据均通过 CheckInRepository 的排序函数获取，根治乱序
 */
class CheckInActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckinBinding
    private var currentYear: Int = 0
    private var currentMonth: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 初始化当前年月
        val (y, m) = CheckInRepository.getCurrentYearMonth()
        currentYear = y
        currentMonth = m

        setupTopBar()
        updateCheckInStats()
        setupCheckInButton()
        buildCalendar()
        setupMonthSwitch()
    }

    /** 顶部返回 + 历史记录入口 */
    private fun setupTopBar() {
        binding.txtBack.setOnClickListener { finish() }
        binding.txtHistory.setOnClickListener {
            startActivity(Intent(this, CheckInHistoryActivity::class.java))
        }
    }

    /** 更新连续天数、总积分展示 */
    private fun updateCheckInStats() {
        val continuousDays = CheckInRepository.getContinuousDays(this)
        val totalPoints = CheckInRepository.getTotalPoints(this)
        binding.txtDays.text = continuousDays.toString()
        binding.txtTotalPoints.text = totalPoints.toString()

        // 连续奖励阶段提示
        val rewardDesc = CheckInRepository.getRewardStageDesc(continuousDays)
        binding.txtRewardStage.text = rewardDesc.ifEmpty { "继续坚持每日党史学习打卡" }
    }

    /** 签到按钮逻辑（防重复点击） */
    private fun setupCheckInButton() {
        // 今日已签到：按钮置灰
        if (CheckInRepository.isCheckedInToday(this)) {
            binding.btnCheckin.text = "今日已签到 ✓"
            binding.btnCheckin.isEnabled = false
            binding.btnCheckin.alpha = 0.6f
        }

        binding.btnCheckin.setOnClickListener {
            // 防重复点击
            binding.btnCheckin.isEnabled = false
            val result = CheckInRepository.doCheckIn(this)
            when (result) {
                is CheckInResult.Success -> {
                    showCheckInSuccessDialog(result.points, result.continuousDays, result.reward)
                    updateCheckInStats()
                    buildCalendar() // 刷新日历
                    binding.btnCheckin.text = "今日已签到 ✓"
                    binding.btnCheckin.alpha = 0.6f
                }
                is CheckInResult.AlreadyCheckedIn -> {
                    showToast("今日已完成党史学习签到，请明日再来")
                    binding.btnCheckin.text = "今日已签到 ✓"
                    binding.btnCheckin.isEnabled = false
                    binding.btnCheckin.alpha = 0.6f
                }
            }
        }
    }

    /** 签到成功弹窗 */
    private fun showCheckInSuccessDialog(points: Int, continuousDays: Int, reward: Int) {
        val dialog = android.app.Dialog(this)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val view = layoutInflater.inflate(R.layout.dialog_checkin_success, null)
        dialog.setContentView(view)

        view.findViewById<TextView>(R.id.txt_dialog_points).text = "+$points"
        view.findViewById<TextView>(R.id.txt_dialog_continuous).text = "已连续签到${continuousDays}天"
        view.findViewById<TextView>(R.id.txt_dialog_reward).text = if (reward > 0) "阶段奖励 +$reward 积分" else ""

        view.findViewById<TextView>(R.id.txt_dialog_confirm).setOnClickListener {
            dialog.dismiss()
        }
        // 奖励提示
        if (reward > 0) {
            showToast("🎉 达成连续签到阶段奖励！额外获得${reward}积分")
        }
        dialog.show()
    }

    /** 月份切换 */
    private fun setupMonthSwitch() {
        binding.txtPrevMonth.setOnClickListener {
            currentMonth--
            if (currentMonth < 1) {
                currentMonth = 12
                currentYear--
            }
            updateMonthTitle()
            buildCalendar()
        }
        binding.txtNextMonth.setOnClickListener {
            currentMonth++
            if (currentMonth > 12) {
                currentMonth = 1
                currentYear++
            }
            updateMonthTitle()
            buildCalendar()
        }
        updateMonthTitle()
    }

    private fun updateMonthTitle() {
        binding.txtMonthTitle.text = "${currentYear}年${currentMonth}月"
    }

    /**
     * 构建日历网格
     * 排序保障：使用 CheckInRepository.getMonthDaysOrdered() 获取正序日期列表
     * 彻底解决日历日期乱序、错位问题
     */
    private fun buildCalendar() {
        binding.gridCalendar.removeAllViews()
        val grid = binding.gridCalendar

        // 星期标题（固定正序）
        val weekTitles = listOf("一", "二", "三", "四", "五", "六", "日")
        for (title in weekTitles) {
            val tv = TextView(this).apply {
                text = title
                textSize = 12f
                gravity = Gravity.CENTER
                setTextColor(getColor(R.color.text_secondary))
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = ViewGroup.LayoutParams.WRAP_CONTENT
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                }
                setPadding(0, dpToPx(8), 0, dpToPx(8))
            }
            grid.addView(tv)
        }

        // 获取本月日期列表（已按正序排列，无需额外排序）
        val dayItems = CheckInRepository.getMonthDaysOrdered(currentYear, currentMonth)
        val checkedDates = CheckInRepository.getMonthCheckedDates(this, currentYear, currentMonth)
        val todayStr = CheckInRepository.getCurrentDateStr()
        val dp4 = dpToPx(4)
        val dp2 = dpToPx(2)

        for (item in dayItems) {
            if (item.isEmpty) {
                // 空白占位
                val v = View(this)
                v.layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = dpToPx(36)
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                }
                grid.addView(v)
                continue
            }

            // 日期格子
            val tv = TextView(this)
            tv.text = item.day.toString()
            tv.gravity = Gravity.CENTER
            tv.textSize = 13f

            val params = GridLayout.LayoutParams().apply {
                width = 0
                height = dpToPx(40)
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            }
            tv.layoutParams = params
            tv.setPadding(0, dp2, 0, dp2)

            val isToday = item.dateStr == todayStr
            val isChecked = checkedDates.contains(item.dateStr)

            when {
                isChecked -> {
                    // 已签到：红色背景 + 白色文字
                    tv.setBackgroundResource(R.drawable.bg_calendar_checked)
                    tv.setTextColor(getColor(R.color.white))
                    tv.setTypeface(null, android.graphics.Typeface.BOLD)
                    // 连续签到金色角标
                    if (CheckInRepository.getContinuousDays(this) > 0) {
                        tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.bg_gold_dot)
                        tv.compoundDrawablePadding = dp2
                    }
                }
                isToday -> {
                    // 今日未签到：金色边框高亮
                    tv.setBackgroundResource(R.drawable.bg_calendar_today)
                    tv.setTextColor(getColor(R.color.primary_red))
                    tv.setTypeface(null, android.graphics.Typeface.BOLD)
                }
                else -> {
                    // 未签到
                    tv.setTextColor(getColor(R.color.text_primary))
                }
            }

            // 点击查看当日签到详情
            tv.setOnClickListener {
                if (isChecked) {
                    showDayDetailDialog(item.dateStr)
                } else if (isToday) {
                    showToast("今日尚未签到，快去完成党史学习打卡吧！")
                }
            }

            grid.addView(tv)
        }
    }

    /** 查看当日签到详情弹窗 */
    private fun showDayDetailDialog(dateStr: String) {
        val history = CheckInRepository.getHistoryList(this)
        val record = history.find { it.date == dateStr }
        if (record != null) {
            showToast("${dateStr}\n获得${record.points}积分\n连续签到${record.continuousDays}天")
        }
    }

    private fun showToast(msg: String) {
        android.widget.Toast.makeText(this, msg, android.widget.Toast.LENGTH_SHORT).show()
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density + 0.5f).toInt()
    }

    companion object {
        fun getCurrentDateStr(): String {
            return java.text.SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(Date())
        }
    }
}
