/**
 * Reflection API client. Calls backend API (same host as sentiment API).
 * Set VITE_SENTIMENT_API_URL for both sentiment and reflection endpoints.
 */

import { RESEARCH_ID_KEY, workshopStorageGet } from './workshopStorage'

const API_BASE = (import.meta.env.VITE_SENTIMENT_API_URL || 'http://localhost:3000').replace(/\/$/, '')

export { RESEARCH_ID_KEY }

export function getStoredResearchId() {
  return (workshopStorageGet(RESEARCH_ID_KEY) || '').trim()
}

/**
 * Submit reflection to backend (appends to Google Sheet).
 */
export function reflect(userId, sectionId, items, confidence) {
  submitReflection(userId, sectionId, confidence, items);
}

export async function submitReflection(userId, sectionId, confidence, items) {
  const title = (userId || '').trim();
  if (!title || !items?.length) return;
  try {
    const res = await fetch(`${API_BASE}/reflect`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        userId: title,
        sectionId: sectionId || '',
        confidence: confidence != null ? confidence : undefined,
        items: items.map((it) => ({
          itemId: it.itemId,
          question: it.question,
          response: it.response,
        })),
      }),
    });
    if (!res.ok) {
      const err = await res.json().catch(() => ({ error: res.statusText }));
      throw new Error(err.error || err.message || `Request failed: ${res.status}`);
    }
  } catch (e) {
    console.error('[reflectionApi] submitReflection failed:', e?.message ?? e);
    throw e;
  }
}
