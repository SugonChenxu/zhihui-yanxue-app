package com.zhihui.yanxue.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.zhihui.yanxue.R
import com.zhihui.yanxue.data.MockCourseRepository
import com.zhihui.yanxue.data.model.Course
import com.zhihui.yanxue.databinding.FragmentCoursesBinding

class CoursesFragment : Fragment() {

    private var _binding: FragmentCoursesBinding? = null
    private val binding get() = _binding!!
    private val courseRepository = MockCourseRepository
    private var currentCategory = "地标"

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
        setupCategoryTabs()
        loadCourses()
    }

    private fun setupCategoryTabs() {
        val tabs = listOf(
            binding.btnTabLandmark to "地标",
            binding.btnTabPerson to "人物",
            binding.btnTabRelic to "文物",
            binding.btnTabEvent to "事件"
        )

        tabs.forEach { (button, category) ->
            button.setOnClickListener {
                currentCategory = category
                updateTabAppearance(category)
                loadCourses()
            }
        }

        updateTabAppearance(currentCategory)
    }

    private fun updateTabAppearance(selectedCategory: String) {
        val tabs = listOf(
            binding.btnTabLandmark to "地标",
            binding.btnTabPerson to "人物",
            binding.btnTabRelic to "文物",
            binding.btnTabEvent to "事件"
        )

        tabs.forEach { (button, category) ->
            if (category == selectedCategory) {
                button.setBackgroundResource(R.drawable.bg_btn_primary)
                button.setTextColor(resources.getColor(android.R.color.white, null))
            } else {
                button.setBackgroundResource(R.drawable.bg_card_white)
                button.setTextColor(resources.getColor(R.color.text_primary, null))
            }
        }
    }

    private fun loadCourses() {
        val courses = courseRepository.getCoursesByCategory(currentCategory)
        // TODO: 设置 RecyclerView Adapter
        // binding.rvCourses.adapter = CourseAdapter(courses) { course ->
        //     // 点击课程，跳转到详情页
        //     val intent = Intent(requireContext(), CourseDetailActivity::class.java)
        //     intent.putExtra("course_id", course.id)
        //     startActivity(intent)
        // }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
