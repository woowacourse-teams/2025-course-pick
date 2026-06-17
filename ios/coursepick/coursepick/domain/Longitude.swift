struct Longitude: Equatable, Hashable {
    let value: Double

    init(_ value: Double) throws {
        guard (-180...180).contains(value) else {
            throw CourseValidationError.invalidLongitude
        }

        self.value = value
    }
}
