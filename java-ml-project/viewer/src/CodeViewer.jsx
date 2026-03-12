import { useEffect, useRef } from 'react'
import hljs from 'highlight.js/lib/core'
import java from 'highlight.js/lib/languages/java'
import 'highlight.js/styles/github.css'
import './CodeViewer.css'
import { getDisplayName } from './fileDisplayNames'

hljs.registerLanguage('java', java)

const DEFAULT_FILE = 'ModelComponents'

export default function CodeViewer({ sources, currentChunk }) {
  const codeContainerRef = useRef(null)
  const highlightRef = useRef(null)

  const fileKey = currentChunk?.file ?? DEFAULT_FILE
  const file = getDisplayName(fileKey)
  const source = sources?.[fileKey] ?? ''
  const lines = source.split('\n')
  const isStepHeader = currentChunk?.substep == null
  const startLine = isStepHeader ? 1 : (currentChunk?.startLine ?? 1)
  const endLine = isStepHeader ? lines.length : (currentChunk?.endLine ?? lines.length)
  const highlightCode = !isStepHeader

  useEffect(() => {
    if (!highlightRef.current || !codeContainerRef.current || !currentChunk) return
    const container = codeContainerRef.current
    const target = highlightRef.current
    const scrollToHighlight = () => {
      const targetRect = target.getBoundingClientRect()
      const containerRect = container.getBoundingClientRect()
      const targetHeight = targetRect.height
      const containerHeight = container.clientHeight
      const targetOffsetInContent = targetRect.top - containerRect.top + container.scrollTop
      const scrollTop = Math.max(targetOffsetInContent - containerHeight / 2 + targetHeight / 2, 0)
      container.scrollTo({ top: scrollTop, behavior: 'smooth' })
    }
    requestAnimationFrame(() => requestAnimationFrame(scrollToHighlight))
  }, [currentChunk?.id])

  return (
    <div className="code-viewer">
      <div className="code-viewer-header">
        <span className="filename">{file}.java</span>
        {highlightCode && (
          <span className="chunk-badge">
            Lines {startLine}–{endLine}
          </span>
        )}
      </div>
      <div className="code-scroll" ref={codeContainerRef}>
        <pre className="code-block">
          <code className="language-java">
            {lines.map((line, i) => {
              const lineNum = i + 1
              const isInChunk = highlightCode && lineNum >= startLine && lineNum <= endLine
              const isFirstChunkLine = highlightCode && lineNum === startLine
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
