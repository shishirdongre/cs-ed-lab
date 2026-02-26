import { useEffect, useRef } from 'react'
import hljs from 'highlight.js/lib/core'
import java from 'highlight.js/lib/languages/java'
import 'highlight.js/styles/github.css'
import './CodeViewer.css'

hljs.registerLanguage('java', java)

export default function CodeViewer({ source, currentChunk }) {
  const codeContainerRef = useRef(null)
  const highlightRef = useRef(null)

  const lines = source.split('\n')
  const startLine = currentChunk?.startLine ?? 1
  const endLine = currentChunk?.endLine ?? lines.length

  useEffect(() => {
    if (!highlightRef.current || !currentChunk) return
    highlightRef.current.scrollIntoView({ behavior: 'smooth', block: 'nearest' })
  }, [currentChunk?.id])

  return (
    <div className="code-viewer">
      <div className="code-viewer-header">
        <span className="filename">YelpSentimentAnalysisSpark.java</span>
        <span className="chunk-badge">
          Lines {startLine}–{endLine}
        </span>
      </div>
      <div className="code-scroll" ref={codeContainerRef}>
        <pre className="code-block">
          <code className="language-java">
            {lines.map((line, i) => {
              const lineNum = i + 1
              const isInChunk = lineNum >= startLine && lineNum <= endLine
              const isFirstChunkLine = lineNum === startLine
              const highlighted = hljs.highlight(line || ' ', { language: 'java' }).value
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
