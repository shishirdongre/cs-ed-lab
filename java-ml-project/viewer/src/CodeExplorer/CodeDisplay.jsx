import { useEffect, useRef } from 'react'
import hljs from 'highlight.js/lib/core'
import java from 'highlight.js/lib/languages/java'
import 'highlight.js/styles/github.css'
import { getExplorerFilename, isPlainTextSource } from '../fileDisplayNames'
import './CodeDisplay.css'

hljs.registerLanguage('java', java)

export default function CodeDisplay({ fileKey, source }) {
  const filename = getExplorerFilename(fileKey || '')
  const plain = isPlainTextSource(fileKey || '')
  const codeRef = useRef(null)

  useEffect(() => {
    const el = codeRef.current
    if (!el) return

    const lines = (source || '').split('\n')
    const html = lines
      .map((line, i) => {
        const lineNum = i + 1
        const content = line || ' '
        const inner = plain
          ? content
              .replace(/&/g, '&amp;')
              .replace(/</g, '&lt;')
              .replace(/>/g, '&gt;')
          : hljs.highlight(content, { language: 'java' }).value
        const contentClass = plain ? 'code-display-line-content code-display-plain' : 'code-display-line-content'
        return `<span class="code-display-line" data-line="${lineNum}"><span class="code-display-line-num" aria-hidden="true">${lineNum}</span><span class="${contentClass}">${inner}</span></span>`
      })
      .join('')

    el.innerHTML = html
  }, [fileKey, source, plain])

  return (
    <div className="code-display">
      <div className="code-display-header">
        <span className="code-display-filename">{filename}</span>
      </div>
      <div className="code-display-scroll">
        <pre className="code-display-block">
          <code ref={codeRef} className="language-java code-display-code" />
        </pre>
      </div>
    </div>
  )
}
