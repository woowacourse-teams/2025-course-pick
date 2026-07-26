package io.coursepick.coursepick.presentation.coursedetail

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.painter.Painter
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
import io.coursepick.coursepick.data.auth.KakaoAuthenticator
import io.coursepick.coursepick.domain.auth.Authenticator
import io.coursepick.coursepick.presentation.auth.AuthDialog
import io.coursepick.coursepick.presentation.customcourse.DeleteCustomCourseDialog

@Composable
fun CourseDetailScreen(
    courseId: String,
    navigateBack: () -> Unit,
    navigateToWriteCourseReview: (CourseDetailUiModel) -> Unit,
    onCourseDeleted: () -> Unit,
    viewModel: CourseDetailViewModel = viewModel(),
) {
    val context: Context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.load(courseId)
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event: CourseDetailViewModel.UiEvent ->
            event.handle(context, navigateToWriteCourseReview, onCourseDeleted)
        }
    }

    val state: CourseDetailViewModel.UiState = viewModel.uiState.collectAsStateWithLifecycle().value

    CourseDetailScreen(
        uiState = state,
        onNavigateBack = navigateBack,
        onToggleFavorite = viewModel::toggleFavorite,
        onReportCourse = viewModel::onReportCourse,
        onDeleteCourse = viewModel::onDeleteCourse,
        onDeleteReview = viewModel::onDeleteReview,
        onReportReview = viewModel::onReportReview,
        onWriteReview = viewModel::onWriteReview,
        onRetry = { viewModel.load(courseId) },
    )

    CourseDetailScreenDialogs(
        dialogState = viewModel.dialogState.collectAsStateWithLifecycle().value,
        onDismissAuthDialog = viewModel::dismissAuthDialog,
        onConfirmAuthDialog = viewModel::signIn,
        onDismissReportCourseDialog = viewModel::dismissReportCourseDialog,
        onConfirmReportCourseDialog = viewModel::submitCourseReport,
        onDismissDeleteCourseDialog = viewModel::dismissDeleteCourseDialog,
        onConfirmDeleteCourseDialog = viewModel::confirmDeleteCourse,
        onDismissDeleteReviewDialog = viewModel::dismissDeleteReviewDialog,
        onConfirmDeleteReviewDialog = viewModel::confirmDeleteReview,
        onDismissReportReviewDialog = viewModel::dismissReportReviewDialog,
        onConfirmReportReviewDialog = viewModel::confirmReportReview,
    )
}

private fun CourseDetailViewModel.UiEvent.handle(
    context: Context,
    navigateToWriteCourseReview: (CourseDetailUiModel) -> Unit,
    onCourseDeleted: () -> Unit,
) {
    when (this) {
        CourseDetailViewModel.UiEvent.AuthenticationSuccess -> {
            Toast.makeText(context, context.getString(R.string.authentication_success_message), Toast.LENGTH_SHORT).show()
        }

        CourseDetailViewModel.UiEvent.AuthenticationCancelled -> {
            Toast.makeText(context, context.getString(R.string.authentication_cancelled_message), Toast.LENGTH_SHORT).show()
        }

        CourseDetailViewModel.UiEvent.AuthenticationFailure -> {
            Toast.makeText(context, context.getString(R.string.authentication_failure_message), Toast.LENGTH_SHORT).show()
        }

        CourseDetailViewModel.UiEvent.NoNetwork -> {
            Toast.makeText(context, context.getString(R.string.failure_no_network_toast_message), Toast.LENGTH_SHORT).show()
        }

        CourseDetailViewModel.UiEvent.UnauthorizedUser -> {
            Toast.makeText(context, context.getString(R.string.failure_unauthorized_user_toast_message), Toast.LENGTH_SHORT).show()
        }

        CourseDetailViewModel.UiEvent.UnknownFailure -> {
            Toast.makeText(context, context.getString(R.string.failure_unknown_toast_message), Toast.LENGTH_SHORT).show()
        }

        CourseDetailViewModel.UiEvent.ReportCourseSuccess -> {
            Toast.makeText(context, context.getString(R.string.report_course_success_message), Toast.LENGTH_SHORT).show()
        }

        CourseDetailViewModel.UiEvent.CourseAlreadyReported -> {
            Toast.makeText(context, context.getString(R.string.report_course_failure_already_reported_message), Toast.LENGTH_SHORT).show()
        }

        CourseDetailViewModel.UiEvent.DeleteCourseSuccess -> {
            Toast.makeText(context, context.getString(R.string.delete_custom_course_dialog_success_message), Toast.LENGTH_SHORT).show()
        }

        CourseDetailViewModel.UiEvent.DeleteReviewSuccess -> {
            Toast.makeText(context, context.getString(R.string.delete_review_success_message), Toast.LENGTH_SHORT).show()
        }

        CourseDetailViewModel.UiEvent.ReportReviewSuccess -> {
            Toast.makeText(context, context.getString(R.string.report_review_success_message), Toast.LENGTH_SHORT).show()
        }

        CourseDetailViewModel.UiEvent.ReviewAlreadyReported -> {
            Toast.makeText(context, context.getString(R.string.report_review_failure_already_reported_message), Toast.LENGTH_SHORT).show()
        }

        is CourseDetailViewModel.UiEvent.NavigateToWriteCourseReview -> {
            navigateToWriteCourseReview(courseDetail)
        }

        CourseDetailViewModel.UiEvent.CourseAlreadyReviewed -> {
            Toast.makeText(context, context.getString(R.string.write_course_review_already_reviewed_message), Toast.LENGTH_SHORT).show()
        }

        CourseDetailViewModel.UiEvent.Exit -> {
            onCourseDeleted()
        }
    }
}

