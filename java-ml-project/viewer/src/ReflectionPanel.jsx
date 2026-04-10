import { useState, useEffect, useRef } from 'react'
import { submitReflection } from './reflectionApi'
import { predictSentiment } from './sentimentApi'
import { getReflectionForChunk } from './reflectionQuestions'
import { ALLOW_EMPTY_REFLECTION_FOR_TESTING } from './reflectionConfig'
import './ReflectionPanel.css'
const DEFAULT_MIN_LENGTH = 75

function normalizeItems(items) {
  if (typeof items === 'string') {
    return [{ item_id: ' ', question: items }]
  }
  return Array.isArray(items) ? items : []
}

export default function ReflectionPanel({ currentChunk, userId, stepIndex, chunksLength, hasNext, hasPrevious, onReflectSuccess, onGoToNext, onGoToPrevious }) {
  const [status, setStatus] = useState('')
  const [values, setValues] = useState({}) // sectionId_itemId -> value
  const [sentimentReview, setSentimentReview] = useState('')
  const [sentimentResult, setSentimentResult] = useState(null)
  const [sentimentLoading, setSentimentLoading] = useState(false)
  const [sentimentError, setSentimentError] = useState('')
  const [lastSubmitted, setLastSubmitted] = useState({}) // sectionId_itemId -> value
  const [confidence, setConfidence] = useState(null)
  const panelScrollRef = useRef(null)

  useEffect(() => {
    if (panelScrollRef.current) {
      panelScrollRef.current.scrollTop = 0
    }
  }, [currentChunk?.id])

  useEffect(() => {
    setConfidence(null)
  }, [currentChunk?.id])

  const reflection = getReflectionForChunk(currentChunk)
  const sectionId = reflection?.sectionId ?? ''
  const items = reflection ? normalizeItems(reflection.items) : []
  const minLength = reflection?.minLength ?? DEFAULT_MIN_LENGTH
  const showConfidence = reflection?.showConfidence ?? true

  const key = (itemId) => (sectionId ? `${sectionId}_${itemId}` : itemId)
  const lengths = items.map((it) => (values[key(it.item_id)] || '').trim().length)
  const allValid = items.length > 0 && lengths.every((L) => L >= minLength)
  const trimmedUserId = (userId || '').trim()
  const textOk = ALLOW_EMPTY_REFLECTION_FOR_TESTING || allValid
  const confidenceOk = !showConfidence || confidence != null
  const canSubmit = Boolean(trimmedUserId && textOk && confidenceOk)

  const makeCounterHtml = (n) => {
    const color = n >= minLength ? '#2e7d32' : '#b71c1c'
    return { __html: `<span class="reflection-counter" style="color:${color}">${n} / ${minLength} chars</span>` }
  }

  const handleSubmit = () => {
    if (!trimmedUserId) {
      setStatus('Please set your Research ID in the top right before submitting.')
      return
    }
    if (!ALLOW_EMPTY_REFLECTION_FOR_TESTING && !allValid) {
      setStatus('Some answers are too short. Please reach the minimum character count.')
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
    onGoToNext?.()
  }

  const setValue = (itemId, text) => {
    setValues((prev) => ({ ...prev, [key(itemId)]: text }))
  }

  const handleSentimentSubmit = async () => {
    const review = sentimentReview.trim()
    if (!review) {
      setSentimentError('Enter a review to analyze.')
      return
    }
    setSentimentError('')
    setSentimentResult(null)
    setSentimentLoading(true)
    try {
      const data = await predictSentiment(review)
      setSentimentResult(data)
    } catch (e) {
      setSentimentError(e?.message || 'Request failed.')
    } finally {
      setSentimentLoading(false)
    }
  }

  if (!currentChunk) return null

  const showSentimentForm = currentChunk?.step === 6
  const canGoNext = hasNext ?? (stepIndex != null && chunksLength != null && stepIndex < chunksLength - 1)
  const canGoPrevious = hasPrevious ?? (stepIndex != null && stepIndex > 0)

  return (
    <aside className="reflection-panel" ref={panelScrollRef}>
      <div className="reflection-panel-inner">
        {showSentimentForm && (
          <section className="sentiment-section">
            <h3 className="reflection-panel-heading">Try Sentiment Analysis</h3>
            <p className="sentiment-hint">Enter a review to predict sentiment (positive/negative). With ~76% accuracy, some predictions may be wrong (for example, sarcasm or short text can confuse the model).</p>
            <textarea
              placeholder="e.g. Amazing pizza, friendly staff!"
              value={sentimentReview}
              onChange={(e) => setSentimentReview(e.target.value)}
              className="reflection-textarea sentiment-textarea"
              rows={3}
              disabled={sentimentLoading}
            />
            <button
              type="button"
              className={`reflection-submit sentiment-submit${sentimentLoading ? ' sentiment-submit--loading' : ''}`}
              onClick={handleSentimentSubmit}
              disabled={sentimentLoading}
              aria-busy={sentimentLoading}
              title={
                sentimentLoading
                  ? 'Please wait for up to 1 minute while the analysis runs.'
                  : 'Send this review to the sentiment API'
              }
            >
              {sentimentLoading && <span className="sentiment-spinner" aria-hidden="true" />}
              <span className="sentiment-submit-label">
                {sentimentLoading ? 'Please wait for up to 1 minute…' : 'Analyze'}
              </span>
            </button>
            {sentimentError && <p className="reflection-warning">{sentimentError}</p>}
            {sentimentResult && (
              <div className={`sentiment-result sentiment-${sentimentResult.sentiment}`}>
                <strong>Sentiment:</strong> {sentimentResult.sentiment}
              </div>
            )}
          </section>
        )}

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
                    <span className="reflection-question-text">{it.question}</span>
                  </h4>
                  <textarea
                    placeholder={
                      minLength > 0
                        ? `Type your reflection here... (minimum ${minLength} characters)`
                        : 'Optional feedback… you can leave this blank and still submit.'
                    }
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
              {canGoNext ? 'Submit and Next' : 'Submit'}
            </button>
            {status && (
              <p className={`reflection-status ${!trimmedUserId ? 'reflection-warning' : ''}`}>
                {status}
              </p>
            )}
            <div className="reflection-nav-buttons">
              <button
                type="button"
                className="reflection-nav-btn"
                onClick={onGoToPrevious}
                disabled={!canGoPrevious}
                title="Previous (←)"
              >
                ← Previous
              </button>
              <button
                type="button"
                className="reflection-nav-btn"
                onClick={onGoToNext}
                disabled={!canGoNext}
                title="Next (→)"
              >
                Next →
              </button>
            </div>
          </div>
        )}
        {!reflection && (
          <div className="reflection-nav-buttons">
            <button
              type="button"
              className="reflection-nav-btn"
              onClick={onGoToPrevious}
              disabled={!canGoPrevious}
              title="Previous (←)"
            >
              ← Previous
            </button>
            <button
              type="button"
              className="reflection-nav-btn"
              onClick={onGoToNext}
              disabled={!canGoNext}
              title="Next (→)"
            >
              Next →
            </button>
          </div>
        )}
      </div>
    </aside>
  )
}
