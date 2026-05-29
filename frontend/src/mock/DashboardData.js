export const defaultConsumedNutrition = {
  consumedCalories: 0,
  consumedCarbs: 0,
  consumedProtein: 0,
  consumedFat: 0,
};

export const emptyMealSummary = {
  mealCount: 0,
  meals: [],
};

function toSafeNumber(value) {
  const numberValue = Number(value);
  return Number.isFinite(numberValue) && numberValue > 0 ? Math.round(numberValue) : 0;
}

function getRate(consumed, target) {
  return target > 0 ? Math.round((consumed / target) * 100) : 0;
}

export function buildDashboardData({
  nutritionResult,
  profileId = null,
  consumed = defaultConsumedNutrition,
  mealSummary = emptyMealSummary,
}) {
  const target = {
    calories: toSafeNumber(nutritionResult?.targetCalories),
    carbs: toSafeNumber(nutritionResult?.targetCarbs),
    protein: toSafeNumber(nutritionResult?.targetProtein),
    fat: toSafeNumber(nutritionResult?.targetFat),
  };

  const consumedNutrition = {
    calories: toSafeNumber(consumed.consumedCalories),
    carbs: toSafeNumber(consumed.consumedCarbs),
    protein: toSafeNumber(consumed.consumedProtein),
    fat: toSafeNumber(consumed.consumedFat),
  };

  return {
    profileId,
    date: new Date().toISOString().slice(0, 10),
    target,
    consumed: consumedNutrition,
    remainingCalories: target.calories - consumedNutrition.calories,
    goalRate: getRate(consumedNutrition.calories, target.calories),
    nutrition: {
      carbs: {
        consumed: consumedNutrition.carbs,
        target: target.carbs,
        rate: getRate(consumedNutrition.carbs, target.carbs),
      },
      protein: {
        consumed: consumedNutrition.protein,
        target: target.protein,
        rate: getRate(consumedNutrition.protein, target.protein),
      },
      fat: {
        consumed: consumedNutrition.fat,
        target: target.fat,
        rate: getRate(consumedNutrition.fat, target.fat),
      },
    },
    meals: mealSummary.meals,
    mealCount: mealSummary.mealCount,
  };
}