@Composable
private fun CourseDetailScreen(
    uiState: CourseDetailViewModel.UiState,
    onNavigateBack: () -> Unit,
    onToggleFavorite: () -> Unit,
    onReportCourse: () -> Unit,
    onDeleteCourse: () -> Unit,
    onDeleteReview: (CourseReviewUiModel) -> Unit,
    onReportReview: (CourseReviewUiModel) -> Unit,
    onWriteReview: () -> Unit,
    onRetry: () -> Unit,
) {
    var isFabVisible: Boolean by remember { mutableStateOf(false) }

    val nestedScrollConnection =
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

    LaunchedEffect(uiState) {
        isFabVisible = uiState is CourseDetailViewModel.UiState.Success
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigateBack = onNavigateBack,
                isMyCourse = uiState is CourseDetailViewModel.UiState.Success && uiState.data.isMine,
                isActionEnabled = uiState is CourseDetailViewModel.UiState.Success,
                onReportCourse = onReportCourse,
                onDeleteCourse = onDeleteCourse,
            )
        },
        floatingActionButton = {
            WriteReviewButton(
                onClick = onWriteReview,
                isVisible = isFabVisible,
            )
        },
        containerColor = colorResource(R.color.background_primary),
    ) { innerPadding: PaddingValues ->
        when (uiState) {
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
                        courseName = uiState.data.name,
                        length = uiState.data.length,
                        isFavorite = uiState.data.isFavorite,
                        onToggleFavorite = onToggleFavorite,
                        averageRating = uiState.data.averageRating,
                    )

                    Spacer(Modifier.height(10.dp))

                    CourseReviewHeader(uiState.data.reviewCount)

                    Spacer(Modifier.height(10.dp))

                    CourseReviews(
                        reviews = uiState.data.reviews,
                        onDelete = onDeleteReview,
                        onReport = onReportReview,
                        modifier =
                            Modifier
                                .weight(1F)
                                .fillMaxWidth()
                                .nestedScroll(nestedScrollConnection),
                    )
                }
            }

            CourseDetailViewModel.UiState.Failure.NoNetwork -> {
                FailureComponent(
                    icon = painterResource(R.drawable.icon_no_network),
                    message = stringResource(R.string.failure_no_network_message),
                    onRetry = onRetry,
                    modifier =
                        Modifier
                            .padding(innerPadding)
                            .fillMaxWidth()
                            .padding(top = 100.dp),
                )
            }

            CourseDetailViewModel.UiState.Failure.Unknown -> {
                FailureComponent(
                    icon = painterResource(R.drawable.icon_unknown_failure),
                    message = stringResource(R.string.failure_unknown_message),
                    onRetry = onRetry,
                    modifier =
                        Modifier
                            .padding(innerPadding)
                            .fillMaxWidth()
                            .padding(top = 100.dp),
                )
            }
        }
    }
}

