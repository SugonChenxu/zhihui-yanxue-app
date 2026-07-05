package com.zhihui.yanxue.data

import com.zhihui.yanxue.data.model.CartItem
import com.zhihui.yanxue.data.model.Product
import java.math.BigDecimal

/**
 * 购物车管理器（全局单例）
 * 对接后端时替换为 API 调用
 */
object CartManager {

    private val cartItems = mutableListOf<CartItem>()

    /** 购物车变更监听器 */
    var onCartChanged: (() -> Unit)? = null

    /** 获取购物车列表 */
    fun getCartItems(): List<CartItem> = cartItems.toList()

    /** 添加商品到购物车 */
    fun addToCart(product: Product, quantity: Int = 1): Boolean {
        val existingItem = cartItems.find { it.product.id == product.id }
        return if (existingItem != null) {
            // 已存在则增加数量
            existingItem.quantity += quantity
            onCartChanged?.invoke()
            false
        } else {
            // 不存在则新增
            cartItems.add(CartItem(product, quantity))
            onCartChanged?.invoke()
            true
        }
    }

    /** 从购物车移除商品 */
    fun removeFromCart(productId: String) {
        cartItems.removeAll { it.product.id == productId }
        onCartChanged?.invoke()
    }

    /** 修改商品数量 */
    fun updateQuantity(productId: String, quantity: Int) {
        val item = cartItems.find { it.product.id == productId }
        if (item != null) {
            if (quantity <= 0) {
                removeFromCart(productId)
            } else {
                item.quantity = quantity
                onCartChanged?.invoke()
            }
        }
    }

    /** 获取购物车商品总数量 */
    fun getTotalCount(): Int {
        return cartItems.sumOf { it.quantity }
    }

    /** 获取购物车总金额 */
    fun getTotalAmount(): BigDecimal {
        return cartItems.fold(BigDecimal.ZERO) { acc, item ->
            acc.add(item.totalPrice)
        }
    }

    /** 清空购物车 */
    fun clearCart() {
        cartItems.clear()
        onCartChanged?.invoke()
    }

    /** 判断商品是否已在购物车中 */
    fun isInCart(productId: String): Boolean {
        return cartItems.any { it.product.id == productId }
    }
}
