import Card from "../common/Card";

export default function CaloriesCard({ consumed }) {
  return (
    <Card title="오늘 섭취 칼로리">
      <p className="text-3xl font-bold text-slate-900">
        {consumed} kcal
      </p>
    </Card>
  );
}