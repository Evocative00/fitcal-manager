import { useCallback, useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api from "../api/api";
import MealRecordForm from "../components/meals/MealRecordForm";
import MealRecordList from "../components/meals/MealRecordList";

function todayString() {
  return new Date().toISOString().slice(0, 10);
}

function getApiErrorMessage(error) {
  const data = error.response?.data;

  if (data?.details?.length > 0) {
    return data.details.join("\n");
  }

  return data?.message || "식단 API 호출 중 오류가 발생했습니다. 백엔드 실행 상태를 확인해주세요.";
}

function loadProfileId() {
  return localStorage.getItem("profileId");
}

function MissingProfileState() {
  return (
    <main className="min-h-screen bg-slate-50 px-6 py-10">
      <section className="mx-auto max-w-3xl rounded-2xl bg-white p-8 text-center shadow-sm">
        <h1 className="mb-3 text-2xl font-bold text-slate-900">
          먼저 프로필을 등록해주세요
        </h1>
        <p className="mb-6 text-slate-500">
          식단 기록은 localStorage의 profileId를 기준으로 저장됩니다.
        </p>
        <Link
          to="/profile"
          className="inline-flex rounded-xl bg-emerald-500 px-5 py-3 font-semibold text-white transition hover:bg-emerald-600"
        >
          프로필 입력하러 가기
        </Link>
      </section>
    </main>
  );
}

export default function MealRecordPage() {
  const profileId = loadProfileId();
  const [selectedDate, setSelectedDate] = useState(todayString());
  const [meals, setMeals] = useState([]);
  const [editingMeal, setEditingMeal] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");

  const fetchMeals = useCallback(async (date = selectedDate) => {
    if (!profileId) {
      return;
    }

    setIsLoading(true);
    setErrorMessage("");

    try {
      const response = await api.get("/meals", {
        params: {
          profileId,
          date,
        },
      });
      setMeals(response.data);
    } catch (error) {
      console.error(error);
      setErrorMessage(getApiErrorMessage(error));
    } finally {
      setIsLoading(false);
    }
  }, [profileId, selectedDate]);

  useEffect(() => {
    fetchMeals();
  }, [fetchMeals]);

  if (!profileId) {
    return <MissingProfileState />;
  }

  const handleSubmit = async (formValues) => {
    const request = {
      profileId: Number(profileId),
      ...formValues,
    };

    setIsSubmitting(true);
    setErrorMessage("");

    try {
      if (editingMeal) {
        await api.put(`/meals/${editingMeal.id}`, request);
        setEditingMeal(null);
      } else {
        await api.post("/meals", request);
      }

      setSelectedDate(request.recordedDate);
      await fetchMeals(request.recordedDate);
    } catch (error) {
      console.error(error);
      setErrorMessage(getApiErrorMessage(error));
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleDelete = async (mealId) => {
    const confirmed = window.confirm("선택한 식단 기록을 삭제할까요?");
    if (!confirmed) {
      return;
    }

    setErrorMessage("");

    try {
      await api.delete(`/meals/${mealId}`);
      if (editingMeal?.id === mealId) {
        setEditingMeal(null);
      }
      await fetchMeals();
    } catch (error) {
      console.error(error);
      setErrorMessage(getApiErrorMessage(error));
    }
  };

  const handleDateChange = (event) => {
    setSelectedDate(event.target.value);
    setEditingMeal(null);
  };

  return (
    <main className="min-h-screen bg-slate-50 px-6 py-10">
      <section className="mx-auto max-w-6xl">
        <div className="mb-6 flex flex-col gap-3 md:flex-row md:items-end md:justify-between">
          <div>
            <p className="mb-1 text-sm font-semibold text-emerald-600">
              profileId: {profileId}
            </p>
            <h1 className="text-2xl font-bold text-slate-900">식단 기록</h1>
            <p className="mt-2 text-slate-500">
              오늘 먹은 식단을 등록하고 날짜별 식단 목록을 관리합니다.
            </p>
          </div>

          <div className="flex flex-col gap-2 sm:flex-row sm:items-center">
            <input
              type="date"
              value={selectedDate}
              onChange={handleDateChange}
              className="rounded-xl border border-slate-200 bg-white px-4 py-2 text-slate-700"
            />
            <Link
              to="/dashboard"
              className="rounded-xl bg-slate-800 px-4 py-2 text-center font-semibold text-white transition hover:bg-slate-900"
            >
              대시보드 확인
            </Link>
          </div>
        </div>

        {errorMessage && (
          <div className="mb-5 whitespace-pre-line rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
            {errorMessage}
          </div>
        )}

        <div className="grid grid-cols-1 gap-6 lg:grid-cols-[420px_1fr]">
          <MealRecordForm
            editingMeal={editingMeal}
            selectedDate={selectedDate}
            isSubmitting={isSubmitting}
            onSubmit={handleSubmit}
            onCancel={() => setEditingMeal(null)}
          />

          <MealRecordList
            meals={meals}
            isLoading={isLoading}
            onEdit={setEditingMeal}
            onDelete={handleDelete}
          />
        </div>
      </section>
    </main>
  );
}
