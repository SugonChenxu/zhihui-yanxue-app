package com.zhihui.yanxue.data.model

/**
 * 课程数据模型
 */
data class Course(
    val id: String,
    val title: String,
    val description: String,
    val category: String, // 地标/人物/文物/事件
    val ageGroup: String, // 小学生/初高中生/大学生/留学生
    val imagePath: String,
    val videoUrl: String? = null,
    val duration: Int = 0, // 时长（分钟）
    val studentCount: Int = 0, // 学习人数
    val rating: Float = 0f,
    val isFree: Boolean = true,
    val price: Double = 0.0,
    val tags: List<String> = emptyList()
)
