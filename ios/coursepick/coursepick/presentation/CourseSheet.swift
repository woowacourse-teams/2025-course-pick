import SwiftUI

struct CourseSheet<Content: View>: View {
    let height: CGFloat
    @ViewBuilder let content: Content

    var body: some View {
        VStack(spacing: 0) {
            Capsule()
                .fill(.grabber)
                .frame(width: 36, height: 5)
                .padding(.top, 5)

            content
        }
        .frame(maxWidth: .infinity)
        .background(.backgroundPrimary)
        .clipShape(.rect(topLeadingRadius: 38, topTrailingRadius: 38))
    }
}

#Preview {
    CourseSheet(height: 360) {
        CourseListSheetView()
    }
}
