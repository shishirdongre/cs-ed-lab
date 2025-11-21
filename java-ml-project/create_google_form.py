#!/usr/bin/env python3
"""
Script to create a Google Form from JSON question files in the questions/ directory
using the Google Forms API.

Requirements:
    pip install google-api-python-client google-auth-httplib2 google-auth-oauthlib

Setup:
    1. Go to https://console.cloud.google.com/
    2. Create a new project or select existing one
    3. Enable Google Forms API
    4. Create OAuth 2.0 credentials (Desktop app)
    5. Download credentials JSON file
    6. Run: python create_google_form.py --credentials credentials.json
"""

import re
import json
import argparse
from pathlib import Path
from typing import List, Dict, Any, Optional

try:
    from google.oauth2 import service_account
    from google_auth_oauthlib.flow import InstalledAppFlow
    from google.auth.transport.requests import Request
    from google.oauth2.credentials import Credentials
    from googleapiclient.discovery import build
    from googleapiclient.errors import HttpError
except ImportError:
    print("Error: Required packages not installed.")
    print("Run: pip install google-api-python-client google-auth-httplib2 google-auth-oauthlib")
    exit(1)


def clean_description(text: str) -> str:
    """Clean description text by removing markdown and response markers."""
    if not text:
        return ''
    
    # Remove markdown bold (**text**)
    text = re.sub(r'\*\*(.+?)\*\*', r'\1', text)
    
    # Remove markdown code blocks (```code```)
    text = re.sub(r'```[\w]*\n?(.+?)\n?```', r'\1', text, flags=re.DOTALL)
    
    # Remove inline code backticks (`code`)
    text = re.sub(r'`([^`]+)`', r'\1', text)
    
    # Remove response markers like [Open text response], [Open text response - minimum 2 sentences], etc.
    text = re.sub(r'\[Open text response[^\]]*\]', '', text, flags=re.IGNORECASE)
    text = re.sub(r'\[Open text response - minimum \d+ sentences?\]', '', text, flags=re.IGNORECASE)
    text = re.sub(r'\[Open text response - minimum \d+ sentences?\]', '', text, flags=re.IGNORECASE)
    
    # Clean up extra whitespace
    text = re.sub(r'\n\s*\n\s*\n+', '\n\n', text)  # Multiple newlines to double
    text = text.strip()
    
    return text

# Scopes required for Google Forms API
SCOPES = ['https://www.googleapis.com/auth/forms.body', 'https://www.googleapis.com/auth/drive.file']

# Google Forms API version
FORMS_API_VERSION = 'v1'


def load_questions_from_directory(questions_dir: str) -> List[Dict[str, Any]]:
    """Load questions from JSON files in the questions directory, ordered by filename."""
    questions_path = Path(questions_dir)
    if not questions_path.exists():
        raise FileNotFoundError(f"Questions directory not found: {questions_dir}")
    
    questions = []
    
    # Get all JSON files and sort them by filename (which includes the order number)
    json_files = sorted(questions_path.glob('*.json'))
    
    for json_file in json_files:
        with open(json_file, 'r', encoding='utf-8') as f:
            question_data = json.load(f)
            questions.append(question_data)
    
    return questions


def create_scale_question(question: Dict[str, Any], item_index: int) -> Dict[str, Any]:
    """Create a scale question."""
    scale_labels = question.get('scale_labels', {})
    # Title cannot contain newlines in Google Forms API
    title = question.get('title', f'Question {question.get("number", "")}').replace('\n', ' ')
    description = clean_description(question.get('description', ''))
    
    item_dict = {
        'createItem': {
            'item': {
                'title': title,
                'questionItem': {
                    'question': {
                        'required': False,  # Make all questions optional
                        'scaleQuestion': {
                            'low': question.get('scale_min', 1),
                            'high': question.get('scale_max', 5),
                            'lowLabel': scale_labels.get('low', 'Low'),
                            'highLabel': scale_labels.get('high', 'High')
                        }
                    }
                }
            },
            'location': {
                'index': item_index
            }
        }
    }
    
    # Only add description if it exists
    if description:
        item_dict['createItem']['item']['description'] = description
    
    return item_dict


