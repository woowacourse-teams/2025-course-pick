package coursepick.coursepick.presentation;

import coursepick.coursepick.application.exception.ErrorType;
import coursepick.coursepick.domain.Coordinate;
import coursepick.coursepick.domain.Course;
import coursepick.coursepick.domain.CourseRepository;
import coursepick.coursepick.presentation.dto.AdminCourseWebResponse;
import coursepick.coursepick.presentation.dto.AdminLoginWebRequest;
import coursepick.coursepick.presentation.dto.CourseRelaceWebRequest;
import jakarta.validation.Valid;
import java.time.Duration;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.Cookie;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile({"local", "dev"})
public class AdminWebController {

    private static final String TOKEN_COOKIE_KEY = "admin-token";
    private final String adminToken;
    private final String kakaoMapApiKey;

    private final CourseRepository courseRepository;

    public AdminWebController(
            @Value("${admin.token}") String adminToken,
            @Value("${admin.kakao-map-api-key}") String kakaoMapApiKey,
            CourseRepository courseRepository
    ) {
        this.adminToken = adminToken;
        this.kakaoMapApiKey = kakaoMapApiKey;
        this.courseRepository = courseRepository;
    }

    @PostMapping("/api/admin/login")
    public ResponseEntity<Void> login(@RequestBody @Valid AdminLoginWebRequest request) {
        if (!adminToken.equals(request.password())) {
            throw ErrorType.INVALID_ADMIN_PASSWORD.create();
        }
        ResponseCookie tokenCookie = ResponseCookie.from(TOKEN_COOKIE_KEY, adminToken)
                .httpOnly(true)
                .maxAge(Duration.ofHours(1))
                .sameSite(Cookie.SameSite.STRICT.attributeValue())
                .path("/admin")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, tokenCookie.toString())
                .build();
    }

    @PatchMapping("/admin/course/{id}")
    public ResponseEntity<Void> modifyCourse(
            @PathVariable("id") String courseId,
            @RequestBody CourseRelaceWebRequest request
    ) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(ErrorType.NOT_EXIST_COURSE::create);
        List<List<Double>> rawCoordinates = request.coordinates();

        if (rawCoordinates != null && !rawCoordinates.isEmpty()) {
            List<Coordinate> coordinates = rawCoordinates.stream()
                    .map(rawCoordinate -> new Coordinate(rawCoordinate.get(0), rawCoordinate.get(1),
                            rawCoordinate.get(2)))
                    .toList();
            course.changeCoordinates(coordinates);
        }
        if (request.name() != null) {
            course.changeName(request.name());
        }
        if (request.roadType() != null) {
            course.changeRoadType(request.roadType());
        }

        courseRepository.save(course);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin/courses/{id}")
    public AdminCourseWebResponse findCourseById(@PathVariable("id") String id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(ErrorType.NOT_EXIST_COURSE::create);

        return AdminCourseWebResponse.from(course);
    }

    @DeleteMapping("/admin/course/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable("id") String id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(ErrorType.NOT_EXIST_COURSE::create);
        courseRepository.delete(course);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin/login")
    public ResponseEntity<String> adminLoginPage() {
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body("""
                        <!DOCTYPE html>
                        <html lang="ko">
                        <head>
                            <meta charset="UTF-8">
                            <meta name="viewport" content="width=device-width, initial-scale=1.0">
                            <title>런세권 관리자 로그인</title>
                            <style>
                        * {
                          margin: 0;
                          padding: 0;
                          box-sizing: border-box;
                        }

                        body {
                          font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', 'Oxygen',
                            'Ubuntu', 'Cantarell', 'Fira Sans', 'Droid Sans', 'Helvetica Neue',
                            sans-serif;
                          -webkit-font-smoothing: antialiased;
                          -moz-osx-font-smoothing: grayscale;
                        }

                        .admin-login-page-container {
                          display: flex;
                          justify-content: center;
                          align-items: center;
                          min-height: 100vh;
                          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                          padding: 20px;
                        }

                        .admin-login-container {
                          padding: 50px 40px;
                          background: white;
                          border-radius: 20px;
                          box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
                          width: 100%;
                          max-width: 450px;
                          text-align: center;
                          animation: slideIn 0.5s ease-out;
                        }

                        @keyframes slideIn {
                          from {
                            opacity: 0;
                            transform: translateY(-30px);
                          }
                          to {
                            opacity: 1;
                            transform: translateY(0);
                          }
                        }

                        .admin-login-title {
                          margin-bottom: 10px;
                          color: #2d3748;
                          font-size: 32px;
                          font-weight: 700;
                        }

                        .admin-login-subtitle {
                          margin-bottom: 30px;
                          color: #718096;
                          font-size: 14px;
                        }

                        .admin-login-form {
                          display: flex;
                          flex-direction: column;
                          gap: 20px;
                        }

                        .admin-login-input {
                          padding: 16px 20px;
                          border-radius: 10px;
                          border: 2px solid #e2e8f0;
                          font-size: 16px;
                          transition: all 0.3s ease;
                          outline: none;
                        }

                        .admin-login-input:focus {
                          border-color: #667eea;
                          box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
                        }

                        .admin-login-input::placeholder {
                          color: #a0aec0;
                        }

                        .admin-login-button {
                          padding: 16px;
                          border: none;
                          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                          color: white;
                          border-radius: 10px;
                          cursor: pointer;
                          font-size: 16px;
                          font-weight: 600;
                          transition: all 0.3s ease;
                          box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
                        }

                        .admin-login-button:hover {
                          transform: translateY(-2px);
                          box-shadow: 0 6px 20px rgba(102, 126, 234, 0.6);
                        }

                        .admin-login-button:active {
                          transform: translateY(0);
                        }

                        .admin-login-button:disabled {
                          opacity: 0.6;
                          cursor: not-allowed;
                          transform: none;
                        }

                        .error-message {
                          margin-top: 15px;
                          padding: 12px;
                          border-radius: 8px;
                          background-color: #fed7d7;
                          color: #c53030;
                          font-size: 14px;
                          display: none;
                          animation: shake 0.5s ease;
                        }

                        .error-message.show {
                          display: block;
                        }

                        @keyframes shake {
                          0%, 100% { transform: translateX(0); }
                          10%, 30%, 50%, 70%, 90% { transform: translateX(-5px); }
                          20%, 40%, 60%, 80% { transform: translateX(5px); }
                        }

                        @media (max-width: 480px) {
                          .admin-login-container {
                            padding: 40px 30px;
                          }

                          .admin-login-title {
                            font-size: 28px;
                          }
                        }
                            </style>
                        </head>
                        <body>
                        <div class="admin-login-page-container">
                            <div class="admin-login-container">
                                <h1 class="admin-login-title">🏃 런세권 관리자 페이지</h1>
                                <p class="admin-login-subtitle">안녕하세요 👋</p>
                                <form class="admin-login-form" id="admin-login-form">
                                    <input
                                            type="password"
                                            id="admin-password"
                                            placeholder="비밀번호를 입력하세요"
                                            class="admin-login-input"
                                            required
                                            autocomplete="current-password">
                                    <button type="submit" class="admin-login-button">로그인</button>
                                </form>
                                <div id="error-message" class="error-message"></div>
                            </div>
                        </div>
                        <script src="https://cdn.jsdelivr.net/npm/axios/dist/axios.min.js"></script>
                        <script>
                        document.addEventListener('DOMContentLoaded', () => {
                            const adminLoginForm = document.getElementById('admin-login-form');
                            const passwordInput = document.getElementById('admin-password');
                            const errorMessage = document.getElementById('error-message');
                            const loginButton = adminLoginForm.querySelector('button[type="submit"]');

                            const showError = (message) => {
                                errorMessage.textContent = message;
                                errorMessage.classList.add('show');
                                setTimeout(() => {
                                    errorMessage.classList.remove('show');
                                }, 5000);
                            };

                            const hideError = () => {
                                errorMessage.classList.remove('show');
                            };

                            adminLoginForm.addEventListener('submit', async (e) => {
                                e.preventDefault();
                                hideError();

                                const password = passwordInput.value.trim();

                                if (!password) {
                                    showError('비밀번호를 입력해주세요.');
                                    return;
                                }

                                loginButton.disabled = true;
                                loginButton.textContent = '로그인 중...';

                                try {
                                    const response = await axios.post('/api/admin/login', {
                                        password: password
                                    }, {
                                        withCredentials: true
                                    });

                                    window.location.href = '/admin';
                                } catch (error) {
                                    console.error('Admin login failed:', error);

                                    if (error.response) {
                                        const status = error.response.status;
                                        if (status === 401) {
                                            showError('비밀번호가 올바르지 않습니다.');
                                        } else if (status === 400) {
                                            showError('잘못된 요청입니다.');
                                        } else {
                                            showError('로그인에 실패했습니다. 다시 시도해주세요.');
                                        }
                                    } else if (error.request) {
                                        showError('서버에 연결할 수 없습니다.');
                                    } else {
                                        showError('로그인 처리 중 오류가 발생했습니다.');
                                    }

                                    passwordInput.value = '';
                                    passwordInput.focus();
                                } finally {
                                    loginButton.disabled = false;
                                    loginButton.textContent = '로그인';
                                }
                            });

                            passwordInput.addEventListener('input', () => {
                                hideError();
                            });

                            passwordInput.focus();
                        });
                        </script>
                        </body>
                        </html>
                        """
                );
    }

    @GetMapping("/admin")
    public ResponseEntity<String> adminPage() {
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body("""
                        <!DOCTYPE html>
                               <html lang="ko">
                               <head>
                                   <meta charset="UTF-8">
                                   <meta name="viewport" content="width=device-width, initial-scale=1.0">
                                   <title>런세권 관리자 페이지</title>
                                   <style>
                                       * {
                                           margin: 0;
                                           padding: 0;
                                           box-sizing: border-box;
                                       }

                                       body {
                                           font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', 'Oxygen',
                                           'Ubuntu', 'Cantarell', 'Fira Sans', 'Droid Sans', 'Helvetica Neue', sans-serif;
                                           -webkit-font-smoothing: antialiased;
                                           -moz-osx-font-smoothing: grayscale;
                                           background-color: #f5f5f5;
                                       }

                                       .container {
                                           display: flex;
                                           flex-direction: column;
                                           height: 100vh;
                                       }

                                       .header {
                                           background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                                           color: white;
                                           padding: 20px 30px;
                                           box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
                                       }

                                       .header h1 {
                                           font-size: 24px;
                                           font-weight: 700;
                                       }

                                       .content {
                                           display: flex;
                                           flex: 1;
                                           overflow: hidden;
                                       }

                                       .map-section {
                                           flex: 2;
                                           position: relative;
                                       }

                                       #map {
                                           width: 100%;
                                           height: 100%;
                                       }

                                       .map-controls {
                                           position: absolute;
                                           top: 20px;
                                           left: 20px;
                                           z-index: 1000;
                                           background: white;
                                           padding: 20px;
                                           border-radius: 10px;
                                           box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
                                       }

                                       .map-controls h3 {
                                           margin-bottom: 15px;
                                           color: #2d3748;
                                           font-size: 18px;
                                       }

                                       .control-row {
                                           display: flex;
                                           gap: 10px;
                                           margin-bottom: 10px;
                                       }

                                       .map-controls input {
                                           padding: 10px;
                                           border: 2px solid #e2e8f0;
                                           border-radius: 5px;
                                           font-size: 14px;
                                           flex: 1;
                                       }

                                       .map-controls input:focus {
                                           outline: none;
                                           border-color: #667eea;
                                       }

                                       .map-controls button {
                                           padding: 10px 20px;
                                           border: none;
                                           background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                                           color: white;
                                           border-radius: 5px;
                                           cursor: pointer;
                                           font-size: 14px;
                                           font-weight: 600;
                                           transition: all 0.3s ease;
                                           white-space: nowrap;
                                       }

                                       .map-controls button:hover {
                                           transform: translateY(-2px);
                                           box-shadow: 0 4px 10px rgba(102, 126, 234, 0.4);
                                       }

                                       .map-controls button:active {
                                           transform: translateY(0);
                                       }

                                       .map-controls button:disabled {
                                           opacity: 0.6;
                                           cursor: not-allowed;
                                           transform: none;
                                       }

                                       .course-list-section {
                                           flex: 1;
                                           background: white;
                                           overflow-y: auto;
                                           border-left: 1px solid #e2e8f0;
                                       }

                                       .course-list-header {
                                           padding: 20px;
                                           border-bottom: 2px solid #e2e8f0;
                                           background: #f7fafc;
                                       }

                                       .course-list-header h2 {
                                           color: #2d3748;
                                           font-size: 20px;
                                           margin-bottom: 5px;
                                       }

                                       .course-count {
                                           color: #718096;
                                           font-size: 14px;
                                       }

                                       .course-list {
                                           padding: 10px;
                                       }

                                       .course-item {
                                           background: white;
                                           border: 2px solid #e2e8f0;
                                           border-radius: 10px;
                                           padding: 15px;
                                           margin-bottom: 10px;
                                           transition: all 0.3s ease;
                                           cursor: pointer;
                                       }

                                       .course-item:hover {
                                           border-color: #667eea;
                                           box-shadow: 0 2px 8px rgba(102, 126, 234, 0.2);
                                           transform: translateY(-2px);
                                       }

                                       .course-item.selected {
                                           border-color: #667eea;
                                           background: #f0f4ff;
                                       }

                                       .course-name {
                                           font-size: 16px;
                                           font-weight: 600;
                                           color: #2d3748;
                                           margin-bottom: 8px;
                                       }

                                       .course-detail {
                                           font-size: 14px;
                                           color: #718096;
                                           margin-bottom: 4px;
                                       }

                                       .course-detail strong {
                                           color: #4a5568;
                                       }

                                       .course-actions {
                                           margin-top: 10px;
                                           display: flex;
                                           gap: 8px;
                                       }

                                       .edit-button {
                                           flex: 1;
                                           padding: 8px 16px;
                                           border: none;
                                           background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                                           color: white;
                                           border-radius: 5px;
                                           cursor: pointer;
                                           font-size: 13px;
                                           font-weight: 600;
                                           transition: all 0.3s ease;
                                       }

                                       .edit-button:hover {
                                           transform: translateY(-1px);
                                           box-shadow: 0 2px 6px rgba(102, 126, 234, 0.4);
                                       }

                                       .delete-button {
                                           flex: 1;
                                           padding: 8px 16px;
                                           border: none;
                                           background: linear-gradient(135deg, #e53e3e 0%, #c53030 100%);
                                           color: white;
                                           border-radius: 5px;
                                           cursor: pointer;
                                           font-size: 13px;
                                           font-weight: 600;
                                           transition: all 0.3s ease;
                                       }

                                       .delete-button:hover {
                                           transform: translateY(-1px);
                                           box-shadow: 0 2px 6px rgba(229, 62, 62, 0.4);
                                       }

                                       .loading {
                                           text-align: center;
                                           padding: 40px;
                                           color: #718096;
                                       }

                                       .empty-state {
                                           text-align: center;
                                           padding: 60px 20px;
                                           color: #718096;
                                       }

                                       .empty-state-icon {
                                           font-size: 48px;
                                           margin-bottom: 20px;
                                       }

                                       .error-message {
                                           background: #fed7d7;
                                           color: #c53030;
                                           padding: 15px;
                                           border-radius: 8px;
                                           margin: 10px 20px;
                                           font-size: 14px;
                                       }

                                       .pagination {
                                           display: flex;
                                           justify-content: center;
                                           align-items: center;
                                           gap: 10px;
                                           padding: 20px;
                                           border-top: 2px solid #e2e8f0;
                                           background: #f7fafc;
                                       }

                                       .pagination button {
                                           padding: 8px 16px;
                                           border: 2px solid #e2e8f0;
                                           background: white;
                                           color: #4a5568;
                                           border-radius: 5px;
                                           cursor: pointer;
                                           font-size: 14px;
                                           font-weight: 600;
                                           transition: all 0.3s ease;
                                       }

                                       .pagination button:hover:not(:disabled) {
                                           border-color: #667eea;
                                           color: #667eea;
                                           transform: translateY(-1px);
                                       }

                                       .pagination button:disabled {
                                           opacity: 0.4;
                                           cursor: not-allowed;
                                           transform: none;
                                       }

                                       .page-info {
                                           color: #4a5568;
                                           font-size: 14px;
                                           font-weight: 600;
                                           min-width: 80px;
                                           text-align: center;
                                       }
                                   </style>
                               </head>
                               <body>
                               <div class="container">
                                   <div class="header">
                                       <h1>런세권 관리자 페이지</h1>
                                   </div>
                                   <div class="content">
                                       <div class="map-section">
                                           <div id="map"></div>
                                           <div class="map-controls">
                                               <h3>코스 검색</h3>
                                               <div class="control-row">
                                                   <button id="search-btn">현재 화면에서 검색</button>
                                               </div>
                                               <div style="margin-top: 10px; font-size: 13px; color: #718096;">
                                                   검색 범위: <span id="scope-display">-</span>m
                                               </div>
                                           </div>
                                       </div>
                                       <div class="course-list-section">
                                           <div class="course-list-header">
                                               <h2>코스 목록</h2>
                                               <div class="course-count">총 <span id="course-count">0</span>개의 코스</div>
                                           </div>
                                           <div id="error-container"></div>
                                           <div class="course-list" id="course-list">
                                               <div class="empty-state">
                                                   <div class="empty-state-icon">🗺️</div>
                                                   <p>지도를 이동하고 검색 버튼을 눌러<br>주변 코스를 검색하세요</p>
                                               </div>
                                           </div>
                                           <div class="pagination" id="pagination" style="display: none;">
                                               <button id="prev-page-btn">이전</button>
                                               <div class="page-info">
                                                   <span id="current-page">1</span> 페이지
                                               </div>
                                               <button id="next-page-btn">다음</button>
                                           </div>
                                       </div>
                                   </div>
                               </div>

                               <script src="https://cdn.jsdelivr.net/npm/axios/dist/axios.min.js"></script>
                               <script type="text/javascript" src="//dapi.kakao.com/v2/maps/sdk.js?appkey=KAKAO_API_KEY_PLACEHOLDER"></script>
                               <script>
                                   // 페이지 초기화
                                   initializePage();

                                   function initializePage() {
                                       let map;
                                       let polylines = [];
                                       let customOverlays = [];
                                       let courses = [];
                                       let selectedCourseIndex = null; // 선택된 코스 인덱스
                                       let currentPage = 0; // 현재 페이지 번호
                                       let lastSearchParams = null; // 마지막 검색 파라미터 저장
                                       let searchCenterMarker = null; // 검색 중심점 마커

                                       // 카카오 맵 초기화
                                       const mapContainer = document.getElementById('map');
                                       const mapOption = {
                                           center: new kakao.maps.LatLng(37.5665, 126.9780), // 서울 시청 기본 위치
                                           level: 5
                                       };

                                       map = new kakao.maps.Map(mapContainer, mapOption);

                                       // 사용자 현재 위치로 이동
                                       if (navigator.geolocation) {
                                           navigator.geolocation.getCurrentPosition(function (position) {
                                               const lat = position.coords.latitude;
                                               const lng = position.coords.longitude;
                                               const locPosition = new kakao.maps.LatLng(lat, lng);
                                               map.setCenter(locPosition);
                                           });
                                       }

                                       // 에러 메시지 표시
                                       function showError(message) {
                                           const errorContainer = document.getElementById('error-container');
                                           errorContainer.innerHTML = `<div class="error-message">${message}</div>`;
                                           setTimeout(() => {
                                               errorContainer.innerHTML = '';
                                           }, 5000);
                                       }

                                       // 폴리라인 제거
                                       function clearPolylines() {
                                           polylines.forEach(polyline => polyline.setMap(null));
                                           polylines = [];
                                       }

                                       // 커스텀 오버레이 제거
                                       function clearCustomOverlays() {
                                           customOverlays.forEach(overlay => overlay.setMap(null));
                                           customOverlays = [];
                                       }

                                       // 검색 중심점 마커 표시
                                       function showSearchCenterMarker() {
                                           // 기존 마커 제거
                                           if (searchCenterMarker) {
                                               searchCenterMarker.setMap(null);
                                           }

                                           if (!lastSearchParams) return;

                                           // 새 마커 생성
                                           const markerPosition = new kakao.maps.LatLng(lastSearchParams.mapLat, lastSearchParams.mapLng);
                                           searchCenterMarker = new kakao.maps.Marker({
                                               position: markerPosition,
                                               map: map
                                           });
                                       }

                                       // 지도 범위 기반 scope 계산
                                       function calculateScope() {
                                           const bounds = map.getBounds();
                                           const sw = bounds.getSouthWest();
                                           const ne = bounds.getNorthEast();
                                           const center = map.getCenter();

                                           // 중심점에서 남서쪽 모서리까지의 거리 계산 (Haversine formula)
                                           const R = 6371000; // 지구 반지름 (미터)
                                           const lat1 = center.getLat() * Math.PI / 180;
                                           const lat2 = sw.getLat() * Math.PI / 180;
                                           const deltaLat = (sw.getLat() - center.getLat()) * Math.PI / 180;
                                           const deltaLng = (sw.getLng() - center.getLng()) * Math.PI / 180;

                                           const a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                                               Math.cos(lat1) * Math.cos(lat2) *
                                               Math.sin(deltaLng / 2) * Math.sin(deltaLng / 2);
                                           const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
                                           const distance = R * c;

                                           // 대각선 거리이므로 약간 여유를 두고 반올림
                                           const calculatedScope = Math.ceil(distance * 1.2);
                                           // 최소 1000m, 최대 3000m로 제한
                                           return Math.max(1000, Math.min(3000, calculatedScope));
                                       }

                                       // scope 디스플레이 업데이트
                                       function updateScopeDisplay() {
                                           const scope = calculateScope();
                                           document.getElementById('scope-display').textContent = scope.toLocaleString();
                                       }

                                       // 코스 검색 (새로운 검색 시작)
                                       async function searchCourses() {
                                           const center = map.getCenter();
                                           const scope = calculateScope();

                                           // 검색 파라미터 저장
                                           lastSearchParams = {
                                               mapLat: center.getLat(),
                                               mapLng: center.getLng(),
                                               scope: scope
                                           };

                                           // 페이지 초기화
                                           currentPage = 0;

                                           // 첫 페이지 로드
                                           await loadCoursesPage(0);
                                       }

                                       // 특정 페이지의 코스 로드
                                       async function loadCoursesPage(page) {
                                           if (!lastSearchParams) return;

                                           const searchBtn = document.getElementById('search-btn');
                                           searchBtn.disabled = true;
                                           searchBtn.textContent = '검색 중...';

                                           try {
                                               const response = await axios.get('/courses', {
                                                   params: {
                                                       ...lastSearchParams,
                                                       page: page
                                                   },
                                                   withCredentials: true
                                               });

                                               console.log('API 응답 성공:', response.data);
                                               courses = response.data;
                                               currentPage = page;

                                               try {
                                                   displayCourses(courses);
                                                   drawAllCoursesOnMap(courses);
                                                   updatePagination();
                                               } catch (displayError) {
                                                   console.error('코스 표시 중 에러:', displayError);
                                                   showError('코스를 표시하는 중 오류가 발생했습니다: ' + displayError.message);
                                               }
                                           } catch (error) {
                                               console.error('코스 검색 실패:', error);
                                               console.error('에러 상세:', error.response, error.request, error.message);
                                               if (error.response && error.response.status === 401) {
                                                   alert('로그인 세션이 만료되었습니다.');
                                                   window.location.href = '/admin-login';
                                               } else {
                                                   showError('코스 검색에 실패했습니다. 다시 시도해주세요.');
                                               }
                                           } finally {
                                               searchBtn.disabled = false;
                                               searchBtn.textContent = '현재 화면에서 검색';
                                           }
                                       }

                                       // 페이지네이션 UI 업데이트
                                       function updatePagination() {
                                           const pagination = document.getElementById('pagination');
                                           const prevBtn = document.getElementById('prev-page-btn');
                                           const nextBtn = document.getElementById('next-page-btn');
                                           const currentPageSpan = document.getElementById('current-page');

                                           // 페이지 번호 업데이트 (1부터 시작하도록 표시)
                                           currentPageSpan.textContent = currentPage + 1;

                                           // 이전 버튼 활성화/비활성화
                                           prevBtn.disabled = currentPage === 0;

                                           // 다음 버튼 활성화/비활성화 (10개 미만이면 마지막 페이지)
                                           nextBtn.disabled = courses.length < 10;

                                           // 페이지네이션 표시
                                           if (courses.length > 0) {
                                               pagination.style.display = 'flex';
                                           } else {
                                               pagination.style.display = 'none';
                                           }
                                       }

                                       // 코스 목록 표시
                                       function displayCourses(courses) {
                                           const courseList = document.getElementById('course-list');
                                           const courseCount = document.getElementById('course-count');

                                           courseCount.textContent = courses.length;

                                           if (courses.length === 0) {
                                               courseList.innerHTML = `
                                                   <div class="empty-state">
                                                       <div class="empty-state-icon">🔍</div>
                                                       <p>검색 결과가 없습니다.<br>다른 위치나 범위로 다시 검색해보세요.</p>
                                                   </div>
                                               `;
                                               return;
                                           }

                                           courseList.innerHTML = courses.map((course, index) => `
                                               <div class="course-item" data-index="${index}" data-course-id="${course.id}" data-course-name="${course.name}">
                                                   <div class="course-name">${course.name}</div>
                                                   <div class="course-detail"><strong>코스 길이:</strong> ${(course.length / 1000).toFixed(2)} km</div>
                                                   <div class="course-detail"><strong>도로 타입:</strong> ${course.roadType}</div>
                                                   <div class="course-detail"><strong>난이도:</strong> ${course.difficulty}</div>
                                                   ${course.distance !== null ? `<div class="course-detail"><strong>거리:</strong> ${(course.distance / 1000).toFixed(2)} km</div>` : ''}
                                                   <div class="course-actions">
                                                       <button class="edit-button" onclick="event.stopPropagation(); window.location.href='/course-edit.html?id=${course.id}'">편집</button>
                                                       <button class="delete-button" data-course-id="${course.id}" data-course-name="${course.name}">삭제</button>
                                                   </div>
                                               </div>
                                           `).join('');

                                           // 코스 아이템 클릭 이벤트
                                           document.querySelectorAll('.course-item').forEach(item => {
                                               item.addEventListener('click', function () {
                                                   const index = parseInt(this.dataset.index);
                                                   selectCourse(index);
                                               });
                                           });

                                           // 삭제 버튼 클릭 이벤트
                                           document.querySelectorAll('.delete-button').forEach(button => {
                                               button.addEventListener('click', async function (event) {
                                                   event.stopPropagation();
                                                   const courseId = this.dataset.courseId;
                                                   const courseName = this.dataset.courseName;

                                                   if (!confirm(`정말로 "${courseName}" 코스를 삭제하시겠습니까?\n\n이 작업은 되돌릴 수 없습니다.`)) {
                                                       return;
                                                   }

                                                   try {
                                                       await axios.delete(`/admin/course/${courseId}`, {
                                                           withCredentials: true
                                                       });

                                                       alert(`"${courseName}" 코스가 삭제되었습니다.`);

                                                       // 현재 페이지 새로고침
                                                       if (lastSearchParams) {
                                                           await loadCoursesPage(currentPage);
                                                       }
                                                   } catch (error) {
                                                       console.error('코스 삭제 실패:', error);
                                                       if (error.response && error.response.status === 401) {
                                                           alert('로그인 세션이 만료되었습니다.');
                                                           window.location.href = '/admin-login';
                                                       } else if (error.response && error.response.status === 404) {
                                                           showError('삭제하려는 코스를 찾을 수 없습니다.');
                                                       } else {
                                                           showError('코스 삭제에 실패했습니다. 다시 시도해주세요.');
                                                       }
                                                   }
                                               });
                                           });
                                       }

                                       // 코스의 시작 좌표 가져오기
                                       function getCourseStartCoordinate(course) {
                                           if (course.segments && course.segments.length > 0 &&
                                               course.segments[0].coordinates && course.segments[0].coordinates.length > 0) {
                                               return course.segments[0].coordinates[0];
                                           }
                                           return null;
                                       }

                                       // inclineType에 따른 색상 반환
                                       function getColorByInclineType(inclineType) {
                                           switch (inclineType) {
                                               case 'UPHILL':
                                                   return '#FF0000'; // 빨간색 (오르막)
                                               case 'FLAT':
                                                   return '#00FF00'; // 초록색 (평지)
                                               case 'DOWNHILL':
                                                   return '#0000FF'; // 파란색 (내리막)
                                               default:
                                                   return '#808080'; // 회색 (기타)
                                           }
                                       }

                                       // 코스 선택
                                       function selectCourse(index) {
                                           // 이전 선택 제거
                                           document.querySelectorAll('.course-item').forEach(item => {
                                               item.classList.remove('selected');
                                           });

                                           // 새로운 선택
                                           document.querySelectorAll('.course-item')[index].classList.add('selected');

                                           // 선택한 코스를 강조하여 지도에 그리기
                                           drawAllCoursesWithSelection(index);
                                       }

                                       // 모든 코스를 지도에 그리기 (초기 상태: 모두 회색)
                                       function drawAllCoursesOnMap(courses) {
                                           // 기존 폴리라인과 오버레이 제거
                                           clearPolylines();
                                           clearCustomOverlays();

                                           // 검색 중심점 마커 표시
                                           showSearchCenterMarker();

                                           // 선택 초기화
                                           selectedCourseIndex = null;

                                           courses.forEach((course, courseIndex) => {
                                               if (!course.segments || course.segments.length === 0) {
                                                   return;
                                               }

                                               // 각 segment를 순회하며 폴리라인 그리기
                                               course.segments.forEach(segment => {
                                                   if (!segment.coordinates || segment.coordinates.length === 0) {
                                                       return;
                                                   }

                                                   // 좌표 배열을 카카오맵 LatLng 객체로 변환
                                                   const path = segment.coordinates.map(coord =>
                                                       new kakao.maps.LatLng(coord.latitude, coord.longitude)
                                                   );

                                                   // 초기 상태는 모두 회색
                                                   const strokeColor = '#888888';

                                                   // 폴리라인 생성
                                                   const polyline = new kakao.maps.Polyline({
                                                       path: path,
                                                       strokeWeight: 4,
                                                       strokeColor: strokeColor,
                                                       strokeOpacity: 0.7,
                                                       strokeStyle: 'solid'
                                                   });

                                                   // 폴리라인 클릭 이벤트 추가
                                                   kakao.maps.event.addListener(polyline, 'click', function() {
                                                       selectCourse(courseIndex);
                                                   });

                                                   // 폴리라인 마우스오버 효과
                                                   kakao.maps.event.addListener(polyline, 'mouseover', function() {
                                                       polyline.setOptions({
                                                           strokeOpacity: 1.0
                                                       });
                                                   });

                                                   kakao.maps.event.addListener(polyline, 'mouseout', function() {
                                                       polyline.setOptions({
                                                           strokeOpacity: 0.7
                                                       });
                                                   });

                                                   // 지도에 표시
                                                   polyline.setMap(map);
                                                   polylines.push(polyline);
                                               });

                                               // 코스 시작점에 이름 라벨 추가
                                               const startCoord = getCourseStartCoordinate(course);
                                               if (startCoord) {
                                                   const position = new kakao.maps.LatLng(startCoord.latitude, startCoord.longitude);

                                                   const content = `<div style="
                                                       padding: 5px 10px;
                                                       background: white;
                                                       border: 2px solid #cccccc;
                                                       border-radius: 5px;
                                                       font-size: 12px;
                                                       font-weight: 400;
                                                       color: #718096;
                                                       box-shadow: 0 2px 6px rgba(0,0,0,0.3);
                                                       white-space: nowrap;
                                                       cursor: pointer;
                                                   ">${course.name}</div>`;

                                                   const customOverlay = new kakao.maps.CustomOverlay({
                                                       position: position,
                                                       content: content,
                                                       yAnchor: 1.5,
                                                       clickable: true
                                                   });

                                                   customOverlay.setMap(map);
                                                   customOverlays.push(customOverlay);

                                                   // 라벨 클릭 이벤트 추가 (DOM 요소에 직접 추가)
                                                   setTimeout(() => {
                                                       const overlayElement = customOverlay.getContent();
                                                       if (overlayElement && overlayElement.addEventListener) {
                                                           overlayElement.addEventListener('click', function() {
                                                               selectCourse(courseIndex);
                                                           });
                                                       }
                                                   }, 0);
                                               }
                                           });
                                       }

                                       // 선택된 코스를 강조하여 모든 코스를 지도에 그리기
                                       function drawAllCoursesWithSelection(selectedIndex) {
                                           // 기존 폴리라인과 오버레이 제거
                                           clearPolylines();
                                           clearCustomOverlays();

                                           // 검색 중심점 마커 표시 (유지)
                                           showSearchCenterMarker();

                                           courses.forEach((course, courseIndex) => {
                                               if (!course.segments || course.segments.length === 0) {
                                                   return;
                                               }

                                               const isSelected = courseIndex === selectedIndex;

                                               // 각 segment를 순회하며 폴리라인 그리기
                                               course.segments.forEach(segment => {
                                                   if (!segment.coordinates || segment.coordinates.length === 0) {
                                                       return;
                                                   }

                                                   // 좌표 배열을 카카오맵 LatLng 객체로 변환
                                                   const path = segment.coordinates.map(coord =>
                                                       new kakao.maps.LatLng(coord.latitude, coord.longitude)
                                                   );

                                                   // 선택된 코스는 원래 색상, 선택되지 않은 코스는 회색
                                                   const strokeColor = isSelected
                                                       ? getColorByInclineType(segment.inclineType)
                                                       : '#888888'; // 진한 회색

                                                   // 폴리라인 생성
                                                   const polyline = new kakao.maps.Polyline({
                                                       path: path,
                                                       strokeWeight: isSelected ? 6 : 4, // 선택된 코스는 더 두껍게
                                                       strokeColor: strokeColor,
                                                       strokeOpacity: isSelected ? 0.9 : 0.7, // 선택되지 않은 코스도 잘 보이도록
                                                       strokeStyle: 'solid'
                                                   });

                                                   // 폴리라인 클릭 이벤트 추가
                                                   kakao.maps.event.addListener(polyline, 'click', function() {
                                                       selectCourse(courseIndex);
                                                   });

                                                   // 폴리라인 마우스오버 효과
                                                   kakao.maps.event.addListener(polyline, 'mouseover', function() {
                                                       polyline.setOptions({
                                                           strokeOpacity: 1.0
                                                       });
                                                   });

                                                   kakao.maps.event.addListener(polyline, 'mouseout', function() {
                                                       polyline.setOptions({
                                                           strokeOpacity: isSelected ? 0.9 : 0.7
                                                       });
                                                   });

                                                   // 지도에 표시
                                                   polyline.setMap(map);
                                                   polylines.push(polyline);
                                               });

                                               // 코스 시작점에 이름 라벨 추가
                                               const startCoord = getCourseStartCoordinate(course);
                                               if (startCoord) {
                                                   const position = new kakao.maps.LatLng(startCoord.latitude, startCoord.longitude);

                                                   const content = `<div style="
                                                       padding: 5px 10px;
                                                       background: white;
                                                       border: 2px solid ${isSelected ? '#667eea' : '#cccccc'};
                                                       border-radius: 5px;
                                                       font-size: 12px;
                                                       font-weight: ${isSelected ? '700' : '400'};
                                                       color: ${isSelected ? '#2d3748' : '#718096'};
                                                       box-shadow: 0 2px 6px rgba(0,0,0,0.3);
                                                       white-space: nowrap;
                                                       cursor: pointer;
                                                   ">${course.name}</div>`;

                                                   const customOverlay = new kakao.maps.CustomOverlay({
                                                       position: position,
                                                       content: content,
                                                       yAnchor: 1.5,
                                                       clickable: true
                                                   });

                                                   customOverlay.setMap(map);
                                                   customOverlays.push(customOverlay);

                                                   // 라벨 클릭 이벤트 추가 (DOM 요소에 직접 추가)
                                                   setTimeout(() => {
                                                       const overlayElement = customOverlay.getContent();
                                                       if (overlayElement && overlayElement.addEventListener) {
                                                           overlayElement.addEventListener('click', function() {
                                                               selectCourse(courseIndex);
                                                           });
                                                       }
                                                   }, 0);
                                               }
                                           });
                                       }

                                       // 지도 이벤트 리스너 - 줌/드래그 시 scope 업데이트
                                       kakao.maps.event.addListener(map, 'zoom_changed', updateScopeDisplay);
                                       kakao.maps.event.addListener(map, 'dragend', updateScopeDisplay);
                                       kakao.maps.event.addListener(map, 'tilesloaded', function() {
                                           // 초기 로드 시 scope 표시
                                           updateScopeDisplay();
                                       });

                                       // 이벤트 리스너
                                       document.getElementById('search-btn').addEventListener('click', searchCourses);
                                       document.getElementById('prev-page-btn').addEventListener('click', function() {
                                           if (currentPage > 0) {
                                               loadCoursesPage(currentPage - 1);
                                           }
                                       });
                                       document.getElementById('next-page-btn').addEventListener('click', function() {
                                           loadCoursesPage(currentPage + 1);
                                       });
                                   }
                               </script>
                               </body>
                               </html>

                        """.replace("KAKAO_API_KEY_PLACEHOLDER", kakaoMapApiKey)
                );
    }
}
