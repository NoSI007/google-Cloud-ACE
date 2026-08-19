package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.data.repository.AceRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class VmState(
    val family: String = "N2 (General-Purpose)",
    val cpus: Int = 2,
    val ram: Int = 8,
    val isSpot: Boolean = false,
    val diskType: String = "Balanced Persistent Disk",
    val diskSize: Int = 100,
    val isMig: Boolean = false
)

data class StorageState(
    val accessFreqDays: Int = 1,
    val retentionMonths: Int = 12,
    val isMultiRegion: Boolean = true,
    val hasLifecycle: Boolean = true
)

data class AiExplanationState(
    val isOpen: Boolean = false,
    val topicTitle: String = "",
    val contextDetail: String = "",
    val explanationText: String = "",
    val isLoading: Boolean = false
)

class AceViewModel(application: Application) : AndroidViewModel(application) {


    private val repository: AceRepository

    val completedLessonIds: StateFlow<Set<String>>
    val bookmarkedTips: StateFlow<List<com.example.data.local.BookmarkedTipEntity>>
    val quizScores: StateFlow<List<com.example.data.local.QuizScoreEntity>>
    val roomFlashcards: StateFlow<List<com.example.data.local.AceFlashcardEntity>>

    val modules: List<AceModule>
    val quizQuestions: List<QuizQuestion>
    val allGcpTerms: List<GcpTerm>
    val allCliCommands: List<GcloudCliCommand>
    val allBestPractices: List<CloudBestPractice>

    // Quick Reference State
    private val _quickRefSearchQuery = MutableStateFlow("")
    val quickRefSearchQuery: StateFlow<String> = _quickRefSearchQuery.asStateFlow()

    private val _quickRefCommandCategory = MutableStateFlow("All")
    val quickRefCommandCategory: StateFlow<String> = _quickRefCommandCategory.asStateFlow()

    private val _quickRefPracticeCategory = MutableStateFlow("All")
    val quickRefPracticeCategory: StateFlow<String> = _quickRefPracticeCategory.asStateFlow()

    private val _quickRefActiveTab = MutableStateFlow(0) // 0: CLI Commands, 1: Best Practices
    val quickRefActiveTab: StateFlow<Int> = _quickRefActiveTab.asStateFlow()

    private val _quickRefHighYieldOnly = MutableStateFlow(false)
    val quickRefHighYieldOnly: StateFlow<Boolean> = _quickRefHighYieldOnly.asStateFlow()

    val filteredCliCommands: StateFlow<List<GcloudCliCommand>>
    val filteredBestPractices: StateFlow<List<CloudBestPractice>>

    // Search & Glossary State
    private val _searchTermQuery = MutableStateFlow("")
    val searchTermQuery: StateFlow<String> = _searchTermQuery.asStateFlow()

    private val _selectedTermCategory = MutableStateFlow("All")
    val selectedTermCategory: StateFlow<String> = _selectedTermCategory.asStateFlow()

    // Flashcard State
    private val _flashcardIndex = MutableStateFlow(0)
    val flashcardIndex: StateFlow<Int> = _flashcardIndex.asStateFlow()

    private val _isCardFlipped = MutableStateFlow(false)
    val isCardFlipped: StateFlow<Boolean> = _isCardFlipped.asStateFlow()

    private val _flashcards = MutableStateFlow<List<GcpTerm>>(emptyList())
    val flashcards: StateFlow<List<GcpTerm>> = _flashcards.asStateFlow()

    // AI Explanation State
    private val _aiExplanationState = MutableStateFlow(AiExplanationState())
    val aiExplanationState: StateFlow<AiExplanationState> = _aiExplanationState.asStateFlow()

    val filteredGcpTerms: StateFlow<List<GcpTerm>>

    // VM State
    private val _vmState = MutableStateFlow(VmState())
    val vmState: StateFlow<VmState> = _vmState.asStateFlow()

    val vmSimulationResult: StateFlow<VmSimulationResult>

    // Storage State
    private val _storageState = MutableStateFlow(StorageState())
    val storageState: StateFlow<StorageState> = _storageState.asStateFlow()

    val storageSimulationResult: StateFlow<StorageSimulationResult>

    // Quiz State
    private val _selectedQuizCategory = MutableStateFlow("All Modules")
    val selectedQuizCategory: StateFlow<String> = _selectedQuizCategory.asStateFlow()

    private val _isImmediateFeedbackMode = MutableStateFlow(true)
    val isImmediateFeedbackMode: StateFlow<Boolean> = _isImmediateFeedbackMode.asStateFlow()

