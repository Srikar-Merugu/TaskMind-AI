# TaskMind AI - API Setup Guide

To ensure all features of TaskMind AI work correctly, you need to provide your own API keys for Firebase and Gemini.

## 1. Firebase Configuration (Sign-In & Database)
The project currently uses a placeholder `google-services.json`.
1. Go to the [Firebase Console](https://console.firebase.google.com/).
2. Create or select a project.
3. Add an **Android App** with package name `com.taskmind.app`.
4. Download the `google-services.json` file.
5. **IMPORTANT**: Replace the entire content of `app/google-services.json` with your downloaded file. Do not just copy the API key, as the Project ID and App ID must also match.
6. Enable **Email/Password** authentication in the Firebase Auth tab.
7. **Enable Required APIs**: Visit the following link to enable the Identity Toolkit for your project (replace `YOUR_PROJECT_NUMBER` with `866439577742` as seen in your logs):
   [Enable Identity Toolkit](https://console.developers.google.com/apis/api/identitytoolkit.googleapis.com/overview?project=866439577742)
   [Enable Firebase Installations](https://console.developers.google.com/apis/api/firebaseinstallations.googleapis.com/overview?project=866439577742)

## 2. Gemini AI Configuration (Chat & Subtasks)
I have externalized the Gemini key management to keep your keys secure.
1. Get an API key from [Google AI Studio](https://aistudio.google.com/).
2. Open the `local.properties` file in the root of the project.
3. Add the following line:
   ```properties
   gemini.api.key=YOUR_ACTUAL_API_KEY_HERE
   ```
4. **Enable the API**: Visit the following link and click **"Enable"** to activate the AI service for your project (replace `YOUR_PROJECT_NUMBER` with `595471646334` if it matches your error):
   [Enable Generative Language API](https://console.developers.google.com/apis/api/generativelanguage.googleapis.com/overview?project=595471646334)
   
5. **Sync Project with Gradle Files** in Android Studio.

## Safety Features Implemented
- **Login Safety**: If the Firebase key is invalid, the app will show a clear error Snackbar instead of crashing.
- **AI Safety**: If the Gemini key is missing, the chat activity will show a helpful warning dialog.
- **Build Safety**: Keys are read from `local.properties` and injected via `BuildConfig`, so they are never accidentally committed to version control.

## Troubleshooting 403 Errors (Access Denied)
If the AI chat shows **"Access denied (403)"** or **"API_KEY_SERVICE_BLOCKED"**:
1. Go to **APIs & Services > Credentials** in the [Google Cloud Console](https://console.developers.google.com/).
2. Click on the API Key you are using (the one in your `local.properties`).
3. Scroll down to **API restrictions**:
   - **RECOMMENDED**: Select **"Don't restrict key"** while testing.
   - **OR**: If you must restrict, ensure **Generative Language API** is added to the list of allowed APIs.
4. Check **Application restrictions**:
   - Set to **"None"** while troubleshooting to ensure your IP or package name isn't being blocked.
