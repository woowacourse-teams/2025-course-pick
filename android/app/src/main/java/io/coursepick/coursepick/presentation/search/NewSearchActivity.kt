package io.coursepick.coursepick.presentation.search

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import io.coursepick.coursepick.presentation.search.ui.theme.CoursePickTheme

class NewSearchActivity : ComponentActivity() {
    private val viewModel: NewSearchViewModel by viewModels { SearchViewModel.Factory }

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
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}

@Composable
fun SearchScreen(
    uiState: NewSearchUiState,
    onQueryChange: (query: String) -> Unit,
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
                    color = colorResource(R.color.gray3),
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
                        SearchResult(place = place, modifier = modifier.fillMaxWidth())
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

@Preview
@Composable
private fun SearchScreenPreview(
    @PreviewParameter(SearchScreenPreviewParameter::class) state: NewSearchUiState,
) {
    SearchScreen(
        uiState = state,
        onQueryChange = {},
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
