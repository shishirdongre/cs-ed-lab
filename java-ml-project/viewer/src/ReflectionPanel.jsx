import { useState, useEffect, useRef, useCallback } from 'react'
import { saveUser, submitReflection } from './reflectionApi'
import './ReflectionPanel.css'

const RESEARCH_ID_KEY = 'workshop_research_id'
const DEFAULT_MIN_LENGTH = 75

function normalizeItems(items) {
  if (typeof items === 'string') {
    return [{ item_id: ' ', question: items }]
  }
  return Array.isArray(items) ? items : []
}

export default function ReflectionPanel({ currentChunk, onReflectSuccess }) {
  const [userId, setUserId] = useState(() => localStorage.getItem(RESEARCH_ID_KEY) || '')
  const [userIdInput, setUserIdInput] = useState(() => localStorage.getItem(RESEARCH_ID_KEY) || '')
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

  const saveUserId = useCallback(async () => {
    const id = (userIdInput || '').trim()
    if (id) {
      setUserId(id)
      localStorage.setItem(RESEARCH_ID_KEY, id)
      setStatus('')
      saveUser(id)
      // No confirmation from API; UI already shows Saved
    } else {
      setStatus('Please enter a Research ID.')
    }
  }, [userIdInput])

  const reflection = currentChunk?.reflection
  const sectionId = reflection?.sectionId ?? ''
  const items = reflection ? normalizeItems(reflection.items) : []
  const minLength = reflection?.minLength ?? DEFAULT_MIN_LENGTH
  const showConfidence = reflection?.showConfidence ?? false

  const key = (itemId) => (sectionId ? `${sectionId}_${itemId}` : itemId)
  const lengths = items.map((it) => (values[key(it.item_id)] || '').trim().length)
  const allValid = items.length > 0 && lengths.every((L) => L >= minLength)
  const canSubmit = allValid && !!userId.trim()

  const makeCounterHtml = (n) => {
    const color = n >= minLength ? '#2e7d32' : '#b71c1c'
    return { __html: `<span class="reflection-counter" style="color:${color}">${n} / ${minLength} chars</span>` }
  }

  const handleSubmit = () => {
    if (!userId.trim()) {
      setStatus('Please set your Research ID above before submitting.')
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
      userId,
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
        <h3 className="reflection-panel-heading">Workshop Setup</h3>
        <div className="reflection-user-id">
          <label htmlFor="research-id">Research ID</label>
          <div className="reflection-user-id-row">
            <input
              id="research-id"
              type="text"
              placeholder="Enter the Research ID your facilitator gives you"
              value={userIdInput}
              onChange={(e) => setUserIdInput(e.target.value)}
              className="reflection-input"
            />
            <button type="button" className="reflection-save-id" onClick={saveUserId}>
              Save
            </button>
          </div>
          {userId && <p className="reflection-user-saved">Saved: {userId}</p>}
          {status && !userId && <p className="reflection-warning">{status}</p>}
        </div>

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
                <label>How confident are you about your answer?</label>
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
                      {n === 1 ? '1 (not confident)' : n === 5 ? '5 (very confident)' : n}
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
            {status && userId && <p className="reflection-status">{status}</p>}
          </div>
        )}
      </div>
    </aside>
  )
}
