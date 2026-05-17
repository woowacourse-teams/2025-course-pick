package io.coursepick.coursepick.presentation.course

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.coursepick.coursepick.data.NetworkMonitor
import io.coursepick.coursepick.data.interceptor.NoNetworkException
import io.coursepick.coursepick.domain.auth.AuthRepository
import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.course.Course
import io.coursepick.coursepick.domain.course.CourseRepository
import io.coursepick.coursepick.domain.course.CoursesPage
import io.coursepick.coursepick.domain.course.Kilometer
import io.coursepick.coursepick.domain.course.Scope
import io.coursepick.coursepick.domain.favorites.FavoritesRepository
import io.coursepick.coursepick.domain.location.Location
import io.coursepick.coursepick.domain.location.LocationRepository
import io.coursepick.coursepick.domain.notice.Notice
import io.coursepick.coursepick.domain.notice.NoticeRepository
import io.coursepick.coursepick.presentation.Logger
import io.coursepick.coursepick.presentation.auth.AuthFeature
import io.coursepick.coursepick.presentation.filter.CourseFilter
import io.coursepick.coursepick.presentation.filter.CourseFilterAction
import io.coursepick.coursepick.presentation.preference.CoursePickPreferences
import io.coursepick.coursepick.presentation.routefinder.RouteFinderApplication
import io.coursepick.coursepick.presentation.ui.MutableSingleLiveData
import io.coursepick.coursepick.presentation.ui.SingleLiveData
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class CoursesViewModel
    @Inject
    constructor(
        private val courseRepository: CourseRepository,
        private val favoritesRepository: FavoritesRepository,
        private val noticeRepository: NoticeRepository,
        private val locationRepository: LocationRepository,
        private val authRepository: AuthRepository,
        private val networkMonitor: NetworkMonitor,
    ) : ViewModel() {
        private val _state: MutableLiveData<CoursesUiState> =
            MutableLiveData(
                CoursesUiState(
                    courses = listOf(CourseListItem.Loading),
                    query = "",
                    status = UiStatus.Loading,
                ),
            )
        val state: LiveData<CoursesUiState> get() = _state

        private val _content: MutableLiveData<CoursesContent> = MutableLiveData(CoursesContent.EXPLORE)
        val content: LiveData<CoursesContent> get() = _content

        val isCoarseLocationPermissionGranted get() = locationRepository.isCoarseLocationPermissionGranted
        val isFineLocationPermissionGranted get() = locationRepository.isFineLocationPermissionGranted

        val locationUpdates: StateFlow<Location?> =
            locationRepository.locationUpdates.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = null,
            )

        private val _reportCourseDialogState = MutableStateFlow<CourseItem?>(null)
        val reportCourseDialogState: StateFlow<CourseItem?> get() = _reportCourseDialogState.asStateFlow()

        private val _authDialogState = MutableStateFlow<AuthFeature?>(null)
        val authDialogState: StateFlow<AuthFeature?> get() = _authDialogState.asStateFlow()

        private val _event: MutableSingleLiveData<CoursesUiEvent> = MutableSingleLiveData()
        val event: SingleLiveData<CoursesUiEvent> get() = _event

        var mapCoordinate: Coordinate? = null
            private set

        private var writeFavoriteJob: Job? = null
        private val pendingFavoriteWrites: MutableMap<String, Boolean> = mutableMapOf()

        private var page: Int = 0
        private var hasNext: Boolean = false

        private var lastMapCoordinate: Coordinate? = null
        private var lastUserCoordinate: Coordinate? = null
        private var lastScope: Scope? = null

        private var originalCourseFilter: CourseFilter = CourseFilter.None

        init {
            checkNetwork()
        }

        fun selectExternalCourse(courseItem: CourseItem) {
            syncExternalSelectedCourse(courseItem)
            _event.value = CoursesUiEvent.SelectCourseManually(courseItem)
        }

        private fun syncExternalSelectedCourse(courseItem: CourseItem) {
            _state.value =
                _state.value?.copy(
                    courses =
                        _state.value?.courses?.let { oldList: List<CourseListItem> ->
                            val hasCourse: Boolean =
                                oldList.any { it is CourseListItem.Course && it.item.id == courseItem.id }
                            if (hasCourse) {
                                oldList.map { item ->
                                    if (item is CourseListItem.Course) {
                                        CourseListItem.Course(item.item.copy(selected = item.item.id == courseItem.id))
                                    } else {
                                        item
                                    }
                                }
                            } else {
                                val clearedList: List<CourseListItem> =
                                    oldList.map { item: CourseListItem ->
                                        if (item is CourseListItem.Course) {
                                            CourseListItem.Course(
                                                item.item.copy(
                                                    selected = false,
                                                ),
                                            )
                                        } else {
                                            item
                                        }
                                    }
                                listOf(CourseListItem.Course(courseItem)) + clearedList
                            }
                        } ?: listOf(CourseListItem.Course(courseItem)),
                    status = UiStatus.Success,
                )
        }

        private fun checkNetwork() {
            if (!networkMonitor.isConnected()) {
                _state.value =
                    state.value?.copy(courses = emptyList(), status = UiStatus.NoInternet)
            }
        }

        fun switchContent(content: CoursesContent) {
            _content.value = content
        }

        fun onMapMoved(coordinate: Coordinate) {
            mapCoordinate = coordinate
        }

        fun select(course: CourseItem) {
            if (course.selected) {
                _event.value = CoursesUiEvent.SelectCourseManually(course)
                return
            }

            val oldCourses: List<CourseListItem> = state.value?.courses ?: return

            val selectedIndex = oldCourses.indexOf(CourseListItem.Course(course))
            if (selectedIndex == -1) return

            val newCourseItems: List<CourseListItem> = newCoursesListItem(oldCourses, course)
            _state.value = state.value?.copy(courses = newCourseItems)
            _event.value = CoursesUiEvent.SelectCourseManually(course)
        }

        fun toggleFavorite(toggledCourse: CourseItem) {
            pendingFavoriteWrites[toggledCourse.id] = !toggledCourse.favorite

            state.value?.courses?.let { courses: List<CourseListItem> ->
                val newCourses =
                    courses.map { item: CourseListItem ->
                        when (item) {
                            is CourseListItem.Course -> {
                                if (item.item.id == toggledCourse.id) {
                                    CourseListItem.Course(item.item.copy(favorite = !item.item.favorite))
                                } else {
                                    item
                                }
                            }

                            is CourseListItem.Loading -> {
                                item
                            }
                        }
                    }
                _state.value = state.value?.copy(courses = newCourses)
            }

            updateFavorites()
        }

        private fun updateFavorites() {
            writeFavoriteJob?.cancel()

            writeFavoriteJob =
                viewModelScope.launch {
                    delay(DEBOUNCE_LIMIT_TIME)

                    pendingFavoriteWrites.toMap().forEach { (courseId: String, favorite: Boolean) ->
                        if (favorite) {
                            favoritesRepository.addFavoriteCourse(courseId)
                        } else {
                            favoritesRepository.removeFavoriteCourse(courseId)
                        }
                    }
                    pendingFavoriteWrites.clear()
                }
        }

        fun fetchCourses(
            mapCoordinate: Coordinate,
            userCoordinate: Coordinate?,
            scope: Scope = Scope.default(),
        ) {
            _state.value =
                state.value?.copy(
                    courses = listOf(CourseListItem.Loading),
                    status = UiStatus.Loading,
                )

            val minLength =
                state.value
                    ?.courseFilter
                    ?.lengthRange
                    ?.start
                    ?.toMeter()
            val maxLength =
                state.value
                    ?.courseFilter
                    ?.lengthRange
                    ?.endInclusive
                    ?.toMeter()

            viewModelScope.launch {
                runCatching {
                    courseRepository.courses(
                        scope = scope,
                        page = 0,
                        mapCoordinate = mapCoordinate,
                        userCoordinate = userCoordinate,
                        minLength = minLength,
                        maxLength = maxLength,
                    )
                }.onSuccess { coursesPage: CoursesPage ->
                    Logger.log(Logger.Event.Success("fetch_courses_new"))

                    val favoritedCourseIds: Set<String> = favoritesRepository.favoriteCourseIds()

                    val courseItems: List<CourseItem> =
                        coursesPage.courses
                            .sortedBy(Course::distance)
                            .mapIndexed { index, course ->
                                CourseItem(
                                    course = course,
                                    selected = index == 0,
                                    favorite = favoritedCourseIds.contains(course.id),
                                )
                            }

                    lastMapCoordinate = mapCoordinate
                    lastUserCoordinate = userCoordinate
                    lastScope = scope

                    page = 0
                    hasNext = coursesPage.hasNext

                    _state.value =
                        state.value?.copy(
                            courses = courseItems.map(CourseListItem::Course),
                            status = UiStatus.Success,
                        )
                }.onFailure { exception: Throwable ->
                    Logger.log(
                        Logger.Event.Failure("fetch_courses_new"),
                        "message" to exception.message.toString(),
                    )
                    if (exception is NoNetworkException) {
                        _state.value =
                            state.value?.copy(
                                courses = emptyList(),
                                status = UiStatus.NoInternet,
                            )
                        return@onFailure
                    }
                    _state.value =
                        state.value?.copy(
                            courses = emptyList(),
                            status = UiStatus.Failure,
                        )
                    _event.value = CoursesUiEvent.FetchCourseFailure
                }
            }
        }

        fun fetchNextCourses() {
            if (state.value?.status == UiStatus.Loading || !hasNext) return

            val existingCourses: List<CourseListItem> = state.value?.courses ?: emptyList()
            _state.value =
                state.value?.copy(
                    courses = existingCourses + CourseListItem.Loading,
                    status = UiStatus.Loading,
                )

            val mapCoordinate: Coordinate = lastMapCoordinate ?: return
            val userCoordinate: Coordinate? = lastUserCoordinate
            val scope: Scope = lastScope ?: return
            val minLength =
                state.value
                    ?.courseFilter
                    ?.lengthRange
                    ?.start
                    ?.toMeter()
            val maxLength =
                state.value
                    ?.courseFilter
                    ?.lengthRange
                    ?.endInclusive
                    ?.toMeter()

            viewModelScope.launch {
                runCatching {
                    val nextPage: Int = page + 1
                    courseRepository.courses(
                        scope = scope,
                        page = nextPage,
                        mapCoordinate = mapCoordinate,
                        userCoordinate = userCoordinate,
                        minLength = minLength,
                        maxLength = maxLength,
                    )
                }.onSuccess { coursesPage: CoursesPage ->
                    Logger.log(Logger.Event.Success("fetch_courses_next"))

                    val favoritedCourseIds: Set<String> = favoritesRepository.favoriteCourseIds()

                    val existingCoursesWithoutLoading =
                        (state.value?.courses ?: emptyList())
                            .filterNot { courseListItem: CourseListItem -> courseListItem is CourseListItem.Loading }

                    val newCourses =
                        coursesPage.courses.map { course: Course ->
                            CourseListItem.Course(
                                CourseItem(
                                    course = course,
                                    selected = false,
                                    favorite = favoritedCourseIds.contains(course.id),
                                ),
                            )
                        }

                    page += 1
                    hasNext = coursesPage.hasNext

                    _state.value =
                        state.value?.copy(
                            courses = existingCoursesWithoutLoading + newCourses,
                            status = UiStatus.Success,
                        )
                }.onFailure { exception: Throwable ->
                    Logger.log(
                        Logger.Event.Failure("fetch_courses_next"),
                        "message" to exception.message.toString(),
                    )

                    val existingCoursesWithoutLoading =
                        (state.value?.courses ?: emptyList())
                            .filterNot { courseListItem: CourseListItem -> courseListItem is CourseListItem.Loading }

                    _state.value =
                        state.value?.copy(
                            courses = existingCoursesWithoutLoading,
                            status = UiStatus.Failure,
                        )
                    _event.value = CoursesUiEvent.FetchNextCoursesFailure
                }
            }
        }

        fun fetchFavorites() {
            _state.value =
                state.value?.copy(
                    courses = listOf(CourseListItem.Loading),
                    status = UiStatus.Loading,
                )

            val favoritedCourseIds: Set<String> = favoritesRepository.favoriteCourseIds()
            if (favoritedCourseIds.isEmpty()) {
                _state.value =
                    state.value?.copy(
                        courses = emptyList(),
                        status = UiStatus.Success,
                    )
                return
            }

            viewModelScope.launch {
                runCatching {
                    courseRepository.courses(favoritedCourseIds.toList())
                }.onSuccess { courses: List<Course> ->
                    val courseItems: List<CourseItem> =
                        courses.map { course: Course ->
                            CourseItem(
                                course = course,
                                selected = false,
                                favorite = true,
                            )
                        }
                    _state.value =
                        state.value
                            ?.copy(
                                courses = courseItems.map(CourseListItem::Course),
                                status = UiStatus.Success,
                            )
                }.onFailure { exception: Throwable ->
                    Logger.log(
                        Logger.Event.Failure("fetch_courses"),
                        "message" to exception.message.toString(),
                    )
                    if (exception is NoNetworkException) {
                        _state.value =
                            state.value
                                ?.copy(
                                    courses = emptyList(),
                                    status = UiStatus.NoInternet,
                                )
                        return@onFailure
                    }
                    _state.value =
                        state.value
                            ?.copy(
                                courses = emptyList(),
                                status = UiStatus.Failure,
                            )
                    _event.value = CoursesUiEvent.FetchCourseFailure
                }
            }
        }

        fun fetchRouteToCourse(
            course: CourseItem,
            origin: Coordinate,
        ) {
            val oldCourses: List<CourseListItem> = state.value?.courses ?: return
            val newCourseItems: List<CourseListItem> = newCoursesListItem(oldCourses, course)
            _state.value =
                state.value?.copy(
                    courses = newCourseItems,
                    status = UiStatus.Loading,
                )

            val selectedCourse: CourseItem = course.copy(selected = true)
            viewModelScope.launch {
                runCatching {
                    courseRepository.routeToCourse(selectedCourse.course, origin)
                }.onSuccess { route: List<Coordinate> ->
                    Logger.log(Logger.Event.Success("fetch_route_to_course"))
                    _state.value = state.value?.copy(status = UiStatus.Success)
                    _event.value = CoursesUiEvent.FetchRouteToCourseSuccess(route, selectedCourse)
                }.onFailure { error: Throwable ->
                    Logger.log(
                        Logger.Event.Failure("fetch_route_to_course"),
                        "message" to error.message.toString(),
                    )
                    _state.value = state.value?.copy(status = UiStatus.Failure)
                    _event.value =
                        if (error is NoNetworkException) {
                            CoursesUiEvent.NoNetworkConnection
                        } else {
                            CoursesUiEvent.FetchRouteToCourseFailure
                        }
                }
            }
        }

        fun fetchNearestCoordinate(
            selectedCourse: CourseItem,
            location: Coordinate,
            routeFinder: RouteFinderApplication.ThirdParty,
        ) {
            viewModelScope.launch {
                runCatching {
                    courseRepository.nearestCoordinate(selectedCourse.course, location)
                }.onSuccess { nearest: Coordinate ->
                    Logger.log(Logger.Event.Success("fetch_nearest_coordinate"))
                    _event.value =
                        CoursesUiEvent.FetchNearestCoordinateSuccess(
                            origin = location,
                            destination = nearest,
                            destinationName = selectedCourse.name,
                            routeFinder,
                        )
                }.onFailure { error: Throwable ->
                    Logger.log(
                        Logger.Event.Failure("fetch_nearest_coordinate"),
                        "message" to error.message.toString(),
                    )
                    _event.value = CoursesUiEvent.FetchNearestCoordinateFailure
                }
            }
        }

        fun setQuery(query: String) {
            _state.value = state.value?.copy(query = query)
        }

        fun handleFilterAction(action: CourseFilterAction) {
            when (action) {
                is CourseFilterAction.Cancel -> {
                    _state.value =
                        state.value?.copy(
                            courseFilter = originalCourseFilter,
                            showFilterDialog = false,
                        )
                }

                is CourseFilterAction.Reset -> {
                    _state.value = state.value?.copy(courseFilter = CourseFilter.None)
                }

                is CourseFilterAction.Apply -> {
                    _state.value = state.value?.copy(showFilterDialog = false)
                    val mapCoordinate = lastMapCoordinate ?: return
                    val scope = lastScope ?: return
                    fetchCourses(mapCoordinate, lastUserCoordinate, scope)
                }

                is CourseFilterAction.UpdateLengthRange -> {
                    updateLengthRange(action.start, action.end)
                }
            }
        }

        fun showFilterDialog() {
            originalCourseFilter = state.value?.courseFilter ?: CourseFilter.None
            _state.value = state.value?.copy(showFilterDialog = true)
        }

        fun dismissFilterDialog() {
            _state.value =
                state.value?.copy(
                    showFilterDialog = false,
                    courseFilter = originalCourseFilter,
                )
        }

        private fun updateLengthRange(
            min: Kilometer,
            max: Kilometer,
        ) {
            val currentRange = state.value?.courseFilter?.lengthRange
            if (currentRange?.start == min && currentRange.endInclusive == max) return

            val updatedLengthRange = min..max

            val updatedCourseFilter =
                state.value?.courseFilter?.copy(lengthRange = updatedLengthRange)
                    ?: CourseFilter.None.copy(lengthRange = updatedLengthRange)
            _state.value = state.value?.copy(courseFilter = updatedCourseFilter)
        }

        fun fetchNotices() {
            viewModelScope.launch {
                runCatching {
                    noticeRepository.notices()
                }.onSuccess { notices: List<Notice> ->
                    withContext(Dispatchers.IO) {
                        CoursePickPreferences.removeInvalidNoticeIds(notices.map(Notice::id).toSet())
                    }
                    _state.value = state.value?.copy(notices = notices)
                }
            }
        }

        fun dismissNotice(id: String) {
            _state.value =
                state.value?.copy(
                    notices =
                        state.value
                            ?.notices
                            ?.filterNot { notice: Notice -> notice.id == id }
                            .orEmpty(),
                )
        }

        fun showSettings() {
            _state.value = state.value?.copy(showSettings = true)
        }

        fun showCourses() {
            _state.value = state.value?.copy(showSettings = false)
        }

        suspend fun currentLocation(): Location? = locationRepository.currentLocation()

        fun onReportCourse(course: CourseItem) {
            viewModelScope.launch {
                if (authRepository.accessToken() == null) {
                    _authDialogState.value = AuthFeature.ReportCourse(course)
                } else {
                    _reportCourseDialogState.value = course
                }
            }
        }

        fun checkAuthForCustomCourse(onAuthorized: () -> Unit) {
            viewModelScope.launch {
                if (authRepository.accessToken() == null) {
                    _authDialogState.value = AuthFeature.CustomCourse
                } else {
                    onAuthorized()
                }
            }
        }

        fun submitCourseReport(course: CourseItem) {
            viewModelScope.launch {
                try {
                    courseRepository.report(course.course)
                    _reportCourseDialogState.value = null
                    _event.value = CoursesUiEvent.ReportCourseSuccess
                } catch (exception: CancellationException) {
                    throw exception
                } catch (_: NoNetworkException) {
                    _event.value = CoursesUiEvent.NoNetworkConnection
                } catch (exception: HttpException) {
                    _event.value =
                        when (exception.code()) {
                            400 -> {
                                dismissReportCourseDialog()
                                CoursesUiEvent.CourseAlreadyReported
                            }

                            401 -> {
                                CoursesUiEvent.ReportCourseUnauthorizedUser
                            }

                            else -> {
                                CoursesUiEvent.ReportCourseUnknownFailure
                            }
                        }
                } catch (_: Throwable) {
                    _event.value = CoursesUiEvent.ReportCourseUnknownFailure
                }
            }
        }

        fun dismissReportCourseDialog() {
            _reportCourseDialogState.value = null
        }

        fun dismissAuthDialog() {
            _authDialogState.value = null
        }

        fun onAuthSuccess(feature: AuthFeature) {
            dismissAuthDialog()
            if (feature is AuthFeature.ReportCourse) {
                onReportCourse(feature.course)
            }
        }

        private fun newCoursesListItem(
            oldCourses: List<CourseListItem>,
            selectedCourse: CourseItem,
        ): List<CourseListItem> =
            oldCourses.map { courseListItem: CourseListItem ->
                if (courseListItem is CourseListItem.Course) {
                    if (courseListItem.item == selectedCourse) {
                        CourseListItem.Course(courseListItem.item.copy(selected = true))
                    } else {
                        CourseListItem.Course(courseListItem.item.copy(selected = false))
                    }
                } else {
                    courseListItem
                }
            }

        companion object {
            private const val DEBOUNCE_LIMIT_TIME = 500L
        }
    }
