package io.coursepick.coursepick.presentation.coursedetail

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.coursepick.coursepick.R
import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.course.Course
import io.coursepick.coursepick.domain.course.CourseName
import io.coursepick.coursepick.domain.course.Distance
import io.coursepick.coursepick.domain.course.Latitude
import io.coursepick.coursepick.domain.course.Length
import io.coursepick.coursepick.domain.course.Longitude
import io.coursepick.coursepick.presentation.auth.AuthDialog
import io.coursepick.coursepick.presentation.auth.AuthFeature
import io.coursepick.coursepick.presentation.auth.AuthUiEvent
import io.coursepick.coursepick.presentation.auth.AuthViewModel
import io.coursepick.coursepick.presentation.auth.KakaoAuthenticator
import io.coursepick.coursepick.presentation.toDistanceText

@Composable
fun CourseDetailScreen(
    onNavigateBack: () -> Unit,
    courseDetailViewModel: CourseDetailViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel(),
) {
    val context: Context = LocalContext.current
    val course: Course = courseDetailViewModel.course.collectAsStateWithLifecycle().value

    LaunchedEffect(Unit) {
        authViewModel.uiEvent.collect { event: AuthUiEvent -> event.handle(context, courseDetailViewModel) }
    }

    LaunchedEffect(Unit) {
        courseDetailViewModel.event.collect { event: CourseDetailEvent -> event.handle(context) }
    }

    CourseDetailScreen(
        course = course,
        averageRating = courseDetailViewModel.averageRating.collectAsStateWithLifecycle().value,
        isFavorite = courseDetailViewModel.isFavorite.collectAsStateWithLifecycle().value,
        reviewCount = courseDetailViewModel.reviewCount.collectAsStateWithLifecycle().value,
        reviews = courseDetailViewModel.reviews.collectAsStateWithLifecycle().value,
        onNavigateBack = onNavigateBack,
        onReportCourse = courseDetailViewModel::onReportCourse,
        onDeleteReview = courseDetailViewModel::deleteReview,
        onReportReview = courseDetailViewModel::reportReview,
        onWriteReview = courseDetailViewModel::onWriteReview,
    )

    val authDialogState: AuthFeature? = courseDetailViewModel.authDialogState.collectAsStateWithLifecycle().value

    if (authDialogState != null) {
        AuthDialog(
            feature = authDialogState,
            onDismissRequest = courseDetailViewModel::dismissAuthDialog,
            onKakaoLoginClick = { authViewModel.authenticate(KakaoAuthenticator(context), authDialogState) },
        )
    }

    if (courseDetailViewModel.showReportCourseDialog.collectAsStateWithLifecycle().value) {
        ReportCourseDialog(
            course = course,
            onConfirm = courseDetailViewModel::submitCourseReport,
            onDismiss = courseDetailViewModel::dismissReportCourseDialog,
        )
    }
}

private fun AuthUiEvent.handle(
    context: Context,
    viewModel: CourseDetailViewModel,
) {
    when (this) {
        is AuthUiEvent.AuthenticateSuccess -> {
            viewModel.onAuthSuccess(feature)
        }

        AuthUiEvent.AuthenticateFailure -> {
            Toast.makeText(context, context.getString(R.string.authentication_failure_message), Toast.LENGTH_SHORT).show()
        }
    }
}

