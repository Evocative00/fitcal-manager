import { useEffect, useState } from "react";
import { Link, useLocation } from "react-router-dom";
import api from "../api/api";
import PageHero from "../components/common/PageHero";
import StatusBanner from "../components/common/StatusBanner";
import MetricCard from "../components/common/MetricCard";
import CaloriesCard from "../components/dashboard/CaloriesCard";
import RemainingCaloriesCard from "../components/dashboard/RemainingCaloriesCard";
import GoalProgressCard from "../components/dashboard/GoalProgressCard";
import FeedbackCard from "../components/dashboard/FeedbackCard";
import NutritionProgress from "../components/dashboard/NutritionProgress";
import MacroBalanceChart from "../components/dashboard/MacroBalanceChart";
import {
  buildDashboardData,
  defaultConsumedNutrition,
  emptyMealSummary,
} from "../mock/DashboardData";
import { formatGram, formatKcal, todayString } from "../utils/formatters";
import { loadJsonFromStorage, loadProfileId } from "../utils/storage";

function getApiErrorMessage(error) {
  const data = error.response?.data;

  if (data?.details?.length > 0) {
    return data.details.join("\n");
  }

  return data?.message || "식단 요약 API 호출 중 오류가 발생했습니다. 백엔드 실행 상태를 확인해주세요.";
}

function createFeedback(data) {
  const { target, consumed, remainingCalories, nutrition } = data;

  if (target.calories === 0) {
    return "목표 칼로리가 없어 달성률을 계산할 수 없습니다. 먼저 권장량을 다시 계산해주세요.";
  }

  if (remainingCalories < 0) {
    return `오늘 목표보다 ${Math.abs(remainingCalories).toLocaleString()} kcal 초과 섭취했습니다. 다음 식사는 가볍게 구성해보세요.`;
  }

  if (consumed.calories === 0) {
    return "아직 기록된 식단이 없습니다. 식단 기록 화면에서 오늘 먹은 음식을 등록해보세요.";
  }

  if (nutrition.protein.rate < 50) {
    return "단백질 섭취량이 목표의 절반보다 낮습니다. 다음 식사에 단백질 식품을 추가해보세요.";
  }

  if (data.goalRate >= 90) {
    return "오늘 칼로리 목표에 거의 도달했습니다. 남은 식사는 목표 탄단지 균형을 기준으로 조절해보세요.";
  }

  return `목표까지 ${remainingCalories.toLocaleString()} kcal 남았습니다. 현재 탄수화물 ${nutrition.carbs.rate}%, 단백질 ${nutrition.protein.rate}%, 지방 ${nutrition.fat.rate}%를 채웠습니다.`;
}

function EmptyDashboardState() {
  return (
    <main className="px-5 py-10 md:px-8">
      <section className="mx-auto max-w-3xl rounded-3xl border border-slate-200 bg-white p-8 text-center shadow-sm">
        <p className="text-sm font-bold text-emerald-600">nutritionResult 필요</p>
        <h1 className="mt-2 text-2xl font-black text-slate-950">
          먼저 권장량을 계산해주세요
        </h1>
        <p className="mt-3 text-slate-500">
          대시보드는 권장량 기준으로 목표 칼로리와 탄단지 목표를 표시합니다.
        </p>
        <Link
          to="/profile"
          className="mt-6 inline-flex rounded-2xl bg-emerald-500 px-5 py-3 font-bold text-white transition hover:bg-emerald-600"
        >
          권장량 계산하기
        </Link>
      </section>
    </main>
  );
}

function MissingProfileState() {
  return (
    <main className="px-5 py-10 md:px-8">
      <section className="mx-auto max-w-3xl rounded-3xl border border-slate-200 bg-white p-8 text-center shadow-sm">
        <p className="text-sm font-bold text-emerald-600">profileId 필요</p>
        <h1 className="mt-2 text-2xl font-black text-slate-950">
          프로필 정보가 없습니다
        </h1>
        <p className="mt-3 text-slate-500">
          대시보드의 실제 식단 요약은 localStorage의 profileId를 기준으로 조회합니다.
        </p>
        <Link
          to="/profile"
          className="mt-6 inline-flex rounded-2xl bg-emerald-500 px-5 py-3 font-bold text-white transition hover:bg-emerald-600"
        >
          프로필 입력하기
        </Link>
      </section>
    </main>
  );
}

function MealSummaryCard({ date, mealCount, isLoading, errorMessage }) {
  return (
    <section className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">
      <div className="mb-4 flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
        <div>
          <p className="text-xs font-bold uppercase tracking-[0.2em] text-emerald-600">meal summary</p>
          <h2 className="mt-1 text-lg font-black text-slate-950">식단 요약</h2>
          <p className="mt-1 text-sm text-slate-500">
            {date} 기준으로 등록된 식단 기록을 합산합니다.
          </p>
        </div>
        <Link
          to="/meals"
          className="rounded-2xl bg-slate-900 px-4 py-3 text-center text-sm font-bold text-white transition hover:bg-slate-800"
        >
          식단 기록하기
        </Link>
      </div>

      {errorMessage ? (
        <StatusBanner type="error">{errorMessage}</StatusBanner>
      ) : (
        <div className="rounded-2xl bg-slate-50 px-4 py-3 text-sm font-semibold text-slate-600">
          {isLoading ? "식단 요약을 불러오는 중입니다." : `${mealCount}건의 식단 기록이 반영되었습니다.`}
        </div>
      )}
    </section>
  );
}

