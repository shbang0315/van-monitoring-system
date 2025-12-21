import { createApp } from 'vue'
import './assets/main.css' // <--- 이 줄을 추가하세요!
// import './style.css' <-- 기존 style.css는 삭제하거나 주석 처리
import App from './App.vue'
import router from './router'

const app = createApp(App)
app.use(router)
app.mount('#app')