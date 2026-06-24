import SwiftUI

struct CourseItemView: View {
    @State var courseName: String
    @State var distance: Double?
    @State var length: Double
    
    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            if distance != nil {
                Text("내 위치에서 \(distance!, format: .number.precision(.fractionLength(2)))km만큼 떨어짐")
                    .font(Font.system(size: 12, weight: .regular))
                    .foregroundStyle(.itemTertiary)
            }
            
            Text(courseName)
                .font(Font.system(size: 17, weight: .bold))
                .foregroundStyle(.textPrimary)
            
            Text("\(length, format: .number.precision(.fractionLength(2)))km")
                .font(Font.system(size: 16, weight: .regular))
                .foregroundStyle(.textPrimary)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(.horizontal, 38)
        .padding(.vertical, 14)
        .overlay(alignment: .top) {
            Rectangle()
                .fill(.lightBorder)
                .frame(height: 1)
        }
    }
}

#Preview {
    CourseItemView(courseName: "종로5가역-경복궁-북악산-혜화역", distance: 4.56, length: 7.43)
}
