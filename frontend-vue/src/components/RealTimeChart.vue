<script setup>
import { onMounted, onUnmounted, ref } from 'vue';
import * as echarts from 'echarts';
import { Client } from '@stomp/stompjs';

// 차트를 그릴 DOM 엘리먼트 참조
const chartRef = ref(null);
let myChart = null;
let stompClient = null;

// 차트 초기화 및 설정
const initChart = () => {
  myChart = echarts.init(chartRef.value);
  const option = {
    title: { text: '실시간 결제 금액 (최근 10건)' },
    tooltip: {},
    xAxis: {
      type: 'category',
      data: [] // 가맹점명 들어갈 곳
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '결제금액',
        type: 'bar',
        data: [], // 금액 들어갈 곳
        itemStyle: { color: '#5470C6' }
      }
    ]
  };
  myChart.setOption(option);
};

// 웹소켓 연결 함수
const connectWebSocket = () => {
  stompClient = new Client({
    // [중요] Gateway(8000)가 아닌 Monitoring Service(8081)에 직접 붙습니다.
    // Gateway 설정 없이 바로 붙는 것이 테스트에 유리합니다.
    brokerURL: 'ws://localhost:8081/ws-monitoring/websocket',
    
    // 연결 성공 시 실행
    onConnect: () => {
      console.log('✅ WebSocket Connected!');
      
      // 구독 설정 (/topic/transactions)
      stompClient.subscribe('/topic/transactions', (message) => {
        const transactions = JSON.parse(message.body);
        updateChart(transactions);
      });
    },
    
    // 에러 발생 시 실행
    onStompError: (frame) => {
      console.error('❌ Broker reported error: ' + frame.headers['message']);
      console.error('Additional details: ' + frame.body);
    }
  });

  stompClient.activate();
};

// 데이터 수신 시 차트 업데이트
const updateChart = (transactions) => {
  // 데이터 가공 (X축: 가맹점명, Y축: 금액)
  // 데이터를 역순(reverse)으로 해서 최신이 오른쪽으로 가게 해도 됨
  const categories = transactions.map(t => t.storeName);
  const data = transactions.map(t => t.amount);

  myChart.setOption({
    xAxis: { data: categories },
    series: [{ data: data }]
  });
  
  console.log(`📊 Chart Updated with ${transactions.length} items`);
};

// 컴포넌트 마운트 시 실행
onMounted(() => {
  initChart();
  connectWebSocket();
  
  // 윈도우 리사이즈 시 차트 크기 조절
  window.addEventListener('resize', () => myChart && myChart.resize());
});

// 컴포넌트 해제 시 연결 종료
onUnmounted(() => {
  if (stompClient) stompClient.deactivate();
  window.removeEventListener('resize', () => myChart && myChart.resize());
});
</script>

<template>
  <div class="chart-container">
    <div ref="chartRef" class="chart"></div>
  </div>
</template>

<style scoped>
.chart-container {
  width: 100%;
  height: 100%;
  padding: 20px;
}
.chart {
  width: 800px; /* 원하는 크기로 조절 */
  height: 500px;
  background-color: #fff;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 10px;
}
</style>