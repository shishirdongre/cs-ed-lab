import { useState, useEffect, useMemo, useCallback, useRef } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import CodeViewer from '../CodeViewer'
import ReflectionPanel from '../ReflectionPanel'
import SidebarTabs from '../SidebarTabs'
import FileList from '../CodeExplorer/FileList'
import CodeDisplay from '../CodeExplorer/CodeDisplay'
import ErrorBoundary from '../ErrorBoundary'
import chunksData from '../chunks.json'
import { sources } from '../sources'
import { getDisplayName, getExplorerFilename } from '../fileDisplayNames'
import { getClassColors } from '../classColors'
import { SUBSECTION_EXPLANATIONS } from '../subsectionExplanations'
import DatasetPreviewSection from '../DatasetPreviewSection'
import ConfusionMatrixPreview from '../ConfusionMatrixPreview'
import PipelineDiagramPreview from '../PipelineDiagramPreview'
import WorkshopThankYouMain from '../WorkshopThankYouMain'
import {
  getFirstSubStepIndex,
  getNextSubStepIndex,
  getPrevSubStepIndex,
  isStepHeaderComplete,
} from '../workshopRouting'
import { getStoredResearchId } from '../reflectionApi'
import { COMPLETED_CHUNKS_KEY, workshopStorageGet, workshopStorageSet } from '../workshopStorage'
import '../App.css'

/** chunk id -> diagram variant (stages-only vs full program + stages) */
const PIPELINE_DIAGRAM_BY_CHUNK = {
  step3: 'stages',
  '3.1': 'stages',
  step4: 'full',
  '4.1': 'full',
}

/**
 * Main workshop UI: routable sections via `/workshop/:chunkId`, sidebar steps, code, reflections.
 */
