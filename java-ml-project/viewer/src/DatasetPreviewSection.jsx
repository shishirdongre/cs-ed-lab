import csvPreview from './sources/simple_yelp_reviews.csv?raw'
import './DatasetPreviewSection.css'

function parseYelpCsvPreview(text) {
  const lines = text.trim().split(/\n/)
  if (lines.length < 2) return []
  const rows = []
  for (let i = 1; i < lines.length; i++) {
    const line = lines[i]
    if (!line.trim()) continue
    const lastComma = line.lastIndexOf(',')
    if (lastComma <= 0) continue
    const sentiment = line.slice(lastComma + 1).trim()
    let reviewText = line.slice(0, lastComma)
    if (reviewText.startsWith('"') && reviewText.endsWith('"')) {
      reviewText = reviewText.slice(1, -1).replace(/""/g, '"')
    }
    reviewText = reviewText.replace(/\\n/g, ' ').replace(/\\'/g, "'")
    const display = reviewText.length > 160 ? `${reviewText.slice(0, 160)}…` : reviewText
    rows.push({ text: display, sentiment })
  }
  return rows
}

const SAMPLE_ROW_LIMIT = 5

/**
 * Two-column HTML table (text | sentiment) for subsection 1.0; narrative lives in subsectionExplanations.
 */
export default function DatasetPreviewSection() {
  const rows = parseYelpCsvPreview(csvPreview).slice(0, SAMPLE_ROW_LIMIT)

  return (
    <section className="dataset-dedicated-section" aria-labelledby="dataset-preview-heading">
      <h3 id="dataset-preview-heading" className="dataset-preview-heading">
        Sample rows (two columns)
      </h3>
      <p className="dataset-preview-note">
        The table shows five example rows. Long text is truncated here for layout. To see the full bundled CSV as
        raw lines (headers and exact comma-separated values), open the <strong>Code</strong> tab. Use the{' '}
        <strong>View code</strong> button above, or pick <strong>simple_yelp_reviews.csv</strong> in the{' '}
        <strong>Code explorer</strong> sidebar.
      </p>
      <div className="dataset-preview-table-wrap">
        <table className="dataset-preview-table">
          <caption className="dataset-preview-caption">
            Five sample rows: columns <code>text</code> and <code>sentiment</code>.
          </caption>
          <thead>
            <tr>
              <th scope="col">text</th>
              <th scope="col">sentiment</th>
            </tr>
          </thead>
          <tbody>
            {rows.map((row, idx) => (
              <tr key={idx}>
                <td className="dataset-preview-cell-text">{row.text}</td>
                <td className="dataset-preview-cell-label">
                  <span className={`dataset-sentiment dataset-sentiment--${row.sentiment}`}>{row.sentiment}</span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </section>
  )
}
