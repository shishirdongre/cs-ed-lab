/**
 * Sentiment analysis API client.
 * Calls the sentiment API endpoint directly.
 * Set VITE_SENTIMENT_API_URL in .env (e.g. http://localhost:3000) to configure.
 */

const API_BASE = (import.meta.env.VITE_SENTIMENT_API_URL || 'http://localhost:3000').replace(/\/$/, '')

export async function predictSentiment(review) {
  const res = await fetch(`${API_BASE}/predict`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ review: (review || '').trim() }),
  })
  if (!res.ok) {
    const err = await res.json().catch(() => ({ error: res.statusText }))
    throw new Error(err.error || err.message || `Request failed: ${res.status}`)
  }
  return res.json()
}