def create_multiple_choice_question(question: Dict[str, Any], item_index: int) -> Dict[str, Any]:
    """Create a multiple choice question."""
    choices = [{'value': opt} for opt in question.get('options', [])]
    # Title cannot contain newlines in Google Forms API
    title = question.get('title', f'Question {question.get("number", "")}').replace('\n', ' ')
    description = clean_description(question.get('description', ''))
    
    item_dict = {
        'createItem': {
            'item': {
                'title': title,
                'questionItem': {
                    'question': {
                        'required': False,  # Make all questions optional
                        'choiceQuestion': {
                            'type': 'RADIO',
                            'options': choices,
                            'shuffle': False
                        }
                    }
                }
            },
            'location': {
                'index': item_index
            }
        }
    }
    
    # Only add description if it exists
    if description:
        item_dict['createItem']['item']['description'] = description
    
    return item_dict


def create_checkbox_question(question: Dict[str, Any], item_index: int) -> Dict[str, Any]:
    """Create a checkbox (multiple select) question."""
    choices = [{'value': opt} for opt in question.get('options', [])]
    # Title cannot contain newlines in Google Forms API
    title = question.get('title', f'Question {question.get("number", "")}').replace('\n', ' ')
    description = clean_description(question.get('description', ''))
    
    item_dict = {
        'createItem': {
            'item': {
                'title': title,
                'questionItem': {
                    'question': {
                        'required': False,  # Make all questions optional
                        'choiceQuestion': {
                            'type': 'CHECKBOX',
                            'options': choices,
                            'shuffle': False
                        }
                    }
                }
            },
            'location': {
                'index': item_index
            }
        }
    }
    
    # Only add description if it exists
    if description:
        item_dict['createItem']['item']['description'] = description
    
    return item_dict


def create_text_question(question: Dict[str, Any], item_index: int, is_paragraph: bool = False) -> Dict[str, Any]:
    """Create a text question (short answer or paragraph)."""
    question_type = 'PARAGRAPH_TEXT' if is_paragraph else 'SHORT_ANSWER'
    
    # Title cannot contain newlines in Google Forms API
    title = question.get('title', f'Question {question.get("number", "")}').replace('\n', ' ')
    
    # If description is provided directly, use it (for questions in groups)
    if question.get('description'):
        description = clean_description(question['description'])
    else:
        # Add code link if available (in description, not title)
        description_parts = []
        if question.get('code_link'):
            description_parts.append(f"Code link: {question['code_link']}")
        
        # Add predict, instruction, reflect if available (but NOT reveal)
        if question.get('predict'):
            description_parts.append(f"PREDICT:\n{clean_description(question['predict'])}")
        if question.get('instruction'):
            description_parts.append(f"\nINSTRUCTION:\n{clean_description(question['instruction'])}")
        # Skip reveal - don't include it in the form
        if question.get('reflect'):
            reflect_text = '\n'.join(question['reflect'])
            description_parts.append(f"\nREFLECT:\n{clean_description(reflect_text)}")
        
        description = '\n'.join(description_parts) if description_parts else ''
    
    return {
        'createItem': {
            'item': {
                'title': title,
                'description': description,
                'questionItem': {
                    'question': {
                        'required': False,  # Make all questions optional
                        'textQuestion': {
                            'paragraph': is_paragraph
                        }
                    }
                }
            },
            'location': {
                'index': item_index
            }
        }
    }


def create_section_break(title: str, item_index: int) -> Dict[str, Any]:
    """Create a section/page break."""
    return {
        'createItem': {
            'item': {
                'title': title,
                'pageBreakItem': {}
            },
            'location': {
                'index': item_index
            }
        }
    }


