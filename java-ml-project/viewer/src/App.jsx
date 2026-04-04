import { Routes, Route, Navigate } from 'react-router-dom'
import chunksData from './chunks.json'
import { getFirstSubStepIndex } from './workshopRouting'
import { getStoredResearchId } from './reflectionApi'
import LandingLayout from './pages/LandingLayout'
import WelcomeStep1 from './pages/WelcomeStep1'
import WelcomeStep2 from './pages/WelcomeStep2'
import WorkshopPage from './pages/WorkshopPage'

const defaultChunkId = chunksData[getFirstSubStepIndex(chunksData)].id

function RequireResearchId({ children }) {
  if (!getStoredResearchId()) {
    return <Navigate to="/welcome" replace />
  }
  return children
}

/**
 * Top-level routes: welcome / Research ID (`/welcome`) and workshop (`/workshop/:chunkId`).
 */
export default function App() {
  return (
    <Routes>
      <Route path="/welcome" element={<LandingLayout />}>
        <Route index element={<WelcomeStep1 />} />
        <Route
          path="classes"
          element={
            <RequireResearchId>
              <WelcomeStep2 />
            </RequireResearchId>
          }
        />
      </Route>
      <Route
        path="/workshop/complete"
        element={
          <RequireResearchId>
            <Navigate to="/workshop/7.1" replace />
          </RequireResearchId>
        }
      />
      <Route
        path="/workshop/:chunkId"
        element={
          <RequireResearchId>
            <WorkshopPage />
          </RequireResearchId>
        }
      />
      <Route
        path="/workshop"
        element={
          <RequireResearchId>
            <Navigate to={`/workshop/${defaultChunkId}`} replace />
          </RequireResearchId>
        }
      />
      <Route path="/" element={<Navigate to="/welcome" replace />} />
    </Routes>
  )
}