    private val _bookmarkedQuestionIds = MutableStateFlow<Set<Int>>(emptySet())
    val bookmarkedQuestionIds: StateFlow<Set<Int>> = _bookmarkedQuestionIds.asStateFlow()

    private val _onlyReviewIncorrect = MutableStateFlow(false)
    val onlyReviewIncorrect: StateFlow<Boolean> = _onlyReviewIncorrect.asStateFlow()

    private val _currentQuizIndex = MutableStateFlow(0)
    val currentQuizIndex: StateFlow<Int> = _currentQuizIndex.asStateFlow()

    private val _selectedQuizAnswers = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val selectedQuizAnswers: StateFlow<Map<Int, Int>> = _selectedQuizAnswers.asStateFlow()

    private val _quizSubmitted = MutableStateFlow(false)
    val quizSubmitted: StateFlow<Boolean> = _quizSubmitted.asStateFlow()

    // Flashcard Knowledge Check Quiz State
    private val _flashcardQuizQuestions = MutableStateFlow<List<FlashcardQuizQuestion>>(emptyList())
    val flashcardQuizQuestions: StateFlow<List<FlashcardQuizQuestion>> = _flashcardQuizQuestions.asStateFlow()

    private val _flashcardQuizIndex = MutableStateFlow(0)
    val flashcardQuizIndex: StateFlow<Int> = _flashcardQuizIndex.asStateFlow()

    private val _flashcardQuizAnswers = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val flashcardQuizAnswers: StateFlow<Map<Int, Int>> = _flashcardQuizAnswers.asStateFlow()

    private val _flashcardQuizSubmitted = MutableStateFlow(false)
    val flashcardQuizSubmitted: StateFlow<Boolean> = _flashcardQuizSubmitted.asStateFlow()

    private val _flashcardQuizScore = MutableStateFlow(0)
    val flashcardQuizScore: StateFlow<Int> = _flashcardQuizScore.asStateFlow()

