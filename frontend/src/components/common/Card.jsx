export default function Card({ title, children }) {
  return (
    <div className="bg-white rounded-2xl shadow-sm p-5">
      {title && (
        <p className="text-sm text-slate-500 mb-3">
          {title}
        </p>
      )}

      {children}
    </div>
  );
}