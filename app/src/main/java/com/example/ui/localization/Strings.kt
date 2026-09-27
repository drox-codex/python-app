package com.example.ui.localization

enum class AppLanguage(val code: String, val displayName: String, val isRtl: Boolean) {
    ARABIC("ar", "العربية", true),
    ENGLISH("en", "English", false)
}

object LocalizedStrings {
    fun get(key: String, language: AppLanguage): String {
        return when (language) {
            AppLanguage.ARABIC -> arabicStrings[key] ?: englishStrings[key] ?: key
            AppLanguage.ENGLISH -> englishStrings[key] ?: key
        }
    }

    private val arabicStrings = mapOf(
        "app_title" to "Python IDE",
        "splash_subtitle_en" to "Code • Run • Learn • Build",
        "splash_subtitle_ar" to "بيئة تطوير بايثون كاملة على هاتفك",
        "start_now" to "ابدأ الآن",
        "skip" to "تخطي",

        // Navigation
        "nav_home" to "الرئيسية",
        "nav_projects" to "المشاريع",
        "nav_packages" to "حزم",
        "nav_settings" to "الإعدادات",

        // Home
        "my_projects" to "مشاريعي",
        "new_project" to "+ مشروع جديد",
        "search_projects" to "بحث في المشاريع...",
        "quick_tools" to "أدوات سريعة",
        "tool_terminal" to "Terminal",
        "tool_terminal_sub" to "محطة الأوامر",
        "tool_packages" to "Packages",
        "tool_packages_sub" to "إدارة الحزم",
        "tool_git" to "Git",
        "tool_git_sub" to "إدارة الإصدارات",
        "tool_settings" to "Settings",
        "tool_settings_sub" to "الإعدادات",
        "last_modified" to "آخر تعديل",
        "today" to "اليوم",
        "yesterday" to "أمس",

        // Explorer
        "explorer" to "مستكشف الملفات",
        "folder" to "مجلد",
        "new_file" to "ملف جديد",
        "new_folder" to "مجلد جديد",
        "rename" to "إعادة تسمية",
        "delete" to "حذف",
        "copy" to "نسخ",
        "file_created" to "تم إنشاء الملف بنجاح",
        "folder_created" to "تم إنشاء المجلد بنجاح",

        // Editor
        "run" to "تشغيل",
        "debug" to "تصحيح",
        "terminal" to "محطة الأوامر",
        "spaces" to "مسافات",
        "search_and_replace" to "بحث واستبدال",
        "find" to "بحث...",
        "replace" to "استبدال...",
        "replace_all" to "استبدال الكل",
        "undo" to "تراجع",
        "redo" to "إعادة",
        "save" to "حفظ",
        "saved" to "تم الحفظ",

        // Output
        "output" to "تشغيل الكود (النتيجة)",
        "output_title" to "Output",
        "clear_output" to "مسح المخرجات",
        "run_again" to "إعادة التشغيل",
        "stop" to "إيقاف",

        // Packages
        "packages_title" to "Packages",
        "search_packages" to "ابحث عن حزمة...",
        "tab_all" to "الكل",
        "tab_popular" to "المفضلة",
        "tab_installed" to "مثبتة",
        "install" to "تثبيت",
        "installed" to "مثبت",
        "uninstall" to "إلغاء التثبيت",
        "installing" to "جاري التثبيت...",

        // Git
        "git_title" to "Git",
        "git_branch" to "الفرع الحالي",
        "clone_repo" to "Clone Repository",
        "clone_repo_sub" to "استنساخ مستودع",
        "commit" to "Commit",
        "commit_sub" to "إضافة التغييرات",
        "push" to "Push",
        "push_sub" to "دفع التغييرات",
        "pull" to "Pull",
        "pull_sub" to "سحب التغييرات",
        "branches" to "Branches",
        "branches_sub" to "فروع المشروع",
        "github" to "GitHub",
        "github_sub" to "ربط بحساب GitHub",

        // Debugger
        "debugger_title" to "Debugger",
        "tab_breakpoints" to "النقاط المتوقفة",
        "tab_callstack" to "المتتبع",
        "tab_variables" to "المتغيرات",
        "continue_exec" to "متابعة",
        "step_over" to "خطوة بخطوة",
        "step_into" to "دخول",
        "stop_debug" to "إيقاف",

        // Templates
        "templates_title" to "قوالب المشاريع",
        "tpl_empty_title" to "Python Empty",
        "tpl_empty_desc" to "مشروع فارغ بسيط",
        "tpl_webapp_title" to "Web App",
        "tpl_webapp_desc" to "تطبيق ويب باستخدام Flask",
        "tpl_datascience_title" to "Data Science",
        "tpl_datascience_desc" to "تحليل البيانات مع Pandas و Matplotlib",
        "tpl_ml_title" to "Machine Learning",
        "tpl_ml_desc" to "تعلم الآلة والذكاء الاصطناعي",
        "tpl_game_title" to "Game",
        "tpl_game_desc" to "لعبة بسيطة باستخدام Pygame",
        "tpl_android_title" to "Android App",
        "tpl_android_desc" to "تطبيق أندرويد (Kivy)",
        "create_project" to "إنشاء المشروع",

        // Settings
        "settings_title" to "الإعدادات",
        "sec_general" to "عام",
        "sec_editor" to "محرر الأكواد",
        "sec_projects" to "المشاريع",
        "sec_python" to "Python",
        "sec_terminal" to "Terminal",
        "sec_other" to "أخرى",
        "theme_label" to "المظهر",
        "theme_val" to "الوضع الداكن",
        "language_label" to "لغة التطبيق",
        "projects_folder_label" to "مجلد المشاريع",
        "interpreter_label" to "Python Interpreter",
        "termux_label" to "Termux Integration",
        "termux_status" to "تم التفعيل",
        "font_size_label" to "حجم الخط",
        "tab_size_label" to "حجم المسافة البادئة (Tabs)",
        "word_wrap_label" to "التفاف الأسطر",
        "line_numbers_label" to "أرقام الأسطر",
        "auto_save_label" to "حفظ تلقائي",
        "about_label" to "حول التطبيق",
        "help_label" to "مساعدة ودعم",

        // About
        "about_tagline" to "أكثر من مجرد محرر .. بيئة تطوير متكاملة على هاتفك",
        "feat_editor" to "محرر كود قوي مع تلوين الصيغ",
        "feat_terminal" to "Terminal مدمج ومحاكاة بايثون",
        "feat_packages" to "إدارة الحزم (Pip Manager)",
        "feat_git" to "إدارة الإصدارات والتحكم (Git)",
        "feat_debugger" to "مصحح الأخطاء وفحص المتغيرات",
        "feat_more" to "قوالب مشاريع وتكامل Termux والمزيد...",
        "build_future" to "معاً نبني مستقبل أفضل"
    )

