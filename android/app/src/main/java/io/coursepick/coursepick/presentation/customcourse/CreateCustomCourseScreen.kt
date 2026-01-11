package io.coursepick.coursepick.presentation.customcourse

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.coursepick.coursepick.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCustomCourseScreen(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .navigationBarsPadding(),
    ) {
        CenterAlignedTopAppBar(
            title = { Text(text = "코스 추가") },
            navigationIcon = {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    modifier = Modifier.padding(start = 10.dp),
                )
            },
            actions = {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 10.dp),
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
        )

        Icon(
            painter = painterResource(R.drawable.icon_add_waypoint),
            contentDescription = null,
            tint = colorResource(R.color.point_primary),
            modifier = Modifier.align(Alignment.Center).size(40.dp),
        )

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier =
                Modifier
                    .align(alignment = Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp, bottom = 10.dp),
        ) {
            Text(
                text = "123km",
                fontSize = 24.sp,
                color = colorResource(R.color.item_primary),
                modifier =
                    Modifier
                        .clip(shape = CircleShape)
                        .background(color = colorResource(R.color.background_primary))
                        .padding(20.dp)
                        .align(Alignment.Bottom),
            )

            Column {
                IconButton(
                    onClick = {},
                    modifier =
                        Modifier
                            .clip(shape = CircleShape)
                            .background(colorResource(R.color.background_primary))
                            .size(70.dp),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.icon_undo),
                        contentDescription = null,
                        tint = colorResource(R.color.item_primary),
                        modifier = Modifier.size(40.dp),
                    )
                }

                Spacer(modifier = Modifier.size(10.dp))

                IconButton(
                    onClick = {},
                    modifier =
                        Modifier
                            .clip(shape = CircleShape)
                            .background(colorResource(R.color.background_primary))
                            .size(70.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = colorResource(R.color.item_primary),
                        modifier = Modifier.size(40.dp),
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
fun CreateCustomCourseScreenPreview() {
    CreateCustomCourseScreen()
}
