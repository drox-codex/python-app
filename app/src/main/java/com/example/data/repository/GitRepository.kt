package com.example.data.repository

import com.example.data.model.GitCommit
import com.example.data.model.GitStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class GitRepository {

    private val _status = MutableStateFlow(
        GitStatus(
            currentBranch = "main",
            remoteUrl = "https://github.com/developer/MyProject.git",
            isClean = false,
            uncommittedFiles = listOf(
                "main.py (modified)",
                "utils.py (modified)",
                "requirements.txt (untracked)"
            ),
            recentCommits = listOf(
                GitCommit("8fa21e4", "Initial project structure and main.py", "Today, 09:30", "Developer"),
                GitCommit("3c74901", "Add helper functions and utils.py", "Yesterday, 18:20", "Developer")
            ),
            branches = listOf("main", "feature/auth", "dev")
        )
    )
    val status: StateFlow<GitStatus> = _status.asStateFlow()

    fun commit(message: String) {
        val current = _status.value
        val newHash = UUID.randomUUID().toString().substring(0, 7)
        val timeStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val newCommit = GitCommit(newHash, message, "Today, $timeStr", "Developer")

        _status.value = current.copy(
            isClean = true,
            uncommittedFiles = emptyList(),
            recentCommits = listOf(newCommit) + current.recentCommits
        )
    }

    fun push(): String {
        return "Everything up-to-date. Pushed to ${_status.value.remoteUrl}"
    }

    fun pull(): String {
        return "Already up-to-date with remote ${_status.value.currentBranch}."
    }

    fun switchBranch(branch: String) {
        if (_status.value.branches.contains(branch)) {
            _status.value = _status.value.copy(currentBranch = branch)
        }
    }

    fun createBranch(name: String) {
        val clean = name.trim().replace(" ", "-")
        if (!_status.value.branches.contains(clean)) {
            _status.value = _status.value.copy(
                branches = _status.value.branches + clean,
                currentBranch = clean
            )
        }
    }
}
