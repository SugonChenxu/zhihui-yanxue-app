package com.zhihui.yanxue.data

import android.content.Context
import android.content.SharedPreferences
import java.text.SimpleDateFormat
import java.util.*

/**
 * 签到数据统一管理层
 * 所有签到相关读写都通过此类，保证数据一致性、排序正确性
 * 后续对接后端：只需修改此类中的方法实现，上层代码无需改动
 */
object CheckInRepository {

    private const val PREF_NAME = "checkin_data"
    private const val KEY_DATES = "checkin_dates"
    private const val KEY_TOTAL_POINTS = "total_points"
    private const val KEY_CONTINUOUS_DAYS = "continuous_days"
    private const val KEY_LAST_CHECKIN_DATE = "last_checkin_date"
    private const val KEY_HISTORY = "checkin_history"

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
    private val displayFormat = SimpleDateFormat("MM月dd日", Locale.CHINA)

    /** 获取 SharedPreferences */
    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    // ==================== 核心数据读写 ====================

    /** 判断今天是否已签到 */
    fun isCheckedInToday(context: Context): Boolean {
        val today = dateFormat.format(Date())
        val dates = getPrefs(context).getStringSet(KEY_DATES, emptySet()) ?: emptySet()
        return dates.contains(today)
    }

    /** 执行签到（核心方法） */
    fun doCheckIn(context: Context): CheckInResult {
        val prefs = getPrefs(context)
        val today = dateFormat.format(Date())
        val dates = LinkedHashSet(prefs.getStringSet(KEY_DATES, emptySet()) ?: emptySet())

        if (dates.contains(today)) {
            return CheckInResult.AlreadyCheckedIn
        }

        // 1. 记录签到日期
        dates.add(today)
        prefs.edit().putStringSet(KEY_DATES, dates).apply()

        // 2. 计算连续天数
        val continuousDays = calculateContinuousDays(dates)
        prefs.edit().putInt(KEY_CONTINUOUS_DAYS, continuousDays).apply()
        prefs.edit().putString(KEY_LAST_CHECKIN_DATE, today).apply()

        // 3. 计算获得积分（基础10分 + 连续奖励）
        val basePoints = 10
        val reward = getContinuousReward(continuousDays)
        val totalPoints = prefs.getInt(KEY_TOTAL_POINTS, 0) + basePoints + reward
        prefs.edit().putInt(KEY_TOTAL_POINTS, totalPoints).apply()

        // 4. 记录历史（按时间倒序存储，最新在前）
        val history = getHistoryList(context).toMutableList()
        history.add(0, CheckInRecord(
            date = today,
            dateDisplay = displayFormat.format(Date()),
            points = basePoints + reward,
            continuousDays = continuousDays,
            reward = reward
        ))
        // 只保留最近200条
        val trimmed = if (history.size > 200) history.subList(0, 200) else history
        saveHistoryList(context, trimmed)

        return CheckInResult.Success(
            points = basePoints + reward,
            continuousDays = continuousDays,
            reward = reward
        )
    }

