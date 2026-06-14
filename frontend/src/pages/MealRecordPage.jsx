import { useCallback, useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";
import api from "../api/api";
import PageHero from "../components/common/PageHero";
import StatusBanner from "../components/common/StatusBanner";
import MetricCard from "../components/common/MetricCard";
import MealRecordForm from "../components/meals/MealRecordForm";
import MealRecordList from "../components/meals/MealRecordList";
import { formatGram, formatKcal, todayString } from "../utils/formatters";
import { loadProfileId } from "../utils/storage";

function getApiErrorMessage(error) {
  const data = error.response?.data;

  if (data?.details?.length > 0) {
    return data.details.join("\n");
  }

  return data?.message || "식단 API 호출 중 오류가 발생했습니다. 백엔드 실행 상태를 확인해주세요.";
}

function calculateTotals(meals) {
  return meals.reduce(
    (acc, meal) => ({
      calories: acc.calories + Number(meal.calories || 0),
      carbs: acc.carbs + Number(meal.carbsG || 0),
      protein: acc.protein + Number(meal.proteinG || 0),
      fat: acc.fat + Number(meal.fatG || 0),
    }),
    { calories: 0, carbs: 0, protein: 0, fat: 0 },
  );
}

function MissingProfileState() {
  return (
    <main className="px-5 py-10 md:px-8">
      <section className="mx-auto max-w-3xl rounded-3xl border border-slate-200 bg-white p-8 text-center shadow-sm">
        <p className="text-sm font-bold text-emerald-600">profileId 필요</p>
        <h1 className="mt-2 text-2xl font-black text-slate-950">
          먼저 프로필을 등록해주세요
        </h1>
        <p className="mt-3 text-slate-500">
          식단 기록은 localStorage의 profileId를 기준으로 저장됩니다.
        </p>
        <Link
          to="/profile"
          className="mt-6 inline-flex rounded-2xl bg-emerald-500 px-5 py-3 font-bold text-white transition hover:bg-emerald-600"
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
  const [formResetKey, setFormResetKey] = useState(0);
  const [isLoading, setIsLoading] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");
  const totals = useMemo(() => calculateTotals(meals), [meals]);

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
    const timerId = window.setTimeout(() => {
      fetchMeals();
    }, 0);

    return () => window.clearTimeout(timerId);
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
      setFormResetKey((prev) => prev + 1);
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
        setFormResetKey((prev) => prev + 1);
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
    setFormResetKey((prev) => prev + 1);
  };

  const handleCancelEdit = () => {
    setEditingMeal(null);
    setFormResetKey((prev) => prev + 1);
  };

  return (
    <main className="px-5 py-10 md:px-8">
      <section className="mx-auto max-w-7xl space-y-6">
        <PageHero
          eyebrow="week 3 meal records"
          title="오늘의 식단을 기록하고 대시보드에 바로 반영하세요"
          description="등록한 식단은 날짜별로 조회되며, 대시보드에서 목표 대비 실제 섭취량으로 자동 합산됩니다."
          actions={
            <>
              <input
                type="date"
                value={selectedDate}
                onChange={handleDateChange}
                className="rounded-2xl border border-slate-200 bg-white px-4 py-3 font-semibold text-slate-700 shadow-sm outline-none transition focus:border-emerald-400 focus:ring-4 focus:ring-emerald-100"
              />
              <Link
                to="/dashboard"
                className="rounded-2xl bg-slate-900 px-5 py-3 text-center font-bold text-white transition hover:-translate-y-0.5 hover:bg-slate-800"
              >
                대시보드 확인
              </Link>
            </>
          }
        >
          <div className="grid grid-cols-2 gap-3 md:grid-cols-5">
            <MetricCard label="profileId" value={profileId} helper="현재 저장된 프로필" />
            <MetricCard label="식단 수" value={`${meals.length}건`} helper={selectedDate} tone="emerald" />
            <MetricCard label="섭취 칼로리" value={formatKcal(totals.calories)} tone="sky" />
            <MetricCard label="탄수화물" value={formatGram(totals.carbs)} />
            <MetricCard label="단백질" value={formatGram(totals.protein)} />
          </div>
        </PageHero>

        {errorMessage && (
          <StatusBanner type="error" title="식단 API 오류">
            {errorMessage}
          </StatusBanner>
        )}

        <div className="grid grid-cols-1 gap-6 lg:grid-cols-[420px_1fr]">
          <MealRecordForm
            key={`${editingMeal?.id || "new"}-${selectedDate}-${formResetKey}`}
            editingMeal={editingMeal}
            selectedDate={selectedDate}
            isSubmitting={isSubmitting}
            onSubmit={handleSubmit}
            onCancel={handleCancelEdit}
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
