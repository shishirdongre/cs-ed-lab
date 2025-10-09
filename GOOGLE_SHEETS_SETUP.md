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

### 7. Upload Key to Colab
1. In Google Colab, click the **Files** icon (📁) in the left sidebar
2. Click **Upload to session storage**
3. Upload your downloaded JSON key file
4. Rename it to `service_account_key.json` (if it isn't already)
5. The file should appear as `/content/service_account_key.json`

### 8. Update the Notebook
1. In the notebook, find the line: `SHEET_ID = "YOUR_GOOGLE_SHEET_ID_HERE"`
2. Replace `YOUR_GOOGLE_SHEET_ID_HERE` with your actual Sheet ID from step 6
3. Run the notebook - it should connect successfully!

## 🔍 Verification

To verify the service account has minimal access:

1. **Test Sheet Access**: The notebook should be able to append rows to your sheet
2. **Test Other Access**: Try accessing other sheets - it should fail
3. **Check Permissions**: In Google Cloud Console, verify the service account has no IAM roles

## 🚨 Security Best Practices

- **Never commit the JSON key** to version control
- **Upload it directly to Colab** each time you run the notebook
- **Delete the key** from your local machine after uploading
- **Rotate keys periodically** (create new ones, delete old ones)
- **Monitor usage** in Google Cloud Console > IAM & Admin > Service Accounts

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