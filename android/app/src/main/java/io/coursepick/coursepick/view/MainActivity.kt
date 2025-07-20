package io.coursepick.coursepick.view

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import io.coursepick.coursepick.R
import io.coursepick.coursepick.KakaoMapManager
import io.coursepick.coursepick.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val viewModel: MainViewModel by viewModels()
    private val courseAdapter by lazy { CourseAdapter(viewModel::select) }
    private val doublePressDetector = DoublePressDetector()
    private val mapManager: KakaoMapManager by lazy { KakaoMapManager(binding.mainMap) }
    private val locationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions(),
        ) { permissions ->
            val granted =
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (!granted) {
                Toast.makeText(this, "위치 권한이 없어 위치를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
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

        binding.adapter = courseAdapter
        setUpObservers()
        setUpDoubleBackPress()
        requestLocationPermissions()
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

    override fun onResume() {
        super.onResume()

        mapManager.resume()
    }

    override fun onPause() {
        super.onPause()

        mapManager.pause()
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun setUpObservers() {
        setUpStateObserver()
        setUpEventObserver()
    }

    private fun setUpStateObserver() {
        viewModel.state.observe(this) { state: MainUiState ->
            courseAdapter.submitList(state.courses)
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun setUpEventObserver() {
        viewModel.event.observe(this) { event: MainUiEvent ->
            when (event) {
                is MainUiEvent.FetchCourseSuccess -> {
                    mapManager.start {
                        selectCourse(event.course)
                    }
                }

                MainUiEvent.FetchCourseFailure ->
                    Toast
                        .makeText(
                            this,
                            "코스 정보를 불러오지 못했습니다.",
                            Toast.LENGTH_SHORT,
                        ).show()

                is MainUiEvent.SelectNewCourse -> {
                    selectCourse(event.course)
                }
            }
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun selectCourse(course: CourseItem) {
        mapManager.draw(course)
        mapManager.moveTo(course)
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
