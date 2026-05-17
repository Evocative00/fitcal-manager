import { BrowserRouter, Routes, Route, Link } from "react-router-dom";
import HomePage from "./pages/HomePage";
import ProfileFormPage from "./pages/ProfileFormPage";
import NutritionResultPage from "./pages/NutritionResultPage";
import DashboardPage from "./pages/DashboardPage";

function Layout({ children }) {
  return (
    <div>
      <header className="bg-white border-b">
        <nav className="max-w-6xl mx-auto px-6 py-4 flex items-center justify-between">
          <Link to="/" className="font-bold text-xl text-emerald-600">
            FitCal
          </Link>

          <div className="flex gap-4 text-sm text-slate-600">
            <Link to="/profile">프로필</Link>
            <Link to="/result">결과</Link>
            <Link to="/dashboard">대시보드</Link>
          </div>
        </nav>
      </header>

      {children}
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
          <Route path="/dashboard" element={<DashboardPage />} />
        </Routes>
      </Layout>
    </BrowserRouter>
  );
}