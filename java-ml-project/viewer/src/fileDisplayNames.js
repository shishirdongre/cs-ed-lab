/**
 * Maps internal file keys (from chunks/sources) to display names shown in the viewer.
 * Keys without an entry use the key as the display name.
 */
export const fileDisplayNames = {
  simple_yelp_reviews: 'simple_yelp_reviews',
}

const PLAIN_TEXT_SOURCE_KEYS = new Set(['simple_yelp_reviews'])

/** CSV / plain text entries in the code explorer (no Java syntax highlighting). */
export function isPlainTextSource(fileKey) {
  return PLAIN_TEXT_SOURCE_KEYS.has(fileKey)
}

/** Filename shown in the code explorer header (includes extension). */
export function getExplorerFilename(fileKey) {
  if (!fileKey) return ''
  if (fileKey === 'simple_yelp_reviews') return 'simple_yelp_reviews.csv'
  return `${getDisplayName(fileKey)}.java`
}

export function getDisplayName(fileKey) {
  return fileDisplayNames[fileKey] ?? fileKey
}
