import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import router from './router' // [추가] 라우터 불러오기

const app = createApp(App)

app.use(router) // [추가] 앱에 라우터 장착
app.mount('#app')