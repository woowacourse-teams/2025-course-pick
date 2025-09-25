package io.coursepick.coursepick.presentation.search

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.coursepick.coursepick.R
import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.course.Latitude
import io.coursepick.coursepick.domain.course.Longitude
import io.coursepick.coursepick.domain.search.Place
import io.coursepick.coursepick.presentation.search.ui.theme.CoursePickTheme

class NewSearchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CoursePickTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding: PaddingValues ->
//                    <androidx.constraintlayout.widget.ConstraintLayout
//
//
//                    <androidx.appcompat.widget.SearchView
//
//                    <androidx.core.widget.ContentLoadingProgressBar
//
//                    <androidx.recyclerview.widget.RecyclerView
//
//                    <TextView
//                    </androidx.constraintlayout.widget.ConstraintLayout>
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    query: String,
    onQueryChange: (query: String) -> Unit,
    places: List<Place>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(text = stringResource(R.string.search_query_hint))
            },
        )
        if (places.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.search_no_result),
                    fontSize = 20.sp,
                )
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(places) { place: Place ->
                    SearchResult(place = place, modifier = modifier.fillMaxWidth())
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
private fun SearchScreenPreview2() {
    SearchScreen(
        query = "화성 회의실",
        onQueryChange = { query: String -> },
        places = emptyList(),
        modifier = Modifier.fillMaxSize(),
    )
}

@Preview(showBackground = true)
@Composable
private fun SearchScreenPreview() {
    SearchScreen(
        query = "",
        onQueryChange = { query: String -> },
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
        modifier = Modifier.fillMaxSize(),
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
