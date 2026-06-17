enum CourseValidationError: Error, Equatable {
    case emptyName
    case invalidDistance
    case invalidLatitude
    case invalidLongitude
}
