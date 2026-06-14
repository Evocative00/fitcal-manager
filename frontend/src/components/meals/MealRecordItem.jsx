const mealTypeLabels = {
  BREAKFAST: "아침",
  LUNCH: "점심",
  DINNER: "저녁",
  SNACK: "간식",
};

function formatNumber(value) {
  return Number(value || 0).toLocaleString();
}

export default function MealRecordItem({ meal, onEdit, onDelete }) {
  const mealTypeLabel = meal.mealTypeLabel || mealTypeLabels[meal.mealType] || meal.mealType;

  return (
    <li className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
      <div className="flex flex-col gap-4 md:flex-row md:items-start md:justify-between">
        <div>
          <div className="mb-2 flex flex-wrap items-center gap-2">
            <span className="rounded-full bg-emerald-50 px-3 py-1 text-xs font-semibold text-emerald-700">
              {mealTypeLabel}
            </span>
            <span className="text-sm text-slate-500">{meal.recordedDate}</span>
          </div>

          <h3 className="text-lg font-bold text-slate-900">{meal.foodName}</h3>
          <p className="mt-2 text-sm text-slate-500">
            {formatNumber(meal.calories)} kcal · 탄수화물 {formatNumber(meal.carbsG)}g · 단백질 {formatNumber(meal.proteinG)}g · 지방 {formatNumber(meal.fatG)}g
          </p>
        </div>

        <div className="flex gap-2">
          <button
            type="button"
            onClick={() => onEdit(meal)}
            className="rounded-lg border border-slate-200 px-3 py-2 text-sm font-semibold text-slate-600 transition hover:bg-slate-100"
          >
            수정
          </button>
          <button
            type="button"
            onClick={() => onDelete(meal.id)}
            className="rounded-lg border border-red-200 px-3 py-2 text-sm font-semibold text-red-600 transition hover:bg-red-50"
          >
            삭제
          </button>
        </div>
      </div>
    </li>
  );
}