    /** 计算连续签到天数（从今天往前连续数） */
    private fun calculateContinuousDays(dates: Set<String>): Int {
        val cal = Calendar.getInstance()
        var count = 0
        while (true) {
            val key = dateFormat.format(cal.time)
            if (dates.contains(key)) {
                count++
                cal.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
        }
        return count
    }

    /** 获取连续签到奖励积分 */
    private fun getContinuousReward(continuousDays: Int): Int {
        return when {
            continuousDays >= 30 -> 50
            continuousDays >= 15 -> 30
            continuousDays >= 7  -> 20
            continuousDays >= 3  -> 10
            else -> 0
        }
    }

    /** 获取当前连续签到天数 */
    fun getContinuousDays(context: Context): Int {
        val prefs = getPrefs(context)
        val dates = prefs.getStringSet(KEY_DATES, emptySet()) ?: emptySet()
        return if (dates.isEmpty()) 0 else calculateContinuousDays(dates)
    }

    /** 获取总积分 */
    fun getTotalPoints(context: Context): Int {
        return getPrefs(context).getInt(KEY_TOTAL_POINTS, 0)
    }

    /** 获取本月已签到日期集合（已去重） */
    fun getMonthCheckedDates(context: Context, year: Int, month: Int): Set<String> {
        val dates = getPrefs(context).getStringSet(KEY_DATES, emptySet()) ?: emptySet()
        val prefix = String.format("%04d-%02d", year, month)
        return dates.filter { it.startsWith(prefix) }.toSet()
    }

    // ==================== 排序工具函数（根治乱序核心） ====================

    /**
     * 将日期字符串按时间正序排列
     * 所有渲染日历、列表前必须调用此函数
     * "yyyy-MM-dd" 格式字符串直接字典序即时间序，无需转换时间戳
     */
    fun sortDatesAsc(dates: Set<String>): List<String> {
        return dates.sorted()
    }

    /**
     * 将签到记录按时间倒序排列（最新在前）
     * 历史记录列表渲染前必须调用此函数
     */
    fun sortRecordsDesc(records: List<CheckInRecord>): List<CheckInRecord> {
        return records.sortedByDescending { it.date }
    }

    /**
     * 获取某月所有日期列表（正序，1号到月末）
     * 直接用于日历渲染，彻底解决乱序问题
     * 返回列表包含：前面空白占位 + 1~月末日期
     */
    fun getMonthDaysOrdered(year: Int, month: Int): List<MonthDayItem> {
        val cal = Calendar.getInstance()
        cal.set(year, month - 1, 1, 0, 0, 0)
        // 星期一是1，星期日是7，转成周一=0
        val firstDayOfWeek = (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7
        val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        val list = mutableListOf<MonthDayItem>()

        // 前面补空白占位
        for (i in 0 until firstDayOfWeek) {
            list.add(MonthDayItem.empty())
        }
        // 日期正序填入（1号到月末，天然正序，无需额外排序）
        for (day in 1..daysInMonth) {
            cal.set(year, month - 1, day, 0, 0, 0)
            val dateStr = dateFormat.format(cal.time)
            list.add(MonthDayItem(
                day = day,
                dateStr = dateStr,
                isCurrentMonth = true
            ))
        }
        return list
    }

    // ==================== 历史记录 ====================

    /** 获取签到历史记录（倒序，最新在前） */
    fun getHistoryList(context: Context): List<CheckInRecord> {
        val json = getPrefs(context).getString(KEY_HISTORY, "[]") ?: "[]"
        return try {
            parseHistoryJson(json)
        } catch (e: Exception) {
            emptyList()
        }
    }

    /** 保存历史记录 */
    private fun saveHistoryList(context: Context, list: List<CheckInRecord>) {
        val json = buildHistoryJson(list)
        getPrefs(context).edit().putString(KEY_HISTORY, json).apply()
    }

    /** 简易JSON构建（避免引入Gson依赖） */
    private fun buildHistoryJson(list: List<CheckInRecord>): String {
        val sb = StringBuilder("[")
        for ((i, r) in list.withIndex()) {
            if (i > 0) sb.append(",")
            sb.append("""{"date":"${r.date}","dateDisplay":"${r.dateDisplay}","points":${r.points},"continuousDays":${r.continuousDays},"reward":${r.reward}}""")
        }
        sb.append("]")
        return sb.toString()
    }

    /** 简易JSON解析 */
    private fun parseHistoryJson(json: String): List<CheckInRecord> {
        val list = mutableListOf<CheckInRecord>()
        // 匹配 {"date":"xxx","dateDisplay":"xxx","points":xx,"continuousDays":xx,"reward":xx}
        val regex = """\{"date":"([^"]+)","dateDisplay":"([^"]+)","points":(\d+),"continuousDays":(\d+),"reward":(\d+)\}""".toRegex()
        regex.findAll(json).forEach { match ->
            val (date, dateDisplay, points, contDays, reward) = match.destructured
            list.add(CheckInRecord(date, dateDisplay, points.toInt(), contDays.toInt(), reward.toInt()))
        }
        return list
    }

    /** 按月筛选历史记录（倒序） */
    fun getHistoryByMonth(context: Context, year: Int, month: Int): List<CheckInRecord> {
        val all = getHistoryList(context)
        val prefix = String.format("%04d-%02d", year, month)
        return all.filter { it.date.startsWith(prefix) }.sortedByDescending { it.date }
    }

    /** 获取当前年月 */
    fun getCurrentYearMonth(): Pair<Int, Int> {
        val cal = Calendar.getInstance()
        return Pair(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1)
    }

    /** 获取今天日期字符串 (yyyy-MM-dd) */
    fun getCurrentDateStr(): String {
        return dateFormat.format(Date())
    }

    /** 解析日期字符串为 Date 对象 */
    fun parseDate(dateStr: String): Date? {
        return try {
            dateFormat.parse(dateStr)
        } catch (e: Exception) {
            null
        }
    }

    /** 获取连续奖励阶段描述 */
    fun getRewardStageDesc(continuousDays: Int): String {
        return when {
            continuousDays >= 30 -> "连续签到30天，获得阶段奖励50积分！"
            continuousDays >= 15 -> "连续签到15天，获得阶段奖励30积分！"
            continuousDays >= 7  -> "连续签到7天，获得阶段奖励20积分！"
            continuousDays >= 3  -> "连续签到3天，获得阶段奖励10积分！"
            else -> ""
        }
    }
}

// ==================== 数据模型 ====================

/** 签到结果 密封类 */
sealed class CheckInResult {
    object AlreadyCheckedIn : CheckInResult()
    data class Success(
        val points: Int,
        val continuousDays: Int,
        val reward: Int
    ) : CheckInResult()
}

/** 签到记录 */
data class CheckInRecord(
    val date: String,
    val dateDisplay: String,
    val points: Int,
    val continuousDays: Int,
    val reward: Int
)

/** 日历日期项 */
data class MonthDayItem(
    val day: Int = 0,
    val dateStr: String = "",
    val isCurrentMonth: Boolean = false,
    val isEmpty: Boolean = false
) {
    companion object {
        fun empty() = MonthDayItem(isEmpty = true)
    }
}
