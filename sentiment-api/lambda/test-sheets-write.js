#!/usr/bin/env node
/**
 * Test script: write a row to Google Sheet using service account credentials.
 * Run from sentiment-api/lambda/ (has googleapis dep).
 *
 * Usage:
 *   cd sentiment-api/lambda
 *   GOOGLE_SHEET_ID=1dAuK4wCw-Tzn5OSUyLe5xhkUijJwk60-WLB_8K07PZM node test-sheets-write.js
 *
 * Credentials: ../../java-ml-project/terraform/service-account.json
 */

const { google } = require('googleapis');
const path = require('path');
const fs = require('fs');

const SHEET_ID = process.env.GOOGLE_SHEET_ID || '1dAuK4wCw-Tzn5OSUyLe5xhkUijJwk60-WLB_8K07PZM';

async function tryCreds(file, data, testRow) {
  if (data.type !== 'service_account') {
    return null;
  }
  const auth = new google.auth.GoogleAuth({
    credentials: data,
    scopes: ['https://www.googleapis.com/auth/spreadsheets'],
  });
  const sheets = google.sheets({ version: 'v4', auth });
  const range = 'Sheet1!A:G';
  const res = await sheets.spreadsheets.values.append({
    spreadsheetId: SHEET_ID,
    range,
    valueInputOption: 'USER_ENTERED',
    insertDataOption: 'INSERT_ROWS',
    requestBody: { values: [testRow] },
  });
  return res;
}

async function main() {
  const p = path.join(__dirname, '../../java-ml-project/terraform/service-account.json');
  if (!fs.existsSync(p)) throw new Error('service-account.json not found in java-ml-project/terraform/');
  const data = JSON.parse(fs.readFileSync(p, 'utf8'));
  const testRow = [
    new Date().toISOString(),
    'test-script',
    'test-section',
    'q1',
    'Test question',
    'Test write from script - credentials work!',
    '5',
  ];
  console.log('Writing to sheet', SHEET_ID);
  console.log('Row:', testRow);
  const res = await tryCreds('service-account.json', data, testRow);
  console.log('OK - updated cells:', res.data.updates?.updatedCells ?? '?');
  console.log('Sheet URL: https://docs.google.com/spreadsheets/d/' + SHEET_ID + '/edit');
}

main().catch((e) => {
  console.error('Error:', e.message);
  if (e.code === 403 || e.message?.includes('403')) {
    console.error('\nGrant the service account access: Share the sheet with workshop-reflections@cs-ed-lab.iam.gserviceaccount.com');
  }
  if (e.message?.includes('Invalid JWT Signature') || e.message?.includes('invalid_grant')) {
    console.error('\nKey may be revoked. Create a new key: Google Cloud Console → IAM → Service Accounts → workshop-reflections → Keys → Add Key → JSON');
  }
  process.exit(1);
});
