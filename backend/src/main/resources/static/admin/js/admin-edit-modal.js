/**
 * 모달 관련 기능 (좌표 수정 모달)
 */

const EditModal = {
    /**
     * 좌표 수정 모달 열기
     */
    openCoordinateModal(coordIdx) {
        const coord = EditState.course.coordinates[coordIdx];
        EditState.currentEditingCoord = { coordIdx };

        document.getElementById('modal-latitude').value = coord.latitude.toFixed(6);
        document.getElementById('modal-longitude').value = coord.longitude.toFixed(6);

        const modal = document.getElementById('coordinate-edit-modal');
        modal.classList.add('active');

        setTimeout(() => {
            document.getElementById('modal-latitude').focus();
        }, 100);
    },

    /**
     * 좌표 수정 모달 닫기
     */
    closeCoordinateModal() {
        const modal = document.getElementById('coordinate-edit-modal');
        modal.classList.remove('active');
        EditState.currentEditingCoord = null;
    },

    /**
     * 모달에서 좌표 저장
     */
    saveCoordinateFromModal() {
        if (!EditState.currentEditingCoord) {
            EditUI.showMessage('수정할 좌표 정보가 없습니다.', 'error');
            return;
        }

        const { coordIdx } = EditState.currentEditingCoord;
        const newLat = parseFloat(document.getElementById('modal-latitude').value);
        const newLng = parseFloat(document.getElementById('modal-longitude').value);

        // 유효성 검사
        if (isNaN(newLat) || isNaN(newLng)) {
            EditUI.showMessage('잘못된 입력입니다. 숫자를 입력해주세요.', 'error');
            return;
        }

        if (newLat < -90 || newLat > 90) {
            EditUI.showMessage('위도는 -90 ~ 90 사이의 값이어야 합니다.', 'error');
            return;
        }

        if (newLng < -180 || newLng > 180) {
            EditUI.showMessage('경도는 -180 ~ 180 사이의 값이어야 합니다.', 'error');
            return;
        }

        // 좌표 업데이트
        const coord = EditState.course.coordinates[coordIdx];
        coord.latitude = newLat;
        coord.longitude = newLng;

        // 지도의 마커 위치 업데이트
        const newPosition = new kakao.maps.LatLng(newLat, newLng);
        const markerInfo = EditState.markers.find(m => m.coordIndex === coordIdx);
        if (markerInfo) {
            markerInfo.marker.setPosition(newPosition);
        }

        // 재렌더링
        EditMap.drawCourseOnMap();
        EditUI.displayCoordinatesList();
        EditUI.updateSaveButton();
        EditUI.showMessage('좌표가 수정되었습니다.', 'success');

        this.closeCoordinateModal();
    },

    /**
     * 모달에서 좌표 삭제
     */
    deleteCoordinateFromModal() {
        if (!EditState.currentEditingCoord) {
            EditUI.showMessage('삭제할 좌표 정보가 없습니다.', 'error');
            return;
        }

        const { coordIdx } = EditState.currentEditingCoord;

        if (!confirm('정말로 이 좌표를 삭제하시겠습니까?')) {
            return;
        }

        const totalCoordinates = EditState.course.coordinates.length;
        if (totalCoordinates <= 2) {
            EditUI.showMessage('코스는 최소 2개 이상의 좌표가 필요합니다.', 'error');
            return;
        }

        EditState.course.coordinates.splice(coordIdx, 1);

        const coordId = `${coordIdx}`;
        EditState.selectedCoordinates.delete(coordId);

        EditMap.drawCourseOnMap();
        EditUI.displayCourseInfo();
        EditUI.displayCoordinatesList();
        EditUI.updateSaveButton();
        EditUI.showMessage('좌표가 삭제되었습니다.', 'success');

        this.closeCoordinateModal();
    },

    /**
     * 모달 이벤트 리스너 초기화
     */
    initModalEvents() {
        // 모달 외부 클릭 시 닫기
        document.getElementById('coordinate-edit-modal').addEventListener('click', function (e) {
            if (e.target === this) {
                EditModal.closeCoordinateModal();
            }
        });

        // Enter 키로 저장
        document.getElementById('coordinate-edit-modal').addEventListener('keydown', function (e) {
            if (e.key === 'Enter' && this.classList.contains('active')) {
                e.preventDefault();
                EditModal.saveCoordinateFromModal();
            }
        });
    }
};

window.EditModal = EditModal;
