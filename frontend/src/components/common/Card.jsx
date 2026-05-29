export default function Card({ title, description, children, className = "" }) {
  return (
    <section className={`bg-white rounded-lg border border-slate-200 p-5 shadow-sm ${className}`}>
      {(title || description) && (
        <header className="mb-4">
          {title && (
            <h2 className="text-sm font-semibold text-slate-700">
              {title}
            </h2>
          )}
          {description && (
            <p className="mt-1 text-sm text-slate-500">
              {description}
            </p>
          )}
        </header>
      )}

      {children}
    </section>
  );
}
