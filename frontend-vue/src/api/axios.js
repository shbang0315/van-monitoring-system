import axios from 'axios';

const baseURL = import.meta.env.VITE_API_URL;

// 1. Axios 인스턴스 생성
const instance = axios.create({
  baseURL: baseURL,
  timeout: 5000,
});

// 2. 요청 인터셉터: 모든 요청 헤더에 AccessToken 실어 보내기
instance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// 3. 응답 인터셉터: 401 에러 발생 시 토큰 재발급 시도
instance.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    // 401 에러이고, 아직 재시도를 안 했다면 (_retry 플래그로 무한루프 방지)
    if (error.response.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        const refreshToken = localStorage.getItem('refreshToken');
        
        // Refresh Token으로 재발급 요청
        const { data } = await axios.post(`${baseURL}/auth/reissue`, {
          refreshToken: refreshToken
        });

        // 새 AccessToken 저장
        localStorage.setItem('accessToken', data.accessToken);

        // 실패했던 원래 요청의 헤더를 새 토큰으로 교체하고 재요청
        originalRequest.headers['Authorization'] = `Bearer ${data.accessToken}`;
        return instance(originalRequest);

      } catch (refreshError) {
        // Refresh Token도 만료되었거나 유효하지 않음 -> 강제 로그아웃
        console.error('세션 만료, 로그아웃 처리');
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        window.location.href = '/login'; // 로그인 페이지로 리다이렉트
        return Promise.reject(refreshError);
      }
    }
    return Promise.reject(error);
  }
);

export default instance;