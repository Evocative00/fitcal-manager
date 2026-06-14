import { Bar, BarChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from "recharts";
import Card from "../common/Card";

function buildChartData(nutrition) {
  return [
    { name: "탄수화물", target: nutrition.carbs.target, consumed: nutrition.carbs.consumed },
    { name: "단백질", target: nutrition.protein.target, consumed: nutrition.protein.consumed },
    { name: "지방", target: nutrition.fat.target, consumed: nutrition.fat.consumed },
  ];
}

export default function MacroBalanceChart({ nutrition }) {
  const chartData = buildChartData(nutrition);

  return (
    <Card
      title="탄단지 비교 차트"
      description="목표량과 실제 섭취량을 한눈에 비교합니다."
      className="h-full"
    >
      <div className="h-72">
        <ResponsiveContainer width="100%" height="100%">
          <BarChart data={chartData} margin={{ top: 8, right: 8, left: -20, bottom: 0 }}>
            <CartesianGrid strokeDasharray="3 3" vertical={false} />
            <XAxis dataKey="name" tickLine={false} axisLine={false} />
            <YAxis tickLine={false} axisLine={false} />
            <Tooltip formatter={(value) => `${Number(value).toLocaleString()} g`} />
            <Bar dataKey="target" name="목표" radius={[8, 8, 0, 0]} fill="#cbd5e1" />
            <Bar dataKey="consumed" name="섭취" radius={[8, 8, 0, 0]} fill="#10b981" />
          </BarChart>
        </ResponsiveContainer>
      </div>
    </Card>
  );
}
