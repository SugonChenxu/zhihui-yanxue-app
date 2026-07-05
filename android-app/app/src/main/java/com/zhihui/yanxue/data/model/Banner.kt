package com.zhihui.yanxue.data.model

/**
 * Banner轮播数据模型
 * 用于首页顶部ViewPager2轮播展示
 */
data class Banner(
    val id: String,
    val imageResName: String,   // drawable图片资源名
    val title: String,          // 主标题
    val subtitle: String,       // 副标题
    val articleId: String       // 点击跳转的文章ID
)
