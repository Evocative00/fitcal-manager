import MealRecordItem from "./MealRecordItem";

export default function MealRecordList({ meals, isLoading, onEdit, onDelete }) {
  if (isLoading) {
    return (
      <section className="rounded-3xl border border-slate-200 bg-white p-8 text-center text-slate-500 shadow-sm">
        <div className="mx-auto mb-4 h-10 w-10 animate-pulse rounded-full bg-emerald-100" />
        식단 목록을 불러오는 중입니다.
      </section>
    );
  }

  if (meals.length === 0) {
    return (
      <section className="rounded-3xl border border-dashed border-slate-300 bg-white p-10 text-center shadow-sm">
        <p className="text-sm font-bold text-emerald-600">empty meal log</p>
        <h2 className="mt-2 text-xl font-black text-slate-950">아직 등록된 식단이 없습니다</h2>
        <p className="mt-3 text-sm leading-relaxed text-slate-500">
          왼쪽 입력 폼에서 오늘 먹은 음식을 등록하면 이곳에 표시되고 대시보드에 합산됩니다.
        </p>
      </section>
    );
  }

  return (
    <section className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">
      <div className="mb-5 flex items-center justify-between gap-3">
        <div>
          <p className="text-xs font-bold uppercase tracking-[0.2em] text-emerald-600">daily list</p>
          <h2 className="mt-1 text-xl font-black text-slate-950">식단 목록</h2>
        </div>
        <span className="rounded-full bg-slate-100 px-3 py-1 text-sm font-bold text-slate-600">
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
