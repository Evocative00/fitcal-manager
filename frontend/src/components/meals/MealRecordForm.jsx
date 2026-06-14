import { useState } from "react";
import { todayString } from "../../utils/formatters";

const mealTypeOptions = [
  { value: "BREAKFAST", label: "아침" },
  { value: "LUNCH", label: "점심" },
  { value: "DINNER", label: "저녁" },
  { value: "SNACK", label: "간식" },
];

const mealPresets = [
  {
    label: "닭가슴살 샐러드",
    values: { foodName: "닭가슴살 샐러드", mealType: "LUNCH", calories: 430, carbsG: 35, proteinG: 45, fatG: 14 },
  },
  {
    label: "현미밥 식단",
    values: { foodName: "현미밥과 닭가슴살", mealType: "DINNER", calories: 520, carbsG: 65, proteinG: 38, fatG: 12 },
  },
  {
    label: "단백질 간식",
    values: { foodName: "그릭요거트와 견과류", mealType: "SNACK", calories: 280, carbsG: 22, proteinG: 20, fatG: 12 },
  },
];

const emptyForm = {
  foodName: "",
  mealType: "BREAKFAST",
  calories: "",
  proteinG: "",
  carbsG: "",
  fatG: "",
  recordedDate: todayString(),
};

function buildFormFromMeal(meal, selectedDate) {
  if (!meal) {
    return { ...emptyForm, recordedDate: selectedDate };
  }

  return {
    foodName: meal.foodName || "",
    mealType: meal.mealType || "BREAKFAST",
    calories: String(meal.calories ?? ""),
    proteinG: String(meal.proteinG ?? ""),
    carbsG: String(meal.carbsG ?? ""),
    fatG: String(meal.fatG ?? ""),
    recordedDate: meal.recordedDate || selectedDate,
  };
}

function toNumber(value) {
  return Number(value);
}

