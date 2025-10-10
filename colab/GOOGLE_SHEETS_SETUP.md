# Google Sheets Service Account Setup

This guide explains how to set up a Google Service Account with **minimal permissions** to access only your specific Google Sheet for the workshop reflections.

## 🔒 Security Model

The service account will have **zero access** to anything except the specific Google Sheet you share with it. It cannot:
- Access other Google Sheets
- Access Google Drive files
- Access Gmail or other Google services
- Create or delete resources
- Access any other Google Cloud resources

## 📋 Step-by-Step Setup

### 1. Create Google Cloud Project
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select an existing one
3. Note your project ID (you'll need this later)

### 2. Enable Google Sheets API
1. In the Google Cloud Console, go to **APIs & Services > Library**
2. Search for "Google Sheets API"
3. Click on it and press **Enable**

### 3. Create Service Account (MINIMAL PERMISSIONS)
1. Go to **IAM & Admin > Service Accounts**
2. Click **Create Service Account**
3. Fill in the details:
   - **Service account name**: `workshop-reflections`
   - **Service account ID**: `workshop-reflections` (auto-generated)
   - **Description**: `Service account for workshop reflections sheet access only`
4. **IMPORTANT**: In the "Grant this service account access to project" section:
   - **IAM Roles**: Leave **EMPTY** or select **"No Role"**
   - This ensures minimal permissions
5. Click **Done**

### 4. Create and Download JSON Key
1. Click on your newly created service account
2. Go to the **Keys** tab
3. Click **Add Key > Create new key**
4. Select **JSON** format
5. Click **Create**
6. The JSON file will download automatically
7. **IMPORTANT**: Keep this file secure - it's like a password!

### 5. Configure Your Google Sheet
1. Open your Google Sheet (the one you want to store reflections in)
2. Click the **Share** button (top-right corner)
3. In the "Add people and groups" field, enter the service account email:
   - Format: `workshop-reflections@YOUR-PROJECT-ID.iam.gserviceaccount.com`
   - You can find this email in the JSON file under `client_email`
4. Set permission to **Editor** (so it can append rows)
5. **Uncheck** "Notify people" (since it's a service account)
6. Click **Send**

### 6. Get Your Google Sheet ID
1. Open your Google Sheet
2. Look at the URL: `https://docs.google.com/spreadsheets/d/SHEET_ID_HERE/edit`
3. Copy the `SHEET_ID_HERE` part
4. This is what you'll use in the notebook

### 7. Copy Key Content into Notebook
1. Open your downloaded JSON key file in a text editor
2. Copy the entire JSON content
3. In the notebook, find the section with `SERVICE_ACCOUNT_FILE = "/content/service_account_key.json"`
4. Replace that section with the following code:

```python
# Service Account Key (embedded for workshop use)
SERVICE_ACCOUNT_KEY = {
    "type": "service_account",
    "project_id": "your-project-id",
    "private_key_id": "...",
    "private_key": "-----BEGIN PRIVATE KEY-----\n...\n-----END PRIVATE KEY-----\n",
    "client_email": "workshop-reflections@your-project.iam.gserviceaccount.com",
    "client_id": "...",
    "auth_uri": "https://accounts.google.com/o/oauth2/auth",
    "token_uri": "https://oauth2.googleapis.com/token",
    "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
    "client_x509_cert_url": "..."
}

# Create credentials from the embedded key
creds = service_account.Credentials.from_service_account_info(
    SERVICE_ACCOUNT_KEY,
    scopes=['https://www.googleapis.com/auth/spreadsheets']
)
gc = gspread.authorize(creds)
ws = gc.open_by_key(SHEET_ID).sheet1
print("✅ Connected using embedded service account key")
```

5. Replace the values in `SERVICE_ACCOUNT_KEY` with your actual key content
6. Update `SHEET_ID` with your Google Sheet ID from step 6

### 8. Update the Notebook
1. In the notebook, find the line: `SHEET_ID = "YOUR_GOOGLE_SHEET_ID_HERE"`
2. Replace `YOUR_GOOGLE_SHEET_ID_HERE` with your actual Sheet ID from step 6
3. Run the notebook - it should connect successfully!

## 🔍 Verification

To verify the service account has minimal access:

1. **Test Sheet Access**: The notebook should be able to append rows to your sheet
2. **Test Other Access**: Try accessing other sheets - it should fail
3. **Check Permissions**: In Google Cloud Console, verify the service account has no IAM roles

## 🚨 Security Implications & Best Practices

### ⚠️ **CRITICAL SECURITY WARNING**
**Embedding the service account key directly in the notebook code has significant security implications:**

- **🔓 Key Exposure**: The private key is visible to anyone with access to the notebook
- **📤 Version Control Risk**: If committed to Git, the key becomes permanently exposed
- **👥 Sharing Risk**: Anyone you share the notebook with can see and use the key
- **🌐 Public Access**: If the notebook is made public, the key is publicly accessible

### 🎓 **Workshop-Specific Security Measures**

**This approach is ONLY acceptable for temporary workshop use with the following conditions:**

1. **⏰ Temporary Use Only**: The service account and key should be **decommissioned immediately after the workshop**
2. **🔒 Minimal Permissions**: The service account has zero IAM roles and can only access the specific shared sheet
3. **📋 Limited Scope**: Only has access to Google Sheets API for the single shared sheet
4. **🗑️ Cleanup Required**: Delete the service account and key after workshop completion

### 🛡️ **Post-Workshop Cleanup (MANDATORY)**

**After the workshop, you MUST:**

1. **Delete the service account** in Google Cloud Console
2. **Revoke access** from the Google Sheet (remove the service account email)
3. **Delete the notebook** or remove the embedded key
4. **Verify cleanup** by checking that the key no longer works

### 🔒 **Production Security Best Practices**

For any production use, follow these practices instead:

- **Never embed keys** in code
- **Use environment variables** or secure key management
- **Upload keys to secure storage** (not version control)
- **Rotate keys regularly**
- **Monitor key usage** and access logs
- **Use least privilege principle**

### 📊 **Risk Assessment for Workshop Use**

| Risk Level | Mitigation |
|------------|------------|
| **High**: Key exposure in code | ✅ Acceptable for temporary workshop use |
| **Medium**: Unauthorized sheet access | ✅ Mitigated by minimal permissions and specific sheet sharing |
| **Low**: Service account abuse | ✅ Mitigated by zero IAM roles and limited scope |
| **Critical**: Permanent exposure | ✅ Mitigated by mandatory post-workshop cleanup |

## 🔧 Troubleshooting

### "Service account key file not found"
- Make sure you uploaded the JSON file to Colab
- Check the file path is `/content/service_account_key.json`

### "Permission denied" or "Sheet not found"
- Verify you shared the sheet with the service account email
- Check the Sheet ID is correct
- Ensure the service account has "Editor" permission on the sheet

### "Authentication failed"
- Verify the JSON key file is valid
- Check that Google Sheets API is enabled
- Ensure the service account was created correctly

## 📊 What the Service Account Can Do

✅ **Can do:**
- Read the specific sheet you shared with it
- Append new rows to that sheet
- Update existing data in that sheet

❌ **Cannot do:**
- Access any other Google Sheets
- Access Google Drive files
- Send emails
- Create or delete resources
- Access any other Google services

This setup ensures maximum security with minimal permissions!