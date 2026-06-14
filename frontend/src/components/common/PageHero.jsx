export default function PageHero({ eyebrow, title, description, actions, children }) {
  return (
    <section className="relative overflow-hidden rounded-3xl border border-emerald-100 bg-gradient-to-br from-emerald-50 via-white to-sky-50 px-6 py-8 shadow-sm md:px-8">
      <div className="absolute -right-16 -top-16 h-44 w-44 rounded-full bg-emerald-200/40 blur-3xl" />
      <div className="absolute -bottom-20 left-1/3 h-40 w-40 rounded-full bg-sky-200/40 blur-3xl" />

      <div className="relative flex flex-col gap-6 lg:flex-row lg:items-end lg:justify-between">
        <div className="max-w-2xl">
          {eyebrow && (
            <p className="mb-2 text-sm font-bold uppercase tracking-[0.2em] text-emerald-600">
              {eyebrow}
            </p>
          )}
          <h1 className="text-3xl font-black tracking-tight text-slate-950 md:text-4xl">
            {title}
          </h1>
          {description && (
            <p className="mt-3 text-base leading-relaxed text-slate-600">
              {description}
            </p>
          )}
        </div>

        {actions && (
          <div className="flex flex-col gap-2 sm:flex-row">
            {actions}
          </div>
        )}
      </div>

      {children && <div className="relative mt-6">{children}</div>}
    </section>
  );
}