def authenticate_oauth(credentials_file: str) -> Any:
    """Authenticate using OAuth flow and return the service object."""
    creds = None
    token_file = 'token.json'
    
    # Load credentials file to check type
    with open(credentials_file, 'r') as f:
        client_config = json.load(f)
    
    # Load existing token
    if Path(token_file).exists():
        creds = Credentials.from_authorized_user_file(token_file, SCOPES)
    
    # If no valid credentials, get new ones
    if not creds or not creds.valid:
        if creds and creds.expired and creds.refresh_token:
            creds.refresh(Request())
        else:
            # Handle both 'installed' and 'web' client types
            if 'installed' in client_config:
                flow = InstalledAppFlow.from_client_secrets_file(credentials_file, SCOPES)
                # Try console flow first (doesn't require redirect URI configuration)
                try:
                    print("   Using console-based authentication (copy-paste code)...")
                    creds = flow.run_console()
                except Exception as console_error:
                    print(f"   Console flow failed: {console_error}")
                    print("   Trying local server flow...")
                    # Fallback to local server with fixed port
                    # Make sure redirect URI in Google Cloud Console includes:
                    # - http://localhost:8080/
                    # - http://127.0.0.1:8080/
                    try:
                        creds = flow.run_local_server(port=8080, open_browser=True)
                    except Exception as server_error:
                        print(f"   Local server flow failed: {server_error}")
                        print("\n   Troubleshooting redirect_uri_mismatch:")
                        print("   1. Go to Google Cloud Console > APIs & Services > Credentials")
                        print("   2. Click on your OAuth 2.0 Client ID")
                        print("   3. Under 'Authorized redirect URIs', add:")
                        print("      - http://localhost:8080/")
                        print("      - http://127.0.0.1:8080/")
                        print("      - http://localhost/")
                        print("      - http://127.0.0.1/")
                        print("   4. Save and try again")
                        raise
            elif 'web' in client_config:
                # For web clients, we need to use a different approach
                # Create a temporary 'installed' config from 'web' config
                print("   Converting web client to installed client format...")
                installed_config = {
                    'installed': client_config['web']
                }
                temp_config_file = 'temp_client_secret.json'
                with open(temp_config_file, 'w') as f:
                    json.dump(installed_config, f)
                try:
                    flow = InstalledAppFlow.from_client_secrets_file(temp_config_file, SCOPES)
                    # Try console flow first
                    try:
                        print("   Using console-based authentication (copy-paste code)...")
                        creds = flow.run_console()
                    except Exception:
                        # Fallback to local server
                        print("   Using local server authentication...")
                        creds = flow.run_local_server(port=8080, open_browser=True)
                finally:
                    # Clean up temp file
                    if Path(temp_config_file).exists():
                        Path(temp_config_file).unlink()
            else:
                raise ValueError("Unknown OAuth client type. Expected 'installed' or 'web'.")
        
        # Save credentials for next run
        with open(token_file, 'w') as token:
            token.write(creds.to_json())
    
    return build('forms', FORMS_API_VERSION, credentials=creds)


def authenticate_service_account(service_account_info: Dict[str, Any]) -> Any:
    """Authenticate using service account and return the service object."""
    try:
        creds = service_account.Credentials.from_service_account_info(
            service_account_info,
            scopes=SCOPES
        )
        return build('forms', FORMS_API_VERSION, credentials=creds)
    except Exception as e:
        print(f"? Authentication failed: {e}")
        raise


def make_form_public(credentials, form_id: str) -> bool:
    """Make the form publicly accessible via Drive API (helps with anonymous access)."""
    try:
        from googleapiclient.discovery import build as drive_build
        drive_service = drive_build('drive', 'v3', credentials=credentials)
        
        # Make the form publicly accessible (anyone with the link can view)
        permission = {
            'type': 'anyone',
            'role': 'reader'
        }
        drive_service.permissions().create(
            fileId=form_id,
            body=permission,
            fields='id'
        ).execute()
        return True
    except Exception as e:
        print(f"   Note: Could not set public access via Drive API: {e}")
        return False


