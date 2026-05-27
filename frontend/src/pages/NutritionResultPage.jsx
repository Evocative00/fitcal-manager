import { Link, useLocation } from "react-router-dom";

function loadStoredNutritionResult() {
  try {
    const storedResult = localStorage.getItem("nutritionResult");
    return storedResult ? JSON.parse(storedResult) : null;
  } catch (error) {
    console.error("nutritionResult 파싱 실패", error);
    return null;
  }
}

function ResultCard({ label, value, unit }) {
  return (
    <div className="bg-white rounded-2xl shadow-sm p-5">
      <p className="text-sm text-slate-500">{label}</p>
      <p className="text-2xl font-bold text-slate-900">
        {value ?? "-"} {unit}
      </p>
    </div>
  );
}

export default function NutritionResultPage() {
  const location = useLocation();
  const result = location.state?.nutritionResult || loadStoredNutritionResult();

  if (!result) {
    return (
      <main className="min-h-screen bg-slate-50 px-6 py-10">
        <section className="max-w-3xl mx-auto bg-white rounded-2xl shadow-sm p-8 text-center">
          <h1 className="text-2xl font-bold text-slate-900 mb-3">
            계산 결과가 없습니다
          </h1>
          <p className="text-slate-500 mb-6">
            먼저 프로필을 입력하고 권장량을 계산해주세요.
          </p>
          <Link
            to="/profile"
            className="inline-flex px-5 py-3 rounded-xl bg-emerald-500 text-white font-semibold hover:bg-emerald-600 transition"
          >
            권장량 계산하러 가기
          </Link>
        </section>
      </main>
    );
  }

  return (
    <main className="min-h-screen bg-slate-50 px-6 py-10">
      <section className="max-w-4xl mx-auto">
        <div className="flex flex-col md:flex-row md:items-end md:justify-between gap-3 mb-6">
          <div>
            <p className="text-sm font-semibold text-emerald-600 mb-1">
              {result.goalLabel || result.goalType}
            </p>
            <h1 className="text-2xl font-bold text-slate-900">
              권장량 계산 결과
            </h1>
          </div>

          <div className="flex gap-2">
            <Link
              to="/profile"
              className="px-4 py-2 rounded-xl bg-white border text-slate-700 font-semibold hover:bg-slate-100 transition"
            >
              다시 계산하기
            </Link>
            <Link
              to="/dashboard"
              className="px-4 py-2 rounded-xl bg-emerald-500 text-white font-semibold hover:bg-emerald-600 transition"
            >
              대시보드로 이동
            </Link>
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
          <ResultCard label="하루 권장 칼로리" value={result.targetCalories} unit="kcal" />
          <ResultCard label="탄수화물" value={result.targetCarbs} unit="g" />
          <ResultCard label="단백질" value={result.targetProtein} unit="g" />
          <ResultCard label="지방" value={result.targetFat} unit="g" />
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
          <ResultCard label="BMR" value={result.bmr} unit="kcal" />
          <ResultCard label="TDEE" value={result.tdee} unit="kcal" />
          <ResultCard label="목표 타입" value={result.goalType} unit="" />
        </div>

        <div className="bg-emerald-50 border border-emerald-100 rounded-2xl p-5 text-emerald-800">
          {result.message}
        </div>
      </section>
    </main>
  );
}
