package io.coursepick.coursepick.view.fixtures

import io.coursepick.coursepick.domain.Course
import io.coursepick.coursepick.domain.CourseRepository

class FakeRepository(
    override val courses: List<Course>,
) : CourseRepository
