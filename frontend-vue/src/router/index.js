import { createRouter, createWebHistory } from 'vue-router';
import HomeView from '../views/HomeView.vue';
import VanDashboard from '../views/VanDashboard.vue';
import LoginView from '../views/LoginView.vue'; // 추가

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/login', name: 'login', component: LoginView }, // 로그인 라우트 추가
    { path: '/', name: 'home', component: HomeView },
    { path: '/van', name: 'van', component: VanDashboard }
  ]
});

// [네비게이션 가드] 페이지 이동 전 검사
router.beforeEach((to, from, next) => {
  // 토큰이 있는지 확인
  const isAuthenticated = localStorage.getItem('accessToken');

  // 1. 로그인이 필요한 페이지인데 토큰이 없다면 -> 로그인 페이지로 팅겨냄
  if (to.name !== 'login' && !isAuthenticated) {
    next({ name: 'login' });
  } 
  // 2. 이미 로그인했는데 로그인 페이지로 가려 하면 -> 메인으로 보냄
  else if (to.name === 'login' && isAuthenticated) {
    next({ name: 'home' });
  } 
  // 3. 통과
  else {
    next();
  }
});

export default router;