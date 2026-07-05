package com.zhihui.yanxue.data.model

/**
 * 文章数据模型
 * 用于首页各专栏展示的图文文章
 */
data class Article(
    val id: String,
    val title: String,
    val summary: String,          // 摘要（卡片展示用）
    val content: String,          // 正文（详情页展示用）
    val tab: String,              // 所属Tab：学习/推荐/探索/观点/专题
    val section: String,          // 所属专栏分区名称
    val imageResName: String,     // drawable图片资源名（占位渐变背景）
    val author: String,
    val publishDate: String,
    val readCount: Int,
    val isPinned: Boolean = false, // 是否置顶（仅推荐Tab使用）
    val tags: List<String> = emptyList()
)
