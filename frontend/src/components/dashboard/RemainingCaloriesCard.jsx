import Card from "../common/Card";

export default function RemainingCaloriesCard({
  target,
  consumed,
}) {
  const remaining = target - consumed;
  const isOver = remaining < 0;

  return (
    <Card title="남은 칼로리">
      <p className={`text-3xl font-bold ${isOver ? "text-red-500" : "text-emerald-600"}`}>
        {Math.abs(remaining)} kcal
      </p>
      {isOver && (
        <p className="text-sm text-red-500 mt-1">
          목표보다 초과 섭취했습니다.
        </p>
      )}
    </Card>
  );
}
