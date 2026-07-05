package com.zhihui.yanxue

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zhihui.yanxue.data.CartManager
import com.zhihui.yanxue.data.model.CartItem
import com.zhihui.yanxue.databinding.ActivityCartBinding
import com.zhihui.yanxue.util.AssetImageLoader

class CartActivity : BaseActivity() {

    private lateinit var binding: ActivityCartBinding
    private lateinit var adapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        refreshCartData()
    }

    private fun setupUI() {
        // 返回
        binding.btnBack.setOnClickListener { finish() }

        // 清空购物车
        binding.btnClear.setOnClickListener {
            if (CartManager.getCartItems().isEmpty()) {
                Toast.makeText(this, "购物车已经是空的", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            android.app.AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("确定要清空购物车吗？")
                .setPositiveButton("确定") { _, _ ->
                    CartManager.clearCart()
                    refreshCartData()
                    Toast.makeText(this, "购物车已清空", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("取消", null)
                .show()
        }

        // 去逛逛
        binding.btnGoShopping.setOnClickListener { finish() }

        // 结算
        binding.btnCheckout.setOnClickListener {
            val count = CartManager.getTotalCount()
            val amount = CartManager.getTotalAmount()
            android.app.AlertDialog.Builder(this)
                .setTitle("确认订单")
                .setMessage("共 ${count} 件商品\n合计：¥${amount}\n\n（模拟下单，公益项目暂不支持真实支付）")
                .setPositiveButton("确认下单") { _, _ ->
                    CartManager.clearCart()
                    refreshCartData()
                    Toast.makeText(this, "下单成功！", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("取消", null)
                .show()
        }

        // 购物车变更监听
        CartManager.onCartChanged = { refreshCartData() }
    }

    private fun refreshCartData() {
        val cartItems = CartManager.getCartItems()

        if (cartItems.isEmpty()) {
            // �购物车
            binding.rvCart.visibility = View.GONE
            binding.layoutEmpty.visibility = View.VISIBLE
            binding.layoutBottomBar.visibility = View.GONE
        } else {
            // 有商品
            binding.rvCart.visibility = View.VISIBLE
            binding.layoutEmpty.visibility = View.GONE
            binding.layoutBottomBar.visibility = View.VISIBLE

            // 更新列表
            adapter = CartAdapter(cartItems.toMutableList())
            binding.rvCart.layoutManager = LinearLayoutManager(this)
            binding.rvCart.adapter = adapter

            // 更新总价
            binding.tvTotalAmount.text = "¥${CartManager.getTotalAmount()}"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        CartManager.onCartChanged = null
    }

    // ==================== 购物车 Adapter ====================

    private inner class CartAdapter(
        private val items: MutableList<CartItem>
    ) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): CartViewHolder {
            val view = android.view.LayoutInflater.from(parent.context)
                .inflate(R.layout.item_cart, parent, false)
            return CartViewHolder(view)
        }

        override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
            holder.bind(items[position], position)
        }

        override fun getItemCount(): Int = items.size

        inner class CartViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
            private val ivImage: android.widget.ImageView = itemView.findViewById(R.id.ivCartImage)
            private val tvName: android.widget.TextView = itemView.findViewById(R.id.tvCartName)
            private val tvCategory: android.widget.TextView = itemView.findViewById(R.id.tvCartCategory)
            private val tvPrice: android.widget.TextView = itemView.findViewById(R.id.tvCartPrice)
            private val tvQuantity: android.widget.TextView = itemView.findViewById(R.id.tvQuantity)
            private val btnMinus: android.widget.TextView = itemView.findViewById(R.id.btnMinus)
            private val btnPlus: android.widget.TextView = itemView.findViewById(R.id.btnPlus)
            private val btnDelete: android.widget.ImageView = itemView.findViewById(R.id.btnDelete)

            fun bind(item: CartItem, position: Int) {
                val product = item.product

                // 加载图片
                val bitmap = AssetImageLoader.loadBitmap(itemView.context, product.imagePath)
                if (bitmap != null) {
                    ivImage.setImageBitmap(bitmap)
                }

                tvName.text = product.name
                tvCategory.text = product.category
                tvPrice.text = "¥${product.price}"
                tvQuantity.text = item.quantity.toString()

                // 减少数量
                btnMinus.setOnClickListener {
                    CartManager.updateQuantity(product.id, item.quantity - 1)
                    refreshCartData()
                }

                // 增加数量
                btnPlus.setOnClickListener {
                    CartManager.updateQuantity(product.id, item.quantity + 1)
                    refreshCartData()
                }

                // 删除
                btnDelete.setOnClickListener {
                    android.app.AlertDialog.Builder(itemView.context)
                        .setTitle("提示")
                        .setMessage("确定要删除「${product.name}」吗？")
                        .setPositiveButton("删除") { _, _ ->
                            CartManager.removeFromCart(product.id)
                            refreshCartData()
                        }
                        .setNegativeButton("取消", null)
                        .show()
                }
            }
        }
    }
}
