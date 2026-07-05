package com.zhihui.yanxue.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.zhihui.yanxue.CheckInActivity
import com.zhihui.yanxue.FeedbackActivity
import com.zhihui.yanxue.HelpCenterActivity
import com.zhihui.yanxue.R
import com.zhihui.yanxue.SettingsActivity
import com.zhihui.yanxue.databinding.FragmentProfileBinding

/**
 * 个人中心页面
 * 功能：展示用户信息、四宫格数据、功能菜单列表
 * 联动：积分与签到模块打通，签到完成后自动刷新积分
 * 接口：GET /api/user/info（用户信息）、GET /api/user/score/get（积分刷新）
 */
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadUserInfo()
        setupClickListeners()
    }

    /**
     * 加载用户信息
     * 接口位置：GET /api/user/info
     * 当前使用 Mock 数据，后续对接后端时替换
     */
    private fun loadUserInfo() {
        // TODO: 对接后端接口 GET /api/user/info
        // 请求示例：
        // val token = getToken()
        // ApiClient.getUserInfo(token).enqueue(object : Callback<UserInfo> {
        //     override fun onSuccess(userInfo: UserInfo) {
        //         updateUI(userInfo)
        //     }
        //     override fun onError(error: String) {
        //         showError(error)
        //     }
        // })

        // 当前使用 Mock 数据
        val userInfo = MockUserRepository.getUserInfo(requireContext())
        updateUI(userInfo)
    }

    /**
     * 更新UI显示
     * @param userInfo 用户信息数据模型
     */
    private fun updateUI(userInfo: UserInfo) {
        // 用户ID
        binding.txtUserId.text = userInfo.userId

        // 学习时长 | 经验值
        binding.txtStudyInfo.text = "累计学习 ${userInfo.studyHour}小时 | 经验值 ${userInfo.exp}"

        // 四宫格数据
        binding.txtFansNum.text = userInfo.fansNum.toString()
        binding.txtFollowNum.text = userInfo.followNum.toString()
        binding.txtScoreNum.text = userInfo.totalScore.toString()
        binding.txtContinuousNum.text = userInfo.continueSignDay.toString()
    }

    /**
     * 设置点击事件
     * 路由跳转：各菜单点击跳转对应页面，路由地址写死在代码中
     */
    private fun setupClickListeners() {
        // 头像点击事件（跳转头像修改页面，预留路由）
        binding.imgAvatar.setOnClickListener {
            // TODO: 跳转头像修改页
            // startActivity(Intent(requireContext(), AvatarEditActivity::class.java))
            showToast("头像修改功能开发中")
        }

        // 积分区域点击（跳转签到日历页面）
        binding.layoutScore.setOnClickListener {
            val intent = Intent(requireContext(), CheckInActivity::class.java)
            startActivity(intent)
        }

        // 连续签到天数点击（跳转签到日历页面）
        binding.layoutContinuous.setOnClickListener {
            val intent = Intent(requireContext(), CheckInActivity::class.java)
            startActivity(intent)
        }

        // 菜单列表点击事件
        setupMenuClickListeners()
    }

    /**
     * 设置菜单列表点击事件
     * 路由地址：
     * - 我的订单 → /order/list
     * - 我的收藏 → /collect/course
     * - 我的历史 → /history/watch
     * - 意见反馈 → /feedback/add
     * - 帮助中心 → /help/faq
     * - 设置 → /user/setting
     */
    private fun setupMenuClickListeners() {
        // 我的订单
        binding.menuOrder.setOnClickListener {
            // TODO: 跳转订单列表页
            // Route: /order/list
            showToast("我的订单功能开发中")
        }

        // 我的收藏
        binding.menuCollect.setOnClickListener {
            // TODO: 跳转收藏列表页
            // Route: /collect/course
            showToast("我的收藏功能开发中")
        }

        // 我的历史
        binding.menuHistory.setOnClickListener {
            // TODO: 跳转历史记录页
            // Route: /history/watch
            showToast("我的历史功能开发中")
        }

        // 意见反馈
        binding.menuFeedback.setOnClickListener {
            // Route: /feedback/add
            val intent = Intent(requireContext(), FeedbackActivity::class.java)
            startActivity(intent)
        }

        // 帮助中心
        binding.menuHelp.setOnClickListener {
            // Route: /help/faq
            val intent = Intent(requireContext(), HelpCenterActivity::class.java)
            startActivity(intent)
        }

        // 设置
        binding.menuSetting.setOnClickListener {
            // Route: /user/setting
            val intent = Intent(requireContext(), SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * 刷新积分数据
     * 接口位置：GET /api/user/score/get
     * 调用场景：签到完成后返回本页时自动调用
     */
    private fun refreshScore() {
        // TODO: 对接后端接口 GET /api/user/score/get
        // 请求示例：
        // val token = getToken()
        // ApiClient.getScore(token).enqueue(object : Callback<Int> {
        //     override fun onSuccess(score: Int) {
        //         binding.txtScoreNum.text = score.toString()
        //     }
        // })

        // 当前使用 Mock 数据刷新
        val userInfo = MockUserRepository.getUserInfo(requireContext())
        binding.txtScoreNum.text = userInfo.totalScore.toString()
        binding.txtContinuousNum.text = userInfo.continueSignDay.toString()
    }

    /**
     * 页面恢复时刷新数据
     * 场景：从签到页返回后自动刷新积分
     */
    override fun onResume() {
        super.onResume()
        refreshScore()
    }

    /**
     * 显示Toast提示
     * @param message 提示信息
     */
    private fun showToast(message: String) {
        android.widget.Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

/**
 * 用户信息数据模型
 * 对应接口：GET /api/user/info 返回数据
 */
data class UserInfo(
    val avatar: String,          // 头像URL
    val userId: String,          // 用户唯一ID
    val studyHour: Float,       // 累计学习时长（小时）
    val exp: Int,               // 当前经验值
    val fansNum: Int,           // 粉丝数
    val followNum: Int,         // 关注数
    val totalScore: Int,        // 总积分
    val continueSignDay: Int    // 当前连续签到天数
)

/**
 * Mock数据仓库
 * 提供模拟用户数据，后续对接后端时删除此类
 */
object MockUserRepository {
    fun getUserInfo(context: android.content.Context): UserInfo {
        // 优先从本地读取签到积分数据
        val score = com.zhihui.yanxue.data.CheckInRepository.getTotalPoints(context)
        val continuousDays = com.zhihui.yanxue.data.CheckInRepository.getContinuousDays(context)

        return UserInfo(
            avatar = "",
            userId = "U20260704001",
            studyHour = 126.5f,
            exp = 3680,
            fansNum = 23,
            followNum = 45,
            totalScore = if (score > 0) score else 1560,
            continueSignDay = if (continuousDays > 0) continuousDays else 7
        )
    }
}
