import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/api";
import PageHero from "../components/common/PageHero";
import StatusBanner from "../components/common/StatusBanner";
import { saveJsonToStorage } from "../utils/storage";

const initialProfile = {
  name: "",
  age: "",
  heightCm: "",
  weightKg: "",
  gender: "",
  activityLevel: "",
  goalType: "",
};

const fieldClassName = "w-full rounded-2xl border border-slate-200 bg-white px-4 py-3 outline-none transition focus:border-emerald-400 focus:ring-4 focus:ring-emerald-100";

function getApiErrorMessage(error) {
  const data = error.response?.data;

  if (data?.details?.length > 0) {
    return data.details.join("\n");
  }

  return data?.message || "API 호출 중 오류가 발생했습니다. 백엔드 실행 상태를 확인해주세요.";
}

export default function ProfileFormPage() {
  const navigate = useNavigate();
  const [profile, setProfile] = useState(initialProfile);
  const [isLoading, setIsLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");

  const handleChange = (e) => {
    const { name, value } = e.target;

    setProfile((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const validateProfile = () => {
    const requiredFields = [
      ["name", "이름"],
      ["age", "나이"],
      ["heightCm", "키"],
      ["weightKg", "몸무게"],
      ["gender", "성별"],
      ["activityLevel", "활동량"],
      ["goalType", "목표"],
    ];

    const emptyField = requiredFields.find(([fieldName]) => !String(profile[fieldName]).trim());
    if (emptyField) {
      return `${emptyField[1]}을(를) 입력해주세요.`;
    }

    if (
      Number.isNaN(Number(profile.age)) ||
      Number.isNaN(Number(profile.heightCm)) ||
      Number.isNaN(Number(profile.weightKg))
    ) {
      return "나이, 키, 몸무게는 숫자로 입력해주세요.";
    }

    return "";
  };

  const buildProfileRequest = () => ({
    name: profile.name.trim(),
    age: Number(profile.age),
    heightCm: Number(profile.heightCm),
    weightKg: Number(profile.weightKg),
    gender: profile.gender,
    activityLevel: profile.activityLevel,
    goalType: profile.goalType,
  });

  const buildNutritionRequest = (request) => ({
    age: request.age,
    heightCm: request.heightCm,
    weightKg: request.weightKg,
    gender: request.gender,
    activityLevel: request.activityLevel,
    goalType: request.goalType,
  });

  const handleSubmit = async (e) => {
    e.preventDefault();

    const validationMessage = validateProfile();
    if (validationMessage) {
      setErrorMessage(validationMessage);
      return;
    }

    setIsLoading(true);
    setErrorMessage("");

    try {
      const profileRequest = buildProfileRequest();

      const profileResponse = await api.post("/profiles", profileRequest);
      localStorage.setItem("profileId", String(profileResponse.data.id));

      const nutritionRequest = buildNutritionRequest(profileRequest);
      const nutritionResponse = await api.post("/nutrition/calculate", nutritionRequest);
      saveJsonToStorage("nutritionResult", nutritionResponse.data);

      navigate("/result", {
        state: {
          profile: profileResponse.data,
          nutritionResult: nutritionResponse.data,
        },
      });
    } catch (error) {
      console.error(error);
      setErrorMessage(getApiErrorMessage(error));
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <main className="px-5 py-10 md:px-8">
      <section className="mx-auto max-w-5xl space-y-6">
        <PageHero
          eyebrow="profile setup"
          title="내 몸에 맞는 권장량을 계산합니다"
          description="입력한 정보는 프로필 저장 API와 권장량 계산 API에 순서대로 전달됩니다. 계산 결과는 결과 화면과 대시보드에서 함께 사용됩니다."
        >
          <div className="grid grid-cols-1 gap-3 md:grid-cols-3">
            <div className="rounded-2xl bg-white/80 px-4 py-3 text-sm font-semibold text-slate-600">1. 프로필 저장</div>
            <div className="rounded-2xl bg-white/80 px-4 py-3 text-sm font-semibold text-slate-600">2. 목표별 권장량 계산</div>
            <div className="rounded-2xl bg-white/80 px-4 py-3 text-sm font-semibold text-slate-600">3. 결과와 대시보드 반영</div>
          </div>
        </PageHero>

        <section className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm md:p-8">
          <div className="mb-6">
            <h2 className="text-xl font-black text-slate-950">기본 정보 입력</h2>
            <p className="mt-2 text-sm text-slate-500">
              API 고정 필드명은 name, age, heightCm, weightKg, gender, activityLevel, goalType입니다.
            </p>
          </div>

          {errorMessage && (
            <div className="mb-5">
              <StatusBanner type="error" title="입력 또는 API 오류">
                {errorMessage}
              </StatusBanner>
            </div>
          )}

          <form onSubmit={handleSubmit} className="grid grid-cols-1 gap-5 md:grid-cols-2">
            <label>
              <span className="mb-1 block text-sm font-semibold text-slate-700">이름</span>
              <input
                name="name"
                value={profile.name}
                onChange={handleChange}
                className={fieldClassName}
                placeholder="고상준"
              />
            </label>

            <label>
              <span className="mb-1 block text-sm font-semibold text-slate-700">나이</span>
              <input
                name="age"
                type="number"
                min="1"
                max="120"
                value={profile.age}
                onChange={handleChange}
                className={fieldClassName}
                placeholder="27"
              />
            </label>

            <label>
              <span className="mb-1 block text-sm font-semibold text-slate-700">키(cm)</span>
              <input
                name="heightCm"
                type="number"
                min="50"
                max="250"
                step="0.1"
                value={profile.heightCm}
                onChange={handleChange}
                className={fieldClassName}
                placeholder="172"
              />
            </label>

            <label>
              <span className="mb-1 block text-sm font-semibold text-slate-700">몸무게(kg)</span>
              <input
                name="weightKg"
                type="number"
                min="20"
                max="300"
                step="0.1"
                value={profile.weightKg}
                onChange={handleChange}
                className={fieldClassName}
                placeholder="70"
              />
            </label>

            <label>
              <span className="mb-1 block text-sm font-semibold text-slate-700">성별</span>
              <select
                name="gender"
                value={profile.gender}
                onChange={handleChange}
                className={fieldClassName}
              >
                <option value="">성별 선택</option>
                <option value="MALE">남성</option>
                <option value="FEMALE">여성</option>
              </select>
            </label>

            <label>
              <span className="mb-1 block text-sm font-semibold text-slate-700">활동량</span>
              <select
                name="activityLevel"
                value={profile.activityLevel}
                onChange={handleChange}
                className={fieldClassName}
              >
                <option value="">활동량 선택</option>
                <option value="LOW">낮음</option>
                <option value="NORMAL">보통</option>
                <option value="HIGH">높음</option>
                <option value="VERY_HIGH">매우 높음</option>
              </select>
            </label>

            <label className="md:col-span-2">
              <span className="mb-1 block text-sm font-semibold text-slate-700">목표</span>
              <select
                name="goalType"
                value={profile.goalType}
                onChange={handleChange}
                className={fieldClassName}
              >
                <option value="">목표 선택</option>
                <option value="DIET">다이어트</option>
                <option value="MAINTAIN">유지</option>
                <option value="BULK_UP">벌크업</option>
                <option value="HIGH_PROTEIN">고단백</option>
              </select>
            </label>

            <button
              type="submit"
              disabled={isLoading}
              className="rounded-2xl bg-emerald-500 py-4 font-black text-white shadow-sm shadow-emerald-200 transition hover:bg-emerald-600 disabled:cursor-not-allowed disabled:bg-slate-300 md:col-span-2"
            >
              {isLoading ? "저장하고 계산하는 중..." : "프로필 저장 후 권장량 계산하기"}
            </button>
          </form>
        </section>
      </section>
    </main>
  );
}
