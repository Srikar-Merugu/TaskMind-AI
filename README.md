# TaskMind AI

TaskMind AI is a modern, clean-architecture Android application built with Kotlin that merges traditional task management with intelligent AI capabilities. 

Built as a demonstration of production-ready Android development, TaskMind AI features a beautiful Canvas-based UI, realtime cloud synchronization, and smart sub-task suggestions powered by Google's Gemini API.

## 🚀 Features

### Phase 1: Authentication & User Accounts
- Secure Firebase Email/Password Authentication
- Premium Login & Registration UI with smooth transitions
- Persistent session state management

### Phase 2: Realtime Database & MVVM Structure
- Real-time task synchronization via Firebase Realtime Database
- Clean MVVM (Model-View-ViewModel) Architecture
- LiveData & Kotlin Coroutines for reactive, non-blocking UI updates
- Add, Edit, and Delete tasks with immediate UI reflection

### Phase 3: Advanced Custom UI
- Custom `Canvas`-based Circular Progress Ring to track daily completion
- Animated Strike-through effects for completed tasks
- Smooth screen-to-screen slide transitions using `overridePendingTransition`

### Phase 4: Network Architecture
- Robust `Retrofit` client implementation with `Gson` parsing
- Coroutine-based API calls (`suspend` functions)
- Structured `Resource` wrapper for Success, Error, and Loading state management

### Phase 5 & 6: Gemini AI Smart Features
- Integrated Google Gemini AI via Retrofit
- Context-aware prompting: AI reads your current tasks to provide relevant advice
- Smart Actions: "AI Assist" button automatically generates sub-task suggestions for complex tasks
- Real-time Chat UI with "Typing..." animations and distinctive chat bubbles

### Phase 7: Cloud Messaging (FCM)
- Firebase Cloud Messaging integration for remote notifications
- Local system notifications with channels for task reminders
- Background/Foreground message handling
- Runtime notification permissions for Android 13+ (Tiramisu)

### Phase 8 & 9: UX Polish & Robustness
- Spring-physics micro-interactions on Floating Action Buttons
- Staggered entry animations for chat messages
- Comprehensive Error Handling: specific messaging for API Rate Limits (429) and Offline states (`UnknownHostException`)
- Non-intrusive `Snackbar` alerts for graceful degradation

## 🛠 Tech Stack
- **Language**: Kotlin
- **Architecture**: MVVM
- **UI**: XML Layouts, Material Design Components, Custom Views
- **Async & Reactive**: Kotlin Coroutines, LiveData, ViewModel
- **Networking**: Retrofit2, Gson
- **Backend & Auth**: Firebase Auth, Firebase Realtime Database, Firebase Cloud Messaging (FCM)
- **AI**: Google Gemini Pro API

## 📋 Evaluation Demo Flow

To verify the functionality of the app, follow this flow:
1. **Launch App**: Observe the Login Screen. Register a new user account.
2. **Dashboard**: Observe the Progress Ring (0%). Click the animated `+` FAB to add a task.
3. **Database Sync**: Add a few tasks. Then toggle a checkbox and watch the Progress Ring animate smoothly.
4. **AI Context Injection**: Click an existing task to edit it, then tap **AI Assist**. The app will transition to the AI Chat screen and automatically ask Gemini to break the task down into sub-tasks.
5. **AI Chat Animations**: Observe the "Typing..." loader and the slide-in animation of the AI's response. Ask a follow-up question.
6. **Network Robustness**: Turn off internet access and try sending a message. Observe the graceful `Snackbar` indicating offline status.
7. **Firebase Notifications**: Application is primed to receive FCM messages via the backend.

---
*Built as a Phase 10 validation project for Industry-level UI/UX and Clean Architecture.*
