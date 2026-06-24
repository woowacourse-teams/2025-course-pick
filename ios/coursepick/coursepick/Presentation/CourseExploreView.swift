import SwiftUI

struct CourseExploreView: View {
    var body: some View {
        GeometryReader { geometry in
            ZStack(alignment: .bottom) {
                NaverMapView()

                CourseSheet(
                    collapsedHeight: geometry.size.height * 0.2,
                    expandedHeight: geometry.size.height * 0.5
                ) {
                    CourseListSheetView()
                }
            }
        }
        .ignoresSafeArea()
    }
}

#Preview {
    CourseExploreView()
}
