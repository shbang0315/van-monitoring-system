<script setup>
import { computed } from 'vue';

const props = defineProps({
  title: { type: String, default: '모니터링' },
  data: { type: Array, default: () => [] },
  type: { type: String, default: 'default' }, // default, danger, warning, success
  // [NEW] 컬럼 설정 (기본값 정의)
  columns: { 
    type: Array, 
    default: () => [
      { label: '시간', key: 'transactionTime', type: 'time', width: '25%' },
      { label: '내용', key: 'storeName', width: '50%' },
      { label: '상태', key: 'status', type: 'status', width: '25%' }
    ] 
  }
});

// 패널 헤더 색상 클래스
const headerAccentClass = computed(() => {
  switch (props.type) {
    case 'danger': return 'accent-danger';
    case 'warning': return 'accent-warning';
    case 'success': return 'accent-success';
    default: return 'accent-default';
  }
});

// [NEW] 데이터 포맷팅 함수 (시간, 금액 등)
const formatValue = (item, col) => {
  const value = item[col.key];
  
  if (value === undefined || value === null) return '-';

  if (col.type === 'time') {
    // 시:분:초 형식 변환
    return new Date(value).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', second: '2-digit' });
  }
  if (col.type === 'money') {
    // 천단위 콤마
    return Number(value).toLocaleString();
  }
  return value;
};
</script>

<template>
  <div class="table-panel">
    <div class="panel-header" :class="headerAccentClass">
      <h4>
        <span class="title-text">{{ title }}</span>
        <span class="count-badge">{{ data.length }}</span>
      </h4>
    </div>

    <div class="table-container">
      <table>
        <thead>
          <tr>
            <th 
              v-for="col in columns" 
              :key="col.key" 
              :style="{ width: col.width, textAlign: col.align || 'left' }"
            >
              {{ col.label }}
            </th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(item, index) in data" :key="item.id || index" class="data-row">
            <td 
              v-for="col in columns" 
              :key="col.key"
              :style="{ textAlign: col.align || 'left' }"
            >
              <span v-if="col.type === 'status'" :class="['status-dot', item[col.key]]"></span>
              
              <span v-else :class="{ 'highlight-text': col.highlight, 'amount-font': col.type === 'money' }">
                {{ formatValue(item, col) }}
              </span>
            </td>
          </tr>

          <tr v-if="data.length === 0">
            <td :colspan="columns.length" class="no-data">데이터가 없습니다.</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<style scoped>
.table-panel {
  height: 100%;
  display: flex;
  flex-direction: column;
  background-color: var(--bg-secondary);
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
  border: 1px solid var(--border-color);
}

.panel-header {
  padding: 12px 16px;
  background-color: rgba(255, 255, 255, 0.03);
  border-bottom: 1px solid var(--border-color);
  display: flex;
  align-items: center;
}

/* 왼쪽 포인트 라인 */
.accent-default { border-left: 4px solid var(--text-muted); }
.accent-danger { border-left: 4px solid var(--color-danger); }
.accent-warning { border-left: 4px solid var(--color-warning); }
.accent-success { border-left: 4px solid var(--color-success); }

.panel-header h4 { margin: 0; font-size: 1rem; font-weight: 600; width: 100%; display: flex; justify-content: space-between; color: var(--text-primary); }
.count-badge { background-color: var(--bg-tertiary); padding: 2px 8px; border-radius: 10px; font-size: 0.8rem; color: var(--text-secondary); }

.table-container { flex: 1; overflow-y: auto; }
table { width: 100%; border-collapse: collapse; font-size: 0.9rem; table-layout: fixed; }

th {
  position: sticky; top: 0; z-index: 1;
  background-color: var(--bg-tertiary);
  color: var(--text-secondary);
  font-weight: 600; font-size: 0.8rem;
  padding: 10px 12px;
}

td {
  padding: 10px 12px;
  border-bottom: 1px solid var(--border-color);
  color: var(--text-secondary);
  white-space: nowrap; overflow: hidden; text-overflow: ellipsis;
}
.data-row:hover td { background-color: rgba(255, 255, 255, 0.05); color: var(--text-primary); }

.no-data { text-align: center; padding: 30px; color: var(--text-muted); font-style: italic; }

/* 특수 스타일 */
.amount-font { font-family: 'Roboto Mono', monospace; font-weight: 700; color: var(--text-primary); }
.highlight-text { color: var(--text-primary); font-weight: 700; } /* 강조 텍스트 */

/* 상태 점 */
.status-dot { display: inline-block; width: 10px; height: 10px; border-radius: 50%; }
.status-dot.SUCCESS { background-color: var(--color-success); box-shadow: 0 0 8px rgba(16, 185, 129, 0.4); }
.status-dot.FAIL { background-color: var(--color-danger); box-shadow: 0 0 8px rgba(239, 68, 68, 0.4); }
.status-dot.CANCEL { background-color: var(--color-warning); box-shadow: 0 0 8px rgba(245, 158, 11, 0.4); }
</style>