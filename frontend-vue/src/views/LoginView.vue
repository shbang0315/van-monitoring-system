<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';

const router = useRouter();

// 입력 데이터
const userId = ref('');
const password = ref('');
const isLoading = ref(false);
const errorMessage = ref('');

// 로그인 함수
const handleLogin = async () => {
  if (!userId.value || !password.value) {
    errorMessage.value = '아이디와 비밀번호를 입력해주세요.';
    return;
  }

  isLoading.value = true;
  errorMessage.value = '';

  try {
    // 1. Gateway를 통해 Auth Service로 요청 (8000번 포트)
    const response = await axios.post('http://localhost:8000/auth/login', {
      userId: userId.value,
      password: password.value
    });

    // 2. 토큰 저장 (Local Storage)
    const token = response.data.accessToken;
    localStorage.setItem('accessToken', token);
    localStorage.setItem('userId', response.data.userId);

    console.log('✅ 로그인 성공! 토큰 저장 완료.');

    // 3. 메인 화면으로 이동
    router.push('/');

  } catch (error) {
    console.error(error);
    if (error.response && error.response.status === 401) {
      errorMessage.value = '아이디 또는 비밀번호가 일치하지 않습니다.';
    } else {
      errorMessage.value = '서버 접속에 실패했습니다. 관리자에게 문의하세요.';
    }
  } finally {
    isLoading.value = false;
  }
};
</script>

<template>
  <div class="login-wrapper">
    <div class="login-card">
      <div class="brand-section">
        <div class="logo-icon">🔒</div>
        <h1>VAN System</h1>
        <p class="subtitle">엔터프라이즈 통합 관제 시스템</p>
      </div>

      <form @submit.prevent="handleLogin" class="form-section">
        <div class="input-group">
          <label for="uid">아이디</label>
          <input 
            id="uid" 
            type="text" 
            v-model="userId" 
            placeholder="Admin ID" 
            autocomplete="off"
          />
        </div>

        <div class="input-group">
          <label for="pwd">비밀번호</label>
          <input 
            id="pwd" 
            type="password" 
            v-model="password" 
            placeholder="Password" 
          />
        </div>

        <div v-if="errorMessage" class="error-msg">
          ⚠️ {{ errorMessage }}
        </div>

        <button type="submit" :disabled="isLoading" class="login-btn">
          <span v-if="isLoading">로그인 중...</span>
          <span v-else>로그인</span>
        </button>
      </form>

      <div class="footer">
        <p>© 2025 VAN Monitoring Corp. All rights reserved.</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 전체 화면 배경 */
.login-wrapper {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: radial-gradient(circle at top right, #1f2937, #111827);
  color: var(--text-primary);
}

/* 로그인 카드 디자인 */
.login-card {
  width: 100%;
  max-width: 420px;
  background-color: var(--bg-secondary);
  padding: 40px;
  border-radius: 16px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.5);
  border: 1px solid var(--border-color);
  display: flex;
  flex-direction: column;
  gap: 30px;
}

/* 브랜드 로고 영역 */
.brand-section { text-align: center; }
.logo-icon { font-size: 3rem; margin-bottom: 10px; }
h1 { margin: 0; font-size: 1.8rem; font-weight: 700; letter-spacing: -0.5px; color: var(--text-primary); }
.subtitle { margin: 5px 0 0; color: var(--text-muted); font-size: 0.9rem; }

/* 폼 영역 */
.form-section { display: flex; flex-direction: column; gap: 20px; }

.input-group { display: flex; flex-direction: column; gap: 8px; }
.input-group label { font-size: 0.85rem; font-weight: 600; color: var(--text-secondary); margin-left: 4px; }
.input-group input {
  background-color: var(--bg-primary);
  border: 1px solid var(--border-color);
  color: var(--text-primary);
  padding: 12px 16px;
  border-radius: 8px;
  font-size: 1rem;
  outline: none;
  transition: all 0.2s;
}
.input-group input:focus {
  border-color: var(--color-info);
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.2); /* 파란색 글로우 */
}

/* 에러 메시지 */
.error-msg {
  background-color: rgba(239, 68, 68, 0.1);
  color: var(--color-danger);
  padding: 10px;
  border-radius: 6px;
  font-size: 0.85rem;
  text-align: center;
  border: 1px solid rgba(239, 68, 68, 0.2);
}

/* 로그인 버튼 */
.login-btn {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: white;
  border: none;
  padding: 14px;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.1s, box-shadow 0.2s;
  margin-top: 10px;
}
.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.4);
}
.login-btn:active { transform: translateY(0); }
.login-btn:disabled { opacity: 0.7; cursor: not-allowed; transform: none; }

/* 푸터 */
.footer { text-align: center; margin-top: 10px; }
.footer p { font-size: 0.75rem; color: var(--text-muted); opacity: 0.6; }
</style>