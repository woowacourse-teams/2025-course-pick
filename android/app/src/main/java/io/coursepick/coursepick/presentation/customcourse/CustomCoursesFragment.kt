package io.coursepick.coursepick.presentation.customcourse

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import io.coursepick.coursepick.R
import io.coursepick.coursepick.databinding.FragmentCustomCoursesBinding
import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.presentation.auth.AuthDialog
import io.coursepick.coursepick.presentation.auth.AuthFeature
import io.coursepick.coursepick.presentation.auth.AuthUiEvent
import io.coursepick.coursepick.presentation.auth.AuthViewModel
import io.coursepick.coursepick.presentation.auth.KakaoAuthenticator
import io.coursepick.coursepick.presentation.compat.OnReconnectListener
import io.coursepick.coursepick.presentation.course.CoursesActivity
import io.coursepick.coursepick.presentation.course.CoursesViewModel
import io.coursepick.coursepick.presentation.coursedetail.CourseDetailActivity
import io.coursepick.coursepick.presentation.createcustomcourse.CoordinateUiModel
import io.coursepick.coursepick.presentation.createcustomcourse.CreateCustomCourseActivity
import io.coursepick.coursepick.presentation.createcustomcourse.toUiModel
import kotlinx.coroutines.launch

class CustomCoursesFragment(
    private val onReconnectListener: OnReconnectListener,
) : Fragment() {
    @Suppress("ktlint:standard:backing-property-naming")
    private var _binding: FragmentCustomCoursesBinding? = null
    private val binding: FragmentCustomCoursesBinding get() = _binding!!

    private val coursesViewModel: CoursesViewModel by activityViewModels()
    private val customCourseViewModel: CustomCourseViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by activityViewModels()

    private val createCustomCourseLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) customCourseViewModel.fetchCustomCourses()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpCollectors()

        customCourseViewModel.fetchCustomCourses()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCustomCoursesBinding.inflate(inflater, container, false)
        binding.customCourses.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val nestedScrollInterop = rememberNestedScrollInteropConnection()
                val customCourseState =
                    customCourseViewModel.state.collectAsStateWithLifecycle().value

                CustomCourseScreen(
                    status = customCourseState,
                    onReconnect = onReconnectListener,
                    onGoToCreateCustomCourse = customCourseViewModel::onGoToCreateCustomCourse,
                    onSelect = { customCourse: CustomCourseItem ->
                        customCourseViewModel.select(customCourse)
                    },
                    onNavigateToCourse = { customCourse: CustomCourseItem ->
                        customCourseViewModel.onNavigateToCourse(customCourse) { courseItem ->
                            (activity as? CoursesActivity)?.navigateToCourse(courseItem)
                        }
                    },
                    onNavigateToDetail = { customCourse: CustomCourseItem ->
                        startActivity(CourseDetailActivity.intent(requireActivity(), customCourse.course.id))
                    },
                    modifier = Modifier.nestedScroll(nestedScrollInterop),
                )

                customCourseViewModel.authDialogState.collectAsStateWithLifecycle().value?.let { feature: AuthFeature ->
                    AuthDialog(
                        feature = feature,
                        onDismissRequest = customCourseViewModel::dismissAuthDialog,
                        onKakaoLoginClick = {
                            authViewModel.authenticate(
                                KakaoAuthenticator(
                                    requireActivity(),
                                ),
                                feature,
                            )
                        },
                    )
                }
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setUpCollectors() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    customCourseViewModel.uiEvent.collect { event: CustomCourseUiEvent ->
                        when (event) {
                            CustomCourseUiEvent.NavigateToCreateCourse -> {
                                goToCreateCustomCourse()
                            }

                            CustomCourseUiEvent.FetchCustomCourseFailure -> {
                                showToastMessage(R.string.custom_courses_load_failed)
                            }

                            CustomCourseUiEvent.RequestFetch -> {
                                customCourseViewModel.fetchCustomCourses()
                            }

                            CustomCourseUiEvent.UnauthorizedUser -> {
                                showToastMessage(R.string.custom_courses_unauthorized_user_message)
                            }

                            is CustomCourseUiEvent.SelectCustomCourse -> {
                                coursesViewModel.selectExternalCourse(event.customCourse.toCourseItem())
                            }
                        }
                    }
                }

                launch {
                    authViewModel.uiEvent.collect { event: AuthUiEvent ->
                        when (event) {
                            is AuthUiEvent.AuthenticateSuccess -> {
                                customCourseViewModel.onAuthSuccess(event.feature)
                            }

                            AuthUiEvent.AuthenticateFailure -> {
                                Toast
                                    .makeText(
                                        requireActivity(),
                                        getString(R.string.authentication_failure_message),
                                        Toast.LENGTH_SHORT,
                                    ).show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun goToCreateCustomCourse() {
        val initialCoordinate: CoordinateUiModel? =
            coursesViewModel.mapCoordinate?.let(Coordinate::toUiModel)
        val intent: Intent = CreateCustomCourseActivity.intent(requireContext(), initialCoordinate)
        createCustomCourseLauncher.launch(intent)
    }

    private fun showToastMessage(resId: Int) =
        Toast
            .makeText(
                requireActivity(),
                getString(resId),
                Toast.LENGTH_SHORT,
            ).show()
}
