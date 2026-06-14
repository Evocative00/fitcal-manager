import { useState } from "react";
import { Link } from "react-router-dom";
import api from "../api/api";
import PageHero from "../components/common/PageHero";
import StatusBanner from "../components/common/StatusBanner";

const flowCards = [
  {
    step: "01",
    title: "프로필 입력",
    description: "나이, 키, 몸무게, 성별, 활동량, 목표를 입력하고 개인 프로필을 저장합니다.",
  },
  {
    step: "02",
    title: "권장량 계산",
    description: "Strategy 패턴으로 목표별 칼로리와 탄단지 권장량을 계산합니다.",
  },
  {
    step: "03",
    title: "식단 기록",
    description: "오늘 먹은 음식을 등록하고 날짜별 식단 목록을 관리합니다.",
  },
  {
    step: "04",
    title: "대시보드 확인",
    description: "목표 대비 실제 섭취량과 진행률, 피드백 메시지를 확인합니다.",
  },
];

export default function HomePage() {
  const [serverStatus, setServerStatus] = useState(null);
  const [isChecking, setIsChecking] = useState(false);

  const checkServer = async () => {
    setIsChecking(true);
    setServerStatus(null);

    try {
      const response = await api.get("/health");
      setServerStatus({ type: "success", message: `서버 연결 성공: ${response.data.status}` });
    } catch (error) {
      setServerStatus({ type: "error", message: "서버 연결 실패. 백엔드가 실행 중인지 확인해주세요." });
      console.error(error);
    } finally {
      setIsChecking(false);
    }
  };

  return (
    <main className="px-5 py-10 md:px-8">
      <section className="mx-auto max-w-7xl space-y-8">
        <PageHero
          eyebrow="AI assisted nutrition planner"
          title="권장량 계산부터 식단 기록까지 한 번에 관리하세요"
          description="FitCal Manager는 사용자의 신체 정보와 목표를 바탕으로 하루 권장 섭취량을 계산하고, 실제 식단 기록을 대시보드에 연결합니다."
          actions={
            <>
              <Link
                to="/profile"
                className="rounded-2xl bg-emerald-500 px-6 py-3 text-center font-bold text-white shadow-sm shadow-emerald-200 transition hover:-translate-y-0.5 hover:bg-emerald-600"
              >
                내 권장량 계산하기
              </Link>
              <button
                type="button"
                onClick={checkServer}
                disabled={isChecking}
                className="rounded-2xl border border-slate-200 bg-white px-6 py-3 font-bold text-slate-700 transition hover:-translate-y-0.5 hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-60"
              >
                {isChecking ? "확인 중..." : "서버 연결 확인"}
              </button>
            </>
          }
        >
          {serverStatus && (
            <StatusBanner type={serverStatus.type}>
              {serverStatus.message}
            </StatusBanner>
          )}
        </PageHero>

        <div className="grid grid-cols-1 gap-4 md:grid-cols-4">
          {flowCards.map((card) => (
            <article
              key={card.step}
              className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm transition hover:-translate-y-1 hover:shadow-md"
            >
              <p className="text-sm font-black text-emerald-500">{card.step}</p>
              <h2 className="mt-3 text-lg font-black text-slate-950">{card.title}</h2>
              <p className="mt-2 text-sm leading-relaxed text-slate-500">{card.description}</p>
            </article>
          ))}
        </div>

        <section className="grid grid-cols-1 gap-4 lg:grid-cols-3">
          <div className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm lg:col-span-2">
            <h2 className="text-xl font-black text-slate-950">현재 구현된 핵심 기능</h2>
            <div className="mt-5 grid grid-cols-1 gap-3 sm:grid-cols-2">
              {[
                "프로필 저장 API",
                "Strategy 패턴 권장량 계산",
                "식단 기록 CRUD",
                "하루 섭취량 요약 API",
                "목표 대비 대시보드",
                "로컬 저장 기반 사용자 흐름",
              ].map((feature) => (
                <div key={feature} className="rounded-2xl bg-slate-50 px-4 py-3 text-sm font-semibold text-slate-700">
                  ✓ {feature}
                </div>
              ))}
            </div>
          </div>

          <div className="rounded-3xl border border-emerald-100 bg-emerald-500 p-6 text-white shadow-sm">
            <p className="text-sm font-bold text-emerald-100">추천 진행 순서</p>
            <ol className="mt-4 space-y-3 text-sm font-semibold">
              <li>1. 프로필 입력</li>
              <li>2. 권장량 결과 확인</li>
              <li>3. 식단 기록 등록</li>
              <li>4. 대시보드에서 진행률 확인</li>
            </ol>
            <Link
              to="/meals"
              className="mt-6 inline-flex rounded-2xl bg-white px-5 py-3 font-bold text-emerald-700 transition hover:bg-emerald-50"
            >
              식단 기록으로 이동
            </Link>
          </div>
        </section>
      </section>
    </main>
  );
}