def update_form_settings(service: Any, form_id: str, require_login: bool = False, credentials=None) -> bool:
    """Update form settings to make it anonymous or require login.
    
    Note: The 'requireLogin' setting is not available in Google Forms API v1.
    This function attempts to make the form publicly accessible via Drive API,
    but the 'Require sign-in' checkbox must be manually disabled in the Forms UI.
    """
    # The Forms API v1 doesn't support requireLogin setting directly
    # We can only update emailCollectionType
    try:
        settings_update = {
            'requests': [{
                'updateSettings': {
                    'settings': {
                        'emailCollectionType': 'DO_NOT_COLLECT' if not require_login else 'COLLECT'
                    },
                    'updateMask': 'emailCollectionType'
                }
            }]
        }
        service.forms().batchUpdate(formId=form_id, body=settings_update).execute()
        
        # Also try to make it publicly accessible via Drive API
        if credentials and not require_login:
            make_form_public(credentials, form_id)
        
        return True
    except Exception as e:
        print(f"⚠️  Error updating form settings: {e}")
        # Check if it's the requireLogin error
        if 'requireLogin' in str(e):
            print("   Note: The 'Require sign-in' setting is not available via API.")
            print("   You need to manually disable it in the form settings:")
            print(f"   1. Go to: https://docs.google.com/forms/d/{form_id}/edit")
            print("   2. Click the Settings (gear) icon")
            print("   3. Uncheck 'Require sign-in to view this form'")
            print("   4. Save the form")
        return False


def expand_question_groups(questions: List[Dict[str, Any]]) -> List[Dict[str, Any]]:
    """Expand question groups into individual questions."""
    expanded = []
    for question in questions:
        if question.get('type') == 'group' and 'questions' in question:
            # Expand the group: add each question from the questions array
            for sub_question in question['questions']:
                # Inherit code_link and instruction from parent if not present in sub-question
                if 'code_link' in question and 'code_link' not in sub_question:
                    sub_question['code_link'] = question['code_link']
                if 'instruction' in question and 'instruction' not in sub_question:
                    sub_question['instruction'] = question['instruction']
                expanded.append(sub_question)
        else:
            # Regular question, add as-is
            expanded.append(question)
    return expanded


