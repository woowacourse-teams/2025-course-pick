package io.coursepick.coursepick.presentation.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.sp
import io.coursepick.coursepick.R
import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.course.Latitude
import io.coursepick.coursepick.domain.course.Longitude
import io.coursepick.coursepick.domain.search.Place

@Composable
fun SearchScreen(
    uiState: SearchUiState,
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
                            modifier = Modifier.fillMaxWidth().clickable { onPlaceSelect(place) },
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchScreenPreview(
    @PreviewParameter(SearchScreenPreviewParameter::class) state: SearchUiState,
) {
    SearchScreen(
        uiState = state,
        onQueryChange = {},
        onPlaceSelect = {},
        modifier = Modifier.fillMaxSize(),
    )
}

private class SearchScreenPreviewParameter : PreviewParameterProvider<SearchUiState> {
    override val values: Sequence<SearchUiState> =
        sequenceOf(
            SearchUiState(
                isLoading = false,
                query = "",
                places = emptyList(),
            ),
            SearchUiState(
                isLoading = true,
                query = "테크살롱",
                places = emptyList(),
            ),
            SearchUiState(
                isLoading = false,
                query = "테크살롱",
                places = emptyList(),
            ),
            SearchUiState(
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
