package io.coursepick.coursepick.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import io.coursepick.coursepick.R
import io.coursepick.coursepick.databinding.ActivityMainBinding
import io.coursepick.coursepick.domain.Coordinate
import io.coursepick.coursepick.domain.Latitude
import io.coursepick.coursepick.domain.Longitude

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val viewModel: MainViewModel by viewModels()
    private val courseAdapter by lazy {
        CourseAdapter(
            object : CourseItemListener {
                override fun select(course: CourseItem) {
                    viewModel.select(course)
                }

                @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
                override fun navigate(course: CourseItem) {
                    mapManager.fetchCurrentLocation(
                        onSuccess = { latitude, longitude ->
                            val url =
                                viewModel.navigate(
                                    selectedCourse = course,
                                    location =
                                        Coordinate(
                                            latitude = latitude,
                                            longitude = longitude,
                                        ),
                                )

                            if (url.isBlank()) {
                                Toast
                                    .makeText(this@MainActivity, "유효하지 않은 URL", Toast.LENGTH_SHORT)
                                    .show()
                                return@fetchCurrentLocation
                            }

                            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                            startActivity(intent)
                        },
                        onFailure = {
                            Toast
                                .makeText(
                                    this@MainActivity,
                                    "현재 위치를 가져올 수 없어요.",
                                    Toast.LENGTH_SHORT,
                                ).show()
                        },
                    )
                }
            },
        )
    }
    private val doublePressDetector = DoublePressDetector()
    private val mapManager by lazy { KakaoMapManager(binding.mainMap) }
    private val onSearchThisAreaListener: OnSearchThisAreaListener =
        object : OnSearchThisAreaListener {
            override fun search() {
                val mapPosition = mapManager.cameraPosition ?: return
                viewModel.fetchCourses(
                    Coordinate(
                        Latitude(mapPosition.latitude),
                        Longitude(mapPosition.longitude),
                    ),
                )
            }
        }

    @SuppressLint("MissingPermission")
    private val locationPermissionLauncher: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions(),
        ) { permissions: Map<String, @JvmSuppressWildcards Boolean> ->
            mapManager.startTrackingCurrentLocation()
        }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            binding.mainCourses.setPadding(0, 0, 0, systemBars.bottom)
            insets
        }

        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.adapter = courseAdapter
        binding.onSearchThisAreaListener = onSearchThisAreaListener

        setUpObservers()
        setUpDoubleBackPress()
        requestLocationPermissions()

        mapManager.start { coordinate: Coordinate ->
            viewModel.fetchCourses(coordinate)
        }
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

    private fun setUpDoubleBackPress() {
        val callback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (doublePressDetector.doublePressed()) {
                        finish()
                    } else {
                        Toast
                            .makeText(
                                this@MainActivity,
                                getString(R.string.main_back_press_exit),
                                Toast.LENGTH_SHORT,
                            ).show()
                    }
                }
            }
        onBackPressedDispatcher.addCallback(this@MainActivity, callback)
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun setUpObservers() {
        setUpStateObserver()
        setUpEventObserver()
    }

    private fun setUpStateObserver() {
        viewModel.state.observe(this) { state: MainUiState ->
            courseAdapter.submitList(state.courses)
        }
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun setUpEventObserver() {
        viewModel.event.observe(this) { event: MainUiEvent ->
            when (event) {
                is MainUiEvent.FetchCourseSuccess -> {
                    event.course?.let { course: CourseItem ->
                        mapManager.draw(course)
                    }
                }

                MainUiEvent.FetchCourseFailure -> {
                    Toast
                        .makeText(
                            this,
                            "코스 정보를 불러오지 못했습니다.",
                            Toast.LENGTH_SHORT,
                        ).show()

                    mapManager.start {}
                }

                is MainUiEvent.SelectNewCourse -> {
                    selectCourse(event.course)
                    val behavior = BottomSheetBehavior.from(binding.mainBottomSheet)
                    behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }
        }
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun selectCourse(course: CourseItem) {
        mapManager.draw(course)
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
