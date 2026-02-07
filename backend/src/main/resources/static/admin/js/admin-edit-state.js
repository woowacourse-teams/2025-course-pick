/**
 * Edit 페이지 전역 상태 관리
 * 모든 모듈에서 공유하는 상태와 설정을 관리합니다.
 */

const EditState = {
    // URL에서 코스 ID 추출
    courseId: new URLSearchParams(window.location.search).get('id'),

    // 지도 및 UI 상태
    map: null,
    markers: [],
    polylines: [],
    course: null,
    originalCourse: null,
    isInitialLoad: true,

    // 선택 관련 상태
    selectedCoordinates: new Set(),
    currentEditingCoord: null,

    // 키보드 및 드래그 상태
    isShiftPressed: false,
    isDraggingMultiple: false,
    dragStartPosition: null,
    selectedMarkersInitialPositions: new Map(),
    dragAnimationFrameId: null,
    currentDraggingMarker: null,

    // 영역 선택 상태
    isAreaSelecting: false,
    areaSelectionStart: null,
    selectionBox: null,

    // 마커 이미지
    normalMarkerImage: null,
    selectedMarkerImage: null,
    normalMarkerImagePin: null,
    selectedMarkerImagePin: null,

    // OSRM Match 관련
    matchedCoordinatesData: null,
    matchPreviewMap: null,

    // 초기화
    init() {
        if (!this.courseId) {
            alert('코스 ID가 지정되지 않았습니다.');
            window.history.back();
        }
        this.selectionBox = document.getElementById('selection-box');
    }
};

// 전역 객체로 노출
window.EditState = EditState;
