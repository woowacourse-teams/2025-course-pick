package io.coursepick.coursepick.presentation.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import io.coursepick.coursepick.domain.search.Place
import io.coursepick.coursepick.presentation.DataKeys
import io.coursepick.coursepick.presentation.Logger
import io.coursepick.coursepick.presentation.search.ui.theme.CoursePickTheme

@AndroidEntryPoint
class SearchActivity : ComponentActivity() {
    private val viewModel: SearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CoursePickTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding: PaddingValues ->

                    val state: SearchUiState? by viewModel.state.observeAsState()

                    SearchScreen(
                        uiState =
                            state ?: SearchUiState(
                                isLoading = false,
                                query = "",
                                places = emptyList(),
                            ),
                        onQueryChange = viewModel::search,
                        onPlaceSelect = ::submit,
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }

    private fun submit(place: Place) {
        Logger.log(Logger.Event.Click("place"), "place" to place)
        val resultIntent =
            Intent().apply {
                putExtra(DataKeys.DATA_KEY_PLACE_LATITUDE, place.latitude)
                putExtra(DataKeys.DATA_KEY_PLACE_LONGITUDE, place.longitude)
                putExtra(DataKeys.DATA_KEY_PLACE_NAME, place.placeName)
            }
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    companion object {
        fun intent(context: Context): Intent = Intent(context, SearchActivity::class.java)
    }
}
