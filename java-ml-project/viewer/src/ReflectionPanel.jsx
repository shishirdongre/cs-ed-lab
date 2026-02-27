import { useState, useEffect, useRef } from 'react'
import { submitReflection } from './reflectionApi'
import './ReflectionPanel.css'
const DEFAULT_MIN_LENGTH = 75

function normalizeItems(items) {
  if (typeof items === 'string') {
    return [{ item_id: ' ', question: items }]
  }
  return Array.isArray(items) ? items : []
}

export default function ReflectionPanel({ currentChunk, userId, onReflectSuccess }) {
  const [status, setStatus] = useState('')
  const [values, setValues] = useState({}) // sectionId_itemId -> value
  const [lastSubmitted, setLastSubmitted] = useState({}) // sectionId_itemId -> value
  const [confidence, setConfidence] = useState(null)
  const panelScrollRef = useRef(null)

  useEffect(() => {
    if (panelScrollRef.current) {
      panelScrollRef.current.scrollTop = 0
    }
  }, [currentChunk?.id])

  const reflection = currentChunk?.reflection
  const sectionId = reflection?.sectionId ?? ''
  const items = reflection ? normalizeItems(reflection.items) : []
  const minLength = reflection?.minLength ?? DEFAULT_MIN_LENGTH
  const showConfidence = reflection?.showConfidence ?? false

  const key = (itemId) => (sectionId ? `${sectionId}_${itemId}` : itemId)
  const lengths = items.map((it) => (values[key(it.item_id)] || '').trim().length)
  const allValid = items.length > 0 && lengths.every((L) => L >= minLength)
  const trimmedUserId = (userId || '').trim()
  const canSubmit = allValid

  const makeCounterHtml = (n) => {
    const color = n >= minLength ? '#2e7d32' : '#b71c1c'
    return { __html: `<span class="reflection-counter" style="color:${color}">${n} / ${minLength} chars</span>` }
  }

  const handleSubmit = () => {
    if (!trimmedUserId) {
      setStatus('Please set your Research ID in the top right before submitting.')
      return
    }
    if (!allValid) {
      setStatus('Some answers are too short. Please reach the minimum character count.')
      return
    }
    const apiItems = items.map((it) => ({
      itemId: it.item_id,
      question: it.question,
      response: (values[key(it.item_id)] || '').trim(),
    }))
    submitReflection(
      trimmedUserId,
      sectionId,
      showConfidence ? confidence : undefined,
      apiItems
    )
    // No confirmation from API; update UI optimistically
    const nextLast = { ...lastSubmitted }
    items.forEach((it) => {
      nextLast[key(it.item_id)] = (values[key(it.item_id)] || '').trim()
    })
    setLastSubmitted(nextLast)
    setStatus('Submitted.')
    onReflectSuccess?.(currentChunk.id)
  }

  const setValue = (itemId, text) => {
    setValues((prev) => ({ ...prev, [key(itemId)]: text }))
  }

  if (!currentChunk) return null

  return (
    <aside className="reflection-panel" ref={panelScrollRef}>
      <div className="reflection-panel-inner">
        <h3 className="reflection-panel-heading">Reflection</h3>
        {!reflection ? (
          <p className="reflection-empty">No reflection for this chunk.</p>
        ) : (
          <div className="reflection-form">
            {reflection.title && <h3 className="reflection-form-title">{reflection.title}</h3>}
            {items.map((it) => {
              const k = key(it.item_id)
              const val = values[k] ?? ''
              const len = val.trim().length
              const saved = lastSubmitted[k]
              const isSaved = saved !== undefined && val.trim() === saved
              return (
                <div key={it.item_id} className="reflection-item">
                  <h4 className="reflection-question">
                    {isSaved && <span className="reflection-check" aria-label="Saved">✓</span>}
                    <span className="reflection-item-id">{it.item_id}</span>
                    {it.question}
                  </h4>
                  <textarea
                    placeholder={`Type your reflection here... (minimum ${minLength} characters)`}
                    value={val}
                    onChange={(e) => setValue(it.item_id, e.target.value)}
                    className="reflection-textarea"
                    rows={4}
                  />
                  <div className="reflection-meta">
                    <span dangerouslySetInnerHTML={makeCounterHtml(len)} />
                    <span className={`reflection-state ${isSaved ? 'saved' : 'unsaved'}`}>
                      {isSaved ? 'Saved' : 'Unsaved'}
                    </span>
                  </div>
                  {len > 0 && len < minLength && (
                    <p className="reflection-warning">Add {minLength - len} more characters.</p>
                  )}
                </div>
              )
            })}
            {showConfidence && (
              <div className="reflection-confidence">
                <p><b>How confident are you about your answer? </b><br></br> 1 - Not confident, 5 - Very confident </p>
                <div className="reflection-confidence-options">
                  {[1, 2, 3, 4, 5].map((n) => (
                    <label key={n} className="reflection-radio">
                      <input
                        type="radio"
                        name="confidence"
                        value={n}
                        checked={confidence === n}
                        onChange={() => setConfidence(n)}
                      />
                      {n === 1 ? '1 ' : n === 5 ? '5' : n}
                    </label>
                  ))}
                </div>
              </div>
            )}
            <button
              type="button"
              className="reflection-submit"
              disabled={!canSubmit}
              onClick={handleSubmit}
            >
              Submit Reflection
            </button>
            {status && (
              <p className={`reflection-status ${!trimmedUserId ? 'reflection-warning' : ''}`}>
                {status}
              </p>
            )}
          </div>
        )}
      </div>
    </aside>
  )
}
