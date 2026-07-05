package com.zhihui.yanxue.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zhihui.yanxue.R
import com.zhihui.yanxue.CourseDetailActivity
import com.zhihui.yanxue.data.MockCourseRepository
import com.zhihui.yanxue.data.model.Course
import com.zhihui.yanxue.databinding.FragmentCoursesBinding

/**
 * 课程中心 Fragment — 左右分栏（参考设计稿）：
 * 左侧：简洁文字分类列表，选中红色左边框指示器
 * 右侧：单列大图课程卡片（大封面+标题+标签+信息）
 */
class CoursesFragment : Fragment() {

    private var _binding: FragmentCoursesBinding? = null
    private val binding get() = _binding!!
    private val courseRepository = MockCourseRepository

    // 分类列表 — 简洁文字风格
    private val categories = listOf(
        "地标", "人物", "文物", "事件"
    )
    private var selectedCategory = "地标"

    // Adapters
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var courseAdapter: CourseCardAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCoursesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCategoryList()
        setupCourseList()
        loadCourses()
    }

    /** 左侧分类列表 */
    private fun setupCategoryList() {
        categoryAdapter = CategoryAdapter(categories, selectedCategory) { category ->
            selectedCategory = category
            categoryAdapter.setSelected(category)
            // 滚动到选中项
            val idx = categories.indexOf(category)
            if (idx >= 0) {
                binding.rvCategory.smoothScrollToPosition(idx)
            }
            loadCourses()
        }
        binding.rvCategory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCategory.adapter = categoryAdapter
    }

    /** 右侧课程列表（单列纵向） */
    private fun setupCourseList() {
        courseAdapter = CourseCardAdapter(emptyList()) { course ->
            val intent = android.content.Intent(requireContext(), CourseDetailActivity::class.java)
            intent.putExtra("course_id", course.id)
            startActivity(intent)
        }
        binding.rvCourses.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCourses.adapter = courseAdapter
    }

    /** 加载课程数据 */
    private fun loadCourses() {
        val courses = courseRepository.getCoursesByCategory(selectedCategory)
        courseAdapter.updateData(courses)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

/* ==================== 左侧分类 Adapter ==================== */

class CategoryAdapter(
    private val categories: List<String>,
    private var selected: String,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.VH>() {

    fun setSelected(category: String) {
        selected = category
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_course_category, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val name = categories[position]
        val isSelected = name == selected
        val ctx = holder.itemView.context

        // 整体背景
        holder.itemView.findViewById<View>(R.id.layout_category_item).setBackgroundResource(
            if (isSelected) R.drawable.bg_category_selected
            else R.drawable.bg_category_normal
        )

        // 红色左边框指示条
        holder.itemView.findViewById<View>(R.id.view_indicator).visibility =
            if (isSelected) View.VISIBLE else View.GONE

        // 分类名称
        val tvName = holder.itemView.findViewById<TextView>(R.id.tv_category_name)
        tvName.text = name
        tvName.setTextColor(
            if (isSelected) ctx.getColor(R.color.primary_red)
            else ctx.getColor(R.color.text_secondary)
        )
        tvName.textSize = if (isSelected) 15f else 14f
        tvName.setTypeface(null, if (isSelected) android.graphics.Typeface.BOLD else android.graphics.Typeface.NORMAL)

        holder.itemView.setOnClickListener { onItemClick(name) }
    }

    override fun getItemCount() = categories.size

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView)
}

/* ==================== 右侧课程卡片 Adapter ==================== */

class CourseCardAdapter(
    private var courses: List<Course>,
    private val onItemClick: (Course) -> Unit
) : RecyclerView.Adapter<CourseCardAdapter.VH>() {

    fun updateData(newList: List<Course>) {
        courses = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_course_card, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val course = courses[position]
        val ctx = holder.itemView.context

        // 封面图
        val coverView = holder.itemView.findViewById<ImageView>(R.id.iv_course_cover)
        val resId = ctx.resources.getIdentifier(course.imagePath, "drawable", ctx.packageName)
        if (resId != 0) {
            coverView.setImageResource(resId)
        } else {
            coverView.setImageResource(R.drawable.bg_article_1)
        }

        // 免费/付费标签
        val freeTag = holder.itemView.findViewById<TextView>(R.id.tv_free_tag)
        freeTag.visibility = View.VISIBLE
        freeTag.text = if (course.isFree) "免费" else "¥${course.price.toInt()}"

        // 学时
        holder.itemView.findViewById<TextView>(R.id.tv_duration).text = "${course.duration}分钟"

        // 标题
        holder.itemView.findViewById<TextView>(R.id.tv_course_title).text = course.title

        // 标签容器
        val tagContainer = holder.itemView.findViewById<LinearLayout>(R.id.layout_tags)
        tagContainer.removeAllViews()
        val tagInflater = LayoutInflater.from(ctx)
        for (tag in course.tags.take(3)) {
            val tagView = tagInflater.inflate(R.layout.item_tag_pill, tagContainer, false) as TextView
            tagView.text = tag
            tagContainer.addView(tagView)
        }

        // 学习人数
        holder.itemView.findViewById<TextView>(R.id.tv_student_count).text = "${course.studentCount}人学习"

        // 评分
        holder.itemView.findViewById<TextView>(R.id.tv_rating).text = "★ ${String.format("%.1f", course.rating)}"

        holder.itemView.setOnClickListener { onItemClick(course) }
    }

    override fun getItemCount() = courses.size

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView)
}
