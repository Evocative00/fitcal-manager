import Card from "../common/Card";
import ProgressBar from "../common/ProgressBar";

export default function NutritionProgress({
  nutrition,
}) {
  return (
    <Card title="탄단지 진행률">
      <div className="space-y-5">
        <div>
          <div className="flex justify-between mb-2">
            <span className="text-slate-700 font-medium">
              탄수화물
            </span>

            <span className="text-sm text-slate-500">
              {nutrition.carbs.current}g /{" "}
              {nutrition.carbs.target}g
            </span>
          </div>

          <ProgressBar
            value={nutrition.carbs.current}
            max={nutrition.carbs.target}
          />
        </div>

        <div>
          <div className="flex justify-between mb-2">
            <span className="text-slate-700 font-medium">
              단백질
            </span>

            <span className="text-sm text-slate-500">
              {nutrition.protein.current}g /{" "}
              {nutrition.protein.target}g
            </span>
          </div>

          <ProgressBar
            value={nutrition.protein.current}
            max={nutrition.protein.target}
          />
        </div>

        <div>
          <div className="flex justify-between mb-2">
            <span className="text-slate-700 font-medium">
              지방
            </span>

            <span className="text-sm text-slate-500">
              {nutrition.fat.current}g /{" "}
              {nutrition.fat.target}g
            </span>
          </div>

          <ProgressBar
            value={nutrition.fat.current}
            max={nutrition.fat.target}
          />
        </div>
      </div>
    </Card>
  );
}