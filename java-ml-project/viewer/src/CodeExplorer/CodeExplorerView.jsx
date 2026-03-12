import { useState } from 'react'
import FileList from './FileList'
import CodeDisplay from './CodeDisplay'
import { sources } from '../sources'
import './CodeExplorerView.css'

export default function CodeExplorerView() {
  const fileKeys = Object.keys(sources || {})
  const [selectedFile, setSelectedFile] = useState(fileKeys[0] || null)
  const source = selectedFile ? sources[selectedFile] : ''

  return (
    <div className="code-explorer-view">
      <aside className="code-explorer-sidebar">
        <FileList
          sources={sources}
          selectedFile={selectedFile}
          onSelectFile={setSelectedFile}
        />
      </aside>
      <div className="code-explorer-main">
        {selectedFile ? (
          <CodeDisplay fileKey={selectedFile} source={source} />
        ) : (
          <div className="code-explorer-empty">Select a file</div>
        )}
      </div>
    </div>
  )
}
