/**
 * API 호출 및 데이터 처리
 */

const EditAPI = {
    /**
     * 코스 정보 로드
     */
    async loadCourse() {
        try {
            const response = await axios.get(`/admin/api/courses/${EditState.courseId}`, {
                withCredentials: true
            });

            if (response.data) {
                EditState.course = response.data;
                EditState.originalCourse = JSON.parse(JSON.stringify(response.data));
                EditUI.displayCourseInfo();
                EditUI.displayCoordinatesList();
                EditMap.drawCourseOnMap();
                EditUI.updateSaveButton();
            } else {
                EditUI.showMessage('코스를 찾을 수 없습니다.');
            }
        } catch (error) {
            if (error.response && error.response.status === 401) {
                alert('로그인 세션이 만료되었습니다.');
                window.location.href = '/admin/login';
            } else if (error.response && error.response.status === 403) {
                alert('권한이 없습니다. 관리자 로그인이 필요합니다.');
                window.location.href = '/admin/login';
            } else {
                EditUI.showMessage('코스 정보를 불러오는데 실패했습니다.');
            }
        }
    },

    /**
     * 코스 이름 업데이트
     */
    updateCourseName(newName) {
        if (!EditState.course) {
            EditUI.showMessage('코스 정보를 찾을 수 없습니다.', 'error');
            return;
        }

        if (!newName.trim()) {
            EditUI.showMessage('코스 이름을 입력해주세요.', 'error');
            const nameInput = document.getElementById('course-name-input');
            if (nameInput) {
                nameInput.value = EditState.course.name;
            }
            return;
        }

        EditState.course.name = newName.trim();
        EditUI.updateSaveButton();
        EditUI.showMessage('코스 이름이 변경되었습니다.', 'success');
    },

    /**
     * 코스 저장
     */
    async saveCourse() {
        if (!EditState.course) {
            EditUI.showMessage('저장할 코스가 없습니다.', 'error');
            return;
        }

        if (!confirm('변경사항을 저장하시겠습니까?')) {
            return;
        }

        const saveButton = document.querySelector('.save-button');
        if (saveButton) {
            saveButton.disabled = true;
            saveButton.textContent = '저장 중...';
        }

        try {
            const coordinates = EditState.course.coordinates.map(coord => [
                coord.latitude,
                coord.longitude,
                coord.elevation
            ]);

            const requestData = {
                coordinates: coordinates,
                name: EditState.course.name,
                roadType: EditState.course.roadType
            };

            const response = await axios.patch(`/admin/api/courses/${EditState.courseId}`, requestData, {
                headers: {
                    'Content-Type': 'application/json'
                },
                withCredentials: true
            });

            if (response.status === 200) {
                EditState.originalCourse = JSON.parse(JSON.stringify(EditState.course));
                EditUI.updateSaveButton();
                EditUI.showMessage('코스가 성공적으로 저장되었습니다.', 'success');
            }

        } catch (error) {
            if (error.response && error.response.status === 401) {
                alert('로그인 세션이 만료되었습니다.');
                window.location.href = '/admin/login';
            } else if (error.response && error.response.status === 403) {
                alert('권한이 없습니다. 관리자 로그인이 필요합니다.');
                window.location.href = '/admin/login';
            } else {
                const errorMessage = error.response?.data?.message || error.message || '알 수 없는 오류';
                EditUI.showMessage('코스 저장에 실패했습니다: ' + errorMessage, 'error');
            }
        } finally {
            if (saveButton) {
                saveButton.disabled = false;
                saveButton.textContent = '저장';
            }
        }
    },

    /**
     * 변경사항 확인
     */
    hasChanges() {
        if (!EditState.course || !EditState.originalCourse) {
            return false;
        }
        return JSON.stringify(EditState.course) !== JSON.stringify(EditState.originalCourse);
    },

    /**
     * 변경사항 초기화
     */
    resetChanges() {
        if (confirm('모든 변경사항을 초기화하시겠습니까?')) {
            EditState.course = JSON.parse(JSON.stringify(EditState.originalCourse));
            EditUI.displayCourseInfo();
            EditUI.displayCoordinatesList();
            EditMap.drawCourseOnMap();
            EditUI.updateSaveButton();
            EditUI.showMessage('변경사항이 초기화되었습니다.', 'success');
        }
    },

    /**
     * 뒤로 가기
     */
    goBack() {
        if (this.hasChanges()) {
            if (confirm('저장하지 않은 변경사항이 있습니다. 정말로 나가시겠습니까?')) {
                window.history.back();
            }
        } else {
            window.history.back();
        }
    }
};

window.EditAPI = EditAPI;
