struct CourseName: Equatable, Hashable {
    let value: String

    init(_ value: String) throws {
        guard !value.isEmpty else {
            throw CourseValidationError.emptyName
        }

        self.value = value
    }
}
