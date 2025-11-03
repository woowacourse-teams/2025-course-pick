package io.coursepick.coursepick.presentation.course

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.IdRes
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.Insets
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.commit
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.kakao.vectormap.LatLng
import dagger.hilt.android.AndroidEntryPoint
import io.coursepick.coursepick.BuildConfig
import io.coursepick.coursepick.R
import io.coursepick.coursepick.databinding.ActivityCoursesBinding
import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.course.Latitude
import io.coursepick.coursepick.domain.course.Longitude
import io.coursepick.coursepick.domain.course.Scope
import io.coursepick.coursepick.presentation.CoursePickApplication
import io.coursepick.coursepick.presentation.CoursePickUpdateManager
import io.coursepick.coursepick.presentation.DataKeys
import io.coursepick.coursepick.presentation.Logger
import io.coursepick.coursepick.presentation.compat.OnReconnectListener
import io.coursepick.coursepick.presentation.compat.getParcelableCompat
import io.coursepick.coursepick.presentation.favorites.FavoriteCoursesFragment
import io.coursepick.coursepick.presentation.map.kakao.KakaoMapManager
import io.coursepick.coursepick.presentation.map.kakao.toCoordinate
import io.coursepick.coursepick.presentation.notice.NoticeDialogFragment
import io.coursepick.coursepick.presentation.preference.CoursePickPreferences
import io.coursepick.coursepick.presentation.preference.PreferencesActivity
import io.coursepick.coursepick.presentation.routefinder.RouteFinderApplication
import io.coursepick.coursepick.presentation.routefinder.RouteFinderChoiceDialogFragment
import io.coursepick.coursepick.presentation.search.SearchActivity
import io.coursepick.coursepick.presentation.ui.DoublePressDetector

