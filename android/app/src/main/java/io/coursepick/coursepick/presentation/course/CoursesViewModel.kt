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
import io.coursepick.coursepick.domain.favorites.FavoriteCourseRepository
import io.coursepick.coursepick.domain.location.Location
import io.coursepick.coursepick.domain.location.LocationRepository
import io.coursepick.coursepick.domain.notice.Notice
import io.coursepick.coursepick.domain.notice.NoticeRepository
import io.coursepick.coursepick.domain.preferences.PreferencesRepository
import io.coursepick.coursepick.domain.preferences.RouteFinder
import io.coursepick.coursepick.presentation.Logger
import io.coursepick.coursepick.presentation.auth.AuthFeature
import io.coursepick.coursepick.presentation.filter.CourseFilter
import io.coursepick.coursepick.presentation.filter.CourseFilterAction
import io.coursepick.coursepick.presentation.ui.MutableSingleLiveData
import io.coursepick.coursepick.presentation.ui.SingleLiveData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoursesViewModel
    @Inject
    constructor(
        private val courseRepository: CourseRepository,
        private val favoriteCourseRepository: FavoriteCourseRepository,
        private val noticeRepository: NoticeRepository,
        private val locationRepository: LocationRepository,
        private val preferencesRepository: PreferencesRepository,
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

        private val _routeFinderDialogCourse = MutableStateFlow<CourseItem?>(null)
        val routeFinderDialogCourse: StateFlow<CourseItem?> get() = _routeFinderDialogCourse.asStateFlow()

        private val _authDialogState = MutableStateFlow<AuthFeature?>(null)
        val authDialogState: StateFlow<AuthFeature?> get() = _authDialogState.asStateFlow()

        private val _event: MutableSingleLiveData<CoursesUiEvent> = MutableSingleLiveData()
        val event: SingleLiveData<CoursesUiEvent> get() = _event

        private val favoriteCourseIds: StateFlow<Set<String>> =
            favoriteCourseRepository.favoriteCourseIds.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptySet(),
            )

        var mapCoordinate: Coordinate? = null
            private set

        private var page: Int = 0
        private var hasNext: Boolean = false

        private var lastMapCoordinate: Coordinate? = null
        private var lastUserCoordinate: Coordinate? = null
        private var lastScope: Scope? = null

        private var originalCourseFilter: CourseFilter = CourseFilter.None

        init {
            checkNetwork()
            collectFavorites()
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

        private fun collectFavorites() {
            viewModelScope.launch {
                favoriteCourseIds.collect { courseIds: Set<String> ->
                    _state.value =
                        state.value?.copy(
                            courses =
                                state.value
                                    ?.courses
                                    ?.map { item: CourseListItem ->
                                        if (item is CourseListItem.Course) {
                                            item.copy(item = item.item.copy(favorite = courseIds.contains(item.item.id)))
                                        } else {
                                            item
                                        }
                                    }.orEmpty(),
                        )
                }
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
            viewModelScope.launch {
                if (toggledCourse.favorite) {
                    favoriteCourseRepository.removeFavorite(toggledCourse.id)
                } else {
                    favoriteCourseRepository.addFavorite(toggledCourse.id)
                }
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

                    val courseItems: List<CourseItem> =
                        coursesPage.courses
                            .sortedBy(Course::distance)
                            .mapIndexed { index, course ->
                                CourseItem(
                                    course = course,
                                    selected = index == 0,
                                    favorite = favoriteCourseIds.value.contains(course.id),
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

                    val existingCoursesWithoutLoading =
                        (state.value?.courses ?: emptyList())
                            .filterNot { courseListItem: CourseListItem -> courseListItem is CourseListItem.Loading }

                    val newCourses =
                        coursesPage.courses.map { course: Course ->
                            CourseListItem.Course(
                                CourseItem(
                                    course = course,
                                    selected = false,
                                    favorite = favoriteCourseIds.value.contains(course.id),
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

            if (favoriteCourseIds.value.isEmpty()) {
                _state.value =
                    state.value?.copy(
                        courses = emptyList(),
                        status = UiStatus.Success,
                    )
                return
            }

            viewModelScope.launch {
                runCatching {
                    courseRepository.courses(favoriteCourseIds.value.toList())
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

        fun onNavigateToCourse(course: CourseItem) {
            if (!locationRepository.isFineLocationPermissionGranted) {
                _event.value = CoursesUiEvent.RequireFineLocationPermission
                return
            }

            viewModelScope.launch {
                val routeFinder: RouteFinder = preferencesRepository.routeFinder.first()
                navigateToCourse(course, routeFinder)
            }
        }

        fun onRouteFinderSelected(
            course: CourseItem,
            routeFinder: RouteFinder,
            rememberSelection: Boolean,
        ) {
            dismissRouteFinderDialog()
            navigateToCourse(course, routeFinder)

            if (rememberSelection) {
                viewModelScope.launch {
                    preferencesRepository.setRouteFinder(routeFinder)
                }
            }
        }

        fun dismissRouteFinderDialog() {
            _routeFinderDialogCourse.value = null
        }

        private fun navigateToCourse(
            course: CourseItem,
            routeFinder: RouteFinder,
        ) {
            val oldCourses: List<CourseListItem> = state.value?.courses ?: return
            val newCourseItems: List<CourseListItem> = newCoursesListItem(oldCourses, course)
            _state.value = state.value?.copy(courses = newCourseItems, status = UiStatus.Loading)
            val selectedCourse: CourseItem = course.copy(selected = true)

            viewModelScope.launch {
                currentLocation()?.let { location: Location ->
                    when (routeFinder) {
                        RouteFinder.None -> {
                            _routeFinderDialogCourse.value = selectedCourse
                        }

                        RouteFinder.Local -> {
                            fetchRouteToCourse(selectedCourse, location.coordinate)
                        }

                        is RouteFinder.ThirdParty -> {
                            launchThirdPartyRouteFinder(selectedCourse, location.coordinate, routeFinder)
                        }
                    }
                } ?: run {
                    _state.value = state.value?.copy(status = UiStatus.Failure)
                    _event.value = CoursesUiEvent.FetchCurrentLocationFailure
                }
            }
        }

        private fun fetchRouteToCourse(
            course: CourseItem,
            origin: Coordinate,
        ) {
            viewModelScope.launch {
                runCatching {
                    courseRepository.routeToCourse(course.course, origin)
                }.onSuccess { route: List<Coordinate> ->
                    Logger.log(Logger.Event.Success("fetch_route_to_course"))
                    _state.value = state.value?.copy(status = UiStatus.Success)
                    _event.value = CoursesUiEvent.FetchRouteToCourseSuccess(route, course)
                }.onFailure { exception: Throwable ->
                    Logger.log(
                        Logger.Event.Failure("fetch_route_to_course"),
                        "message" to exception.message.toString(),
                    )
                    _state.value = state.value?.copy(status = UiStatus.Failure)
                    _event.value =
                        if (exception is NoNetworkException) {
                            CoursesUiEvent.NoNetworkConnection
                        } else {
                            CoursesUiEvent.FetchRouteToCourseFailure
                        }
                }
            }
        }

        private fun launchThirdPartyRouteFinder(
            course: CourseItem,
            origin: Coordinate,
            routeFinder: RouteFinder.ThirdParty,
        ) {
            viewModelScope.launch {
                runCatching {
                    courseRepository.nearestCoordinate(course.course, origin)
                }.onSuccess { destination: Coordinate ->
                    Logger.log(Logger.Event.Success("fetch_nearest_coordinate"))
                    _state.value = state.value?.copy(status = UiStatus.Success)
                    _event.value = CoursesUiEvent.LaunchThirdPartyRouteFinder(course, origin, destination, routeFinder)
                }.onFailure { exception: Throwable ->
                    Logger.log(
                        Logger.Event.Failure("fetch_nearest_coordinate"),
                        "message" to exception.message.toString(),
                    )
                    _state.value = state.value?.copy(status = UiStatus.Failure)
                    _event.value =
                        if (exception is NoNetworkException) {
                            CoursesUiEvent.NoNetworkConnection
                        } else {
                            CoursesUiEvent.FetchRouteToCourseFailure
                        }
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

        fun muteNotice(id: String) {
            viewModelScope.launch {
                noticeRepository.muteNotice(id)
            }
        }

        fun showMenu() {
            _state.value = state.value?.copy(showMenu = true)
        }

        fun showCourses() {
            _state.value = state.value?.copy(showMenu = false)
        }

        suspend fun currentLocation(): Location? = locationRepository.currentLocation()

        fun checkAuthForCustomCourse(onAuthorized: () -> Unit) {
            viewModelScope.launch {
                if (authRepository.accessToken() == null) {
                    _authDialogState.value = AuthFeature.CustomCourse
                } else {
                    onAuthorized()
                }
            }
        }

        fun dismissAuthDialog() {
            _authDialogState.value = null
        }

        fun onAuthSuccess() {
            dismissAuthDialog()
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
    }