    private val englishStrings = mapOf(
        "app_title" to "Python IDE",
        "splash_subtitle_en" to "Code • Run • Learn • Build",
        "splash_subtitle_ar" to "Full Python Development Environment On Your Phone",
        "start_now" to "Get Started",
        "skip" to "Skip",

        // Navigation
        "nav_home" to "Home",
        "nav_projects" to "Projects",
        "nav_packages" to "Packages",
        "nav_settings" to "Settings",

        // Home
        "my_projects" to "My Projects",
        "new_project" to "+ New Project",
        "search_projects" to "Search projects...",
        "quick_tools" to "Quick Tools",
        "tool_terminal" to "Terminal",
        "tool_terminal_sub" to "Command Line",
        "tool_packages" to "Packages",
        "tool_packages_sub" to "Package Manager",
        "tool_git" to "Git",
        "tool_git_sub" to "Version Control",
        "tool_settings" to "Settings",
        "tool_settings_sub" to "Preferences",
        "last_modified" to "Modified",
        "today" to "Today",
        "yesterday" to "Yesterday",

        // Explorer
        "explorer" to "File Explorer",
        "folder" to "Folder",
        "new_file" to "New File",
        "new_folder" to "New Folder",
        "rename" to "Rename",
        "delete" to "Delete",
        "copy" to "Copy",
        "file_created" to "File created successfully",
        "folder_created" to "Folder created successfully",

        // Editor
        "run" to "Run",
        "debug" to "Debug",
        "terminal" to "Terminal",
        "spaces" to "Spaces",
        "search_and_replace" to "Search & Replace",
        "find" to "Find...",
        "replace" to "Replace...",
        "replace_all" to "Replace All",
        "undo" to "Undo",
        "redo" to "Redo",
        "save" to "Save",
        "saved" to "Saved",

        // Output
        "output" to "Run / Output",
        "output_title" to "Output",
        "clear_output" to "Clear Output",
        "run_again" to "Run Again",
        "stop" to "Stop",

        // Packages
        "packages_title" to "Packages",
        "search_packages" to "Search package...",
        "tab_all" to "All",
        "tab_popular" to "Popular",
        "tab_installed" to "Installed",
        "install" to "Install",
        "installed" to "Installed",
        "uninstall" to "Uninstall",
        "installing" to "Installing...",

        // Git
        "git_title" to "Git",
        "git_branch" to "Current Branch",
        "clone_repo" to "Clone Repository",
        "clone_repo_sub" to "Clone from remote URL",
        "commit" to "Commit",
        "commit_sub" to "Stage & commit changes",
        "push" to "Push",
        "push_sub" to "Push to remote",
        "pull" to "Pull",
        "pull_sub" to "Pull latest changes",
        "branches" to "Branches",
        "branches_sub" to "Manage Git branches",
        "github" to "GitHub",
        "github_sub" to "Connect GitHub account",

        // Debugger
        "debugger_title" to "Debugger",
        "tab_breakpoints" to "Breakpoints",
        "tab_callstack" to "Call Stack",
        "tab_variables" to "Variables",
        "continue_exec" to "Continue",
        "step_over" to "Step Over",
        "step_into" to "Step Into",
        "stop_debug" to "Stop",

        // Templates
        "templates_title" to "Project Templates",
        "tpl_empty_title" to "Python Empty",
        "tpl_empty_desc" to "Clean minimal script template",
        "tpl_webapp_title" to "Web App",
        "tpl_webapp_desc" to "Web app powered by Flask",
        "tpl_datascience_title" to "Data Science",
        "tpl_datascience_desc" to "Data analysis with Pandas & Matplotlib",
        "tpl_ml_title" to "Machine Learning",
        "tpl_ml_desc" to "ML & AI modeling starter",
        "tpl_game_title" to "Game",
        "tpl_game_desc" to "2D arcade game template",
        "tpl_android_title" to "Android App",
        "tpl_android_desc" to "Cross-platform mobile app (Kivy)",
        "create_project" to "Create Project",

        // Settings
        "settings_title" to "Settings",
        "sec_general" to "General",
        "sec_editor" to "Editor",
        "sec_projects" to "Projects",
        "sec_python" to "Python",
        "sec_terminal" to "Terminal",
        "sec_other" to "Other",
        "theme_label" to "Theme",
        "theme_val" to "Dark Mode",
        "language_label" to "App Language",
        "projects_folder_label" to "Projects Directory",
        "interpreter_label" to "Python Interpreter",
        "termux_label" to "Termux Integration",
        "termux_status" to "Enabled",
        "font_size_label" to "Font Size",
        "tab_size_label" to "Tab Size",
        "word_wrap_label" to "Word Wrap",
        "line_numbers_label" to "Line Numbers",
        "auto_save_label" to "Auto Save",
        "about_label" to "About Python IDE",
        "help_label" to "Help & Support",

        // About
        "about_tagline" to "More than an editor.. A complete IDE in your pocket",
        "feat_editor" to "Powerful code editor with syntax highlighting",
        "feat_terminal" to "Built-in interactive Python terminal",
        "feat_packages" to "Package management with pip integration",
        "feat_git" to "Version control & GitHub integration",
        "feat_debugger" to "Interactive debugger & variable inspector",
        "feat_more" to "Project templates, Termux bridge, and more...",
        "build_future" to "Together We Build The Future"
    )
}
