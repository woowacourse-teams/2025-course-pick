/**
 * UI 업데이트 및 표시 기능
 */

const EditUI = {
    /**
     * 메시지 표시
     */
    showMessage(message, type = 'error') {
        const messageContainer = document.getElementById('message-container');
        const className = type === 'error' ? 'error-message' : 'success-message';
        messageContainer.innerHTML = `<div class="${className}">${message}</div>`;
        setTimeout(() => {
            messageContainer.innerHTML = '';
        }, 5000);
    },

    /**
     * 코스 정보 표시
     */
    displayCourseInfo() {
        const infoContainer = document.getElementById('course-info');
        const totalCoordinates = EditState.course.coordinates ? EditState.course.coordinates.length : 0;

        const html = `
            <div class="course-info">
                <div class="info-item">
                    <div class="info-label">코스 이름</div>
                    <div class="info-value editable">
                        <input type="text"
                               id="course-name-input"
                               class="info-input"
                               value="${EditState.course.name}"
                               onchange="EditAPI.updateCourseName(this.value)">
                    </div>
                </div>
                <div class="info-item">
                    <div class="info-label">코스 길이</div>
                    <div class="info-value">${(EditState.course.length / 1000).toFixed(2)} km</div>
                </div>
                <div class="info-item">
                    <div class="info-label">총 좌표 수</div>
                    <div class="info-value">${totalCoordinates}개</div>
                </div>
            </div>
        `;

        infoContainer.innerHTML = html;
    },

    /**
     * 좌표 목록 표시
     */
    displayCoordinatesList() {
        const coordinatesContainer = document.getElementById('coordinates-container');
        coordinatesContainer.style.display = 'block';

        let html = `
            <div class="coordinates-header">
                <div class="coordinates-title">전체 좌표 목록</div>
                <div class="bulk-actions">
                    <button class="bulk-button secondary" onclick="EditCoordinates.selectOddCoordinates()">
                        홀수 선택
                    </button>
                    <button class="bulk-button secondary" onclick="EditCoordinates.selectEvenCoordinates()">
                        짝수 선택
                    </button>
                    <button class="bulk-button secondary" onclick="EditUI.showSelectedOnMap()" id="show-on-map-btn" disabled>
                        지도에서 보기
                    </button>
                    <button class="bulk-button primary" onclick="EditOSRM.matchSelectedCoordinates()" id="match-button" disabled>
                        좌표 보정 (OSRM Match)
                    </button>
                    <button class="bulk-button danger" onclick="EditCoordinates.bulkDeleteCoordinates()" id="bulk-delete-btn" disabled style="background: #e53e3e; color: white;">
                        일괄 삭제
                    </button>
                </div>
            </div>
            <table class="coordinates-table">
                <thead>
                    <tr>
                        <th>
                            <label class="select-all-label">
                                <input type="checkbox" class="coord-checkbox" id="select-all-checkbox" onchange="EditCoordinates.toggleAllCoordinates()">
                                전체 선택
                            </label>
                        </th>
                        <th>번호</th>
                        <th>위도</th>
                        <th>경도</th>
                        <th>편집</th>
                    </tr>
                </thead>
                <tbody>
        `;

        if (EditState.course.coordinates && EditState.course.coordinates.length > 0) {
            EditState.course.coordinates.forEach((coord, coordIdx) => {
                const coordId = `${coordIdx}`;
                const lat = (coord.latitude != null && !isNaN(coord.latitude)) ? Number(coord.latitude).toFixed(6) : 'N/A';
                const lng = (coord.longitude != null && !isNaN(coord.longitude)) ? Number(coord.longitude).toFixed(6) : 'N/A';

                html += `
                    <tr id="coord-row-${coordId}" class="${EditState.selectedCoordinates.has(coordId) ? 'selected' : ''}">
                        <td>
                            <input type="checkbox" class="coord-checkbox"
                                   id="checkbox-${coordId}"
                                   ${EditState.selectedCoordinates.has(coordId) ? 'checked' : ''}
                                   onchange="EditCoordinates.toggleCoordinate(${coordIdx})">
                        </td>
                        <td class="coord-number">#${coordIdx + 1}</td>
                        <td class="coord-value">${lat}</td>
                        <td class="coord-value">${lng}</td>
                        <td>
                            <button class="coord-edit-btn" onclick="EditModal.openCoordinateModal(${coordIdx})">
                                수정
                            </button>
                        </td>
                    </tr>
                `;
            });
        }

        html += `
                </tbody>
            </table>
        `;

        coordinatesContainer.innerHTML = html;
        this.updateBulkActionButtons();
    },

    /**
     * 일괄 작업 버튼 상태 업데이트
     */
    updateBulkActionButtons() {
        const showOnMapBtn = document.getElementById('show-on-map-btn');
        const matchButton = document.getElementById('match-button');
        const bulkDeleteBtn = document.getElementById('bulk-delete-btn');
        const clearSelectionBtn = document.getElementById('clear-selection-btn');

        const hasSelection = EditState.selectedCoordinates.size > 0;
        const hasTwoOrMore = EditState.selectedCoordinates.size >= 2;

        if (showOnMapBtn) showOnMapBtn.disabled = !hasSelection;
        if (matchButton) matchButton.disabled = !hasTwoOrMore;
        if (bulkDeleteBtn) bulkDeleteBtn.disabled = !hasSelection;

        if (clearSelectionBtn) {
            if (hasSelection) {
                clearSelectionBtn.classList.add('visible');
            } else {
                clearSelectionBtn.classList.remove('visible');
            }
        }
    },

    /**
     * 전체 선택 체크박스 상태 업데이트
     */
    updateSelectAllCheckbox() {
        const selectAllCheckbox = document.getElementById('select-all-checkbox');
        if (!selectAllCheckbox) return;

        const totalCoordinates = EditState.course.coordinates ? EditState.course.coordinates.length : 0;

        if (EditState.selectedCoordinates.size === 0) {
            selectAllCheckbox.checked = false;
            selectAllCheckbox.indeterminate = false;
        } else if (EditState.selectedCoordinates.size === totalCoordinates) {
            selectAllCheckbox.checked = true;
            selectAllCheckbox.indeterminate = false;
        } else {
            selectAllCheckbox.checked = false;
            selectAllCheckbox.indeterminate = true;
        }
    },

    /**
     * 선택된 좌표 지도에 표시
     */
    showSelectedOnMap() {
        if (EditState.selectedCoordinates.size === 0) {
            this.showMessage('선택된 좌표가 없습니다.', 'error');
            return;
        }

        EditState.markers.forEach(item => {
            item.marker.setZIndex(1);
        });

        const selectedBounds = new kakao.maps.LatLngBounds();
        let selectedCount = 0;

        EditState.selectedCoordinates.forEach(coordId => {
            const coordIdx = Number(coordId);
            const markerInfo = EditState.markers.find(m => m.coordIndex === coordIdx);

            if (markerInfo) {
                markerInfo.marker.setZIndex(1000);
                const position = markerInfo.marker.getPosition();
                selectedBounds.extend(position);
                selectedCount++;
            }
        });

        if (selectedCount > 0) {
            EditState.map.setBounds(selectedBounds);
            this.showMessage(`${selectedCount}개의 선택된 좌표가 강조되었습니다.`, 'success');
        }
    },

    /**
     * 저장 버튼 상태 업데이트
     */
    updateSaveButton() {
        const saveButton = document.querySelector('.save-button');
        if (saveButton) {
            saveButton.disabled = !EditAPI.hasChanges();
        }
    }
};

window.EditUI = EditUI;
