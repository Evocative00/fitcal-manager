import { BrowserRouter, Routes, Route, NavLink, Link } from "react-router-dom";
import HomePage from "./pages/HomePage";
import ProfileFormPage from "./pages/ProfileFormPage";
import NutritionResultPage from "./pages/NutritionResultPage";
import DashboardPage from "./pages/DashboardPage";
import MealRecordPage from "./pages/MealRecordPage";

const navItems = [
  { to: "/profile", label: "프로필" },
  { to: "/result", label: "결과" },
  { to: "/meals", label: "식단 기록" },
  { to: "/dashboard", label: "대시보드" },
];

function navClassName({ isActive }) {
  return [
    "rounded-full px-3 py-2 text-sm font-semibold transition",
    isActive
      ? "bg-emerald-100 text-emerald-700"
      : "text-slate-600 hover:bg-slate-100 hover:text-slate-950",
  ].join(" ");
}

function Layout({ children }) {
  return (
    <div className="min-h-screen bg-slate-50 text-slate-950">
      <header className="sticky top-0 z-30 border-b border-slate-200/80 bg-white/90 backdrop-blur-xl">
        <nav className="mx-auto flex max-w-7xl flex-col gap-3 px-5 py-4 md:flex-row md:items-center md:justify-between md:px-8">
          <Link to="/" className="group flex items-center gap-3">
            <span className="grid h-10 w-10 place-items-center rounded-2xl bg-emerald-500 text-lg font-black text-white shadow-sm shadow-emerald-200 transition group-hover:scale-105">
              F
            </span>
            <span>
              <span className="block text-lg font-black tracking-tight text-slate-950">FitCal</span>
              <span className="block text-xs font-medium text-slate-500">개인 맞춤형 식단 매니저</span>
            </span>
          </Link>

          <div className="flex flex-wrap items-center gap-2">
            {navItems.map((item) => (
              <NavLink key={item.to} to={item.to} className={navClassName}>
                {item.label}
              </NavLink>
            ))}
          </div>
        </nav>
      </header>

      {children}

      <footer className="border-t border-slate-200 bg-white px-5 py-8 text-center text-sm text-slate-500">
        <p className="font-semibold text-slate-700">FitCal Manager</p>
        <p className="mt-1">Strategy 패턴 기반 권장량 계산과 식단 기록 대시보드</p>
      </footer>
    </div>
  );
}

export default function App() {
  return (
    <BrowserRouter>
      <Layout>
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/profile" element={<ProfileFormPage />} />
          <Route path="/result" element={<NutritionResultPage />} />
          <Route path="/meals" element={<MealRecordPage />} />
          <Route path="/dashboard" element={<DashboardPage />} />
        </Routes>
      </Layout>
    </BrowserRouter>
  );
}
