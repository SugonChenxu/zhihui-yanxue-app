package com.zhihui.yanxue.data

import com.zhihui.yanxue.data.model.Product
import java.math.BigDecimal

/**
 * Mock 商品数据仓库
 * 对接后端时替换为 API 调用即可
 */
object MockProductRepository {

    /** 全部商品列表（对应 assets/images 中的6张图片） */
    val products: List<Product> = listOf(
        Product(
            id = "1",
            name = "核心价值观拼图·建党百年",
            price = BigDecimal("58.00"),
            originalPrice = BigDecimal("88.00"),
            imagePath = "images/product_corevalues_puzzle.png",
            description = "以建党百年历史事件为主题的核心价值观拼图，精选1920-1982年间重要历史时刻，寓教于乐，在拼图过程中感悟红色精神。",
            category = "拼图系列",
            tags = listOf("热销", "限量"),
            salesCount = 326,
            stock = 50,
            details = listOf(
                "材质：优质环保纸板",
                "规格：500片装，完成后尺寸38×26cm",
                "适用年龄：8岁以上",
                "附赠：建党百年历史大事记手册一份"
            )
        ),
        Product(
            id = "2",
            name = "红色文化帆布袋",
            price = BigDecimal("29.90"),
            originalPrice = BigDecimal("49.90"),
            imagePath = "images/product_totebag.png",
            description = "纯棉帆布袋，印有\u201c壹佰年 1921-2021\u201d纪念标识，结实耐用，环保时尚，是日常出行、学习的理想选择。",
            category = "生活用品",
            tags = listOf("新品"),
            salesCount = 512,
            stock = 200,
            details = listOf(
                "材质：12安纯棉帆布",
                "规格：35×40cm，手提带长30cm",
                "工艺：丝网印刷，不掉色",
                "承重：约10kg"
            )
        ),
        Product(
            id = "3",
            name = "磁吸冰箱贴套装",
            price = BigDecimal("19.90"),
            originalPrice = BigDecimal("35.00"),
            imagePath = "images/product_fridge_magnet.png",
            description = "精美磁吸冰箱贴，融合日历与植物元素设计，既是实用家居好物，也是红色文化传播的载体。",
            category = "生活用品",
            tags = listOf("热销"),
            salesCount = 738,
            stock = 300,
            details = listOf(
                "材质：软磁+铜版纸覆膜",
                "规格：单张9×5.5cm，全套6张",
                "工艺：四色印刷，防水覆膜",
                "功能：磁吸、装饰、日历"
            )
        ),
        Product(
            id = "4",
            name = "历史事件和纸胶带",
            price = BigDecimal("15.90"),
            originalPrice = BigDecimal("25.00"),
            imagePath = "images/product_history_tape.png",
            description = "红色主题和纸胶带，印有经典历史标语，可用于手账装饰、礼品包装，让红色元素融入日常生活。",
            category = "文具用品",
            tags = listOf("文创"),
            salesCount = 289,
            stock = 150,
            details = listOf(
                "材质：和纸",
                "规格：宽1.5cm，长5m",
                "工艺：环保油墨印刷",
                "套装：3卷/套，不同图案"
            )
        ),
        Product(
            id = "5",
            name = "红色文化3D立体积木",
            price = BigDecimal("89.00"),
            originalPrice = BigDecimal("128.00"),
            imagePath = "images/product_3d_block.png",
            description = "3D立体拼插积木，以红色文化建筑为原型，锻炼动手能力的同时了解红色历史，适合青少年研学使用。",
            category = "拼图系列",
            tags = listOf("热销", "益智"),
            salesCount = 456,
            stock = 80,
            details = listOf(
                "材质：ABS环保塑料",
                "规格：3×3立体方块，含200+零件",
                "适用年龄：6岁以上",
                "附赠：拼装说明书及红色故事卡"
            )
        ),
        Product(
            id = "6",
            name = "红色文化主题拼图",
            price = BigDecimal("39.90"),
            originalPrice = BigDecimal("59.90"),
            imagePath = "images/product_red_culture_puzzle.png",
            description = "以红色经典舞蹈场景为主题的拼图，色彩鲜明，画面生动，让青少年在拼图过程中感受红色文化魅力。",
            category = "拼图系列",
            tags = listOf("新品", "教育"),
            salesCount = 167,
            stock = 120,
            details = listOf(
                "材质：优质荷兰白卡纸",
                "规格：300片装，完成后尺寸30×21cm",
                "适用年龄：10岁以上",
                "附赠：红色文化知识卡片"
            )
        )
    )

    /** 按 ID 获取商品 */
    fun getProductById(id: String): Product? {
        return products.find { it.id == id }
    }

    /** 按分类筛选 */
    fun getProductsByCategory(category: String): List<Product> {
        return if (category == "全部") products
        else products.filter { it.category == category }
    }

    /** 所有分类 */
    val categories: List<String> = listOf("全部", "拼图系列", "生活用品", "文具用品")
}
