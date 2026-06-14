import MealRecordItem from "./MealRecordItem";

export default function MealRecordList({ meals, isLoading, onEdit, onDelete }) {
  if (isLoading) {
    return (
      <section className="rounded-2xl border border-slate-200 bg-white p-6 text-center text-slate-500 shadow-sm">
        식단 목록을 불러오는 중입니다.
      </section>
    );
  }

  if (meals.length === 0) {
    return (
      <section className="rounded-2xl border border-dashed border-slate-300 bg-white p-8 text-center shadow-sm">
        <h2 className="text-lg font-bold text-slate-900">아직 등록된 식단이 없습니다</h2>
        <p className="mt-2 text-sm text-slate-500">
          왼쪽 입력 폼에서 오늘 먹은 음식을 등록하면 이곳에 표시됩니다.
        </p>
      </section>
    );
  }

  return (
    <section>
      <div className="mb-4 flex items-center justify-between gap-3">
        <h2 className="text-lg font-bold text-slate-900">식단 목록</h2>
        <span className="rounded-full bg-slate-100 px-3 py-1 text-sm font-semibold text-slate-600">
          {meals.length}건
        </span>
      </div>

      <ul className="space-y-3">
        {meals.map((meal) => (
          <MealRecordItem
            key={meal.id}
            meal={meal}
            onEdit={onEdit}
            onDelete={onDelete}
          />
        ))}
      </ul>
    </section>
  );
}
