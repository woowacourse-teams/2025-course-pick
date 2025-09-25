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
import io.coursepick.coursepick.domain.search.Place
import io.coursepick.coursepick.presentation.IntentKeys
import io.coursepick.coursepick.presentation.Logger
import io.coursepick.coursepick.presentation.search.ui.theme.CoursePickTheme

class NewSearchActivity : ComponentActivity() {
    private val viewModel: NewSearchViewModel by viewModels { NewSearchViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CoursePickTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding: PaddingValues ->

                    val state: NewSearchUiState? by viewModel.state.observeAsState()

                    SearchScreen(
                        uiState =
                            state ?: NewSearchUiState(
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
                putExtra(IntentKeys.EXTRA_KEYS_PLACE_LATITUDE, place.latitude)
                putExtra(IntentKeys.EXTRA_KEYS_PLACE_LONGITUDE, place.longitude)
                putExtra(IntentKeys.EXTRA_KEYS_PLACE_NAME, place.placeName)
            }
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    companion object {
        fun intent(context: Context): Intent = Intent(context, NewSearchActivity::class.java)
    }
}
