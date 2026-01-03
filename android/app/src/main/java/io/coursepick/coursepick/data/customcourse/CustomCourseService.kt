package io.coursepick.coursepick.data.customcourse

import io.coursepick.coursepick.data.course.CourseDto
import retrofit2.http.Body

interface CustomCourseService {
    suspend fun draftSegment(
        @Body request: DraftSegmentRequest,
    ): DraftSegmentDto

    suspend fun course(
        @Body request: DraftCourseDto,
    ): CourseDto
}
