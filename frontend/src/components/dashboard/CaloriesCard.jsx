import Card from "../common/Card";

export default function CaloriesCard({ consumed, target }) {
  return (
    <Card title="오늘 섭취 칼로리">
      <p className="text-3xl font-bold text-slate-900">
        {consumed.toLocaleString()} kcal
      </p>
      <p className="mt-1 text-sm text-slate-500">
        목표 {target.toLocaleString()} kcal
      </p>
    </Card>
  );
}
