/**
 * Google Sheets integration from the frontend using service account (no backend).
 * No confirmation returned to the UI; sends are fire-and-forget.
 */

import * as jose from 'jose'
/*
const SHEET_ID = ''
const SCOPES = [
  'https://www.googleapis.com/auth/spreadsheets',
  'https://www.googleapis.com/auth/drive',
]

const SERVICE_ACCOUNT_INFO = {
  type: 'service_account',
  project_id: '',
  private_key_id: '',
  private_key: `-----BEGIN PRIVATE KEY----
-----END PRIVATE KEY-----`,
  client_email: '',
  token_uri: '',
}  */

const RESEARCH_ID_KEY = 'workshop_research_id'

function sanitizeSheetTitle(raw) {
  const bad = [':', '\\', '/', '?', '*', '[', ']']
  let title = (raw || '').trim()
  for (const ch of bad) title = title.replaceAll(ch, '')
  if (title.length > 80) title = title.slice(0, 80)
  return title || 'User'
}

let cachedToken = null
let tokenExpiry = 0

async function getAccessToken() {
  if (cachedToken && Date.now() < tokenExpiry - 60000) return cachedToken
  const key = await jose.importPKCS8(SERVICE_ACCOUNT_INFO.private_key, 'RS256')
  const payload = { scope: SCOPES.join(' ') }
  const jwt = await new jose.SignJWT(payload)
    .setProtectedHeader({ alg: 'RS256', typ: 'JWT' })
    .setIssuer(SERVICE_ACCOUNT_INFO.client_email)
    .setAudience(SERVICE_ACCOUNT_INFO.token_uri)
    .setIssuedAt()
    .setExpirationTime('1h')
    .sign(key)
  const res = await fetch(SERVICE_ACCOUNT_INFO.token_uri, {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    body: new URLSearchParams({
      grant_type: 'urn:ietf:params:oauth:grant-type:jwt-bearer',
      assertion: jwt,
    }),
  })
  if (!res.ok) {
    const text = await res.text()
    throw new Error(text || res.statusText)
  }
  const data = await res.json()
  cachedToken = data.access_token
  tokenExpiry = Date.now() + (data.expires_in || 3600) * 1000
  return cachedToken
}

function sheetRange(title) {
  const safe = title.replace(/'/g, "''")
  return `'${safe}'!A:G`
}

/**
 * Research ID sent first so a sheet with that name may be created.
 * Fire-and-forget; no confirmation returned to the UI.
 */
export async function saveUser(userId) {
  const title = sanitizeSheetTitle(userId)
  if (!title) return
  try {
    const token = await getAccessToken()
    const res = await fetch(
      `https://sheets.googleapis.com/v4/spreadsheets/${SHEET_ID}?fields=sheets.properties`,
      { headers: { Authorization: `Bearer ${token}` } }
    )
    if (!res.ok) throw new Error(await res.text())
    const data = await res.json()
    const exists = (data.sheets || []).some(
      (s) => (s.properties && s.properties.title) === title
    )
    if (!exists) {
      const batchRes = await fetch(
        `https://sheets.googleapis.com/v4/spreadsheets/${SHEET_ID}:batchUpdate`,
        {
          method: 'POST',
          headers: {
            Authorization: `Bearer ${token}`,
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            requests: [
              {
                addSheet: {
                  properties: {
                    title,
                    gridProperties: { rowCount: 2000, columnCount: 10 },
                  },
                },
              },
            ],
          }),
        }
      )
      if (!batchRes.ok) throw new Error(await batchRes.text())
      const appendRes = await fetch(
        `https://sheets.googleapis.com/v4/spreadsheets/${SHEET_ID}/values/${encodeURIComponent(sheetRange(title))}:append?valueInputOption=USER_ENTERED`,
        {
          method: 'POST',
          headers: {
            Authorization: `Bearer ${token}`,
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            values: [
              [
                'timestamp',
                'user_id',
                'section_id',
                'item_id',
                'question',
                'response',
                'confidence',
              ],
            ],
          }),
        }
      )
      if (!appendRes.ok) throw new Error(await appendRes.text())
    }
    if (typeof localStorage !== 'undefined') {
      localStorage.setItem(RESEARCH_ID_KEY, userId)
    }
  } catch (e) {
    console.error('[reflectionApi] saveUser failed:', e?.message ?? e)
  }
}

/**
 * Append reflection rows to the user's sheet. Fire-and-forget; no confirmation returned.
 */
export function reflect(userId, sectionId, items, confidence) {
  submitReflection(userId, sectionId, confidence, items)
}

export async function submitReflection(userId, sectionId, confidence, items) {
  const title = sanitizeSheetTitle(userId)
  if (!title || !items || !items.length) return
  try {
    const token = await getAccessToken()
    const ts = new Date().toISOString()
    const rows = items.map((it) => [
      ts,
      userId,
      sectionId,
      it.itemId || '',
      it.question || '',
      it.response || '',
      confidence != null ? String(confidence) : '',
    ])
    const appendRes = await fetch(
      `https://sheets.googleapis.com/v4/spreadsheets/${SHEET_ID}/values/${encodeURIComponent(sheetRange(title))}:append?valueInputOption=USER_ENTERED`,
      {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ values: rows }),
      }
    )
    if (!appendRes.ok) {
      const errText = await appendRes.text()
      throw new Error(`Sheets append ${appendRes.status}: ${errText}`)
    }
  } catch (e) {
    console.error('[reflectionApi] submitReflection failed:', e?.message ?? e)
  }
}
