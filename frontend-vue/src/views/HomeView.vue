<template>
  <div class="home-container">
    <h1>🚀 VAN 시스템 통합 모니터링</h1>
    <div class="menu-grid">
      <div class="menu-card" @click="$router.push('/van')">
        <div class="icon">💳</div>
        <h3>VAN 거래 모니터링</h3>
        <p>실시간 승인/취소 트랜잭션 현황</p>
      </div>

      <div class="menu-card disabled">
        <div class="icon">📂</div>
        <h3>BatchGW 모니터링</h3>
        <p>대량 파일 송수신 현황 (준비중)</p>
      </div>

      <div class="menu-card disabled">
        <div class="icon">🖥️</div>
        <h3>시스템 리소스</h3>
        <p>CPU/Memory 상태 (준비중)</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.home-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 60px 20px;
  text-align: center;
}

h1 {
  color: #2c3e50;
  margin-bottom: 50px;
  font-size: 2.5rem;
}

.menu-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 30px;
}

.menu-card {
  background: white;
  padding: 40px;
  border-radius: 16px;
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.05);
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid #eee;
}

.menu-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.1);
  border-color: #3498db;
}

.icon {
  font-size: 3rem;
  margin-bottom: 20px;
}

.menu-card h3 {
  margin: 0 0 10px;
  color: #34495e;
}

.menu-card p {
  color: #7f8c8d;
  margin: 0;
}

.disabled {
  opacity: 0.6;
  cursor: not-allowed;
  background-color: #f9f9f9;
}
.disabled:hover {
  transform: none;
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.05);
  border-color: #eee;
}
</style>