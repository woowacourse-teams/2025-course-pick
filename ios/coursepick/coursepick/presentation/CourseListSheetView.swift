import SwiftUI

struct CourseListSheetView: View {
    var body: some View {
        VStack {
            Text("코스 목록")
                .font(.system(size: 22, weight: .bold))
                .padding(.top, 38)
                .padding(.horizontal, 38)

            Spacer()
        }
        .frame(maxWidth: .infinity, alignment: .leading)
    }
}

#Preview {
    CourseListSheetView()
}
