/**
 * Find 페이지 전용 기능
 * 코스 목록 표시, 지도 렌더링, 페이지네이션
 */

const AdminList = {
    // 상태
    map: null,
    polylines: [],
    customOverlays: [],
    courses: [],
    selectedCourseIndex: null,
    currentPage: 0,
    lastSearchParams: null,
    searchCenterMarker: null,

    /**
     * 지도 초기화
     */
    initMap() {
        const mapContainer = document.getElementById('map');
        const mapOption = {
            center: new kakao.maps.LatLng(37.5665, 126.9780),
            level: 5
        };

        this.map = new kakao.maps.Map(mapContainer, mapOption);

        // 사용자 현재 위치로 이동
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition((position) => {
                const lat = position.coords.latitude;
                const lng = position.coords.longitude;
                const locPosition = new kakao.maps.LatLng(lat, lng);
                this.map.setCenter(locPosition);
            });
        }

        // 지도 이벤트 리스너
        kakao.maps.event.addListener(this.map, 'zoom_changed', () => this.updateScopeDisplay());
        kakao.maps.event.addListener(this.map, 'dragend', () => this.updateScopeDisplay());
        kakao.maps.event.addListener(this.map, 'tilesloaded', () => this.updateScopeDisplay());
    },

    /**
     * 에러 메시지 표시
     */
    showError(message) {
        const errorContainer = document.getElementById('error-container');
        errorContainer.innerHTML = `<div class="error-message">${message}</div>`;
        setTimeout(() => {
            errorContainer.innerHTML = '';
        }, 5000);
    },

    /**
     * 폴리라인 제거
     */
    clearPolylines() {
        this.polylines.forEach(polyline => polyline.setMap(null));
        this.polylines = [];
    },

    /**
     * 커스텀 오버레이 제거
     */
    clearCustomOverlays() {
        this.customOverlays.forEach(overlay => overlay.setMap(null));
        this.customOverlays = [];
    },

    /**
     * 검색 중심점 마커 표시
     */
    showSearchCenterMarker() {
        if (this.searchCenterMarker) {
            this.searchCenterMarker.setMap(null);
        }

        if (!this.lastSearchParams) return;

        const markerPosition = new kakao.maps.LatLng(
                this.lastSearchParams.mapLat,
                this.lastSearchParams.mapLng
        );
        this.searchCenterMarker = new kakao.maps.Marker({
            position: markerPosition,
            map: this.map
        });
    },

    /**
     * 지도 범위 기반 scope 계산
     */
    calculateScope() {
        const bounds = this.map.getBounds();
        const sw = bounds.getSouthWest();
        const center = this.map.getCenter();

        const R = 6371000;
        const lat1 = center.getLat() * Math.PI / 180;
        const lat2 = sw.getLat() * Math.PI / 180;
        const deltaLat = (sw.getLat() - center.getLat()) * Math.PI / 180;
        const deltaLng = (sw.getLng() - center.getLng()) * Math.PI / 180;

        const a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                Math.sin(deltaLng / 2) * Math.sin(deltaLng / 2);
        const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        const distance = R * c;

        const calculatedScope = Math.ceil(distance * 1.2);
        return Math.max(1000, Math.min(3000, calculatedScope));
    },

    /**
     * scope 디스플레이 업데이트
     */
    updateScopeDisplay() {
        const scope = this.calculateScope();
        const display = document.getElementById('scope-display');
        if (display) {
            display.textContent = scope.toLocaleString();
        }
    },

    /**
     * 코스 검색 (화면 기반)
     */
    async searchCourses(apiEndpoint) {
        const center = this.map.getCenter();
        const scope = this.calculateScope();

        this.lastSearchParams = {
            mapLat: center.getLat(),
            mapLng: center.getLng(),
            scope: scope
        };

        this.currentPage = 0;
        await this.loadCoursesPage(0, apiEndpoint);
    },

    /**
     * 특정 페이지의 코스 로드
     */
    async loadCoursesPage(page, apiEndpoint) {
        if (!this.lastSearchParams) return;

        const searchBtn = document.getElementById('search-btn');
        searchBtn.disabled = true;
        searchBtn.textContent = '검색 중...';

        try {
            const response = await axios.get(apiEndpoint, {
                params: {
                    ...this.lastSearchParams,
                    page: page
                },
                withCredentials: true
            });

            this.courses = response.data.courses || response.data || [];
            const hasNextPage = response.data.hasNext;
            this.currentPage = page;

            this.displayCourses();
            this.drawAllCoursesOnMap();
            this.updatePagination(hasNextPage);

        } catch (error) {
            if (error.response && error.response.status === 401) {
                alert('로그인 세션이 만료되었습니다.');
                window.location.href = '/admin/login';
            } else {
                this.showError('코스 검색에 실패했습니다. 다시 시도해주세요.');
            }
        } finally {
            searchBtn.disabled = false;
            searchBtn.textContent = '현재 화면에서 검색';
        }
    },

    /**
     * 페이지네이션 UI 업데이트
     */
    updatePagination(hasNext) {
        const pagination = document.getElementById('pagination');
        const prevBtn = document.getElementById('prev-page-btn');
        const nextBtn = document.getElementById('next-page-btn');
        const currentPageSpan = document.getElementById('current-page');

        currentPageSpan.textContent = this.currentPage + 1;
        prevBtn.disabled = this.currentPage === 0;
        nextBtn.disabled = hasNext !== undefined ? !hasNext : this.courses.length < 10;

        if (this.courses.length > 0) {
            pagination.style.display = 'flex';
        } else {
            pagination.style.display = 'none';
        }
    },

    /**
     * 코스 목록 표시
     */
    displayCourses() {
        const courseList = document.getElementById('course-list');
        const courseCount = document.getElementById('course-count');

        courseCount.textContent = this.courses.length;

        if (this.courses.length === 0) {
            courseList.innerHTML = `
                <div class="empty-state">
                    <div class="empty-state-icon">🔍</div>
                    <p>검색 결과가 없습니다.<br>다른 위치나 범위로 다시 검색해보세요.</p>
                </div>
            `;
            return;
        }

        courseList.innerHTML = this.courses.map((course, index) => {
            const distanceHtml = course.distance !== null ?
                    `<div class="course-detail"><strong>거리:</strong> ${(course.distance / 1000).toFixed(2)} km</div>` : '';

            return `
                <div class="course-item" data-index="${index}" data-course-id="${course.id}" data-course-name="${course.name}">
                    <div class="course-name">${course.name}</div>
                    <div class="course-detail"><strong>코스 길이:</strong> ${(course.length / 1000).toFixed(2)} km</div>
                    ${distanceHtml}
                    <div class="course-actions">
                        <button class="edit-button" onclick="event.stopPropagation(); window.location.href='/admin/courses/edit?id=${course.id}'">편집</button>
                        <button class="delete-button" data-course-id="${course.id}" data-course-name="${course.name}">삭제</button>
                    </div>
                </div>
            `;
        }).join('');

        this.attachCourseItemEvents();
    },

    /**
     * 코스 아이템 이벤트 연결
     */
    attachCourseItemEvents() {
        document.querySelectorAll('.course-item').forEach(item => {
            item.addEventListener('click', () => {
                const index = parseInt(item.dataset.index);
                this.selectCourse(index);
            });
        });

        document.querySelectorAll('.delete-button').forEach(button => {
            button.addEventListener('click', async (event) => {
                event.stopPropagation();
                const courseId = button.dataset.courseId;
                const courseName = button.dataset.courseName;

                if (!confirm(`정말로 "${courseName}" 코스를 삭제하시겠습니까?\n\n이 작업은 되돌릴 수 없습니다.`)) {
                    return;
                }

                try {
                    await axios.delete(`/admin/api/courses/${courseId}`, {
                        withCredentials: true
                    });

                    alert(`"${courseName}" 코스가 삭제되었습니다.`);

                    if (this.lastSearchParams) {
                        await this.loadCoursesPage(this.currentPage, this.getApiEndpoint());
                    }
                } catch (error) {
                    if (error.response && error.response.status === 401) {
                        alert('로그인 세션이 만료되었습니다.');
                        window.location.href = '/admin/login';
                    } else {
                        this.showError('코스 삭제에 실패했습니다. 다시 시도해주세요.');
                    }
                }
            });
        });
    },

    /**
     * 코스 선택
     */
    selectCourse(index) {
        document.querySelectorAll('.course-item').forEach(item => {
            item.classList.remove('selected');
        });

        document.querySelectorAll('.course-item')[index].classList.add('selected');
        this.drawAllCoursesWithSelection(index);
    },

    /**
     * 모든 코스를 지도에 그리기
     */
    drawAllCoursesOnMap() {
        this.clearPolylines();
        this.clearCustomOverlays();
        this.showSearchCenterMarker();
        this.selectedCourseIndex = null;

        this.courses.forEach((course, courseIndex) => {
            this.drawCourse(course, courseIndex, false);
        });
    },

    /**
     * 선택된 코스를 강조하여 그리기
     */
    drawAllCoursesWithSelection(selectedIndex) {
        this.clearPolylines();
        this.clearCustomOverlays();
        this.showSearchCenterMarker();

        this.courses.forEach((course, courseIndex) => {
            const isSelected = courseIndex === selectedIndex;
            this.drawCourse(course, courseIndex, isSelected);
        });
    },

    /**
     * 단일 코스 그리기
     */
    drawCourse(course, courseIndex, isSelected) {
        const coordinates = this.getCourseCoordinates(course);
        if (!coordinates || coordinates.length === 0) return;

        const path = coordinates.map(coord =>
                new kakao.maps.LatLng(coord.latitude, coord.longitude)
        );

        const strokeColor = isSelected ? '#2d3748' : '#888888';

        const polyline = new kakao.maps.Polyline({
            path: path,
            strokeWeight: isSelected ? 6 : 4,
            strokeColor: strokeColor,
            strokeOpacity: isSelected ? 0.9 : 0.7,
            strokeStyle: 'solid',
            zIndex: isSelected ? 100 : 1
        });

        kakao.maps.event.addListener(polyline, 'click', () => {
            this.selectCourse(courseIndex);
        });

        kakao.maps.event.addListener(polyline, 'mouseover', () => {
            polyline.setOptions({strokeOpacity: 1.0});
        });

        kakao.maps.event.addListener(polyline, 'mouseout', () => {
            polyline.setOptions({strokeOpacity: isSelected ? 0.9 : 0.7});
        });

        polyline.setMap(this.map);
        this.polylines.push(polyline);

        // 코스 이름 라벨
        const startCoord = coordinates[0];
        if (startCoord) {
            const position = new kakao.maps.LatLng(startCoord.latitude, startCoord.longitude);

            const content = `<div style="
                padding: 5px 10px;
                background: white;
                border: 2px solid ${isSelected ? '#2d3748' : '#cccccc'};
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
                clickable: true,
                zIndex: isSelected ? 100 : 1
            });

            customOverlay.setMap(this.map);
            this.customOverlays.push(customOverlay);

            setTimeout(() => {
                const overlayElement = customOverlay.getContent();
                if (overlayElement && overlayElement.addEventListener) {
                    overlayElement.addEventListener('click', () => {
                        this.selectCourse(courseIndex);
                    });
                }
            }, 0);
        }
    },

    /**
     * 코스 좌표 추출 (segments 또는 coordinates 지원)
     */
    getCourseCoordinates(course) {
        // find.html은 coordinates 직접 사용
        if (course.coordinates && course.coordinates.length > 0) {
            return course.coordinates;
        }
        // main.html은 segments 사용
        if (course.segments && course.segments.length > 0) {
            return course.segments.flatMap(segment => segment.coordinates || []);
        }
        return [];
    },

    /**
     * API 엔드포인트 반환 (하위 클래스에서 오버라이드)
     */
    getApiEndpoint() {
        return '/v1/courses';
    }
};

window.AdminList = AdminList;
