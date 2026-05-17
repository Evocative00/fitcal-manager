import { Link } from "react-router-dom";
import api from "../api/api";

export default function HomePage() {
  const checkServer = async () => {
    try {
      const response = await api.get("/health");
      alert(`서버 연결 성공: ${response.data.status}`);
    } catch (error) {
      alert("서버 연결 실패. 백엔드가 실행 중인지 확인해주세요.");
      console.error(error);
    }
  };

  return (
    <main className="min-h-screen bg-slate-50 flex items-center justify-center px-6">
      <section className="w-full max-w-3xl text-center">
        <p className="text-sm font-semibold text-emerald-600 mb-3">
          개인 맞춤형 식단 및 칼로리 관리 매니저
        </p>

        <h1 className="text-5xl font-bold text-slate-900 mb-4">
          FitCal Manager
        </h1>

        <p className="text-slate-600 text-lg mb-8">
          신체 정보와 목표를 입력하면 하루 권장 칼로리와 탄단지 권장량을 계산합니다.
        </p>

        <div className="flex flex-col sm:flex-row justify-center gap-3">
          <Link
            to="/profile"
            className="px-6 py-3 rounded-xl bg-emerald-500 text-white font-semibold hover:bg-emerald-600 transition"
          >
            내 권장량 계산하기
          </Link>

          <button
            onClick={checkServer}
            className="px-6 py-3 rounded-xl bg-slate-800 text-white font-semibold hover:bg-slate-900 transition"
          >
            서버 연결 확인
          </button>
        </div>
      </section>
    </main>
  );
}