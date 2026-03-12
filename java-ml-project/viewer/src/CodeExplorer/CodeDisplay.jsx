import { useEffect, useRef } from 'react'
import hljs from 'highlight.js/lib/core'
import java from 'highlight.js/lib/languages/java'
import 'highlight.js/styles/github.css'
import { getDisplayName } from '../fileDisplayNames'
import './CodeDisplay.css'

hljs.registerLanguage('java', java)

export default function CodeDisplay({ fileKey, source }) {
  const displayName = getDisplayName(fileKey || '')
  const codeRef = useRef(null)

  useEffect(() => {
    const el = codeRef.current
    if (!el) return

    const lines = (source || '').split('\n')
    const html = lines
      .map((line, i) => {
        const lineNum = i + 1
        const highlighted = hljs.highlight(line || ' ', { language: 'java' }).value
        return `<span class="code-display-line" data-line="${lineNum}"><span class="code-display-line-num" aria-hidden="true">${lineNum}</span><span class="code-display-line-content">${highlighted}</span></span>`
      })
      .join('')

    el.innerHTML = html
  }, [fileKey, source])

  return (
    <div className="code-display">
      <div className="code-display-header">
        <span className="code-display-filename">{displayName}.java</span>
      </div>
      <div className="code-display-scroll">
        <pre className="code-display-block">
          <code ref={codeRef} className="language-java code-display-code" />
        </pre>
      </div>
    </div>
  )
}
