/**
 * 지도 초기화 및 코스 그리기
 */

const EditMap = {
    /**
     * 카카오 지도 초기화
     */
    initMap() {
        const mapContainer = document.getElementById('map');
        const mapOption = {
            center: new kakao.maps.LatLng(37.5665, 126.9780),
            level: 1
        };

        EditState.map = new kakao.maps.Map(mapContainer, mapOption);
        this.initMarkerImages();
    },

    /**
     * 마커 이미지 초기화
     */
    initMarkerImages() {
        const dotSize = new kakao.maps.Size(6.76, 6.76);
        const pinSize = new kakao.maps.Size(20.8, 31.2);

        // 점 형태 마커
        const normalDotSvg = `data:image/svg+xml;base64,${btoa(`
            <svg xmlns="http://www.w3.org/2000/svg" width="6.76" height="6.76">
                <circle cx="3.38" cy="3.38" r="3.38" fill="#4A90E2" stroke="white" stroke-width="0.845"/>
            </svg>
        `)}`;

        const selectedDotSvg = `data:image/svg+xml;base64,${btoa(`
            <svg xmlns="http://www.w3.org/2000/svg" width="6.76" height="6.76">
                <circle cx="3.38" cy="3.38" r="3.38" fill="#FF0000" stroke="white" stroke-width="0.845"/>
            </svg>
        `)}`;

        EditState.normalMarkerImage = new kakao.maps.MarkerImage(normalDotSvg, dotSize);
        EditState.selectedMarkerImage = new kakao.maps.MarkerImage(selectedDotSvg, dotSize);

        // 핀 형태 마커
        EditState.normalMarkerImagePin = new kakao.maps.MarkerImage(
            'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/markerStar.png',
            pinSize
        );
        EditState.selectedMarkerImagePin = new kakao.maps.MarkerImage(
            'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/marker_red.png',
            pinSize
        );
    },

    /**
     * 지도에 코스 그리기
     */
    drawCourseOnMap() {
        this.clearMap();

        if (!EditState.course || !EditState.course.coordinates || EditState.course.coordinates.length === 0) {
            return;
        }

        // 폴리라인 생성
        const path = EditState.course.coordinates.map(coord =>
            new kakao.maps.LatLng(coord.latitude, coord.longitude)
        );

        const polyline = new kakao.maps.Polyline({
            path: path,
            strokeWeight: 5,
            strokeColor: '#2d3748',
            strokeOpacity: 0.8,
            strokeStyle: 'solid'
        });

        polyline.setMap(EditState.map);
        EditState.polylines.push(polyline);

        // 각 좌표에 드래그 가능한 마커 추가
        EditState.course.coordinates.forEach((coord, coordIndex) => {
            const position = new kakao.maps.LatLng(coord.latitude, coord.longitude);
            const marker = new kakao.maps.Marker({
                position: position,
                draggable: true,
                map: EditState.map,
                image: EditState.normalMarkerImage
            });

            this.addMarkerEvents(marker, coordIndex);
            EditState.markers.push({ marker: marker, coordIndex: coordIndex });
        });

        // 지도 중심 설정 (초기 로드 시에만)
        if (EditState.isInitialLoad && path.length > 0) {
            const bounds = new kakao.maps.LatLngBounds();
            path.forEach(coord => bounds.extend(coord));
            EditState.map.setBounds(bounds);
            EditState.isInitialLoad = false;
        }
    },

    /**
     * 마커에 이벤트 리스너 추가
     */
    addMarkerEvents(marker, coordIndex) {
        const coordId = `${coordIndex}`;

        // 클릭 이벤트
        kakao.maps.event.addListener(marker, 'click', () => {
            const checkbox = document.getElementById(`checkbox-${coordId}`);
            if (EditState.isShiftPressed) {
                if (checkbox) {
                    checkbox.checked = !checkbox.checked;
                    EditCoordinates.toggleCoordinate(coordIndex);
                }
            } else {
                EditCoordinates.clearAllSelections();
                if (checkbox) {
                    checkbox.checked = true;
                    EditCoordinates.toggleCoordinate(coordIndex);
                }
            }
        });

        // 마우스오버/아웃 이벤트
        kakao.maps.event.addListener(marker, 'mouseover', () => {
            const isSelected = EditState.selectedCoordinates.has(coordId);
            const pinImage = isSelected ? EditState.selectedMarkerImagePin : EditState.normalMarkerImagePin;
            marker.setImage(pinImage);
            marker.setZIndex(2000);
        });

        kakao.maps.event.addListener(marker, 'mouseout', () => {
            const isSelected = EditState.selectedCoordinates.has(coordId);
            const dotImage = isSelected ? EditState.selectedMarkerImage : EditState.normalMarkerImage;
            marker.setImage(dotImage);
            marker.setZIndex(isSelected ? 1000 : 1);
        });

        // 드래그 이벤트
        this.addMarkerDragEvents(marker, coordIndex);

        // 오른쪽 클릭 이벤트
        kakao.maps.event.addListener(marker, 'rightclick', () => {
            EditModal.openCoordinateModal(coordIndex);
        });
    },

    /**
     * 마커 드래그 이벤트 추가
     */
    addMarkerDragEvents(marker, coordIndex) {
        const coordId = `${coordIndex}`;

        kakao.maps.event.addListener(marker, 'dragstart', () => {
            const isSelected = EditState.selectedCoordinates.has(coordId);
            const pinImage = isSelected ? EditState.selectedMarkerImagePin : EditState.normalMarkerImagePin;
            marker.setImage(pinImage);

            if (EditState.selectedCoordinates.has(coordId) && EditState.selectedCoordinates.size > 1) {
                EditCoordinates.startMultipleDrag(marker);
            }
        });

        kakao.maps.event.addListener(marker, 'dragend', () => {
            if (EditState.dragAnimationFrameId) {
                cancelAnimationFrame(EditState.dragAnimationFrameId);
                EditState.dragAnimationFrameId = null;
            }

            const newPosition = marker.getPosition();
            const isSelected = EditState.selectedCoordinates.has(coordId);

            if (EditState.isDraggingMultiple) {
                EditCoordinates.endMultipleDrag();
            } else {
                this.onMarkerDragEnd(coordIndex, newPosition);
                const dotImage = isSelected ? EditState.selectedMarkerImage : EditState.normalMarkerImage;
                marker.setImage(dotImage);
            }

            EditState.isDraggingMultiple = false;
            EditState.currentDraggingMarker = null;
            EditState.dragStartPosition = null;
            EditState.selectedMarkersInitialPositions.clear();
        });
    },

    /**
     * 단일 마커 드래그 종료 처리
     */
    onMarkerDragEnd(coordIndex, newPosition) {
        EditState.course.coordinates[coordIndex].latitude = newPosition.getLat();
        EditState.course.coordinates[coordIndex].longitude = newPosition.getLng();
        this.updatePolylines();
        EditUI.updateSaveButton();
        EditUI.showMessage('좌표가 변경되었습니다.', 'success');
    },

    /**
     * 폴리라인 업데이트
     */
    updatePolylines() {
        EditState.polylines.forEach(polyline => polyline.setMap(null));
        EditState.polylines = [];

        if (!EditState.course.coordinates || EditState.course.coordinates.length === 0) {
            return;
        }

        const path = EditState.course.coordinates.map(coord =>
            new kakao.maps.LatLng(coord.latitude, coord.longitude)
        );

        const polyline = new kakao.maps.Polyline({
            path: path,
            strokeWeight: 5,
            strokeColor: '#2d3748',
            strokeOpacity: 0.8,
            strokeStyle: 'solid'
        });

        polyline.setMap(EditState.map);
        EditState.polylines.push(polyline);
    },

    /**
     * 지도 초기화 (마커 및 폴리라인 제거)
     */
    clearMap() {
        EditState.markers.forEach(item => item.marker.setMap(null));
        EditState.markers = [];
        EditState.polylines.forEach(polyline => polyline.setMap(null));
        EditState.polylines = [];
    }
};

window.EditMap = EditMap;
