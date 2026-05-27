import { Link } from "react-router-dom";
import CaloriesCard from "../components/dashboard/CaloriesCard";
import RemainingCaloriesCard from "../components/dashboard/RemainingCaloriesCard";
import GoalProgressCard from "../components/dashboard/GoalProgressCard";
import FeedbackCard from "../components/dashboard/FeedbackCard";
import NutritionProgress from "../components/dashboard/NutritionProgress";

const defaultConsumed = {
  calories: 0,
  carbs: 0,
  protein: 0,
  fat: 0,
};

function loadNutritionResult() {
  try {
    const storedResult = localStorage.getItem("nutritionResult");
    return storedResult ? JSON.parse(storedResult) : null;
  } catch (error) {
    console.error("nutritionResult 파싱 실패", error);
    return null;
  }
}

function createFeedback(consumed, target) {
  if (consumed.calories > target.calories) {
    return "오늘 권장 칼로리를 초과했습니다. 남은 식사는 가볍게 구성해보세요.";
  }

  if (consumed.protein < target.protein * 0.5) {
    return "단백질 섭취가 아직 부족합니다. 다음 식사에 닭가슴살, 두부, 계란 같은 단백질 식품을 추가해보세요.";
  }

  return "현재까지 목표에 맞게 잘 진행 중입니다. 남은 식사도 균형 있게 기록해보세요.";
}

export default function DashboardPage() {
  const nutritionResult = loadNutritionResult();

  if (!nutritionResult) {
    return (
      <main className="min-h-screen bg-slate-50 px-6 py-10">
        <section className="max-w-3xl mx-auto bg-white rounded-2xl shadow-sm p-8 text-center">
          <h1 className="text-2xl font-bold text-slate-900 mb-3">
            먼저 권장량을 계산해주세요
          </h1>
          <p className="text-slate-500 mb-6">
            대시보드는 localStorage의 nutritionResult를 기준으로 목표 칼로리와 탄단지 목표를 표시합니다.
          </p>
          <Link
            to="/profile"
            className="inline-flex px-5 py-3 rounded-xl bg-emerald-500 text-white font-semibold hover:bg-emerald-600 transition"
          >
            프로필 입력하기
          </Link>
        </section>
      </main>
    );
  }

  const target = {
    calories: Number(nutritionResult.targetCalories) || 0,
    carbs: Number(nutritionResult.targetCarbs) || 0,
    protein: Number(nutritionResult.targetProtein) || 0,
    fat: Number(nutritionResult.targetFat) || 0,
  };
  const consumed = defaultConsumed;
  const goalRate = target.calories > 0
    ? Math.round((consumed.calories / target.calories) * 100)
    : 0;

  const dashboardData = {
    calories: {
      consumed: consumed.calories,
      target: target.calories,
    },
    goalRate,
    feedback: createFeedback(consumed, target),
    nutrition: {
      carbs: {
        current: consumed.carbs,
        target: target.carbs,
      },
      protein: {
        current: consumed.protein,
        target: target.protein,
      },
      fat: {
        current: consumed.fat,
        target: target.fat,
      },
    },
  };

  return (
    <main className="min-h-screen bg-slate-50 px-6 py-10">
      <section className="max-w-5xl mx-auto">
        <div className="flex flex-col md:flex-row md:items-end md:justify-between gap-3 mb-6">
          <div>
            <p className="text-sm font-semibold text-emerald-600 mb-1">
              {nutritionResult.goalLabel || nutritionResult.goalType}
            </p>
            <h1 className="text-2xl font-bold text-slate-900">
              오늘의 대시보드
            </h1>
          </div>

          <Link
            to="/profile"
            className="px-4 py-2 rounded-xl bg-white border text-slate-700 font-semibold hover:bg-slate-100 transition"
          >
            다시 계산하기
          </Link>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
          <CaloriesCard
            consumed={dashboardData.calories.consumed}
          />

          <RemainingCaloriesCard
            consumed={dashboardData.calories.consumed}
            target={dashboardData.calories.target}
          />

          <GoalProgressCard
            goalRate={dashboardData.goalRate}
          />
        </div>

        <div className="mb-6">
          <NutritionProgress
            nutrition={dashboardData.nutrition}
          />
        </div>

        <FeedbackCard feedback={dashboardData.feedback} />
      </section>
    </main>
  );
}
