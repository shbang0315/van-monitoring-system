<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { Client } from '@stomp/stompjs';
import { useRouter } from 'vue-router';
import MonitoringTable from '../components/MonitoringTable.vue';

const router = useRouter();
let stompClient = null;

// --- 데이터 변수 ---
const allLogs = ref([]);
const successLogs = ref([]);
const failLogs = ref([]);
const cancelLogs = ref([]);
const highAmountLogs = ref([]);
const itmxCrdtLogs = ref([]);
const itmxPontLogs = ref([]);
const itmxCashLogs = ref([]);
const itmxCrdtRespLogs = ref([]);
const itmxPontRespLogs = ref([]);
const itmxCashRespLogs = ref([]);

// --- [NEW] 구역별 컬럼 정의 (Columns) ---
// 1. INST_CODE 별 거래 건수(신용)
const itmxCrdtCols = [
  { label: '거래시간', key: 'lastTime', type: 'time', width: '25%' },
  { label: '기관(INST_CODE)', key: 'instCode', width: '30%' },
  { label: '건수', key: 'count', align: 'right', width: '20%' },
  { label: '거절률', key: 'rejectRate', align: 'right', width: '25%' }
];

// 2. INST_CODE 별 거래 건수(포인트)
const itmxPontCols = [
  { label: '거래시간', key: 'lastTime', type: 'time', width: '25%' },
  { label: '기관(INST_CODE)', key: 'instCode', width: '30%' },
  { label: '건수', key: 'count', align: 'right', width: '20%' },
  { label: '거절률', key: 'rejectRate', align: 'right', width: '25%' }
];

// 3. INST_CODE 별 거래 건수(현금영수증)
const itmxCashCols = [
  { label: '거래시간', key: 'lastTime', type: 'time', width: '25%' },
  { label: '기관(INST_CODE)', key: 'instCode', width: '30%' },
  { label: '건수', key: 'count', align: 'right', width: '20%' },
  { label: '거절률', key: 'rejectRate', align: 'right', width: '25%' }
];

// 4. 응답코드별 건수(신용)
const itmxCrdtRespCols = [
  { label: '발생시간', key: 'lastTime', type: 'time', width: '30%' },
  { label: '응답코드', key: 'trxRespCd', width: '25%' },
  { label: '건수', key: 'count', align: 'right', width: '25%' }
];

// 5. 응답코드별 건수(포인트)
const itmxPontRespCols = [
  { label: '발생시간', key: 'lastTime', type: 'time', width: '30%' },
  { label: '응답코드', key: 'trxRespCd', width: '25%' },
  { label: '건수', key: 'count', align: 'right', width: '25%' }
];

// 6. 응답코드별 건수(현금)
const itmxCashRespCols = [
  { label: '발생시간', key: 'lastTime', type: 'time', width: '30%' },
  { label: '응답코드', key: 'trxRespCd', width: '25%' },
  { label: '건수', key: 'count', align: 'right', width: '25%' }
];

// 2. 전체 거래 로그 (기본)
const allLogsCols = [
  { label: '시간', key: 'transactionTime', type: 'time', width: '22%' },
  { label: '가맹점명', key: 'storeName', width: '38%' },
  { label: '금액', key: 'amount', type: 'money', align: 'right', width: '25%' },
  { label: '상태', key: 'status', type: 'status', align: 'center', width: '15%' }
];

// 3. 승인 성공 (상태 컬럼 생략, 가맹점 강조)
const successCols = [
  { label: '승인시간', key: 'transactionTime', type: 'time', width: '25%' },
  { label: '가맹점 (정상승인)', key: 'storeName', width: '45%', highlight: true }, 
  { label: '매출액', key: 'amount', type: 'money', align: 'right', width: '30%' }
];

// 4. 오류/실패 (에러 집중)
const failCols = [
  { label: '발생시간', key: 'transactionTime', type: 'time', width: '25%' },
  { label: '가맹점', key: 'storeName', width: '30%' },
  { label: '요청액', key: 'amount', type: 'money', align: 'right', width: '25%' },
  { label: '경고', key: 'status', type: 'status', align: 'center', width: '20%' }
];

// 5. 취소 거래 (단순화)
const cancelCols = [
  { label: '취소시간', key: 'transactionTime', type: 'time', width: '25%' },
  { label: '취소 가맹점', key: 'storeName', width: '40%' },
  { label: '취소금액', key: 'amount', type: 'money', align: 'right', width: '35%' }
];

// 6. 고액 결제 (금액 강조)
const highAmountCols = [
  { label: '시간', key: 'transactionTime', type: 'time', width: '20%' },
  { label: 'VIP 가맹점', key: 'storeName', width: '30%' },
  { label: '고액 매출', key: 'amount', type: 'money', align: 'right', width: '50%', highlight: true }
];

