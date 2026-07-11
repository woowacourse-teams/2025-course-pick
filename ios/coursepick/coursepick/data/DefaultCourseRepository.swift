final class DefaultCourseRepository: CourseRepository {
    func fetchCourses() -> [Course] {
        Self.courses
    }

    private static let courses: [Course] = [
        course(
            name: "석촌호수",
            distance: 200.123,
            length: 2146.123,
            coordinates: [
                (37.514167, 127.103611),
                (37.514575, 127.101982),
                (37.515438, 127.100941),
                (37.516312, 127.101887),
                (37.516084, 127.104021),
                (37.515167, 127.104611),
                (37.514167, 127.103611)
            ]
        ),
        course(
            name: "올림픽공원",
            distance: 1240.4,
            length: 3598.7,
            coordinates: [
                (37.520062, 127.121463),
                (37.521552, 127.122468),
                (37.523055, 127.124163),
                (37.522447, 127.126962),
                (37.519862, 127.127617),
                (37.518273, 127.124524),
                (37.520062, 127.121463)
            ]
        ),
        course(
            name: "한강 잠실",
            distance: 2870.8,
            length: 5125.5,
            coordinates: [
                (37.517487, 127.084178),
                (37.518842, 127.088745),
                (37.520104, 127.094071),
                (37.521327, 127.099213),
                (37.520412, 127.104424),
                (37.518529, 127.108653)
            ]
        )
    ]

    private static func course(
        name: String,
        distance: Double,
        length: Double,
        coordinates: [(latitude: Double, longitude: Double)]
    ) -> Course {
        Course(
            name: try! CourseName(name),
            distance: try! Distance(meters: distance),
            length: try! Distance(meters: length),
            coordinates: coordinates.map { coordinate in
                Coordinate(
                    latitude: try! Latitude(coordinate.latitude),
                    longitude: try! Longitude(coordinate.longitude)
                )
            }
        )
    }
}
