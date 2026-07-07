package com.zhihui.yanxue.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.zhihui.yanxue.R
import com.zhihui.yanxue.RedMapActivity
import com.zhihui.yanxue.databinding.FragmentFeaturesBinding

class FeaturesFragment : Fragment() {

    private var _binding: FragmentFeaturesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeaturesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFeatureCards()
    }

    private fun setupFeatureCards() {
        // 1. VR体验
        setupFeature(
            card = binding.featureVr.root,
            icon = R.drawable.ic_vr,
            title = "VR体验",
            desc = "身临其境感受红色场馆，沉浸式学习革命历史",
            onClick = { /* TODO: 跳转VR体验页面 */ }
        )

        // 2. 直播微课
        setupFeature(
            card = binding.featureLive.root,
            icon = R.drawable.ic_live,
            title = "直播微课",
            desc = "名师在线直播授课，随时随地学党史知国情",
            onClick = { /* TODO: 跳转直播微课页面 */ }
        )

        // 3. AR寻宝
        setupFeature(
            card = binding.featureAr.root,
            icon = R.drawable.ic_ar,
            title = "AR寻宝",
            desc = "AR互动探索红色文物，趣味学习寓教于乐",
            onClick = { /* TODO: 跳转AR寻宝页面 */ }
        )

        // 4. 红色剧本杀
        setupFeature(
            card = binding.featureScriptKill.root,
            icon = R.drawable.ic_script,
            title = "红色剧本杀",
            desc = "扮演革命角色体验历史，深度参与红色故事演绎",
            onClick = { /* TODO: 跳转红色剧本杀页面 */ }
        )

        // 5. 红色地图（天津红色地标分布）
        setupFeature(
            card = binding.featureRedMap.root,
            icon = R.drawable.ic_map,
            title = "红色地图",
            desc = "天津红色教育点位导航，探索津门革命足迹",
            onClick = {
                val intent = Intent(requireContext(), RedMapActivity::class.java)
                startActivity(intent)
            }
        )
    }

    /**
     * 设置单个功能卡片的内容和点击事件
     */
    private fun setupFeature(
        card: View,
        icon: Int,
        title: String,
        desc: String,
        onClick: () -> Unit
    ) {
        val ivIcon = card.findViewById<ImageView>(R.id.ivFeatureIcon)
        val tvTitle = card.findViewById<TextView>(R.id.tvFeatureTitle)
        val tvDesc = card.findViewById<TextView>(R.id.tvFeatureDesc)

        ivIcon.setImageResource(icon)
        tvTitle.text = title
        tvDesc.text = desc
        card.setOnClickListener { onClick() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
