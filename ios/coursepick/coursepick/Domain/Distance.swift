struct Distance: Equatable, Hashable {
    let meters: Double

    init(meters: Double) throws {
        guard meters.isFinite, meters >= 0 else {
            throw CourseValidationError.invalidDistance
        }

        self.meters = meters
    }
}