export default function WorkshopPage() {
  const { chunkId } = useParams()
  const navigate = useNavigate()

  const chunks = chunksData
  const stepCount = useMemo(() => Math.max(...chunks.map((c) => c.step)), [chunks])
  const defaultIndex = useMemo(() => getFirstSubStepIndex(chunks), [chunks])

  const stepIndex = useMemo(() => {
    if (!chunkId) return defaultIndex
    const i = chunks.findIndex((c) => c.id === chunkId)
    return i >= 0 ? i : defaultIndex
  }, [chunkId, chunks, defaultIndex])

  const goToChunk = useCallback(
    (i) => {
      if (i >= 0 && i < chunks.length) navigate(`/workshop/${chunks[i].id}`, { replace: false })
    },
    [chunks, navigate]
  )

  useEffect(() => {
    if (!chunkId) return
    const i = chunks.findIndex((c) => c.id === chunkId)
    if (i < 0) navigate(`/workshop/${chunksData[getFirstSubStepIndex(chunksData)].id}`, { replace: true })
  }, [chunkId, chunks, navigate])

  // Read on each render so returning from /welcome picks up a freshly saved Research ID.
  const userId = getStoredResearchId()
  const [completedChunkIds, setCompletedChunkIds] = useState(() => {
    try {
      const saved = workshopStorageGet(COMPLETED_CHUNKS_KEY)
      return saved ? JSON.parse(saved) : []
    } catch {
      return []
    }
  })

  const currentChunk = chunks[stepIndex]
  const isThankYouStep = currentChunk?.step === 7
  /** Sidebar row with no sub-number (e.g. "Step 3: …"); overview only, no code tab. */
  const isMainStepHeading = currentChunk != null && currentChunk.substep == null
  const [activeTab, setActiveTab] = useState('steps')
  /** Within Steps: explanation first, then code in a separate tab. */
  const [contentTab, setContentTab] = useState('explanation')
  const fileKeys = Object.keys(sources || {})
  const [selectedFile, setSelectedFile] = useState(fileKeys[0] || null)

  useEffect(() => {
    setContentTab('explanation')
  }, [stepIndex])

  useEffect(() => {
    if (currentChunk?.id === '1.0' && sources.simple_yelp_reviews) {
      setSelectedFile('simple_yelp_reviews')
    }
  }, [currentChunk?.id])

  const headerRef = useRef(null)

  useEffect(() => {
    const el = headerRef.current
    if (!el || typeof ResizeObserver === 'undefined') return

    const syncHeaderHeight = () => {
      document.documentElement.style.setProperty('--header-height', `${el.offsetHeight}px`)
    }
    syncHeaderHeight()
    const ro = new ResizeObserver(syncHeaderHeight)
    ro.observe(el)
    return () => {
      ro.disconnect()
      document.documentElement.style.removeProperty('--header-height')
    }
  }, [])

  const goToNext = useCallback(() => {
    const next = getNextSubStepIndex(chunks, stepIndex)
    if (next !== stepIndex) {
      goToChunk(next)
    }
  }, [chunks, stepIndex, goToChunk])

  const goToPrevious = () => goToChunk(getPrevSubStepIndex(chunks, stepIndex))

  const hasNextChunk = getNextSubStepIndex(chunks, stepIndex) !== stepIndex

  useEffect(() => {
    const handleKeyDown = (e) => {
      if (e.target.closest('input, textarea, select')) return
      if (e.key === 'ArrowLeft') {
        goToChunk(getPrevSubStepIndex(chunks, stepIndex))
      } else if (e.key === 'ArrowRight') {
        goToNext()
      }
    }
    window.addEventListener('keydown', handleKeyDown)
    return () => window.removeEventListener('keydown', handleKeyDown)
  }, [chunks, stepIndex, goToChunk, goToNext])

  const handleReflectSuccess = (chunkIdMark) => {
    setCompletedChunkIds((prev) => {
      if (prev.includes(chunkIdMark)) return prev
      const next = [...prev, chunkIdMark]
      try {
        workshopStorageSet(COMPLETED_CHUNKS_KEY, JSON.stringify(next))
      } catch {}
      return next
    })
  }

  return (
    <div className="app">
      <header ref={headerRef} className="app-header">
        <div className="app-header-left">
          <h1>Java ML Workshop</h1>
        </div>
        <div className="app-header-right">
          {userId ? (
            <span className="research-id-saved">Research ID: {userId}</span>
          ) : (
            <span className="research-id-warning">No Research ID yet.</span>
          )}
        </div>
      </header>

      <div className="app-body">
        <aside className="sidebar">
          <SidebarTabs activeTab={activeTab} onTabChange={setActiveTab} />
          {activeTab === 'steps' && (
            <nav className="chunk-nav">
              <div className="step-indicator">
                {currentChunk.substep != null
                  ? `Step ${currentChunk.step}.${currentChunk.substep} (Step ${currentChunk.step} of ${stepCount})`
                  : `Step ${currentChunk.step} of ${stepCount}`}
              </div>
              <ul className="chunk-list" aria-label="Chunk list">
                {chunks.map((chunk, i) => {
                  const isActive = i === stepIndex
                  const isStepHeader = chunk.substep == null
                  const isCompleted = isStepHeader
                    ? isStepHeaderComplete(chunks, chunk.step, completedChunkIds)
                    : completedChunkIds.includes(chunk.id)
                  const stepLabel = isStepHeader ? chunk.step : `${chunk.step}.${chunk.substep}`
                  const fileColors = chunk.file ? getClassColors(chunk.file) : null
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
                      {!isStepHeader && chunk.file && (
                        <span
                          className="chunk-file-pill"
                          style={{
                            background: fileColors.bg,
                            color: fileColors.label,
                            borderColor: fileColors.border,
                          }}
                          title={getExplorerFilename(chunk.file)}
                        >
                          {getExplorerFilename(chunk.file)}
                        </span>
                      )}
                    </>
                  )
                  return (
                    <li key={chunk.id}>
                      {isStepHeader ? (
                        <button
                          type="button"
                          className={`chunk-item chunk-item-header ${isCompleted ? 'chunk-item-completed' : ''}`}
                          onClick={() => goToChunk(i)}
                        >
                          {content}
                        </button>
                      ) : (
                        <button
                          type="button"
                          className={`chunk-item chunk-item-indent ${isActive ? 'active' : ''} ${isCompleted ? 'chunk-item-completed' : ''}`}
                          style={
                            fileColors
                              ? { borderLeftColor: fileColors.border, borderLeftWidth: 3, borderLeftStyle: 'solid' }
                              : undefined
                          }
                          onClick={() => goToChunk(i)}
                        >
                          {content}
                        </button>
                      )}
                    </li>
                  )
                })}
              </ul>
            </nav>
          )}
          {activeTab === 'codeExplorer' && (
            <div className="sidebar-code-explorer">
              <FileList sources={sources} selectedFile={selectedFile} onSelectFile={setSelectedFile} />
            </div>
          )}
        </aside>

        <div className={`main-and-reflection${isThankYouStep ? ' main-and-reflection--thank-you' : ''}`}>
          {activeTab === 'steps' && (
            <>
              <main className="main-content">
                {isThankYouStep ? (
                  <WorkshopThankYouMain
                    chunkId={currentChunk.id}
                    title={currentChunk.title}
                    userId={userId}
                    onFeedbackSubmitted={() => handleReflectSuccess('7.1')}
                    onGoToPrevious={goToPrevious}
                    onGoToNext={goToNext}
                    hasPrevious={getPrevSubStepIndex(chunks, stepIndex) !== stepIndex}
                    hasNext={hasNextChunk}
                  />
                ) : (
                  <>
                    {!isMainStepHeading && (
                      <div className="main-content-tabbar" role="tablist" aria-label="Step content">
                        <button
                          type="button"
                          role="tab"
                          id="tab-explanation"
                          className={`content-tab ${contentTab === 'explanation' ? 'active' : ''}`}
                          aria-selected={contentTab === 'explanation'}
                          aria-controls="panel-explanation"
                          onClick={() => setContentTab('explanation')}
                        >
                          Explanation
                        </button>
                        <button
                          type="button"
                          role="tab"
                          id="tab-code"
                          className={`content-tab ${contentTab === 'code' ? 'active' : ''}`}
                          aria-selected={contentTab === 'code'}
                          aria-controls="panel-code"
                          onClick={() => setContentTab('code')}
                        >
                          Code
                        </button>
                      </div>
                    )}
                    <div className="main-content-panels">
                      {(isMainStepHeading || contentTab === 'explanation') && (
                        <section
                          id="panel-explanation"
                          role="tabpanel"
                          aria-labelledby={isMainStepHeading ? undefined : 'tab-explanation'}
                          className="narrative"
                          aria-label={isMainStepHeading ? 'Section overview' : 'Explanation'}
                        >
                          <h2>{currentChunk.title}</h2>
                          {SUBSECTION_EXPLANATIONS[currentChunk.id] ? (
                            <>
                              {SUBSECTION_EXPLANATIONS[currentChunk.id]
                                .trim()
                                .split(/\n\n+/)
                                .map((para, idx) => (
                                  <p key={idx}>{para}</p>
                                ))}
                              {currentChunk.id === '1.0' && <DatasetPreviewSection />}
                              {PIPELINE_DIAGRAM_BY_CHUNK[currentChunk.id] && (
                                <PipelineDiagramPreview variant={PIPELINE_DIAGRAM_BY_CHUNK[currentChunk.id]} />
                              )}
                              {currentChunk.id === '5.4' && <ConfusionMatrixPreview />}
                            </>
                          ) : (
                            <p>{currentChunk.description}</p>
                          )}
                          {!isMainStepHeading && (
                            <button type="button" className="view-code-cta" onClick={() => setContentTab('code')}>
                              View code
                            </button>
                          )}
                        </section>
                      )}
                      {!isMainStepHeading && contentTab === 'code' && (
                        <div
                          id="panel-code"
                          role="tabpanel"
                          aria-labelledby="tab-code"
                          className="code-viewer-wrapper"
                        >
                          <CodeViewer sources={sources} currentChunk={currentChunk} />
                        </div>
                      )}
                    </div>
                  </>
                )}
              </main>
              {!isThankYouStep && (
                <ReflectionPanel
                  currentChunk={currentChunk}
                  userId={userId}
                  stepIndex={stepIndex}
                  chunksLength={chunks.length}
                  hasNext={hasNextChunk}
                  hasPrevious={getPrevSubStepIndex(chunks, stepIndex) !== stepIndex}
                  onReflectSuccess={handleReflectSuccess}
                  onGoToNext={goToNext}
                  onGoToPrevious={goToPrevious}
                />
              )}
            </>
          )}
          {activeTab === 'codeExplorer' && (
            <div className="code-explorer-main-wrap">
              <ErrorBoundary key={selectedFile}>
                <CodeDisplay fileKey={selectedFile} source={selectedFile ? sources[selectedFile] : ''} />
              </ErrorBoundary>
            </div>
          )}
        </div>
      </div>
    </div>
  )
}