def create_form(service: Any, questions: List[Dict[str, Any]], form_title: str) -> str:
    """Create the Google Form with all questions."""
    # Create the form
    form = {
        'info': {
            'title': form_title,
            'documentTitle': form_title
        }
    }
    
    try:
        created_form = service.forms().create(body=form).execute()
        form_id = created_form['formId']
        print(f"? Form created: {created_form['responderUri']}")
        print(f"   Form ID: {form_id}")
        
        # Set form to anonymous (no sign-in required)
        print("? Setting form to anonymous (no sign-in required)...")
        # Get credentials for Drive API access
        creds_for_drive = None
        try:
            from google.oauth2.credentials import Credentials
            token_file = 'token.json'
            if Path(token_file).exists():
                creds_for_drive = Credentials.from_authorized_user_file(token_file, SCOPES)
        except:
            pass
        
        if update_form_settings(service, form_id, require_login=False, credentials=creds_for_drive):
            print("? Form settings updated (email collection disabled)")
            print("⚠️  IMPORTANT: The 'Require sign-in' setting must be manually disabled:")
            print(f"   1. Go to: https://docs.google.com/forms/d/{form_id}/edit")
            print("   2. Click the Settings (gear) icon at the top")
            print("   3. Under 'Responses', uncheck 'Require sign-in to view this form'")
            print("   4. Click 'Save'")
        else:
            print("⚠️  Warning: Could not update all form settings")
            print("   Please manually disable 'Require sign-in' in the form settings")
        
        # Expand question groups into individual questions
        expanded_questions = expand_question_groups(questions)
        
        # Add questions
        requests = []
        item_index = 0
        
        for question in expanded_questions:
            if question.get('type') == 'section':
                requests.append(create_section_break(question['title'], item_index))
                item_index += 1
            elif question.get('type') == 'scale':
                requests.append(create_scale_question(question, item_index))
                item_index += 1
            elif question.get('type') == 'checkbox':
                requests.append(create_checkbox_question(question, item_index))
                item_index += 1
            elif question.get('type') == 'multiple_choice':
                requests.append(create_multiple_choice_question(question, item_index))
                item_index += 1
            elif question.get('type') == 'short_answer':
                requests.append(create_text_question(question, item_index, is_paragraph=False))
                item_index += 1
            elif question.get('type') == 'paragraph':
                requests.append(create_text_question(question, item_index, is_paragraph=True))
                item_index += 1
            else:
                print(f"⚠️  Unknown question type: {question.get('type')} for {question.get('title', 'unknown')}")
        
        # Batch update the form in smaller chunks to avoid API limits
        if requests:
            batch_size = 20  # Process 20 items at a time
            total_added = 0
            for i in range(0, len(requests), batch_size):
                batch = requests[i:i + batch_size]
                batch_update = {
                    'requests': batch
                }
                try:
                    service.forms().batchUpdate(formId=form_id, body=batch_update).execute()
                    total_added += len(batch)
                    print(f"? Added batch {i//batch_size + 1} ({len(batch)} items, {total_added}/{len(requests)} total)")
                except HttpError as error:
                    print(f"? Error adding batch {i//batch_size + 1}: {error}")
                    if hasattr(error, 'resp') and error.resp.status == 500:
                        print(f"   Retrying batch {i//batch_size + 1}...")
                        import time
                        time.sleep(2)  # Wait 2 seconds before retry
                        try:
                            service.forms().batchUpdate(formId=form_id, body=batch_update).execute()
                            total_added += len(batch)
                            print(f"? Successfully added batch {i//batch_size + 1} on retry")
                        except Exception as retry_error:
                            print(f"? Retry failed: {retry_error}")
                            # Continue with next batch
                    else:
                        raise  # Re-raise if not a 500 error
            print(f"? Added {total_added} items to the form")
        
        return form_id
        
    except HttpError as error:
        print(f"? An error occurred: {error}")
        if hasattr(error, 'error_details'):
            print(f"   Error details: {error.error_details}")
        if hasattr(error, 'resp'):
            print(f"   Status code: {error.resp.status}")
            try:
                error_data = json.loads(error.resp.data.decode()) if error.resp.data else {}
                print(f"   Error message: {error_data.get('error', {}).get('message', 'No message')}")
            except:
                pass
        print(f"\n   Possible issues:")
        print(f"   - Google Forms API might not be enabled for the service account")
        print(f"   - Service account might not have proper permissions")
        print(f"   - Google Forms API may require user OAuth (not service accounts)")
        print(f"   - API might be temporarily unavailable (500 error)")
        return None
    except Exception as e:
        print(f"? Unexpected error: {e}")
        import traceback
        traceback.print_exc()
        return None


