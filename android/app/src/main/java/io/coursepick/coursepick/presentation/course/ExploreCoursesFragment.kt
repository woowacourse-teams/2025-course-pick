package io.coursepick.coursepick.presentation.course

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import io.coursepick.coursepick.databinding.FragmentExploreCoursesBinding
import io.coursepick.coursepick.presentation.compat.OnDescribeCourseColorListener
import io.coursepick.coursepick.presentation.compat.OnReconnectListener

class ExploreCoursesFragment(
    courseItemListener: CourseItemListener,
    private val onReconnectListener: OnReconnectListener,
    private val onDescribeCourseColorListener: OnDescribeCourseColorListener,
) : Fragment() {
    @Suppress("ktlint:standard:backing-property-naming")
    private var _binding: FragmentExploreCoursesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CoursesViewModel by activityViewModels()
    private val courseAdapter by lazy { CourseAdapter(courseItemListener) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentExploreCoursesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setUpBindingVariables()
        setUpStateObserver()
        setUpScrollListener()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setUpBindingVariables() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.adapter = courseAdapter
        binding.viewModel = viewModel
        binding.onReconnectListener = onReconnectListener
        binding.onDescribeCourseColorListener = onDescribeCourseColorListener
    }

    private fun setUpStateObserver() {
        viewModel.state.observe(viewLifecycleOwner) { state: CoursesUiState ->
            courseAdapter.submitList(state.courses)
        }
    }

    private fun setUpScrollListener() {
        binding.exploreCourses.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrolled(
                    recyclerView: RecyclerView,
                    dx: Int,
                    dy: Int,
                ) {
                    super.onScrolled(recyclerView, dx, dy)

                    val layoutManager: LinearLayoutManager =
                        recyclerView.layoutManager as? LinearLayoutManager ?: return

                    val totalItemCount: Int = layoutManager.itemCount

                    val lastVisibleItem: Int = layoutManager.findLastVisibleItemPosition()

                    if (lastVisibleItem >= totalItemCount - LOAD_MORE_THRESHOLD) {
                        viewModel.fetchNextCourses()
                    }
                }
            },
        )
    }

    fun scrollTo(courseItem: CourseItem) {
        val position =
            courseAdapter.currentList.indexOfFirst { item: CourseListItem ->
                item is CourseListItem.Course && item.item.id == courseItem.id
            }
        if (position == -1) return
        val layoutManager = binding.exploreCourses.layoutManager as? LinearLayoutManager ?: return
        val smoothScroller =
            object : LinearSmoothScroller(requireContext()) {
                override fun getVerticalSnapPreference(): Int = SNAP_TO_START
            }
        smoothScroller.targetPosition = position
        layoutManager.startSmoothScroll(smoothScroller)
    }

    companion object {
        private const val LOAD_MORE_THRESHOLD: Int = 3
    }
}
