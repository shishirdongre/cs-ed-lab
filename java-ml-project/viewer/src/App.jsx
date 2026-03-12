import { useState, useEffect } from 'react'
import CodeViewer from './CodeViewer'
import ReflectionPanel from './ReflectionPanel'
import SectionReflectionForm from './SectionReflectionForm'
import SidebarTabs from './SidebarTabs'
import FileList from './CodeExplorer/FileList'
import CodeDisplay from './CodeExplorer/CodeDisplay'
import ErrorBoundary from './ErrorBoundary'
import chunksData from './chunks.json'
import { sources } from './sources'
import { getDisplayName } from './fileDisplayNames'
import { saveUser } from './reflectionApi'
import { SUBSECTION_EXPLANATIONS } from './subsectionExplanations'
import './App.css'

const RESEARCH_ID_KEY = 'workshop_research_id'
const COMPLETED_CHUNKS_KEY = 'workshop_completed_chunks'

function getSubStepIdsForStep(chunks, step) {
  return chunks.filter((c) => c.step === step && c.substep != null).map((c) => c.id)
}

function isStepHeaderComplete(chunks, step, completedChunkIds) {
  const subIds = getSubStepIdsForStep(chunks, step)
  return subIds.length > 0 && subIds.every((id) => completedChunkIds.includes(id))
}

function getFirstSubStepIndex(chunks) {
  const i = chunks.findIndex((c) => c.substep != null)
  return i >= 0 ? i : 0
}

function getNextSubStepIndex(chunks, currentIndex) {
  for (let i = currentIndex + 1; i < chunks.length; i++) {
    if (chunks[i].substep != null) return i
  }
  return currentIndex
}

function getPrevSubStepIndex(chunks, currentIndex) {
  for (let i = currentIndex - 1; i >= 0; i--) {
    if (chunks[i].substep != null) return i
  }
  return currentIndex
}