def main():
    parser = argparse.ArgumentParser(description='Create Google Form from JSON question files')
    parser.add_argument('--credentials', help='Path to OAuth client credentials JSON file')
    parser.add_argument('--use-embedded', action='store_true', 
                       help='[DEPRECATED] Use embedded service account key - no longer supported for security reasons')
    parser.add_argument('--questions-dir', default='questions', help='Directory containing JSON question files')
    parser.add_argument('--title', default='Java Sentiment Analysis Lab', 
                       help='Form title')
    parser.add_argument('--update-form-id', help='Update settings for an existing form (form ID)')
    parser.add_argument('--require-login', action='store_true', 
                       help='Require login (default: anonymous, no login required)')
    
    args = parser.parse_args()
    
    # If updating an existing form, just update settings and exit
    if args.update_form_id:
        # Determine authentication method
        use_oauth = False
        service_account_info = None
        credentials_file = None
        
        if args.use_embedded:
            print("⚠️  Error: --use-embedded flag is no longer supported for security reasons.")
            print("   Please provide a credentials file using --credentials instead.")
            return
        elif args.credentials:
            if not Path(args.credentials).exists():
                print(f"? Credentials file not found: {args.credentials}")
                return
            with open(args.credentials, 'r') as f:
                creds_data = json.load(f)
            
            if 'installed' in creds_data or 'web' in creds_data:
                use_oauth = True
                credentials_file = args.credentials
            elif creds_data.get('type') == 'service_account':
                service_account_info = creds_data
            else:
                print("? Could not determine credential type.")
                return
        else:
            print("? Either provide --credentials file or use --use-embedded flag")
            return
        
        print("\n?? Authenticating with Google...")
        try:
            if use_oauth:
                service = authenticate_oauth(credentials_file)
            else:
                service = authenticate_service_account(service_account_info)
            print("? Authentication successful")
        except Exception as e:
            print(f"? Authentication failed: {e}")
            return
        
        print(f"\n?? Updating form settings for: {args.update_form_id}")
        require_login = args.require_login
        status = "require login" if require_login else "anonymous (no login required)"
        print(f"   Setting form to {status}...")
        
        # Get credentials for Drive API
        creds_for_drive = None
        try:
            from google.oauth2.credentials import Credentials
            token_file = 'token.json'
            if Path(token_file).exists():
                creds_for_drive = Credentials.from_authorized_user_file(token_file, SCOPES)
        except:
            pass
        
        if update_form_settings(service, args.update_form_id, require_login=require_login, credentials=creds_for_drive):
            print(f"? Form settings updated")
            if not require_login:
                print("⚠️  IMPORTANT: Manually disable 'Require sign-in' in form settings:")
                print(f"   1. Go to: https://docs.google.com/forms/d/{args.update_form_id}/edit")
                print("   2. Click Settings (gear) icon")
                print("   3. Uncheck 'Require sign-in to view this form'")
            print(f"   View form: https://docs.google.com/forms/d/{args.update_form_id}/edit")
        else:
            print(f"? Failed to update form settings")
        return
    
    # Check if questions directory exists
    if not Path(args.questions_dir).exists():
        print(f"? Questions directory not found: {args.questions_dir}")
        return
    
    # Determine authentication method
    use_oauth = False
    service_account_info = None
    credentials_file = None
    
    if args.use_embedded:
        print("⚠️  Error: --use-embedded flag is no longer supported for security reasons.")
        print("   Please provide a credentials file using --credentials instead.")
        return
    elif args.credentials:
        # Load from file and determine type
        if not Path(args.credentials).exists():
            print(f"? Credentials file not found: {args.credentials}")
            return
        with open(args.credentials, 'r') as f:
            creds_data = json.load(f)
        
        if 'installed' in creds_data or 'web' in creds_data:
            # OAuth client credentials
            use_oauth = True
            credentials_file = args.credentials
        elif creds_data.get('type') == 'service_account':
            # Service account
            service_account_info = creds_data
        else:
            print("? Could not determine credential type. Expected OAuth client credentials or service account.")
            return
    else:
        print("? Either provide --credentials file or use --use-embedded flag")
        return
    
    print(f"?? Loading questions from {args.questions_dir}/...")
    questions = load_questions_from_directory(args.questions_dir)
    print(f"? Loaded {len(questions)} items")
    
    print("\n?? Authenticating with Google...")
    try:
        if use_oauth:
            print("   Using OAuth authentication (browser will open)...")
            service = authenticate_oauth(credentials_file)
        else:
            print("   Using service account authentication...")
            service = authenticate_service_account(service_account_info)
        print("? Authentication successful")
    except Exception as e:
        print(f"? Authentication failed: {e}")
        import traceback
        traceback.print_exc()
        return
    
    print(f"\n?? Creating form: {args.title}")
    form_id = create_form(service, questions, args.title)
    
    if form_id:
        print(f"\n?? Form created successfully!")
        print(f"   View form: https://docs.google.com/forms/d/{form_id}/edit")
        print(f"   Share form: https://docs.google.com/forms/d/{form_id}/viewform")


if __name__ == '__main__':
    main()
