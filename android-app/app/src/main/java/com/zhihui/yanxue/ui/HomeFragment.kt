package com.zhihui.yanxue.ui

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.zhihui.yanxue.CheckInActivity
import com.zhihui.yanxue.CheckInHistoryActivity
import com.zhihui.yanxue.MessageActivity
import com.zhihui.yanxue.R
import com.zhihui.yanxue.SearchActivity
import com.zhihui.yanxue.data.CheckInRepository

class HomeFragment : Fragment() {

    private var _binding: com.zhihui.yanxue.databinding.FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var currentTab = "学习"

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
        setupTopBar()
        setupTabs()
        updateCheckInCard()  // 更新签到卡片状态
        setupCheckInCard()   // 设置签到卡片点击
        setupContent()
        binding.swipeRefresh.setOnRefreshListener {
            updateCheckInCard()  // 下拉刷新时更新签到状态
            setupContent()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun setupTopBar() {
        binding.layoutSearch.setOnClickListener {
            startActivity(Intent(requireContext(), SearchActivity::class.java))
        }
        binding.txtCheckin.setOnClickListener {
            startActivity(Intent(requireContext(), CheckInActivity::class.java))
        }
        binding.txtNotification.setOnClickListener {
            startActivity(Intent(requireContext(), MessageActivity::class.java))
        }
    }

    private fun setupTabs() {
        val tabs = listOf("学习", "推荐", "探索", "观点", "专题")
        binding.layoutTabs.removeAllViews()
        for (tab in tabs) {
            val tv = TextView(requireContext()).apply {
                text = tab
                textSize = 14f
                gravity = Gravity.CENTER
                if (tab == currentTab) {
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.primary_red))
                    setTypeface(null, android.graphics.Typeface.BOLD)
                } else {
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary))
                }
                setOnClickListener { onTabSelected(tab) }
            }
            val params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f).apply {
                gravity = Gravity.CENTER
            }
            tv.layoutParams = params
            binding.layoutTabs.addView(tv)
        }
    }

    private fun onTabSelected(tab: String) {
        currentTab = tab
        setupTabs()
        setupContent()
    }

    private fun setupContent() {
        binding.layoutContentCards.removeAllViews()
        val items = getMockDataForTab(currentTab)
        val dp8 = dpToPx(8)
        val dp12 = dpToPx(12)
        for (item in items) {
            val card = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_card_white)
                setPadding(dp12, dp12, dp12, dp12)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = dp8
                }
            }
            val title = TextView(requireContext()).apply {
                text = item.first
                textSize = 15f
                setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary))
                setTypeface(null, android.graphics.Typeface.BOLD)
            }
            card.addView(title)
            val desc = TextView(requireContext()).apply {
                text = item.second
                textSize = 12f
                setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary))
                setPadding(0, dp8, 0, 0)
            }
            card.addView(desc)
            binding.layoutContentCards.addView(card)
        }
    }

    private fun getMockDataForTab(tab: String): List<Pair<String, String>> {
        return when (tab) {
            "学习" -> listOf(
                "周恩来精神与天津" to "走进周邓纪念馆，感悟伟人品格与革命情怀",
                "平津战役全纪实" to "从战略部署到胜利会师，全景式回顾平津战役",
                "觉悟社里的青春" to "探寻周恩来、马骏等青年志士的革命初心"
            )
            "推荐" -> listOf(
                "天津红色文化之旅" to "上线带你走遍天津红色景点",
                "青少年的红色信仰" to "红色精神如何照亮青少年成长之路",
                "VR体验：虎门行" to "沉浸式虚拟游览，足不出户感受天津红色文化"
            )
            "探索" -> listOf(
                "天津红色地标地图" to "使用地图功能，探索天津各大红色地标",
                "AR寻宝游戏" to "打开摄像头，在实景中寻找红色纪念品",
                "红色脑力大挑战" to "通过问答闯关，测试你对天津红色文化的了解"
            )
            "观点" -> listOf(
                "如何看待天津红色文化" to "专家观点：红色文化是城市的根与魂",
                "青少年应如何传承红色精神" to "观点文章：创新传承方式，让红色文化活起来",
                "红色旅游与乡村振兴的关系" to "观点：红色旅游如何助力乡村振兴"
            )
            "专题" -> listOf(
                "天津红色教育专题系列" to "针对不同年龄段的红色教育专题课程",
                "走进平津战役专题微课" to "专题系列微课，深度解读平津战役历史",
                "平津战役专题展览" to "线上专题展览，多视角回顾历史瞬间"
            )
            else -> emptyList()
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density + 0.5f).toInt()
    }

    /** 更新首页签到卡片状态 */
    private fun updateCheckInCard() {
        val isCheckedIn = CheckInRepository.isCheckedInToday(requireContext())
        val continuousDays = CheckInRepository.getContinuousDays(requireContext())

        if (isCheckedIn) {
            binding.txtCheckinStatus.text = "今日已签到 ✓"
            binding.txtCheckinStatus.setTextColor(requireContext().getColor(R.color.white))
            binding.txtGoCheckin.text = "查看详情 →"
        } else {
            binding.txtCheckinStatus.text = "今日尚未签到"
            binding.txtCheckinStatus.setTextColor(requireContext().getColor(R.color.white))
            binding.txtGoCheckin.text = "去签到 →"
        }
        binding.txtCheckinDaysHint.text = "已连续签到${continuousDays}天"
    }

    /** 设置签到卡片点击事件 */
    private fun setupCheckInCard() {
        binding.cardCheckin.setOnClickListener {
            startActivity(Intent(requireContext(), CheckInActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        // 从签到页返回后刷新状态
        updateCheckInCard()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
