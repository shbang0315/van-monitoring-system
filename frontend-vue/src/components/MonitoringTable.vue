<script setup>
import { computed } from 'vue';

const props = defineProps({
  title: { type: String, default: '모니터링' },
  data: { type: Array, default: () => [] },
  type: { type: String, default: 'default' } // default, danger, warning, success
});

const formatDate = (dateString) => new Date(dateString).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', second: '2-digit' });
const formatMoney = (amount) => amount.toLocaleString();

// 타입별 헤더 포인트 색상 (왼쪽 라인)
const headerAccentClass = computed(() => {
  switch (props.type) {
    case 'danger': return 'accent-danger';
    case 'warning': return 'accent-warning';
    case 'success': return 'accent-success';
    default: return 'accent-default';
  }
});
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
            <th width="22%">시간</th>
            <th width="38%">가맹점</th>
            <th width="25%" class="text-right">금액</th>
            <th width="15%" class="text-center">상태</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in data" :key="item.id" class="data-row">
            <td class="time">{{ formatDate(item.transactionTime) }}</td>
            <td class="store-name" :title="item.storeName">{{ item.storeName }}</td>
            <td class="amount">{{ formatMoney(item.amount) }}</td>
            <td class="text-center">
              <span :class="['status-dot', item.status]"></span>
            </td>
          </tr>
          <tr v-if="data.length === 0">
            <td colspan="4" class="no-data">데이터가 없습니다.</td>
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
  background-color: var(--bg-secondary); /* 어두운 패널 배경 */
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06); /* 부드러운 그림자 */
  border: 1px solid var(--border-color);
}

.panel-header {
  padding: 12px 16px;
  background-color: rgba(255, 255, 255, 0.03); /* 아주 살짝 밝게 */
  border-bottom: 1px solid var(--border-color);
  display: flex;
  align-items: center;
}
/* 왼쪽 포인트 라인 스타일 */
.accent-default { border-left: 4px solid var(--text-muted); }
.accent-danger { border-left: 4px solid var(--color-danger); }
.accent-warning { border-left: 4px solid var(--color-warning); }
.accent-success { border-left: 4px solid var(--color-success); }

.panel-header h4 { margin: 0; font-size: 1rem; font-weight: 600; display: flex; justify-content: space-between; width: 100%; color: var(--text-primary); }
.count-badge { background-color: var(--bg-tertiary); padding: 2px 8px; border-radius: 10px; font-size: 0.8rem; color: var(--text-secondary); }

.table-container { flex: 1; overflow-y: auto; }
table { width: 100%; border-collapse: collapse; font-size: 0.9rem; table-layout: fixed; }

th {
  position: sticky; top: 0; z-index: 1;
  background-color: var(--bg-tertiary);
  color: var(--text-secondary);
  font-weight: 600; font-size: 0.8rem; text-transform: uppercase; letter-spacing: 0.05em;
  padding: 10px 12px; text-align: left;
}

td {
  padding: 10px 12px;
  border-bottom: 1px solid var(--border-color);
  color: var(--text-secondary);
  white-space: nowrap; overflow: hidden; text-overflow: ellipsis;
  transition: background-color 0.2s;
}
/* 마우스 오버 효과 */
.data-row:hover td { background-color: rgba(255, 255, 255, 0.05); color: var(--text-primary); }

.time { font-family: 'Roboto Mono', monospace; color: var(--text-muted); font-size: 0.85rem; }
.store-name { font-weight: 500; }
.amount { text-align: right; font-weight: 700; color: var(--text-primary); font-family: 'Roboto Mono', monospace; }
.text-center { text-align: center; }
.no-data { text-align: center; padding: 30px; color: var(--text-muted); font-style: italic; }

/* 상태 점 디자인 개선 */
.status-dot { display: inline-block; width: 10px; height: 10px; border-radius: 50%; }
.status-dot.SUCCESS { background-color: var(--color-success); box-shadow: 0 0 8px rgba(16, 185, 129, 0.4); }
.status-dot.FAIL { background-color: var(--color-danger); box-shadow: 0 0 8px rgba(239, 68, 68, 0.4); }
.status-dot.CANCEL { background-color: var(--color-warning); box-shadow: 0 0 8px rgba(245, 158, 11, 0.4); }
</style>