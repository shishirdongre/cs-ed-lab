/**
 * Stable color coding per Java class file key (matches `sources` and chunk `file` fields).
 * Used in the workshop step list, code explorer, and code viewer filename badge.
 */
export const CLASS_COLORS = {
  YelpSentimentAnalysis: { border: '#2563eb', bg: 'rgba(37, 99, 235, 0.12)', label: '#1e40af' },
  SparkInitializer: { border: '#7c3aed', bg: 'rgba(124, 58, 237, 0.12)', label: '#5b21b6' },
  DataLoader: { border: '#059669', bg: 'rgba(5, 150, 105, 0.12)', label: '#047857' },
  simple_yelp_reviews: { border: '#0d9488', bg: 'rgba(13, 148, 136, 0.12)', label: '#0f766e' },
  DataSplitter: { border: '#d97706', bg: 'rgba(217, 119, 6, 0.12)', label: '#b45309' },
  SentimentModelTrainer: { border: '#dc2626', bg: 'rgba(220, 38, 38, 0.12)', label: '#b91c1c' },
  SentimentPredictor: { border: '#0891b2', bg: 'rgba(8, 145, 178, 0.12)', label: '#0e7490' },
  ModelEvaluator: { border: '#4f46e5', bg: 'rgba(79, 70, 229, 0.12)', label: '#4338ca' },
  SampleReviewTester: { border: '#65a30d', bg: 'rgba(101, 163, 13, 0.12)', label: '#4d7c0f' },
}

/** @param {string} fileKey */
export function getClassColors(fileKey) {
  return CLASS_COLORS[fileKey] ?? { border: '#94a3b8', bg: 'rgba(148, 163, 184, 0.15)', label: '#475569' }
}
