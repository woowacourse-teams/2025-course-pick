import SwiftUI

enum CourseListState {
    case loaded
    case empty
    case networkError
}

struct CourseListSheetView: View {
    let state: CourseListState
    let courses: [Course]
    @Binding var selectedCourse: Course?
    
    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            Text("러닝 코스")
                .font(.system(size: 22, weight: .bold))
                .foregroundStyle(.textPrimary)
                .padding(.horizontal, 38)
                .padding(.top, 28)
                .padding(.bottom, 17)
            
            content
                .frame(maxWidth: .infinity, maxHeight: .infinity)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
    }
    
    @ViewBuilder
    private var content: some View {
        switch state {
        case .loaded:
            ScrollView {
                LazyVStack(spacing: 0) {
                    ForEach(courses, id: \.self) { course in
                        Button {
                            selectedCourse = course
                        } label: {
                            CourseItemView(
                                courseName: course.name.value,
                                distance: course.distance.meters / 1000,
                                length: course.length.meters / 1000,
                                isSelected: course == selectedCourse
                            )
                        }
                        .buttonStyle(.plain)
                    }
                }
            }
            
        case .empty:
            VStack {
                Spacer()
                
                Text("이 곳엔 코스가 없어요")
                    .font(.system(size: 18, weight: .regular))
                    .foregroundStyle(.textPrimary)
                
                Spacer()
            }
            
        case .networkError:
            VStack(spacing: 8) {
                Spacer()
                
                Text("네트워크에 연결되지 않았습니다.")
                    .font(.system(size: 18, weight: .regular))
                    .foregroundStyle(.textPrimary)
                
                Text("설정을 확인하고 다시 시도해주세요.")
                    .font(.system(size: 18, weight: .regular))
                    .foregroundStyle(.textPrimary)
                
                Spacer()
            }
            .multilineTextAlignment(.center)
        }
    }
}

#Preview("Loaded") {
    let courses = DefaultCourseRepository().fetchCourses()

    CourseListSheetView(
        state: .loaded,
        courses: courses,
        selectedCourse: .constant(courses.first)
    )
}

#Preview("Empty") {
    CourseListSheetView(
        state: .empty,
        courses: [],
        selectedCourse: .constant(nil)
    )
}

#Preview("Network Error") {
    CourseListSheetView(
        state: .networkError,
        courses: [],
        selectedCourse: .constant(nil)
    )
}