    init {
        val db = AppDatabase.getDatabase(application)
        repository = AceRepository(
            progressDao = db.userProgressDao(),
            flashcardDao = db.aceFlashcardDao()
        )

        viewModelScope.launch {
            repository.ensureFlashcardsSeeded()
        }

        roomFlashcards = repository.allRoomFlashcards
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        modules = repository.getModules()
        quizQuestions = repository.getPracticeQuizQuestions()
        allGcpTerms = repository.getGcpTerms()
        allCliCommands = repository.getCliCommands()
        allBestPractices = repository.getCloudBestPractices()
        _flashcards.value = allGcpTerms
        generateFlashcardQuiz()

        filteredCliCommands = combine(
            _quickRefSearchQuery,
            _quickRefCommandCategory,
            _quickRefHighYieldOnly
        ) { query, category, highYieldOnly ->
            allCliCommands.filter { cmd ->
                val matchesCategory = (category == "All" || cmd.category.equals(category, ignoreCase = true))
                val q = query.trim().lowercase()
                val matchesQuery = q.isEmpty() ||
                        cmd.command.lowercase().contains(q) ||
                        cmd.description.lowercase().contains(q) ||
                        cmd.category.lowercase().contains(q) ||
                        cmd.syntaxBreakdown.lowercase().contains(q) ||
                        cmd.aceExamTip.lowercase().contains(q) ||
                        cmd.commonFlags.any { it.first.lowercase().contains(q) || it.second.lowercase().contains(q) }

                val matchesHighYield = !highYieldOnly || cmd.aceExamTip.isNotEmpty()
                matchesCategory && matchesQuery && matchesHighYield
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), allCliCommands)

        filteredBestPractices = combine(
            _quickRefSearchQuery,
            _quickRefPracticeCategory,
            _quickRefHighYieldOnly
        ) { query, category, highYieldOnly ->
            allBestPractices.filter { bp ->
                val matchesCategory = (category == "All" || bp.category.equals(category, ignoreCase = true))
                val q = query.trim().lowercase()
                val matchesQuery = q.isEmpty() ||
                        bp.title.lowercase().contains(q) ||
                        bp.rule.lowercase().contains(q) ||
                        bp.rationale.lowercase().contains(q) ||
                        bp.actionableGuideline.lowercase().contains(q) ||
                        bp.antiPattern.lowercase().contains(q) ||
                        bp.category.lowercase().contains(q)

                val matchesHighYield = !highYieldOnly || bp.aceExamPriority.contains("Critical", ignoreCase = true)
                matchesCategory && matchesQuery && matchesHighYield
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), allBestPractices)

        filteredGcpTerms = combine(_searchTermQuery, _selectedTermCategory) { query, category ->
            allGcpTerms.filter { term ->
                val matchesCategory = (category == "All" || term.category.equals(category, ignoreCase = true))
                val q = query.trim().lowercase()
                val matchesQuery = q.isEmpty() ||
                        term.acronymOrTerm.lowercase().contains(q) ||
                        term.fullName.lowercase().contains(q) ||
                        term.definition.lowercase().contains(q) ||
                        term.aceExamTip.lowercase().contains(q)
                matchesCategory && matchesQuery
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), allGcpTerms)

        completedLessonIds = repository.completedLessonIds
            .map { it.toSet() }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

        bookmarkedTips = repository.bookmarkedTips
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        quizScores = repository.quizScores
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        vmSimulationResult = _vmState.map { s ->
            repository.calculateVmSimulation(
                machineFamily = s.family,
                vCpus = s.cpus,
                ramGb = s.ram,
                isSpot = s.isSpot,
                diskType = s.diskType,
                diskSizeGb = s.diskSize,
                isMig = s.isMig
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            repository.calculateVmSimulation("N2 (General-Purpose)", 2, 8, false, "Balanced Persistent Disk", 100, false)
        )

        storageSimulationResult = _storageState.map { s ->
            repository.calculateStorageSimulation(
                accessFrequencyDays = s.accessFreqDays,
                retentionMonths = s.retentionMonths,
                isMultiRegion = s.isMultiRegion,
                hasLifecycleRule = s.hasLifecycle
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            repository.calculateStorageSimulation(1, 12, true, true)
        )
    }

    fun toggleLessonCompleted(lessonId: String, currentCompleted: Boolean) {
        viewModelScope.launch {
            repository.toggleLessonCompletion(lessonId, !currentCompleted)
        }
    }

    fun toggleBookmark(tipId: String, lessonTitle: String, tipText: String, isCurrentlyBookmarked: Boolean) {
        viewModelScope.launch {
            repository.toggleBookmark(tipId, lessonTitle, tipText, !isCurrentlyBookmarked)
        }
    }

    // VM Simulator updates
    fun updateVmFamily(family: String) { _vmState.value = _vmState.value.copy(family = family) }
    fun updateVmCpus(cpus: Int) { _vmState.value = _vmState.value.copy(cpus = cpus) }
    fun updateVmRam(ram: Int) { _vmState.value = _vmState.value.copy(ram = ram) }
    fun updateVmIsSpot(isSpot: Boolean) { _vmState.value = _vmState.value.copy(isSpot = isSpot) }
    fun updateVmDiskType(type: String) { _vmState.value = _vmState.value.copy(diskType = type) }
    fun updateVmDiskSize(size: Int) { _vmState.value = _vmState.value.copy(diskSize = size) }
    fun updateVmIsMig(isMig: Boolean) { _vmState.value = _vmState.value.copy(isMig = isMig) }

    // Storage Simulator updates
    fun updateStorageAccessFreq(freqDays: Int) { _storageState.value = _storageState.value.copy(accessFreqDays = freqDays) }
    fun updateStorageRetention(months: Int) { _storageState.value = _storageState.value.copy(retentionMonths = months) }
    fun updateStorageIsMultiRegion(isMulti: Boolean) { _storageState.value = _storageState.value.copy(isMultiRegion = isMulti) }
    fun updateStorageHasLifecycle(hasLife: Boolean) { _storageState.value = _storageState.value.copy(hasLifecycle = hasLife) }

    // Quiz Actions
    fun updateSelectedQuizCategory(category: String) {
        _selectedQuizCategory.value = category
        _currentQuizIndex.value = 0
        _selectedQuizAnswers.value = emptyMap()
        _quizSubmitted.value = false
        _onlyReviewIncorrect.value = false
    }

    fun toggleImmediateFeedbackMode() {
        _isImmediateFeedbackMode.value = !_isImmediateFeedbackMode.value
    }

    fun toggleBookmarkQuizQuestion(questionId: Int) {
        val current = _bookmarkedQuestionIds.value.toMutableSet()
        if (current.contains(questionId)) {
            current.remove(questionId)
        } else {
            current.add(questionId)
        }
        _bookmarkedQuestionIds.value = current
    }

    fun getFilteredQuizQuestions(): List<QuizQuestion> {
        val category = _selectedQuizCategory.value
        val baseList = if (category == "All Modules") {
            quizQuestions
        } else {
            quizQuestions.filter { it.topicCategory.contains(category, ignoreCase = true) || category.contains(it.topicCategory, ignoreCase = true) }
        }
        return if (_onlyReviewIncorrect.value) {
            baseList.filterIndexed { idx, q ->
                val ans = _selectedQuizAnswers.value[idx]
                ans != null && ans != q.correctOptionIndex
            }.ifEmpty { baseList }
        } else {
            baseList
        }
    }

    fun answerQuizQuestion(questionIndex: Int, optionIndex: Int) {
        if (!_quizSubmitted.value) {
            _selectedQuizAnswers.value = _selectedQuizAnswers.value.toMutableMap().apply {
                put(questionIndex, optionIndex)
            }
        }
    }

    fun submitQuiz() {
        if (!_quizSubmitted.value) {
            _quizSubmitted.value = true
            val activeList = getFilteredQuizQuestions()
            var correctCount = 0
            activeList.forEachIndexed { idx, q ->
                if (_selectedQuizAnswers.value[idx] == q.correctOptionIndex) {
                    correctCount++
                }
            }
            viewModelScope.launch {
                repository.recordQuizResult(correctCount, activeList.size)
            }
        }
    }

    fun jumpToQuizQuestion(index: Int) {
        val activeList = getFilteredQuizQuestions()
        if (index in activeList.indices) {
            _currentQuizIndex.value = index
        }
    }

    fun nextQuizQuestion() {
        val activeList = getFilteredQuizQuestions()
        if (_currentQuizIndex.value < activeList.size - 1) {
            _currentQuizIndex.value = _currentQuizIndex.value + 1
        }
    }

    fun previousQuizQuestion() {
        if (_currentQuizIndex.value > 0) {
            _currentQuizIndex.value = _currentQuizIndex.value - 1
        }
    }

    fun resetQuiz() {
        _quizSubmitted.value = false
        _selectedQuizAnswers.value = emptyMap()
        _currentQuizIndex.value = 0
        _onlyReviewIncorrect.value = false
    }

    fun retakeIncorrectOnly() {
        _onlyReviewIncorrect.value = true
        _quizSubmitted.value = false
        _selectedQuizAnswers.value = emptyMap()
        _currentQuizIndex.value = 0
    }

    // Glossary Search Actions
    fun updateSearchTermQuery(query: String) {
        _searchTermQuery.value = query
    }

    fun updateSelectedTermCategory(category: String) {
        _selectedTermCategory.value = category
        resetFlashcardsForCategory(category)
    }

    private fun resetFlashcardsForCategory(category: String) {
        val filtered = if (category == "All") allGcpTerms else allGcpTerms.filter { it.category.equals(category, ignoreCase = true) }
        _flashcards.value = filtered
        _flashcardIndex.value = 0
        _isCardFlipped.value = false
    }

    fun flipCard() {
        _isCardFlipped.value = !_isCardFlipped.value
    }

    fun nextFlashcard() {
        val list = _flashcards.value
        if (list.isNotEmpty() && _flashcardIndex.value < list.size - 1) {
            _flashcardIndex.value = _flashcardIndex.value + 1
            _isCardFlipped.value = false
        }
    }

    fun previousFlashcard() {
        val list = _flashcards.value
        if (list.isNotEmpty() && _flashcardIndex.value > 0) {
            _flashcardIndex.value = _flashcardIndex.value - 1
            _isCardFlipped.value = false
        }
    }

    fun shuffleFlashcards() {
        _flashcards.value = _flashcards.value.shuffled()
        _flashcardIndex.value = 0
        _isCardFlipped.value = false
    }

    // Flashcard Knowledge Check Quiz Actions
    fun generateFlashcardQuiz() {
        val pool = if (_selectedTermCategory.value == "All") allGcpTerms else _flashcards.value.ifEmpty { allGcpTerms }
        val sample = pool.shuffled().take(6)

        val questions = sample.mapIndexed { index, term ->
            val distractors = allGcpTerms.filter { it.acronymOrTerm != term.acronymOrTerm }.shuffled().take(3)
            val isPromptDefinition = index % 2 == 0

            val prompt = if (isPromptDefinition) {
                "Which GCP service/concept corresponds to this definition?\n\"${term.definition}\""
            } else {
                "Which GCP service or configuration rule is described by this ACE Exam Tip?\n\"${term.aceExamTip}\""
            }

            val correctAnswerText = "${term.acronymOrTerm} (${term.fullName})"
            val distractorTextList = distractors.map { "${it.acronymOrTerm} (${it.fullName})" }

            val allOptions = (distractorTextList + correctAnswerText).shuffled()
            val correctIdx = allOptions.indexOf(correctAnswerText)

            FlashcardQuizQuestion(
                id = index + 1,
                term = term,
                questionPrompt = prompt,
                options = allOptions,
                correctOptionIndex = if (correctIdx >= 0) correctIdx else 0,
                explanation = "Definition: ${term.definition}\nACE Exam Tip: ${term.aceExamTip}"
            )
        }

        _flashcardQuizQuestions.value = questions
        _flashcardQuizIndex.value = 0
        _flashcardQuizAnswers.value = emptyMap()
        _flashcardQuizSubmitted.value = false
        _flashcardQuizScore.value = 0
    }

    fun answerFlashcardQuizQuestion(questionIndex: Int, optionIndex: Int) {
        if (!_flashcardQuizSubmitted.value) {
            _flashcardQuizAnswers.value = _flashcardQuizAnswers.value.toMutableMap().apply {
                put(questionIndex, optionIndex)
            }
        }
    }

    fun submitFlashcardQuiz() {
        if (!_flashcardQuizSubmitted.value && _flashcardQuizQuestions.value.isNotEmpty()) {
            _flashcardQuizSubmitted.value = true
            var correctCount = 0
            _flashcardQuizQuestions.value.forEachIndexed { idx, q ->
                if (_flashcardQuizAnswers.value[idx] == q.correctOptionIndex) {
                    correctCount++
                }
            }
            _flashcardQuizScore.value = correctCount
            viewModelScope.launch {
                repository.recordQuizResult(correctCount, _flashcardQuizQuestions.value.size)
            }
        }
    }

    fun nextFlashcardQuizQuestion() {
        if (_flashcardQuizIndex.value < _flashcardQuizQuestions.value.size - 1) {
            _flashcardQuizIndex.value = _flashcardQuizIndex.value + 1
        }
    }

    fun previousFlashcardQuizQuestion() {
        if (_flashcardQuizIndex.value > 0) {
            _flashcardQuizIndex.value = _flashcardQuizIndex.value - 1
        }
    }

    // AI Explanation Actions
    fun requestAiExpandedExplanation(topicTitle: String, contextDetail: String) {
        _aiExplanationState.value = AiExplanationState(
            isOpen = true,
            topicTitle = topicTitle,
            contextDetail = contextDetail,
            explanationText = "",
            isLoading = true
        )

        viewModelScope.launch {
            val result = com.example.data.remote.GeminiService.getExpandedExplanation(topicTitle, contextDetail)
            _aiExplanationState.value = _aiExplanationState.value.copy(
                explanationText = result,
                isLoading = false
            )
        }
    }

    fun toggleRoomFlashcardMastery(cardId: String, isCurrentlyMastered: Boolean) {
        viewModelScope.launch {
            repository.updateFlashcardMastery(cardId, !isCurrentlyMastered)
        }
    }

    fun resetRoomFlashcardProgress() {
        viewModelScope.launch {
            repository.resetAllFlashcardProgress()
        }
    }

    fun addCustomFlashcard(serviceName: String, category: String, prompt: String, definition: String, examTip: String, keyFeatures: String) {
        val newCard = com.example.data.local.AceFlashcardEntity(
            id = "custom_${System.currentTimeMillis()}",
            serviceName = serviceName,
            serviceCategory = category,
            frontPrompt = prompt,
            backDefinition = definition,
            examTip = examTip,
            keyFeaturesCsv = keyFeatures
        )
        viewModelScope.launch {
            repository.insertCustomFlashcard(newCard)
        }
    }

    fun updateQuickRefSearch(query: String) {
        _quickRefSearchQuery.value = query
    }

    fun updateQuickRefCommandCategory(category: String) {
        _quickRefCommandCategory.value = category
    }

    fun updateQuickRefPracticeCategory(category: String) {
        _quickRefPracticeCategory.value = category
    }

    fun setQuickRefTab(tab: Int) {
        _quickRefActiveTab.value = tab
    }

    fun toggleQuickRefHighYieldOnly() {
        _quickRefHighYieldOnly.value = !_quickRefHighYieldOnly.value
    }

    fun dismissAiExplanation() {
        _aiExplanationState.value = _aiExplanationState.value.copy(isOpen = false)
    }
}


