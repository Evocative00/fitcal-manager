import { useEffect, useState } from "react";

const mealTypeOptions = [
  { value: "BREAKFAST", label: "아침" },
  { value: "LUNCH", label: "점심" },
  { value: "DINNER", label: "저녁" },
  { value: "SNACK", label: "간식" },
];

const emptyForm = {
  foodName: "",
  mealType: "BREAKFAST",
  calories: "",
  proteinG: "",
  carbsG: "",
  fatG: "",
  recordedDate: new Date().toISOString().slice(0, 10),
};

function buildFormFromMeal(meal) {
  if (!meal) {
    return emptyForm;
  }

  return {
    foodName: meal.foodName || "",
    mealType: meal.mealType || "BREAKFAST",
    calories: String(meal.calories ?? ""),
    proteinG: String(meal.proteinG ?? ""),
    carbsG: String(meal.carbsG ?? ""),
    fatG: String(meal.fatG ?? ""),
    recordedDate: meal.recordedDate || new Date().toISOString().slice(0, 10),
  };
}

function toNumber(value) {
  return Number(value);
}

export default function MealRecordForm({ editingMeal, selectedDate, isSubmitting, onSubmit, onCancel }) {
  const [form, setForm] = useState({ ...emptyForm, recordedDate: selectedDate });
  const [validationMessage, setValidationMessage] = useState("");

  useEffect(() => {
    if (editingMeal) {
      setForm(buildFormFromMeal(editingMeal));
      setValidationMessage("");
      return;
    }

    setForm({ ...emptyForm, recordedDate: selectedDate });
    setValidationMessage("");
  }, [editingMeal, selectedDate]);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setForm((prev) => ({ ...prev, [name]: value }));
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
    <section className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
      <div className="mb-5 flex items-center justify-between gap-3">
        <div>
          <h2 className="text-lg font-bold text-slate-900">
            {editingMeal ? "식단 수정" : "식단 등록"}
          </h2>
          <p className="mt-1 text-sm text-slate-500">
            칼로리와 탄수화물, 단백질, 지방 섭취량을 기록합니다.
          </p>
        </div>

        {editingMeal && (
          <button
            type="button"
            onClick={onCancel}
            className="rounded-lg border border-slate-200 px-3 py-2 text-sm font-semibold text-slate-600 transition hover:bg-slate-100"
          >
            수정 취소
          </button>
        )}
      </div>

      {validationMessage && (
        <div className="mb-4 rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
          {validationMessage}
        </div>
      )}

      <form onSubmit={handleSubmit} className="grid grid-cols-1 gap-4 md:grid-cols-2">
        <input
          name="foodName"
          value={form.foodName}
          onChange={handleChange}
          className="rounded-xl border px-4 py-3"
          placeholder="음식명"
        />

        <select
          name="mealType"
          value={form.mealType}
          onChange={handleChange}
          className="rounded-xl border px-4 py-3"
        >
          {mealTypeOptions.map((option) => (
            <option key={option.value} value={option.value}>
              {option.label}
            </option>
          ))}
        </select>

        <input
          name="recordedDate"
          type="date"
          value={form.recordedDate}
          onChange={handleChange}
          className="rounded-xl border px-4 py-3"
        />

        <input
          name="calories"
          type="number"
          min="1"
          step="0.1"
          value={form.calories}
          onChange={handleChange}
          className="rounded-xl border px-4 py-3"
          placeholder="칼로리(kcal)"
        />

        <input
          name="carbsG"
          type="number"
          min="0"
          step="0.1"
          value={form.carbsG}
          onChange={handleChange}
          className="rounded-xl border px-4 py-3"
          placeholder="탄수화물(g)"
        />

        <input
          name="proteinG"
          type="number"
          min="0"
          step="0.1"
          value={form.proteinG}
          onChange={handleChange}
          className="rounded-xl border px-4 py-3"
          placeholder="단백질(g)"
        />

        <input
          name="fatG"
          type="number"
          min="0"
          step="0.1"
          value={form.fatG}
          onChange={handleChange}
          className="rounded-xl border px-4 py-3 md:col-span-2"
          placeholder="지방(g)"
        />

        <button
          type="submit"
          disabled={isSubmitting}
          className="rounded-xl bg-emerald-500 py-3 font-semibold text-white transition hover:bg-emerald-600 disabled:cursor-not-allowed disabled:bg-slate-300 md:col-span-2"
        >
          {isSubmitting ? "저장 중..." : editingMeal ? "식단 수정하기" : "식단 등록하기"}
        </button>
      </form>
    </section>
  );
}