@Composable
private fun TopAppBar(
    navigateBack: () -> Unit,
    isMyCourse: Boolean,
    isActionEnabled: Boolean,
    onReportCourse: () -> Unit,
    onDeleteCourse: () -> Unit,
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
                        .clickable { navigateBack() }
                        .padding(14.dp),
            )
        },
        actions = {
            Icon(
                painter = painterResource(if (isMyCourse) R.drawable.icon_delete_course else R.drawable.icon_report_course),
                contentDescription =
                    stringResource(
                        if (isMyCourse) {
                            R.string.course_detail_delete_course_button_description
                        } else {
                            R.string.course_detail_report_course_button_description
                        },
                    ),
                tint = colorResource(if (isActionEnabled) R.color.item_primary else R.color.item_tertiary),
                modifier =
                    Modifier
                        .padding(end = 10.dp)
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable(isActionEnabled) { if (isMyCourse) onDeleteCourse() else onReportCourse() }
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

@Composable
private fun FailureComponent(
    icon: Painter,
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            tint = colorResource(R.color.item_primary),
            modifier = Modifier.size(48.dp),
        )

        Spacer(Modifier.height(20.dp))

        Text(
            text = message,
            color = colorResource(R.color.item_primary),
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.failure_retry_button),
            color = colorResource(R.color.item_primary),
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier =
                Modifier
                    .border(width = 1.dp, color = colorResource(R.color.background_border), shape = RoundedCornerShape(50))
                    .clip(RoundedCornerShape(50))
                    .clickable { onRetry() }
                    .padding(horizontal = 20.dp, vertical = 10.dp),
        )
    }
}

@Composable
private fun CourseDetailScreenDialogs(
    dialogState: CourseDetailViewModel.DialogState,
    onDismissAuthDialog: () -> Unit,
    onConfirmAuthDialog: (Authenticator, CourseDetailViewModel.AuthFeature) -> Unit,
    onDismissReportCourseDialog: () -> Unit,
    onConfirmReportCourseDialog: () -> Unit,
    onDismissDeleteCourseDialog: () -> Unit,
    onConfirmDeleteCourseDialog: () -> Unit,
    onDismissDeleteReviewDialog: () -> Unit,
    onConfirmDeleteReviewDialog: (CourseReviewUiModel) -> Unit,
    onDismissReportReviewDialog: () -> Unit,
    onConfirmReportReviewDialog: (CourseReviewUiModel) -> Unit,
) {
    val context: Context = LocalContext.current

    if (dialogState.authDialog != null) {
        val featureName: String =
            when (dialogState.authDialog) {
                is CourseDetailViewModel.AuthFeature.ReportCourse -> stringResource(R.string.report_course_feature_name)
                is CourseDetailViewModel.AuthFeature.DeleteCourse -> stringResource(R.string.course_detail_delete_course_feature_name)
                is CourseDetailViewModel.AuthFeature.DeleteReview -> stringResource(R.string.delete_review_feature_name)
                is CourseDetailViewModel.AuthFeature.ReportReview -> stringResource(R.string.report_review_feature_name)
                is CourseDetailViewModel.AuthFeature.WriteReview -> stringResource(R.string.write_course_review_feature_name)
            }

        AuthDialog(
            featureName = featureName,
            onDismissRequest = onDismissAuthDialog,
            onKakaoLoginClick = { onConfirmAuthDialog(KakaoAuthenticator(context), dialogState.authDialog) },
        )
    }

    if (dialogState.reportCourseDialog != null) {
        ReportCourseDialog(
            courseName = dialogState.reportCourseDialog,
            onDismiss = onDismissReportCourseDialog,
            onConfirm = onConfirmReportCourseDialog,
        )
    }

    if (dialogState.deleteCourseDialog != null) {
        DeleteCustomCourseDialog(
            courseName = dialogState.deleteCourseDialog,
            onDismiss = onDismissDeleteCourseDialog,
            onConfirm = onConfirmDeleteCourseDialog,
        )
    }

    if (dialogState.deleteReviewDialog != null) {
        DeleteReviewDialog(
            review = dialogState.deleteReviewDialog,
            onDismiss = onDismissDeleteReviewDialog,
            onConfirm = onConfirmDeleteReviewDialog,
        )
    }

    if (dialogState.reportReviewDialog != null) {
        ReportReviewDialog(
            review = dialogState.reportReviewDialog,
            onDismiss = onDismissReportReviewDialog,
            onConfirm = onConfirmReportReviewDialog,
        )
    }
}

