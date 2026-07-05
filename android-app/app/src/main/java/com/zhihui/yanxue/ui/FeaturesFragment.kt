package com.zhihui.yanxue.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.zhihui.yanxue.R
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
        // TODO: 设置特色功能卡片
        // 1. VR体验 - 概念展示
        // 2. 直播微课 - 占位
        // 3. AR寻宝 - 概念展示
        // 4. 红色剧本杀 - 概念展示
        // 5. 课程地图 - 红色地标分布
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
