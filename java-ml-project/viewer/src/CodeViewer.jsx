import { useEffect, useRef } from 'react'
import hljs from 'highlight.js/lib/core'
import java from 'highlight.js/lib/languages/java'
import 'highlight.js/styles/github.css'
import './CodeViewer.css'
import { getExplorerFilename, isPlainTextSource } from './fileDisplayNames'
import { getClassColors } from './classColors'

hljs.registerLanguage('java', java)

const DEFAULT_FILE = 'SentimentModelTrainer'

/** In viewer/sources/YelpSentimentAnalysis.java; only these lines get chunk highlights for substeps. */
const YELP_SENTIMENT_MAIN_FIRST = 39
const YELP_SENTIMENT_MAIN_LAST = 83

/**
 * Substeps on YelpSentimentAnalysis: clip chunk range to main() so static fields / helpers are never highlighted.
 * All other files: no line highlights (full class shown only).
 */
function getMainOnlyHighlightRange(chunk) {
  if (!chunk || chunk.file !== 'YelpSentimentAnalysis') return null
  if (chunk.substep == null) return null
  const a = chunk.startLine ?? 1
  const b = chunk.endLine ?? 99999
  const lo = Math.max(a, YELP_SENTIMENT_MAIN_FIRST)
  const hi = Math.min(b, YELP_SENTIMENT_MAIN_LAST)
  if (lo > hi) return null
  return { start: lo, end: hi }
}

export default function CodeViewer({ sources, currentChunk }) {
  const codeContainerRef = useRef(null)
  const highlightRef = useRef(null)

  const fileKey = currentChunk?.file ?? DEFAULT_FILE
  const filename = getExplorerFilename(fileKey)
  const classColors = getClassColors(fileKey)
  const source = sources?.[fileKey] ?? ''
  const lines = source.split('\n')
  const plain = isPlainTextSource(fileKey)
  const mainHighlight = getMainOnlyHighlightRange(currentChunk)
  const highlightCode = mainHighlight != null
  const startLine = mainHighlight?.start ?? 1
  const endLine = mainHighlight?.end ?? lines.length

  useEffect(() => {
    const container = codeContainerRef.current
    if (!container || !currentChunk) return
    if (!highlightRef.current || !mainHighlight) {
      container.scrollTo({ top: 0, behavior: 'smooth' })
      return
    }
    const target = highlightRef.current
    const targetOffset = target.offsetTop
    const targetHeight = target.offsetHeight || 0
    const containerHeight = container.clientHeight || 0
    const scrollTop = Math.max(targetOffset - containerHeight / 2 + targetHeight / 2, 0)
    container.scrollTo({ top: scrollTop, behavior: 'smooth' })
  }, [currentChunk?.id, mainHighlight?.start, mainHighlight?.end])

  return (
    <div className="code-viewer">
      <div className="code-viewer-header">
        <span
          className="filename filename-colored"
          style={{
            borderBottom: `3px solid ${classColors.border}`,
            color: classColors.label,
          }}
        >
          {filename}
        </span>
        {highlightCode && (
          <span className="chunk-badge">
            Lines {startLine} to {endLine} (main)
          </span>
        )}
      </div>
      <div className="code-scroll" ref={codeContainerRef}>
        <pre className="code-block">
          <code className={plain ? 'language-plaintext' : 'language-java'}>
            {lines.map((line, i) => {
              const lineNum = i + 1
              const isInChunk = highlightCode && lineNum >= startLine && lineNum <= endLine
              const isFirstChunkLine = highlightCode && lineNum === mainHighlight.start
              const raw = line || ' '
              const highlighted = plain
                ? raw.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
                : hljs.highlight(raw, { language: 'java' }).value
              return (
                <span
                  key={lineNum}
                  ref={isFirstChunkLine ? highlightRef : null}
                  className={`line ${isInChunk ? 'chunk-highlight' : ''}`}
                  data-line={lineNum}
                >
                  <span className="line-num" aria-hidden="true">{lineNum}</span>
                  <span className="line-content" dangerouslySetInnerHTML={{ __html: highlighted }} />
                </span>
              )
            })}
          </code>
        </pre>
      </div>
    </div>
  )
}
