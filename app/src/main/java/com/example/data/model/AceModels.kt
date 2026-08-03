package com.example.data.model

data class AceModule(
    val id: String,
    val title: String,
    val sectionNumber: String,
    val examWeight: String,
    val summary: String,
    val iconName: String,
    val lessons: List<AceLesson>
)

data class AceLesson(
    val id: String,
    val title: String,
    val subtitle: String,
    val readingTimeMinutes: Int,
    val contentSections: List<LessonSection>,
    val keyTakeaways: List<String>,
    val aceExamTips: List<String>
)

data class LessonSection(
    val heading: String,
    val bodyParagraphs: List<String>,
    val codeOrConceptSnippet: String? = null,
    val tableRows: List<Pair<String, String>>? = null
)

data class QuizQuestion(
    val id: Int,
    val topicCategory: String,
    val questionText: String,
    val options: List<String>,
    val correctOptionIndex: Int,
    val explanation: String
)

data class VmSimulationResult(
    val machineType: String,
    val spotDiscount: Boolean,
    val estimatedMonthlyCost: Double,
    val recommendationSummary: String,
    val bestWorkloadUseCases: List<String>
)

data class StorageSimulationResult(
    val recommendedClass: String,
    val recommendedLocation: String,
    val estimatedStorageCostPerGb: Double,
    val lifecycleRecommendation: String,
    val keyExamRule: String
)

data class GcpTerm(
    val acronymOrTerm: String,
    val fullName: String,
    val category: String,
    val definition: String,
    val aceExamTip: String
)

data class FlashcardQuizQuestion(
    val id: Int,
    val term: GcpTerm,
    val questionPrompt: String,
    val options: List<String>,
    val correctOptionIndex: Int,
    val explanation: String
)

