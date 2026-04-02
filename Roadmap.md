# Sahtek / Sahtek Online - Project Roadmap

Sahtek is a modern medical platform designed to bridge the gap between patients and healthcare professionals. This roadmap outlines the development journey for the Mobile App (Kotlin/Compose), Backend integration, and the Web platform.

---

## 📊 Current Progress
- [x] **Project Setup**: Android Studio, Kotlin, Jetpack Compose, MVVM Architecture.
- [x] **Navigation**: Implementation of `AppNavGraph`, `Screen.kt`, and `MainActivity` with distinct flows for Patient and Doctor.
- [x] **Authentication UI**: Role Selection, Login, Patient Signup, and Doctor Signup screens fully designed.
- [x] **Network Layer**: Retrofit client configured with `AuthApiService` and `PatientApiService`. Base URL and Logging Interceptor active.
- [x] **Theming & Assets**: Clean medical UI with soft blue accents, rounded cards, and updated app icons.
- [x] **Initial Patient Flow**: `PatientPatientsPage` and basic structure for browsing doctors.
- [x] **Initial Doctor Flow**: `DoctorHomeScreen` created to start the professional dashboard.

---


---

## 📱 Mobile App Roadmap

### Phase 1: Foundation & Authentication (High Priority)
*   **Objective**: Ensure a secure and seamless entry point for all users.
*   **Tasks**:
    - [x] Design Role Selection Screen (Patient/Doctor)
    - [x] Implement Login Screen UI
    - [x] Implement Patient/Doctor Signup UI
    - [x] Integrate Auth API (Login/Register calls)
    - [ ] Handle JWT Token storage (EncryptedSharedPreferences)
    - [ ] Auto-login logic (Splash Screen check)
*   **Expected Output**: A fully functional authentication flow connecting to the backend.

### Phase 2: Patient Flow (High Priority)
*   **Objective**: Provide patients with tools to find doctors and manage health data.
*   **Tasks**:
    - [x] **Home Dashboard**: Initial UI structure for patient navigation.
    - [x] **Doctor Search/List**: UI for browsing available healthcare providers.
    - [ ] **Doctor Details**: View doctor bio, ratings, and availability.
    - [ ] **AI Symptom Checker**: UI for inputting symptoms for analysis.
*   **Expected Output**: Patients can navigate the app and browse medical services.

### Phase 3: Doctor Flow (Medium Priority)
*   **Objective**: Enable doctors to manage their practice and patients.
*   **Tasks**:
    - [x] **Doctor Dashboard**: Initial home screen for the doctor role.
    - [ ] **Appointment Management**: Accept, decline, or reschedule requests.
    - [ ] **Patient Records**: View medical history shared by patients.
*   **Expected Output**: A dedicated workspace for healthcare providers.

### Phase 4: Appointments & Consultations (High Priority)
*   **Objective**: The core functionality of the platform.
*   **Tasks**:
    - [x] **Booking System**: Calendar integration for choosing slots.
    - [x] **Appointment Status**: Real-time updates (Pending, Confirmed, Completed).
    - [ ] **Prescription View**: Digital prescriptions after consultations.
*   **Expected Output**: End-to-end booking flow between Patient and Doctor.

### Phase 5: Profile & Settings (Medium Priority)
*   **Objective**: Personalization and account management.
*   **Tasks**:
    - [x] **Profile Management**: Edit name, photo, and medical info.
    - [x] **Settings**: Language, dark mode toggle, and privacy.
    - [x] **Logout**: Securely clear session data and navigate to Login.
*   **Expected Output**: Users can manage their identity and app preferences.

### Phase 6: AI Analysis & Notifications (Low Priority)
*   **Objective**: Advanced features for better user engagement.
*   **Tasks**:
    - [x] **AI Result Pages**: Detailed breakdown of AI health analysis.
    - [ ] **Push Notifications**: Reminders for appointments and messages.
    - [ ] **In-app Messaging**: Real-time chat between doctor and patient.
*   **Expected Output**: An intelligent and interactive medical assistant experience.

---

## ☁️ Backend Integration Roadmap
*   **Phase 1**: Authentication & User Profiles (Syncing mobile with existing DB).
*   **Phase 2**: Doctor Directory & Scheduling logic.
*   **Phase 3**: AI Model API integration for health analysis.
*   **Phase 4**: Notification Service (Firebase Cloud Messaging).

---

## 🛠 Tech Stack Summary
- **Mobile**: Kotlin, Jetpack Compose, Coroutines/Flow, Retrofit, OkHttp.
- **Architecture**: MVVM (Model-View-ViewModel).
- **Backend**: (Existing) hosted on Render.
- **Design**: Material 3, Clean Medical Style.

---

## 🔮 Future Improvements
- [ ] **Video Consultations**: Integrated tele-medicine video calls.
- [ ] **Wearable Integration**: Sync data from Google Fit / Apple Health.
- [ ] **Offline Mode**: Cache medical records for viewing without internet.
- [ ] **Multi-language Support**: Arabic, French, and English.
