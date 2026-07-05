package com.zhihui.yanxue.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.zhihui.yanxue.ArticleDetailActivity
import com.zhihui.yanxue.BannerAdapter
import com.zhihui.yanxue.CheckInActivity
import com.zhihui.yanxue.MessageActivity
import com.zhihui.yanxue.R
import com.zhihui.yanxue.SearchActivity
import com.zhihui.yanxue.data.CheckInRepository
import com.zhihui.yanxue.data.MockArticleRepository
import com.zhihui.yanxue.data.model.Article
import com.zhihui.yanxue.data.model.Banner

class HomeFragment : Fragment() {

    private var _binding: com.zhihui.yanxue.databinding.FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var currentTab = "推荐"

    // === Banner 轮播相关 ===
    private lateinit var bannerAdapter: BannerAdapter
    private val banners: List<Banner> by lazy { MockArticleRepository.getBanners() }
    private val bannerHandler = Handler(Looper.getMainLooper())
    private var isBannerAutoScrolling = false

    /** Banner 自动滚动任务 */
    private val bannerRunnable = object : Runnable {
        override fun run() {
            if (!isBannerAutoScrolling || banners.isEmpty()) return
            val current = binding.viewpagerBanner.currentItem
            binding.viewpagerBanner.setCurrentItem(current + 1, true)
            bannerHandler.postDelayed(this, 3000)
        }
    }

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
        setupBanner()
        updateCheckInCard()
        setupCheckInCard()
        setupContent()
        binding.swipeRefresh.setOnRefreshListener {
            updateCheckInCard()
            setupBanner()
            setupContent()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    // ==================== 顶部栏 ====================

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

    // ==================== Tab 切换 ====================

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

    // ==================== Banner 轮播 ====================

    /**
     * 初始化 ViewPager2 Banner 轮播
     * - 无限滑动（Int.MAX_VALUE）
     * - 自动循环滚动（3秒间隔）
     * - 底部页码指示器
     * - 点击跳转文章详情
     * - 手指按下暂停自动滚动，抬起恢复
     */
    private fun setupBanner() {
        if (banners.isEmpty()) return

        bannerAdapter = BannerAdapter(banners) { banner ->
            // 点击 Banner 跳转文章详情
            val intent = Intent(requireContext(), ArticleDetailActivity::class.java).apply {
                putExtra(ArticleDetailActivity.EXTRA_ARTICLE_ID, banner.articleId)
            }
            startActivity(intent)
        }
        binding.viewpagerBanner.adapter = bannerAdapter

        // 设置初始位置到中间，实现"无限"向左滑动的效果
        val midPosition = Int.MAX_VALUE / 2
        val startPos = midPosition - (midPosition % banners.size)
        binding.viewpagerBanner.setCurrentItem(startPos, false)

        // 页面切换回调 — 更新指示器
        binding.viewpagerBanner.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateIndicator(bannerAdapter.getRealPosition(position))
            }
        })

        // 设置指示器
        setupIndicator()

        // 触摸监听 — 按下暂停、抬起恢复（使用 post 确保 RecyclerView 已创建）
        binding.viewpagerBanner.post {
            val rv = binding.viewpagerBanner.getChildAt(0)
            rv?.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> stopBannerAutoScroll()
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> startBannerAutoScroll()
                }
                false
            }
        }

        // 启动自动滚动
        startBannerAutoScroll()
    }

    /** 创建底部页码指示器圆点 */
    private fun setupIndicator() {
        binding.layoutIndicator.removeAllViews()
        val dotSize = dpToPx(8)
        val dotMargin = dpToPx(4)
        for (i in banners.indices) {
            val dot = ImageView(requireContext()).apply {
                setImageResource(R.drawable.dot_indicator_unselected)
                layoutParams = LinearLayout.LayoutParams(dotSize, dotSize).apply {
                    marginStart = dotMargin
                    marginEnd = dotMargin
                }
            }
            binding.layoutIndicator.addView(dot)
        }
        // 默认选中第一个
        updateIndicator(0)
    }

    /** 更新指示器选中状态 */
    private fun updateIndicator(selectedIndex: Int) {
        val count = binding.layoutIndicator.childCount
        for (i in 0 until count) {
            val dot = binding.layoutIndicator.getChildAt(i) as ImageView
            dot.setImageResource(
                if (i == selectedIndex) R.drawable.dot_indicator_selected
                else R.drawable.dot_indicator_unselected
            )
        }
    }

    /** 启动 Banner 自动滚动 */
    private fun startBannerAutoScroll() {
        if (isBannerAutoScrolling || banners.isEmpty()) return
        isBannerAutoScrolling = true
        bannerHandler.postDelayed(bannerRunnable, 3000)
    }

    /** 停止 Banner 自动滚动 */
    private fun stopBannerAutoScroll() {
        isBannerAutoScrolling = false
        bannerHandler.removeCallbacks(bannerRunnable)
    }

    // ==================== 文章内容 ====================

    /**
     * 根据当前 Tab 渲染内容
     * - 推荐 Tab: 3 篇置顶文章(大卡片) + 专栏分区(横向滚动)
     * - 其他 Tab: 专栏分区(横向滚动)，每分区 4-5 篇带图文章
     */
    private fun setupContent() {
        binding.layoutContentCards.removeAllViews()

        // 更新精选内容标题
        binding.txtCategoryTitle.text = when (currentTab) {
            "推荐" -> "为你推荐"
            "学习" -> "红色学堂"
            "探索" -> "沉浸探索"
            "观点" -> "思想碰撞"
            "专题" -> "红色专题"
            else -> "精选内容"
        }

        val articles = MockArticleRepository.getArticlesByTab(currentTab)
        if (articles.isEmpty()) return

        // 推荐 Tab: 先展示 3 篇置顶文章
        if (currentTab == "推荐") {
            val pinnedArticles = MockArticleRepository.getPinnedArticles(currentTab)
            for (article in pinnedArticles) {
                val pinnedView = createPinnedArticleView(article)
                binding.layoutContentCards.addView(pinnedView)
            }
        }

        // 按专栏分区展示文章
        val sections = MockArticleRepository.getSectionsByTab(currentTab)
        for (section in sections) {
            val sectionArticles = MockArticleRepository.getArticlesBySection(currentTab, section)

            // 推荐 Tab 的"精选推荐"分区是置顶文章，已在上方展示，跳过
            if (currentTab == "推荐" && section == "精选推荐") continue

            // 添加分区标题（含差异化副标题）
            val headerView = createSectionHeader(section, getSectionSubtitle(section))
            binding.layoutContentCards.addView(headerView)

            // 添加横向滚动文章列表
            val scrollRow = createHorizontalArticleRow(sectionArticles)
            binding.layoutContentCards.addView(scrollRow)
        }
    }

    /**
     * 获取专栏差异化副标题
     */
    private fun getSectionSubtitle(section: String): String {
        return when (section) {
            "红色地标" -> "寻访津门革命遗址"
            "革命人物" -> "致敬红色先驱英魂"
            "精选推荐" -> "编辑精选不容错过"
            "热门话题" -> "大家都在看"
            "特色活动" -> "近期活动汇总"
            "沉浸体验" -> "VR/AR 带您穿越时空"
            "互动学习" -> "玩中学 学中玩"
            "专家视角" -> "学者深度解读历史"
            "青年之声" -> "学子心得感悟分享"
            "红色线路" -> "六条精品研学路线"
            "主题教育" -> "系统化学习指南"
            else -> "精彩内容"
        }
    }

    /**
     * 创建专栏分区标题（含差异化副标题）
     */
    private fun createSectionHeader(sectionName: String, subtitle: String): View {
        val dp4 = dpToPx(4)
        val dp8 = dpToPx(8)
        val dp16 = dpToPx(16)
        val dp14 = dpToPx(14)

        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp14
                bottomMargin = dp8
                marginStart = dp16
                marginEnd = dp16
            }
        }

        // 左侧红色竖线
        val indicator = View(requireContext()).apply {
            setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primary_red))
            layoutParams = LinearLayout.LayoutParams(dpToPx(3), dpToPx(18)).apply {
                marginEnd = dp8
            }
        }
        container.addView(indicator)

        // 标题 + 副标题竖排
        val titleContainer = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val title = TextView(requireContext()).apply {
            text = sectionName
            textSize = 16f
            setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary))
            setTypeface(null, android.graphics.Typeface.BOLD)
        }
        titleContainer.addView(title)

        val subtitleView = TextView(requireContext()).apply {
            text = subtitle
            textSize = 11f
            setTextColor(ContextCompat.getColor(requireContext(), R.color.text_hint))
            setPadding(0, dp4, 0, 0)
        }
        titleContainer.addView(subtitleView)

        container.addView(titleContainer)

        // 更多
        val more = TextView(requireContext()).apply {
            text = "更多 >"
            textSize = 12f
            setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary))
        }
        container.addView(more)

        return container
    }

    /**
     * 创建横向滚动的文章列表
     */
    private fun createHorizontalArticleRow(articles: List<Article>): View {
        val dp12 = dpToPx(12)
        val dp10 = dpToPx(10)

        val scrollView = HorizontalScrollView(requireContext()).apply {
            isFillViewport = true
            isHorizontalScrollBarEnabled = false
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val rowLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(dp12, 0, dp12, 0)
        }

        for (article in articles) {
            val cardView = createArticleCardView(article)
            rowLayout.addView(cardView)
        }

        scrollView.addView(rowLayout)
        return scrollView
    }

    /**
     * 创建单篇文章卡片（横向滚动中的子项）
     */
    private fun createArticleCardView(article: Article): View {
        val dp8 = dpToPx(8)
        val dp10 = dpToPx(10)
        val dp200 = dpToPx(200)
        val dp110 = dpToPx(110)

        val card = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_card_white)
            layoutParams = LinearLayout.LayoutParams(dp200, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                marginEnd = dp10
            }
        }

        // === 配图区域 ===
        val imageFrame = android.widget.FrameLayout(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp110
            )
        }

        // 图片（使用 ImageView 加载生成的 PNG 配图）
        val imageView = ImageView(requireContext()).apply {
            scaleType = ImageView.ScaleType.CENTER_CROP
            val resId = resources.getIdentifier(article.imageResName, "drawable", requireContext().packageName)
            if (resId != 0) {
                setImageResource(resId)
            } else {
                setImageResource(R.drawable.article_exhibition)
            }
        }
        imageFrame.addView(imageView)

        // 标签
        if (article.tags.isNotEmpty()) {
            val tagText = TextView(requireContext()).apply {
                text = article.tags[0]
                textSize = 10f
                setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_tag_red)
                setPadding(dp8, dpToPx(3), dp8, dpToPx(3))
                layoutParams = android.widget.FrameLayout.LayoutParams(
                    android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                    android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.TOP or Gravity.START
                ).apply {
                    topMargin = dp8
                    leftMargin = dp8
                }
            }
            imageFrame.addView(tagText)
        }

        // 阅读量
        val readsText = TextView(requireContext()).apply {
            text = formatReadCount(article.readCount)
            textSize = 10f
            setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            background = android.graphics.drawable.GradientDrawable().apply {
                setColor(0x80000000.toInt())
                cornerRadius = dpToPx(3).toFloat()
            }
            setPadding(dpToPx(6), dpToPx(2), dpToPx(6), dpToPx(2))
            layoutParams = android.widget.FrameLayout.LayoutParams(
                android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM or Gravity.END
            ).apply {
                bottomMargin = dpToPx(6)
                rightMargin = dp8
            }
        }
        imageFrame.addView(readsText)

        card.addView(imageFrame)

        // === 文字信息区域 ===
        val textContainer = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp10, dp10, dp10, dp10)
        }

        // 标题
        val title = TextView(requireContext()).apply {
            text = article.title
            textSize = 14f
            setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary))
            setTypeface(null, android.graphics.Typeface.BOLD)
            maxLines = 2
            ellipsize = android.text.TextUtils.TruncateAt.END
        }
        textContainer.addView(title)

        // 摘要
        val summary = TextView(requireContext()).apply {
            text = article.summary
            textSize = 11f
            setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary))
            maxLines = 2
            ellipsize = android.text.TextUtils.TruncateAt.END
            setPadding(0, dpToPx(4), 0, 0)
        }
        textContainer.addView(summary)

        // 作者+日期
        val infoRow = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, dpToPx(6), 0, 0)
        }

        val author = TextView(requireContext()).apply {
            text = article.author
            textSize = 10f
            setTextColor(ContextCompat.getColor(requireContext(), R.color.text_hint))
            maxLines = 1
            ellipsize = android.text.TextUtils.TruncateAt.END
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        infoRow.addView(author)

        val date = TextView(requireContext()).apply {
            text = article.publishDate
            textSize = 10f
            setTextColor(ContextCompat.getColor(requireContext(), R.color.text_hint))
        }
        infoRow.addView(date)

        textContainer.addView(infoRow)
        card.addView(textContainer)

        // 点击跳转详情
        card.setOnClickListener {
            val intent = Intent(requireContext(), ArticleDetailActivity::class.java).apply {
                putExtra(ArticleDetailActivity.EXTRA_ARTICLE_ID, article.id)
            }
            startActivity(intent)
        }

        return card
    }

    /**
     * 创建置顶文章大卡片（仅推荐 Tab 使用）
     */
    private fun createPinnedArticleView(article: Article): View {
        val dp8 = dpToPx(8)
        val dp10 = dpToPx(10)
        val dp12 = dpToPx(12)
        val dp14 = dpToPx(14)
        val dp160 = dpToPx(160)

        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_card_white)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp10
            }
        }

        // 置顶标识行
        val topRow = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp12, dp10, dp12, 0)
        }

        val pinnedTag = TextView(requireContext()).apply {
            text = "置顶"
            textSize = 10f
            setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_tag_red)
            setPadding(dp8, dpToPx(2), dp8, dpToPx(2))
        }
        topRow.addView(pinnedTag)

        val spacer = View(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(0, 0, 1f)
        }
        topRow.addView(spacer)

        val readsText = TextView(requireContext()).apply {
            text = formatReadCount(article.readCount)
            textSize = 10f
            setTextColor(ContextCompat.getColor(requireContext(), R.color.text_hint))
        }
        topRow.addView(readsText)

        container.addView(topRow)

        // 配图区域
        val imageFrame = android.widget.FrameLayout(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp160
            ).apply {
                topMargin = dpToPx(6)
            }
        }

        // 使用 ImageView 加载生成的 PNG 配图
        val imageView = ImageView(requireContext()).apply {
            scaleType = ImageView.ScaleType.CENTER_CROP
            val resId = resources.getIdentifier(article.imageResName, "drawable", requireContext().packageName)
            if (resId != 0) {
                setImageResource(resId)
            } else {
                setImageResource(R.drawable.article_exhibition)
            }
        }
        imageFrame.addView(imageView)

        // 标题浮层
        val textOverlay = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = android.widget.FrameLayout.LayoutParams(
                android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM
            )
            setPadding(dp14, dp14, dp14, dp14)
            background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_banner_overlay)
        }

        val title = TextView(requireContext()).apply {
            text = article.title
            textSize = 18f
            setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            setTypeface(null, android.graphics.Typeface.BOLD)
            setShadowLayer(3f, 1f, 1f, 0x80000000.toInt())
        }
        textOverlay.addView(title)

        val summary = TextView(requireContext()).apply {
            text = article.summary
            textSize = 12f
            setTextColor(0xE0FFFFFF.toInt())
            maxLines = 2
            ellipsize = android.text.TextUtils.TruncateAt.END
            setShadowLayer(2f, 1f, 1f, 0x80000000.toInt())
            setPadding(0, dpToPx(4), 0, 0)
        }
        textOverlay.addView(summary)

        imageFrame.addView(textOverlay)
        container.addView(imageFrame)

        // 作者信息行
        val authorRow = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp10, dp10, dp10, dp10)
        }

        val author = TextView(requireContext()).apply {
            text = article.author
            textSize = 12f
            setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary))
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        authorRow.addView(author)

        val date = TextView(requireContext()).apply {
            text = article.publishDate
            textSize = 11f
            setTextColor(ContextCompat.getColor(requireContext(), R.color.text_hint))
        }
        authorRow.addView(date)

        container.addView(authorRow)

        // 点击跳转详情
        container.setOnClickListener {
            val intent = Intent(requireContext(), ArticleDetailActivity::class.java).apply {
                putExtra(ArticleDetailActivity.EXTRA_ARTICLE_ID, article.id)
            }
            startActivity(intent)
        }

        return container
    }

    // ==================== 工具方法 ====================

    /** 格式化阅读量 */
    private fun formatReadCount(count: Int): String {
        return if (count >= 1000) {
            String.format("%.1fk阅读", count / 1000.0)
        } else {
            "${count}阅读"
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density + 0.5f).toInt()
    }

    // ==================== 签到卡片 ====================

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

    // ==================== 生命周期 ====================

    override fun onResume() {
        super.onResume()
        updateCheckInCard()
        startBannerAutoScroll()
    }

    override fun onPause() {
        super.onPause()
        stopBannerAutoScroll()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopBannerAutoScroll()
        _binding = null
    }
}
