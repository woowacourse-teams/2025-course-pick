package coursepick.coursepick.presentation;

import coursepick.coursepick.application.exception.ErrorType;
import coursepick.coursepick.domain.Coordinate;
import coursepick.coursepick.domain.Course;
import coursepick.coursepick.domain.CourseRepository;
import coursepick.coursepick.presentation.dto.AdminCourseWebResponse;
import coursepick.coursepick.presentation.dto.AdminLoginWebRequest;
import coursepick.coursepick.presentation.dto.CourseRelaceWebRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.Cookie;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;

@RestController
@Profile("dev")
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

    @GetMapping("/admin/login")
    public ResponseEntity<String> adminLoginPage() {
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(ADMIN_LOGIN_PAGE);
    }

    @PostMapping("/admin/login")
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

    @GetMapping("/admin")
    public ResponseEntity<String> adminPage() {
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(ADMIN_MAIN_PAGE.replace("KAKAO_API_KEY_PLACEHOLDER", kakaoMapApiKey));
    }

    @GetMapping("/admin/courses/{id}")
    public AdminCourseWebResponse findCourseById(@PathVariable("id") String id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(ErrorType.NOT_EXIST_COURSE::create);

        return AdminCourseWebResponse.from(course);
    }

    @GetMapping("/admin/courses/edit")
    public ResponseEntity<String> courseEditPage() {
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(ADMIN_EDIT_PAGE.replace("KAKAO_API_KEY_PLACEHOLDER", kakaoMapApiKey));
    }

    @PatchMapping("/admin/courses/{id}")
    public ResponseEntity<Void> modifyCourse(
            @PathVariable("id") String courseId,
            @RequestBody CourseRelaceWebRequest request
    ) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(ErrorType.NOT_EXIST_COURSE::create);
        List<List<Double>> rawCoordinates = request.coordinates();

        if (rawCoordinates != null && !rawCoordinates.isEmpty()) {
            List<Coordinate> coordinates = rawCoordinates.stream()
                    .map(rawCoordinate -> new Coordinate(rawCoordinate.get(0), rawCoordinate.get(1), rawCoordinate.get(2)))
                    .toList();
            course.changeCoordinates(coordinates);
        }
        if (request.name() != null) course.changeName(request.name());
        if (request.roadType() != null) course.changeRoadType(request.roadType());

        courseRepository.save(course);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/admin/courses/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable("id") String id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(ErrorType.NOT_EXIST_COURSE::create);
        courseRepository.delete(course);

        return ResponseEntity.ok().build();
    }

    private static final String ADMIN_LOGIN_PAGE = """
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
                        const response = await axios.post('/admin/login', {
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
            """;

    private static final String ADMIN_MAIN_PAGE = """
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
                                       window.location.href = '/admin/login';
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
                                           <button class="edit-button" onclick="event.stopPropagation(); window.location.href='/admin/courses/edit?id=${course.id}'">편집</button>
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

                                       if (!confirm(`정말로 "${courseName}" 코스를 삭제하시겠습니까?

            이 작업은 되돌릴 수 없습니다.`)) {
                                           return;
                                       }

                                       try {
                                           await axios.delete(`/admin/courses/${courseId}`, {
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
                                               window.location.href = '/admin/login';
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

            """;

    private static final String ADMIN_EDIT_PAGE = """
            <!DOCTYPE html>
            <html lang="ko">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>코스 편집 - 런세권</title>
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
                        min-height: 100vh;
                    }

                    .header {
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                        color: white;
                        padding: 20px 30px;
                        box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
                        display: flex;
                        justify-content: space-between;
                        align-items: center;
                    }

                    .header h1 {
                        font-size: 24px;
                        font-weight: 700;
                    }

                    .header-buttons {
                        display: flex;
                        gap: 10px;
                    }

                    .back-button, .save-button {
                        padding: 10px 20px;
                        border: 2px solid white;
                        background: transparent;
                        color: white;
                        border-radius: 5px;
                        cursor: pointer;
                        font-size: 14px;
                        font-weight: 600;
                        transition: all 0.3s ease;
                    }

                    .back-button:hover {
                        background: white;
                        color: #667eea;
                    }

                    .save-button {
                        background: white;
                        color: #667eea;
                    }

                    .save-button:hover {
                        background: #f0f0f0;
                        transform: translateY(-2px);
                        box-shadow: 0 4px 8px rgba(255, 255, 255, 0.3);
                    }

                    .save-button:disabled {
                        opacity: 0.5;
                        cursor: not-allowed;
                        transform: none;
                    }

                    .content {
                        display: flex;
                        height: 70vh;
                        overflow: hidden;
                    }

                    .map-section {
                        flex: 3;
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
                        max-width: 300px;
                    }

                    .map-controls h3 {
                        margin-bottom: 15px;
                        color: #2d3748;
                        font-size: 18px;
                    }

                    .map-controls p {
                        font-size: 14px;
                        color: #718096;
                        margin-bottom: 10px;
                        line-height: 1.5;
                    }

                    .control-button {
                        padding: 10px 20px;
                        border: none;
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                        color: white;
                        border-radius: 5px;
                        cursor: pointer;
                        font-size: 14px;
                        font-weight: 600;
                        transition: all 0.3s ease;
                        width: 100%;
                        margin-top: 10px;
                    }

                    .control-button:hover {
                        transform: translateY(-2px);
                        box-shadow: 0 4px 10px rgba(102, 126, 234, 0.4);
                    }

                    .control-button:active {
                        transform: translateY(0);
                    }

                    .control-button:disabled {
                        opacity: 0.6;
                        cursor: not-allowed;
                        transform: none;
                    }

                    .control-button.secondary {
                        background: #e2e8f0;
                        color: #2d3748;
                    }

                    .control-button.secondary:hover {
                        background: #cbd5e0;
                    }

                    .info-section {
                        flex: 1;
                        background: white;
                        overflow-y: auto;
                        border-left: 1px solid #e2e8f0;
                        padding: 20px;
                    }

                    .info-section h2 {
                        color: #2d3748;
                        font-size: 20px;
                        margin-bottom: 20px;
                    }

                    .course-info {
                        margin-bottom: 20px;
                    }

                    .info-item {
                        margin-bottom: 15px;
                        padding-bottom: 15px;
                        border-bottom: 1px solid #e2e8f0;
                    }

                    .info-item:last-child {
                        border-bottom: none;
                    }

                    .info-label {
                        font-size: 14px;
                        font-weight: 600;
                        color: #4a5568;
                        margin-bottom: 5px;
                    }

                    .info-value {
                        font-size: 14px;
                        color: #2d3748;
                    }

                    .info-value.editable {
                        width: 100%;
                    }

                    .info-input {
                        width: 100%;
                        padding: 8px 12px;
                        border: 2px solid #e2e8f0;
                        border-radius: 6px;
                        font-size: 14px;
                        color: #2d3748;
                        transition: all 0.3s ease;
                        outline: none;
                    }

                    .info-input:focus {
                        border-color: #667eea;
                        box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
                    }

                    .info-select {
                        width: 100%;
                        padding: 8px 12px;
                        border: 2px solid #e2e8f0;
                        border-radius: 6px;
                        font-size: 14px;
                        color: #2d3748;
                        transition: all 0.3s ease;
                        outline: none;
                        background-color: white;
                        cursor: pointer;
                    }

                    .info-select:focus {
                        border-color: #667eea;
                        box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
                    }

                    .info-select:hover {
                        border-color: #cbd5e0;
                    }

                    .segment-list {
                        margin-top: 20px;
                    }

                    .segment-item {
                        background: #f7fafc;
                        border: 1px solid #e2e8f0;
                        border-radius: 8px;
                        padding: 15px;
                        margin-bottom: 10px;
                    }

                    .segment-header {
                        font-weight: 600;
                        color: #2d3748;
                        margin-bottom: 10px;
                        display: flex;
                        align-items: center;
                        gap: 10px;
                    }

                    .incline-badge {
                        padding: 4px 8px;
                        border-radius: 4px;
                        font-size: 12px;
                        font-weight: 600;
                    }

                    .incline-badge.UPHILL {
                        background: #fed7d7;
                        color: #c53030;
                    }

                    .incline-badge.FLAT {
                        background: #c6f6d5;
                        color: #276749;
                    }

                    .incline-badge.DOWNHILL {
                        background: #bee3f8;
                        color: #2c5282;
                    }

                    .coordinate-count {
                        font-size: 12px;
                        color: #718096;
                    }

                    .loading {
                        text-align: center;
                        padding: 40px;
                        color: #718096;
                    }

                    .error-message {
                        background: #fed7d7;
                        color: #c53030;
                        padding: 15px;
                        border-radius: 8px;
                        margin-bottom: 20px;
                        font-size: 14px;
                    }

                    .success-message {
                        background: #c6f6d5;
                        color: #276749;
                        padding: 15px;
                        border-radius: 8px;
                        margin-bottom: 20px;
                        font-size: 14px;
                    }

                    .marker-dragging {
                        cursor: move;
                    }

                    .coordinates-section {
                        background: white;
                        padding: 30px;
                        border-top: 2px solid #e2e8f0;
                    }

                    .coordinates-header {
                        display: flex;
                        justify-content: space-between;
                        align-items: center;
                        margin-bottom: 15px;
                    }

                    .coordinates-title {
                        font-size: 18px;
                        font-weight: 700;
                        color: #2d3748;
                    }

                    .bulk-actions {
                        display: flex;
                        gap: 10px;
                    }

                    .bulk-button {
                        padding: 8px 16px;
                        border: none;
                        border-radius: 6px;
                        font-size: 13px;
                        font-weight: 600;
                        cursor: pointer;
                        transition: all 0.2s ease;
                    }

                    .bulk-button.primary {
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                        color: white;
                    }

                    .bulk-button.primary:hover {
                        transform: translateY(-1px);
                        box-shadow: 0 4px 8px rgba(102, 126, 234, 0.3);
                    }

                    .bulk-button.secondary {
                        background: #e2e8f0;
                        color: #2d3748;
                    }

                    .bulk-button.secondary:hover {
                        background: #cbd5e0;
                    }

                    .bulk-button:disabled {
                        opacity: 0.5;
                        cursor: not-allowed;
                        transform: none;
                    }

                    .coordinates-table {
                        width: 100%;
                        border-collapse: collapse;
                        margin-top: 10px;
                        font-size: 13px;
                    }

                    .coordinates-table thead {
                        background: #f7fafc;
                        position: sticky;
                        top: 0;
                        z-index: 10;
                    }

                    .coordinates-table th {
                        padding: 12px 8px;
                        text-align: left;
                        font-weight: 600;
                        color: #4a5568;
                        border-bottom: 2px solid #e2e8f0;
                    }

                    .coordinates-table td {
                        padding: 10px 8px;
                        border-bottom: 1px solid #e2e8f0;
                        color: #2d3748;
                    }

                    .coordinates-table tr:hover {
                        background: #f7fafc;
                    }

                    .coordinates-table tr.selected {
                        background: #e6f0ff;
                    }

                    .coord-checkbox {
                        width: 18px;
                        height: 18px;
                        cursor: pointer;
                    }

                    .coord-number {
                        font-family: 'Monaco', 'Menlo', monospace;
                        color: #667eea;
                        font-weight: 600;
                    }

                    .coord-value {
                        font-family: 'Monaco', 'Menlo', monospace;
                        font-size: 12px;
                    }

                    .coord-edit-btn {
                        padding: 4px 12px;
                        border: 1px solid #667eea;
                        background: white;
                        color: #667eea;
                        border-radius: 4px;
                        font-size: 12px;
                        cursor: pointer;
                        transition: all 0.2s ease;
                    }

                    .coord-edit-btn:hover {
                        background: #667eea;
                        color: white;
                    }

                    .segment-group-header {
                        background: #edf2f7;
                        font-weight: 700;
                        color: #2d3748;
                    }

                    .select-all-label {
                        display: flex;
                        align-items: center;
                        gap: 6px;
                        font-size: 13px;
                        color: #4a5568;
                        cursor: pointer;
                    }

                    /* 모달 스타일 */
                    .modal-overlay {
                        display: none;
                        position: fixed;
                        top: 0;
                        left: 0;
                        width: 100%;
                        height: 100%;
                        background: rgba(0, 0, 0, 0.5);
                        z-index: 9999;
                        justify-content: center;
                        align-items: center;
                    }

                    .modal-overlay.active {
                        display: flex;
                    }

                    .modal-content {
                        background: white;
                        border-radius: 12px;
                        padding: 30px;
                        max-width: 500px;
                        width: 90%;
                        box-shadow: 0 10px 40px rgba(0, 0, 0, 0.3);
                        animation: modalSlideIn 0.3s ease;
                    }

                    @keyframes modalSlideIn {
                        from {
                            transform: translateY(-50px);
                            opacity: 0;
                        }
                        to {
                            transform: translateY(0);
                            opacity: 1;
                        }
                    }

                    .modal-header {
                        font-size: 20px;
                        font-weight: 700;
                        color: #2d3748;
                        margin-bottom: 20px;
                        display: flex;
                        justify-content: space-between;
                        align-items: center;
                    }

                    .modal-close {
                        background: none;
                        border: none;
                        font-size: 24px;
                        cursor: pointer;
                        color: #718096;
                        padding: 0;
                        width: 30px;
                        height: 30px;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        border-radius: 4px;
                        transition: all 0.2s ease;
                    }

                    .modal-close:hover {
                        background: #e2e8f0;
                        color: #2d3748;
                    }

                    .modal-form {
                        display: flex;
                        flex-direction: column;
                        gap: 20px;
                    }

                    .modal-form-group {
                        display: flex;
                        flex-direction: column;
                        gap: 8px;
                    }

                    .modal-label {
                        font-size: 14px;
                        font-weight: 600;
                        color: #4a5568;
                    }

                    .modal-input {
                        width: 100%;
                        padding: 12px 16px;
                        border: 2px solid #e2e8f0;
                        border-radius: 8px;
                        font-size: 14px;
                        color: #2d3748;
                        transition: all 0.3s ease;
                        outline: none;
                        font-family: 'Monaco', 'Menlo', monospace;
                    }

                    .modal-input:focus {
                        border-color: #667eea;
                        box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
                    }

                    .modal-actions {
                        display: flex;
                        gap: 10px;
                        margin-top: 10px;
                    }

                    .modal-button {
                        flex: 1;
                        padding: 12px 24px;
                        border: none;
                        border-radius: 8px;
                        font-size: 14px;
                        font-weight: 600;
                        cursor: pointer;
                        transition: all 0.3s ease;
                    }

                    .modal-button.primary {
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                        color: white;
                    }

                    .modal-button.primary:hover {
                        transform: translateY(-2px);
                        box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
                    }

                    .modal-button.secondary {
                        background: #e2e8f0;
                        color: #2d3748;
                    }

                    .modal-button.secondary:hover {
                        background: #cbd5e0;
                    }
                </style>
            </head>
            <body>
            <div class="container">
                <div class="header">
                    <h1>코스 편집</h1>
                    <div class="header-buttons">
                        <button class="save-button" onclick="saveCourse()">저장</button>
                        <button class="back-button" onclick="window.history.back()">← 뒤로 가기</button>
                    </div>
                </div>
                <div class="content">
                    <div class="map-section">
                        <div id="map"></div>
                        <div class="map-controls">
                            <h3>편집 가이드</h3>
                            <p>지도 위의 마커를 드래그하여 코스 좌표를 수정할 수 있습니다.</p>
                            <p>변경사항은 자동으로 저장됩니다.</p>
                            <button class="control-button secondary" onclick="resetChanges()">변경사항 초기화</button>
                        </div>
                    </div>
                    <div class="info-section">
                        <h2>코스 정보</h2>
                        <div id="message-container"></div>
                        <div id="course-info" class="loading">코스 정보를 불러오는 중...</div>
                    </div>
                </div>
                <div id="coordinates-container" class="coordinates-section" style="display: none;">
                    <div class="loading">좌표 목록을 불러오는 중...</div>
                </div>
            </div>

            <!-- 좌표 수정 모달 -->
            <div id="coordinate-edit-modal" class="modal-overlay">
                <div class="modal-content">
                    <div class="modal-header">
                        <span>좌표 수정</span>
                        <button class="modal-close" onclick="closeCoordinateModal()">&times;</button>
                    </div>
                    <div class="modal-form">
                        <div class="modal-form-group">
                            <label class="modal-label" for="modal-latitude">위도 (Latitude)</label>
                            <input type="number" id="modal-latitude" class="modal-input" step="0.000001" placeholder="예: 37.566535">
                        </div>
                        <div class="modal-form-group">
                            <label class="modal-label" for="modal-longitude">경도 (Longitude)</label>
                            <input type="number" id="modal-longitude" class="modal-input" step="0.000001" placeholder="예: 126.978000">
                        </div>
                        <div class="modal-form-group">
                            <label class="modal-label" for="modal-elevation">고도 (Elevation, m)</label>
                            <input type="number" id="modal-elevation" class="modal-input" step="0.1" placeholder="예: 15.5">
                        </div>
                        <div class="modal-actions">
                            <button class="modal-button secondary" onclick="closeCoordinateModal()">취소</button>
                            <button class="modal-button primary" onclick="saveCoordinateFromModal()">확인</button>
                        </div>
                    </div>
                </div>
            </div>

            <script src="https://cdn.jsdelivr.net/npm/axios/dist/axios.min.js"></script>
            <script type="text/javascript" src="//dapi.kakao.com/v2/maps/sdk.js?appkey=71f900d9b14c079c34329764a15e7ae2"></script>
            <script>
                // URL에서 코스 ID 추출
                const urlParams = new URLSearchParams(window.location.search);
                const courseId = urlParams.get('id');

                if (!courseId) {
                    alert('코스 ID가 지정되지 않았습니다.');
                    window.history.back();
                }

                let map;
                let course = null;
                let markers = [];
                let polylines = [];
                let originalCourse = null;
                let isInitialLoad = true; // 초기 로드 여부 플래그
                let selectedCoordinates = new Set(); // 선택된 좌표 (seg-idx_coord-idx 형식)
                let currentEditingCoord = null; // 현재 수정 중인 좌표 {segIdx, coordIdx}

                // 마커 이미지 설정
                let normalMarkerImage;
                let selectedMarkerImage;

                // 페이지 초기화
                initializePage();

                async function initializePage() {
                    // 카카오 맵 초기화
                    const mapContainer = document.getElementById('map');
                    const mapOption = {
                        center: new kakao.maps.LatLng(37.5665, 126.9780),
                        level: 5
                    };

                    map = new kakao.maps.Map(mapContainer, mapOption);

                    // 마커 이미지 초기화
                    const imageSize = new kakao.maps.Size(24, 35);
                    // normalMarkerImage는 null (카카오맵 기본 마커 사용)
                    normalMarkerImage = null;
                    // selectedMarkerImage는 빨간색 마커
                    selectedMarkerImage = new kakao.maps.MarkerImage(
                        'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/marker_red.png',
                        imageSize
                    );

                    // 코스 정보 로드
                    await loadCourse();
                }

                // 메시지 표시
                function showMessage(message, type = 'error') {
                    const messageContainer = document.getElementById('message-container');
                    const className = type === 'error' ? 'error-message' : 'success-message';
                    messageContainer.innerHTML = `<div class="${className}">${message}</div>`;
                    setTimeout(() => {
                        messageContainer.innerHTML = '';
                    }, 5000);
                }

                // 코스 정보 로드
                async function loadCourse() {
                    try {
                        const response = await axios.get(`/admin/courses/${courseId}`, {
                            withCredentials: true
                        });

                        console.log('API Response:', response.data);

                        if (response.data) {
                            course = response.data;
                            console.log('Course loaded:', course);
                            console.log('Segments:', course.segments);
                            originalCourse = JSON.parse(JSON.stringify(course)); // 깊은 복사
                            displayCourseInfo();
                            displayCoordinatesList();
                            drawCourseOnMap();
                        } else {
                            showMessage('코스를 찾을 수 없습니다.');
                        }
                    } catch (error) {
                        console.error('코스 로드 실패:', error);
                        if (error.response && error.response.status === 401) {
                            alert('로그인 세션이 만료되었습니다.');
                            window.location.href = '/admin/login';
                        } else if (error.response && error.response.status === 403) {
                            alert('권한이 없습니다. 관리자 로그인이 필요합니다.');
                            window.location.href = '/admin/login';
                        } else {
                            showMessage('코스 정보를 불러오는데 실패했습니다.');
                        }
                    }
                }

                // 코스 정보 표시
                function displayCourseInfo() {
                    try {
                        console.log('displayCourseInfo 시작');
                        const infoContainer = document.getElementById('course-info');

                        const totalCoordinates = course.segments.reduce((sum, segment) =>
                            sum + segment.coordinates.length, 0
                        );

                        let html = `
                            <div class="course-info">
                                <div class="info-item">
                                    <div class="info-label">코스 이름</div>
                                    <div class="info-value editable">
                                        <input type="text"
                                               id="course-name-input"
                                               class="info-input"
                                               value="${course.name}"
                                               onchange="updateCourseName(this.value)">
                                    </div>
                                </div>
                                <div class="info-item">
                                    <div class="info-label">코스 길이</div>
                                    <div class="info-value">${(course.length / 1000).toFixed(2)} km</div>
                                </div>
                                <div class="info-item">
                                    <div class="info-label">도로 타입</div>
                                    <div class="info-value editable">
                                        <select id="road-type-select"
                                                class="info-select"
                                                onchange="updateRoadType(this.value)">
                                            <option value="트랙" ${course.roadType === '트랙' ? 'selected' : ''}>트랙</option>
                                            <option value="트레일" ${course.roadType === '트레일' ? 'selected' : ''}>트레일</option>
                                            <option value="보도" ${course.roadType === '보도' ? 'selected' : ''}>보도</option>
                                            <option value="알수없음" ${course.roadType === '알수없음' ? 'selected' : ''}>알수없음</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="info-item">
                                    <div class="info-label">난이도</div>
                                    <div class="info-value">${course.difficulty}</div>
                                </div>
                                <div class="info-item">
                                    <div class="info-label">총 좌표 수</div>
                                    <div class="info-value">${totalCoordinates}개</div>
                                </div>
                            </div>
                            <div class="segment-list">
                                <h3 style="margin-bottom: 15px; color: #2d3748;">세그먼트 목록</h3>
                        `;

                        course.segments.forEach((segment, index) => {
                            html += `
                                <div class="segment-item">
                                    <div class="segment-header">
                                        <span>세그먼트 ${index + 1}</span>
                                        <span class="incline-badge ${segment.inclineType}">${segment.inclineType}</span>
                                    </div>
                                    <div class="coordinate-count">좌표 ${segment.coordinates.length}개</div>
                                </div>
                            `;
                        });

                        html += '</div>';

                        infoContainer.innerHTML = html;
                        console.log('displayCourseInfo 완료');
                    } catch (error) {
                        console.error('displayCourseInfo 에러:', error);
                        console.error('Error stack:', error.stack);
                        showMessage('코스 정보 표시 중 오류가 발생했습니다: ' + error.message, 'error');
                    }
                }

                // 좌표 목록 표시
                function displayCoordinatesList() {
                    try {
                        console.log('displayCoordinatesList 시작');
                        const coordinatesContainer = document.getElementById('coordinates-container');
                        coordinatesContainer.style.display = 'block';

                        let html = `
                            <div class="coordinates-header">
                                <div class="coordinates-title">전체 좌표 목록</div>
                                <div class="bulk-actions">
                                    <button class="bulk-button secondary" onclick="showSelectedOnMap()" id="show-on-map-btn" disabled>
                                        지도에서 보기
                                    </button>
                                    <button class="bulk-button primary" onclick="bulkEditCoordinates()" id="bulk-edit-btn" disabled>
                                        일괄 수정
                                    </button>
                                </div>
                            </div>
                            <table class="coordinates-table">
                                <thead>
                                    <tr>
                                        <th>
                                            <label class="select-all-label">
                                                <input type="checkbox" class="coord-checkbox" id="select-all-checkbox" onchange="toggleAllCoordinates()">
                                                전체 선택
                                            </label>
                                        </th>
                                        <th>번호</th>
                                        <th>세그먼트</th>
                                        <th>위도</th>
                                        <th>경도</th>
                                        <th>고도(m)</th>
                                        <th>편집</th>
                                    </tr>
                                </thead>
                                <tbody>
                        `;

                        let coordNumber = 1;
                        course.segments.forEach((segment, segIdx) => {
                            segment.coordinates.forEach((coord, coordIdx) => {
                                const coordId = `${segIdx}_${coordIdx}`;
                                const lat = (coord.latitude != null && !isNaN(coord.latitude)) ? Number(coord.latitude).toFixed(6) : 'N/A';
                                const lng = (coord.longitude != null && !isNaN(coord.longitude)) ? Number(coord.longitude).toFixed(6) : 'N/A';
                                const elev = (coord.elevation != null && !isNaN(coord.elevation)) ? Number(coord.elevation).toFixed(1) : 'N/A';

                                html += `
                                    <tr id="coord-row-${coordId}" class="${selectedCoordinates.has(coordId) ? 'selected' : ''}">
                                        <td>
                                            <input type="checkbox" class="coord-checkbox"
                                                   id="checkbox-${coordId}"
                                                   ${selectedCoordinates.has(coordId) ? 'checked' : ''}
                                                   onchange="toggleCoordinate(${segIdx}, ${coordIdx})">
                                        </td>
                                        <td class="coord-number">#${coordNumber}</td>
                                        <td>세그먼트 ${segIdx + 1}</td>
                                        <td class="coord-value">${lat}</td>
                                        <td class="coord-value">${lng}</td>
                                        <td class="coord-value">${elev}</td>
                                        <td>
                                            <button class="coord-edit-btn" onclick="editSingleCoordinate(${segIdx}, ${coordIdx})">
                                                수정
                                            </button>
                                        </td>
                                    </tr>
                                `;
                                coordNumber++;
                            });
                        });

                        html += `
                                </tbody>
                            </table>
                        `;

                        coordinatesContainer.innerHTML = html;
                        updateBulkActionButtons();
                        console.log('displayCoordinatesList 완료');
                    } catch (error) {
                        console.error('displayCoordinatesList 에러:', error);
                        console.error('Error stack:', error.stack);
                        showMessage('좌표 목록 표시 중 오류가 발생했습니다: ' + error.message, 'error');
                    }
                }

                // inclineType에 따른 색상 반환
                function getColorByInclineType(inclineType) {
                    switch (inclineType) {
                        case 'UPHILL':
                            return '#FF0000';
                        case 'FLAT':
                            return '#00FF00';
                        case 'DOWNHILL':
                            return '#0000FF';
                        default:
                            return '#808080';
                    }
                }

                // 지도에 코스 그리기
                function drawCourseOnMap() {
                    try {
                        console.log('drawCourseOnMap 시작');
                        console.log('course:', course);
                        console.log('map:', map);

                        // 기존 마커와 폴리라인 제거
                        clearMap();

                        if (!course || !course.segments || course.segments.length === 0) {
                            console.log('코스 데이터 없음:', { course, segments: course?.segments });
                            return;
                        }

                        console.log('세그먼트 개수:', course.segments.length);

                        let allCoordinates = [];

                        // 각 세그먼트별로 폴리라인 그리기
                        course.segments.forEach((segment, segmentIndex) => {
                            console.log(`세그먼트 ${segmentIndex}:`, segment);

                            if (!segment.coordinates || segment.coordinates.length === 0) {
                                console.log(`세그먼트 ${segmentIndex} 좌표 없음`);
                                return;
                            }

                            console.log(`세그먼트 ${segmentIndex} 좌표 개수:`, segment.coordinates.length);

                            // 좌표 배열을 카카오맵 LatLng 객체로 변환
                            const path = segment.coordinates.map(coord => {
                                console.log('좌표:', coord);
                                return new kakao.maps.LatLng(coord.latitude, coord.longitude);
                            });

                            allCoordinates.push(...path);

                            // 폴리라인 생성
                            const polyline = new kakao.maps.Polyline({
                                path: path,
                                strokeWeight: 5,
                                strokeColor: getColorByInclineType(segment.inclineType),
                                strokeOpacity: 0.8,
                                strokeStyle: 'solid'
                            });

                            polyline.setMap(map);
                            polylines.push(polyline);
                            console.log(`세그먼트 ${segmentIndex} 폴리라인 추가됨`);

                            // 각 좌표에 드래그 가능한 마커 추가
                            segment.coordinates.forEach((coord, coordIndex) => {
                                const position = new kakao.maps.LatLng(coord.latitude, coord.longitude);

                                const marker = new kakao.maps.Marker({
                                    position: position,
                                    draggable: true,
                                    map: map
                                    // image를 설정하지 않아 카카오맵 기본 마커 사용
                                });

                                // 마커 드래그 이벤트
                                kakao.maps.event.addListener(marker, 'dragend', function () {
                                    const newPosition = marker.getPosition();
                                    onMarkerDragEnd(segmentIndex, coordIndex, newPosition);
                                });

                                // 마커 오른쪽 클릭 이벤트
                                kakao.maps.event.addListener(marker, 'rightclick', function () {
                                    openCoordinateModal(segmentIndex, coordIndex);
                                });

                                markers.push({
                                    marker: marker,
                                    segmentIndex: segmentIndex,
                                    coordIndex: coordIndex
                                });
                            });
                            console.log(`세그먼트 ${segmentIndex} 마커 추가됨`);
                        });

                        console.log('총 마커 개수:', markers.length);
                        console.log('총 폴리라인 개수:', polylines.length);

                        // 지도 중심을 코스에 맞추기 (초기 로드 시에만)
                        if (isInitialLoad && allCoordinates.length > 0) {
                            console.log('지도 중심 설정 중...');
                            const bounds = new kakao.maps.LatLngBounds();
                            allCoordinates.forEach(coord => bounds.extend(coord));
                            map.setBounds(bounds);
                            isInitialLoad = false;
                            console.log('지도 중심 설정 완료');
                        }

                        console.log('drawCourseOnMap 완료');
                    } catch (error) {
                        console.error('drawCourseOnMap 에러:', error);
                        console.error('Error stack:', error.stack);
                        showMessage('지도에 코스를 그리는 중 오류가 발생했습니다: ' + error.message, 'error');
                    }
                }

                // 마커 드래그 종료 시 처리
                function onMarkerDragEnd(segmentIndex, coordIndex, newPosition) {
                    const segment = course.segments[segmentIndex];
                    const isFirstCoord = coordIndex === 0;
                    const isLastCoord = coordIndex === segment.coordinates.length - 1;

                    // 현재 좌표 업데이트
                    segment.coordinates[coordIndex].latitude = newPosition.getLat();
                    segment.coordinates[coordIndex].longitude = newPosition.getLng();

                    // 업데이트할 세그먼트 인덱스 수집
                    const segmentsToUpdate = [segmentIndex];
                    const markersToUpdate = [];

                    // 세그먼트 경계 좌표인 경우 인접 세그먼트와 마커도 업데이트
                    if (isFirstCoord && segmentIndex > 0) {
                        // 첫 번째 좌표이고 이전 세그먼트가 있는 경우
                        const prevSegment = course.segments[segmentIndex - 1];
                        const prevLastIndex = prevSegment.coordinates.length - 1;
                        prevSegment.coordinates[prevLastIndex].latitude = newPosition.getLat();
                        prevSegment.coordinates[prevLastIndex].longitude = newPosition.getLng();
                        segmentsToUpdate.push(segmentIndex - 1);

                        // 이전 세그먼트의 마지막 마커 찾아서 위치 업데이트
                        const prevMarkerInfo = markers.find(m =>
                            m.segmentIndex === segmentIndex - 1 &&
                            m.coordIndex === prevLastIndex
                        );
                        if (prevMarkerInfo) {
                            prevMarkerInfo.marker.setPosition(newPosition);
                        }
                    }

                    if (isLastCoord && segmentIndex < course.segments.length - 1) {
                        // 마지막 좌표이고 다음 세그먼트가 있는 경우
                        const nextSegment = course.segments[segmentIndex + 1];
                        nextSegment.coordinates[0].latitude = newPosition.getLat();
                        nextSegment.coordinates[0].longitude = newPosition.getLng();
                        segmentsToUpdate.push(segmentIndex + 1);

                        // 다음 세그먼트의 첫 번째 마커 찾아서 위치 업데이트
                        const nextMarkerInfo = markers.find(m =>
                            m.segmentIndex === segmentIndex + 1 &&
                            m.coordIndex === 0
                        );
                        if (nextMarkerInfo) {
                            nextMarkerInfo.marker.setPosition(newPosition);
                        }
                    }

                    // 영향받은 세그먼트의 폴리라인만 업데이트
                    updateSegmentPolylines(segmentsToUpdate);

                    // 변경 알림
                    let message = '좌표가 변경되었습니다.';
                    if (isFirstCoord && segmentIndex > 0) {
                        message += ' (이전 세그먼트와 연결됨)';
                    } else if (isLastCoord && segmentIndex < course.segments.length - 1) {
                        message += ' (다음 세그먼트와 연결됨)';
                    }
                    message += ' (자동 저장 기능은 추후 구현 예정)';
                    showMessage(message, 'success');
                    console.log('Updated course:', course);
                }

                // 특정 세그먼트들의 폴리라인만 업데이트
                function updateSegmentPolylines(segmentIndices) {
                    segmentIndices.forEach(segmentIndex => {
                        const segment = course.segments[segmentIndex];
                        if (!segment || !segment.coordinates || segment.coordinates.length === 0) {
                            return;
                        }

                        // 기존 폴리라인 제거 (해당 세그먼트만)
                        const oldPolyline = polylines[segmentIndex];
                        if (oldPolyline) {
                            oldPolyline.setMap(null);
                        }

                        // 새 경로로 폴리라인 생성
                        const path = segment.coordinates.map(coord =>
                            new kakao.maps.LatLng(coord.latitude, coord.longitude)
                        );

                        const newPolyline = new kakao.maps.Polyline({
                            path: path,
                            strokeWeight: 5,
                            strokeColor: getColorByInclineType(segment.inclineType),
                            strokeOpacity: 0.8,
                            strokeStyle: 'solid'
                        });

                        newPolyline.setMap(map);
                        polylines[segmentIndex] = newPolyline;
                    });
                }

                // 모든 폴리라인 업데이트
                function updatePolylines() {
                    // 기존 폴리라인 제거
                    polylines.forEach(polyline => polyline.setMap(null));
                    polylines = [];

                    // 폴리라인 다시 그리기
                    course.segments.forEach(segment => {
                        if (!segment.coordinates || segment.coordinates.length === 0) {
                            return;
                        }

                        const path = segment.coordinates.map(coord =>
                            new kakao.maps.LatLng(coord.latitude, coord.longitude)
                        );

                        const polyline = new kakao.maps.Polyline({
                            path: path,
                            strokeWeight: 5,
                            strokeColor: getColorByInclineType(segment.inclineType),
                            strokeOpacity: 0.8,
                            strokeStyle: 'solid'
                        });

                        polyline.setMap(map);
                        polylines.push(polyline);
                    });
                }

                // 지도 초기화
                function clearMap() {
                    markers.forEach(item => item.marker.setMap(null));
                    markers = [];
                    polylines.forEach(polyline => polyline.setMap(null));
                    polylines = [];
                }

                // 변경사항 초기화
                function resetChanges() {
                    if (confirm('모든 변경사항을 초기화하시겠습니까?')) {
                        course = JSON.parse(JSON.stringify(originalCourse));
                        displayCourseInfo();
                        displayCoordinatesList();
                        drawCourseOnMap();
                        showMessage('변경사항이 초기화되었습니다.', 'success');
                    }
                }

                // 좌표 선택/해제
                function toggleCoordinate(segIdx, coordIdx) {
                    const coordId = `${segIdx}_${coordIdx}`;
                    const row = document.getElementById(`coord-row-${coordId}`);
                    const checkbox = document.getElementById(`checkbox-${coordId}`);

                    // 마커 찾기
                    const markerInfo = markers.find(m =>
                        m.segmentIndex === segIdx && m.coordIndex === coordIdx
                    );

                    if (checkbox.checked) {
                        selectedCoordinates.add(coordId);
                        row.classList.add('selected');
                        // 마커 이미지를 선택 이미지로 변경
                        if (markerInfo && selectedMarkerImage) {
                            markerInfo.marker.setImage(selectedMarkerImage);
                            markerInfo.marker.setZIndex(1000);
                        }
                    } else {
                        selectedCoordinates.delete(coordId);
                        row.classList.remove('selected');
                        // 마커 이미지를 기본 이미지로 변경
                        if (markerInfo) {
                            if (normalMarkerImage) {
                                markerInfo.marker.setImage(normalMarkerImage);
                            } else {
                                // normalMarkerImage가 null이면 기본 마커로 복원
                                markerInfo.marker.setImage(null);
                            }
                            markerInfo.marker.setZIndex(1);
                        }
                    }

                    updateBulkActionButtons();
                    updateSelectAllCheckbox();
                }

                // 전체 선택/해제
                function toggleAllCoordinates() {
                    const selectAllCheckbox = document.getElementById('select-all-checkbox');
                    const isChecked = selectAllCheckbox.checked;

                    course.segments.forEach((segment, segIdx) => {
                        segment.coordinates.forEach((coord, coordIdx) => {
                            const coordId = `${segIdx}_${coordIdx}`;
                            const row = document.getElementById(`coord-row-${coordId}`);
                            const checkbox = document.getElementById(`checkbox-${coordId}`);

                            // 마커 찾기
                            const markerInfo = markers.find(m =>
                                m.segmentIndex === segIdx && m.coordIndex === coordIdx
                            );

                            if (checkbox) {
                                checkbox.checked = isChecked;
                                if (isChecked) {
                                    selectedCoordinates.add(coordId);
                                    row.classList.add('selected');
                                    // 마커 이미지를 선택 이미지로 변경
                                    if (markerInfo && selectedMarkerImage) {
                                        markerInfo.marker.setImage(selectedMarkerImage);
                                        markerInfo.marker.setZIndex(1000);
                                    }
                                } else {
                                    selectedCoordinates.delete(coordId);
                                    row.classList.remove('selected');
                                    // 마커 이미지를 기본 이미지로 변경
                                    if (markerInfo) {
                                        if (normalMarkerImage) {
                                            markerInfo.marker.setImage(normalMarkerImage);
                                        } else {
                                            // normalMarkerImage가 null이면 기본 마커로 복원
                                            markerInfo.marker.setImage(null);
                                        }
                                        markerInfo.marker.setZIndex(1);
                                    }
                                }
                            }
                        });
                    });

                    updateBulkActionButtons();
                }

                // 전체 선택 체크박스 상태 업데이트
                function updateSelectAllCheckbox() {
                    const selectAllCheckbox = document.getElementById('select-all-checkbox');
                    if (!selectAllCheckbox) return;

                    const totalCoordinates = course.segments.reduce((sum, segment) =>
                        sum + segment.coordinates.length, 0
                    );

                    if (selectedCoordinates.size === 0) {
                        selectAllCheckbox.checked = false;
                        selectAllCheckbox.indeterminate = false;
                    } else if (selectedCoordinates.size === totalCoordinates) {
                        selectAllCheckbox.checked = true;
                        selectAllCheckbox.indeterminate = false;
                    } else {
                        selectAllCheckbox.checked = false;
                        selectAllCheckbox.indeterminate = true;
                    }
                }

                // 일괄 수정 버튼 상태 업데이트
                function updateBulkActionButtons() {
                    const showOnMapBtn = document.getElementById('show-on-map-btn');
                    const bulkEditBtn = document.getElementById('bulk-edit-btn');

                    const hasSelection = selectedCoordinates.size > 0;

                    if (showOnMapBtn) {
                        showOnMapBtn.disabled = !hasSelection;
                    }
                    if (bulkEditBtn) {
                        bulkEditBtn.disabled = !hasSelection;
                    }
                }

                // 선택된 좌표 지도에 표시
                function showSelectedOnMap() {
                    if (selectedCoordinates.size === 0) {
                        showMessage('선택된 좌표가 없습니다.', 'error');
                        return;
                    }

                    // 모든 마커의 zIndex를 기본값으로 설정
                    markers.forEach(item => {
                        item.marker.setZIndex(1);
                    });

                    // 선택된 좌표의 마커만 강조
                    const selectedBounds = new kakao.maps.LatLngBounds();
                    let selectedCount = 0;

                    selectedCoordinates.forEach(coordId => {
                        const [segIdx, coordIdx] = coordId.split('_').map(Number);
                        const markerInfo = markers.find(m =>
                            m.segmentIndex === segIdx && m.coordIndex === coordIdx
                        );

                        if (markerInfo) {
                            markerInfo.marker.setZIndex(1000);
                            const position = markerInfo.marker.getPosition();
                            selectedBounds.extend(position);
                            selectedCount++;
                        }
                    });

                    if (selectedCount > 0) {
                        map.setBounds(selectedBounds);
                        showMessage(`${selectedCount}개의 선택된 좌표가 강조되었습니다.`, 'success');
                    }
                }

                // 단일 좌표 수정
                function editSingleCoordinate(segIdx, coordIdx) {
                    const coord = course.segments[segIdx].coordinates[coordIdx];

                    const latitude = prompt('위도를 입력하세요:', coord.latitude.toFixed(6));
                    if (latitude === null) return;

                    const longitude = prompt('경도를 입력하세요:', coord.longitude.toFixed(6));
                    if (longitude === null) return;

                    const elevation = prompt('고도(m)를 입력하세요:', coord.elevation.toFixed(1));
                    if (elevation === null) return;

                    const newLat = parseFloat(latitude);
                    const newLng = parseFloat(longitude);
                    const newElev = parseFloat(elevation);

                    // 유효성 검사
                    if (isNaN(newLat) || isNaN(newLng) || isNaN(newElev)) {
                        showMessage('잘못된 입력입니다. 숫자를 입력해주세요.', 'error');
                        return;
                    }

                    if (newLat < -90 || newLat > 90) {
                        showMessage('위도는 -90 ~ 90 사이의 값이어야 합니다.', 'error');
                        return;
                    }

                    if (newLng < -180 || newLng > 180) {
                        showMessage('경도는 -180 ~ 180 사이의 값이어야 합니다.', 'error');
                        return;
                    }

                    // 좌표 업데이트
                    coord.latitude = newLat;
                    coord.longitude = newLng;
                    coord.elevation = newElev;

                    // 세그먼트 경계 좌표인 경우 인접 세그먼트도 업데이트
                    const segment = course.segments[segIdx];
                    const isFirstCoord = coordIdx === 0;
                    const isLastCoord = coordIdx === segment.coordinates.length - 1;
                    const segmentsToUpdate = [segIdx];

                    if (isFirstCoord && segIdx > 0) {
                        const prevSegment = course.segments[segIdx - 1];
                        const prevLastIndex = prevSegment.coordinates.length - 1;
                        prevSegment.coordinates[prevLastIndex].latitude = newLat;
                        prevSegment.coordinates[prevLastIndex].longitude = newLng;
                        prevSegment.coordinates[prevLastIndex].elevation = newElev;
                        segmentsToUpdate.push(segIdx - 1);
                    }

                    if (isLastCoord && segIdx < course.segments.length - 1) {
                        const nextSegment = course.segments[segIdx + 1];
                        nextSegment.coordinates[0].latitude = newLat;
                        nextSegment.coordinates[0].longitude = newLng;
                        nextSegment.coordinates[0].elevation = newElev;
                        segmentsToUpdate.push(segIdx + 1);
                    }

                    // 지도의 마커 위치 업데이트
                    const newPosition = new kakao.maps.LatLng(newLat, newLng);
                    const markerInfo = markers.find(m =>
                        m.segmentIndex === segIdx && m.coordIndex === coordIdx
                    );
                    if (markerInfo) {
                        markerInfo.marker.setPosition(newPosition);
                    }

                    // 인접 마커들도 업데이트
                    if (isFirstCoord && segIdx > 0) {
                        const prevSegment = course.segments[segIdx - 1];
                        const prevLastIndex = prevSegment.coordinates.length - 1;
                        const prevMarkerInfo = markers.find(m =>
                            m.segmentIndex === segIdx - 1 && m.coordIndex === prevLastIndex
                        );
                        if (prevMarkerInfo) {
                            prevMarkerInfo.marker.setPosition(newPosition);
                        }
                    }

                    if (isLastCoord && segIdx < course.segments.length - 1) {
                        const nextMarkerInfo = markers.find(m =>
                            m.segmentIndex === segIdx + 1 && m.coordIndex === 0
                        );
                        if (nextMarkerInfo) {
                            nextMarkerInfo.marker.setPosition(newPosition);
                        }
                    }

                    // 폴리라인 업데이트
                    updateSegmentPolylines(segmentsToUpdate);

                    // UI 업데이트
                    displayCoordinatesList();
                    showMessage('좌표가 수정되었습니다.', 'success');
                }

                // 일괄 수정
                function bulkEditCoordinates() {
                    if (selectedCoordinates.size === 0) {
                        showMessage('선택된 좌표가 없습니다.', 'error');
                        return;
                    }

                    const elevationOffset = prompt(`${selectedCoordinates.size}개의 선택된 좌표의 고도를 일괄 수정합니다.\\n고도 변경값(m)을 입력하세요 (예: +10, -5):`, '+0');
                    if (elevationOffset === null) return;

                    const offset = parseFloat(elevationOffset);
                    if (isNaN(offset)) {
                        showMessage('잘못된 입력입니다. 숫자를 입력해주세요.', 'error');
                        return;
                    }

                    // 선택된 좌표들의 고도 일괄 수정
                    const updatedSegments = new Set();
                    selectedCoordinates.forEach(coordId => {
                        const [segIdx, coordIdx] = coordId.split('_').map(Number);
                        const coord = course.segments[segIdx].coordinates[coordIdx];
                        coord.elevation += offset;
                        updatedSegments.add(segIdx);

                        // 세그먼트 경계 좌표인 경우 인접 세그먼트도 체크
                        const segment = course.segments[segIdx];
                        const isFirstCoord = coordIdx === 0;
                        const isLastCoord = coordIdx === segment.coordinates.length - 1;

                        if (isFirstCoord && segIdx > 0) {
                            const prevSegment = course.segments[segIdx - 1];
                            const prevLastIndex = prevSegment.coordinates.length - 1;
                            prevSegment.coordinates[prevLastIndex].elevation = coord.elevation;
                            updatedSegments.add(segIdx - 1);
                        }

                        if (isLastCoord && segIdx < course.segments.length - 1) {
                            const nextSegment = course.segments[segIdx + 1];
                            nextSegment.coordinates[0].elevation = coord.elevation;
                            updatedSegments.add(segIdx + 1);
                        }
                    });

                    // UI 업데이트
                    displayCoordinatesList();
                    showMessage(`${selectedCoordinates.size}개 좌표의 고도가 ${offset > 0 ? '+' : ''}${offset}m 변경되었습니다.`, 'success');
                }

                // 코스 이름 업데이트
                function updateCourseName(newName) {
                    if (!course) {
                        showMessage('코스 정보를 찾을 수 없습니다.', 'error');
                        return;
                    }

                    // 빈 문자열 체크
                    if (!newName.trim()) {
                        showMessage('코스 이름을 입력해주세요.', 'error');
                        // 이전 값으로 복원
                        const nameInput = document.getElementById('course-name-input');
                        if (nameInput) {
                            nameInput.value = course.name;
                        }
                        return;
                    }

                    // 코스 이름 업데이트
                    course.name = newName.trim();
                    showMessage('코스 이름이 변경되었습니다.', 'success');
                    console.log('코스 이름 변경:', course.name);
                }

                // 도로 타입 업데이트
                function updateRoadType(newRoadType) {
                    if (!course) {
                        showMessage('코스 정보를 찾을 수 없습니다.', 'error');
                        return;
                    }

                    // 유효한 도로 타입인지 확인
                    const validRoadTypes = ['트랙', '트레일', '보도', '알수없음'];
                    if (!validRoadTypes.includes(newRoadType)) {
                        showMessage('유효하지 않은 도로 타입입니다.', 'error');
                        // 이전 값으로 복원
                        const roadTypeSelect = document.getElementById('road-type-select');
                        if (roadTypeSelect) {
                            roadTypeSelect.value = course.roadType;
                        }
                        return;
                    }

                    // 도로 타입 업데이트
                    course.roadType = newRoadType;
                    showMessage('도로 타입이 변경되었습니다.', 'success');
                    console.log('도로 타입 변경:', course.roadType);
                }

                // 코스 저장
                async function saveCourse() {
                    if (!course) {
                        showMessage('저장할 코스가 없습니다.', 'error');
                        return;
                    }

                    if (!confirm('변경사항을 저장하시겠습니까?')) {
                        return;
                    }

                    // 저장 버튼 비활성화
                    const saveButton = document.querySelector('.save-button');
                    if (saveButton) {
                        saveButton.disabled = true;
                        saveButton.textContent = '저장 중...';
                    }

                    try {
                        console.log('=== 저장할 코스 데이터 ===');
                        console.log('Course ID:', courseId);
                        console.log('Course Data:', JSON.stringify(course, null, 2));

                        // 모든 세그먼트의 좌표를 평탄화하여 [[lat, lng, elev], ...] 형식으로 변환
                        const coordinates = [];
                        course.segments.forEach(segment => {
                            segment.coordinates.forEach(coord => {
                                coordinates.push([
                                    coord.latitude,
                                    coord.longitude,
                                    coord.elevation
                                ]);
                            });
                        });

                        // API 요청 데이터 준비
                        const requestData = {
                            coordinates: coordinates,
                            name: course.name,
                            roadType: course.roadType
                        };

                        console.log('=== API 요청 데이터 ===');
                        console.log('Request Data:', JSON.stringify(requestData, null, 2));

                        // API 호출
                        const response = await axios.patch(`/admin/courses/${courseId}`, requestData, {
                            headers: {
                                'Content-Type': 'application/json'
                            },
                            withCredentials: true
                        });

                        if (response.status === 200) {
                            // 저장 성공 시 originalCourse 업데이트
                            originalCourse = JSON.parse(JSON.stringify(course));
                            showMessage('코스가 성공적으로 저장되었습니다.', 'success');
                            console.log('코스 저장 성공');
                        }

                    } catch (error) {
                        console.error('코스 저장 실패:', error);
                        if (error.response && error.response.status === 401) {
                            alert('로그인 세션이 만료되었습니다.');
                            window.location.href = '/admin/login';
                        } else if (error.response && error.response.status === 403) {
                            alert('권한이 없습니다. 관리자 로그인이 필요합니다.');
                            window.location.href = '/admin/login';
                        } else {
                            const errorMessage = error.response?.data?.message || error.message || '알 수 없는 오류';
                            showMessage('코스 저장에 실패했습니다: ' + errorMessage, 'error');
                        }
                    } finally {
                        // 저장 버튼 활성화
                        if (saveButton) {
                            saveButton.disabled = false;
                            saveButton.textContent = '저장';
                        }
                    }
                }

                // 좌표 수정 모달 열기
                function openCoordinateModal(segIdx, coordIdx) {
                    const coord = course.segments[segIdx].coordinates[coordIdx];

                    // 현재 수정 중인 좌표 정보 저장
                    currentEditingCoord = { segIdx, coordIdx };

                    // 모달 입력 필드에 현재 값 설정
                    document.getElementById('modal-latitude').value = coord.latitude.toFixed(6);
                    document.getElementById('modal-longitude').value = coord.longitude.toFixed(6);
                    document.getElementById('modal-elevation').value = coord.elevation.toFixed(1);

                    // 모달 표시
                    const modal = document.getElementById('coordinate-edit-modal');
                    modal.classList.add('active');

                    // 첫 번째 입력 필드에 포커스
                    setTimeout(() => {
                        document.getElementById('modal-latitude').focus();
                    }, 100);
                }

                // 좌표 수정 모달 닫기
                function closeCoordinateModal() {
                    const modal = document.getElementById('coordinate-edit-modal');
                    modal.classList.remove('active');
                    currentEditingCoord = null;
                }

                // 모달에서 좌표 저장
                function saveCoordinateFromModal() {
                    if (!currentEditingCoord) {
                        showMessage('수정할 좌표 정보가 없습니다.', 'error');
                        return;
                    }

                    const { segIdx, coordIdx } = currentEditingCoord;

                    // 입력값 가져오기
                    const newLat = parseFloat(document.getElementById('modal-latitude').value);
                    const newLng = parseFloat(document.getElementById('modal-longitude').value);
                    const newElev = parseFloat(document.getElementById('modal-elevation').value);

                    // 유효성 검사
                    if (isNaN(newLat) || isNaN(newLng) || isNaN(newElev)) {
                        showMessage('잘못된 입력입니다. 숫자를 입력해주세요.', 'error');
                        return;
                    }

                    if (newLat < -90 || newLat > 90) {
                        showMessage('위도는 -90 ~ 90 사이의 값이어야 합니다.', 'error');
                        return;
                    }

                    if (newLng < -180 || newLng > 180) {
                        showMessage('경도는 -180 ~ 180 사이의 값이어야 합니다.', 'error');
                        return;
                    }

                    // 좌표 업데이트
                    const coord = course.segments[segIdx].coordinates[coordIdx];
                    coord.latitude = newLat;
                    coord.longitude = newLng;
                    coord.elevation = newElev;

                    // 세그먼트 경계 좌표인 경우 인접 세그먼트도 업데이트
                    const segment = course.segments[segIdx];
                    const isFirstCoord = coordIdx === 0;
                    const isLastCoord = coordIdx === segment.coordinates.length - 1;
                    const segmentsToUpdate = [segIdx];

                    if (isFirstCoord && segIdx > 0) {
                        const prevSegment = course.segments[segIdx - 1];
                        const prevLastIndex = prevSegment.coordinates.length - 1;
                        prevSegment.coordinates[prevLastIndex].latitude = newLat;
                        prevSegment.coordinates[prevLastIndex].longitude = newLng;
                        prevSegment.coordinates[prevLastIndex].elevation = newElev;
                        segmentsToUpdate.push(segIdx - 1);
                    }

                    if (isLastCoord && segIdx < course.segments.length - 1) {
                        const nextSegment = course.segments[segIdx + 1];
                        nextSegment.coordinates[0].latitude = newLat;
                        nextSegment.coordinates[0].longitude = newLng;
                        nextSegment.coordinates[0].elevation = newElev;
                        segmentsToUpdate.push(segIdx + 1);
                    }

                    // 지도의 마커 위치 업데이트
                    const newPosition = new kakao.maps.LatLng(newLat, newLng);
                    const markerInfo = markers.find(m =>
                        m.segmentIndex === segIdx && m.coordIndex === coordIdx
                    );
                    if (markerInfo) {
                        markerInfo.marker.setPosition(newPosition);
                    }

                    // 인접 마커들도 업데이트
                    if (isFirstCoord && segIdx > 0) {
                        const prevSegment = course.segments[segIdx - 1];
                        const prevLastIndex = prevSegment.coordinates.length - 1;
                        const prevMarkerInfo = markers.find(m =>
                            m.segmentIndex === segIdx - 1 && m.coordIndex === prevLastIndex
                        );
                        if (prevMarkerInfo) {
                            prevMarkerInfo.marker.setPosition(newPosition);
                        }
                    }

                    if (isLastCoord && segIdx < course.segments.length - 1) {
                        const nextMarkerInfo = markers.find(m =>
                            m.segmentIndex === segIdx + 1 && m.coordIndex === 0
                        );
                        if (nextMarkerInfo) {
                            nextMarkerInfo.marker.setPosition(newPosition);
                        }
                    }

                    // 폴리라인 업데이트
                    updateSegmentPolylines(segmentsToUpdate);

                    // UI 업데이트
                    displayCoordinatesList();
                    showMessage('좌표가 수정되었습니다.', 'success');

                    // 모달 닫기
                    closeCoordinateModal();
                }

                // 모달 외부 클릭 시 닫기
                document.getElementById('coordinate-edit-modal').addEventListener('click', function(e) {
                    if (e.target === this) {
                        closeCoordinateModal();
                    }
                });

                // ESC 키로 모달 닫기
                document.addEventListener('keydown', function(e) {
                    if (e.key === 'Escape') {
                        const modal = document.getElementById('coordinate-edit-modal');
                        if (modal.classList.contains('active')) {
                            closeCoordinateModal();
                        }
                    }
                });

                // Enter 키로 모달 저장
                document.getElementById('coordinate-edit-modal').addEventListener('keydown', function(e) {
                    if (e.key === 'Enter' && this.classList.contains('active')) {
                        e.preventDefault();
                        saveCoordinateFromModal();
                    }
                });
            </script>
            </body>
            </html>
            """;

}
