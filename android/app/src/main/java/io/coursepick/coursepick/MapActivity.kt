package io.coursepick.coursepick

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import io.coursepick.coursepick.databinding.ActivityMapBinding

class MapActivity : AppCompatActivity() {
    private val binding: ActivityMapBinding by lazy { ActivityMapBinding.inflate(layoutInflater) }
    private val mapManager: KakaoMapManager by lazy { KakaoMapManager(binding.mapMap) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mapManager.init()
    }

    override fun onResume() {
        super.onResume()

        mapManager.resume()
    }

    override fun onPause() {
        super.onPause()

        mapManager.pause()
    }
}
