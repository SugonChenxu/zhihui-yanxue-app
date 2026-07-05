package com.zhihui.yanxue.data.model

import java.math.BigDecimal

/**
 * 商品数据模型
 */
data class Product(
    val id: String,
    val name: String,
    val price: BigDecimal,
    val originalPrice: BigDecimal? = null,
    val imagePath: String,
    val description: String,
    val category: String,
    val tags: List<String> = emptyList(),
    val salesCount: Int = 0,
    val stock: Int = 100,
    val details: List<String> = emptyList()
)

/**
 * 购物车商品项
 */
data class CartItem(
    val product: Product,
    var quantity: Int = 1
) {
    val totalPrice: BigDecimal
        get() = product.price.multiply(BigDecimal(quantity))
}

/**
 * 订单数据模型
 */
data class Order(
    val id: String,
    val items: List<CartItem>,
    val totalAmount: BigDecimal,
    val createTime: Long,
    val status: OrderStatus
)

/**
 * 订单状态
 */
enum class OrderStatus {
    PENDING_PAYMENT,  // 待付款
    PAID,             // 已付款
    SHIPPED,          // 已发货
    COMPLETED,        // 已完成
    CANCELLED         // 已取消
}
