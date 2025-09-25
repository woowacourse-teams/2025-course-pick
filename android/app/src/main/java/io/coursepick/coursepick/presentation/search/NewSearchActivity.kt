package io.coursepick.coursepick.presentation.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.coursepick.coursepick.R
import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.course.Latitude
import io.coursepick.coursepick.domain.course.Longitude
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

@Composable
fun SearchScreen(
    uiState: NewSearchUiState,
    onQueryChange: (query: String) -> Unit,
    onPlaceSelect: (place: Place) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        TextField(
            value = uiState.query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(fontSize = 18.sp),
            placeholder = {
                Text(
                    text = stringResource(R.string.search_query_hint),
                    color = colorResource(R.color.item_tertiary),
                    fontSize = 18.sp,
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                )
            },
        )
        when {
            uiState.isLoading -> {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator()
                }
            }

            uiState.query.isNotBlank() && uiState.places.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(R.string.search_no_result),
                        fontSize = 20.sp,
                    )
                }
            }

            else -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(uiState.places) { place: Place ->
                        SearchResult(
                            place = place,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onPlaceSelect(place) })
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResult(
    place: Place,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(horizontal = 20.dp)) {
        Text(
            text = place.placeName,
            fontWeight = FontWeight.Bold,
            color = colorResource(R.color.item_primary),
            modifier =
                Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth(),
        )

        Text(
            text = place.addressName,
            color = colorResource(R.color.item_secondary),
            modifier =
                Modifier
                    .padding(vertical = 10.dp)
                    .fillMaxWidth(),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchScreenPreview(
    @PreviewParameter(SearchScreenPreviewParameter::class) state: NewSearchUiState,
) {
    SearchScreen(
        uiState = state,
        onQueryChange = {},
        onPlaceSelect = {},
        modifier = Modifier.fillMaxSize(),
    )
}

private class SearchScreenPreviewParameter : PreviewParameterProvider<NewSearchUiState> {
    override val values: Sequence<NewSearchUiState> =
        sequenceOf(
            NewSearchUiState(
                isLoading = false,
                query = "",
                places = emptyList(),
            ),
            NewSearchUiState(
                isLoading = true,
                query = "테크살롱",
                places = emptyList(),
            ),
            NewSearchUiState(
                isLoading = false,
                query = "테크살롱",
                places = emptyList(),
            ),
            NewSearchUiState(
                isLoading = false,
                query = "테크살롱",
                places =
                    List(10) {
                        Place(
                            id = 0,
                            addressName = "서울 송파구 신천동 7-20",
                            placeName = "검색어",
                            coordinate =
                                Coordinate(
                                    latitude = Latitude(0.0),
                                    longitude = Longitude(0.0),
                                ),
                        )
                    },
            ),
        )
}

@Preview(showBackground = true)
@Composable
private fun SearchResultPreview() {
    SearchResult(
        Place(
            id = 0,
            addressName = "서울 송파구 신천동 7-20",
            placeName = "검색어",
            coordinate =
                Coordinate(
                    latitude = Latitude(0.0),
                    longitude = Longitude(0.0),
                ),
        ),
    )
}
