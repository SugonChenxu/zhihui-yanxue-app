package com.zhihui.yanxue

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat

/**
 * 设置页面
 * 功能：清理缓存、字号调整、推送开关、夜间模式、用户协议、隐私政策
 * 交互：纯前端模拟，无需后端接口
 * 路由：/user/setting
 */
class SettingsActivity : BaseActivity() {

    private lateinit var switchPush: SwitchCompat
    private lateinit var switchNight: SwitchCompat
    private lateinit var txtFontSizeValue: TextView

    // 当前字号档位：0=标准，1=大号，2=特大号
    private var currentFontSize = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        initViews()
        setupBackButton()
        setupClearCache()
        setupFontSize()
        setupPushSwitch()
        setupNightMode()
        setupUserAgreement()
        setupPrivacyPolicy()
    }

    /**
     * 初始化视图
     */
    private fun initViews() {
        switchPush = findViewById(R.id.switch_push)
        switchNight = findViewById(R.id.switch_night)
        txtFontSizeValue = findViewById(R.id.txt_font_size_value)
    }

    /**
     * 设置返回按钮
     */
    private fun setupBackButton() {
        findViewById<TextView>(R.id.txt_back_setting).setOnClickListener {
            finish()
        }
    }

    /**
     * 清理缓存功能
     * 交互：点击弹出确认弹窗，确认后弹窗提示"缓存清理完成"
     */
    private fun setupClearCache() {
        findViewById<LinearLayout>(R.id.menu_clear_cache).setOnClickListener {
            showClearCacheDialog()
        }
    }

    /**
     * 显示清理缓存确认弹窗
     */
    private fun showClearCacheDialog() {
        AlertDialog.Builder(this)
            .setTitle("清理缓存")
            .setMessage("确定清理本地缓存？")
            .setPositiveButton("确定") { _, _ ->
                // 模拟清理缓存，弹窗提示完成
                showToast("缓存清理完成")
            }
            .setNegativeButton("取消", null)
            .show()
    }

    /**
     * 字号调整功能
     * 交互：点击弹出底部弹窗，提供三档字号：标准、大号、特大号
     * 选中后页面文字实时切换大小展示效果
     */
    private fun setupFontSize() {
        findViewById<LinearLayout>(R.id.menu_font_size).setOnClickListener {
            showFontSizeDialog()
        }
    }

    /**
     * 显示字号调整弹窗
     * 提供三档字号选择：标准、大号、特大号
     */
    private fun showFontSizeDialog() {
        val fontSizes = arrayOf("标准", "大号", "特大号")

        AlertDialog.Builder(this)
            .setTitle("字号调整")
            .setSingleChoiceItems(fontSizes, currentFontSize) { dialog, which ->
                // 选中字号档位
                currentFontSize = which
                txtFontSizeValue.text = fontSizes[which]

                // 实时切换页面文字大小（模拟效果）
                applyFontSize()

                // 关闭弹窗
                dialog.dismiss()

                // 弹窗提示已保存
                showToast("字号设置已保存")
            }
            .setNegativeButton("取消", null)
            .show()
    }

    /**
     * 应用字号设置
     * 功能：根据当前字号档位，调整页面文字大小
     * 实现：保存设置后重建Activity使全局字号生效
     */
    private fun applyFontSize() {
        val scale = when (currentFontSize) {
            0 -> 1.0f  // 标准
            1 -> 1.2f  // 大号
            2 -> 1.4f  // 特大号
            else -> 1.0f
        }

        // 保存字号设置到 SharedPreferences
        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        prefs.edit().putInt("font_size", currentFontSize).apply()

        // 重建Activity使字号设置全局生效
        recreate()
    }

    /**
     * 推送消息开关
     * 交互：右侧自带切换开关，打开/关闭切换时有切换动画
     * 切换后弹窗提示：推送设置已保存
     */
    private fun setupPushSwitch() {
        // 读取保存的推送设置
        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        switchPush.isChecked = prefs.getBoolean("push_enabled", true)

        switchPush.setOnCheckedChangeListener { _, isChecked ->
            // 保存推送设置
            prefs.edit().putBoolean("push_enabled", isChecked).apply()

            // 弹窗提示已保存
            showToast("推送设置已保存")
        }
    }

    /**
     * 夜间模式开关
     * 交互：右侧切换开关，开启页面切换深色夜间主题
     * 关闭恢复原浅色红金主题，可实时预览效果
     */
    private fun setupNightMode() {
        // 读取保存的夜间模式设置
        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        switchNight.isChecked = prefs.getBoolean("night_mode", false)

        switchNight.setOnCheckedChangeListener { _, isChecked ->
            // 保存夜间模式设置
            prefs.edit().putBoolean("night_mode", isChecked).apply()

            // 切换夜间模式
            applyNightMode(isChecked)

            // 弹窗提示已保存
            showToast(if (isChecked) "已开启夜间模式" else "已关闭夜间模式")
        }
    }

    /**
     * 应用夜间模式
     * @param enabled 是否开启夜间模式
     * 实现：切换夜间模式后重建Activity立即生效
     */
    private fun applyNightMode(enabled: Boolean) {
        // 保存夜间模式设置
        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        prefs.edit().putBoolean("night_mode", enabled).apply()

        // 使用 AppCompatDelegate 切换夜间模式
        androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(
            if (enabled) {
                androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
            } else {
                androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
            }
        )

        // 重建Activity使夜间模式立即生效
        recreate()
    }

    /**
     * 用户协议
     * 交互：点击新开页面/弹窗展示静态协议文本
     */
    private fun setupUserAgreement() {
        findViewById<LinearLayout>(R.id.menu_user_agreement).setOnClickListener {
            showUserAgreementDialog()
        }
    }

    /**
     * 显示用户协议弹窗
     * 内容：占位通用APP用户协议文案
     */
    private fun showUserAgreementDialog() {
        val agreementText = """
            《智绘研学e启航用户协议》

            欢迎使用智绘研学e启航APP（以下简称"本应用"）。

            一、服务条款
            1. 本应用为用户提供红色文化研学课程、VR体验、直播互动等教育服务。
            2. 用户在使用本应用前，应仔细阅读并理解本协议的全部内容。

            二、用户权利义务
            1. 用户有权使用本应用提供的各项教育服务。
            2. 用户应遵守相关法律法规，不得利用本应用从事违法活动。
            3. 用户应妥善保管个人账号信息，对因账号泄露导致的损失自行承担责任。

            三、知识产权
            1. 本应用中的所有内容（包括但不限于文字、图片、视频、音频等）的知识产权归天津商务职业学院所有。
            2. 未经授权，用户不得复制、修改、传播本应用中的任何内容。

            四、免责声明
            1. 本应用将尽最大努力提供稳定可靠的服务，但不保证服务不会中断或出错。
            2. 因不可抗力因素导致的服务中断或数据丢失，本应用不承担责任。

            五、协议修改
            本应用有权根据需要修改本协议，修改后的协议将在应用内公布。

            六、法律适用
            本协议的订立、执行和解释及争议的解决均适用中华人民共和国法律。

            版本日期：2026年7月1日
        """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("用户协议")
            .setMessage(agreementText)
            .setPositiveButton("确定", null)
            .show()
    }

    /**
     * 隐私政策
     * 交互：点击新开页面/弹窗展示静态隐私政策文本
     */
    private fun setupPrivacyPolicy() {
        findViewById<LinearLayout>(R.id.menu_privacy_policy).setOnClickListener {
            showPrivacyPolicyDialog()
        }
    }

    /**
     * 显示隐私政策弹窗
     * 内容：占位通用APP隐私政策文案
     */
    private fun showPrivacyPolicyDialog() {
        val privacyText = """
            《智绘研学e启航隐私政策》

            本应用非常重视用户的隐私保护。本隐私政策说明了我们如何收集、使用、存储和保护您的个人信息。

            一、信息收集
            1. 我们收集您提供的个人信息，包括用户名、手机号、学习记录等。
            2. 我们自动收集您使用本应用的信息，包括设备信息、操作日志等。

            二、信息使用
            1. 我们收集的信息用于提供和优化我们的服务。
            2. 我们不会将您的个人信息出售给第三方。

            三、信息存储
            1. 我们采用合理的安全措施保护您的个人信息。
            2. 我们会按照法律法规的要求存储您的个人信息。

            四、信息共享
            1. 未经您同意，我们不会向第三方共享您的个人信息。
            2. 在下列情况下，我们可能会共享您的个人信息：
               (1) 获得您的明确同意；
               (2) 根据法律法规的要求；
               (3) 为保护本应用及用户的合法权益。

            五、您的权利
            1. 您有权访问、更正、删除您的个人信息。
            2. 您有权撤回您对我们处理您个人信息的同意。

            六、政策更新
            我们可能会不时更新本隐私政策。更新后的政策将在应用内公布。

            七、联系我们
            如您对本隐私政策有任何疑问，可通过以下方式联系我们：
            天津商务学院·旅游管理专业·李玉冰 13111111111

            版本日期：2026年7月1日
        """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("隐私政策")
            .setMessage(privacyText)
            .setPositiveButton("确定", null)
            .show()
    }

    /**
     * 显示Toast提示
     * @param message 提示信息
     */
    private fun showToast(message: String) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show()
    }
}
