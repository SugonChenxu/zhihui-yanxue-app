package com.zhihui.yanxue

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.zhihui.yanxue.data.MockArticleRepository

/**
 * 文章详情页
 * 展示文章完整内容、作者信息、阅读量等
 *
 * 路由地址：ArticleDetailActivity
 * 接收参数：extra_article_id (String) - 文章ID
 */
class ArticleDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ARTICLE_ID = "extra_article_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_detail)

        val articleId = intent.getStringExtra(EXTRA_ARTICLE_ID) ?: run {
            finish()
            return
        }

        val article = MockArticleRepository.getArticleById(articleId) ?: run {
            Toast.makeText(this, "文章不存在", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // 绑定视图
        val btnBack = findViewById<View>(R.id.btn_back)
        val btnShare = findViewById<TextView>(R.id.btn_share)
        val viewImage = findViewById<ImageView>(R.id.view_detail_image)
        val txtTag1 = findViewById<TextView>(R.id.txt_detail_tag1)
        val txtTag2 = findViewById<TextView>(R.id.txt_detail_tag2)
        val txtTitle = findViewById<TextView>(R.id.txt_detail_title)
        val txtAuthor = findViewById<TextView>(R.id.txt_detail_author)
        val txtDate = findViewById<TextView>(R.id.txt_detail_date)
        val txtReads = findViewById<TextView>(R.id.txt_detail_reads)
        val txtContent = findViewById<TextView>(R.id.txt_detail_content)
        val btnLike = findViewById<TextView>(R.id.btn_like)
        val btnCollect = findViewById<TextView>(R.id.btn_collect)

        // 设置配图
        val imageResId = resources.getIdentifier(article.imageResName, "drawable", packageName)
        if (imageResId != 0) {
            viewImage.setImageResource(imageResId)
        }

        // 设置标签
        if (article.tags.isNotEmpty()) {
            txtTag1.text = article.tags[0]
            txtTag1.visibility = View.VISIBLE
        } else {
            txtTag1.visibility = View.GONE
        }
        if (article.tags.size > 1) {
            txtTag2.text = article.tags[1]
            txtTag2.visibility = View.VISIBLE
        } else {
            txtTag2.visibility = View.GONE
        }

        // 设置文本内容
        txtTitle.text = article.title
        txtAuthor.text = article.author
        txtDate.text = article.publishDate
        txtReads.text = formatReadCount(article.readCount)
        txtContent.text = article.content

        // 按钮事件
        btnBack.setOnClickListener { finish() }
        btnShare.setOnClickListener {
            Toast.makeText(this, "分享功能开发中", Toast.LENGTH_SHORT).show()
        }
        btnLike.setOnClickListener {
            Toast.makeText(this, "已点赞", Toast.LENGTH_SHORT).show()
        }
        btnCollect.setOnClickListener {
            Toast.makeText(this, "已收藏", Toast.LENGTH_SHORT).show()
        }
    }

    /** 格式化阅读量 */
    private fun formatReadCount(count: Int): String {
        return if (count >= 1000) {
            String.format("%.1fk阅读", count / 1000.0)
        } else {
            "${count}阅读"
        }
    }
}
