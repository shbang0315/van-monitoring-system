<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { Client } from '@stomp/stompjs';
import { useRouter } from 'vue-router';
import MonitoringTable from '../components/MonitoringTable.vue';

const router = useRouter();
const transactions = ref([]);
let stompClient = null;

// [웹소켓 연결]
const connectWebSocket = () => {
  stompClient = new Client({
    brokerURL: 'ws://localhost:8081/ws-monitoring/websocket',
    onConnect: () => {
      stompClient.subscribe('/topic/transactions', (msg) => {
        const newData = JSON.parse(msg.body);
        // 최신 50개까지 유지 (테이블이 넓어졌으니 조금 더 많이 보여줘도 됩니다)
        transactions.value = [...newData, ...transactions.value].slice(0, 50);
      });
    }
  });
  stompClient.activate();
};

// --- [데이터 필터링] 핵심 6가지만 남김 ---
const allLogs = computed(() => transactions.value);
const successLogs = computed(() => transactions.value.filter(t => t.status === 'SUCCESS'));
const failLogs = computed(() => transactions.value.filter(t => t.status === 'FAIL'));
const cancelLogs = computed(() => transactions.value.filter(t => t.status === 'CANCEL'));
const highAmountLogs = computed(() => transactions.value.filter(t => t.amount >= 100000));
const gangnamLogs = computed(() => transactions.value.filter(t => t.storeName.includes('강남')));

onMounted(() => connectWebSocket());
onUnmounted(() => stompClient && stompClient.deactivate());
</script>

<template>
  <div class="dashboard-wrapper">
    <header>
      <div class="left">
        <button @click="router.push('/')">🏠</button>
        <h2>VAN 통합 관제 (Wide-View)</h2>
      </div>
      <div class="right">
        <span class="live-badge">● LIVE SYSTEM</span>
        <span class="clock">{{ new Date().toLocaleTimeString() }}</span>
      </div>
    </header>

    <div class="grid-3x2">
      <MonitoringTable title="📜 전체 거래 로그" :data="allLogs" type="default" />
      <MonitoringTable title="✅ 승인 성공" :data="successLogs" type="success" />
      <MonitoringTable title="🚨 오류/실패 감지" :data="failLogs" type="danger" />

      <MonitoringTable title="↩️ 취소 거래" :data="cancelLogs" type="warning" />
      <MonitoringTable title="💰 고액 결제 (10만↑)" :data="highAmountLogs" type="warning" />
      <MonitoringTable title="🏢 VIP점 (강남본점)" :data="gangnamLogs" type="default" />
    </div>
  </div>
</template>

<style scoped>
.dashboard-wrapper {
  background-color: #1e1e2f; /* Dark Theme */
  height: 100vh;
  display: flex;
  flex-direction: column;
  color: #e0e0e0;
}

header {
  background: #27293d;
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #333;
  height: 60px; /* 헤더 높이 살짝 키움 */
}
header h2 { margin: 0; font-size: 1.2rem; color: white; letter-spacing: 1px; }
header button { background: #3c3f58; border: none; color: white; cursor: pointer; padding: 6px 12px; border-radius: 4px; margin-right: 15px; transition: 0.2s; }
header button:hover { background: #505475; }

.live-badge { color: #00b894; font-weight: bold; font-size: 0.85rem; margin-right: 20px; animation: blink 1.5s infinite; }
.clock { font-family: 'Courier New', monospace; font-size: 1.1rem; color: #fff; font-weight: bold; }

/* [핵심] 3x2 그리드 스타일 */
.grid-3x2 {
  flex: 1;
  padding: 15px; /* 여백 확보 */
  display: grid;
  /* 가로를 3등분 (1fr 1fr 1fr) -> 테이블이 훨씬 넓어짐 */
  grid-template-columns: repeat(3, 1fr); 
  grid-template-rows: repeat(2, 1fr);    
  gap: 15px; /* 간격도 살짝 넓힘 */
  overflow: hidden;
}

@keyframes blink { 0% { opacity: 1; } 50% { opacity: 0.3; } 100% { opacity: 1; } }
</style>