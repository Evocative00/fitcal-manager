import Card from "../common/Card";
import ProgressBar from "../common/ProgressBar";

const NUTRIENTS = [
  { key: "carbs", label: "탄수화물" },
  { key: "protein", label: "단백질" },
  { key: "fat", label: "지방" },
];

export default function NutritionProgress({ nutrition }) {
  return (
    <Card
      title="탄단지 목표"
      description="오늘 섭취량과 계산된 권장 목표를 비교합니다."
    >
      <div className="space-y-5">
        {NUTRIENTS.map(({ key, label }) => {
          const item = nutrition[key] || { consumed: 0, target: 0, rate: 0 };

          return (
            <div key={key}>
              <div className="mb-2 flex items-center justify-between gap-3">
                <span className="font-medium text-slate-700">
                  {label}
                </span>
                <span className="shrink-0 text-sm text-slate-500">
                  {item.consumed.toLocaleString()}g / {item.target.toLocaleString()}g
                </span>
              </div>

              <ProgressBar
                value={item.consumed}
                max={item.target}
                unit="g"
              />

              <p className={`mt-1 text-sm ${item.rate > 100 ? "text-red-500" : "text-slate-500"}`}>
                목표 대비 {item.rate}%
              </p>
            </div>
          );
        })}
      </div>
    </Card>
  );
}
