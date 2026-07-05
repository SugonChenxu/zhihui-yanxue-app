package com.zhihui.yanxue.data.model

/**
 * 天津红色教育点位实体类
 * 用于红色地图模块展示天津红色场馆位置信息
 *
 * @param name 场馆名称
 * @param latitude 纬度
 * @param longitude 经度
 * @param imageResId 场馆缩略图资源ID
 * @param description 场馆红色历史简介
 * @param address 场馆详细地址
 */
data class RedSiteBean(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val imageResId: Int,
    val description: String,
    val address: String
) {
    override fun toString(): String {
        return "RedSiteBean(name='$name', lat=$latitude, lng=$longitude)"
    }
}
