import Card from "../common/Card";

export default function GoalProgressCard({ goalRate }) {
  const safeGoalRate = Math.max(0, Math.min(Number(goalRate) || 0, 100));

  return (
    <Card title="목표 달성률">
      <p className="text-3xl font-bold text-slate-900">
        {safeGoalRate}%
      </p>
    </Card>
  );
}
