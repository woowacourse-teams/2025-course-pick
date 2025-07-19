package io.coursepick.coursepick

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import io.coursepick.coursepick.databinding.ActivityMapBinding
import io.coursepick.coursepick.domain.Coordinate
import io.coursepick.coursepick.domain.Course
import io.coursepick.coursepick.domain.CourseName
import io.coursepick.coursepick.domain.Distance
import io.coursepick.coursepick.domain.Latitude
import io.coursepick.coursepick.domain.Length
import io.coursepick.coursepick.domain.Longitude

class MapActivity : AppCompatActivity() {
    private val binding: ActivityMapBinding by lazy { ActivityMapBinding.inflate(layoutInflater) }
    private val mapManager: KakaoMapManager by lazy { KakaoMapManager(binding.mapMap) }
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

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        requestLocationPermissions()
        mapManager.start(course)
    }

    override fun onResume() {
        super.onResume()

        mapManager.resume()
    }

    override fun onPause() {
        super.onPause()

        mapManager.pause()
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

private val course =
    Course(
        id = 0,
        name = CourseName(value = "Seokchon-Lake"),
        distance = Distance(meter = 449),
        length = Length(meter = 449),
        coordinates =
            listOf(
                Coordinate(
                    latitude = Latitude(value = 37.509835),
                    longitude = Longitude(value = 127.102495),
                ),
                Coordinate(
                    latitude = Latitude(value = 37.510367),
                    longitude = Longitude(value = 127.101655),
                ),
                Coordinate(
                    latitude = Latitude(value = 37.509204),
                    longitude = Longitude(value = 127.098165),
                ),
                Coordinate(
                    latitude = Latitude(value = 37.508038),
                    longitude = Longitude(value = 127.097838),
                ),
                Coordinate(
                    latitude = Latitude(value = 37.506873),
                    longitude = Longitude(value = 127.09791),
                ),
                Coordinate(
                    latitude = Latitude(value = 37.507053),
                    longitude = Longitude(value = 127.09991),
                ),
                Coordinate(
                    latitude = Latitude(value = 37.508496),
                    longitude = Longitude(value = 127.102865),
                ),
                Coordinate(
                    latitude = Latitude(value = 37.509548),
                    longitude = Longitude(value = 127.102787),
                ),
                Coordinate(
                    latitude = Latitude(value = 37.509928),
                    longitude = Longitude(value = 127.103403),
                ),
                Coordinate(
                    latitude = Latitude(value = 37.509479),
                    longitude = Longitude(value = 127.104189),
                ),
                Coordinate(
                    latitude = Latitude(value = 37.511344),
                    longitude = Longitude(value = 127.106898),
                ),
                Coordinate(
                    latitude = Latitude(value = 37.512422),
                    longitude = Longitude(value = 127.107543),
                ),
                Coordinate(
                    latitude = Latitude(value = 37.513117),
                    longitude = Longitude(value = 127.107097),
                ),
                Coordinate(
                    latitude = Latitude(value = 37.512668),
                    longitude = Longitude(value = 127.105626),
                ),
                Coordinate(
                    latitude = Latitude(value = 37.511144),
                    longitude = Longitude(value = 127.102666),
                ),
                Coordinate(
                    latitude = Latitude(value = 37.510188),
                    longitude = Longitude(value = 127.103115),
                ),
                Coordinate(
                    latitude = Latitude(value = 37.509835),
                    longitude = Longitude(value = 127.102495),
                ),
            ),
    )
