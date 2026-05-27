import Card from "../common/Card";

export default function RemainingCaloriesCard({
  target,
  consumed,
}) {
  const remaining = target - consumed;

  return (
    <Card title="남은 칼로리">
      <p className="text-3xl font-bold text-emerald-600">
        {remaining} kcal
      </p>
    </Card>
  );
}