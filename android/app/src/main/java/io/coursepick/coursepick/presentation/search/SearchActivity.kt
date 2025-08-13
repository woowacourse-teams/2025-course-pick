package io.coursepick.coursepick.presentation.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import io.coursepick.coursepick.databinding.ActivitySearchBinding
import io.coursepick.coursepick.domain.search.Place
import io.coursepick.coursepick.presentation.CoordinateKeys
import io.coursepick.coursepick.presentation.Logger

class SearchActivity : AppCompatActivity() {
    private val binding by lazy { ActivitySearchBinding.inflate(layoutInflater) }
    private val viewModel: SearchViewModel by viewModels()
    private val adapter: SearchAdapter by lazy { SearchAdapter(::submitPlace) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view: View, insets: WindowInsetsCompat ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        Logger.log(Logger.Event.Enter("search"))

        setUpBindingVariables()
        setUpObserves()
    }

    private fun setUpObserves() {
        viewModel.state.observe(this) { state: List<Place> ->
            adapter.submitList(state)
        }
    }

    private fun setUpBindingVariables() {
        binding.searchView.requestFocus()
        binding.adapter = adapter
        binding.searchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean = true

                override fun onQueryTextChange(newText: String?): Boolean {
                    viewModel.search(newText.orEmpty())
                    return true
                }
            },
        )
    }

    private fun submitPlace(place: Place) {
        val resultIntent =
            Intent().apply {
                putExtra(CoordinateKeys.EXTRA_KEYS_LATITUDE, place.latitude)
                putExtra(CoordinateKeys.EXTRA_KEYS_LONGITUDE, place.longitude)
            }
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    override fun onDestroy() {
        Logger.log(Logger.Event.Exit("search"))

        super.onDestroy()
    }

    companion object {
        fun intent(context: Context): Intent = Intent(context, SearchActivity::class.java)
    }
}
