import SwiftUI

struct CourseExploreView: View {
    private static let defaultInitialCoordinate: Coordinate? = {
        guard let latitude = try? Latitude(37.515411),
              let longitude = try? Longitude(127.1029607)
        else {
            return nil
        }

        return Coordinate(latitude: latitude, longitude: longitude)
    }()

    @State private var sheetHeight: CGFloat = 0
    @State private var selectedCourse: Course?

    private let courses: [Course]
    private let initialCoordinate: Coordinate?

    init(
        courses: [Course],
        initialCoordinate: Coordinate? = Self.defaultInitialCoordinate
    ) {
        self.courses = courses
        self.initialCoordinate = initialCoordinate
        self._selectedCourse = State(initialValue: courses.first)
    }

    var body: some View {
        GeometryReader { geometry in
            let collapsedHeight = geometry.size.height * 0.2
            let expandedHeight = geometry.size.height * 0.5
            let displayedSheetHeight = sheetHeight == 0 ? expandedHeight : sheetHeight

            ZStack(alignment: .bottomTrailing) {
                NaverMapView(
                    polylines: courses.map(\.coordinates),
                    selectedPolyline: selectedCourse?.coordinates,
                    initialCoordinate: initialCoordinate,
                    bottomContentInset: displayedSheetHeight
                ) { coordinates in
                    selectedCourse = courses.first { course in
                        course.coordinates == coordinates
                    }
                }

                CourseSheet(
                    collapsedHeight: collapsedHeight,
                    expandedHeight: expandedHeight,
                    displayedHeight: $sheetHeight
                ) {
                    CourseListSheetView(
                        state: .loaded,
                        courses: courses,
                        selectedCourse: $selectedCourse
                    )
                }

                Button {
                } label: {
                    Image(systemName: "scope")
                        .font(.system(size: 20))
                        .frame(width: 48, height: 48)
                        .background(.backgroundPrimary)
                        .clipShape(Circle())
                }
                .padding(.trailing, 8)
                .padding(.bottom, displayedSheetHeight + 8)
                .foregroundColor(.textPrimary)
                .buttonStyle(.plain)
            }
        }
        .ignoresSafeArea()
    }
}

#Preview {
    CourseExploreView(courses: DefaultCourseRepository().fetchCourses())
}
