/**
 * 좌표 선택, 편집, 삭제 기능
 */

const EditCoordinates = {
    /**
     * 좌표 선택/해제 토글
     */
    toggleCoordinate(coordIdx) {
        const coordId = `${coordIdx}`;
        const row = document.getElementById(`coord-row-${coordId}`);
        const checkbox = document.getElementById(`checkbox-${coordId}`);
        const markerInfo = EditState.markers.find(m => m.coordIndex === coordIdx);

        if (checkbox.checked) {
            EditState.selectedCoordinates.add(coordId);
            row.classList.add('selected');
            if (markerInfo && EditState.selectedMarkerImage) {
                markerInfo.marker.setImage(EditState.selectedMarkerImage);
                markerInfo.marker.setZIndex(1000);
            }
        } else {
            EditState.selectedCoordinates.delete(coordId);
            row.classList.remove('selected');
            if (markerInfo && EditState.normalMarkerImage) {
                markerInfo.marker.setImage(EditState.normalMarkerImage);
                markerInfo.marker.setZIndex(1);
            }
        }

        EditUI.updateBulkActionButtons();
        EditUI.updateSelectAllCheckbox();
    },

    /**
     * 모든 선택 해제
     */
    clearAllSelections() {
        const selectedArray = Array.from(EditState.selectedCoordinates);

        selectedArray.forEach(coordId => {
            const coordIdx = Number(coordId);
            const row = document.getElementById(`coord-row-${coordId}`);
            const checkbox = document.getElementById(`checkbox-${coordId}`);
            const markerInfo = EditState.markers.find(m => m.coordIndex === coordIdx);

            if (checkbox) checkbox.checked = false;
            if (row) row.classList.remove('selected');
            if (markerInfo && EditState.normalMarkerImage) {
                markerInfo.marker.setImage(EditState.normalMarkerImage);
                markerInfo.marker.setZIndex(1);
            }
        });

        EditState.selectedCoordinates.clear();
        EditUI.updateBulkActionButtons();
        EditUI.updateSelectAllCheckbox();
    },

    /**
     * 전체 선택/해제
     */
    toggleAllCoordinates() {
        const selectAllCheckbox = document.getElementById('select-all-checkbox');
        const isChecked = selectAllCheckbox.checked;

        EditState.course.coordinates.forEach((coord, coordIdx) => {
            const coordId = `${coordIdx}`;
            const row = document.getElementById(`coord-row-${coordId}`);
            const checkbox = document.getElementById(`checkbox-${coordId}`);
            const markerInfo = EditState.markers.find(m => m.coordIndex === coordIdx);

            if (checkbox) {
                checkbox.checked = isChecked;
                if (isChecked) {
                    EditState.selectedCoordinates.add(coordId);
                    row.classList.add('selected');
                    if (markerInfo && EditState.selectedMarkerImage) {
                        markerInfo.marker.setImage(EditState.selectedMarkerImage);
                        markerInfo.marker.setZIndex(1000);
                    }
                } else {
                    EditState.selectedCoordinates.delete(coordId);
                    row.classList.remove('selected');
                    if (markerInfo && EditState.normalMarkerImage) {
                        markerInfo.marker.setImage(EditState.normalMarkerImage);
                        markerInfo.marker.setZIndex(1);
                    }
                }
            }
        });

        EditUI.updateBulkActionButtons();
    },

    /**
     * 홀수번째 좌표 선택
     */
    selectOddCoordinates() {
        this.clearAllSelections();
        this.selectCoordinatesByPredicate((coordNumber) => coordNumber % 2 === 1);
        EditUI.showMessage(`${EditState.selectedCoordinates.size}개의 홀수번째 좌표가 선택되었습니다.`, 'success');
    },

    /**
     * 짝수번째 좌표 선택
     */
    selectEvenCoordinates() {
        this.clearAllSelections();
        this.selectCoordinatesByPredicate((coordNumber) => coordNumber % 2 === 0);
        EditUI.showMessage(`${EditState.selectedCoordinates.size}개의 짝수번째 좌표가 선택되었습니다.`, 'success');
    },

    /**
     * 조건에 맞는 좌표 선택
     */
    selectCoordinatesByPredicate(predicate) {
        let coordNumber = 1;
        EditState.course.coordinates.forEach((coord, coordIdx) => {
            if (predicate(coordNumber)) {
                const coordId = `${coordIdx}`;
                const row = document.getElementById(`coord-row-${coordId}`);
                const checkbox = document.getElementById(`checkbox-${coordId}`);
                const markerInfo = EditState.markers.find(m => m.coordIndex === coordIdx);

                if (checkbox) {
                    checkbox.checked = true;
                    EditState.selectedCoordinates.add(coordId);
                    row.classList.add('selected');
                    if (markerInfo && EditState.selectedMarkerImage) {
                        markerInfo.marker.setImage(EditState.selectedMarkerImage);
                        markerInfo.marker.setZIndex(1000);
                    }
                }
            }
            coordNumber++;
        });

        EditUI.updateBulkActionButtons();
        EditUI.updateSelectAllCheckbox();
    },

    /**
     * 선택된 좌표 일괄 삭제
     */
    bulkDeleteCoordinates() {
        if (EditState.selectedCoordinates.size === 0) {
            EditUI.showMessage('선택된 좌표가 없습니다.', 'error');
            return;
        }

        const totalCoordinates = EditState.course.coordinates.length;
        const remainingCoordinates = totalCoordinates - EditState.selectedCoordinates.size;

        if (remainingCoordinates < 2) {
            EditUI.showMessage(`코스는 최소 2개 이상의 좌표가 필요합니다. (현재: ${totalCoordinates}개, 선택: ${EditState.selectedCoordinates.size}개)`, 'error');
            return;
        }

        if (!confirm(`정말로 선택한 ${EditState.selectedCoordinates.size}개의 좌표를 삭제하시겠습니까?`)) {
            return;
        }

        // 역순 정렬하여 삭제
        const coordsToDelete = Array.from(EditState.selectedCoordinates)
            .map(coordId => parseInt(coordId))
            .sort((a, b) => b - a);

        coordsToDelete.forEach(coordIdx => {
            if (coordIdx >= 0 && coordIdx < EditState.course.coordinates.length) {
                EditState.course.coordinates.splice(coordIdx, 1);
            }
        });

        EditState.selectedCoordinates.clear();
        EditMap.drawCourseOnMap();
        EditUI.displayCourseInfo();
        EditUI.displayCoordinatesList();
        EditUI.updateSaveButton();
        EditUI.showMessage(`${coordsToDelete.length}개의 좌표가 삭제되었습니다.`, 'success');
    },

    /**
     * 다중 마커 드래그 시작
     */
    startMultipleDrag(marker) {
        EditState.isDraggingMultiple = true;
        EditState.currentDraggingMarker = marker;
        const startPos = marker.getPosition();
        EditState.dragStartPosition = new kakao.maps.LatLng(startPos.getLat(), startPos.getLng());

        // 모든 선택된 마커의 초기 위치 저장
        EditState.selectedMarkersInitialPositions.clear();
        EditState.selectedCoordinates.forEach(selectedCoordId => {
            const coordIdx = Number(selectedCoordId);
            const markerInfo = EditState.markers.find(m => m.coordIndex === coordIdx);
            if (markerInfo) {
                const pos = markerInfo.marker.getPosition();
                EditState.selectedMarkersInitialPositions.set(
                    selectedCoordId,
                    new kakao.maps.LatLng(pos.getLat(), pos.getLng())
                );
                markerInfo.marker.setImage(EditState.selectedMarkerImagePin);
            }
        });

        EditState.dragAnimationFrameId = requestAnimationFrame(this.updateMultipleDragPositions.bind(this));
    },

    /**
     * 다중 드래그 중 마커 위치 업데이트
     */
    updateMultipleDragPositions() {
        if (!EditState.isDraggingMultiple || !EditState.currentDraggingMarker || !EditState.dragStartPosition) {
            return;
        }

        const currentPosition = EditState.currentDraggingMarker.getPosition();
        const totalLatDiff = currentPosition.getLat() - EditState.dragStartPosition.getLat();
        const totalLngDiff = currentPosition.getLng() - EditState.dragStartPosition.getLng();

        EditState.selectedCoordinates.forEach(coordId => {
            const initialPosition = EditState.selectedMarkersInitialPositions.get(coordId);
            if (initialPosition) {
                const markerInfo = EditState.markers.find(m => {
                    const mCoordId = `${m.coordIndex}`;
                    return mCoordId === coordId;
                });

                if (markerInfo && markerInfo.marker !== EditState.currentDraggingMarker) {
                    const newPos = new kakao.maps.LatLng(
                        initialPosition.getLat() + totalLatDiff,
                        initialPosition.getLng() + totalLngDiff
                    );
                    markerInfo.marker.setPosition(newPos);
                }
            }
        });

        if (EditState.isDraggingMultiple) {
            EditState.dragAnimationFrameId = requestAnimationFrame(this.updateMultipleDragPositions.bind(this));
        }
    },

    /**
     * 다중 마커 드래그 종료
     */
    endMultipleDrag() {
        EditState.selectedCoordinates.forEach(coordId => {
            const coordIdx = Number(coordId);
            const markerInfo = EditState.markers.find(m => m.coordIndex === coordIdx);

            if (markerInfo) {
                const currentPos = markerInfo.marker.getPosition();
                EditState.course.coordinates[coordIdx].latitude = currentPos.getLat();
                EditState.course.coordinates[coordIdx].longitude = currentPos.getLng();
                markerInfo.marker.setImage(EditState.selectedMarkerImage);
            }
        });

        EditMap.updatePolylines();
        EditUI.displayCoordinatesList();
        EditUI.updateSaveButton();
        EditUI.showMessage(`${EditState.selectedCoordinates.size}개의 좌표가 함께 이동되었습니다.`, 'success');
    },

    /**
     * 영역 선택 시작
     */
    startAreaSelection(e) {
        if (!EditState.isShiftPressed) return;

        const mapContainer = document.getElementById('map');
        const rect = mapContainer.getBoundingClientRect();

        EditState.areaSelectionStart = {
            x: e.clientX - rect.left,
            y: e.clientY - rect.top
        };

        EditState.isAreaSelecting = true;

        EditState.selectionBox.style.left = EditState.areaSelectionStart.x + 'px';
        EditState.selectionBox.style.top = EditState.areaSelectionStart.y + 'px';
        EditState.selectionBox.style.width = '0px';
        EditState.selectionBox.style.height = '0px';
        EditState.selectionBox.style.display = 'block';
    },

    /**
     * 영역 선택 업데이트
     */
    updateAreaSelection(e) {
        if (!EditState.isAreaSelecting || !EditState.areaSelectionStart) return;

        const mapContainer = document.getElementById('map');
        const rect = mapContainer.getBoundingClientRect();

        const currentX = e.clientX - rect.left;
        const currentY = e.clientY - rect.top;

        const width = Math.abs(currentX - EditState.areaSelectionStart.x);
        const height = Math.abs(currentY - EditState.areaSelectionStart.y);
        const left = Math.min(currentX, EditState.areaSelectionStart.x);
        const top = Math.min(currentY, EditState.areaSelectionStart.y);

        EditState.selectionBox.style.left = left + 'px';
        EditState.selectionBox.style.top = top + 'px';
        EditState.selectionBox.style.width = width + 'px';
        EditState.selectionBox.style.height = height + 'px';
    },

    /**
     * 영역 선택 완료
     */
    completeAreaSelection(e) {
        if (!EditState.isAreaSelecting || !EditState.areaSelectionStart) return;

        const mapContainer = document.getElementById('map');
        const rect = mapContainer.getBoundingClientRect();

        const currentX = e.clientX - rect.left;
        const currentY = e.clientY - rect.top;

        const selectionRect = {
            left: Math.min(currentX, EditState.areaSelectionStart.x),
            top: Math.min(currentY, EditState.areaSelectionStart.y),
            right: Math.max(currentX, EditState.areaSelectionStart.x),
            bottom: Math.max(currentY, EditState.areaSelectionStart.y)
        };

        const selectedCount = this.selectMarkersInRect(selectionRect);

        EditState.selectionBox.style.display = 'none';
        EditState.isAreaSelecting = false;
        EditState.areaSelectionStart = null;

        if (selectedCount > 0) {
            EditUI.showMessage(`${selectedCount}개의 좌표가 선택되었습니다.`, 'success');
        }
    },

    /**
     * 사각형 영역 내의 마커 선택
     */
    selectMarkersInRect(selectionRect) {
        let selectedCount = 0;

        EditState.markers.forEach(markerInfo => {
            const marker = markerInfo.marker;
            const position = marker.getPosition();

            const projection = EditState.map.getProjection();
            const point = projection.containerPointFromCoords(position);

            if (point.x >= selectionRect.left && point.x <= selectionRect.right &&
                point.y >= selectionRect.top && point.y <= selectionRect.bottom) {

                const coordId = `${markerInfo.coordIndex}`;
                const checkbox = document.getElementById(`checkbox-${coordId}`);

                if (checkbox && !checkbox.checked) {
                    checkbox.checked = true;
                    this.toggleCoordinate(markerInfo.coordIndex);
                    selectedCount++;
                }
            }
        });

        return selectedCount;
    },

    /**
     * 영역 선택 취소
     */
    cancelAreaSelection() {
        EditState.selectionBox.style.display = 'none';
        EditState.isAreaSelecting = false;
        EditState.areaSelectionStart = null;
    }
};

window.EditCoordinates = EditCoordinates;
