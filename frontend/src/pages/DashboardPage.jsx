import { useEffect, useState } from "react";
import { Link, useLocation } from "react-router-dom";
import api from "../api/api";
import CaloriesCard from "../components/dashboard/CaloriesCard";
import RemainingCaloriesCard from "../components/dashboard/RemainingCaloriesCard";
import GoalProgressCard from "../components/dashboard/GoalProgressCard";
import FeedbackCard from "../components/dashboard/FeedbackCard";
import NutritionProgress from "../components/dashboard/NutritionProgress";
import {
  buildDashboardData,
  defaultConsumedNutrition,
  emptyMealSummary,
} from "../mock/DashboardData";

function todayString() {
  return new Date().toISOString().slice(0, 10);
}

function loadJsonFromStorage(key) {
  try {
    const storedValue = localStorage.getItem(key);
    return storedValue ? JSON.parse(storedValue) : null;
  } catch (error) {
    console.error(`${key} 파싱 실패`, error);
    return null;
  }
}

function loadProfileId() {
  return localStorage.getItem("profileId");
}

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
    <main className="min-h-screen bg-slate-50 px-6 py-10">
      <section className="mx-auto max-w-3xl rounded-lg border border-slate-200 bg-white p-8 text-center shadow-sm">
        <h1 className="mb-3 text-2xl font-bold text-slate-900">
          먼저 권장량을 계산해주세요
        </h1>
        <p className="mb-6 text-slate-500">
          대시보드는 권장량 기준으로 목표 칼로리와 탄단지 목표를 표시합니다.
        </p>
        <Link
          to="/profile"
          className="inline-flex rounded-lg bg-emerald-500 px-5 py-3 font-semibold text-white transition hover:bg-emerald-600"
        >
          권장량 계산하기
        </Link>
      </section>
    </main>
  );
}

function MissingProfileState() {
  return (
    <main className="min-h-screen bg-slate-50 px-6 py-10">
      <section className="mx-auto max-w-3xl rounded-lg border border-slate-200 bg-white p-8 text-center shadow-sm">
        <h1 className="mb-3 text-2xl font-bold text-slate-900">
          프로필 정보가 없습니다
        </h1>
        <p className="mb-6 text-slate-500">
          대시보드의 실제 식단 요약은 localStorage의 profileId를 기준으로 조회합니다.
        </p>
        <Link
          to="/profile"
          className="inline-flex rounded-lg bg-emerald-500 px-5 py-3 font-semibold text-white transition hover:bg-emerald-600"
        >
          프로필 입력하기
        </Link>
      </section>
    </main>
  );
}

function MealSummaryCard({ date, mealCount, isLoading, errorMessage }) {
  return (
    <section className="rounded-lg border border-slate-200 bg-white p-5 shadow-sm">
      <div className="mb-4 flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
        <div>
          <h2 className="text-sm font-semibold text-slate-700">식단 요약</h2>
          <p className="mt-1 text-sm text-slate-500">
            {date} 기준으로 등록된 식단 기록을 합산합니다.
          </p>
        </div>
        <Link
          to="/meals"
          className="rounded-lg bg-slate-800 px-4 py-2 text-center text-sm font-semibold text-white transition hover:bg-slate-900"
        >
          식단 기록하기
        </Link>
      </div>

      {errorMessage ? (
        <p className="whitespace-pre-line rounded-lg bg-red-50 px-4 py-3 text-sm text-red-700">
          {errorMessage}
        </p>
      ) : (
        <p className="rounded-lg bg-slate-50 px-4 py-3 text-sm text-slate-600">
          {isLoading ? "식단 요약을 불러오는 중입니다." : `${mealCount}건의 식단 기록이 반영되었습니다.`}
        </p>
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
    const fetchMealSummary = async () => {
      if (!profileId) {
        return;
      }

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
    };

    fetchMealSummary();
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
    <main className="min-h-screen bg-slate-50 px-6 py-10">
      <section className="mx-auto max-w-5xl">
        <div className="mb-6 flex flex-col gap-3 md:flex-row md:items-end md:justify-between">
          <div>
            <p className="mb-1 text-sm font-semibold text-emerald-600">
              {nutritionResult.goalLabel || nutritionResult.goalType || "목표"}
            </p>
            <h1 className="text-2xl font-bold text-slate-900">
              오늘의 대시보드
            </h1>
            <p className="mt-1 text-sm text-slate-500">
              profileId: {dashboardData.profileId}
            </p>
          </div>

          <div className="flex flex-col gap-2 sm:flex-row sm:items-center">
            <input
              type="date"
              value={selectedDate}
              onChange={(event) => setSelectedDate(event.target.value)}
              className="rounded-lg border border-slate-200 bg-white px-4 py-2 text-slate-700"
            />
            <Link
              to="/profile"
              className="rounded-lg border border-slate-200 bg-white px-4 py-2 text-center font-semibold text-slate-700 transition hover:bg-slate-100"
            >
              다시 계산하기
            </Link>
          </div>
        </div>

        <div className="mb-6 grid grid-cols-1 gap-4 md:grid-cols-3">
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

        <div className="mb-6">
          <NutritionProgress nutrition={dashboardData.nutrition} />
        </div>

        <div className="mb-6 grid grid-cols-1 gap-4 md:grid-cols-4">
          <div className="rounded-lg border border-slate-200 bg-white p-4">
            <p className="text-sm text-slate-500">목표 칼로리</p>
            <p className="mt-1 text-xl font-bold text-slate-900">
              {dashboardData.target.calories.toLocaleString()} kcal
            </p>
          </div>
          <div className="rounded-lg border border-slate-200 bg-white p-4">
            <p className="text-sm text-slate-500">목표 탄수화물</p>
            <p className="mt-1 text-xl font-bold text-slate-900">
              {dashboardData.target.carbs.toLocaleString()} g
            </p>
          </div>
          <div className="rounded-lg border border-slate-200 bg-white p-4">
            <p className="text-sm text-slate-500">목표 단백질</p>
            <p className="mt-1 text-xl font-bold text-slate-900">
              {dashboardData.target.protein.toLocaleString()} g
            </p>
          </div>
          <div className="rounded-lg border border-slate-200 bg-white p-4">
            <p className="text-sm text-slate-500">목표 지방</p>
            <p className="mt-1 text-xl font-bold text-slate-900">
              {dashboardData.target.fat.toLocaleString()} g
            </p>
          </div>
        </div>

        <div className="space-y-6">
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
