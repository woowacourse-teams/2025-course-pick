import SwiftUI

struct CourseListSheetView: View {
    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            Text("러닝 코스")
                .font(.system(size: 22, weight: .bold))
                .foregroundStyle(.textPrimary)
                .padding(.horizontal, 38)
                .padding(.top, 28)
                .padding(.bottom, 17)
            
            ScrollView {
                LazyVStack(spacing: 0) {
                    ForEach(0..<20, id: \.self) { _ in
                        CourseItemView(
                            courseName: "종로5가역-경복궁-북악산-혜화역",
                            distance: 4.56,
                            length: 7.43
                        )
                    }
                }
            }
        }
        .frame(maxWidth: .infinity, alignment: .leading)
    }
}

#Preview {
    CourseListSheetView()
}