private fun CourseDetailEvent.handle(context: Context) {
    when (this) {
        CourseDetailEvent.NoNetwork -> {
            Toast.makeText(context, context.getString(R.string.courses_no_network_message), Toast.LENGTH_SHORT).show()
        }

        CourseDetailEvent.ReportCourseSuccess -> {
            Toast.makeText(context, context.getString(R.string.report_course_success_message), Toast.LENGTH_SHORT).show()
        }

        CourseDetailEvent.CourseAlreadyReported -> {
            Toast.makeText(context, context.getString(R.string.report_course_failure_already_reported), Toast.LENGTH_SHORT).show()
        }

        CourseDetailEvent.ReportCourseUnauthorizedUser -> {
            Toast.makeText(context, context.getString(R.string.report_course_failure_unauthorized_user_message), Toast.LENGTH_SHORT).show()
        }

        CourseDetailEvent.ReportCourseUnknownFailure -> {
            Toast.makeText(context, context.getString(R.string.report_course_failure_unknown_message), Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
private fun CourseDetailScreen(
    course: Course,
    averageRating: Float,
    isFavorite: Boolean,
    reviewCount: Int,
    reviews: List<Review>,
    onNavigateBack: () -> Unit,
    onReportCourse: (Course) -> Unit,
    onDeleteReview: (Review) -> Unit,
    onReportReview: (Review) -> Unit,
    onWriteReview: () -> Unit,
) {
    var isFabVisible by remember { mutableStateOf(true) }

    val nestedScrollConnection =
        object : NestedScrollConnection {
            override fun onPreScroll(
                available: Offset,
                source: NestedScrollSource,
            ): Offset {
                if (available.y < -1) {
                    isFabVisible = false
                } else if (available.y > 1) {
                    isFabVisible = true
                }

                return super.onPreScroll(available, source)
            }
        }

    Scaffold(
        topBar = {
            TopAppBar(
                onNavigateBack = onNavigateBack,
                onReportCourse = { onReportCourse(course) },
            )
        },
        floatingActionButton = {
            WriteReviewButton(
                onClick = onWriteReview,
                isVisible = isFabVisible,
            )
        },
    ) { innerPadding: PaddingValues ->
        Column(
            Modifier
                .background(colorResource(R.color.background_primary))
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 10.dp),
        ) {
            CourseInfo(
                courseName = course.name,
                length = course.length,
                isFavorite = isFavorite,
                averageRating = averageRating,
            )

            Spacer(Modifier.height(10.dp))

            CourseReviewHeader(reviewCount)

            Spacer(Modifier.height(10.dp))

            CourseReviews(
                reviews = reviews,
                onDelete = onDeleteReview,
                onReport = onReportReview,
                modifier =
                    Modifier
                        .weight(1F)
                        .nestedScroll(nestedScrollConnection),
            )
        }
    }
}

@Composable
fun TopAppBar(
    onNavigateBack: () -> Unit,
    onReportCourse: () -> Unit,
    modifier: Modifier = Modifier,
) {
    @OptIn(ExperimentalMaterial3Api::class)
    CenterAlignedTopAppBar(
        title = { Text(text = stringResource(R.string.course_detail_header)) },
        navigationIcon = {
            Icon(
                painter = painterResource(R.drawable.icon_arrow_back),
                contentDescription = stringResource(R.string.course_detail_navigate_back_description),
                tint = colorResource(R.color.item_primary),
                modifier =
                    Modifier
                        .padding(end = 10.dp)
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable { onNavigateBack() }
                        .padding(14.dp),
            )
        },
        actions = {
            Icon(
                painter = painterResource(R.drawable.icon_report_course),
                contentDescription = stringResource(R.string.course_detail_report_course_button_description),
                tint = colorResource(R.color.item_primary),
                modifier =
                    Modifier
                        .padding(end = 10.dp)
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable { onReportCourse() }
                        .padding(8.dp),
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
        modifier = modifier,
    )
}

@Composable
private fun WriteReviewButton(
    onClick: () -> Unit,
    isVisible: Boolean,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { fullHeight: Int -> fullHeight }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { fullHeight: Int -> fullHeight }) + fadeOut(),
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier
                    .clip(RoundedCornerShape(50))
                    .clickable { onClick() }
                    .background(colorResource(R.color.point_primary))
                    .padding(horizontal = 18.dp, vertical = 16.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.icon_write_review),
                contentDescription = null,
                tint = colorResource(R.color.item_white),
            )

            Spacer(Modifier.width(10.dp))

            Text(
                text = stringResource(R.string.course_detail_write_review_button),
                color = colorResource(R.color.item_white),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun CourseInfo(
    courseName: CourseName,
    length: Length,
    averageRating: Float,
    isFavorite: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(if (isFavorite) R.drawable.icon_favorite else R.drawable.icon_not_favorite),
                contentDescription = null,
                tint = colorResource(R.color.item_primary),
                modifier =
                    Modifier
                        .background(color = colorResource(R.color.background_tertiary), shape = CircleShape)
                        .padding(4.dp),
            )

            Spacer(Modifier.width(10.dp))

            Text(
                text = courseName.value,
                color = colorResource(R.color.item_primary),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Spacer(Modifier.height(10.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = averageRating.toString(),
                color = colorResource(R.color.point_primary),
                fontSize = 18.sp,
            )

            Spacer(Modifier.width(10.dp))

            RatingStars(rating = averageRating, modifier = Modifier.height(18.dp))
        }

        Spacer(Modifier.height(10.dp))

        CourseLengthInfo(length = length)
    }
}

@Composable
private fun CourseLengthInfo(
    length: Length,
    modifier: Modifier = Modifier,
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        Icon(
            painter = painterResource(R.drawable.icon_length),
            contentDescription = null,
            tint = colorResource(R.color.item_primary),
        )

        Spacer(Modifier.width(10.dp))

        Text(
            text = length.toDistanceText(),
            color = colorResource(R.color.item_primary),
            fontSize = 18.sp,
        )
    }
}

@Composable
private fun CourseReviewHeader(
    reviewCount: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Text(
            text = stringResource(R.string.course_detail_review_header),
            color = colorResource(R.color.item_primary),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )

        Spacer(Modifier.width(5.dp))

        Text(
            text = stringResource(R.string.course_detail_review_count, reviewCount),
            color = colorResource(R.color.item_primary),
            fontSize = 16.sp,
        )

        Spacer(Modifier.width(10.dp))

        HorizontalDivider(
            thickness = 1.dp,
            color = colorResource(R.color.background_border),
        )
    }
}

@Composable
private fun CourseReviews(
    reviews: List<Review>,
    onDelete: (Review) -> Unit,
    onReport: (Review) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier) {
        itemsIndexed(
            items = reviews,
            key = { _, review: Review -> review.id },
        ) { index: Int, review: Review ->
            ReviewItem(
                review = review,
                onDelete = onDelete,
                onReport = onReport,
                modifier = Modifier.padding(vertical = 10.dp),
            )

            if (index != reviews.lastIndex) {
                HorizontalDivider(thickness = 1.dp, color = colorResource(R.color.background_border_light))
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun CourseDetailScreenPreview() {
    CourseDetailScreen(
        course =
            Course(
                id = "",
                name = CourseName("석촌호수 동호 한바퀴"),
                distance = Distance(0),
                length = Length(0),
                coordinates = List(2) { (Coordinate(Latitude(0.0), Longitude(0.0))) },
            ),
        averageRating = 4.32F,
        isFavorite = false,
        reviewCount = 99,
        reviews =
            List(10) { index: Int ->
                Review(
                    id = index.toString(),
                    username = "달리는 런숭이 $index",
                    isMine = index == 0,
                    rating = 4 + index / 10F,
                    comment = "리뷰 내용 ".repeat(10 + index * 5),
                )
            },
        onNavigateBack = { },
        onReportCourse = { },
        onDeleteReview = { },
        onReportReview = { },
        onWriteReview = { },
    )
}
