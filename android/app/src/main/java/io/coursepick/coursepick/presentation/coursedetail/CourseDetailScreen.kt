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
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.coursepick.coursepick.R
import io.coursepick.coursepick.presentation.auth.AuthDialog
import io.coursepick.coursepick.presentation.auth.AuthFeature
import io.coursepick.coursepick.presentation.auth.AuthUiEvent
import io.coursepick.coursepick.presentation.auth.AuthViewModel
import io.coursepick.coursepick.presentation.auth.KakaoAuthenticator

@Composable
fun CourseDetailScreen(
    courseId: String,
    onNavigateBack: () -> Unit,
    onWriteReview: (CourseDetailUiModel) -> Unit,
    courseDetailViewModel: CourseDetailViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel(),
) {
    val context: Context = LocalContext.current

    LaunchedEffect(Unit) {
        courseDetailViewModel.load(courseId)
    }

    LaunchedEffect(Unit) {
        authViewModel.uiEvent.collect { event: AuthUiEvent -> event.handle(context, courseDetailViewModel) }
    }

    LaunchedEffect(Unit) {
        courseDetailViewModel.event.collect { event: CourseDetailViewModel.UiEvent -> event.handle(context) }
    }

    val state: CourseDetailViewModel.UiState = courseDetailViewModel.state.collectAsStateWithLifecycle().value

    CourseDetailScreen(
        state = state,
        onNavigateBack = onNavigateBack,
        onToggleFavorite = courseDetailViewModel::toggleFavorite,
        authDialog = courseDetailViewModel.authDialog.collectAsStateWithLifecycle().value,
        onConfirmAuthDialog = { authFeature: AuthFeature -> authViewModel.authenticate(KakaoAuthenticator(context), authFeature) },
        onDismissAuthDialog = courseDetailViewModel::dismissAuthDialog,
        showReportCourseDialog = courseDetailViewModel.showReportCourseDialog.collectAsStateWithLifecycle().value,
        onReportCourse = courseDetailViewModel::onReportCourse,
        onConfirmReportCourseDialog = courseDetailViewModel::submitCourseReport,
        onDismissReportCourseDialog = courseDetailViewModel::dismissReportCourseDialog,
        onWriteReview = onWriteReview,
    )
}

private fun AuthUiEvent.handle(
    context: Context,
    courseDetailViewModel: CourseDetailViewModel,
) {
    when (this) {
        is AuthUiEvent.AuthenticateSuccess -> {
            courseDetailViewModel.onAuthSuccess(feature)
        }

        AuthUiEvent.AuthenticateFailure -> {
            Toast.makeText(context, context.getString(R.string.authentication_failure_message), Toast.LENGTH_SHORT).show()
        }
    }
}

