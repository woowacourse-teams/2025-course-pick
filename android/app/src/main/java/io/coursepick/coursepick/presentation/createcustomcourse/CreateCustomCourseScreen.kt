package io.coursepick.coursepick.presentation.createcustomcourse

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.coursepick.coursepick.R
import io.coursepick.coursepick.domain.course.Length
import io.coursepick.coursepick.presentation.toDistanceText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCustomCourseScreen(
    length: Length,
    onClose: () -> Unit,
    onConfirm: () -> Unit,
    onUndoWaypoint: () -> Unit,
    onAddWaypoint: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .fillMaxSize()
            .navigationBarsPadding(),
    ) {
        Column {
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(R.string.create_custom_course_header)) },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.create_custom_course_exit_button_description),
                        modifier =
                            Modifier
                                .padding(start = 10.dp)
                                .size(48.dp)
                                .clip(CircleShape)
                                .clickable { onClose() }
                                .padding(10.dp),
                    )
                },
                actions = {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = stringResource(R.string.create_custom_course_confirm_button_description),
                        modifier =
                            Modifier
                                .padding(end = 10.dp)
                                .size(48.dp)
                                .clip(CircleShape)
                                .clickable { onConfirm() }
                                .padding(10.dp),
                    )
                },
                colors =
                    TopAppBarColors(
                        containerColor = colorResource(R.color.background_primary),
                        scrolledContainerColor = colorResource(R.color.background_primary),
                        navigationIconContentColor = colorResource(R.color.item_primary),
                        titleContentColor = colorResource(R.color.item_primary),
                        actionIconContentColor = colorResource(R.color.item_primary),
                    ),
                modifier = Modifier.shadow(elevation = 10.dp),
            )

            Text(
                text = length.toDistanceText(),
                fontSize = 24.sp,
                color = colorResource(R.color.item_primary),
                modifier =
                    Modifier
                        .align(Alignment.Start)
                        .padding(10.dp)
                        .shadow(elevation = 8.dp, shape = CircleShape)
                        .clip(shape = CircleShape)
                        .background(color = colorResource(R.color.background_primary))
                        .padding(horizontal = 20.dp, vertical = 10.dp),
            )
        }

        Icon(
            painter = painterResource(R.drawable.icon_new_waypoint_position),
            contentDescription = null,
            tint = colorResource(R.color.point_primary),
            modifier =
                Modifier
                    .align(Alignment.Center)
                    .size(40.dp),
        )

        Column(
            Modifier
                .align(Alignment.BottomEnd)
                .padding(10.dp),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier =
                    Modifier
                        .shadow(elevation = 8.dp, shape = CircleShape)
                        .clip(shape = CircleShape)
                        .background(colorResource(R.color.background_primary))
                        .size(70.dp)
                        .clickable { onUndoWaypoint() },
            ) {
                Icon(
                    painter = painterResource(R.drawable.icon_undo),
                    contentDescription = stringResource(R.string.create_custom_course_remove_waypoint_button_description),
                    tint = colorResource(R.color.item_primary),
                    modifier = Modifier.size(40.dp),
                )
            }

            Spacer(modifier = Modifier.size(10.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier =
                    Modifier
                        .shadow(elevation = 8.dp, shape = CircleShape)
                        .clip(shape = CircleShape)
                        .background(colorResource(R.color.background_primary))
                        .size(70.dp)
                        .clickable { onAddWaypoint() },
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.create_custom_course_add_waypoint_button_description),
                    tint = colorResource(R.color.item_primary),
                    modifier = Modifier.size(40.dp),
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
fun CreateCustomCourseScreenPreview_ShortCourse() {
    CreateCustomCourseScreen(
        length = Length(123),
        onClose = { },
        onConfirm = { },
        onUndoWaypoint = { },
        onAddWaypoint = { },
    )
}

@PreviewLightDark
@Composable
fun CreateCustomCourseScreenPreview_LongCourse() {
    CreateCustomCourseScreen(
        length = Length(12345),
        onClose = { },
        onConfirm = { },
        onUndoWaypoint = { },
        onAddWaypoint = { },
    )
}
