# Creating Google Form from Markdown File

This script automatically creates a Google Form from the `GOOGLE_FORM_QUESTIONS.md` file using the Google Forms API.

## Prerequisites

1. **Python 3.7+** installed
2. **Google Cloud Project** with Forms API enabled (already set up for cs-ed-lab project)
3. **Service account key** (embedded in script, or provide your own)

## Setup Instructions

### Step 1: Install Required Packages

```bash
pip install google-api-python-client google-auth-httplib2 google-auth-oauthlib
```

### Step 2: Create Google Cloud Project

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select an existing one
3. Note your project name/ID

### Step 3: Enable Google Forms API (if not already enabled)

1. In Google Cloud Console, go to **APIs & Services** ? **Library**
2. Search for "Google Forms API"
3. Click on it and press **Enable**

**Note:** This should already be enabled for the cs-ed-lab project.

### Step 4: Run the Script

The script has a service account key embedded, so you can run it directly:

```bash
python create_google_form.py --use-embedded
```

The script will:
1. Authenticate using the embedded service account (no browser needed)
2. Create the form with all questions from the markdown file
3. Print the form URL

**Note:** The form will be created in the service account's Google Drive (`workshop-reflections@cs-ed-lab.iam.gserviceaccount.com`). You may need to share it with your personal Google account to access it.

## Command Line Options

```bash
python create_google_form.py \
    --use-embedded \
    --input GOOGLE_FORM_QUESTIONS.md \
    --title "Java Sentiment Analysis Lab - Pre/Post Survey"
```

- `--use-embedded`: Use embedded service account key (recommended)
- `--credentials`: Path to service account JSON file (optional, if not using embedded)
- `--input`: Path to markdown file (default: `GOOGLE_FORM_QUESTIONS.md`)
- `--title`: Title for the Google Form (default: "Java Sentiment Analysis Lab - Pre/Post Survey")

## Output

The script will:
- Create a new Google Form
- Add all questions from the markdown file
- Print the form edit URL and view URL
- Save authentication token for future runs (in `token.json`)

## Troubleshooting

### "API not enabled" error
- Make sure Google Forms API is enabled in your Google Cloud project
- Wait a few minutes after enabling for it to propagate

### "Invalid credentials" error
- Make sure the service account has the Forms API enabled
- Check that the service account has proper permissions in Google Cloud Console

### "Permission denied" error
- Make sure you're signed in with a Google account that has permission to create forms
- Check that the OAuth consent screen is properly configured

### Questions not appearing correctly
- Check the markdown file format matches the expected structure
- Some complex formatting might need manual adjustment in Google Forms

## Manual Alternative

If the script doesn't work, you can manually create the form:
1. Go to [Google Forms](https://forms.google.com)
2. Create a new form
3. Copy questions from the markdown file section by section
4. Use the code links provided in each question

## Notes

- Service account authentication doesn't require browser interaction
- The form will be created in the service account's Google Drive
- You may need to share the form with your personal Google account to access it
- The service account email is: `workshop-reflections@cs-ed-lab.iam.gserviceaccount.com`
- You can share the form URL with students after creation
