package io.coursepick.coursepick.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.coursepick.coursepick.data.course.CourseService
import io.coursepick.coursepick.data.notice.NoticeService
import io.coursepick.coursepick.data.search.SearchService
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    @Provides
    fun provideCourseService(
        @CoursePickRetrofit retrofit: Retrofit,
    ): CourseService = retrofit.create(CourseService::class.java)

    @Provides
    fun provideSearchService(
        @KakaoRetrofit retrofit: Retrofit,
    ): SearchService = retrofit.create(SearchService::class.java)

    @Provides
    fun provideNoticeService(
        @CoursePickRetrofit retrofit: Retrofit,
    ): NoticeService = retrofit.create(NoticeService::class.java)
}
