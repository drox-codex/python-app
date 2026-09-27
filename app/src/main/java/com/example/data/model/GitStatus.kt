package com.example.data.model

data class GitStatus(
    val currentBranch: String = "main",
    val remoteUrl: String = "https://github.com/developer/myproject.git",
    val isClean: Boolean = false,
    val uncommittedFiles: List<String> = listOf("main.py (modified)", "utils.py (modified)", "requirements.txt (untracked)"),
    val recentCommits: List<GitCommit> = listOf(
        GitCommit("8fa21e4", "Initial project structure", "Today, 09:30", "Developer"),
        GitCommit("3c74901", "Add core helper utilities", "Yesterday, 18:20", "Developer")
    ),
    val branches: List<String> = listOf("main", "feature/auth", "dev")
)

data class GitCommit(
    val hash: String,
    val message: String,
    val timestamp: String,
    val author: String
)
