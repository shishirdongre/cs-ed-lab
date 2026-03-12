/**
 * Maps internal file keys (from chunks/sources) to display names shown in the viewer.
 * Keeps naming consistent and removes "Refactored" etc. from user-facing labels.
 */
export const fileDisplayNames = {
  YelpSentimentAnalysisRefactored: 'YelpSentimentAnalysis',
}

export function getDisplayName(fileKey) {
  return fileDisplayNames[fileKey] ?? fileKey
}
