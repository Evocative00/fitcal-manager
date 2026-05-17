export default function DashboardPage() {
  return (
    <main className="min-h-screen bg-slate-50 px-6 py-10">
      <section className="max-w-5xl mx-auto">
        <h1 className="text-2xl font-bold text-slate-900 mb-6">
          오늘의 대시보드
        </h1>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
          <div className="bg-white rounded-2xl shadow-sm p-5">
            <p className="text-sm text-slate-500">오늘 섭취 칼로리</p>
            <p className="text-3xl font-bold text-slate-900">1,450 kcal</p>
          </div>

          <div className="bg-white rounded-2xl shadow-sm p-5">
            <p className="text-sm text-slate-500">남은 칼로리</p>
            <p className="text-3xl font-bold text-emerald-600">650 kcal</p>
          </div>

          <div className="bg-white rounded-2xl shadow-sm p-5">
            <p className="text-sm text-slate-500">목표 달성률</p>
            <p className="text-3xl font-bold text-slate-900">69%</p>
          </div>
        </div>

        <div className="bg-white rounded-2xl shadow-sm p-6">
          <p className="font-semibold text-slate-900 mb-3">피드백 메시지</p>
          <p className="text-slate-600">
            단백질이 조금 부족합니다. 저녁 식사에 단백질 식품을 추가해보세요.
          </p>
        </div>
      </section>
    </main>
  );
}