/** Shared helpers for workshop chunk navigation and URL sync. */

export function getSubStepIdsForStep(chunks, step) {
  return chunks.filter((c) => c.step === step && c.substep != null).map((c) => c.id)
}

export function isStepHeaderComplete(chunks, step, completedChunkIds) {
  const subIds = getSubStepIdsForStep(chunks, step)
  return subIds.length > 0 && subIds.every((id) => completedChunkIds.includes(id))
}

export function getFirstSubStepIndex(chunks) {
  const i = chunks.findIndex((c) => c.substep != null)
  return i >= 0 ? i : 0
}

export function getNextSubStepIndex(chunks, currentIndex) {
  for (let i = currentIndex + 1; i < chunks.length; i++) {
    if (chunks[i].substep != null) return i
  }
  return currentIndex
}

export function getPrevSubStepIndex(chunks, currentIndex) {
  if (currentIndex > 0) {
    const cur = chunks[currentIndex]
    const prev = chunks[currentIndex - 1]
    if (cur?.substep != null && prev?.substep == null && prev?.step === cur?.step) {
      return currentIndex - 1
    }
  }
  for (let i = currentIndex - 1; i >= 0; i--) {
    if (chunks[i].substep != null) return i
  }
  return currentIndex
}
