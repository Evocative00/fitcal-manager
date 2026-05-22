import Card from "../common/Card";

export default function GoalProgressCard({ goalRate }) {
  return (
    <Card title="목표 달성률">
      <p className="text-3xl font-bold text-slate-900">
        {goalRate}%
      </p>
    </Card>
  );
}