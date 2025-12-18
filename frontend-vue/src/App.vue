<script setup>
import RealTimeChart from './components/RealTimeChart.vue'
</script>

<template>
  <div class="app-container">
    <h1>💳 VAN 거래내역 실시간 모니터링</h1>
    <RealTimeChart />
  </div>
</template>

<style>
/* 전체 스타일 리셋 */
body {
  margin: 0;
  background-color: #f0f2f5;
  font-family: 'Arial', sans-serif;
}
.app-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 30px;
}
h1 {
  color: #333;
  margin-bottom: 20px;
}
</style>