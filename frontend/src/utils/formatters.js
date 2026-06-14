export function formatNumber(value, fractionDigits = 0) {
  const numberValue = Number(value);

  if (!Number.isFinite(numberValue)) {
    return "0";
  }

  return numberValue.toLocaleString(undefined, {
    maximumFractionDigits: fractionDigits,
  });
}

export function formatKcal(value) {
  return `${formatNumber(value)} kcal`;
}

export function formatGram(value) {
  return `${formatNumber(value)} g`;
}

export function clampRate(value) {
  const numberValue = Number(value);

  if (!Number.isFinite(numberValue)) {
    return 0;
  }

  return Math.min(Math.max(Math.round(numberValue), 0), 100);
}

export function todayString() {
  return new Date().toISOString().slice(0, 10);
}