export default function MealRecordForm({ editingMeal, selectedDate, isSubmitting, onSubmit, onCancel }) {
  const [form, setForm] = useState(() => buildFormFromMeal(editingMeal, selectedDate));
  const [validationMessage, setValidationMessage] = useState("");

  const handleChange = (event) => {
    const { name, value } = event.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const applyPreset = (preset) => {
    setValidationMessage("");
    setForm((prev) => ({
      ...prev,
      ...preset.values,
      calories: String(preset.values.calories),
      carbsG: String(preset.values.carbsG),
      proteinG: String(preset.values.proteinG),
      fatG: String(preset.values.fatG),
      recordedDate: prev.recordedDate || selectedDate,
    }));
  };

  const validate = () => {
    if (!form.foodName.trim()) {
      return "음식명을 입력해주세요.";
    }

    if (!form.mealType) {
      return "식사 유형을 선택해주세요.";
    }

    if (!form.recordedDate) {
      return "식단 날짜를 선택해주세요.";
    }

    const numericFields = [form.calories, form.proteinG, form.carbsG, form.fatG];
    if (numericFields.some((value) => String(value).trim() === "")) {
      return "칼로리와 탄단지를 모두 입력해주세요.";
    }

    const calories = toNumber(form.calories);
    const proteinG = toNumber(form.proteinG);
    const carbsG = toNumber(form.carbsG);
    const fatG = toNumber(form.fatG);

    if ([calories, proteinG, carbsG, fatG].some((value) => Number.isNaN(value))) {
      return "칼로리와 탄단지는 숫자로 입력해주세요.";
    }

    if (calories <= 0) {
      return "칼로리는 0보다 크게 입력해주세요.";
    }

    if ([proteinG, carbsG, fatG].some((value) => value < 0)) {
      return "탄수화물, 단백질, 지방은 0 이상으로 입력해주세요.";
    }

    return "";
  };

  const handleSubmit = (event) => {
    event.preventDefault();

    const message = validate();
    if (message) {
      setValidationMessage(message);
      return;
    }

    setValidationMessage("");
    onSubmit({
      foodName: form.foodName.trim(),
      mealType: form.mealType,
      calories: toNumber(form.calories),
      proteinG: toNumber(form.proteinG),
      carbsG: toNumber(form.carbsG),
      fatG: toNumber(form.fatG),
      recordedDate: form.recordedDate,
    });
  };

  return (
    <section className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">
      <div className="mb-5 flex items-start justify-between gap-3">
        <div>
          <p className="text-xs font-bold uppercase tracking-[0.2em] text-emerald-600">
            meal log
          </p>
          <h2 className="mt-2 text-xl font-black text-slate-950">
            {editingMeal ? "식단 수정" : "식단 등록"}
          </h2>
          <p className="mt-1 text-sm text-slate-500">
            자주 먹는 식단은 추천 버튼으로 빠르게 채울 수 있습니다.
          </p>
        </div>

        {editingMeal && (
          <button
            type="button"
            onClick={onCancel}
            className="rounded-xl border border-slate-200 px-3 py-2 text-sm font-bold text-slate-600 transition hover:bg-slate-100"
          >
            수정 취소
          </button>
        )}
      </div>

      {!editingMeal && (
        <div className="mb-5 flex flex-wrap gap-2">
          {mealPresets.map((preset) => (
            <button
              key={preset.label}
              type="button"
              onClick={() => applyPreset(preset)}
              className="rounded-full border border-emerald-100 bg-emerald-50 px-3 py-2 text-xs font-bold text-emerald-700 transition hover:bg-emerald-100"
            >
              + {preset.label}
            </button>
          ))}
        </div>
      )}

      {validationMessage && (
        <div className="mb-4 rounded-2xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
          {validationMessage}
        </div>
      )}

      <form onSubmit={handleSubmit} className="grid grid-cols-1 gap-4 md:grid-cols-2">
        <label className="md:col-span-2">
          <span className="mb-1 block text-sm font-semibold text-slate-700">음식명</span>
          <input
            name="foodName"
            value={form.foodName}
            onChange={handleChange}
            className="w-full rounded-2xl border border-slate-200 px-4 py-3 outline-none transition focus:border-emerald-400 focus:ring-4 focus:ring-emerald-100"
            placeholder="예: 닭가슴살 샐러드"
          />
        </label>

        <label>
          <span className="mb-1 block text-sm font-semibold text-slate-700">식사 유형</span>
          <select
            name="mealType"
            value={form.mealType}
            onChange={handleChange}
            className="w-full rounded-2xl border border-slate-200 px-4 py-3 outline-none transition focus:border-emerald-400 focus:ring-4 focus:ring-emerald-100"
          >
            {mealTypeOptions.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </label>

        <label>
          <span className="mb-1 block text-sm font-semibold text-slate-700">기록 날짜</span>
          <input
            name="recordedDate"
            type="date"
            value={form.recordedDate}
            onChange={handleChange}
            className="w-full rounded-2xl border border-slate-200 px-4 py-3 outline-none transition focus:border-emerald-400 focus:ring-4 focus:ring-emerald-100"
          />
        </label>

        <label>
          <span className="mb-1 block text-sm font-semibold text-slate-700">칼로리</span>
          <input
            name="calories"
            type="number"
            min="1"
            step="0.1"
            value={form.calories}
            onChange={handleChange}
            className="w-full rounded-2xl border border-slate-200 px-4 py-3 outline-none transition focus:border-emerald-400 focus:ring-4 focus:ring-emerald-100"
            placeholder="kcal"
          />
        </label>

        <label>
          <span className="mb-1 block text-sm font-semibold text-slate-700">탄수화물</span>
          <input
            name="carbsG"
            type="number"
            min="0"
            step="0.1"
            value={form.carbsG}
            onChange={handleChange}
            className="w-full rounded-2xl border border-slate-200 px-4 py-3 outline-none transition focus:border-emerald-400 focus:ring-4 focus:ring-emerald-100"
            placeholder="g"
          />
        </label>

        <label>
          <span className="mb-1 block text-sm font-semibold text-slate-700">단백질</span>
          <input
            name="proteinG"
            type="number"
            min="0"
            step="0.1"
            value={form.proteinG}
            onChange={handleChange}
            className="w-full rounded-2xl border border-slate-200 px-4 py-3 outline-none transition focus:border-emerald-400 focus:ring-4 focus:ring-emerald-100"
            placeholder="g"
          />
        </label>

        <label>
          <span className="mb-1 block text-sm font-semibold text-slate-700">지방</span>
          <input
            name="fatG"
            type="number"
            min="0"
            step="0.1"
            value={form.fatG}
            onChange={handleChange}
            className="w-full rounded-2xl border border-slate-200 px-4 py-3 outline-none transition focus:border-emerald-400 focus:ring-4 focus:ring-emerald-100"
            placeholder="g"
          />
        </label>

        <button
          type="submit"
          disabled={isSubmitting}
          className="rounded-2xl bg-emerald-500 py-3 font-black text-white shadow-sm shadow-emerald-200 transition hover:bg-emerald-600 disabled:cursor-not-allowed disabled:bg-slate-300 md:col-span-2"
        >
          {isSubmitting ? "저장 중..." : editingMeal ? "식단 수정하기" : "식단 등록하기"}
        </button>
      </form>
    </section>
  );
}
