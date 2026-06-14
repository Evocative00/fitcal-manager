import { formatGram, formatKcal } from "../../utils/formatters";

const mealTypeLabels = {
  BREAKFAST: "아침",
  LUNCH: "점심",
  DINNER: "저녁",
  SNACK: "간식",
};

export default function MealRecordItem({ meal, onEdit, onDelete }) {
  const mealTypeLabel = meal.mealTypeLabel || mealTypeLabels[meal.mealType] || meal.mealType;

  return (
    <li className="rounded-2xl border border-slate-200 bg-slate-50/70 p-5 transition hover:border-emerald-200 hover:bg-white hover:shadow-sm">
      <div className="flex flex-col gap-4 md:flex-row md:items-start md:justify-between">
        <div>
          <div className="mb-2 flex flex-wrap items-center gap-2">
            <span className="rounded-full bg-emerald-100 px-3 py-1 text-xs font-black text-emerald-700">
              {mealTypeLabel}
            </span>
            <span className="text-sm font-medium text-slate-500">{meal.recordedDate}</span>
          </div>

          <h3 className="text-lg font-black text-slate-950">{meal.foodName}</h3>
          <div className="mt-3 grid grid-cols-2 gap-2 text-sm text-slate-600 sm:grid-cols-4">
            <span className="rounded-xl bg-white px-3 py-2 font-semibold">{formatKcal(meal.calories)}</span>
            <span className="rounded-xl bg-white px-3 py-2 font-semibold">탄 {formatGram(meal.carbsG)}</span>
            <span className="rounded-xl bg-white px-3 py-2 font-semibold">단 {formatGram(meal.proteinG)}</span>
            <span className="rounded-xl bg-white px-3 py-2 font-semibold">지 {formatGram(meal.fatG)}</span>
          </div>
        </div>

        <div className="flex shrink-0 gap-2">
          <button
            type="button"
            onClick={() => onEdit(meal)}
            className="rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm font-bold text-slate-600 transition hover:bg-slate-100"
          >
            수정
          </button>
          <button
            type="button"
            onClick={() => onDelete(meal.id)}
            className="rounded-xl border border-red-200 bg-white px-3 py-2 text-sm font-bold text-red-600 transition hover:bg-red-50"
          >
            삭제
          </button>
        </div>
      </div>
    </li>
  );
}
