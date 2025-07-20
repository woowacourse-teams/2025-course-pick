package io.coursepick.coursepick.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import io.coursepick.coursepick.R
import io.coursepick.coursepick.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val viewModel: MainViewModel by viewModels()
    private val courseAdapter by lazy { CourseAdapter(viewModel::select) }
    private val doubleBackPressDetector = DoubleBackPressDetector()

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
        setUpObservers(courseAdapter)
        setUpDoubleBackPress()
    }

    private fun setUpDoubleBackPress() {
        val callback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (doubleBackPressDetector.doubleBackPressed()) {
                        finish()
                    } else {
                        Toast
                            .makeText(
                                this@MainActivity,
                                getString(R.string.toast_back_press_exit),
                                Toast.LENGTH_SHORT,
                            ).show()
                    }
                }
            }
        onBackPressedDispatcher.addCallback(this@MainActivity, callback)
    }

    private fun setUpObservers(courseAdapter: CourseAdapter) {
        viewModel.state.observe(this) { state: MainUiState ->
            courseAdapter.submitList(state.courses)
        }
    }
}
