package com.example.data.repository

import android.content.Context
import com.example.data.model.DefaultTemplates
import com.example.data.model.Project
import com.example.data.model.ProjectFile
import com.example.data.model.ProjectTemplate
import com.example.data.model.ProjectType
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProjectRepository(private val context: Context) {

    private val projectsRoot: File = File(context.filesDir, "projects").apply {
        if (!exists()) {
            mkdirs()
        }
    }

    init {
        ensureInitialProjects()
    }

    fun getProjects(): List<Project> {
        val dirs = projectsRoot.listFiles { file -> file.isDirectory } ?: emptyArray()
        return dirs.map { dir ->
            val filesCount = dir.walkTopDown().maxDepth(3).filter { it.isFile }.count()
            val lastMod = dir.lastModified()
            val type = when {
                dir.name.contains("Web", ignoreCase = true) -> ProjectType.WEB_APP
                dir.name.contains("Network", ignoreCase = true) -> ProjectType.NETWORK
                dir.name.contains("AI", ignoreCase = true) -> ProjectType.AI_TOOLS
                else -> ProjectType.STANDARD
            }
            Project(
                id = dir.name,
                name = dir.name,
                directoryPath = dir.absolutePath,
                lastModified = lastMod,
                lastModifiedFormatted = formatDate(lastMod),
                fileCount = filesCount,
                projectType = type
            )
        }.sortedByDescending { it.lastModified }
    }

    fun getProject(projectName: String): Project? {
        val dir = File(projectsRoot, projectName)
        if (!dir.exists() || !dir.isDirectory) return null
        return Project(
            id = dir.name,
            name = dir.name,
            directoryPath = dir.absolutePath,
            lastModified = dir.lastModified(),
            lastModifiedFormatted = formatDate(dir.lastModified()),
            fileCount = dir.walkTopDown().maxDepth(3).filter { it.isFile }.count()
        )
    }

    fun createProject(name: String, template: ProjectTemplate? = null): Project {
        val cleanName = name.trim().replace(Regex("[^a-zA-Z0-9._-]"), "_")
        val projDir = File(projectsRoot, cleanName)
        if (!projDir.exists()) {
            projDir.mkdirs()
        }

        val tpl = template ?: DefaultTemplates.list.first()
        tpl.defaultFiles.forEach { (fileName, content) ->
            val targetFile = File(projDir, fileName)
            targetFile.parentFile?.mkdirs()
            targetFile.writeText(content)
        }

        return Project(
            id = cleanName,
            name = cleanName,
            directoryPath = projDir.absolutePath,
            lastModified = System.currentTimeMillis(),
            lastModifiedFormatted = formatDate(System.currentTimeMillis()),
            fileCount = tpl.defaultFiles.size,
            projectType = tpl.type
        )
    }

    fun deleteProject(projectName: String): Boolean {
        val dir = File(projectsRoot, projectName)
        return if (dir.exists()) dir.deleteRecursively() else false
    }

    fun renameProject(oldName: String, newName: String): Boolean {
        val oldDir = File(projectsRoot, oldName)
        val cleanNew = newName.trim().replace(Regex("[^a-zA-Z0-9._-]"), "_")
        val newDir = File(projectsRoot, cleanNew)
        return if (oldDir.exists() && !newDir.exists()) oldDir.renameTo(newDir) else false
    }

    fun getFiles(directoryPath: String): List<ProjectFile> {
        val dir = File(directoryPath)
        if (!dir.exists() || !dir.isDirectory) return emptyList()

        val items = dir.listFiles() ?: emptyArray()
        return items.map { item ->
            val isDir = item.isDirectory
            val size = if (isDir) 0L else item.length()
            val ext = if (isDir) "" else item.extension
            ProjectFile(
                name = item.name,
                path = item.absolutePath,
                isDirectory = isDir,
                sizeBytes = size,
                sizeFormatted = if (isDir) "مجلد" else formatFileSize(size),
                lastModifiedFormatted = formatDate(item.lastModified()),
                extension = ext
            )
        }.sortedWith(compareBy({ !it.isDirectory }, { it.name.lowercase(Locale.ROOT) }))
    }

    fun readFile(filePath: String): String {
        val file = File(filePath)
        return if (file.exists() && file.isFile) {
            file.readText()
        } else {
            ""
        }
    }

    fun saveFile(filePath: String, content: String): Boolean {
        return try {
            val file = File(filePath)
            file.parentFile?.mkdirs()
            file.writeText(content)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun createFile(parentPath: String, fileName: String, content: String = ""): Boolean {
        return try {
            val parent = File(parentPath)
            if (!parent.exists()) parent.mkdirs()
            val file = File(parent, fileName)
            if (!file.exists()) {
                file.createNewFile()
                if (content.isNotEmpty()) {
                    file.writeText(content)
                }
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun createFolder(parentPath: String, folderName: String): Boolean {
        return try {
            val folder = File(parentPath, folderName)
            if (!folder.exists()) {
                folder.mkdirs()
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun deleteItem(path: String): Boolean {
        val item = File(path)
        return if (item.exists()) {
            if (item.isDirectory) item.deleteRecursively() else item.delete()
        } else {
            false
        }
    }

    fun renameItem(oldPath: String, newName: String): Boolean {
        val oldFile = File(oldPath)
        val newFile = File(oldFile.parentFile, newName)
        return if (oldFile.exists() && !newFile.exists()) {
            oldFile.renameTo(newFile)
        } else {
            false
        }
    }

    private fun ensureInitialProjects() {
        val existing = projectsRoot.listFiles { f -> f.isDirectory }
        if (existing.isNullOrEmpty()) {
            // Create MyProject
            val myProjectDir = File(projectsRoot, "MyProject").apply { mkdirs() }
            File(myProjectDir, ".vscode").mkdirs()
            File(myProjectDir, ".vscode/settings.json").writeText("{\n  \"python.analysis.typeCheckingMode\": \"basic\"\n}")
            File(myProjectDir, "src").mkdirs()
            File(myProjectDir, "src/helpers.py").writeText("def helper():\n    return 'Helper function ready'\n")
            File(myProjectDir, "assets").mkdirs()
            File(myProjectDir, "assets/data.json").writeText("{\"status\": \"ok\"}")

            val mainPyContent = """# برنامج بسيط #
name = input("ما اسمك؟ ")
print(f"مرحباً {name}")

for i in range(5):
    print(f"الرقم : {i}")
"""
            File(myProjectDir, "main.py").writeText(mainPyContent)

            val utilsPyContent = """# دوال مساعدة
def greet(user):
    return f"أهلاً وسهلاً يا {user}"

def calculate_stats(numbers):
    total = sum(numbers)
    count = len(numbers)
    return total / count if count > 0 else 0
"""
            File(myProjectDir, "utils.py").writeText(utilsPyContent)
            File(myProjectDir, "requirements.txt").writeText("requests>=2.31.0\nnumpy>=1.26.4\n")
            File(myProjectDir, "README.md").writeText("# MyProject\n\nبيئة تطوير بايثون كاملة على هاتفك مع محرر أكواد و Terminal و Git.\n")

            // Create WebApp
            val webAppDir = File(projectsRoot, "WebApp").apply { mkdirs() }
            File(webAppDir, "app.py").writeText("print('WebApp server running on port 5000')\n")
            File(webAppDir, "requirements.txt").writeText("flask>=3.0.2\n")

            // Create NetworkScanner
            val netDir = File(projectsRoot, "NetworkScanner").apply { mkdirs() }
            File(netDir, "scanner.py").writeText("print('Scanning local subnets...')\n")

            // Create AI-Tools
            val aiDir = File(projectsRoot, "AI-Tools").apply { mkdirs() }
            File(aiDir, "pipeline.py").writeText("print('Loading machine learning model weights...')\n")
        }
    }

    private fun formatDate(millis: Long): String {
        val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
        return sdf.format(Date(millis))
    }

    private fun formatFileSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "${bytes / 1024} KB"
            else -> String.format(Locale.ROOT, "%.1f MB", bytes / (1024.0 * 1024.0))
        }
    }
}
