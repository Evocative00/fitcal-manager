export default function NutritionResultPage() {
  const mockResult = {
    targetCalories: 2100,
    targetCarbs: 210,
    targetProtein: 184,
    targetFat: 58,
    goalType: "DIET",
    message: "다이어트 목표에 맞춰 권장 칼로리를 조정했습니다.",
  };

  return (
    <main className="min-h-screen bg-slate-50 px-6 py-10">
      <section className="max-w-4xl mx-auto">
        <h1 className="text-2xl font-bold text-slate-900 mb-6">
          권장량 계산 결과
        </h1>

        <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
          <div className="bg-white rounded-2xl shadow-sm p-5">
            <p className="text-sm text-slate-500">하루 권장 칼로리</p>
            <p className="text-2xl font-bold text-emerald-600">
              {mockResult.targetCalories} kcal
            </p>
          </div>

          <div className="bg-white rounded-2xl shadow-sm p-5">
            <p className="text-sm text-slate-500">탄수화물</p>
            <p className="text-2xl font-bold">{mockResult.targetCarbs} g</p>
          </div>

          <div className="bg-white rounded-2xl shadow-sm p-5">
            <p className="text-sm text-slate-500">단백질</p>
            <p className="text-2xl font-bold">{mockResult.targetProtein} g</p>
          </div>

          <div className="bg-white rounded-2xl shadow-sm p-5">
            <p className="text-sm text-slate-500">지방</p>
            <p className="text-2xl font-bold">{mockResult.targetFat} g</p>
          </div>
        </div>

        <div className="bg-emerald-50 border border-emerald-100 rounded-2xl p-5 text-emerald-800">
          {mockResult.message}
        </div>
      </section>
    </main>
  );
}