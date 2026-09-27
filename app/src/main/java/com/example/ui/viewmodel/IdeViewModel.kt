package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.execution.BuiltinPythonRuntime
import com.example.data.execution.PythonRuntime
import com.example.data.execution.TerminalLine
import com.example.data.execution.TerminalSession
import com.example.data.model.AppSettings
import com.example.data.model.DebuggerSession
import com.example.data.model.DefaultTemplates
import com.example.data.model.GitStatus
import com.example.data.model.PackageInfo
import com.example.data.model.Project
import com.example.data.model.ProjectFile
import com.example.data.model.ProjectTemplate
import com.example.data.repository.GitRepository
import com.example.data.repository.PackageManagerRepository
import com.example.data.repository.ProjectRepository
import com.example.data.repository.SettingsRepository
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.LocalizedStrings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

sealed class IdeScreen {
    object Welcome : IdeScreen()
    object Home : IdeScreen()
    object Explorer : IdeScreen()
    object Editor : IdeScreen()
    object Output : IdeScreen()
    object Terminal : IdeScreen()
    object Packages : IdeScreen()
    object Git : IdeScreen()
    object Debugger : IdeScreen()
    object Templates : IdeScreen()
    object Settings : IdeScreen()
    object About : IdeScreen()
}

class IdeViewModel(application: Application) : AndroidViewModel(application) {

    private val projectRepository = ProjectRepository(application)
    private val packageRepository = PackageManagerRepository()
    private val gitRepository = GitRepository()
    private val settingsRepository = SettingsRepository()
    private val pythonRuntime: PythonRuntime = BuiltinPythonRuntime()
    private val terminalSession = TerminalSession(projectRepository, packageRepository, pythonRuntime)

    // Navigation & Screen Stack
    private val _currentScreen = MutableStateFlow<IdeScreen>(IdeScreen.Welcome)
    val currentScreen: StateFlow<IdeScreen> = _currentScreen.asStateFlow()

    private val screenStack = mutableListOf<IdeScreen>(IdeScreen.Welcome)

    // Settings & Localization
    val settings: StateFlow<AppSettings> = settingsRepository.settings

    // Projects & Files
    private val _projects = MutableStateFlow<List<Project>>(emptyList())
    val projects: StateFlow<List<Project>> = _projects.asStateFlow()

    private val _activeProject = MutableStateFlow<Project?>(null)
    val activeProject: StateFlow<Project?> = _activeProject.asStateFlow()

    private val _currentFiles = MutableStateFlow<List<ProjectFile>>(emptyList())
    val currentFiles: StateFlow<List<ProjectFile>> = _currentFiles.asStateFlow()

    private val _currentDirectoryPath = MutableStateFlow<String>("")
    val currentDirectoryPath: StateFlow<String> = _currentDirectoryPath.asStateFlow()

    // Editor State
    private val _activeFilePath = MutableStateFlow<String?>(null)
    val activeFilePath: StateFlow<String?> = _activeFilePath.asStateFlow()

    private val _activeFileName = MutableStateFlow<String>("main.py")
    val activeFileName: StateFlow<String> = _activeFileName.asStateFlow()

    private val _editorCode = MutableStateFlow<String>("")
    val editorCode: StateFlow<String> = _editorCode.asStateFlow()

    private val _cursorLine = MutableStateFlow(1)
    val cursorLine: StateFlow<Int> = _cursorLine.asStateFlow()

    private val _cursorCol = MutableStateFlow(1)
    val cursorCol: StateFlow<Int> = _cursorCol.asStateFlow()

    private val undoStack = mutableListOf<String>()
    private val redoStack = mutableListOf<String>()

    // Output & Execution
    private val _outputContent = MutableStateFlow<String>("")
    val outputContent: StateFlow<String> = _outputContent.asStateFlow()

    private val _isExecuting = MutableStateFlow(false)
    val isExecuting: StateFlow<Boolean> = _isExecuting.asStateFlow()

    // Terminal
    val terminalLines: StateFlow<List<TerminalLine>> = terminalSession.lines

    // Packages
    val packages: StateFlow<List<PackageInfo>> = packageRepository.packages
    private val _packageSearchQuery = MutableStateFlow("")
    val packageSearchQuery: StateFlow<String> = _packageSearchQuery.asStateFlow()

    // Git
    val gitStatus: StateFlow<GitStatus> = gitRepository.status

    // Debugger
    private val _debuggerSession = MutableStateFlow(DebuggerSession())
    val debuggerSession: StateFlow<DebuggerSession> = _debuggerSession.asStateFlow()

