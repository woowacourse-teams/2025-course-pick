package io.coursepick.coursepick.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class GetCoursesResponse : ArrayList<GetCoursesResponse.Course>() {
    @Serializable
    data class Course(
        @SerialName("type")
        val type: String?,
        @SerialName("geometry")
        val geometry: Geometry?,
        @SerialName("properties")
        val properties: Properties?,
    ) {
        @Serializable
        data class Geometry(
            @SerialName("type")
            val type: String?,
            @SerialName("coordinates")
            val coordinates: List<List<Double?>?>?,
        )

        @Serializable
        data class Properties(
            @SerialName("name")
            val name: String?,
            @SerialName("distance")
            val distance: Int?,
            @SerialName("length")
            val length: Int?,
        )
    }
}
