struct Latitude: Equatable, Hashable {
    let value: Double

    init(_ value: Double) throws {
        guard (-90...90).contains(value) else {
            throw CourseValidationError.invalidLatitude
        }

        self.value = value
    }
}
