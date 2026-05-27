import CaloriesCard from "../components/dashboard/CaloriesCard";
import RemainingCaloriesCard from "../components/dashboard/RemainingCaloriesCard";
import GoalProgressCard from "../components/dashboard/GoalProgressCard";
import FeedbackCard from "../components/dashboard/FeedbackCard";
import NutritionProgress from "../components/dashboard/NutritionProgress";

import { dashboardData } from "../mock/DashboardData";

export default function DashboardPage() {
  return (
    <main className="min-h-screen bg-slate-50 px-6 py-10">
      <section className="max-w-5xl mx-auto">
        <h1 className="text-2xl font-bold text-slate-900 mb-6">
          오늘의 대시보드
        </h1>

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