// --- WebSocket 연결 ---
const connectWebSocket = () => {
  stompClient = new Client({
    // [주의] 실제 운영 시 이 주소는 환경 변수나 설정에서 가져오도록 변경 권장
    brokerURL: 'ws://localhost:8081/ws-monitoring/websocket',
    onConnect: () => {
      // 6개 채널 구독
      stompClient.subscribe('/topic/van/itmx/crdt', (msg) => { itmxCrdtLogs.value = JSON.parse(msg.body); });
      stompClient.subscribe('/topic/van/itmx/pont', (msg) => { itmxPontLogs.value = JSON.parse(msg.body); });
      stompClient.subscribe('/topic/van/itmx/cash', (msg) => { itmxCashLogs.value = JSON.parse(msg.body); });
      stompClient.subscribe('/topic/van/itmx/crdtResp', (msg) => { itmxCrdtRespLogs.value = JSON.parse(msg.body); });
      stompClient.subscribe('/topic/van/itmx/pontResp', (msg) => { itmxPontRespLogs.value = JSON.parse(msg.body); });
      stompClient.subscribe('/topic/van/itmx/cashResp', (msg) => { itmxCashRespLogs.value = JSON.parse(msg.body); });
      // stompClient.subscribe('/topic/van/all', (msg) => { allLogs.value = JSON.parse(msg.body); });
      // stompClient.subscribe('/topic/van/success', (msg) => { successLogs.value = JSON.parse(msg.body); });
      // stompClient.subscribe('/topic/van/fail', (msg) => { failLogs.value = JSON.parse(msg.body); });
      // stompClient.subscribe('/topic/van/cancel', (msg) => { cancelLogs.value = JSON.parse(msg.body); });
      // stompClient.subscribe('/topic/van/high', (msg) => { highAmountLogs.value = JSON.parse(msg.body); });
    }
    
  });
  stompClient.activate();
};

onMounted(() => connectWebSocket());
onUnmounted(() => stompClient && stompClient.deactivate());
</script>

<template>
  <div class="dashboard-wrapper">
    <header>
      <div class="left">
        <button @click="router.push('/')" class="home-btn">🏠 Home</button>
        <h2>VAN 통합 관제</h2>
      </div>
      <div class="right">
        <span class="live-badge"><span class="live-dot"></span>LIVE SYSTEM</span>
        <span class="clock">{{ new Date().toLocaleTimeString() }}</span>
      </div>
    </header>

    <div class="grid-3x2">

      <MonitoringTable 
        title="🏢 INST_CODE 별 거래 건수(신용)" 
        :data="itmxCrdtLogs" 
        :columns="itmxCrdtCols" 
        type="default" 
      />

      <MonitoringTable 
        title="📜 INST_CODE 별 거래 건수(포인트)" 
        :data="itmxPontLogs" 
        :columns="itmxPontCols" 
        type="default" 
      />
      
      <MonitoringTable 
        title="✅ INST_CODE 별 거래 건수(현금)" 
        :data="itmxCashLogs" 
        :columns="itmxCashCols" 
        type="default" 
      />
      
      <MonitoringTable 
        title="🚨 응답코드별 건수(신용)" 
        :data="itmxCrdtRespLogs" 
        :columns="itmxCrdtRespCols" 
        type="danger" 
      />

      <MonitoringTable 
        title="🚨 응답코드별 건수(포인트)" 
        :data="itmxPontRespLogs" 
        :columns="itmxPontRespCols" 
        type="danger" 
      />

      <MonitoringTable 
        title="🚨 응답코드별 건수(현금)" 
        :data="itmxCashRespLogs" 
        :columns="itmxCashRespCols" 
        type="danger" 
      />

      <!--<MonitoringTable 
        title="↩️ 취소 거래" 
        :data="cancelLogs" 
        :columns="cancelCols" 
        type="warning" 
      />
      
      <MonitoringTable 
        title="💰 고액 결제 (10만↑)" 
        :data="highAmountLogs" 
        :columns="highAmountCols" 
        type="warning" 
      /> -->

    </div>
  </div>
</template>

<style scoped>
.dashboard-wrapper {
  background-color: var(--bg-primary); /* 메인 배경색 적용 */
  height: 100vh;
  display: flex;
  flex-direction: column;
}

header {
  background-color: var(--bg-secondary); /* 헤더 배경색 */
  padding: 0 25px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 64px;
  border-bottom: 1px solid var(--border-color);
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}

.left { display: flex; align-items: center; gap: 15px; }
header h2 { margin: 0; font-size: 1.25rem; font-weight: 700; color: var(--text-primary); letter-spacing: -0.5px; }

.home-btn {
  background: var(--bg-tertiary);
  border: 1px solid var(--border-color);
  color: var(--text-secondary);
  cursor: pointer; padding: 8px 12px; border-radius: 8px;
  display: flex; align-items: center; gap: 8px; font-weight: 500; font-size: 0.9rem;
  transition: all 0.2s;
}
.home-btn:hover { background: var(--border-color); color: var(--text-primary); }

.right { display: flex; align-items: center; gap: 20px; }
.live-badge {
  background-color: rgba(16, 185, 129, 0.1); /* 은은한 초록 배경 */
  color: var(--color-success);
  padding: 6px 12px; border-radius: 20px;
  font-weight: 600; font-size: 0.8rem;
  display: flex; align-items: center; gap: 6px;
}
.live-dot { display: inline-block; width: 8px; height: 8px; background-color: var(--color-success); border-radius: 50%; animation: pulse 2s infinite; }
.clock { font-family: 'Roboto Mono', monospace; font-size: 1rem; color: var(--text-secondary); font-weight: 500; }

.grid-3x2 {
  flex: 1;
  padding: 20px;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  grid-template-rows: repeat(2, 1fr);
  gap: 20px;
  overflow: hidden;
}

@keyframes pulse {
  0% { box-shadow: 0 0 0 0 rgba(16, 185, 129, 0.4); }
  70% { box-shadow: 0 0 0 10px rgba(16, 185, 129, 0); }
  100% { box-shadow: 0 0 0 0 rgba(16, 185, 129, 0); }
}
</style>