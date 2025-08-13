package io.coursepick.coursepick.presentation.course

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.Insets
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.kakao.vectormap.LatLng
import io.coursepick.coursepick.R
import io.coursepick.coursepick.databinding.ActivityCoursesBinding
import io.coursepick.coursepick.domain.Coordinate
import io.coursepick.coursepick.domain.Latitude
import io.coursepick.coursepick.domain.Longitude
import io.coursepick.coursepick.presentation.DoublePressDetector
import io.coursepick.coursepick.presentation.SearchActivity
import io.coursepick.coursepick.presentation.view.map.kakao.KakaoMapManager
import io.coursepick.coursepick.presentation.view.map.kakao.toCoordinate
import io.coursepick.coursepick.presentation.view.preference.CoursePickPreferences
import io.coursepick.coursepick.presentation.view.preference.PreferencesActivity
import io.coursepick.coursepick.presentation.view.routetfinder.RouteFinderApplication
import io.coursepick.coursepick.presentation.view.routetfinder.RouteFinderChoiceDialogFragment
import io.coursepick.coursepick.util.CoordinateKeys
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CoursesActivity :
    AppCompatActivity(),
    CoursesAction {
    private var searchLauncher: ActivityResultLauncher<Intent>? = null
    private val binding by lazy { ActivityCoursesBinding.inflate(layoutInflater) }
    private val viewModel: CoursesViewModel by viewModels()
    private val courseAdapter by lazy { CourseAdapter(CourseItemListener()) }
    private val doublePressDetector = DoublePressDetector()
    private val mapManager by lazy { KakaoMapManager(binding.mainMap) }

    @SuppressLint("MissingPermission")
    private val locationPermissionLauncher: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions(),
        ) {
            mapManager.startTrackingCurrentLocation()
        }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view: View, insets: WindowInsetsCompat ->
            val systemBars: Insets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            setUpBottomSheet(insets)
            insets
        }

        setUpBindingVariables()
        setUpObservers()
        setUpDoubleBackPress()
        requestLocationPermissions()

        mapManager.start { coordinate: Coordinate ->
            mapManager.setOnCameraMoveListener {
                binding.mainSearchThisAreaButton.visibility = View.VISIBLE
            }
            fetchCourses(coordinate)
        }

        searchLauncher = searchActivityResultLauncher()
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun onResume() {
        super.onResume()

        mapManager.resume()
        mapManager.startTrackingCurrentLocation()
    }

    override fun onPause() {
        super.onPause()

        mapManager.pause()
        mapManager.stopTrackingCurrentLocation()
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun searchThisArea() {
        val mapPosition: LatLng = mapManager.cameraPosition ?: return
        val coordinate = mapPosition.toCoordinate()
        binding.mainSearchThisAreaButton.visibility = View.GONE
        mapManager.showSearchPosition(coordinate)
        fetchCourses(coordinate)
    }

    override fun openMenu() {
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
        val intent = SearchActivity.Companion.intent(this)
        searchLauncher?.launch(intent) ?: Toast
            .makeText(
                this,
                "현재 검색 기능을 사용할 수 없습니다.",
                Toast.LENGTH_SHORT,
            ).show()
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
        val latitudeExtraKey = CoordinateKeys.EXTRA_KEYS_LATITUDE
        val longitudeExtraKey = CoordinateKeys.EXTRA_KEYS_LONGITUDE
        if (intent == null ||
            !intent.hasExtra(latitudeExtraKey) ||
            !intent.hasExtra(
                longitudeExtraKey,
            )
        ) {
            Toast.makeText(this, "위치 정보가 전달되지 않았습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val latitudeValue = intent.getDoubleExtra(latitudeExtraKey, 0.0)
        val longitudeValue = intent.getDoubleExtra(longitudeExtraKey, 0.0)

        val latitude = Latitude(latitudeValue)
        val longitude = Longitude(longitudeValue)

        mapManager.moveTo(latitude, longitude)
        fetchCourses(Coordinate(latitude, longitude))
    }

    private fun navigateToPreferences() {
        val intent = Intent(this, PreferencesActivity::class.java)
        startActivity(intent)
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun moveToCurrentLocation() {
        mapManager.moveToCurrentLocation()
    }

    private fun navigateToFeedback() {
        val intent = Intent(Intent.ACTION_VIEW, getString(R.string.feedback_url).toUri())

        startActivity(intent)
    }

    private fun navigateToPrivacyPolicy() {
        val intent = Intent(Intent.ACTION_VIEW, getString(R.string.privacy_policy_url).toUri())

        startActivity(intent)
    }

    private fun navigateToOpenSourceNotice() {
        startActivity(Intent(this, OssLicensesMenuActivity::class.java))
    }

    private fun CourseItemListener(): CourseItemListener =
        object : CourseItemListener {
            override fun select(course: CourseItem) {
                viewModel.select(course)
            }

            @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
            override fun navigateToMap(course: CourseItem) {
                mapManager.fetchCurrentLocation(
                    onSuccess = { latitude: Latitude, longitude: Longitude ->
                        viewModel.fetchNearestCoordinate(course, Coordinate(latitude, longitude))
                    },
                    onFailure = {
                        Toast
                            .makeText(this@CoursesActivity, "현재 위치를 가져올 수 없어요.", Toast.LENGTH_SHORT)
                            .show()
                    },
                )
            }
        }

    private fun setUpBottomSheet(insets: WindowInsetsCompat) {
        val bottomSheet = binding.mainBottomSheet
        val screenHeight = Resources.getSystem().displayMetrics.heightPixels
        val systemBars: Insets = insets.getInsets(WindowInsetsCompat.Type.systemBars())

        bottomSheet.layoutParams.height = screenHeight / 2
        bottomSheet.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
        mapManager.setBottomPadding(screenHeight - systemBars.bottom - bottomSheet.height)

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
        binding.adapter = courseAdapter
        binding.action = this
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun fetchCourses(mapCoordinate: Coordinate) {
        mapManager.fetchCurrentLocation(
            onSuccess = { userLatitude: Latitude, userLongitude: Longitude ->
                val userCoordinate = Coordinate(userLatitude, userLongitude)
                viewModel.fetchCourses(mapCoordinate, userCoordinate)
            },
            onFailure = {
                viewModel.fetchCourses(mapCoordinate)
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

    private fun setUpObservers() {
        setUpStateObserver()
        setUpEventObserver()
    }

    private fun setUpStateObserver() {
        viewModel.state.observe(this) { state: CoursesUiState ->
            courseAdapter.submitList(state.courses)
            mapManager.setOnCourseClickListener(state.courses) { course: CourseItem ->
                viewModel.select(course)
            }
            mapManager.draw(state.courses)
        }
    }

    private fun setUpEventObserver() {
        viewModel.event.observe(this) { event: CoursesUiEvent ->
            when (event) {
                is CoursesUiEvent.FetchCourseSuccess -> {
                    event.nearestCourse ?: {
                        binding.mainSearchThisAreaButton.visibility = View.VISIBLE
                        Toast
                            .makeText(
                                this,
                                "이 지역에 코스가 없습니다.",
                                Toast.LENGTH_SHORT,
                            ).show()
                    }
                }

                CoursesUiEvent.FetchCourseFailure -> {
                    binding.mainSearchThisAreaButton.visibility = View.VISIBLE
                    Toast.makeText(this, "코스 정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }

                is CoursesUiEvent.SelectNewCourse -> {
                    selectCourse(event.course)
                    val behavior = BottomSheetBehavior.from(binding.mainBottomSheet)
                    behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }

                is CoursesUiEvent.FetchNearestCoordinateSuccess -> {
                    lifecycleScope.launch {
                        val selectedApp: RouteFinderApplication? =
                            withContext(Dispatchers.IO) {
                                CoursePickPreferences.selectedRouteFinder
                            }

                        selectedApp?.launch(
                            this@CoursesActivity,
                            event.origin,
                            event.destination,
                            event.destinationName,
                        ) ?: RouteFinderChoiceDialogFragment.Companion
                            .newInstance(
                                event.origin,
                                event.destination,
                                event.destinationName,
                            ).show(supportFragmentManager, null)
                    }
                }

                CoursesUiEvent.FetchNearestCoordinateFailure ->
                    Toast
                        .makeText(this, "코스까지 가는 길을 찾지 못했습니다.", Toast.LENGTH_SHORT)
                        .show()
            }
        }
    }

    private fun selectCourse(course: CourseItem) {
        mapManager.fitTo(course)
    }

    private fun requestLocationPermissions() {
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ),
        )
    }
}
