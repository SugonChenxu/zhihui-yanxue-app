package com.zhihui.yanxue.data

import com.zhihui.yanxue.data.model.Course

object MockCourseRepository {

    val courses: List<Course> = listOf(
        Course("c001", "平津战役纪念馆", "位于天津市红桥区，全面反映平津战役历史的专题纪念馆，陈列大量珍贵文物和历史照片。", "地标", "初高中生", "course_memorial", duration = 45, studentCount = 1280, rating = 4.8f, tags = listOf("纪念馆", "红色地标")),
        Course("c002", "周恩来邓颖超纪念馆", "位于天津市南开区，全面展示周恩来、邓颖超两位伟人的光辉一生和丰功伟绩。", "地标", "大学生", "course_zhou_deng", duration = 50, studentCount = 2156, rating = 4.9f, tags = listOf("伟人", "纪念馆")),
        Course("c003", "盘山烈士陵园", "位于天津市蓟州区盘山，是冀东地区最大的烈士陵园，安葬着众多革命烈士。", "地标", "小学生", "course_cemetery", duration = 30, studentCount = 890, rating = 4.7f, tags = listOf("烈士陵园", "爱国主义")),
        // 人物
        Course("c004", "于方舟烈士事迹", "于方舟是天津早期党组织领导人之一，为天津革命事业作出重要贡献。", "人物", "大学生", "course_martyr", duration = 40, studentCount = 670, rating = 4.6f, tags = listOf("烈士", "天津党组织")),
        Course("c005", "吉鸿昌将军", "抗日民族英雄吉鸿昌将军的英勇事迹，再现其坚定信念和爱国情怀。", "人物", "初高中生", "course_ji_hongchang", duration = 35, studentCount = 1020, rating = 4.8f, tags = listOf("抗日英雄", "将军")),
        // 文物
        Course("c006", "《天津学生联合会报》", "五四运动时期天津学生运动的重要刊物，反映了青年学生的爱国热情。", "文物", "大学生", "course_student_news", duration = 25, studentCount = 540, rating = 4.5f, tags = listOf("五四运动", "文物")),
        Course("c007", "平津战役前线指挥所电话", "平津战役期间使用过的通讯设备，见证了历史关键时刻。", "文物", "初高中生", "course_phone", duration = 20, studentCount = 430, rating = 4.4f, tags = listOf("平津战役", "文物")),
        // 事件
        Course("c008", "天津解放", "1949年1月15日天津解放的历史过程，讲述解放军英勇作战的故事。", "事件", "初高中生", "course_tianjin_liberation", duration = 40, studentCount = 1560, rating = 4.9f, tags = listOf("天津解放", "解放战争")),
        Course("c009", "五四运动在天津", "五四运动期间天津学生的爱国行动，包括罢课、游行等活动。", "事件", "大学生", "course_may_fourth", duration = 35, studentCount = 890, rating = 4.7f, tags = listOf("五四运动", "学生运动"))
    )

    fun getCoursesByCategory(category: String): List<Course> {
        return if (category == "全部") courses else courses.filter { it.category == category }
    }

    fun getCourseById(id: String): Course? {
        return courses.find { it.id == id }
    }

    val categories: List<String> = listOf("地标", "人物", "文物", "事件")
}
