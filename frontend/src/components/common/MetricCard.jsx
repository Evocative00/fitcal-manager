export default function MetricCard({ label, value, helper, tone = "default" }) {
  const toneClass = {
    default: "text-slate-950",
    emerald: "text-emerald-600",
    red: "text-red-500",
    sky: "text-sky-600",
    amber: "text-amber-600",
  }[tone] || "text-slate-950";

  return (
    <div className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm transition hover:-translate-y-0.5 hover:shadow-md">
      <p className="text-sm font-medium text-slate-500">{label}</p>
      <p className={`mt-2 text-2xl font-black ${toneClass}`}>{value}</p>
      {helper && <p className="mt-2 text-sm text-slate-500">{helper}</p>}
    </div>
  );
}
