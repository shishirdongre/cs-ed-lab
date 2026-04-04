import { getExplorerFilename } from '../fileDisplayNames'
import { getClassColors } from '../classColors'
import './FileList.css'

export default function FileList({ sources, selectedFile, onSelectFile }) {
  const fileKeys = Object.keys(sources || {}).sort()

  return (
    <ul className="file-list" aria-label="Source files">
      {fileKeys.map((key) => {
        const isSelected = key === selectedFile
        const colors = getClassColors(key)
        return (
          <li key={key}>
            <button
              type="button"
              className={`file-list-item ${isSelected ? 'active' : ''}`}
              style={{
                borderLeft: `4px solid ${colors.border}`,
                background: isSelected ? colors.bg : 'transparent',
              }}
              onClick={() => onSelectFile(key)}
            >
              <span style={{ color: colors.label }}>{getExplorerFilename(key)}</span>
            </button>
          </li>
        )
      })}
    </ul>
  )
}
