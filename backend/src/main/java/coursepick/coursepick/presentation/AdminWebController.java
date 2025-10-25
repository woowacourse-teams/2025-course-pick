package coursepick.coursepick.presentation;

import coursepick.coursepick.application.exception.ErrorType;
import coursepick.coursepick.presentation.dto.AdminLoginWebRequest;
import jakarta.validation.Valid;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.Cookie;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("dev")
public class AdminWebController {

    private static final String TOKEN_COOKIE_KEY = "admin-token";
    private final String adminPassword;

    public AdminWebController(@Value("${admin.token}") String adminPassword) {
        this.adminPassword = adminPassword;
    }

    @PostMapping("/admin/login")
    public ResponseEntity<Void> login(@RequestBody @Valid AdminLoginWebRequest request) {
        if (!adminPassword.equals(request.password())) {
            throw ErrorType.INVALID_ADMIN_PASSWORD.create();
        }
        ResponseCookie tokenCookie = ResponseCookie.from(TOKEN_COOKIE_KEY, adminPassword)
                .httpOnly(true)
                .maxAge(Duration.ofHours(1))
                .sameSite(Cookie.SameSite.STRICT.attributeValue())
                .path("/admin")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, tokenCookie.toString())
                .build();
    }

    @GetMapping("/admin-login")
    public ResponseEntity<String> adminLoginPage() {
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body("""
                        <!DOCTYPE html>
                        <html lang="ko">
                        <head>
                            <meta charset="UTF-8">
                            <meta name="viewport" content="width=device-width, initial-scale=1.0">
                            <title>런세권 관리자 로그인</title>
                            <style>
                        * {
                          margin: 0;
                          padding: 0;
                          box-sizing: border-box;
                        }

                        body {
                          font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', 'Oxygen',
                            'Ubuntu', 'Cantarell', 'Fira Sans', 'Droid Sans', 'Helvetica Neue',
                            sans-serif;
                          -webkit-font-smoothing: antialiased;
                          -moz-osx-font-smoothing: grayscale;
                        }

                        .admin-login-page-container {
                          display: flex;
                          justify-content: center;
                          align-items: center;
                          min-height: 100vh;
                          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                          padding: 20px;
                        }

                        .admin-login-container {
                          padding: 50px 40px;
                          background: white;
                          border-radius: 20px;
                          box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
                          width: 100%;
                          max-width: 450px;
                          text-align: center;
                          animation: slideIn 0.5s ease-out;
                        }

                        @keyframes slideIn {
                          from {
                            opacity: 0;
                            transform: translateY(-30px);
                          }
                          to {
                            opacity: 1;
                            transform: translateY(0);
                          }
                        }

                        .admin-login-title {
                          margin-bottom: 10px;
                          color: #2d3748;
                          font-size: 32px;
                          font-weight: 700;
                        }

                        .admin-login-subtitle {
                          margin-bottom: 30px;
                          color: #718096;
                          font-size: 14px;
                        }

                        .admin-login-form {
                          display: flex;
                          flex-direction: column;
                          gap: 20px;
                        }

                        .admin-login-input {
                          padding: 16px 20px;
                          border-radius: 10px;
                          border: 2px solid #e2e8f0;
                          font-size: 16px;
                          transition: all 0.3s ease;
                          outline: none;
                        }

                        .admin-login-input:focus {
                          border-color: #667eea;
                          box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
                        }

                        .admin-login-input::placeholder {
                          color: #a0aec0;
                        }

                        .admin-login-button {
                          padding: 16px;
                          border: none;
                          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                          color: white;
                          border-radius: 10px;
                          cursor: pointer;
                          font-size: 16px;
                          font-weight: 600;
                          transition: all 0.3s ease;
                          box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
                        }

                        .admin-login-button:hover {
                          transform: translateY(-2px);
                          box-shadow: 0 6px 20px rgba(102, 126, 234, 0.6);
                        }

                        .admin-login-button:active {
                          transform: translateY(0);
                        }

                        .admin-login-button:disabled {
                          opacity: 0.6;
                          cursor: not-allowed;
                          transform: none;
                        }

                        .error-message {
                          margin-top: 15px;
                          padding: 12px;
                          border-radius: 8px;
                          background-color: #fed7d7;
                          color: #c53030;
                          font-size: 14px;
                          display: none;
                          animation: shake 0.5s ease;
                        }

                        .error-message.show {
                          display: block;
                        }

                        @keyframes shake {
                          0%, 100% { transform: translateX(0); }
                          10%, 30%, 50%, 70%, 90% { transform: translateX(-5px); }
                          20%, 40%, 60%, 80% { transform: translateX(5px); }
                        }

                        @media (max-width: 480px) {
                          .admin-login-container {
                            padding: 40px 30px;
                          }

                          .admin-login-title {
                            font-size: 28px;
                          }
                        }
                            </style>
                        </head>
                        <body>
                        <div class="admin-login-page-container">
                            <div class="admin-login-container">
                                <h1 class="admin-login-title">🏃 런세권 관리자 페이지</h1>
                                <p class="admin-login-subtitle">안녕하세요 👋</p>
                                <form class="admin-login-form" id="admin-login-form">
                                    <input
                                            type="password"
                                            id="admin-password"
                                            placeholder="비밀번호를 입력하세요"
                                            class="admin-login-input"
                                            required
                                            autocomplete="current-password">
                                    <button type="submit" class="admin-login-button">로그인</button>
                                </form>
                                <div id="error-message" class="error-message"></div>
                            </div>
                        </div>
                        <script src="https://cdn.jsdelivr.net/npm/axios/dist/axios.min.js"></script>
                        <script>
                        document.addEventListener('DOMContentLoaded', () => {
                            const adminLoginForm = document.getElementById('admin-login-form');
                            const passwordInput = document.getElementById('admin-password');
                            const errorMessage = document.getElementById('error-message');
                            const loginButton = adminLoginForm.querySelector('button[type="submit"]');

                            const showError = (message) => {
                                errorMessage.textContent = message;
                                errorMessage.classList.add('show');
                                setTimeout(() => {
                                    errorMessage.classList.remove('show');
                                }, 5000);
                            };

                            const hideError = () => {
                                errorMessage.classList.remove('show');
                            };

                            adminLoginForm.addEventListener('submit', async (e) => {
                                e.preventDefault();
                                hideError();

                                const password = passwordInput.value.trim();

                                if (!password) {
                                    showError('비밀번호를 입력해주세요.');
                                    return;
                                }

                                loginButton.disabled = true;
                                loginButton.textContent = '로그인 중...';

                                try {
                                    const response = await axios.post('/admin/login', {
                                        password: password
                                    }, {
                                        withCredentials: true
                                    });

                                    window.location.href = '/coming-soon.html';
                                } catch (error) {
                                    console.error('Admin login failed:', error);

                                    if (error.response) {
                                        const status = error.response.status;
                                        if (status === 401) {
                                            showError('비밀번호가 올바르지 않습니다.');
                                        } else if (status === 400) {
                                            showError('잘못된 요청입니다.');
                                        } else {
                                            showError('로그인에 실패했습니다. 다시 시도해주세요.');
                                        }
                                    } else if (error.request) {
                                        showError('서버에 연결할 수 없습니다.');
                                    } else {
                                        showError('로그인 처리 중 오류가 발생했습니다.');
                                    }

                                    passwordInput.value = '';
                                    passwordInput.focus();
                                } finally {
                                    loginButton.disabled = false;
                                    loginButton.textContent = '로그인';
                                }
                            });

                            passwordInput.addEventListener('input', () => {
                                hideError();
                            });

                            passwordInput.focus();
                        });
                        </script>
                        </body>
                        </html>
                        """
                );
    }
}