private fun CourseDetailViewModel.UiEvent.handle(context: Context) {
    when (this) {
        CourseDetailViewModel.UiEvent.NoNetwork -> {
            Toast.makeText(context, context.getString(R.string.failure_no_network_toast_message), Toast.LENGTH_SHORT).show()
        }

        CourseDetailViewModel.UiEvent.ReportCourseSuccess -> {
            Toast.makeText(context, context.getString(R.string.report_course_success_message), Toast.LENGTH_SHORT).show()
        }

        CourseDetailViewModel.UiEvent.CourseAlreadyReported -> {
            Toast.makeText(context, context.getString(R.string.report_course_failure_already_reported), Toast.LENGTH_SHORT).show()
        }

        CourseDetailViewModel.UiEvent.ReportCourseUnauthorizedUser -> {
            Toast.makeText(context, context.getString(R.string.report_course_failure_unauthorized_user_message), Toast.LENGTH_SHORT).show()
        }

        CourseDetailViewModel.UiEvent.ReportCourseUnknownFailure -> {
            Toast.makeText(context, context.getString(R.string.report_course_failure_unknown_message), Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
private fun CourseDetailScreen(
    state: CourseDetailViewModel.UiState,
    onNavigateBack: () -> Unit,
    onToggleFavorite: () -> Unit,
    authDialog: AuthFeature?,
    onConfirmAuthDialog: (AuthFeature) -> Unit,
    onDismissAuthDialog: () -> Unit,
    showReportCourseDialog: Boolean,
    onReportCourse: () -> Unit,
    onConfirmReportCourseDialog: () -> Unit,
    onDismissReportCourseDialog: () -> Unit,
    onWriteReview: (CourseDetailUiModel) -> Unit,
) {
    var isFabVisible: Boolean by remember(state) { mutableStateOf(state is CourseDetailViewModel.UiState.Success) }

    val nestedScrollConnection: NestedScrollConnection =
        remember {
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
        }

    Scaffold(
        topBar = {
            TopAppBar(
                onNavigateBack = onNavigateBack,
                canReportCourse = state is CourseDetailViewModel.UiState.Success,
                onReportCourse = onReportCourse,
            )
        },
        floatingActionButton = {
            WriteReviewButton(
                onClick = { if (state is CourseDetailViewModel.UiState.Success) onWriteReview(state.data) },
                isVisible = isFabVisible,
            )
        },
        containerColor = colorResource(R.color.background_primary),
    ) { innerPadding: PaddingValues ->
        when (state) {
            CourseDetailViewModel.UiState.Loading -> {
                Box(
                    Modifier
                        .padding(innerPadding)
                        .fillMaxWidth()
                        .padding(top = 100.dp),
                ) {
                    CircularProgressIndicator(
                        color = colorResource(R.color.item_primary),
                        modifier =
                            Modifier
                                .align(Alignment.Center)
                                .size(80.dp),
                    )
                }
            }

            is CourseDetailViewModel.UiState.Success -> {
                Column(
                    Modifier
                        .background(colorResource(R.color.background_primary))
                        .padding(innerPadding)
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                ) {
                    CourseInfo(
                        courseName = state.data.name,
                        length = state.data.length,
                        isFavorite = state.data.isFavorite,
                        onToggleFavorite = onToggleFavorite,
                        averageRating = state.data.averageRating,
                    )

                    Spacer(Modifier.height(10.dp))

                    CourseReviewHeader(state.data.reviewCount)

                    Spacer(Modifier.height(10.dp))

                    CourseReviews(
                        reviews = state.data.reviews,
                        onDelete = { },
                        onReport = { },
                        modifier =
                            Modifier
                                .weight(1F)
                                .fillMaxWidth()
                                .nestedScroll(nestedScrollConnection),
                    )
                }

                if (authDialog != null) {
                    AuthDialog(
                        feature = authDialog,
                        onDismissRequest = onDismissAuthDialog,
                        onKakaoLoginClick = { onConfirmAuthDialog(authDialog) },
                    )
                }

                if (showReportCourseDialog) {
                    ReportCourseDialog(
                        courseName = state.data.name,
                        onConfirm = onConfirmReportCourseDialog,
                        onDismiss = onDismissReportCourseDialog,
                    )
                }
            }

            CourseDetailViewModel.UiState.Failure.NoNetwork -> {
            }

            CourseDetailViewModel.UiState.Failure.Unknown -> {
            }
        }
    }
}

@Composable
private fun TopAppBar(
    onNavigateBack: () -> Unit,
    canReportCourse: Boolean,
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
                tint = colorResource(if (canReportCourse) R.color.item_primary else R.color.item_tertiary),
                modifier =
                    Modifier
                        .padding(end = 10.dp)
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable(canReportCourse) { onReportCourse() }
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
        modifier = modifier.shadow(elevation = 10.dp),
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
    courseName: String,
    length: Double,
    averageRating: Float,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
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
                        .clip(CircleShape)
                        .background(colorResource(R.color.background_tertiary))
                        .clickable { onToggleFavorite() }
                        .padding(4.dp),
            )

            Spacer(Modifier.width(10.dp))

            Text(
                text = courseName,
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

            StarRating(rating = averageRating, starSize = 18.dp)
        }

        Spacer(Modifier.height(10.dp))

        CourseLengthInfo(length = length)
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
    reviews: List<CourseReviewUiModel>,
    onDelete: (CourseReviewUiModel) -> Unit,
    onReport: (CourseReviewUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (reviews.isEmpty()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier,
        ) {
            Spacer(Modifier.height(50.dp))

            Text(
                text = stringResource(R.string.course_detail_empty_review_message),
                color = colorResource(R.color.item_tertiary),
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
            )
        }
    } else {
        LazyColumn(modifier) {
            itemsIndexed(
                items = reviews,
                key = { _, review: CourseReviewUiModel -> review.id },
            ) { index: Int, review: CourseReviewUiModel ->
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
}

@PreviewLightDark
@Composable
private fun CourseDetailScreenPreview_Loading() {
    CourseDetailScreen(
        state = CourseDetailViewModel.UiState.Loading,
        onNavigateBack = { },
        onToggleFavorite = { },
        authDialog = null,
        onConfirmAuthDialog = { },
        onDismissAuthDialog = { },
        showReportCourseDialog = false,
        onReportCourse = { },
        onConfirmReportCourseDialog = { },
        onDismissReportCourseDialog = { },
        onWriteReview = { },
    )
}

@PreviewLightDark
@Composable
private fun CourseDetailScreenPreview_Success_EmptyReview() {
    CourseDetailScreen(
        state =
            CourseDetailViewModel.UiState.Success(
                data =
                    CourseDetailUiModel(
                        id = "",
                        name = "석촌호수 동호 한바퀴",
                        length = 0.0,
                        isFavorite = false,
                        reviewCount = 0,
                        averageRating = 4.32F,
                        tags = emptyList(),
                        reviews = emptyList(),
                    ),
            ),
        onNavigateBack = { },
        onToggleFavorite = { },
        authDialog = null,
        onConfirmAuthDialog = { },
        onDismissAuthDialog = { },
        showReportCourseDialog = false,
        onReportCourse = { },
        onConfirmReportCourseDialog = { },
        onDismissReportCourseDialog = { },
        onWriteReview = { },
    )
}

@PreviewLightDark
@Composable
private fun CourseDetailScreenPreview_Success_NonEmptyReviews() {
    CourseDetailScreen(
        state =
            CourseDetailViewModel.UiState.Success(
                data =
                    CourseDetailUiModel(
                        id = "",
                        name = "석촌호수 동호 한바퀴",
                        length = 0.0,
                        isFavorite = false,
                        reviewCount = 99,
                        averageRating = 4.32F,
                        tags = emptyList(),
                        reviews =
                            List(10) { index: Int ->
                                CourseReviewUiModel(
                                    id = "$index",
                                    authorId = "",
                                    authorName = "달리는 런숭이 $index",
                                    isMine = index == 0,
                                    rating = 4 + index / 10F,
                                    content = "리뷰 내용 ".repeat(10 + index * 5),
                                )
                            },
                    ),
            ),
        onNavigateBack = { },
        onToggleFavorite = { },
        authDialog = null,
        onConfirmAuthDialog = { },
        onDismissAuthDialog = { },
        showReportCourseDialog = false,
        onReportCourse = { },
        onConfirmReportCourseDialog = { },
        onDismissReportCourseDialog = { },
        onWriteReview = { },
    )
}

@PreviewLightDark
@Composable
private fun CourseDetailScreenPreview_Failure_NoNetwork() {
    CourseDetailScreen(
        state = CourseDetailViewModel.UiState.Failure.NoNetwork,
        onNavigateBack = { },
        onToggleFavorite = { },
        authDialog = null,
        onConfirmAuthDialog = { },
        onDismissAuthDialog = { },
        showReportCourseDialog = false,
        onReportCourse = { },
        onConfirmReportCourseDialog = { },
        onDismissReportCourseDialog = { },
        onWriteReview = { },
    )
}

@PreviewLightDark
@Composable
private fun CourseDetailScreenPreview_Failure_Unknown() {
    CourseDetailScreen(
        state = CourseDetailViewModel.UiState.Failure.Unknown,
        onNavigateBack = { },
        onToggleFavorite = { },
        authDialog = null,
        onConfirmAuthDialog = { },
        onDismissAuthDialog = { },
        showReportCourseDialog = false,
        onReportCourse = { },
        onConfirmReportCourseDialog = { },
        onDismissReportCourseDialog = { },
        onWriteReview = { },
    )
}
