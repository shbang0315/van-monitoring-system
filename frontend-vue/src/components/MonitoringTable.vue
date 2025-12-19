<script setup>
import { computed } from 'vue';

const props = defineProps({
  title: { type: String, default: '모니터링' },
  data: { type: Array, default: () => [] },
  type: { type: String, default: 'default' } // default, danger, warning, success 등
});

const formatDate = (dateString) => new Date(dateString).toLocaleTimeString();
const formatMoney = (amount) => amount.toLocaleString();

// 타입에 따른 헤더 색상 클래스
const headerClass = computed(() => {
  switch (props.type) {
    case 'danger': return 'bg-danger';
    case 'warning': return 'bg-warning';
    case 'success': return 'bg-success';
    default: return 'bg-default';
  }
});
</script>

<template>
  <div class="table-container">
    <div class="table-header" :class="headerClass">
      <h4>{{ title }} <span class="count">({{ data.length }})</span></h4>
    </div>
    <div class="table-body">
      <table>
        <thead>
          <tr>
            <th width="25%">시간</th>
            <th width="35%">가맹점</th>
            <th width="25%">금액</th>
            <th width="15%">상태</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in data" :key="item.id">
            <td class="time">{{ formatDate(item.transactionTime) }}</td>
            <td class="store">{{ item.storeName }}</td>
            <td class="amount">{{ formatMoney(item.amount) }}</td>
            <td>
              <span :class="['status-dot', item.status]"></span>
            </td>
          </tr>
          <tr v-if="data.length === 0">
            <td colspan="4" class="no-data">데이터 없음</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<style scoped>
.table-container {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: white;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #e0e0e0;
}

.table-header {
  padding: 10px 15px;
  color: #333;
  border-bottom: 1px solid #eee;
}
.table-header h4 { margin: 0; font-size: 0.95rem; display: flex; justify-content: space-between; }
.count { font-weight: normal; opacity: 0.7; }

/* 헤더 색상 테마 */
.bg-default { background-color: #f8f9fa; border-left: 4px solid #6c757d; }
.bg-danger { background-color: #fff5f5; border-left: 4px solid #ff7675; }
.bg-warning { background-color: #fffbf0; border-left: 4px solid #fdcb6e; }
.bg-success { background-color: #f0fff4; border-left: 4px solid #00b894; }

.table-body { flex: 1; overflow-y: auto; }
table { width: 100%; border-collapse: collapse; font-size: 0.8rem; table-layout: fixed; }
th { position: sticky; top: 0; background: white; color: #888; font-weight: 600; padding: 8px; text-align: left; border-bottom: 1px solid #eee; font-size: 0.75rem; }
td { padding: 6px 8px; border-bottom: 1px solid #f5f5f5; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }

.time { color: #666; font-family: monospace; }
.amount { text-align: right; font-weight: bold; color: #2d3436; }
.no-data { text-align: center; color: #999; padding: 20px; }

/* 상태 점 (Dot) */
.status-dot { display: inline-block; width: 8px; height: 8px; border-radius: 50%; }
.status-dot.SUCCESS { background-color: #00b894; box-shadow: 0 0 3px #00b894; }
.status-dot.FAIL { background-color: #ff7675; box-shadow: 0 0 3px #ff7675; }
.status-dot.CANCEL { background-color: #fab1a0; }
</style>