import { useState } from 'react'
import { submitReflection } from './reflectionApi'
import { getReflectionForChunk } from './reflectionQuestions'
import { ALLOW_EMPTY_REFLECTION_FOR_TESTING } from './reflectionConfig'
import './SectionReflectionForm.css'

const DEFAULT_MIN_LENGTH = 75

function normalizeItems(items) {
  if (typeof items === 'string') {
    return [{ item_id: ' ', question: items }]
  }
  return Array.isArray(items) ? items : []
}

export default function SectionReflectionForm({ chunk, userId, onReflectSuccess, onGoToNext, hasNext }) {
  const [values, setValues] = useState({})
  const [confidence, setConfidence] = useState(null)
  const [status, setStatus] = useState('')
  const [lastSubmitted, setLastSubmitted] = useState({})

  const reflection = getReflectionForChunk(chunk)
  if (!reflection) return null

  const sectionId = reflection.sectionId ?? ''
  const items = normalizeItems(reflection.items ?? [])
  const minLength = reflection.minLength ?? DEFAULT_MIN_LENGTH
  const showConfidence = reflection.showConfidence ?? true

  const key = (itemId) => (sectionId ? `${sectionId}_${itemId}` : itemId)
  const lengths = items.map((it) => (values[key(it.item_id)] || '').trim().length)
  const allValid = items.length > 0 && lengths.every((L) => L >= minLength)
  const trimmedUserId = (userId || '').trim()
  const textOk = ALLOW_EMPTY_REFLECTION_FOR_TESTING || allValid
  const confidenceOk = !showConfidence || confidence != null
  const canSubmit = Boolean(trimmedUserId && textOk && confidenceOk)

  const setValue = (itemId, text) => {
    setValues((prev) => ({ ...prev, [key(itemId)]: text }))
  }

  const handleSubmit = () => {
    if (!trimmedUserId) {
      setStatus('Please set your Research ID first.')
      return
    }
    if (!ALLOW_EMPTY_REFLECTION_FOR_TESTING && !allValid) {
      setStatus('Please reach the minimum character count.')
      return
    }
    if (showConfidence && confidence == null) {
      setStatus('Please select a confidence level before submitting.')
      return
    }
    const apiItems = items.map((it) => ({
      itemId: it.item_id,
      question: it.question,
      response: (values[key(it.item_id)] || '').trim(),
    }))
    submitReflection(trimmedUserId, sectionId, showConfidence ? confidence : undefined, apiItems)
    const nextLast = { ...lastSubmitted }
    items.forEach((it) => {
      nextLast[key(it.item_id)] = (values[key(it.item_id)] || '').trim()
    })
    setLastSubmitted(nextLast)
    setStatus('Submitted.')
    onReflectSuccess?.(chunk?.id)
  }

  return (
    <div className="section-reflection-form">
      <h4 className="section-reflection-title">{reflection.title}</h4>
      {reflection.preamble && (
        <div className="section-reflection-preamble">{reflection.preamble}</div>
      )}
      {items.map((it) => {
        const k = key(it.item_id)
        const val = values[k] ?? ''
        const len = val.trim().length
        const saved = lastSubmitted[k]
        const isSaved = saved !== undefined && val.trim() === saved
        return (
          <div key={it.item_id} className="section-reflection-item">
            <label className="section-reflection-question">
              {it.question}
              {isSaved && <span className="section-reflection-saved" aria-label="Saved">✓</span>}
            </label>
            <textarea
              placeholder={`Type here... (min ${minLength} chars)`}
              value={val}
              onChange={(e) => setValue(it.item_id, e.target.value)}
              className="section-reflection-textarea"
              rows={3}
            />
            <span className={`section-reflection-counter ${len >= minLength ? 'valid' : ''}`}>
              {len} / {minLength}
            </span>
          </div>
        )
      })}
      {showConfidence && (
        <div className="section-reflection-confidence">
          <label className="section-reflection-confidence-label">Confidence (1 to 5):</label>
          <div className="section-reflection-confidence-options">
            {[1, 2, 3, 4, 5].map((n) => (
              <label key={n} className="section-reflection-radio">
                <input
                  type="radio"
                  name={`confidence-${sectionId}`}
                  value={n}
                  checked={confidence === n}
                  onChange={() => setConfidence(n)}
                />
                {n}
              </label>
            ))}
          </div>
        </div>
      )}
      <button
        type="button"
        className="section-reflection-submit"
        disabled={!canSubmit}
        onClick={handleSubmit}
      >
        {hasNext ? 'Submit and Next' : 'Submit'}
      </button>
      {status && (
        <p className={`section-reflection-status ${!trimmedUserId ? 'warning' : ''}`}>{status}</p>
      )}
    </div>
  )
}
