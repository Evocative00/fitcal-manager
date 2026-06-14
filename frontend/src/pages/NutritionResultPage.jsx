import { Link, useLocation } from "react-router-dom";
import PageHero from "../components/common/PageHero";
import MetricCard from "../components/common/MetricCard";
import StatusBanner from "../components/common/StatusBanner";
import { formatGram, formatKcal } from "../utils/formatters";
import { loadJsonFromStorage } from "../utils/storage";

function ResultCard({ label, value, unit, helper, tone }) {
  const displayValue = unit === "kcal" ? formatKcal(value) : unit === "g" ? formatGram(value) : value ?? "-";

  return (
    <MetricCard
      label={label}
      value={displayValue}
      helper={helper}
      tone={tone}
    />
  );
}

export default function NutritionResultPage() {
  const location = useLocation();
  const result = location.state?.nutritionResult || loadJsonFromStorage("nutritionResult");

  if (!result) {
    return (
      <main className="px-5 py-10 md:px-8">
        <section className="mx-auto max-w-3xl rounded-3xl border border-slate-200 bg-white p-8 text-center shadow-sm">
          <p className="text-sm font-bold text-emerald-600">calculation required</p>
          <h1 className="mt-2 text-2xl font-black text-slate-950">
            계산 결과가 없습니다
          </h1>
          <p className="mt-3 text-slate-500">
            먼저 프로필을 입력하고 권장량을 계산해주세요.
          </p>
          <Link
            to="/profile"
            className="mt-6 inline-flex rounded-2xl bg-emerald-500 px-5 py-3 font-bold text-white transition hover:bg-emerald-600"
          >
            권장량 계산하러 가기
          </Link>
        </section>
      </main>
    );
  }

  return (
    <main className="px-5 py-10 md:px-8">
      <section className="mx-auto max-w-7xl space-y-6">
        <PageHero
          eyebrow="nutrition target"
          title="권장량 계산 결과"
          description="프로필 정보와 목표 유형에 따라 계산된 하루 권장 칼로리와 탄단지 목표입니다. 이 값은 대시보드의 목표 기준으로 사용됩니다."
          actions={
            <>
              <Link
                to="/meals"
                className="rounded-2xl bg-emerald-500 px-5 py-3 text-center font-bold text-white transition hover:-translate-y-0.5 hover:bg-emerald-600"
              >
                식단 기록하기
              </Link>
              <Link
                to="/dashboard"
                className="rounded-2xl bg-slate-900 px-5 py-3 text-center font-bold text-white transition hover:-translate-y-0.5 hover:bg-slate-800"
              >
                대시보드 보기
              </Link>
              <Link
                to="/profile"
                className="rounded-2xl border border-slate-200 bg-white px-5 py-3 text-center font-bold text-slate-700 transition hover:-translate-y-0.5 hover:bg-slate-50"
              >
                다시 계산하기
              </Link>
            </>
          }
        >
          <StatusBanner type="success" title={result.goalLabel || result.goalType}>
            {result.message}
          </StatusBanner>
        </PageHero>

        <div className="grid grid-cols-1 gap-4 md:grid-cols-4">
          <ResultCard label="하루 권장 칼로리" value={result.targetCalories} unit="kcal" tone="emerald" />
          <ResultCard label="탄수화물" value={result.targetCarbs} unit="g" />
          <ResultCard label="단백질" value={result.targetProtein} unit="g" tone="sky" />
          <ResultCard label="지방" value={result.targetFat} unit="g" tone="amber" />
        </div>

        <div className="grid grid-cols-1 gap-4 md:grid-cols-3">
          <ResultCard label="BMR" value={result.bmr} unit="kcal" helper="기초대사량" />
          <ResultCard label="TDEE" value={result.tdee} unit="kcal" helper="활동량 반영 유지 칼로리" />
          <ResultCard label="목표 타입" value={result.goalType} helper="API enum 고정값" />
        </div>

        <section className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">
          <h2 className="text-xl font-black text-slate-950">다음 단계</h2>
          <div className="mt-4 grid grid-cols-1 gap-3 md:grid-cols-3">
            <div className="rounded-2xl bg-slate-50 p-4 text-sm text-slate-600">
              <p className="font-bold text-slate-900">1. 식단 등록</p>
              <p className="mt-1">오늘 먹은 음식을 /meals에서 입력합니다.</p>
            </div>
            <div className="rounded-2xl bg-slate-50 p-4 text-sm text-slate-600">
              <p className="font-bold text-slate-900">2. 요약 확인</p>
              <p className="mt-1">대시보드에서 summary API 합산 결과를 확인합니다.</p>
            </div>
            <div className="rounded-2xl bg-slate-50 p-4 text-sm text-slate-600">
              <p className="font-bold text-slate-900">3. 피드백 반영</p>
              <p className="mt-1">목표 초과 또는 단백질 부족 메시지를 보고 식단을 조정합니다.</p>
            </div>
          </div>
        </section>
      </section>
    </main>
  );
}
