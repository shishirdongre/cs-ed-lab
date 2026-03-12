/**
 * Google Sheets backend for reflection submissions.
 * Uses service account from env: GOOGLE_SHEET_ID, GOOGLE_SERVICE_ACCOUNT_JSON (base64 or raw JSON).
 */

const { google } = require('googleapis');

const RESEARCH_ID_KEY = 'workshop_research_id';

function getSheetId() {
  return process.env.GOOGLE_SHEET_ID || '';
}

function getSheetsClient() {
  const sheetId = getSheetId();
  if (!sheetId) return null;

  let creds;
  try {
    const raw = process.env.GOOGLE_SERVICE_ACCOUNT_JSON;
    if (!raw) return null;
    creds = raw.startsWith('{')
      ? JSON.parse(raw)
      : JSON.parse(Buffer.from(raw, 'base64').toString('utf8'));
  } catch (e) {
    console.error('[sheets] Invalid GOOGLE_SERVICE_ACCOUNT_JSON:', e?.message);
    return null;
  }

  const auth = new google.auth.GoogleAuth({
    credentials: creds,
    scopes: ['https://www.googleapis.com/auth/spreadsheets'],
  });
  return google.sheets({ version: 'v4', auth });
}

function sanitizeSheetTitle(raw) {
  const bad = [':', '\\', '/', '?', '*', '[', ']'];
  let title = (raw || '').trim();
  for (const ch of bad) title = title.replaceAll(ch, '');
  if (title.length > 80) title = title.slice(0, 80);
  return title || 'User';
}

function sheetRange(title) {
  const safe = title.replace(/'/g, "''");
  return `'${safe}'!A:G`;
}

/**
 * Create user sheet if needed and add header row.
 */
async function saveUser(userId) {
  const sheets = getSheetsClient();
  const sheetId = getSheetId();
  if (!sheets || !sheetId) return;

  const title = sanitizeSheetTitle(userId);
  if (!title) return;

  try {
    const meta = await sheets.spreadsheets.get({
      spreadsheetId: sheetId,
      fields: 'sheets.properties',
    });
    const exists = (meta.data.sheets || []).some(
      (s) => s.properties?.title === title
    );
    if (!exists) {
      await sheets.spreadsheets.batchUpdate({
        spreadsheetId: sheetId,
        requestBody: {
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
        },
      });
      await sheets.spreadsheets.values.append({
        spreadsheetId: sheetId,
        range: sheetRange(title),
        valueInputOption: 'USER_ENTERED',
        requestBody: {
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
        },
      });
    }
  } catch (e) {
    console.error('[sheets] saveUser failed:', e?.message ?? e);
    throw e;
  }
}

/**
 * Append reflection rows to the user's sheet.
 */
async function submitReflection(userId, sectionId, confidence, items) {
  const sheets = getSheetsClient();
  const sheetId = getSheetId();
  if (!sheets || !sheetId) return;

  const title = sanitizeSheetTitle(userId);
  if (!title || !items?.length) return;

  const ts = new Date().toISOString();
  const rows = items.map((it) => [
    ts,
    userId,
    sectionId,
    it.itemId || '',
    it.question || '',
    it.response || '',
    confidence != null ? String(confidence) : '',
  ]);

  try {
    await sheets.spreadsheets.values.append({
      spreadsheetId: sheetId,
      range: sheetRange(title),
      valueInputOption: 'USER_ENTERED',
      requestBody: { values: rows },
    });
  } catch (e) {
    console.error('[sheets] submitReflection failed:', e?.message ?? e);
    throw e;
  }
}

module.exports = { saveUser, submitReflection, getSheetId, RESEARCH_ID_KEY };
