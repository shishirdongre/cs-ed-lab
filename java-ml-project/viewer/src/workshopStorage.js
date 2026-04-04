/**
 * Workshop UI persistence uses the browser Storage API (localStorage or sessionStorage),
 * not HTTP cookies.
 *
 * - Default `local`: survives browser restarts (facilitator test / returning learners).
 * - `session`: clears when the tab or window closes. Set VITE_WORKSHOP_STORAGE=session.
 */
const MODE = (import.meta.env.VITE_WORKSHOP_STORAGE || 'local').toLowerCase()

export const RESEARCH_ID_KEY = 'workshop_research_id'
export const COMPLETED_CHUNKS_KEY = 'workshop_completed_chunks'

function getStore() {
  if (typeof window === 'undefined') return null
  return MODE === 'session' ? window.sessionStorage : window.localStorage
}

export function workshopStorageGet(key) {
  try {
    return getStore()?.getItem(key) ?? null
  } catch {
    return null
  }
}

export function workshopStorageSet(key, value) {
  try {
    getStore()?.setItem(key, value)
  } catch {}
}

/**
 * Removes workshop keys from **both** local and session storage so nothing leaks
 * when switching VITE_WORKSHOP_STORAGE or using “start fresh”.
 */
export function clearWorkshopStoredState() {
  for (const key of [RESEARCH_ID_KEY, COMPLETED_CHUNKS_KEY]) {
    try {
      localStorage.removeItem(key)
      sessionStorage.removeItem(key)
    } catch {}
  }
}
