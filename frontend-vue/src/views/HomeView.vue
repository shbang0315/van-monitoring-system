<template>
  <div class="home-container">
    <div class="content-wrapper">
      <header>
        <h1>🚀 VAN 시스템 통합 관제탑</h1>
        <p class="subtitle">실시간 트랜잭션 및 시스템 상태 모니터링</p>
      </header>
      
      <div class="menu-grid">
        <div class="menu-card active" @click="$router.push('/van')">
          <div class="icon-wrapper accent-blue">💳</div>
          <div class="card-content">
            <h3>VAN 거래 모니터링</h3>
            <p>실시간 승인/취소 현황 대시보드</p>
          </div>
          <div class="arrow">→</div>
        </div>

        <div class="menu-card disabled">
          <div class="icon-wrapper accent-purple">📂</div>
          <div class="card-content">
            <h3>BatchGW 모니터링</h3>
            <p>대량 파일 송수신 현황 (준비중)</p>
          </div>
        </div>

        <div class="menu-card disabled">
          <div class="icon-wrapper accent-green">🖥️</div>
          <div class="card-content">
            <h3>시스템 리소스</h3>
            <p>서버 CPU/Memory 상태 (준비중)</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.home-container {
  min-height: 100vh;
  background-color: var(--bg-primary); /* 다크 배경 */
  display: flex; justify-content: center; align-items: center;
  padding: 40px;
}
.content-wrapper { max-width: 1000px; width: 100%; }

header { text-align: center; margin-bottom: 60px; }
h1 { color: var(--text-primary); font-size: 2.5rem; font-weight: 800; margin-bottom: 15px; letter-spacing: -1px; }
.subtitle { color: var(--text-secondary); font-size: 1.1rem; }

.menu-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 25px; }

.menu-card {
  background-color: var(--bg-secondary); /* 패널 배경 */
  border: 1px solid var(--border-color);
  padding: 30px; border-radius: 16px;
  display: flex; align-items: center; gap: 20px;
  cursor: pointer; transition: all 0.3s ease;
  position: relative; overflow: hidden;
}
.menu-card.active:hover {
  transform: translateY(-5px);
  border-color: var(--color-info);
  box-shadow: 0 10px 25px -5px rgba(59, 130, 246, 0.15); /* 푸른빛 그림자 */
}
.menu-card.active:hover .arrow { opacity: 1; transform: translateX(5px); color: var(--color-info); }

.icon-wrapper {
  width: 60px; height: 60px; display: flex; justify-content: center; align-items: center;
  font-size: 2rem; border-radius: 12px; background: var(--bg-tertiary);
}
.accent-blue { color: var(--color-info); background: rgba(59, 130, 246, 0.1); }
.accent-purple { color: #8b5cf6; background: rgba(139, 92, 246, 0.1); }
.accent-green { color: var(--color-success); background: rgba(16, 185, 129, 0.1); }

.card-content h3 { margin: 0 0 8px; color: var(--text-primary); font-size: 1.2rem; }
.card-content p { margin: 0; color: var(--text-muted); font-size: 0.95rem; }

.arrow { position: absolute; right: 30px; font-size: 1.5rem; color: var(--text-muted); opacity: 0; transition: all 0.3s; }
.disabled { opacity: 0.5; cursor: not-allowed; filter: grayscale(0.5); }
</style>