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
