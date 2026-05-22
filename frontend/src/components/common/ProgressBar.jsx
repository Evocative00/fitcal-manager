export default function ProgressBar({ value, max }) {
  const percentage = (value / max) * 100;

  return (
    <div>
      <div className="w-full h-3 bg-slate-200 rounded-full overflow-hidden">
        <div
          className="h-full bg-emerald-500 rounded-full"
          style={{ width: `${percentage}%` }}
        />
      </div>

      <p className="text-sm text-slate-500 mt-1">
        {value} / {max}
      </p>
    </div>
  );
}