<script setup>
import { onMounted, onUnmounted, ref, watch } from 'vue';
import * as echarts from 'echarts';

// 부모로부터 데이터 받기
const props = defineProps(['data']);
const chartRef = ref(null);
let myChart = null;

const initChart = () => {
  myChart = echarts.init(chartRef.value);
  updateChart();
};

const updateChart = () => {
  if (!myChart || !props.data) return;
  
  // 데이터 가공
  const categories = props.data.map(t => t.storeName);
  const seriesData = props.data.map(t => t.amount);

  myChart.setOption({
    title: { text: '📊 최근 결제 금액 추이', left: 'center', textStyle: { fontSize: 16 } },
    tooltip: { trigger: 'axis' },
    grid: { bottom: '10%', top: '20%', left: '10%', right: '5%' },
    xAxis: { type: 'category', data: categories, axisLabel: { interval: 0, rotate: 30 } },
    yAxis: { type: 'value' },
    series: [{ name: '금액', type: 'bar', data: seriesData, itemStyle: { color: '#5470C6' }, barWidth: '40%' }]
  });
};

// 데이터가 바뀔 때마다 차트 갱신
watch(() => props.data, updateChart, { deep: true });

onMounted(() => {
  initChart();
  window.addEventListener('resize', () => myChart?.resize());
});
</script>

<template>
  <div ref="chartRef" class="chart-box"></div>
</template>

<style scoped>
.chart-box { width: 100%; height: 100%; min-height: 250px; }
</style>