import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/api";

const initialProfile = {
  name: "",
  age: "",
  heightCm: "",
  weightKg: "",
  gender: "",
  activityLevel: "",
  goalType: "",
};

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

    if (Number.isNaN(Number(profile.age)) || Number.isNaN(Number(profile.heightCm)) || Number.isNaN(Number(profile.weightKg))) {
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
      localStorage.setItem("nutritionResult", JSON.stringify(nutritionResponse.data));

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
    <main className="min-h-screen bg-slate-50 px-6 py-10">
      <section className="max-w-3xl mx-auto bg-white rounded-2xl shadow-sm p-8">
        <h1 className="text-2xl font-bold text-slate-900 mb-2">
          프로필 입력
        </h1>

        <p className="text-slate-500 mb-8">
          키, 몸무게, 나이, 성별, 활동량, 목표를 입력하면 프로필 저장 후 권장량을 계산합니다.
        </p>

        {errorMessage && (
          <div className="mb-5 rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700 whitespace-pre-line">
            {errorMessage}
          </div>
        )}

        <form onSubmit={handleSubmit} className="grid grid-cols-1 md:grid-cols-2 gap-5">
          <input
            name="name"
            value={profile.name}
            onChange={handleChange}
            className="border rounded-xl px-4 py-3"
            placeholder="이름"
          />

          <input
            name="age"
            type="number"
            min="1"
            max="120"
            value={profile.age}
            onChange={handleChange}
            className="border rounded-xl px-4 py-3"
            placeholder="나이"
          />

          <input
            name="heightCm"
            type="number"
            min="50"
            max="250"
            step="0.1"
            value={profile.heightCm}
            onChange={handleChange}
            className="border rounded-xl px-4 py-3"
            placeholder="키(cm)"
          />

          <input
            name="weightKg"
            type="number"
            min="20"
            max="300"
            step="0.1"
            value={profile.weightKg}
            onChange={handleChange}
            className="border rounded-xl px-4 py-3"
            placeholder="몸무게(kg)"
          />

          <select
            name="gender"
            value={profile.gender}
            onChange={handleChange}
            className="border rounded-xl px-4 py-3"
          >
            <option value="">성별 선택</option>
            <option value="MALE">남성</option>
            <option value="FEMALE">여성</option>
          </select>

          <select
            name="activityLevel"
            value={profile.activityLevel}
            onChange={handleChange}
            className="border rounded-xl px-4 py-3"
          >
            <option value="">활동량 선택</option>
            <option value="LOW">낮음</option>
            <option value="NORMAL">보통</option>
            <option value="HIGH">높음</option>
            <option value="VERY_HIGH">매우 높음</option>
          </select>

          <select
            name="goalType"
            value={profile.goalType}
            onChange={handleChange}
            className="border rounded-xl px-4 py-3 md:col-span-2"
          >
            <option value="">목표 선택</option>
            <option value="DIET">다이어트</option>
            <option value="MAINTAIN">유지</option>
            <option value="BULK_UP">벌크업</option>
            <option value="HIGH_PROTEIN">고단백</option>
          </select>

          <button
            type="submit"
            disabled={isLoading}
            className="md:col-span-2 bg-emerald-500 text-white rounded-xl py-3 font-semibold hover:bg-emerald-600 transition disabled:cursor-not-allowed disabled:bg-slate-300"
          >
            {isLoading ? "계산 중..." : "권장량 계산하기"}
          </button>
        </form>
      </section>
    </main>
  );
}
