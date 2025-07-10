# 📱 Android SDK 버전 선정 및 사유 정리

---

## 🎯 `minSdkVersion`: `Android 10 (API level 29)`

### ✅ 선정 사유

1. **Kakao Maps SDK 최소 요구 사항 충족**
    - [공식 문서](https://apis.map.kakao.com/android_v2/docs/)에 따르면, Android 6.0(API 23) 이상을 요구
    - Android 10은 이보다 높은 수준으로, Kakao Maps SDK 활용에 문제 없음

2. **대한민국 사용자 기반의 Android 버전 점유율 고려**
    - StatCounter 기준, **Android 10 이상 사용자가 약 99%**
    - 즉, Android 10부터 지원해도 **사실상 거의 모든 사용자에게 도달 가능**
    -
   📊 [출처: Android Version Market Share – South Korea](https://gs.statcounter.com/android-version-market-share/mobile/south-korea)

3. **권한 처리 방식이 명확함**
    - Android 10부터 위치 권한 등 **민감한 권한 요청이 세분화**되고 정책이 강화됨
    - 초기부터 Android 10을 기준으로 개발하면, **명확한 권한 흐름을 전제로 설계할 수 있어 혼선이 줄어듦**

---

## 🚀 `targetSdkVersion`: `Android 15 (API level 35)`

### ✅ 선정 사유

1. **Google Play 배포 정책 대응**

   > 2025년 8월 31일부터, 최신 Android 출시로부터 1년 이내의 API 수준을 타겟팅하지 않으면 앱을 업데이트할 수 없음

    - 📎 [공식 정책 참고](https://developer.android.com/google/play/requirements/target-sdk)
