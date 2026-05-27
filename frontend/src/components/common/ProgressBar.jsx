export default function ProgressBar({ value = 0, max = 0 }) {
  const safeValue = Number.isFinite(Number(value)) ? Number(value) : 0;
  const safeMax = Number.isFinite(Number(max)) ? Number(max) : 0;
  const percentage = safeMax > 0 ? Math.min((safeValue / safeMax) * 100, 100) : 0;

  return (
    <div>
      <div className="w-full h-3 bg-slate-200 rounded-full overflow-hidden">
        <div
          className="h-full bg-emerald-500 rounded-full"
          style={{ width: `${percentage}%` }}
        />
      </div>

      <p className="text-sm text-slate-500 mt-1">
        {safeValue} / {safeMax}
      </p>
    </div>
  );
}
