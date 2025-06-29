# DuBEAT - Royalty-Free Music Streaming App

<div align="center">
  <img width="200" src="https://github.com/jluispcardenas/androidmusic/raw/master/src/main/res/drawable/logo.png" alt="DuBEAT Logo"/>
  
  [![Google Play Store](https://img.shields.io/badge/Google_Play-414141?style=for-the-badge&logo=google-play&logoColor=white)](https://play.google.com/store/apps/details?id=club.codeexpert.music)
  [![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com)
  [![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)](https://www.java.com)
</div>

## Overview

DuBEAT is a modern Android music streaming application that provides access to thousands of royalty-free songs. Built with Clean Architecture principles and modern Android development practices, the app offers a seamless music discovery and streaming experience.

## Features

### 🎵 Core Functionality
- **Music Discovery**: Explore and search through an extensive catalog of royalty-free music
- **Smart Recommendations**: Personalized song recommendations based on user preferences
- **Offline Listening**: Download songs for offline playback
- **Background Playback**: Continue listening while using other apps

### 🔐 User Experience
- **Firebase Authentication**: Secure user authentication with Google Sign-In
- **Intuitive Navigation**: Bottom navigation with dedicated sections for discovery, downloads, notifications, and settings
- **Material Design**: Modern UI following Material Design guidelines

## Technical Architecture

### Architecture Pattern
- **Clean Architecture**: Separation of concerns with distinct layers
- **MVVM Pattern**: Model-View-ViewModel for UI components
- **Repository Pattern**: Centralized data access management

### Key Technologies
- **Dependency Injection**: Dagger 2 for modular and testable code
- **Local Database**: Room for efficient local data storage
- **Network Layer**: Volley for HTTP requests and file downloads
- **Image Loading**: Picasso for optimized image handling
- **Navigation**: Navigation Component for fragment-based navigation

### Backend Integration
- **API**: AWS Lambda serverless backend
- **Endpoint**: `https://560zo4tew9.execute-api.us-east-1.amazonaws.com/prod`
- **Authentication**: Firebase Auth integration

## Technical Specifications

- **Target SDK**: Android API 30 (Android 11)
- **Minimum SDK**: Android API 27 (Android 8.1)
- **Package**: `club.codeexpert.music`
- **Audio Format**: WAV files for high-quality playback
- **Architecture**: Clean Architecture with MVVM

## Development Setup

### Prerequisites
- Android Studio Arctic Fox or later
- JDK 8 or later
- Android SDK API 30

### Build Instructions
```bash
# Clone the repository
git clone https://github.com/jluispcardenas/androidmusic.git

# Navigate to project directory
cd androidmusic

# Build debug APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug
```

### Testing
```bash
# Run unit tests
./gradlew test

# Run instrumentation tests
./gradlew connectedAndroidTest
```

## Project Structure

```
src/main/java/club/codeexpert/music/
├── activities/          # Activity classes
├── data/               # Data layer (Repository, Database)
│   └── db/            # Room database entities and DAOs
├── managers/          # Business logic managers
├── services/          # Background services
└── ui/               # UI components and ViewModels
    ├── about/
    ├── discover/
    ├── downloads/
    └── notifications/
```

## Contributing

This project welcomes contributions! Please follow these guidelines:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is available for collaboration and learning purposes. Please ensure compliance with music licensing when using the streaming functionality.

## Connect

<div align="center">
  <a href="https://www.linkedin.com/in/jose-luis-cardenas-54366983">
    <img src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white" alt="LinkedIn"/>
  </a>
  <a href="mailto:jluispcardenas@gmail.com">
    <img src="https://img.shields.io/badge/Gmail-D14836?style=for-the-badge&logo=gmail&logoColor=white" alt="Gmail"/>
  </a>
</div>

---

<div align="center">
  <sub>Built with ❤️ using Android and Clean Architecture</sub>
</div>
