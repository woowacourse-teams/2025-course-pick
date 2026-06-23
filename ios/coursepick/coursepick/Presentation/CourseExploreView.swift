import SwiftUI

struct CourseExploreView: View {
    var body: some View {
        GeometryReader { geometry in
            ZStack(alignment: .bottom) {
                NaverMapView()
                    .ignoresSafeArea()

                CourseSheet(height: geometry.size.height * 0.5) {
                    CourseListSheetView()
                }
            }
            .ignoresSafeArea(edges: .bottom)
        }
    }
}

#Preview {
    CourseExploreView()
}
