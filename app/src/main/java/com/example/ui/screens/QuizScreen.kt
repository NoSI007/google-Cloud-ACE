package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AceViewModel
import com.example.ui.components.AiExplanationBottomSheet
import com.example.ui.components.MultiChoiceQuizCard
import com.example.ui.components.QuestionMatrixNavigator
import com.example.ui.components.QuizSummaryCard
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(viewModel: AceViewModel) {
    val selectedCategory by viewModel.selectedQuizCategory.collectAsState()
    val isImmediateFeedback by viewModel.isImmediateFeedbackMode.collectAsState()
    val bookmarkedIds by viewModel.bookmarkedQuestionIds.collectAsState()
    val currentIndex by viewModel.currentQuizIndex.collectAsState()
    val selectedAnswers by viewModel.selectedQuizAnswers.collectAsState()
    val submitted by viewModel.quizSubmitted.collectAsState()
    val onlyReviewIncorrect by viewModel.onlyReviewIncorrect.collectAsState()

    val questions = viewModel.getFilteredQuizQuestions()
    val currentQuestion = questions.getOrNull(currentIndex)

    val moduleCategories = listOf(
        "All Modules",
        "1. Cloud Core & Environment",
        "2. Compute Engine & VMs",
        "3. Storage & Databases",
        "4. Containers & Serverless",
        "5. Security & Access Management",
        "6. Cloud Networking & Hybrid"
    )

    // Calculate score
    var score = 0
    questions.forEachIndexed { idx, q ->
        if (selectedAnswers[idx] == q.correctOptionIndex) {
            score++
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Quiz,
                            contentDescription = null,
                            tint = EditorialPrimary
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "ACE Knowledge Quiz",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.sp,
                                color = EditorialTextPrimary
                            )
                            Text(
                                text = if (isImmediateFeedback) "Interactive Practice Mode" else "Exam Simulation Mode",
                                fontSize = 11.sp,
                                color = EditorialTextSecondary
                            )
                        }
                    }
                },
                actions = {
                    // Practice vs Exam Mode Toggle Button
                    FilterChip(
                        selected = isImmediateFeedback,
                        onClick = { viewModel.toggleImmediateFeedbackMode() },
                        label = {
                            Text(
                                text = if (isImmediateFeedback) "Instant Feedback" else "Exam Mode",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = if (isImmediateFeedback) Icons.Default.FlashOn else Icons.Default.Timer,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EditorialPrimaryContainer,
                            selectedLabelColor = EditorialPrimaryDark
                        ),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("toggle_quiz_mode_chip")
                    )

                    IconButton(
                        onClick = { viewModel.resetQuiz() },
                        modifier = Modifier.testTag("reset_quiz_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset Quiz",
                            tint = EditorialTextSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = EditorialBackground
                )
            )
        }
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(EditorialBackground),
            contentAlignment = Alignment.TopCenter
        ) {
            val isWide = maxWidth > 650.dp
            val horizontalPadding = if (isWide) 36.dp else 16.dp

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 800.dp),
                contentPadding = PaddingValues(horizontal = horizontalPadding, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Module Filter Selector Chips
                item {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.FilterList,
                                contentDescription = null,
                                tint = EditorialTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "FILTER BY STUDY NOTE MODULE",
                                style = EditorialLabelCaps,
                                color = EditorialTextSecondary
                            )
                        }

                        val scrollState = rememberScrollState()
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(scrollState),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            moduleCategories.forEach { category ->
                                val isSelected = selectedCategory == category
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.updateSelectedQuizCategory(category) },
                                    label = {
                                        Text(
                                            text = category,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = EditorialPrimary,
                                        selectedLabelColor = Color.White,
                                        containerColor = EditorialSurface,
                                        labelColor = EditorialTextPrimary
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = isSelected,
                                        borderColor = if (isSelected) EditorialPrimary else EditorialSurfaceVariant
                                    ),
                                    modifier = Modifier.testTag("quiz_category_chip_$category")
                                )
                            }
                        }
                    }
                }

                // Quiz Progress & Stats Header
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = EditorialSurface),
                        border = BorderStroke(1.dp, EditorialSurfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Question ${if (questions.isEmpty()) 0 else currentIndex + 1} of ${questions.size}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = EditorialTextPrimary
                                    )
                                    if (onlyReviewIncorrect) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(0xFFFCE8E6)
                                        ) {
                                            Text(
                                                text = "Reviewing Missed",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = GoogleRed,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                Text(
                                    text = "${selectedAnswers.size}/${questions.size} answered",
                                    fontSize = 12.sp,
                                    color = EditorialTextSecondary
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            val progress = if (questions.isNotEmpty()) {
                                (currentIndex + 1).toFloat() / questions.size.toFloat()
                            } else 0f

                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp),
                                color = EditorialPrimary,
                                trackColor = EditorialSurfaceVariant
                            )
                        }
                    }
                }

                // If Submitted and finished, show the full Score Breakdown Card
                if (submitted) {
                    item {
                        QuizSummaryCard(
                            score = score,
                            totalQuestions = questions.size,
                            questions = questions,
                            selectedAnswers = selectedAnswers,
                            onRetakeAll = { viewModel.resetQuiz() },
                            onRetakeIncorrect = { viewModel.retakeIncorrectOnly() },
                            onReviewMistakes = { /* keep reviewing on same view */ }
                        )
                    }
                }

                // Main Multi-Choice Question Component Card
                if (currentQuestion != null) {
                    item {
                        val isBookmarked = bookmarkedIds.contains(currentQuestion.id)

                        MultiChoiceQuizCard(
                            question = currentQuestion,
                            questionNumber = currentIndex + 1,
                            totalQuestions = questions.size,
                            selectedOptionIndex = selectedAnswers[currentIndex],
                            onOptionSelected = { optIdx ->
                                viewModel.answerQuizQuestion(currentIndex, optIdx)
                            },
                            isSubmitted = submitted,
                            isImmediateFeedbackMode = isImmediateFeedback,
                            isBookmarked = isBookmarked,
                            onToggleBookmark = {
                                viewModel.toggleBookmarkQuizQuestion(currentQuestion.id)
                            },
                            onAskAi = { title, context ->
                                viewModel.requestAiExpandedExplanation(title, context)
                            }
                        )
                    }

                    // Question Matrix Navigation Grid
                    item {
                        QuestionMatrixNavigator(
                            totalQuestions = questions.size,
                            currentIndex = currentIndex,
                            selectedAnswers = selectedAnswers,
                            bookmarkedQuestionIds = bookmarkedIds,
                            isSubmitted = submitted,
                            questions = questions,
                            onSelectQuestion = { idx ->
                                viewModel.jumpToQuizQuestion(idx)
                            }
                        )
                    }

                    // Bottom Navigation Buttons
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.previousQuizQuestion() },
                                enabled = currentIndex > 0,
                                shape = CircleShape,
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = EditorialTextPrimary
                                ),
                                border = BorderStroke(1.dp, EditorialSurfaceVariant),
                                modifier = Modifier.testTag("prev_quiz_question_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Previous")
                            }

                            if (currentIndex < questions.size - 1) {
                                Button(
                                    onClick = { viewModel.nextQuizQuestion() },
                                    colors = ButtonDefaults.buttonColors(containerColor = EditorialPrimary),
                                    shape = CircleShape,
                                    modifier = Modifier.testTag("next_quiz_question_btn")
                                ) {
                                    Text("Next Question")
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.ArrowForward,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            } else if (!submitted) {
                                Button(
                                    onClick = { viewModel.submitQuiz() },
                                    colors = ButtonDefaults.buttonColors(containerColor = EditorialPrimaryDark),
                                    shape = CircleShape,
                                    modifier = Modifier.testTag("submit_quiz_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Complete & Grade Exam")
                                }
                            } else {
                                Button(
                                    onClick = { viewModel.resetQuiz() },
                                    colors = ButtonDefaults.buttonColors(containerColor = EditorialPrimary),
                                    shape = CircleShape,
                                    modifier = Modifier.testTag("retake_quiz_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Retake Quiz")
                                }
                            }
                        }
                    }
                } else {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No questions found for this filter. Try selecting 'All Modules'.",
                                color = EditorialTextSecondary,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }

    AiExplanationBottomSheet(viewModel = viewModel)
}