    // Status snackbar / banner message
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    init {
        refreshProjects()
        // Automatically select first project as default active
        val defaultProj = _projects.value.firstOrNull { it.name == "MyProject" } ?: _projects.value.firstOrNull()
        defaultProj?.let { selectProject(it) }
    }

    fun navigateTo(screen: IdeScreen) {
        if (_currentScreen.value != screen) {
            screenStack.add(screen)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        if (screenStack.size > 1) {
            screenStack.removeAt(screenStack.size - 1)
            _currentScreen.value = screenStack.last()
            return true
        }
        return false
    }

    fun refreshProjects() {
        val list = projectRepository.getProjects()
        _projects.value = list
    }

    fun selectProject(project: Project) {
        _activeProject.value = project
        _currentDirectoryPath.value = project.directoryPath
        terminalSession.setWorkingDirectory(project.directoryPath)
        loadDirectory(project.directoryPath)

        // If main.py or first py file exists, load it into editor
        val files = projectRepository.getFiles(project.directoryPath)
        val mainPy = files.firstOrNull { it.name == "main.py" } ?: files.firstOrNull { it.isPythonFile }
        if (mainPy != null) {
            openFile(mainPy.path)
        }
    }

    fun loadDirectory(path: String) {
        _currentDirectoryPath.value = path
        _currentFiles.value = projectRepository.getFiles(path)
    }

    fun createProject(name: String, template: ProjectTemplate? = null) {
        val newProj = projectRepository.createProject(name, template)
        refreshProjects()
        selectProject(newProj)
        showMessage("تم إنشاء المشروع: ${newProj.name}")
    }

    fun deleteProject(project: Project) {
        projectRepository.deleteProject(project.name)
        refreshProjects()
        if (_activeProject.value?.id == project.id) {
            _activeProject.value = _projects.value.firstOrNull()
            _activeProject.value?.let { selectProject(it) }
        }
        showMessage("تم حذف المشروع")
    }

    fun openFile(filePath: String) {
        val file = File(filePath)
        if (file.exists() && file.isFile) {
            _activeFilePath.value = filePath
            _activeFileName.value = file.name
            val text = projectRepository.readFile(filePath)
            _editorCode.value = text
            undoStack.clear()
            redoStack.clear()
            undoStack.add(text)
            updateCursorPos(1, 1)
        }
    }

    fun updateEditorCode(newCode: String) {
        if (_editorCode.value != newCode) {
            undoStack.add(_editorCode.value)
            redoStack.clear()
            _editorCode.value = newCode
            if (settings.value.autoSave && _activeFilePath.value != null) {
                projectRepository.saveFile(_activeFilePath.value!!, newCode)
            }
        }
    }

    fun undo() {
        if (undoStack.isNotEmpty()) {
            val previous = undoStack.removeAt(undoStack.size - 1)
            redoStack.add(_editorCode.value)
            _editorCode.value = previous
            if (settings.value.autoSave && _activeFilePath.value != null) {
                projectRepository.saveFile(_activeFilePath.value!!, previous)
            }
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            val next = redoStack.removeAt(redoStack.size - 1)
            undoStack.add(_editorCode.value)
            _editorCode.value = next
            if (settings.value.autoSave && _activeFilePath.value != null) {
                projectRepository.saveFile(_activeFilePath.value!!, next)
            }
        }
    }

    fun saveCurrentFile() {
        _activeFilePath.value?.let { path ->
            val success = projectRepository.saveFile(path, _editorCode.value)
            if (success) {
                showMessage("تم حفظ الملف")
            }
        }
    }

    fun updateCursorPos(line: Int, col: Int) {
        _cursorLine.value = line
        _cursorCol.value = col
    }

    fun createNewFile(fileName: String, content: String = "") {
        val targetDir = _currentDirectoryPath.value.ifEmpty {
            _activeProject.value?.directoryPath ?: ""
        }
        if (targetDir.isNotEmpty()) {
            val success = projectRepository.createFile(targetDir, fileName, content)
            if (success) {
                loadDirectory(targetDir)
                openFile(File(targetDir, fileName).absolutePath)
                showMessage("تم إنشاء $fileName")
            } else {
                showMessage("فشل إنشاء الملف أو الاسم موجود بالفعل")
            }
        }
    }

    fun createNewFolder(folderName: String) {
        val targetDir = _currentDirectoryPath.value.ifEmpty {
            _activeProject.value?.directoryPath ?: ""
        }
        if (targetDir.isNotEmpty()) {
            val success = projectRepository.createFolder(targetDir, folderName)
            if (success) {
                loadDirectory(targetDir)
                showMessage("تم إنشاء المجلد $folderName")
            }
        }
    }

    fun deleteItem(path: String) {
        val success = projectRepository.deleteItem(path)
        if (success) {
            loadDirectory(_currentDirectoryPath.value)
            showMessage("تم الحذف بنجاح")
        }
    }

    fun renameItem(oldPath: String, newName: String) {
        val success = projectRepository.renameItem(oldPath, newName)
        if (success) {
            loadDirectory(_currentDirectoryPath.value)
            showMessage("تم تعديل الاسم بنجاح")
        }
    }

    // Run / Execution
    fun runCurrentFile(interactiveInput: String? = null) {
        val scriptName = _activeFileName.value
        val codeToRun = _editorCode.value
        _outputContent.value = ""
        _isExecuting.value = true
        navigateTo(IdeScreen.Output)

        viewModelScope.launch {
            pythonRuntime.execute(
                scriptName = scriptName,
                code = codeToRun,
                inputProvider = { prompt -> interactiveInput ?: "Ahmed" },
                onOutput = { text, isError ->
                    _outputContent.value += text
                }
            )
            _isExecuting.value = false
        }
    }

    fun clearOutput() {
        _outputContent.value = ""
    }

    fun stopExecution() {
        _isExecuting.value = false
        _outputContent.value += "\n[Stopped by user]\n"
    }

    // Terminal
    fun executeTerminalCommand(cmd: String) {
        viewModelScope.launch {
            terminalSession.executeCommand(cmd)
        }
    }

    fun clearTerminal() {
        terminalSession.clear()
    }

    // Packages
    fun setPackageSearch(query: String) {
        _packageSearchQuery.value = query
    }

    fun togglePackageInstall(pkg: PackageInfo) {
        if (pkg.isInstalled) {
            packageRepository.uninstallPackage(pkg.name)
            showMessage("تم إلغاء تثبيت ${pkg.name}")
        } else {
            packageRepository.installPackage(pkg.name)
            showMessage("تم تثبيت ${pkg.name} بنجاح")
        }
    }

    // Git
    fun gitCommit(message: String) {
        gitRepository.commit(message)
        showMessage("تم تسجيل التغييرات: $message")
    }

    fun gitPush() {
        val msg = gitRepository.push()
        showMessage(msg)
    }

    fun gitPull() {
        val msg = gitRepository.pull()
        showMessage(msg)
    }

    fun gitSwitchBranch(branch: String) {
        gitRepository.switchBranch(branch)
        showMessage("تم الانتقال إلى فرع $branch")
    }

    fun gitCreateBranch(branch: String) {
        gitRepository.createBranch(branch)
        showMessage("تم إنشاء فرع $branch")
    }

    // Debugger
    fun continueDebug() {
        val current = _debuggerSession.value
        val nextLine = current.currentLine + 1
        _debuggerSession.value = current.copy(
            isPaused = false,
            currentLine = nextLine,
            variables = listOf(
                com.example.data.model.DebugVariable("x", "${10 + nextLine}", "int"),
                com.example.data.model.DebugVariable("y", "${20 + nextLine}", "int"),
                com.example.data.model.DebugVariable("z", "${30 + nextLine}", "int"),
                com.example.data.model.DebugVariable("name", "\"Ahmed\"", "str"),
                com.example.data.model.DebugVariable("status", "True", "bool")
            )
        )
    }

    fun stepOverDebug() {
        continueDebug()
    }

    fun stopDebug() {
        _debuggerSession.value = _debuggerSession.value.copy(
            isRunning = false,
            isPaused = true
        )
        showMessage("تم إنهاء جلسة تصحيح الأخطاء")
    }

    // Settings
    fun setLanguage(lang: AppLanguage) {
        settingsRepository.updateLanguage(lang)
    }

    fun setDarkMode(isDark: Boolean) {
        settingsRepository.updateDarkMode(isDark)
    }

    fun setFontSize(sizeSp: Int) {
        settingsRepository.updateFontSize(sizeSp)
    }

    fun setTabSize(spaces: Int) {
        settingsRepository.updateTabSize(spaces)
    }

    fun setWordWrap(wrap: Boolean) {
        settingsRepository.updateWordWrap(wrap)
    }

    fun setLineNumbers(show: Boolean) {
        settingsRepository.updateLineNumbers(show)
    }

    fun setAutoSave(enabled: Boolean) {
        settingsRepository.updateAutoSave(enabled)
    }

    fun setTermuxIntegration(enabled: Boolean) {
        settingsRepository.updateTermuxIntegration(enabled)
    }

    fun showMessage(msg: String) {
        _toastMessage.value = msg
    }

    fun clearMessage() {
        _toastMessage.value = null
    }

    fun tr(key: String): String {
        return LocalizedStrings.get(key, settings.value.language)
    }
}
