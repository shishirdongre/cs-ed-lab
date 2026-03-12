import { getDisplayName } from '../fileDisplayNames'
import './FileList.css'

export default function FileList({ sources, selectedFile, onSelectFile }) {
  const fileKeys = Object.keys(sources || {}).sort()

  return (
    <ul className="file-list" aria-label="Source files">
      {fileKeys.map((key) => {
        const displayName = getDisplayName(key)
        const isSelected = key === selectedFile
        return (
          <li key={key}>
            <button
              type="button"
              className={`file-list-item ${isSelected ? 'active' : ''}`}
              onClick={() => onSelectFile(key)}
            >
              {displayName}.java
            </button>
          </li>
        )
      })}
    </ul>
  )
}
