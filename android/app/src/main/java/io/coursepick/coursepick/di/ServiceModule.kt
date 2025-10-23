package io.coursepick.coursepick.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.coursepick.coursepick.data.course.CourseService
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    @Provides
    fun provideCourseService(retrofit: Retrofit): CourseService = retrofit.create(CourseService::class.java)
}
