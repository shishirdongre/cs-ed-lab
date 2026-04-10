import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { RESEARCH_ID_KEY, workshopStorageGet, workshopStorageSet } from '../workshopStorage'
import './Landing.css'

/**
 * Step 1: workshop intro + large research ID field and primary action to continue.
 */
export default function WelcomeStep1() {
  const navigate = useNavigate()
  const [researchId, setResearchId] = useState(() => workshopStorageGet(RESEARCH_ID_KEY) || '')
  const [status, setStatus] = useState('')

  const handleContinue = () => {
    const id = (researchId || '').trim()
    if (!id) {
      setStatus('Please enter the Research ID your facilitator gave you.')
      return
    }
    setStatus('')
    workshopStorageSet(RESEARCH_ID_KEY, id)
    navigate('/welcome/classes')
  }

  return (
    <div className="landing-panel">
      <h1 className="landing-title">Java ML Workshop</h1>
      <p className="landing-lead">
        You will read real Java code step by step. The app walks through a small program that reads restaurant
        reviews, learns from them, and guesses whether a new review sounds positive or negative. You do not need
        any background in machine learning; just follow along at your own pace. Short answers you choose to share help
        us see what works in the session.
      </p>

      <label htmlFor="landing-research-id" className="landing-label">
        Research ID
      </label>
      <textarea
        id="landing-research-id"
        className="landing-textarea"
        rows={4}
        placeholder="Enter the Research ID your facilitator gives you"
        value={researchId}
        onChange={(e) => setResearchId(e.target.value)}
        autoComplete="off"
      />

      {status && <p className="landing-status">{status}</p>}

      <button type="button" className="landing-primary" onClick={handleContinue}>
        Save and continue
      </button>

      <p className="landing-hint">
        Already entered your ID?{' '}
        <Link to="/welcome/classes">Go to step 2</Link>
      </p>
    </div>
  )
}
