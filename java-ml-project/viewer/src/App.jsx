import { useState, useEffect } from 'react'
import CodeViewer from './CodeViewer'
import ReflectionPanel from './ReflectionPanel'
import chunksData from './chunks.json'
import source from './source.java?raw'
import { saveUser } from './reflectionApi'
import './App.css'

const RESEARCH_ID_KEY = 'workshop_research_id'

export default function App() {
  const [stepIndex, setStepIndex] = useState(0)
  const [userId, setUserId] = useState(() => localStorage.getItem(RESEARCH_ID_KEY) || '')
  const [userIdInput, setUserIdInput] = useState(() => localStorage.getItem(RESEARCH_ID_KEY) || '')
  const [userStatus, setUserStatus] = useState('')
  const [completedChunkIds, setCompletedChunkIds] = useState([])
  const chunks = chunksData
  const currentChunk = chunks[stepIndex]

  useEffect(() => {
    const handleKeyDown = (e) => {
      if (e.target.closest('input, textarea, select')) return
      if (e.key === 'ArrowLeft') {
        setStepIndex((i) => Math.max(0, i - 1))
      } else if (e.key === 'ArrowRight') {
        setStepIndex((i) => Math.min(chunks.length - 1, i + 1))
      }
    }
    window.addEventListener('keydown', handleKeyDown)
    return () => window.removeEventListener('keydown', handleKeyDown)
  }, [chunks.length])

  const handleSaveUserId = async () => {
    const id = (userIdInput || '').trim()
    if (!id) {
      setUserStatus('Please enter a Research ID.')
      return
    }
    setUserId(id)
    setUserStatus('')
    try {
      await saveUser(id)
    } catch (e) {
      // Errors are already logged inside saveUser; surface a simple message for the user.
      setUserStatus('There was a problem saving your Research ID. Please try again.')
    }
  }

  const handleReflectSuccess = (chunkId) => {
    setCompletedChunkIds((prev) => (prev.includes(chunkId) ? prev : [...prev, chunkId]))
  }

  return (
    <div className="app">
      <header className="app-header">
        <div className="app-header-left">
          <h1>Java Workshop - Yelp Sentiment Analysis</h1>
          <p className="subtitle">Step through the code chunk by chunk</p>
        </div>
        <div className="app-header-right">
          <label htmlFor="research-id" className="research-id-label">
            Research ID
          </label>
          <div className="research-id-row">
            <input
              id="research-id"
              type="text"
              placeholder="Enter the Research ID your facilitator gives you"
              value={userIdInput}
              onChange={(e) => setUserIdInput(e.target.value)}
              className="research-id-input"
            />
            <button type="button" className="research-id-save" onClick={handleSaveUserId}>
              Save
            </button>
          </div>
          {userId && <p className="research-id-saved">Saved: {userId}</p>}
          {!userId && userStatus && <p className="research-id-warning">{userStatus}</p>}
        </div>
      </header>

      <div className="app-body">
        <aside className="sidebar">
          <nav className="chunk-nav">
            <div className="nav-buttons">
              <button
                onClick={() => setStepIndex((i) => Math.max(0, i - 1))}
                disabled={stepIndex === 0}
                title="Previous chunk (←)"
              >
                ← Previous
              </button>
              <button
                onClick={() => setStepIndex((i) => Math.min(chunks.length - 1, i + 1))}
                disabled={stepIndex === chunks.length - 1}
                title="Next chunk (→)"
              >
                Next →
              </button>
            </div>
            <div className="step-indicator">
              Step {stepIndex + 1} of {chunks.length}
            </div>
            <ul className="chunk-list" aria-label="Chunk list">
              {chunks.map((chunk, i) => {
                const isActive = i === stepIndex
                const isCompleted = completedChunkIds.includes(chunk.id)
                const isSubChunk = chunk.parentId != null
                return (
                  <li key={chunk.id}>
                    <button
                      className={`chunk-item ${isActive ? 'active' : ''} ${isSubChunk ? 'chunk-item-indent' : ''}`}
                      onClick={() => setStepIndex(i)}
                    >
                      <span
                        className={`chunk-status ${isCompleted ? 'complete' : 'incomplete'}`}
                        aria-label={
                          isCompleted ? 'All reflections submitted for this chunk' : 'Reflections incomplete for this chunk'
                        }
                      >
                        {isCompleted ? '✓' : '✗'}
                      </span>
                      <span className="chunk-num">{chunk.id}</span>
                      <span className="chunk-title">{chunk.title}</span>
                    </button>
                  </li>
                )
              })}
            </ul>
          </nav>
        </aside>

        <div className="main-and-reflection">
          <main className="main-content">
            <div className="code-viewer-wrapper">
              <CodeViewer
                source={source}
                currentChunk={currentChunk}
              />
            </div>
            <section className="narrative" aria-label="Explanation">
              <p className="narrative-label">Explanation</p>
              <h2>{currentChunk.title}</h2>
              <p>{currentChunk.description}</p>
            </section>
          </main>
          <ReflectionPanel
            currentChunk={currentChunk}
            userId={userId}
            onReflectSuccess={handleReflectSuccess}
          />
        </div>
      </div>
    </div>
  )
}
