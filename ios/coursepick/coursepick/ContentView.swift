import SwiftUI

struct ContentView: View {
    private let courses = DefaultCourseRepository().fetchCourses()

    var body: some View {
        CourseExploreView(courses: courses)
    }
}

#Preview {
    ContentView()
}
