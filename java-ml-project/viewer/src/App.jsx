import { useState, useEffect } from 'react'
import CodeViewer from './CodeViewer'
import ReflectionPanel from './ReflectionPanel'
import chunksData from './chunks.json'
import source from './source.java?raw'
import './App.css'

export default function App() {
  const [stepIndex, setStepIndex] = useState(0)
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

  return (
    <div className="app">
      <header className="app-header">
        <h1>Java Tutor — Yelp Sentiment Analysis</h1>
        <p className="subtitle">Step through the code chunk by chunk</p>
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
              {chunks.map((chunk, i) => (
                <li key={chunk.id}>
                  <button
                    className={`chunk-item ${i === stepIndex ? 'active' : ''}`}
                    onClick={() => setStepIndex(i)}
                  >
                    <span className="chunk-num">{chunk.id}</span>
                    <span className="chunk-title">{chunk.title}</span>
                  </button>
                </li>
              ))}
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
          <ReflectionPanel currentChunk={currentChunk} />
        </div>
      </div>
    </div>
  )
}
