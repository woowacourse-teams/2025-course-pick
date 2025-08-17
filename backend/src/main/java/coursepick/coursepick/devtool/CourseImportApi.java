package coursepick.coursepick.devtool;

import coursepick.coursepick.application.CourseParserService;
import coursepick.coursepick.application.dto.CourseFile;
import coursepick.coursepick.domain.Course;
import coursepick.coursepick.domain.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@Profile("local")
@RequiredArgsConstructor
public class CourseImportApi {

    private final CourseParserService courseParserService;
    private final CourseRepository courseRepository;

    @PostMapping("/import")
    public ResponseEntity<Void> importFiles(@RequestParam("files") List<MultipartFile> files) throws IOException {
        for (MultipartFile file : files) {
            List<Course> courses = courseParserService.parseAndCloseInputStream(CourseFile.from(file));
            courseRepository.saveAll(courses);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/import")
    public String importFiles() {
        return """
                 <!DOCTYPE html>
                <html lang="ko">
                <head>
                    <meta charset="UTF-8"/>
                    <meta name="viewport" content="width=device-width, initial-scale=1"/>
                    <title>코스 파일 업로드</title>
                    <style>
                        :root {
                            --bg: #0b0c0f;
                            --card: #12141a;
                            --text: #e7e9ee;
                            --muted: #9aa3b2;
                            --primary: #6ea8fe;
                            --primary-weak: #1b2a4b;
                            --border: #222733;
                            --danger: #ff6b6b;
                            --ok: #58d68d;
                            --focus: 0 0 0 3px rgba(110, 168, 254, .35);
                        }

                        @media (prefers-color-scheme: light) {
                            :root {
                                --bg: #f6f7fb;
                                --card: #ffffff;
                                --text: #12141a;
                                --muted: #697386;
                                --primary: #2f6feb;
                                --primary-weak: #e7efff;
                                --border: #e6e8ee;
                            }
                        }

                        * {
                            box-sizing: border-box
                        }

                        body {
                            margin: 0;
                            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Inter, Helvetica, Arial, sans-serif;
                            background: var(--bg);
                            color: var(--text);
                            line-height: 1.5;
                        }

                        .wrap {
                            max-width: 760px;
                            margin: 4rem auto;
                            padding: 0 1rem;
                        }

                        .card {
                            background: var(--card);
                            border: 1px solid var(--border);
                            border-radius: 16px;
                            padding: 24px 20px 16px;
                            box-shadow: 0 10px 30px rgba(0, 0, 0, .15);
                        }

                        h1 {
                            font-size: 1.4rem;
                            margin: 0 0 .75rem
                        }

                        p.desc {
                            margin: .25rem 0 1rem;
                            color: var(--muted);
                            font-size: .95rem
                        }

                        .hidden {
                            display: none
                        }

                        .btn {
                            appearance: none;
                            border: 1px solid var(--border);
                            background: var(--primary);
                            color: #fff;
                            padding: 10px 14px;
                            border-radius: 10px;
                            font-weight: 600;
                            cursor: pointer;
                            transition: transform .05s ease;
                        }

                        .btn.secondary {
                            background: transparent;
                            color: var(--text)
                        }

                        .btn:active {
                            transform: translateY(1px)
                        }

                        .btn:focus-visible {
                            outline: none;
                            box-shadow: var(--focus)
                        }

                        .hint {
                            font-size: .85rem;
                            color: var(--muted)
                        }

                        .dropzone {
                            border: 2px dashed var(--border);
                            border-radius: 14px;
                            padding: 28px;
                            text-align: center;
                            background: linear-gradient(180deg, transparent, rgba(110, 168, 254, .05));
                            transition: border-color .15s ease, background-color .15s ease;
                        }

                        .file-list {
                            margin: 12px 0 6px;
                            border-top: 1px solid var(--border)
                        }

                        .file-item {
                            display: flex;
                            justify-content: space-between;
                            align-items: center;
                            padding: 10px 0;
                            border-bottom: 1px solid var(--border)
                        }

                        .file-name {
                            overflow: hidden;
                            text-overflow: ellipsis;
                            white-space: nowrap;
                            max-width: 70%
                        }

                        .file-meta {
                            font-size: .85rem;
                            color: var(--muted)
                        }

                        .remove {
                            background: transparent;
                            border: 0;
                            color: var(--danger);
                            cursor: pointer
                        }

                        .progress {
                            height: 10px;
                            background: #0d1117;
                            border: 1px solid var(--border);
                            border-radius: 999px;
                            overflow: hidden;
                            margin: 8px 0 0;
                        }

                        .bar {
                            height: 100%;
                            width: 0;
                            background: var(--primary);
                            transition: width .1s linear
                        }

                        .footer {
                            display: flex;
                            justify-content: space-between;
                            align-items: center;
                            gap: 12px;
                            margin-top: 16px
                        }

                        .badge {
                            font-size: .8rem;
                            padding: 4px 8px;
                            border-radius: 999px;
                            border: 1px solid var(--border);
                            color: var(--muted)
                        }

                        .warn {
                            color: var(--danger)
                        }

                        .ok {
                            color: var(--ok)
                        }

                        .modal-backdrop {
                            position: fixed;
                            inset: 0;
                            background: rgba(0, 0, 0, .5);
                            display: none;
                            align-items: center;
                            justify-content: center;
                            z-index: 1000;
                        }

                        .modal-backdrop.show {
                            display: flex;
                        }

                        .modal {
                            background: var(--card);
                            border: 1px solid var(--border);
                            border-radius: 14px;
                            padding: 20px;
                            max-width: 420px;
                            width: calc(100% - 32px);
                            box-shadow: 0 10px 30px rgba(0, 0, 0, .45);
                            text-align: center;
                        }

                        .modal h2 {
                            margin: 0 0 8px;
                            font-size: 1.1rem;
                        }

                        .modal p {
                            margin: 0 0 16px;
                            color: var(--muted);
                        }

                        .modal .btn-row {
                            display: flex;
                            gap: 8px;
                            justify-content: center;
                        }

                        .modal.ok h2 {
                            color: var(--ok);
                        }

                        .modal.warn h2 {
                            color: var(--danger);
                        }
                    </style>
                </head>
                <body>
                <div class="wrap">
                    <div class="card" role="region" aria-labelledby="title">
                        <h1 id="title">코스 파일 업로드</h1>
                        <p class="desc">GPX/KML 지원. 여러 파일을 한 번에 업로드할 수 있습니다. 드래그&드롭 또는 파일 선택을 이용하세요.</p>

                        <form id="uploadForm" action="/import" method="post" enctype="multipart/form-data" novalidate>
                            <div class="dropzone" id="dropzone" tabindex="0" aria-label="드래그 앤 드롭 영역">
                                <p style="margin:0 0 8px"><strong>여기로 파일을 끌어다 놓으세요</strong></p>
                                <p class="hint" style="margin:0 0 12px">또는</p>
                                <label for="files" class="btn secondary">파일 선택</label>
                                <input class="hidden" type="file" id="files" name="files" multiple
                                       accept=".gpx,.kml,application/gpx+xml,"/>
                            </div>

                            <div id="fileList" class="file-list" aria-live="polite"></div>

                            <div class="footer">
                                <div>
                                    <span id="fileCount" class="badge">선택된 파일: 0</span>
                                    <span id="totalSize" class="badge">총 용량: 0 B</span>
                                </div>
                                <div>
                                    <button id="resetBtn" type="button" class="btn secondary">초기화</button>
                                    <button id="submitBtn" type="submit" class="btn" disabled>업로드</button>
                                </div>
                            </div>

                            <div class="progress" aria-hidden="true">
                                <div id="progressBar" class="bar"></div>
                            </div>
                            <div id="status" class="hint" style="margin-top:6px"></div>
                        </form>
                        <div id="modal" class="modal-backdrop" role="dialog" aria-modal="true" aria-hidden="true">
                            <div id="modalBox" class="modal" role="document">
                                <h2 id="modalTitle"></h2>
                                <p id="modalMsg"></p>
                                <div class="btn-row">
                                    <button id="modalClose" type="button" class="btn secondary">닫기</button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <script>
                    (function () {
                        const form = document.getElementById('uploadForm');
                        const dz = document.getElementById('dropzone');
                        const input = document.getElementById('files');
                        const fileListEl = document.getElementById('fileList');
                        const fileCountEl = document.getElementById('fileCount');
                        const totalSizeEl = document.getElementById('totalSize');
                        const submitBtn = document.getElementById('submitBtn');
                        const resetBtn = document.getElementById('resetBtn');
                        const progressBar = document.getElementById('progressBar');
                        const statusEl = document.getElementById('status');
                        const modal = document.getElementById('modal');
                        const modalBox = document.getElementById('modalBox');
                        const modalTitle = document.getElementById('modalTitle');
                        const modalMsg = document.getElementById('modalMsg');
                        const modalClose = document.getElementById('modalClose');
                        const ALLOWED_EXT = ['gpx', 'kml'];
                        const MAX_TOTAL_BYTES = 1024 * 1024 * 200; // 200MB 총합 제한 (원하시면 조정)

                        let files = [];

                        function openModal(title, msg, variant /* 'ok' | 'warn' */) {
                            modalTitle.textContent = title;
                            modalMsg.textContent = msg;
                            modalBox.classList.remove('ok', 'warn');
                            if (variant) modalBox.classList.add(variant);
                            modal.classList.add('show');
                            modal.setAttribute('aria-hidden', 'false');
                            modalClose.focus();
                        }

                        function closeModal() {
                            modal.classList.remove('show');
                            modal.setAttribute('aria-hidden', 'true');
                            modalBox.classList.remove('ok', 'warn');
                        }

                        function formatBytes(n) {
                            const u = ['B', 'KB', 'MB', 'GB', 'TB'];
                            let i = 0;
                            let v = n;
                            while (v >= 1024 && i < u.length - 1) {
                                v /= 1024;
                                i++;
                            }
                            return `${v.toFixed(v < 10 && i > 0 ? 1 : 0)} ${u[i]}`;
                        }

                        function extOf(name) {
                            const i = name.lastIndexOf('.');
                            return i >= 0 ? name.slice(i + 1).toLowerCase() : '';
                        }

                        function refresh() {
                            // 제한/정보 갱신
                            const total = files.reduce((a, f) => a + f.size, 0);
                            const invalid = files.filter(f => !ALLOWED_EXT.includes(extOf(f.name)));
                            const tooBig = total > MAX_TOTAL_BYTES;

                            // 파일 목록 렌더
                            fileListEl.innerHTML = files.map((f, idx) => `
                                      <div class="file-item">
                                        <div class="file-name" title="${f.name}">${f.name}</div>
                                        <div class="file-meta">${formatBytes(f.size)} · <button class="remove" data-idx="${idx}" aria-label="삭제">삭제</button></div>
                                      </div>
                                    `).join('');

                            // 카운터/용량
                            fileCountEl.textContent = `선택된 파일: ${files.length}`;
                            totalSizeEl.textContent = `총 용량: ${formatBytes(total)}`;

                            // 제출 가능 여부
                            submitBtn.disabled = files.length === 0 || invalid.length > 0 || tooBig;

                            // 상태 메시지
                            if (invalid.length > 0) {
                                statusEl.innerHTML = `허용되지 않는 확장자가 포함되어 있습니다. <span class="warn">(.gpx, .kml 만 가능)</span>`;
                            } else if (tooBig) {
                                statusEl.innerHTML = `총 용량이 너무 큽니다. <span class="warn">최대 ${formatBytes(MAX_TOTAL_BYTES)}</span>`;
                            } else if (files.length > 0) {
                                statusEl.innerHTML = `<span class="ok">업로드 준비 완료</span>`;
                            } else {
                                statusEl.textContent = '';
                            }
                        }

                        function addFiles(fs) {
                            // FileList → 배열
                            const arr = Array.from(fs || []);
                            // 같은 이름의 파일이 있으면 뒤에 (2) 같은 중복 방지 로직을 둘 수도 있지만,
                            // 여기서는 단순히 뒤에 추가만 합니다.
                            files = files.concat(arr);
                            refresh();
                        }

                        fileListEl.addEventListener('click', (e) => {
                            if (e.target.matches('.remove')) {
                                const idx = Number(e.target.getAttribute('data-idx'));
                                files.splice(idx, 1);
                                refresh();
                            }
                        });

                        // 드래그&드롭
                        ['dragenter', 'dragover'].forEach(evt => {
                            dz.addEventListener(evt, (e) => {
                                e.preventDefault();
                                e.stopPropagation();
                                dz.classList.add('dragover');
                            });
                        });
                        ['dragleave', 'drop'].forEach(evt => {
                            dz.addEventListener(evt, (e) => {
                                e.preventDefault();
                                e.stopPropagation();
                                dz.classList.remove('dragover');
                            });
                        });
                        dz.addEventListener('drop', (e) => {
                            if (e.dataTransfer && e.dataTransfer.files) {
                                addFiles(e.dataTransfer.files);
                            }
                        });

                        // 파일 선택
                        input.addEventListener('change', (e) => addFiles(e.target.files));

                        // 초기화
                        resetBtn.addEventListener('click', () => {
                            files = [];
                            input.value = '';
                            progressBar.style.width = '0%';
                            statusEl.textContent = '';
                            refresh();
                        });

                        // 폼 제출(XHR로 진행률 표시). 서버는 기존 /import (multipart/form-data) 그대로 사용.
                        form.addEventListener('submit', (e) => {
                            e.preventDefault();
                            if (submitBtn.disabled) return;

                            const fd = new FormData();
                            files.forEach(f => fd.append('files', f, f.name));

                            progressBar.style.width = '0%';
                            statusEl.textContent = '업로드 중...';

                            const xhr = new XMLHttpRequest();
                            xhr.open('POST', form.getAttribute('action'));
                            xhr.upload.onprogress = (ev) => {
                                if (ev.lengthComputable) {
                                    const pct = (ev.loaded / ev.total) * 100;
                                    progressBar.style.width = pct.toFixed(0) + '%';
                                }
                            };
                            xhr.onload = () => {
                                if (xhr.status >= 200 && xhr.status < 300) {
                                    progressBar.style.width = '100%';
                                    openModal('업로드 성공', `총 ${files.length}개 파일이 처리되었습니다.`, 'ok');
                                } else {
                                    openModal('업로드 실패', `서버 응답: ${xhr.status} ${xhr.statusText}`, 'warn');
                                }
                            };
                            xhr.onerror = () => {
                                openModal('업로드 실패', '네트워크 오류가 발생했습니다.', 'warn');
                            };
                            xhr.send(fd);
                        });

                        modalClose.addEventListener('click', closeModal);
                        modal.addEventListener('click', (e) => {
                            if (e.target === modal) closeModal();
                        });
                        document.addEventListener('keydown', (e) => {
                            if (e.key === 'Escape' && modal.classList.contains('show')) closeModal();
                        });

                        // 초기 렌더
                        refresh();
                    })();
                </script>
                </body>
                </html>
                """;
    }
}
