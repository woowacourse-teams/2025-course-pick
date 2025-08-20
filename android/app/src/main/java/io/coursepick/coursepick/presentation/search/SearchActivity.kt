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
import io.coursepick.coursepick.presentation.IntentKeys
import io.coursepick.coursepick.presentation.Logger

class SearchActivity : AppCompatActivity() {
    private val binding by lazy { ActivitySearchBinding.inflate(layoutInflater) }
    private val viewModel: SearchViewModel by viewModels { SearchViewModel.Factory }
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

        setUpBindingVariables()
        setUpViews()
        setUpObservers()
        intent.getStringExtra(IntentKeys.EXTRA_KEYS_PLACE_NAME)?.let { placeName: String ->
            binding.searchView.setQuery(placeName, false)
        }
    }

    private fun setUpBindingVariables() {
        binding.viewModel = viewModel
        binding.adapter = adapter
        binding.lifecycleOwner = this
    }

    private fun setUpViews() {
        binding.searchView.requestFocus()
        binding.searchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean = true

                override fun onQueryTextChange(newText: String?): Boolean {
                    viewModel.updateQueryState(newText.isNullOrBlank())
                    viewModel.search(newText.orEmpty())
                    return true
                }
            },
        )
    }

    private fun setUpObservers() {
        viewModel.state.observe(this) { state: SearchUiState ->
            adapter.submitList(state.places)
        }
    }

    private fun submitPlace(place: Place) {
        Logger.log(Logger.Event.Click("place"), "place" to place)
        val resultIntent =
            Intent().apply {
                putExtra(IntentKeys.EXTRA_KEYS_PLACE_LATITUDE, place.latitude)
                putExtra(IntentKeys.EXTRA_KEYS_PLACE_LONGITUDE, place.longitude)
                putExtra(IntentKeys.EXTRA_KEYS_PLACE_NAME, place.placeName)
            }
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    companion object {
        fun intent(context: Context): Intent = Intent(context, SearchActivity::class.java)
    }
}
