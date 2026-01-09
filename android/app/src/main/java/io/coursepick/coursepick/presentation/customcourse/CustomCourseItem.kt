package io.coursepick.coursepick.presentation.customcourse

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.res.TypedArrayUtils.getString
import io.coursepick.coursepick.R
import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.course.Course
import io.coursepick.coursepick.domain.course.CourseName
import io.coursepick.coursepick.domain.course.Distance
import io.coursepick.coursepick.domain.course.InclineSummary
import io.coursepick.coursepick.domain.course.InclineType
import io.coursepick.coursepick.domain.course.Latitude
import io.coursepick.coursepick.domain.course.Length
import io.coursepick.coursepick.domain.course.Longitude
import io.coursepick.coursepick.domain.course.Segment

@Composable
fun CustomCourseItem(
    course: CustomCourseUiModel,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .background(
                    colorResource(
                        if (course.selected) {
                            R.color.background_tertiary
                        } else {
                            R.color.background_primary
                        },
                    ),
                ).padding(horizontal = 16.dp, vertical = 10.dp)
                .fillMaxWidth(),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = course.course.name.value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.item_primary),
            )

            if (course.distance != null) {
                Text(
                    text =
                        stringResource(
                            id = R.string.main_course_distance_suffix,
                            course.distance,
                        ),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.point_primary),
                )
            }
        }

        Spacer(modifier = Modifier.size(10.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = painterResource(R.drawable.icon_length),
                        contentDescription = null,
                    )

                    Spacer(modifier = Modifier.size(6.dp))

                    Text(
                        text = "${course.course.length.meter.value}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.item_primary),
                    )

                    Spacer(modifier = Modifier.size(18.dp))

                    Image(
                        painter = painterResource(R.drawable.icon_road_type),
                        contentDescription = null,
                    )

                    Spacer(modifier = Modifier.size(6.dp))

                    Text(
                        text = course.course.roadType,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.item_primary),
                    )
                }

                Spacer(modifier = Modifier.size(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text =
                            stringResource(
                                id = R.string.item_course_summary_format,
                                course.course.difficulty,
                                stringResource(course.inclineSummaryStringResourceId),
                            ),
                        fontSize = 14.sp,
                        color = colorResource(R.color.item_primary),
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier =
                    Modifier
                        .clip(CircleShape)
                        .border(
                            width = 1.dp,
                            color = colorResource(R.color.background_border),
                            shape = CircleShape,
                        ).background(
                            color = colorResource(R.color.background_tertiary),
                        ).padding(
                            start = 2.dp,
                            end = 8.dp,
                            top = 6.dp,
                            bottom = 6.dp,
                        ),
            ) {
                Image(
                    painter = painterResource(R.drawable.icon_navigate),
                    contentDescription = null,
                )

                Text(
                    text = stringResource(R.string.item_course_navigate),
                    fontSize = 12.sp,
                    color = colorResource(R.color.item_primary),
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
fun CustomCourseItemPreview() {
    CustomCourseItem(
        course =
            CustomCourseUiModel(
                course =
                    Course(
                        id = "0",
                        name = CourseName("Preview Course"),
                        distance = Distance(123),
                        length = Length(456),
                        roadType = "보도",
                        difficulty = "쉬움",
                        inclineSummary = InclineSummary.MOSTLY_FLAT,
                        segments =
                            listOf(
                                Segment(
                                    inclineType = InclineType.UNKNOWN,
                                    coordinates =
                                        listOf(
                                            Coordinate(Latitude(0.0), Longitude(0.0)),
                                            Coordinate(Latitude(0.0), Longitude(0.0)),
                                        ),
                                ),
                            ),
                    ),
                selected = false,
            ),
    )
}
