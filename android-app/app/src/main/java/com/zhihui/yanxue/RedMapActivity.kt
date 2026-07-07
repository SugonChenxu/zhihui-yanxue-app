package com.zhihui.yanxue

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.CoordinateConverter
import com.amap.api.maps.MapView
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Marker
import com.amap.api.maps.model.MarkerOptions
import com.zhihui.yanxue.data.RedMapDataUtil
import com.zhihui.yanxue.data.model.RedSiteBean
import com.zhihui.yanxue.databinding.ActivityRedMapBinding
import java.util.Locale

/**
 * 天津红色地图Activity
 * 功能：展示天津8个红色教育点位，支持搜索、列表跳转、夜间模式
 */
class RedMapActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRedMapBinding
    private lateinit var aMap: AMap
    private lateinit var mapView: MapView
    private val redSites = mutableListOf<RedSiteBean>()
    private val markerMap = mutableMapOf<Marker, RedSiteBean>()  // Marker与点位数据映射
    private var isNightMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRedMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 初始化地图
        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        initMap()

        // 加载红色点位数据
        loadRedSites()

        // 设置搜索框
        setupSearch()

        // 设置侧边列表
        setupSideList()

        // 设置按钮事件
        setupButtons()
    }

    /**
     * 初始化高德地图
     */
    private fun initMap() {
        aMap = mapView.map

        // 设置天津市中心点（近似坐标）
        val tianjinCenter = LatLng(39.1276, 117.2098)
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tianjinCenter, 10f))

        // 开启定位蓝点（如果有权限）
        aMap.isMyLocationEnabled = false  // 暂不开启，避免权限问题
    }

    /**
     * 加载红色点位数据并添加到地图
     */
    private fun loadRedSites() {
        redSites.addAll(RedMapDataUtil.getAllRedSites())

        for (site in redSites) {
            // 坐标转换：WGS84 -> GCJ02（高德地图使用GCJ02）
            val latLng = convertCoord(site.latitude, site.longitude)

            // 创建Marker选项
            val markerOption = MarkerOptions()
                .position(latLng)
                .title(site.name)
                .snippet(site.description)
                .icon(BitmapDescriptorFactory.fromResource(site.imageResId))  // 使用场馆缩略图作为Marker图标
                .anchor(0.5f, 0.5f)  // 中心点对齐

            // 添加Marker到地图
            val marker = aMap.addMarker(markerOption)
            if (marker != null) {
                markerMap[marker] = site
            }
        }

        // 设置Marker点击事件
        aMap.setOnMarkerClickListener { marker ->
            showSiteDetail(markerMap[marker])
            true  // 返回true表示消费事件，不显示默认信息窗
        }
    }

    /**
     * 坐标转换：WGS84 -> GCJ02
     * 高德地图使用GCJ02坐标系
     */
    private fun convertCoord(lat: Double, lng: Double): LatLng {
        val converter = CoordinateConverter(this)
        converter.from(CoordinateConverter.CoordType.GPS)  // WGS84
        converter.coord(LatLng(lat, lng))
        return converter.convert() ?: LatLng(lat, lng)
    }

    /**
     * 显示点位详情底部弹窗
     */
    private fun showSiteDetail(site: RedSiteBean?) {
        site?.let {
            binding.layoutDetailSheet.visibility = View.VISIBLE
            binding.imgSiteDetail.setImageResource(it.imageResId)
            binding.tvSiteName.text = it.name
            binding.tvSiteAddress.text = it.address
            binding.tvSiteDescription.text = it.description

            // 移动地图视角到该点位
            val latLng = convertCoord(it.latitude, it.longitude)
            aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

            // 显示"返回全市"按钮
            binding.btnResetView.visibility = View.VISIBLE
        }
    }

    /**
     * 设置搜索框功能
     */
    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val keyword = s.toString().trim()
                if (keyword.isNotEmpty()) {
                    binding.btnClearSearch.visibility = View.VISIBLE
                    searchSite(keyword)
                } else {
                    binding.btnClearSearch.visibility = View.GONE
                    resetMapView()
                }
            }

            override fun afterTextChanged(s: android.text.Editable?) {}
        })

        binding.btnClearSearch.setOnClickListener {
            binding.etSearch.text.clear()
            resetMapView()
        }
    }

    /**
     * 搜索点位（根据名称模糊匹配）
     */
    private fun searchSite(keyword: String) {
        val matchedSite = redSites.find { it.name.contains(keyword) }
        if (matchedSite != null) {
            showSiteDetail(matchedSite)
        }
    }

    /**
     * 重置地图视角到天津市全域
     */
    private fun resetMapView() {
        val tianjinCenter = LatLng(39.1276, 117.2098)
        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(tianjinCenter, 10f))
        binding.layoutDetailSheet.visibility = View.GONE
        binding.btnResetView.visibility = View.GONE
    }

    /**
     * 设置侧边点位列表
     */
    private fun setupSideList() {
        val adapter = RedSiteListAdapter(redSites) { site ->
            showSiteDetail(site)
            binding.layoutSideList.visibility = View.GONE  // 点击后隐藏侧边列表
        }
        binding.rvRedSites.layoutManager = LinearLayoutManager(this)
        binding.rvRedSites.adapter = adapter

        // 切换侧边列表显示/隐藏
        binding.btnToggleList.setOnClickListener {
            binding.layoutSideList.visibility = if (binding.layoutSideList.visibility == View.VISIBLE) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }
    }

    /**
     * 设置按钮事件
     */
    private fun setupButtons() {
        // 返回全市按钮
        binding.btnResetView.setOnClickListener {
            resetMapView()
        }

        // 夜间模式切换按钮
        binding.btnToggleNight.setOnClickListener {
            isNightMode = !isNightMode
            if (isNightMode) {
                aMap.mapType = AMap.MAP_TYPE_NIGHT  // 夜间模式
                binding.btnToggleNight.setImageResource(R.drawable.ic_day)
            } else {
                aMap.mapType = AMap.MAP_TYPE_NORMAL  // 正常模式
                binding.btnToggleNight.setImageResource(R.drawable.ic_night)
            }
        }

        // 关闭详情弹窗按钮
        binding.btnCloseDetail.setOnClickListener {
            binding.layoutDetailSheet.visibility = View.GONE
        }
    }

    // ----------- 生命周期方法（必须调用，否则地图无法正常显示）-----------
    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }
}

/**
 * 侧边红色点位列表适配器
 */
class RedSiteListAdapter(
    private val sites: List<RedSiteBean>,
    private val onItemClick: (RedSiteBean) -> Unit
) : RecyclerView.Adapter<RedSiteListAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgThumb: ImageView = itemView.findViewById(R.id.img_site_thumb)
        val tvName: TextView = itemView.findViewById(R.id.tv_site_name)
        val tvAddress: TextView = itemView.findViewById(R.id.tv_site_address)
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.item_red_site, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val site = sites[position]
        holder.imgThumb.setImageResource(site.imageResId)
        holder.tvName.text = site.name
        holder.tvAddress.text = site.address
        holder.itemView.setOnClickListener {
            onItemClick(site)
        }
    }

    override fun getItemCount(): Int = sites.size
}
