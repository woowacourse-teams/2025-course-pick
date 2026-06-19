import SwiftUI

struct CourseExploreView: View {
    @State private var selectedDetent: PresentationDetent = .medium

    var body: some View {
        NaverMapView()
            .ignoresSafeArea()
            .sheet(isPresented: .constant(true)) {
                CourseListSheetView()
                    .presentationDetents([.fraction(0.15), .medium], selection: $selectedDetent)
                    .presentationDragIndicator(.visible)
                    .presentationBackground(.white)
                    .presentationBackgroundInteraction(.enabled(upThrough: .medium))
                    .interactiveDismissDisabled()
            }
    }
}

#Preview {
    CourseExploreView()
}
