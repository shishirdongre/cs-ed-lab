import './ConfusionMatrixPreview.css'

/** Illustrative counts only; matches ModelEvaluator layout: matrix[actual][predicted]. */
const EXAMPLE = {
  tn: 420,
  fp: 95,
  fn: 110,
  tp: 375,
}

/**
 * 2x2 confusion matrix HTML table for Step 5.4 explanation (rows = actual label, cols = predicted).
 */
export default function ConfusionMatrixPreview() {
  const { tn, fp, fn, tp } = EXAMPLE

  return (
    <section className="confusion-matrix-section" aria-labelledby="confusion-matrix-heading">
      <h3 id="confusion-matrix-heading" className="confusion-matrix-heading">
        Confusion matrix layout (2x2)
      </h3>
      <p className="confusion-matrix-note">
        Same indexing as in code: <code>matrix[actual][predicted]</code>. Rows are the true label, columns are what
        the model predicted. Labels 0 and 1 are negative and positive sentiment. The counts below are made up for
        illustration; your program fills the cells from the test set.
      </p>
      <div className="confusion-matrix-table-wrap">
        <table className="confusion-matrix-table">
          <caption className="confusion-matrix-caption">
            Example counts. TN = true negative, FP = false positive, FN = false negative, TP = true positive.
          </caption>
          <thead>
            <tr>
              <th scope="col" className="confusion-matrix-corner" />
              <th scope="col" className="confusion-matrix-colhead">
                Predicted 0
                <span className="confusion-matrix-sub">(negative)</span>
              </th>
              <th scope="col" className="confusion-matrix-colhead">
                Predicted 1
                <span className="confusion-matrix-sub">(positive)</span>
              </th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <th scope="row" className="confusion-matrix-rowhead">
                Actual 0
                <span className="confusion-matrix-sub">(negative)</span>
              </th>
              <td className="confusion-matrix-cell confusion-matrix-cell-tn">
                <span className="confusion-matrix-abbr">TN</span>
                <span className="confusion-matrix-count">{tn}</span>
              </td>
              <td className="confusion-matrix-cell confusion-matrix-cell-fp">
                <span className="confusion-matrix-abbr">FP</span>
                <span className="confusion-matrix-count">{fp}</span>
              </td>
            </tr>
            <tr>
              <th scope="row" className="confusion-matrix-rowhead">
                Actual 1
                <span className="confusion-matrix-sub">(positive)</span>
              </th>
              <td className="confusion-matrix-cell confusion-matrix-cell-fn">
                <span className="confusion-matrix-abbr">FN</span>
                <span className="confusion-matrix-count">{fn}</span>
              </td>
              <td className="confusion-matrix-cell confusion-matrix-cell-tp">
                <span className="confusion-matrix-abbr">TP</span>
                <span className="confusion-matrix-count">{tp}</span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  )
}
