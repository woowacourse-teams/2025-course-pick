/**
 * Find 페이지 전용 기능
 * 이름으로 코스 검색
 */

const AdminFind = {
    /**
     * 이름으로 코스 검색
     */
    async searchCourseByName() {
        const nameInput = document.getElementById('course-name-input');
        const searchBtn = document.getElementById('search-by-name-btn');
        const courseName = nameInput.value.trim();

        if (!courseName) {
            alert('코스 이름을 입력해주세요.');
            return;
        }

        searchBtn.disabled = true;
        searchBtn.textContent = '검색 중...';

        try {
            const response = await axios.get('/admin/api/courses', {
                params: { name: courseName },
                withCredentials: true
            });

            AdminList.courses = [response.data];
            AdminList.lastSearchParams = null;

            AdminList.displayCourses();
            AdminList.drawAllCoursesOnMap();

            // 페이지네이션 숨김
            document.getElementById('pagination').style.display = 'none';

            // 검색된 코스 위치로 지도 이동
            if (response.data.coordinates && response.data.coordinates.length > 0) {
                const firstCoord = response.data.coordinates[0];
                const moveLatLon = new kakao.maps.LatLng(firstCoord.latitude, firstCoord.longitude);
                AdminList.map.setCenter(moveLatLon);
                AdminList.map.setLevel(5);
            }

        } catch (error) {
            if (error.response && error.response.status === 401) {
                alert('로그인 세션이 만료되었습니다.');
                window.location.href = '/admin/login';
            } else if (error.response && error.response.status === 404) {
                alert('해당 이름의 코스를 찾을 수 없습니다.');
            } else {
                alert('코스 검색에 실패했습니다. 다시 시도해주세요.');
            }
        } finally {
            searchBtn.disabled = false;
            searchBtn.textContent = '이름 검색';
        }
    },

    /**
     * 페이지 초기화
     */
    initializePage() {
        AdminList.initMap();

        // API 엔드포인트 설정
        AdminList.getApiEndpoint = () => '/v1/courses';

        // 이벤트 리스너
        document.getElementById('search-btn').addEventListener('click', () => {
            AdminList.searchCourses('/v1/courses');
        });

        document.getElementById('search-by-name-btn').addEventListener('click', () => {
            this.searchCourseByName();
        });

        document.getElementById('course-name-input').addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                this.searchCourseByName();
            }
        });

        document.getElementById('prev-page-btn').addEventListener('click', () => {
            if (AdminList.currentPage > 0) {
                AdminList.loadCoursesPage(AdminList.currentPage - 1, '/v1/courses');
            }
        });

        document.getElementById('next-page-btn').addEventListener('click', () => {
            AdminList.loadCoursesPage(AdminList.currentPage + 1, '/v1/courses');
        });
    }
};

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', () => {
    AdminFind.initializePage();
});
