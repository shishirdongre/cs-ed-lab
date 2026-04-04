import { Outlet, Link, useLocation } from 'react-router-dom'
import './Landing.css'

/**
 * Shell for the welcome flow: step indicator (1 / 2) and shared chrome.
 */
export default function LandingLayout() {
  const location = useLocation()
  // Second screen lives at /welcome/classes
  const step = location.pathname.includes('/classes') ? 2 : 1

  return (
    <div className="landing">
      <div className="landing-step-row" role="navigation" aria-label="Welcome steps">
        <div className={`landing-step ${step === 1 ? 'active' : ''}`}>
          <span className="landing-step-num">1</span>
          <span className="landing-step-label">Welcome</span>
        </div>
        <div className="landing-step-line" aria-hidden="true" />
        <div className={`landing-step ${step === 2 ? 'active' : ''}`}>
          <span className="landing-step-num">2</span>
          <span className="landing-step-label">Project classes</span>
        </div>
      </div>

      <Outlet />

      <p className="landing-footer-nav">
        <Link to="/welcome">Step 1</Link>
        {' · '}
        <Link to="/welcome/classes">Step 2</Link>
      </p>
    </div>
  )
}
