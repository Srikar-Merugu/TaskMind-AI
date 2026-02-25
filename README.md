# 🧠 TaskMind AI

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Android](https://img.shields.io/badge/Android-7.0+-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/)
[![Firebase](https://img.shields.io/badge/Firebase-Auth/DB/FCM-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)](https://firebase.google.com/)
[![Gemini](https://img.shields.io/badge/Google_Gemini-AI_Mentor-4285F4?style=for-the-badge&logo=google-gemini&logoColor=white)](https://deepmind.google/technologies/gemini/)

TaskMind AI is an industry-grade Android application that elevates task management through an intelligent, AI-driven personal mentor. Built with a focus on modern **Clean Architecture**, **MVVM**, and **Material 3**, it seamlessly merges traditional productivity with the power of Google's Gemini API.

---

## ✨ Key Pillar Features

### 🤖 Intelligent AI Mentorship
- **Context-Aware Support**: The built-in AI Mentor understands your tasks. Clicking the "AI Assist" button provides instant, tailored advice.
- **Auto-Subtask Generation**: Stop overthinking. Let Gemini break down complex goals into actionable 1-2-3 steps.
- **Dynamic Chat Interface**: Professional realtime chat UI with "Typing..." indicators and smooth animations.

### 🛡️ Production-Ready Auth Flow
- **Industry Standards**: Registration flow includes real-time validation, name persistence in Firebase Realtime Database, and Material 3 design.
- **Session Persistence**: Automatic secure login using Firebase Auth.
- **UX Polish**: Custom shake animations for input errors and refined transition effects.

### 📊 Advanced Data Visualization
- **Canvas Progress Ring**: A custom-drawn circular progress view that dynamically tracks your daily completion percentage.
- **Realtime Sync**: Powered by Firebase Realtime Database for instantaneous updates across devices.
- **Reactive UI**: Built entirely on LiveData and ViewModels for a fluid, lag-free experience.

### 🔔 Smart Multi-Channel Notifications
- Full integration with **Firebase Cloud Messaging (FCM)**.
- Android 13+ runtime permission handling.
- Local system notifications with dedicated channels for productivity reminders.

---

## 🏗️ Architecture: The "Pro" Stack

TaskMind AI is built for scalability and maintainability, adhering to **SOLID** principles:

- **UI Layer**: Material 3 components, View Binding, and Activity-based navigation.
- **Domain Layer**: Clean Model-specific business logic.
- **Data Layer (Repository Pattern)**: Decoupled data sources (Firebase, Retrofit) from the UI layer.
- **API Strategy**: Coroutine-based Retrofit implementation with custom `Result` wrappers for graceful error handling.

---

## 🛠 Tech Stack & Tools

| Category | Technology |
| :--- | :--- |
| **Language** | Kotlin (Coroutines, Flow) |
| **Arch** | MVVM, Repository Pattern, Clean Architecture |
| **Backend** | Firebase Auth, Realtime Database, FCM |
| **Networking** | Retrofit 2, Gson, OkHttp |
| **AI** | Google Gemini API (Vertex AI/Generative AI) |
| **UI/UX** | Custom Canvas Views, Material 3, ViewBinding |

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog+ 
- Firebase Project (google-services.json included)
- Gemini API Key ([Get it here](https://aistudio.google.com/))

### Setup
1. **Clone the project**:
   ```bash
   git clone https://github.com/Srikar-Merugu/TaskMind-AI.git
   ```
2. **Add API Keys**:
   Create a `local.properties` file in the root and add:
   ```properties
   gemini.api.key=YOUR_API_KEY
   ```
3. **Run**:
   Sync Gradle and deploy to your device or emulator.

---

## 🤝 Contributions

Developed by **Srikar** and **Nirajlpu**. We welcome contributions that align with the high-quality standards of this project.

- **Srikar**: Lead Architectural Design & AI Integration
- **Nirajlpu**: Core Features & Collaboration

---

## 📜 License
Distributed under the MIT License. See `LICENSE` for more information.

*Built as a Phase 10 validation project for Industry-level UI/UX and Clean Architecture.*
