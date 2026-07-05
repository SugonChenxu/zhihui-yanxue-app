package com.zhihui.yanxue.ui

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zhihui.yanxue.CartActivity
import com.zhihui.yanxue.ProductDetailActivity
import com.zhihui.yanxue.R
import com.zhihui.yanxue.data.CartManager
import com.zhihui.yanxue.data.MockProductRepository
import com.zhihui.yanxue.data.model.Product
import com.zhihui.yanxue.databinding.FragmentShopBinding
import com.zhihui.yanxue.util.AssetImageLoader

class ShopFragment : Fragment() {

    private var _binding: FragmentShopBinding? = null
    private val binding get() = _binding!!
    private var currentCategory = "全部"
    private lateinit var adapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShopBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCategoryTabs()
        setupRecyclerView()
        setupCartButton()
        updateCartBadge()
    }

    /** 设置分类标签 */
    private fun setupCategoryTabs() {
        binding.layoutCategories.removeAllViews()
        val categories = MockProductRepository.categories
        for (category in categories) {
            val tv = TextView(requireContext()).apply {
                text = category
                textSize = 14f
                gravity = Gravity.CENTER
                setPadding(dpToPx(16), dpToPx(8), dpToPx(16), dpToPx(8))
                if (category == currentCategory) {
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.primary_red))
                    setTypeface(null, android.graphics.Typeface.BOLD)
                } else {
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary))
                }
                setOnClickListener {
                    currentCategory = category
                    setupCategoryTabs()
                    refreshProductList()
                }
            }
            binding.layoutCategories.addView(tv)
        }
    }

    /** 设置商品列表 */
    private fun setupRecyclerView() {
        adapter = ProductAdapter(
            products = MockProductRepository.getProductsByCategory(currentCategory).toMutableList(),
            onItemClick = { product -> openProductDetail(product) },
            onAddToCart = { product -> addToCart(product) }
        )
        binding.rvProducts.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvProducts.adapter = adapter
    }

    /** 刷新商品列表 */
    private fun refreshProductList() {
        adapter.updateData(MockProductRepository.getProductsByCategory(currentCategory))
    }

    /** 打开商品详情页 */
    private fun openProductDetail(product: Product) {
        val intent = Intent(requireContext(), ProductDetailActivity::class.java)
        intent.putExtra("product_id", product.id)
        startActivity(intent)
    }

    /** 添加到购物车 */
    private fun addToCart(product: Product) {
        CartManager.addToCart(product)
        updateCartBadge()
        // 简单提示
        val tv = TextView(requireContext()).apply {
            text = "已加入购物车"
            setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            textSize = 14f
            gravity = Gravity.CENTER
            setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.gray_dark))
            setPadding(dpToPx(24), dpToPx(12), dpToPx(24), dpToPx(12))
        }
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.CENTER
        }
        tv.layoutParams = params
        // 简单的Toast效果
        android.widget.Toast.makeText(requireContext(), "已加入购物车", android.widget.Toast.LENGTH_SHORT).show()
    }

    /** 设置购物车按钮 */
    private fun setupCartButton() {
        binding.layoutCart.setOnClickListener {
            startActivity(Intent(requireContext(), CartActivity::class.java))
        }
    }

    /** 更新购物车角标 */
    private fun updateCartBadge() {
        val count = CartManager.getTotalCount()
        if (count > 0) {
            binding.tvCartBadge.visibility = View.VISIBLE
            binding.tvCartBadge.text = if (count > 99) "99+" else count.toString()
        } else {
            binding.tvCartBadge.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        updateCartBadge()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density + 0.5f).toInt()
    }

    // ==================== 商品列表 Adapter ====================

    private inner class ProductAdapter(
        private var products: MutableList<Product>,
        private val onItemClick: (Product) -> Unit,
        private val onAddToCart: (Product) -> Unit
    ) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

        fun updateData(newProducts: List<Product>) {
            products.clear()
            products.addAll(newProducts)
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_product_card, parent, false)
            return ProductViewHolder(view)
        }

        override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
            holder.bind(products[position])
        }

        override fun getItemCount(): Int = products.size

        inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val ivImage: android.widget.ImageView = itemView.findViewById(R.id.ivProductImage)
            private val tvName: TextView = itemView.findViewById(R.id.tvProductName)
            private val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
            private val tvOriginalPrice: TextView = itemView.findViewById(R.id.tvOriginalPrice)
            private val tvSales: TextView = itemView.findViewById(R.id.tvSalesCount)
            private val layoutTags: LinearLayout = itemView.findViewById(R.id.layoutTags)

            fun bind(product: Product) {
                // 加载 assets 图片
                val bitmap = AssetImageLoader.loadBitmap(itemView.context, product.imagePath)
                if (bitmap != null) {
                    ivImage.setImageBitmap(bitmap)
                }

                tvName.text = product.name
                tvPrice.text = "¥${product.price}"
                tvSales.text = "已售 ${product.salesCount}"

                // 原价（划线）
                if (product.originalPrice != null) {
                    tvOriginalPrice.visibility = View.VISIBLE
                    tvOriginalPrice.text = "¥${product.originalPrice}"
                    tvOriginalPrice.paintFlags = tvOriginalPrice.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    tvOriginalPrice.visibility = View.GONE
                }

                // 标签
                layoutTags.removeAllViews()
                for (tag in product.tags) {
                    val tagView = TextView(itemView.context).apply {
                        text = tag
                        textSize = 10f
                        setTextColor(ContextCompat.getColor(itemView.context, R.color.primary_red))
                        background = ContextCompat.getDrawable(itemView.context, R.drawable.bg_tag_red)
                        setPadding(dpToPx(6), dpToPx(2), dpToPx(6), dpToPx(2))
                        val params = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            marginEnd = dpToPx(4)
                        }
                        layoutParams = params
                    }
                    layoutTags.addView(tagView)
                }

                // 点击打开详情
                itemView.setOnClickListener { onItemClick(product) }
            }
        }
    }
}
