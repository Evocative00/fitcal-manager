export function loadJsonFromStorage(key) {
  try {
    const storedValue = localStorage.getItem(key);
    return storedValue ? JSON.parse(storedValue) : null;
  } catch (error) {
    console.error(`${key} 파싱 실패`, error);
    return null;
  }
}

export function saveJsonToStorage(key, value) {
  localStorage.setItem(key, JSON.stringify(value));
}

export function loadProfileId() {
  return localStorage.getItem("profileId");
}
