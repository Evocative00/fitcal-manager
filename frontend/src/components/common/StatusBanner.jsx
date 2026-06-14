const variants = {
  info: "border-sky-200 bg-sky-50 text-sky-800",
  success: "border-emerald-200 bg-emerald-50 text-emerald-800",
  warning: "border-amber-200 bg-amber-50 text-amber-800",
  error: "border-red-200 bg-red-50 text-red-800",
};

export default function StatusBanner({ type = "info", title, children }) {
  return (
    <div className={`rounded-2xl border px-4 py-3 text-sm ${variants[type] || variants.info}`}>
      {title && <p className="font-bold">{title}</p>}
      {children && <div className={title ? "mt-1 whitespace-pre-line" : "whitespace-pre-line"}>{children}</div>}
    </div>
  );
}
