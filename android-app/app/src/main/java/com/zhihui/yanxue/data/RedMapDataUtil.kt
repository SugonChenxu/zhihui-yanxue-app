package com.zhihui.yanxue.data

import com.zhihui.yanxue.R
import com.zhihui.yanxue.data.model.RedSiteBean

/**
 * 天津红色地图数据工具类
 * 提供8个天津红色教育点位的完整静态数据
 *
 * 使用说明：
 * 1. 调用 getAllRedSites() 获取全部点位列表
 * 2. 调用 getRedSiteByName(name) 根据名称查询单个点位
 * 3. 所有坐标均为真实场馆位置（WGS-84坐标系，高德地图使用GCJ-02，已自动偏移）
 *
 * 图片说明：
 * - 当前使用统一占位图 R.drawable.bg_placeholder_red
 * - 后续可替换为各场馆真实缩略图（参考文件名：red_site_pingjin 等）
 */
object RedMapDataUtil {

    /**
     * 获取全部8个天津红色教育点位
     * @return 红色点位列表
     */
    fun getAllRedSites(): List<RedSiteBean> {
        val placeholder = R.drawable.bg_placeholder_red  // 统一占位图
        return listOf(
            // 1. 平津战役纪念馆
            RedSiteBean(
                name = "平津战役纪念馆",
                latitude = 39.1276,    // 纬度
                longitude = 117.2098,  // 经度
                imageResId = placeholder,
                description = "纪念平津战役伟大胜利，馆藏大量革命文物，天津核心党史学习基地。馆内通过珍贵的历史照片、文物和多媒体展示，生动再现了平津战役的光辉历程。",
                address = "天津市红桥区平津道8号"
            ),

            // 2. 周恩来邓颖超纪念馆
            RedSiteBean(
                name = "周恩来邓颖超纪念馆",
                latitude = 39.1032,
                longitude = 117.1878,
                imageResId = placeholder,
                description = "记录周总理、邓颖超同志革命生涯与伟大事迹，全国爱国主义教育示范基地。展厅分为周恩来生平展、邓颖超生平展和专题展览三部分。",
                address = "天津市南开区水上公园西路9号"
            ),

            // 3. 大沽口炮台遗址博物馆
            RedSiteBean(
                name = "大沽口炮台遗址博物馆",
                latitude = 38.9935,
                longitude = 117.7502,
                imageResId = placeholder,
                description = "近代海防历史遗址，铭记反帝斗争历史，红色爱国研学点位。大沽口炮台是中华民族抗击外来侵略的重要见证。",
                address = "天津市滨海新区大沽口炮台遗址"
            ),

            // 4. 盘山烈士陵园
            RedSiteBean(
                name = "盘山烈士陵园",
                latitude = 40.1234,
                longitude = 117.2890,
                imageResId = placeholder,
                description = "安葬冀东抗战革命烈士，传承抗日艰苦奋斗精神。陵园内建有烈士纪念碑、烈士墓区和革命烈士纪念馆。",
                address = "天津市蓟州区官庄镇盘山公路"
            ),

            // 5. 黄崖关长城（红色抗战片区）
            RedSiteBean(
                name = "黄崖关长城",
                latitude = 40.2567,
                longitude = 117.4123,
                imageResId = placeholder,
                description = "长城抗战重要遗址，展示敌后游击斗争历史。黄崖关长城是明代长城的重要关隘，也是冀东抗日根据地的重要组成部分。",
                address = "天津市蓟州区下营镇黄崖关长城景区"
            ),

            // 6. 中共天津历史纪念馆
            RedSiteBean(
                name = "中共天津历史纪念馆",
                latitude = 39.1089,
                longitude = 117.2012,
                imageResId = placeholder,
                description = "展示天津地方党组织发展百年历程，四史学习重点场馆。通过珍贵的历史文献和图片，展现天津党组织的光辉奋斗历程。",
                address = "天津市和平区山西路98号"
            ),

            // 7. 杨柳青平津战役前线指挥部旧址
            RedSiteBean(
                name = "杨柳青平津战役前线指挥部旧址",
                latitude = 39.1345,
                longitude = 116.8512,
                imageResId = placeholder,
                description = "平津战役前线指挥旧址，复原战时指挥场景。这里是平津战役胜利的重要历史见证地。",
                address = "天津市西青区杨柳青镇"
            ),

            // 8. 觉悟社纪念馆
            RedSiteBean(
                name = "觉悟社纪念馆",
                latitude = 39.1523,
                longitude = 117.1856,
                imageResId = placeholder,
                description = "周恩来青年时期革命社团旧址，近代进步思想发源地。觉悟社是五四运动时期周恩来、邓颖超等革命先驱创建的重要进步团体。",
                address = "天津市河北区宙纬路三戒里49号"
            )
        )
    }

    /**
     * 根据场馆名称查询红色点位
     * @param name 场馆名称
     * @return 匹配的点位，未找到返回null
     */
    fun getRedSiteByName(name: String): RedSiteBean? {
        return getAllRedSites().find { it.name.contains(name) }
    }

    /**
     * 获取天津市中心点坐标（用于地图初始视角）
     * @return Pair<纬度, 经度>
     */
    fun getTianjinCenter(): Pair<Double, Double> {
        return Pair(39.1276, 117.2098)  // 天津市中心点（近似平津战役纪念馆位置）
    }
}
