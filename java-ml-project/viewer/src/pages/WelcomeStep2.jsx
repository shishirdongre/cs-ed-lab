import { Link } from 'react-router-dom'
import { getClassColors } from '../classColors'
import { getExplorerFilename } from '../fileDisplayNames'
import chunksData from '../chunks.json'
import { getFirstSubStepIndex } from '../workshopRouting'
import './Landing.css'

/**
 * One-line descriptions for each Java class in the project (viewer source keys).
 * You will explore these in depth during the workshop; this screen is only a map.
 */
const CLASS_BLURBS = {
  YelpSentimentAnalysis: 'Starts the program and connects the main steps in order.',
  DataLoader: 'Loads the review file from disk and gets it ready to use.',
  DataSplitter: 'Divides reviews into a “practice” set and a “test” set.',
  SentimentModelTrainer: 'Teaches the program using the practice reviews.',
  SentimentPredictor: 'Uses what was learned to label new reviews as positive or negative.',
  ModelEvaluator: 'Checks how well the program did on the test reviews.',
  SampleReviewTester: 'Tries a few example reviews so you can see the result.',
  simple_yelp_reviews: 'Training data: review text and positive/negative labels (CSV).',
}

const ORDER = [
  'YelpSentimentAnalysis',
  'DataLoader',
  'simple_yelp_reviews',
  'DataSplitter',
  'SentimentModelTrainer',
  'SentimentPredictor',
  'ModelEvaluator',
  'SampleReviewTester',
]

/**
 * Step 2: color-coded class list so learners can orient before opening the code explorer.
 */
export default function WelcomeStep2() {
  const firstChunkId = chunksData[getFirstSubStepIndex(chunksData)].id

  return (
    <div className="landing-panel landing-panel-wide">
      <h2 className="landing-title">Classes in this project</h2>
      <p className="landing-lead">
        Each row is one Java file. You will see what it does when you reach it in the guided steps. This list is
        only to help you find names in the project and in the code explorer. No need to memorize anything here.
      </p>

      <ul className="landing-class-list">
        {ORDER.map((key) => {
          const colors = getClassColors(key)
          const name = getExplorerFilename(key)
          return (
            <li
              key={key}
              className="landing-class-item"
              style={{
                borderLeftColor: colors.border,
                background: colors.bg,
              }}
            >
              <span className="landing-class-name" style={{ color: colors.label }}>
                {name}
              </span>
              <span className="landing-class-desc">{CLASS_BLURBS[key]}</span>
            </li>
          )
        })}
      </ul>

      <Link className="landing-primary landing-primary-link" to={`/workshop/${firstChunkId}`}>
        Enter workshop
      </Link>
    </div>
  )
}