@AndroidEntryPoint
class CoursesActivity :
    AppCompatActivity(),
    CoursesAction,
    OnReconnectListener {
    private val coursePickApplication by lazy { application as CoursePickApplication }
    private var searchLauncher: ActivityResultLauncher<Intent>? = null
    private val binding by lazy { ActivityCoursesBinding.inflate(layoutInflater) }
    private val viewModel: CoursesViewModel by viewModels()
    private val courseAdapter by lazy { CourseAdapter(courseItemListener) }
    private val doublePressDetector = DoublePressDetector()
    private val mapManager by lazy { KakaoMapManager(binding.mainMap) }
    private var systemBars: Insets? = null
    private lateinit var updateManager: CoursePickUpdateManager

    private val locationPermissionLauncher: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions(),
        ) {}

    private val courseItemListener =
        object : CourseItemListener {
            override fun select(course: CourseItem) {
                Logger.log(
                    Logger.Event.Click("course_on_list"),
                    "id" to course.id,
                    "name" to course.name,
                )
                viewModel.select(course)
            }

            override fun toggleFavorite(course: CourseItem) {
                viewModel.toggleFavorite(course)
            }

            @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
            override fun navigateToCourse(course: CourseItem) {
                Logger.log(
                    Logger.Event.Click("navigate"),
                    "id" to course.id,
                    "name" to course.name,
                )

                mapManager.fetchCurrentLocation(
                    onSuccess = { latitude: Latitude, longitude: Longitude ->
                        val origin = Coordinate(latitude, longitude)
                        val selectedApp: RouteFinderApplication? =
                            CoursePickPreferences.selectedRouteFinder
                        if (selectedApp == null) {
                            supportFragmentManager.setFragmentResultListener(
                                DataKeys.DATA_KEY_ROUTE_FINDER_CHOICE_REQUEST,
                                this@CoursesActivity,
                            ) { _, bundle: Bundle ->
                                supportFragmentManager.clearFragmentResultListener(DataKeys.DATA_KEY_ROUTE_FINDER_CHOICE_REQUEST)
                                val selectedApp: RouteFinderApplication =
                                    bundle.getParcelableCompat<RouteFinderApplication>(DataKeys.DATA_KEY_ROUTE_FINDER_CHOICE_RESULT)
                                        ?: return@setFragmentResultListener
                                handleNavigation(course, origin, selectedApp)
                            }
                            RouteFinderChoiceDialogFragment().show(supportFragmentManager, null)
                            return@fetchCurrentLocation
                        }
                        handleNavigation(course, origin, selectedApp)
                    },
                    onFailure = {
                        Toast
                            .makeText(this@CoursesActivity, "현재 위치를 가져올 수 없어요.", Toast.LENGTH_SHORT)
                            .show()
                    },
                )
            }
        }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun onCreate(savedInstanceState: Bundle?) {
        setUpFragmentFactory()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view: View, insets: WindowInsetsCompat ->
            val systemBars: Insets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            this.systemBars = systemBars
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            setUpNavigation(systemBars)
            setUpBottomSheet(systemBars)
            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.mainBottomNavigation, null)

        mapManager.start {
            setUpObservers()
            systemBars?.let(::setUpMapPadding)
            mapManager.setOnCameraMoveListener {
                binding.mainSearchThisAreaButton.visibility = View.VISIBLE
                binding.mainCurrentLocationButton.setColorFilter(
                    ContextCompat.getColor(
                        this,
                        R.color.item_primary,
                    ),
                )
            }
            mapManager.fetchCurrentLocation(
                onSuccess = { latitude: Latitude, longitude: Longitude ->
                    viewModel.fetchCourses(
                        mapCoordinate = Coordinate(latitude, longitude),
                        userCoordinate = Coordinate(latitude, longitude),
                    )
                },
                onFailure = {
                    val mapCoordinate: Coordinate =
                        mapManager.cameraPosition?.toCoordinate()
                            ?: return@fetchCurrentLocation
                    viewModel.fetchCourses(
                        mapCoordinate = mapCoordinate,
                        userCoordinate = null,
                    )
                },
            )

            setUpBottomNavigation()
            if (savedInstanceState == null) {
                binding.mainBottomNavigation.selectedItemId = R.id.coursesMenu
            }
        }

        setUpBindingVariables()
        setUpDoubleBackPress()
        requestLocationPermissions()

        searchLauncher = searchActivityResultLauncher()

        updateManager = CoursePickUpdateManager(this)
        updateManager.checkForUpdate()

        if (savedInstanceState == null) {
            showNoticeIfNeeded()
        }
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun onResume() {
        super.onResume()

        mapManager.resume()
        mapManager.startTrackingCurrentLocation()

        updateManager.onResume()
    }

    override fun onPause() {
        super.onPause()

        mapManager.pause()
        mapManager.stopTrackingCurrentLocation()
    }

    override fun onStop() {
        super.onStop()

        updateManager.onStop()
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun searchThisArea() {
        selectMenuWithoutListener(R.id.coursesMenu)

        val mapPosition: LatLng =
            mapManager.cameraPosition ?: run {
                Toast.makeText(this, "지도 위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
                return
            }

        val coordinate = mapPosition.toCoordinate()
        Logger.log(
            Logger.Event.Click("search_this_area"),
            "latitude" to coordinate.latitude.value,
            "longitude" to coordinate.longitude.value,
        )
        binding.mainSearchThisAreaButton.visibility = View.GONE
        mapManager.showSearchPosition(coordinate)
        val scope =
            try {
                mapManager.scope(coordinate)
            } catch (e: IllegalStateException) {
                Toast
                    .makeText(
                        this,
                        e.message ?: "지도를 불러올 수 없어 코스를 탐색할 수 없습니다.",
                        Toast.LENGTH_SHORT,
                    ).show()
                return
            }

        fetchCourses(coordinate, scope)
    }

    override fun openMenu() {
        Logger.log(Logger.Event.Click("drawer_menu"))
        binding.mainDrawer.open()
    }

    override fun navigate(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_preferences -> navigateToPreferences()
            R.id.item_user_feedback -> navigateToFeedback()
            R.id.item_privacy_policy -> navigateToPrivacyPolicy()
            R.id.item_open_source_notice -> navigateToOpenSourceNotice()
        }

        binding.mainDrawer.close()

        return true
    }

    override fun search() {
        val intent = SearchActivity.intent(this)
        val query: String? = viewModel.state.value?.query
        if (!query.isNullOrBlank()) intent.putExtra(DataKeys.DATA_KEY_PLACE_NAME, query)
        searchLauncher?.launch(intent) ?: Toast
            .makeText(
                this,
                "현재 검색 기능을 사용할 수 없습니다.",
                Toast.LENGTH_SHORT,
            ).show()
    }

    override fun copyClientId() {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip: ClipData = ClipData.newPlainText(null, coursePickApplication.installationId.value)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "사용자 ID가 복사됐습니다.", Toast.LENGTH_SHORT).show()
    }

    override fun showCourseColorDescription() {
        supportFragmentManager.findFragmentByTag(COURSE_COLOR_DIALOG_TAG)
            ?: CourseColorDescriptionDialog().show(supportFragmentManager, COURSE_COLOR_DIALOG_TAG)
    }

    override fun clearQuery() {
        viewModel.setQuery("")
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun onReconnect() {
        val coordinate = mapManager.cameraPosition?.toCoordinate()
        if (coordinate != null) {
            fetchCourses(coordinate, Scope.default())
        } else {
            mapManager.fetchCurrentLocation(
                onSuccess = { lat, lng ->
                    fetchCourses(Coordinate(lat, lng), Scope.default())
                },
                onFailure = {
                    Toast
                        .makeText(this, "위치 정보를 가져올 수 없어 데이터를 갱신할 수 없습니다.", Toast.LENGTH_SHORT)
                        .show()
                },
            )
        }
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun searchActivityResultLauncher(): ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                handleLocationResult(result.data)
            }
        }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun handleLocationResult(intent: Intent?) {
        selectMenuWithoutListener(R.id.coursesMenu)

        if (intent == null) {
            Toast.makeText(this@CoursesActivity, "검색 정보가 전달되지 않았습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val query: String? = intent.getStringExtra(DataKeys.DATA_KEY_PLACE_NAME)
        if (!query.isNullOrBlank()) {
            viewModel.setQuery(query)
        }

        if (!intent.hasExtra(DataKeys.DATA_KEY_PLACE_LATITUDE) || !intent.hasExtra(DataKeys.DATA_KEY_PLACE_LONGITUDE)) {
            Toast.makeText(this, "위치 정보가 전달되지 않았습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val latitudeValue = intent.getDoubleExtra(DataKeys.DATA_KEY_PLACE_LATITUDE, 0.0)
        val longitudeValue = intent.getDoubleExtra(DataKeys.DATA_KEY_PLACE_LONGITUDE, 0.0)

        val latitude = Latitude(latitudeValue)
        val longitude = Longitude(longitudeValue)
        val coordinate = Coordinate(latitude, longitude)

        mapManager.resetZoomLevel()
        mapManager.showSearchPosition(coordinate)
        mapManager.moveTo(latitude, longitude)
        fetchCourses(coordinate, Scope.default())
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun switchContent(content: CoursesContent) {
        binding.mainCoursesHeader.text = getString(content.headerId)
        binding.mainEmptyCourses.text =
            when (content) {
                CoursesContent.EXPLORE -> getString(R.string.main_empty_courses_description)
                CoursesContent.FAVORITES -> getString(R.string.main_empty_favorites_description)
            }

        supportFragmentManager.commit {
            setReorderingAllowed(true)
            supportFragmentManager.fragments.forEach { fragment: Fragment ->
                if (fragment is ExploreCoursesFragment || fragment is FavoriteCoursesFragment) {
                    hide(fragment)
                }
            }
            supportFragmentManager
                .findFragmentByTag(content.fragmentClass.name)
                ?.let(::show)
                ?: run {
                    add(
                        R.id.mainFragmentContainer,
                        content.fragmentClass,
                        null,
                        content.fragmentClass.name,
                    )
                }
        }
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun selectMenuWithoutListener(
        @IdRes menuId: Int,
    ) {
        binding.mainBottomNavigation.setOnItemSelectedListener(null)
        when (menuId) {
            R.id.coursesMenu -> {
                switchContent(CoursesContent.EXPLORE)
                binding.mainBottomNavigation.selectedItemId = menuId
            }

            R.id.favoritesMenu -> {
                switchContent(CoursesContent.FAVORITES)
                binding.mainBottomNavigation.selectedItemId = menuId
            }

            else -> Unit
        }
        setUpBottomNavigation()
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun setUpBottomNavigation() {
        binding.mainBottomNavigation.setOnItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.coursesMenu -> {
                    switchContent(CoursesContent.EXPLORE)
                    mapManager.cameraPosition?.toCoordinate()?.let { mapCoordinate: Coordinate ->
                        viewModel.fetchCourses(
                            mapCoordinate = mapCoordinate,
                            userCoordinate = null,
                        )
                    }
                    true
                }

                R.id.favoritesMenu -> {
                    switchContent(CoursesContent.FAVORITES)
                    viewModel.fetchFavorites()
                    true
                }

                else -> false
            }
        }
    }

    private fun navigateToPreferences() {
        Logger.log(Logger.Event.Click("navigate_to_preferences"))
        val intent = Intent(this, PreferencesActivity::class.java)
        startActivity(intent)
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun moveToCurrentLocation() {
        Logger.log(Logger.Event.Click("move_to_current_location"))
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            showFineLocationPermissionRationaleDialog()
        } else {
            mapManager.showCurrentLocation {
                binding.mainCurrentLocationButton.setColorFilter(
                    ContextCompat.getColor(
                        this,
                        R.color.gray3,
                    ),
                )
            }
        }
    }

    private fun showFineLocationPermissionRationaleDialog() {
        val message: String =
            getString(
                if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    R.string.fine_location_permission_rationale_dialog_with_coarse_permission_message
                } else {
                    R.string.fine_location_permission_rationale_dialog_without_coarse_permission_message
                },
            )

        AlertDialog
            .Builder(this)
            .setMessage(message)
            .setPositiveButton(
                getString(R.string.fine_location_permission_rationale_dialog_positive_button),
            ) { dialog: DialogInterface, _ ->
                dialog.dismiss()
            }.setNegativeButton(
                getString(R.string.fine_location_permission_rationale_dialog_negative_button),
            ) { dialog: DialogInterface, _ ->
                val intent =
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = "package:$packageName".toUri()
                    }
                startActivity(intent)
                dialog.dismiss()
            }.show()
    }

    private fun navigateToFeedback() {
        Logger.log(Logger.Event.Click("navigate_to_feedback"))
        val intent =
            Intent(
                Intent.ACTION_VIEW,
                getString(
                    R.string.feedback_url,
                    """
                    사용자 ID: ${coursePickApplication.installationId.value}%0A
                    앱 버전: ${BuildConfig.VERSION_NAME}%0A
                    안드로이드 버전: ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})%0A
                    사용 기기: ${Build.MANUFACTURER} ${Build.MODEL}%0A
                    발생한 버그:%0A
                    """.trimIndent(),
                ).toUri(),
            )
        startActivity(intent)
    }

    private fun navigateToPrivacyPolicy() {
        Logger.log(Logger.Event.Click("navigate_to_privacy_policy"))
        val intent = Intent(Intent.ACTION_VIEW, getString(R.string.privacy_policy_url).toUri())

        startActivity(intent)
    }

    private fun navigateToOpenSourceNotice() {
        Logger.log(Logger.Event.Click("navigate_to_open_source_notice"))
        startActivity(Intent(this, OssLicensesMenuActivity::class.java))
    }

    private fun handleNavigation(
        course: CourseItem,
        origin: Coordinate,
        selectedApp: RouteFinderApplication,
    ) {
        when (selectedApp) {
            is RouteFinderApplication.InApp -> {
                viewModel.fetchRouteToCourse(course, origin)
            }

            is RouteFinderApplication.KakaoMap -> {
                viewModel.fetchNearestCoordinate(
                    course,
                    origin,
                    RouteFinderApplication.KakaoMap,
                )
            }

            is RouteFinderApplication.NaverMap -> {
                viewModel.fetchNearestCoordinate(
                    course,
                    origin,
                    RouteFinderApplication.NaverMap,
                )
            }
        }
    }

    private fun setUpFragmentFactory() {
        supportFragmentManager.fragmentFactory =
            object : FragmentFactory() {
                override fun instantiate(
                    classLoader: ClassLoader,
                    className: String,
                ): Fragment =
                    when (className) {
                        ExploreCoursesFragment::class.java.name -> {
                            ExploreCoursesFragment(courseItemListener)
                        }

                        FavoriteCoursesFragment::class.java.name -> {
                            FavoriteCoursesFragment(courseItemListener)
                        }

                        else -> {
                            super.instantiate(classLoader, className)
                        }
                    }
            }
    }

    private fun setUpNavigation(systemBars: Insets) {
        binding.mainNavigation.setPadding(0, 0, 0, systemBars.bottom)
    }

    private fun setUpMapPadding(systemBars: Insets) {
        val bottomSheet = binding.mainBottomSheet
        val screenHeight = Resources.getSystem().displayMetrics.heightPixels
        mapManager.setBottomPadding(screenHeight - systemBars.bottom - bottomSheet.height)
    }

    private fun setUpBottomSheet(systemBars: Insets) {
        val bottomSheet = binding.mainBottomSheet
        val screenHeight = Resources.getSystem().displayMetrics.heightPixels

        bottomSheet.layoutParams.height = screenHeight / 2
        bottomSheet.setPadding(
            systemBars.left,
            0,
            systemBars.right,
            systemBars.bottom + binding.mainBottomNavigation.height,
        )

        val behavior = BottomSheetBehavior.from(bottomSheet)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.addBottomSheetCallback(
            object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(
                    bottomSheet: View,
                    newState: Int,
                ) = Unit

                override fun onSlide(
                    bottomSheet: View,
                    slideOffset: Float,
                ) {
                    mapManager.setBottomPadding(
                        screenHeight - systemBars.bottom - bottomSheet.y.toInt(),
                    )
                }
            },
        )
    }

    private fun setUpBindingVariables() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.action = this
        binding.clientId = coursePickApplication.installationId.value
        binding.listener = this
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun fetchCourses(
        mapCenter: Coordinate,
        scope: Scope,
    ) {
        mapManager.fetchCurrentLocation(
            onSuccess = { userLatitude: Latitude, userLongitude: Longitude ->
                val userCoordinate = Coordinate(userLatitude, userLongitude)
                viewModel.fetchCourses(mapCenter, userCoordinate, scope)
            },
            onFailure = {
                viewModel.fetchCourses(mapCenter, null, scope)
            },
        )
    }

    private fun setUpDoubleBackPress() {
        val callback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (binding.mainDrawer.isOpen) {
                        binding.mainDrawer.close()
                        return
                    }

                    if (doublePressDetector.doublePressed()) {
                        finish()
                    } else {
                        Toast
                            .makeText(
                                this@CoursesActivity,
                                getString(R.string.main_back_press_exit),
                                Toast.LENGTH_SHORT,
                            ).show()
                    }
                }
            }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun setUpObservers() {
        setUpStateObserver()
        setUpEventObserver()
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun setUpStateObserver() {
        viewModel.state.observe(this) { state: CoursesUiState ->
            courseAdapter.submitList(state.courses)
            mapManager.removeAllLines()
            mapManager.setOnCourseClickListener(state.courses) { course: CourseItem ->
                viewModel.select(course)
            }
            mapManager.draw(state.courses)
        }
    }

    private fun setUpEventObserver() {
        viewModel.event.observe(this) { event: CoursesUiEvent ->
            when (event) {
                CoursesUiEvent.FetchCourseFailure -> {
                    binding.mainSearchThisAreaButton.visibility = View.VISIBLE
                    Toast.makeText(this, "코스 정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }

                is CoursesUiEvent.SelectCourseManually -> {
                    mapManager.fitTo(event.course)
                    val fragment =
                        supportFragmentManager.findFragmentByTag(
                            ExploreCoursesFragment::class.java.name,
                        ) as? ExploreCoursesFragment
                    fragment?.scrollTo(event.course)
                }

                is CoursesUiEvent.FetchRouteToCourseSuccess -> {
                    mapManager.removeAllLines()
                    mapManager.fitTo(event.route)
                    mapManager.draw(event.course)
                    mapManager.drawRouteToCourse(event.route, event.course)
                }

                is CoursesUiEvent.FetchRouteToCourseFailure -> {
                    Toast
                        .makeText(this, "코스까지 가는 길을 찾지 못했습니다.", Toast.LENGTH_SHORT)
                        .show()
                }

                is CoursesUiEvent.FetchNearestCoordinateSuccess -> {
                    event.routeFinder.launch(
                        this@CoursesActivity,
                        event.origin,
                        event.destination,
                        event.destinationName,
                    )
                }

                CoursesUiEvent.FetchNearestCoordinateFailure ->
                    Toast
                        .makeText(this, "코스까지 가는 길을 찾지 못했습니다.", Toast.LENGTH_SHORT)
                        .show()

                is CoursesUiEvent.ShowNotice -> {
                    NoticeDialogFragment.show(
                        fragmentManager = supportFragmentManager,
                        notice = event.notice,
                        onDoNotShowAgain = {
                            CoursePickPreferences.setDoNotShowNotice(event.notice.id)
                        },
                    )
                }
            }
        }
    }

    private fun requestLocationPermissions() {
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ),
        )
    }

    private fun showNoticeIfNeeded() {
        if (coursePickApplication.hasShownNoticeThisSession) {
            return
        }

        val noticeId = NOTICE_ID_VERIFIED_LOCATION
        if (!CoursePickPreferences.shouldShowNotice(noticeId)) {
            return
        }

        coursePickApplication.markNoticeAsShown()
        viewModel.fetchNotice(noticeId)
    }

    private companion object {
        const val COURSE_COLOR_DIALOG_TAG = "CourseColorDescriptionDialog"
        const val NOTICE_ID_VERIFIED_LOCATION = "verified_location"
    }
}
