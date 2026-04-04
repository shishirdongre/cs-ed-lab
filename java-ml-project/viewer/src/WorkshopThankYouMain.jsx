import { useState } from 'react'
import { SUBSECTION_EXPLANATIONS } from './subsectionExplanations'
import { submitReflection } from './reflectionApi'
import './WorkshopThankYouMain.css'

/** Same section id as Google Sheet rows for end-of-workshop feedback. */
const FEEDBACK_SECTION_ID = 'workshop_complete'

/**
 * Step 7 only: custom layout — no code UI, no reflection panel.
 * Substep 7.1 adds optional feedback in this column (not shared with ReflectionPanel).
 */
export default function WorkshopThankYouMain({
  chunkId,
  title,
  userId,
  onFeedbackSubmitted,
  onGoToPrevious,
  onGoToNext,
  hasPrevious,
  hasNext,
}) {
  const raw = SUBSECTION_EXPLANATIONS[chunkId] ?? ''
  const paragraphs = raw.trim() ? raw.trim().split(/\n\n+/) : []

  const [feedback, setFeedback] = useState('')
  const [status, setStatus] = useState('')
  const [submitting, setSubmitting] = useState(false)
  const [feedbackSaved, setFeedbackSaved] = useState(false)

  const showFeedback = chunkId === '7.1'
  const trimmedUserId = (userId || '').trim()

  const handleFeedbackSubmit = async () => {
    if (!trimmedUserId) {
      setStatus('Set your Research ID in the header before submitting.')
      return
    }
    setSubmitting(true)
    setStatus('')
    try {
      await submitReflection(trimmedUserId, FEEDBACK_SECTION_ID, undefined, [
        {
          itemId: 'general_feedback',
          question: 'General workshop feedback',
          response: feedback.trim(),
        },
      ])
      setStatus('Saved. Thank you for the feedback.')
      setFeedbackSaved(true)
      onFeedbackSubmitted?.()
    } catch (e) {
      setStatus(e?.message || 'Could not reach the server. You can try again later.')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="workshop-thank-you-root">
      <section
        className="workshop-thank-you-main narrative"
        aria-labelledby="workshop-thank-you-heading"
      >
        <h2 id="workshop-thank-you-heading">{title}</h2>
        {paragraphs.length > 0 ? (
          paragraphs.map((para, idx) => <p key={idx}>{para}</p>)
        ) : (
          <p className="workshop-thank-you-fallback">{title}</p>
        )}

        {showFeedback && (
          <div className="workshop-thank-you-feedback">
            <label className="workshop-thank-you-label" htmlFor="workshop-thank-you-feedback-field">
              Optional feedback
            </label>
            <textarea
              id="workshop-thank-you-feedback-field"
              className="workshop-thank-you-textarea"
              rows={5}
              value={feedback}
              onChange={(e) => setFeedback(e.target.value)}
              placeholder="What helped, what was unclear, or ideas for next time."
              disabled={submitting || feedbackSaved}
            />
            <button
              type="button"
              className="workshop-thank-you-submit"
              disabled={!trimmedUserId || submitting || feedbackSaved}
              onClick={handleFeedbackSubmit}
            >
              {feedbackSaved ? 'Feedback sent' : submitting ? 'Sending…' : 'Submit feedback'}
            </button>
            {status && (
              <p className={`workshop-thank-you-status ${!trimmedUserId ? 'workshop-thank-you-status--warn' : ''}`} role="status">
                {status}
              </p>
            )}
          </div>
        )}
      </section>

      <div className="workshop-thank-you-nav">
        <button
          type="button"
          className="workshop-thank-you-nav-btn"
          onClick={onGoToPrevious}
          disabled={!hasPrevious}
        >
          ← Previous
        </button>
        <button
          type="button"
          className="workshop-thank-you-nav-btn"
          onClick={onGoToNext}
          disabled={!hasNext}
        >
          Next →
        </button>
      </div>
    </div>
  )
}
