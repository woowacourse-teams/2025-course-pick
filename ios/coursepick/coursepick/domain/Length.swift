struct Length: Equatable, Hashable {
    let meters: Double

    init(meters: Double) throws {
        guard meters >= 0 else {
            throw CourseValidationError.invalidDistance
        }

        self.meters = meters
    }
}
