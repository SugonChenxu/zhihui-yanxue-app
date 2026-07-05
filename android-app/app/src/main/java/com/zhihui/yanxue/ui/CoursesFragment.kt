package com.zhihui.yanxue.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.zhihui.yanxue.data.MockCourseRepository
import com.zhihui.yanxue.data.model.CourseCategory
import com.zhihui.yanxue.databinding.FragmentCoursesBinding

/**
 * 课程中心 Fragment — 左右分栏：
 * 左侧：课程分类列表（竖向）
 * 右侧：对应课程网格列表
 */
class CoursesFragment : Fragment() {

    private var _binding: FragmentCoursesBinding? = null
    private val binding get() = _binding!!
    private val courseRepository = MockCourseRepository

    // 分类列表
    private val categories = listOf(
        CourseCategory("地标", "ic_courses"),
        CourseCategory("人物", "ic_person"),
        CourseCategory("文物", "ic_collect"),
        CourseCategory("事件", "ic_history")
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
        setupCourseGrid()
        loadCourses()
    }

    /** 左侧分类列表 */
    private fun setupCategoryList() {
        categoryAdapter = CategoryAdapter(categories, selectedCategory) { category ->
            selectedCategory = category
            categoryAdapter.setSelected(category)
            loadCourses()
        }
        binding.rvCategory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCategory.adapter = categoryAdapter
    }

    /** 右侧课程网格（2列） */
    private fun setupCourseGrid() {
        courseAdapter = CourseCardAdapter(emptyList()) { course ->
            // 点击课程 → 跳转详情页
            val intent = android.content.Intent(requireContext(), CourseDetailActivity::class.java)
            intent.putExtra("course_id", course.id)
            startActivity(intent)
        }
        binding.rvCourses.layoutManager = GridLayoutManager(requireContext(), 2)
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

/** 分类数据模型 */
data class CourseCategory(val name: String, val iconName: String)

/** 左侧分类 Adapter */
class CategoryAdapter(
    private val categories: List<CourseCategory>,
    private var selected: String,
    private val onItemClick: (String) -> Unit
) : androidx.recyclerview.widget.RecyclerView.Adapter<CategoryAdapter.VH>() {

    fun setSelected(category: String) {
        selected = category
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.item_course_category, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val category = categories[position]
        val isSelected = category.name == selected

        holder.itemView.findViewById<android.widget.LinearLayout>(R.id.layout_category_item)
            .setBackgroundResource(
                if (isSelected) R.drawable.bg_category_selected
                else R.drawable.bg_category_normal
            )

        val iconView = holder.itemView.findViewById<android.widget.ImageView>(R.id.iv_category_icon)
        val nameView = holder.itemView.findViewById<android.widget.TextView>(R.id.tv_category_name)

        // 根据分类设置图标（使用内置图标）
        val iconRes = when (category.name) {
            "地标" -> R.drawable.ic_courses
            "人物" -> R.drawable.ic_person
            "文物" -> R.drawable.ic_collect
            "事件" -> R.drawable.ic_history
            else -> R.drawable.ic_courses
        }
        iconView.setImageResource(iconRes)
        if (isSelected) {
            iconView.setColorFilter(holder.itemView.context.getColor(R.color.primary_red))
        } else {
            iconView.setColorFilter(holder.itemView.context.getColor(R.color.text_secondary))
        }

        nameView.text = category.name
        nameView.setTextColor(
            if (isSelected) holder.itemView.context.getColor(R.color.primary_red)
            else holder.itemView.context.getColor(R.color.text_secondary)
        )

        holder.itemView.setOnClickListener { onItemClick(category.name) }
    }

    override fun getItemCount() = categories.size

    class VH(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView)
}

/** 右侧课程卡片 Adapter */
class CourseCardAdapter(
    private var courses: List<com.zhihui.yanxue.data.model.Course>,
    private val onItemClick: (com.zhihui.yanxue.data.model.Course) -> Unit
) : androidx.recyclerview.widget.RecyclerView.Adapter<CourseCardAdapter.VH>() {

    fun updateData(newList: List<com.zhihui.yanxue.data.model.Course>) {
        courses = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.item_course_card, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val course = courses[position]
        val ctx = holder.itemView.context

        // 封面图（从 drawable 加载）
        val coverView = holder.itemView.findViewById<android.widget.ImageView>(R.id.iv_course_cover)
        val resId = ctx.resources.getIdentifier(course.imagePath, "drawable", ctx.packageName)
        if (resId != 0) {
            coverView.setImageResource(resId)
        } else {
            coverView.setImageResource(R.drawable.bg_article_1)
        }

        // 标题
        holder.itemView.findViewById<android.widget.TextView>(R.id.tv_course_title).text = course.title

        // 学时
        holder.itemView.findViewById<android.widget.TextView>(R.id.tv_duration).text =
            "${course.duration}分钟"

        // 学习人数
        holder.itemView.findViewById<android.widget.TextView>(R.id.tv_student_count).text =
            "${course.studentCount}人学习"

        // 免费/付费标签
        val freeTag = holder.itemView.findViewById<android.widget.TextView>(R.id.tv_free_tag)
        if (course.isFree) {
            freeTag.visibility = View.VISIBLE
            freeTag.text = "免费"
        } else {
            freeTag.text = "¥${course.price.toInt()}"
        }

        holder.itemView.setOnClickListener { onItemClick(course) }
    }

    override fun getItemCount() = courses.size

    class VH(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView)
}
