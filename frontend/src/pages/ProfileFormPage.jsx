export default function ProfileFormPage() {
  return (
    <main className="min-h-screen bg-slate-50 px-6 py-10">
      <section className="max-w-3xl mx-auto bg-white rounded-2xl shadow-sm p-8">
        <h1 className="text-2xl font-bold text-slate-900 mb-2">
          프로필 입력
        </h1>

        <p className="text-slate-500 mb-8">
          키, 몸무게, 나이, 성별, 활동량, 목표를 입력해주세요.
        </p>

        <form className="grid grid-cols-1 md:grid-cols-2 gap-5">
          <input className="border rounded-xl px-4 py-3" placeholder="이름" />
          <input className="border rounded-xl px-4 py-3" placeholder="나이" />
          <input className="border rounded-xl px-4 py-3" placeholder="키(cm)" />
          <input className="border rounded-xl px-4 py-3" placeholder="몸무게(kg)" />

          <select className="border rounded-xl px-4 py-3">
            <option>성별 선택</option>
            <option value="MALE">남성</option>
            <option value="FEMALE">여성</option>
          </select>

          <select className="border rounded-xl px-4 py-3">
            <option>활동량 선택</option>
            <option value="LOW">낮음</option>
            <option value="NORMAL">보통</option>
            <option value="HIGH">높음</option>
            <option value="VERY_HIGH">매우 높음</option>
          </select>

          <select className="border rounded-xl px-4 py-3 md:col-span-2">
            <option>목표 선택</option>
            <option value="DIET">다이어트</option>
            <option value="MAINTAIN">유지</option>
            <option value="BULK_UP">벌크업</option>
            <option value="HIGH_PROTEIN">고단백</option>
          </select>

          <button
            type="button"
            className="md:col-span-2 bg-emerald-500 text-white rounded-xl py-3 font-semibold hover:bg-emerald-600 transition"
          >
            권장량 계산하기
          </button>
        </form>
      </section>
    </main>
  );
}