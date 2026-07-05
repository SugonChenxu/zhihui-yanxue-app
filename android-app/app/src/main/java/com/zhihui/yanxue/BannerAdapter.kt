package com.zhihui.yanxue

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zhihui.yanxue.data.model.Banner

/**
 * Banner轮播适配器
 * 用于首页ViewPager2展示5张轮播Banner
 *
 * 点击Banner跳转对应文章详情页
 */
class BannerAdapter(
    private val banners: List<Banner>,
    private val onItemClick: (Banner) -> Unit
) : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

    /**
     * 无限轮播：返回Int.MAX_VALUE使ViewPager2可无限滑动
     * 实际位置 = position % banners.size
     */
    override fun getItemCount(): Int = if (banners.isEmpty()) 0 else Int.MAX_VALUE

    /** 获取真实数据索引 */
    fun getRealPosition(position: Int): Int = if (banners.isEmpty()) 0 else position % banners.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_banner, parent, false)
        return BannerViewHolder(view)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        val realPos = getRealPosition(position)
        val banner = banners[realPos]
        holder.bind(banner)
    }

    inner class BannerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgBanner: ImageView = itemView.findViewById(R.id.img_banner)
        private val txtTitle: TextView = itemView.findViewById(R.id.txt_banner_title)
        private val txtSubtitle: TextView = itemView.findViewById(R.id.txt_banner_subtitle)

        fun bind(banner: Banner) {
            // 加载drawable图片资源
            val resId = itemView.context.resources.getIdentifier(
                banner.imageResName, "drawable", itemView.context.packageName
            )
            if (resId != 0) {
                imgBanner.setImageResource(resId)
            }

            txtTitle.text = banner.title
            txtSubtitle.text = banner.subtitle

            itemView.setOnClickListener {
                onItemClick(banner)
            }
        }
    }
}
