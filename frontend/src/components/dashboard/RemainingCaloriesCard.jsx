import Card from "../common/Card";

export default function RemainingCaloriesCard({ remaining }) {
  const isOver = remaining < 0;

  return (
    <Card title={isOver ? "초과 칼로리" : "남은 칼로리"}>
      <p className={`text-3xl font-bold ${isOver ? "text-red-500" : "text-emerald-600"}`}>
        {Math.abs(remaining).toLocaleString()} kcal
      </p>
      <p className={`mt-1 text-sm ${isOver ? "text-red-500" : "text-slate-500"}`}>
        {isOver ? "목표보다 초과 섭취했습니다." : "오늘 더 섭취할 수 있는 칼로리입니다."}
      </p>
    </Card>
  );
}
