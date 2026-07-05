package com.zhihui.yanxue

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.zhihui.yanxue.data.CartManager
import com.zhihui.yanxue.data.MockProductRepository
import com.zhihui.yanxue.data.model.Product
import com.zhihui.yanxue.databinding.ActivityProductDetailBinding
import com.zhihui.yanxue.util.AssetImageLoader

class ProductDetailActivity : BaseActivity() {

    private lateinit var binding: ActivityProductDetailBinding
    private var product: Product? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 获取商品ID
        val productId = intent.getStringExtra("product_id") ?: ""
        product = MockProductRepository.getProductById(productId)

        if (product == null) {
            Toast.makeText(this, "商品不存在", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupUI()
        setupButtons()
    }

    private fun setupUI() {
        val p = product!!

        // 返回按钮
        binding.btnBack.setOnClickListener { finish() }

        // 加载商品图片
        val bitmap = AssetImageLoader.loadBitmap(this, p.imagePath)
        if (bitmap != null) {
            binding.ivProductImage.setImageBitmap(bitmap)
        }

        // 价格
        binding.tvPrice.text = "¥${p.price}"
        binding.tvSalesCount.text = "已售 ${p.salesCount}"

        // 原价
        if (p.originalPrice != null) {
            binding.tvOriginalPrice.visibility = View.VISIBLE
            binding.tvOriginalPrice.text = "¥${p.originalPrice}"
            binding.tvOriginalPrice.paintFlags = binding.tvOriginalPrice.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            binding.tvOriginalPrice.visibility = View.GONE
        }

        // 商品名称
        binding.tvProductName.text = p.name

        // 标签
        binding.layoutTags.removeAllViews()
        for (tag in p.tags) {
            val tagView = TextView(this).apply {
                text = tag
                textSize = 11f
                setTextColor(ContextCompat.getColor(this@ProductDetailActivity, R.color.primary_red))
                background = ContextCompat.getDrawable(this@ProductDetailActivity, R.drawable.bg_tag_red)
                setPadding(dpToPx(8), dpToPx(2), dpToPx(8), dpToPx(2))
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    marginEnd = dpToPx(8)
                }
                layoutParams = params
            }
            binding.layoutTags.addView(tagView)
        }

        // 商品描述
        binding.tvDescription.text = p.description

        // 规格参数
        binding.layoutDetails.removeAllViews()
        for (detail in p.details) {
            val tv = TextView(this).apply {
                text = "· $detail"
                textSize = 13f
                setTextColor(ContextCompat.getColor(this@ProductDetailActivity, R.color.text_secondary))
                setPadding(0, dpToPx(4), 0, dpToPx(4))
            }
            binding.layoutDetails.addView(tv)
        }
    }

    private fun setupButtons() {
        val p = product ?: return

        // 加入购物车
        binding.btnAddToCart.setOnClickListener {
            CartManager.addToCart(p)
            Toast.makeText(this, "已加入购物车", Toast.LENGTH_SHORT).show()
        }

        // 立即购买（直接加入购物车并跳转购物车页）
        binding.btnBuyNow.setOnClickListener {
            CartManager.addToCart(p)
            val intent = android.content.Intent(this, CartActivity::class.java)
            startActivity(intent)
            finish()
        }

        // 购物车
        binding.btnGoCart.setOnClickListener {
            val intent = android.content.Intent(this, CartActivity::class.java)
            startActivity(intent)
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density + 0.5f).toInt()
    }
}
