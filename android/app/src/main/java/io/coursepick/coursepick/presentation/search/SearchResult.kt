package io.coursepick.coursepick.presentation.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.coursepick.coursepick.R
import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.course.Latitude
import io.coursepick.coursepick.domain.course.Longitude
import io.coursepick.coursepick.domain.search.Place

@Composable
fun SearchResult(
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
