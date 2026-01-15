/**
 * OSRM Match 기능 (좌표 보정)
 */

const EditOSRM = {
    /**
     * 선택된 좌표를 OSRM Match로 보정
     */
    async matchSelectedCoordinates() {
        if (EditState.selectedCoordinates.size === 0) {
            EditUI.showMessage('보정할 좌표를 먼저 선택해주세요.', 'error');
            return;
        }

        const coordsToMatch = [];
        const selectedCoordIds = [];

        EditState.course.coordinates.forEach((coord, coordIdx) => {
            const coordId = `${coordIdx}`;
            if (EditState.selectedCoordinates.has(coordId)) {
                coordsToMatch.push({
                    latitude: coord.latitude,
                    longitude: coord.longitude,
                    elevation: coord.elevation
                });
                selectedCoordIds.push(coordIdx);
            }
        });

        if (coordsToMatch.length < 2) {
            EditUI.showMessage('최소 2개 이상의 좌표를 선택해주세요.', 'error');
            return;
        }

        const matchButton = document.getElementById('match-button');
        const originalText = matchButton.textContent;
        matchButton.disabled = true;
        matchButton.textContent = '보정 중...';

        try {
            const response = await axios.post('/admin/api/coordinates/snap', {
                coordinates: coordsToMatch
            }, {
                headers: { 'Content-Type': 'application/json' },
                withCredentials: true
            });

            EditState.matchedCoordinatesData = {
                original: coordsToMatch,
                matched: response.data.matchedCoordinates,
                selectedCoordIds: selectedCoordIds
            };

            this.showMatchPreview();

        } catch (error) {
            if (error.response && error.response.status === 401) {
                alert('로그인 세션이 만료되었습니다.');
                window.location.href = '/admin/login';
            } else {
                const errorMessage = error.response?.data?.message || error.message || '알 수 없는 오류';
                EditUI.showMessage('좌표 보정에 실패했습니다: ' + errorMessage, 'error');
            }
        } finally {
            matchButton.disabled = false;
            matchButton.textContent = originalText;
        }
    },

    /**
     * 보정 미리보기 모달 표시
     */
    showMatchPreview() {
        const modal = document.getElementById('match-preview-modal');
        modal.classList.add('active');

        document.getElementById('original-count').textContent = EditState.matchedCoordinatesData.original.length;
        document.getElementById('matched-count').textContent = EditState.matchedCoordinatesData.matched.length;

        setTimeout(() => {
            this.initMatchPreviewMap();
        }, 100);
    },

    /**
     * 미리보기 지도 초기화
     */
    initMatchPreviewMap() {
        const container = document.getElementById('match-preview-map');
        container.innerHTML = '';

        const options = {
            center: new kakao.maps.LatLng(
                EditState.matchedCoordinatesData.original[0].latitude,
                EditState.matchedCoordinatesData.original[0].longitude
            ),
            level: 3
        };

        EditState.matchPreviewMap = new kakao.maps.Map(container, options);

        // 원본 좌표 (빨간색)
        const originalPath = EditState.matchedCoordinatesData.original.map(coord =>
            new kakao.maps.LatLng(coord.latitude, coord.longitude)
        );
        const originalPolyline = new kakao.maps.Polyline({
            path: originalPath,
            strokeWeight: 5,
            strokeColor: '#FF0000',
            strokeOpacity: 0.7,
            strokeStyle: 'solid'
        });
        originalPolyline.setMap(EditState.matchPreviewMap);

        // 보정된 좌표 (파란색)
        const matchedPath = EditState.matchedCoordinatesData.matched.map(coord =>
            new kakao.maps.LatLng(coord.latitude, coord.longitude)
        );
        const matchedPolyline = new kakao.maps.Polyline({
            path: matchedPath,
            strokeWeight: 5,
            strokeColor: '#0000FF',
            strokeOpacity: 0.7,
            strokeStyle: 'solid'
        });
        matchedPolyline.setMap(EditState.matchPreviewMap);

        // 시작점 마커
        new kakao.maps.Marker({
            position: originalPath[0],
            map: EditState.matchPreviewMap
        });

        // 종료점 마커
        new kakao.maps.Marker({
            position: originalPath[originalPath.length - 1],
            map: EditState.matchPreviewMap
        });

        // 지도 범위 조정
        const bounds = new kakao.maps.LatLngBounds();
        [...originalPath, ...matchedPath].forEach(point => bounds.extend(point));
        EditState.matchPreviewMap.setBounds(bounds);
    },

    /**
     * 보정 미리보기 모달 닫기
     */
    closeMatchPreview() {
        const modal = document.getElementById('match-preview-modal');
        modal.classList.remove('active');
        EditState.matchedCoordinatesData = null;
        EditState.matchPreviewMap = null;
    },

    /**
     * 보정된 좌표 적용
     */
    applyMatchedCoordinates() {
        if (!EditState.matchedCoordinatesData) {
            EditUI.showMessage('적용할 보정 데이터가 없습니다.', 'error');
            return;
        }

        const selectedCoordIds = EditState.matchedCoordinatesData.selectedCoordIds;
        const matched = EditState.matchedCoordinatesData.matched;

        if (selectedCoordIds.length < 2) {
            EditUI.showMessage('최소 2개 이상의 좌표를 선택해야 합니다.', 'error');
            return;
        }

        const firstIdx = selectedCoordIds[0];
        const lastIdx = selectedCoordIds[selectedCoordIds.length - 1];

        const deleteCount = lastIdx - firstIdx + 1;
        EditState.course.coordinates.splice(firstIdx, deleteCount, ...matched);

        EditMap.drawCourseOnMap();
        EditUI.displayCoordinatesList();
        EditUI.updateSaveButton();

        EditUI.showMessage(`${matched.length}개의 좌표로 보정되었습니다.`, 'success');
        this.closeMatchPreview();

        EditState.selectedCoordinates.clear();
        EditUI.updateBulkActionButtons();
    },

    /**
     * OSRM 모달 이벤트 리스너 초기화
     */
    initOSRMEvents() {
        // 모달 외부 클릭 시 닫기
        document.getElementById('match-preview-modal').addEventListener('click', function (e) {
            if (e.target === this) {
                EditOSRM.closeMatchPreview();
            }
        });
    }
};

window.EditOSRM = EditOSRM;