@PreviewLightDark
@Composable
private fun CourseDetailScreenPreview_Loading() {
    CourseDetailScreen(
        uiState = CourseDetailViewModel.UiState.Loading,
        onNavigateBack = { },
        onToggleFavorite = { },
        onReportCourse = { },
        onDeleteCourse = { },
        onDeleteReview = { },
        onReportReview = { },
        onWriteReview = { },
        onRetry = { },
    )
}

@PreviewLightDark
@Composable
private fun CourseDetailScreenPreview_Success_EmptyReview_MyCourse() {
    CourseDetailScreen(
        uiState =
            CourseDetailViewModel.UiState.Success(
                data =
                    CourseDetailUiModel(
                        id = "",
                        name = "석촌호수 동호 한바퀴",
                        length = 0.0,
                        reviewCount = 0,
                        averageRating = 4.32F,
                        tags = emptyList(),
                        reviews = emptyList(),
                        isFavorite = false,
                        isMine = true,
                    ),
            ),
        onNavigateBack = { },
        onToggleFavorite = { },
        onReportCourse = { },
        onDeleteCourse = { },
        onDeleteReview = { },
        onReportReview = { },
        onWriteReview = { },
        onRetry = { },
    )
}

@PreviewLightDark
@Composable
private fun CourseDetailScreenPreview_Success_EmptyReview_NotMyCourse() {
    CourseDetailScreen(
        uiState =
            CourseDetailViewModel.UiState.Success(
                data =
                    CourseDetailUiModel(
                        id = "",
                        name = "석촌호수 동호 한바퀴",
                        length = 0.0,
                        reviewCount = 0,
                        averageRating = 4.32F,
                        tags = emptyList(),
                        reviews = emptyList(),
                        isFavorite = false,
                        isMine = false,
                    ),
            ),
        onNavigateBack = { },
        onToggleFavorite = { },
        onReportCourse = { },
        onDeleteCourse = { },
        onDeleteReview = { },
        onReportReview = { },
        onWriteReview = { },
        onRetry = { },
    )
}

@PreviewLightDark
@Composable
private fun CourseDetailScreenPreview_Success_NonEmptyReviews() {
    CourseDetailScreen(
        uiState =
            CourseDetailViewModel.UiState.Success(
                data =
                    CourseDetailUiModel(
                        id = "",
                        name = "석촌호수 동호 한바퀴",
                        length = 0.0,
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
                        isFavorite = false,
                        isMine = false,
                    ),
            ),
        onNavigateBack = { },
        onToggleFavorite = { },
        onReportCourse = { },
        onDeleteCourse = { },
        onDeleteReview = { },
        onReportReview = { },
        onWriteReview = { },
        onRetry = { },
    )
}

@PreviewLightDark
@Composable
private fun CourseDetailScreenPreview_Failure_NoNetwork() {
    CourseDetailScreen(
        uiState = CourseDetailViewModel.UiState.Failure.NoNetwork,
        onNavigateBack = { },
        onToggleFavorite = { },
        onReportCourse = { },
        onDeleteCourse = { },
        onDeleteReview = { },
        onReportReview = { },
        onWriteReview = { },
        onRetry = { },
    )
}

@PreviewLightDark
@Composable
private fun CourseDetailScreenPreview_Failure_Unknown() {
    CourseDetailScreen(
        uiState = CourseDetailViewModel.UiState.Failure.Unknown,
        onNavigateBack = { },
        onToggleFavorite = { },
        onReportCourse = { },
        onDeleteCourse = { },
        onDeleteReview = { },
        onReportReview = { },
        onWriteReview = { },
        onRetry = { },
    )
}