export default function App() {
  const [stepIndex, setStepIndex] = useState(() => getFirstSubStepIndex(chunksData))
  const [userId, setUserId] = useState(() => localStorage.getItem(RESEARCH_ID_KEY) || '')
  const [userIdInput, setUserIdInput] = useState(() => localStorage.getItem(RESEARCH_ID_KEY) || '')
  const [userStatus, setUserStatus] = useState('')
  const [completedChunkIds, setCompletedChunkIds] = useState(() => {
    try {
      const saved = localStorage.getItem(COMPLETED_CHUNKS_KEY)
      return saved ? JSON.parse(saved) : []
    } catch {
      return []
    }
  })
  const chunks = chunksData
  const currentChunk = chunks[stepIndex]
  const [activeTab, setActiveTab] = useState('steps')
  const fileKeys = Object.keys(sources || {})
  const [selectedFile, setSelectedFile] = useState(fileKeys[0] || null)

  useEffect(() => {
    const handleKeyDown = (e) => {
      if (e.target.closest('input, textarea, select')) return
      if (e.key === 'ArrowLeft') {
        setStepIndex((i) => getPrevSubStepIndex(chunks, i))
      } else if (e.key === 'ArrowRight') {
        setStepIndex((i) => getNextSubStepIndex(chunks, i))
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
    setCompletedChunkIds((prev) => {
      if (prev.includes(chunkId)) return prev
      const next = [...prev, chunkId]
      try {
        localStorage.setItem(COMPLETED_CHUNKS_KEY, JSON.stringify(next))
      } catch {}
      return next
    })
  }

  const goToNext = () => setStepIndex((i) => getNextSubStepIndex(chunks, i))
  const goToPrevious = () => setStepIndex((i) => getPrevSubStepIndex(chunks, i))

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
          <SidebarTabs activeTab={activeTab} onTabChange={setActiveTab} />
          {activeTab === 'steps' && (
          <nav className="chunk-nav">
            <div className="step-indicator">
              {currentChunk.substep != null
                ? `Step ${currentChunk.step}.${currentChunk.substep} (Step ${currentChunk.step} of 6)`
                : `Step ${currentChunk.step} of 6`}
            </div>
            <ul className="chunk-list" aria-label="Chunk list">
              {chunks.map((chunk, i) => {
                const isActive = i === stepIndex
                const isStepHeader = chunk.substep == null
                const isSubStep = chunk.substep != null
                const isCompleted = isStepHeader
                  ? isStepHeaderComplete(chunks, chunk.step, completedChunkIds)
                  : completedChunkIds.includes(chunk.id)
                const stepLabel = isStepHeader ? chunk.step : `${chunk.step}.${chunk.substep}`
                const content = (
                  <>
                    <span
                      className={`chunk-status ${isCompleted ? 'complete' : 'incomplete'}`}
                      aria-label={
                        isCompleted ? 'All reflections submitted for this chunk' : 'Reflections incomplete for this chunk'
                      }
                    >
                      ✓
                    </span>
                    <span className="chunk-num">{stepLabel}</span>
                    <span className="chunk-title">{chunk.title}</span>
                    {!isStepHeader && (
                      <span className="chunk-lines" title={`${getDisplayName(chunk.file)}.java: ${chunk.startLine}–${chunk.endLine}`}>
                        {chunk.startLine}–{chunk.endLine}
                      </span>
                    )}
                  </>
                )
                return (
                  <li key={chunk.id}>
                    {isStepHeader ? (
                      <div
                        className={`chunk-item chunk-item-header ${isCompleted ? 'chunk-item-completed' : ''}`}
                        aria-hidden="true"
                      >
                        {content}
                      </div>
                    ) : (
                      <button
                        className={`chunk-item chunk-item-indent ${isActive ? 'active' : ''} ${isCompleted ? 'chunk-item-completed' : ''}`}
                        onClick={() => setStepIndex(i)}
                      >
                        {content}
                      </button>
                    )}
                  </li>
                )
              })}
            </ul>
            {currentChunk?.substep != null && (
              <SectionReflectionForm
                chunk={currentChunk}
                userId={userId}
                onReflectSuccess={handleReflectSuccess}
                onGoToNext={goToNext}
                hasNext={getNextSubStepIndex(chunks, stepIndex) !== stepIndex}
              />
            )}
          </nav>
          )}
          {activeTab === 'codeExplorer' && (
            <div className="sidebar-code-explorer">
              <FileList
                sources={sources}
                selectedFile={selectedFile}
                onSelectFile={setSelectedFile}
              />
            </div>
          )}
        </aside>

        <div className="main-and-reflection">
          {activeTab === 'steps' && (
          <>
          <main className="main-content">
            <section className="narrative" aria-label="Explanation">
              <p className="narrative-label">Explanation</p>
              <h2>{currentChunk.title}</h2>
              {SUBSECTION_EXPLANATIONS[currentChunk.id] ? (
                SUBSECTION_EXPLANATIONS[currentChunk.id]
                  .trim()
                  .split(/\n\n+/)
                  .map((para, i) => <p key={i}>{para}</p>)
              ) : (
                <p>{currentChunk.description}</p>
              )}
            </section>
            <div className="code-viewer-wrapper">
              <CodeViewer
                sources={sources}
                currentChunk={currentChunk}
              />
            </div>
          </main>
          <ReflectionPanel
            currentChunk={currentChunk}
            userId={userId}
            stepIndex={stepIndex}
            chunksLength={chunks.length}
            hasNext={getNextSubStepIndex(chunks, stepIndex) !== stepIndex}
            hasPrevious={getPrevSubStepIndex(chunks, stepIndex) !== stepIndex}
            onReflectSuccess={handleReflectSuccess}
            onGoToNext={goToNext}
            onGoToPrevious={goToPrevious}
          />
          </>
          )}
          {activeTab === 'codeExplorer' && (
            <div className="code-explorer-main-wrap">
              <ErrorBoundary key={selectedFile}>
                <CodeDisplay
                  fileKey={selectedFile}
                  source={selectedFile ? sources[selectedFile] : ''}
                />
              </ErrorBoundary>
            </div>
          )}
        </div>
      </div>
    </div>
  )
}
