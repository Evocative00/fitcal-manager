import Card from "../common/Card";
import ProgressBar from "../common/ProgressBar";

export default function GoalProgressCard({ consumed, target, goalRate }) {
  return (
    <Card title="목표 달성률">
      <p className={`text-3xl font-bold ${goalRate > 100 ? "text-red-500" : "text-slate-900"}`}>
        {goalRate}%
      </p>
      <div className="mt-3">
        <ProgressBar value={consumed} max={target} unit="kcal" showLabel={false} />
      </div>
      <p className="mt-2 text-sm text-slate-500">
        {consumed.toLocaleString()} / {target.toLocaleString()} kcal
      </p>
    </Card>
  );
}
