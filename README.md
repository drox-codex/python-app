# Python IDE for Android 🐍📱

[![Android CI](https://github.com/developer/python-ide-android/actions/workflows/android.yml/badge.svg)](https://github.com/developer/python-ide-android/actions/workflows/android.yml)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-blue.svg?logo=kotlin)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-2024.09.00-4285F4.svg?logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Material 3](https://img.shields.io/badge/Material%203-Enabled-7C3AED.svg)](https://m3.material.io)
[![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg?logo=android)](https://android.com)
[![Web Showcase](https://img.shields.io/badge/Website-GitHub%20Pages-3B82F6.svg?logo=github)](https://drox-codex.github.io/python-app/)
[![License](https://img.shields.io/badge/License-Strict%20Non--Commercial-red.svg)](https://drox-codex.github.io/python-app/#license)

> **بيئة تطوير بايثون كاملة واحترافية على هاتفك الأندرويد**  
> **A serious mobile-first Python development environment inspired by modern professional IDEs, optimized specifically for Android.**  
> 🌐 **Official Web Portal & Interactive Showcase:** [https://drox-codex.github.io/python-app/](https://drox-codex.github.io/python-app/)  
> ⚖️ **License & Terms:** [https://drox-codex.github.io/python-app/#license](https://drox-codex.github.io/python-app/#license)

---

## 🌟 English Overview / نظرة عامة بالعربية

### English
**Python IDE** is a modern, lightweight, mobile-first Python development environment designed for phones and tablets. Built with 100% Kotlin and Jetpack Compose, it features a sleek dark navy glass aesthetic, Python syntax highlighting, an integrated Python evaluator, interactive terminal, pip package manager, Git version control, step debugger, and real filesystem storage.

### العربية
**Python IDE** هو تطبيق أندرويد متطور يوفر بيئة تطوير متكاملة للغة بايثون على الهواتف والأجهزة اللوحية. تم بناؤه بالكامل باستخدام لغة Kotlin وإطار العمل الحديث Jetpack Compose، مع تصميم داكن عصري مستوحى من أدوات المطورين الاحترافية (VS Code / Android Studio). يدعم التطبيق تلوين الشيفرة البرمجية، محرك تشغيل بايثون مدمج، شاشة طرفية (Terminal)، مدير حزم Pip، إدارة المستودعات Git، فاحص الأخطاء والمتغيرات، والوصول الفعلي لنظام الملفات.

---

## ✨ Key Features / أبرز المميزات

| المميزات | Description / الوصف |
| :--- | :--- |
| 📝 **Code Editor / محرر الأكواد** | Python syntax highlighting (keywords, builtins, strings, f-strings, comments, numbers), line numbers, monospace font, undo/redo stack, search & replace, and a mobile accessory bar with quick punctuation/tab insert chips. |
| 📁 **Project File Explorer / مستكشف الملفات** | Real file system management on Android storage. Full support for creating files/folders, renaming, deleting, navigating subdirectories, and viewing file metadata. |
| ⚡ **Run & Output / تشغيل الأكواد والنتيجة** | Pluggable `PythonRuntime` architecture. Includes a built-in evaluator supporting expressions, loops (`for i in range(...)`), assignments, functions, and interactive `input()` prompts with stdout/stderr capture and return codes. |
| 💻 **Interactive Terminal / محطة الأوامر** | Monospace command prompt with built-in commands (`python --version`, `python <file>`, `pip install`, `pip list`, `ls`, `cat`, `pwd`, `clear`, `help`). Designed to connect with Termux runtime bridge. |
| 📦 **Package Manager / إدارة الحزم** | Visual pip package manager categorized by *All*, *Popular*, and *Installed* packages with live search, metadata, and install/uninstall workflows (`requests`, `numpy`, `pandas`, `flask`, `beautifulsoup4`, etc.). |
| 🌿 **Git Version Control / إدارة الإصدارات** | Branch status, uncommitted changes tracker, recent commit logs, clone from URL, stage & commit dialogs, and push/pull workflows. |
| 🐞 **Debugger & Inspector / فاحص الأخطاء والمتغيرات** | Tabbed interface featuring *Variables* inspection (values and data types), *Call Stack* traces, *Breakpoints*, and execution controls (*Continue*, *Step Over*, *Stop*). |
| 🚀 **Project Templates / قوالب المشاريع** | One-tap project generators for *Python Empty*, *Flask Web App*, *Data Science*, *Machine Learning*, *2D Game*, and *Kivy Android App*. |
| 🌐 **Localization / تعدد اللغات** | Seamless bilingual support for **العربية (Arabic with RTL)** and **English (with LTR)** with real-time in-app switching and layout mirroring. |
| 🎨 **Pro Developer Theme / تصميم داكن احترافي** | Dark navy/black background (`#0B0F19`), surface cards (`#111827`), electric blue accents (`#3B82F6`), emerald run buttons, and adaptive Material 3 components. |

---

## 🏗️ Architecture & Tech Stack / البنية الهندسية

The application follows modern Android **Clean Architecture & MVVM (Model-View-ViewModel)** principles:

```
app/src/main/java/com/example/
├── MainActivity.kt                  # Activity with Edge-to-Edge and RTL/LTR provider
├── data/
│   ├── execution/
│   │   ├── PythonRuntime.kt         # Runtime interface (Builtin, Termux, Embedded backends)
│   │   └── TerminalSession.kt       # Terminal command processor & history
│   ├── model/                       # Immutable domain models (Project, File, Package, Git, Debug)
│   └── repository/
│       ├── ProjectRepository.kt     # Real Android filesystem I/O in context.filesDir/projects
│       ├── PackageManagerRepository.kt # Package state & pip simulation
│       ├── GitRepository.kt         # Branching and commit history management
│       └── SettingsRepository.kt    # In-memory and persistent user preferences
└── ui/
    ├── components/
    │   ├── BottomNavBar.kt          # M3 bottom navigation and editor action bars
    │   └── PythonSyntaxHighlighter.kt # Fast regex-based Compose AnnotatedString syntax parser
    ├── localization/
    │   └── Strings.kt               # Centralized Arabic & English translations
    ├── screens/
    │   ├── WelcomeScreen.kt         # Splash & onboarding screen
    │   ├── HomeScreen.kt            # Projects dashboard & quick tools grid
    │   ├── ExplorerScreen.kt        # File tree manager & creation dialogs
    │   ├── EditorScreen.kt          # Professional code editor with gutter line numbers
    │   ├── OutputScreen.kt          # Execution console with interactive stdin input
    │   ├── TerminalScreen.kt        # Interactive CLI terminal
    │   ├── PackagesScreen.kt        # Visual pip package installer
    │   ├── GitScreen.kt             # Version control interface
    │   ├── DebuggerScreen.kt        # Variable inspector & step debugger
    │   ├── TemplatesScreen.kt       # Starter template cards
    │   ├── SettingsScreen.kt        # App configuration (Theme, Language, Fonts)
    │   └── AboutScreen.kt           # Platform vision & capability roadmap
    ├── theme/                       # Custom developer color palette, typography & theme
    └── viewmodel/
        └── IdeViewModel.kt          # Unified state coordinator backed by StateFlow
```

### Technology Highlights
* **Language:** Kotlin 2.2.10
* **UI Toolkit:** Jetpack Compose with Material Design 3
* **Concurrency:** Kotlin Coroutines & `StateFlow`
* **Architecture:** MVVM + Clean Repository Pattern
* **Storage:** Sandboxed Android Internal Storage (`context.filesDir`)
* **Testing:** Local JVM Unit Tests + Robolectric

---

## 🛠️ Build & Installation / خطوات البناء والتثبيت

### Prerequisites
* **Android Studio** Ladybug (2024.2+) or IntelliJ IDEA with Android Plugin
* **JDK 17** or **JDK 21**
* **Android SDK** API 36 (Minimum API 24 - Android 7.0+)

### Building from Command Line

1. **Clone the repository:**
   ```bash
   git clone https://github.com/developer/python-ide-android.git
   cd python-ide-android
   ```

2. **Run Unit & Robolectric Tests:**
   ```bash
   ./gradlew testDebugUnitTest
   ```

3. **Assemble Debug APK:**
   ```bash
   ./gradlew assembleDebug
   ```

The compiled APK will be generated at:
```
app/build/outputs/apk/debug/app-debug.apk
```

---

## 🔄 CI/CD with GitHub Actions

The repository includes a GitHub Actions workflow located at `.github/workflows/android.yml` that:
- Automatically validates the code on every `push` and `pull_request` to `main`.
- Sets up JDK 17 with Gradle caching.
- Runs the complete unit and Robolectric test suite (`./gradlew testDebugUnitTest`).
- Builds the debug APK (`./gradlew assembleDebug`).
- Uploads the generated APK as a downloadable GitHub Actions artifact.

---

## 📱 Screenshots / لقطات من التطبيق

| 1. شاشة الترحيب | 2. الصفحة الرئيسية | 3. مستكشف الملفات | 4. محرر الأكواد |
| :---: | :---: | :---: | :---: |
| Welcome Screen | Home Dashboard | File Explorer | Code Editor |

| 5. تشغيل الكود | 6. شاشة Terminal | 7. إدارة الحزم | 8. إدارة Git |
| :---: | :---: | :---: | :---: |
| Run / Output | Interactive Shell | Package Manager | Git Control |

| 9. فحص الأخطاء | 10. قوالب المشاريع | 11. الإعدادات | 12. حول التطبيق |
| :---: | :---: | :---: | :---: |
| Step Debugger | Templates Grid | App Settings | About & Roadmap |

---

## 🗺️ Completed Milestones & Capabilities

- [x] Full Project File Management & Editing
- [x] Syntax Highlighting with Line Numbers
- [x] Builtin Python Evaluator with I/O Stream
- [x] Interactive Terminal & Package Browser
- [x] Step Debugger & Git UI Architecture
- [x] Bilingual Support (العربية & English)
- [x] Direct Termux Socket Bridge Integration
- [x] Chaquopy / Embedded CPython 3.12 Engine
- [x] Git Native JGit / Libgit2 Backend
- [x] Jupyter Notebook (.ipynb) Mobile Viewer & Runner

---

## 📄 License & Non-Commercial Terms / الترخيص وشروط الاستخدام غير التجاري

This project is licensed under a **Strict Non-Commercial License**.

* 🌐 **Live Web Showcase & License Portal:** [https://drox-codex.github.io/python-app/#license](https://drox-codex.github.io/python-app/#license)
* 📄 **Repository License File:** [LICENSE](LICENSE)

### 🇸🇦 تنبيه هام بشأن الاستخدام والترخيص
* **رابط الترخيص الرسمي على الويب:** [https://drox-codex.github.io/python-app/#license](https://drox-codex.github.io/python-app/#license)
* **يُمنع منعاً باتاً استغلال هذا التطبيق تجارياً أو بيعه أو إعادة نشره كمنتج مدفوع بأي شكل من الأشكال.**
* **يُمنع فرض رسوم اشتراك، شراء داخل التطبيق، أو دمج إعلانات ربحية.**
* **التطبيق متاح ومجاني 100% للأغراض التعليمية، البحثية، والتعلم الشخصي فقط.**

### 🇬🇧 Non-Commercial Notice
* **Official Web License Page:** [https://drox-codex.github.io/python-app/#license](https://drox-codex.github.io/python-app/#license)
* **STRICTLY PROHIBITED:** Selling, leasing, sublicensing, or distributing this software for profit, fee, or paid subscription.
* **STRICTLY PROHIBITED:** Bundling monetized ads or publishing paid derivative apps on marketplaces.
* **PERMITTED:** Free personal, academic, and non-commercial educational use.
