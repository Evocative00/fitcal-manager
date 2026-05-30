function toSafeNumber(value) {
  const numberValue = Number(value);
  return Number.isFinite(numberValue) && numberValue > 0 ? numberValue : 0;
}

export default function ProgressBar({ value = 0, max = 0, unit = "", showLabel = true }) {
  const safeValue = toSafeNumber(value);
  const safeMax = toSafeNumber(max);
  const rawPercentage = safeMax > 0 ? (safeValue / safeMax) * 100 : 0;
  const displayPercentage = Math.min(Math.max(rawPercentage, 0), 100);
  const isOver = rawPercentage > 100;

  return (
    <div>
      <div
        className="h-3 w-full overflow-hidden rounded-full bg-slate-200"
        role="progressbar"
        aria-valuenow={Math.round(displayPercentage)}
        aria-valuemin="0"
        aria-valuemax="100"
      >
        <div
          className={`h-full rounded-full transition-all ${isOver ? "bg-red-500" : "bg-emerald-500"}`}
          style={{ width: `${displayPercentage}%` }}
        />
      </div>

      {showLabel && (
        <p className="mt-1 text-sm text-slate-500">
          {safeValue.toLocaleString()} / {safeMax.toLocaleString()}
          {unit}
          {safeMax === 0 && " (목표 없음)"}
        </p>
      )}
    </div>
  );
}
