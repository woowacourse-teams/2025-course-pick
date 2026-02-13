/**
 * Edit 페이지 초기화 및 이벤트 바인딩
 */

// 전역 함수 등록 (HTML onclick에서 사용)
window.saveCourse = () => EditAPI.saveCourse();
window.resetChanges = () => EditAPI.resetChanges();
window.goBack = () => EditAPI.goBack();
window.clearAllSelections = () => EditCoordinates.clearAllSelections();
window.updateCourseName = (newName) => EditAPI.updateCourseName(newName);

/**
 * 페이지 초기화
 */
async function initializeEditPage() {
    // 상태 초기화
    EditState.init();

    // 지도 초기화
    EditMap.initMap();

    // 영역 선택 이벤트 리스너
    const mapContainer = document.getElementById('map');
    mapContainer.addEventListener('mousedown', (e) => EditCoordinates.startAreaSelection(e));
    mapContainer.addEventListener('mousemove', (e) => EditCoordinates.updateAreaSelection(e));
    mapContainer.addEventListener('mouseup', (e) => EditCoordinates.completeAreaSelection(e));

    // 키보드 이벤트 리스너
    document.addEventListener('keydown', function (e) {
        if (e.key === 'Shift') {
            EditState.isShiftPressed = true;
            if (EditState.map) {
                EditState.map.setDraggable(false);
            }
        } else if (e.key === 'Escape') {
            const modal = document.getElementById('coordinate-edit-modal');
            if (modal && modal.classList.contains('active')) {
                EditModal.closeCoordinateModal();
            } else if (EditState.selectedCoordinates.size > 0) {
                EditCoordinates.clearAllSelections();
            }
        }
    });

    document.addEventListener('keyup', function (e) {
        if (e.key === 'Shift') {
            EditState.isShiftPressed = false;
            if (EditState.map) {
                EditState.map.setDraggable(true);
            }
            if (EditState.isAreaSelecting) {
                EditCoordinates.cancelAreaSelection();
            }
        }
    });

    // 모달 이벤트 초기화
    EditModal.initModalEvents();
    EditOSRM.initOSRMEvents();

    // 코스 정보 로드
    await EditAPI.loadCourse();
}

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', initializeEditPage);