export default function DashboardPage() {
  const location = useLocation();
  const nutritionResult = location.state?.nutritionResult || loadJsonFromStorage("nutritionResult");
  const profileId = loadProfileId();
  const [selectedDate, setSelectedDate] = useState(todayString());
  const [mealSummary, setMealSummary] = useState(emptyMealSummary);
  const [isLoadingSummary, setIsLoadingSummary] = useState(false);
  const [summaryErrorMessage, setSummaryErrorMessage] = useState("");

  useEffect(() => {
    if (!profileId) {
      return undefined;
    }

    const timerId = window.setTimeout(async () => {
      setIsLoadingSummary(true);
      setSummaryErrorMessage("");

      try {
        const response = await api.get("/meals/summary", {
          params: {
            profileId,
            date: selectedDate,
          },
        });
        setMealSummary(response.data);
      } catch (error) {
        console.error(error);
        setMealSummary({ ...emptyMealSummary, profileId, date: selectedDate });
        setSummaryErrorMessage(getApiErrorMessage(error));
      } finally {
        setIsLoadingSummary(false);
      }
    }, 0);

    return () => window.clearTimeout(timerId);
  }, [profileId, selectedDate]);

  if (!nutritionResult) {
    return <EmptyDashboardState />;
  }

  if (!profileId) {
    return <MissingProfileState />;
  }

  const consumed = mealSummary || defaultConsumedNutrition;
  const dashboardData = buildDashboardData({
    nutritionResult,
    profileId,
    consumed,
    mealSummary,
    date: selectedDate,
  });
  const feedback = createFeedback(dashboardData);

  return (
    <main className="px-5 py-10 md:px-8">
      <section className="mx-auto max-w-7xl space-y-6">
        <PageHero
          eyebrow="daily progress"
          title="목표와 실제 섭취량을 비교하세요"
          description="권장량 계산 결과와 식단 기록 summary API를 연결해 오늘의 칼로리와 탄단지 진행률을 보여줍니다."
          actions={
            <>
              <input
                type="date"
                value={selectedDate}
                onChange={(event) => setSelectedDate(event.target.value)}
                className="rounded-2xl border border-slate-200 bg-white px-4 py-3 font-semibold text-slate-700 shadow-sm outline-none transition focus:border-emerald-400 focus:ring-4 focus:ring-emerald-100"
              />
              <Link
                to="/meals"
                className="rounded-2xl bg-emerald-500 px-5 py-3 text-center font-bold text-white transition hover:-translate-y-0.5 hover:bg-emerald-600"
              >
                식단 기록하기
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
          <div className="grid grid-cols-2 gap-3 md:grid-cols-4">
            <MetricCard label="profileId" value={dashboardData.profileId} helper="현재 사용자" />
            <MetricCard label="목표 칼로리" value={formatKcal(dashboardData.target.calories)} tone="emerald" />
            <MetricCard label="섭취 칼로리" value={formatKcal(dashboardData.consumed.calories)} tone="sky" />
            <MetricCard label="식단 기록" value={`${dashboardData.mealCount}건`} helper={dashboardData.date} />
          </div>
        </PageHero>

        <div className="grid grid-cols-1 gap-4 md:grid-cols-3">
          <CaloriesCard
            consumed={dashboardData.consumed.calories}
            target={dashboardData.target.calories}
          />

          <RemainingCaloriesCard
            remaining={dashboardData.remainingCalories}
          />

          <GoalProgressCard
            consumed={dashboardData.consumed.calories}
            target={dashboardData.target.calories}
            goalRate={dashboardData.goalRate}
          />
        </div>

        <div className="grid grid-cols-1 gap-6 lg:grid-cols-[1fr_420px]">
          <NutritionProgress nutrition={dashboardData.nutrition} />
          <MacroBalanceChart nutrition={dashboardData.nutrition} />
        </div>

        <div className="grid grid-cols-1 gap-4 md:grid-cols-4">
          <MetricCard label="목표 탄수화물" value={formatGram(dashboardData.target.carbs)} />
          <MetricCard label="목표 단백질" value={formatGram(dashboardData.target.protein)} />
          <MetricCard label="목표 지방" value={formatGram(dashboardData.target.fat)} />
          <MetricCard label="현재 지방 섭취" value={formatGram(dashboardData.consumed.fat)} tone="amber" />
        </div>

        <div className="grid grid-cols-1 gap-6 lg:grid-cols-[1fr_420px]">
          <FeedbackCard feedback={feedback} />
          <MealSummaryCard
            date={dashboardData.date}
            mealCount={dashboardData.mealCount}
            isLoading={isLoadingSummary}
            errorMessage={summaryErrorMessage}
          />
        </div>
      </section>
    </main>
  );
}
