package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.QuizQuestion
import com.example.ui.theme.*

/**
 * Reusable, high-craft Jetpack Compose Multi-Choice Quiz Card Component.
 * Supports study-note question rendering, option selection (A, B, C, D),
 * immediate feedback or exam submission modes, study note explanations,
 * and AI deep dive integration.
 */
@Composable
fun MultiChoiceQuizCard(
    question: QuizQuestion,
    questionNumber: Int,
    totalQuestions: Int,
    selectedOptionIndex: Int?,
    onOptionSelected: (Int) -> Unit,
    isSubmitted: Boolean,
    isImmediateFeedbackMode: Boolean = true,
    isBookmarked: Boolean = false,
    onToggleBookmark: () -> Unit = {},
    onAskAi: (title: String, context: String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val showFeedback = isSubmitted || (isImmediateFeedbackMode && selectedOptionIndex != null)
    val optionLetters = listOf("A", "B", "C", "D")

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("multi_choice_quiz_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = EditorialSurface),
        border = BorderStroke(1.dp, EditorialSurfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header: Category Badge + Question Counter + Bookmark Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = EditorialPrimaryContainer
                    ) {
                        Text(
                            text = "Q$questionNumber",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = EditorialPrimaryDark,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = EditorialBadgeBg
                    ) {
                        Text(
                            text = question.topicCategory,
                            style = EditorialLabelCaps,
                            color = EditorialPrimaryDark,
                            maxLines = 1,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                IconButton(
                    onClick = onToggleBookmark,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("bookmark_quiz_question_btn")
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = if (isBookmarked) "Remove bookmark" else "Bookmark question",
                        tint = if (isBookmarked) GoogleYellow else EditorialTextSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Question Prompt Text
            Text(
                text = question.questionText,
                fontFamily = FontFamily.Serif,
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 24.sp,
                color = EditorialTextPrimary,
                modifier = Modifier.testTag("quiz_question_text")
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Multiple Choice Options (A, B, C, D)
            question.options.forEachIndexed { optIdx, optionText ->
                val isSelected = selectedOptionIndex == optIdx
                val isCorrect = optIdx == question.correctOptionIndex
                val letter = optionLetters.getOrElse(optIdx) { "${optIdx + 1}" }

                val containerBg = when {
                    showFeedback && isCorrect -> Color(0xFFE6F4EA) // Soft green
                    showFeedback && isSelected && !isCorrect -> Color(0xFFFCE8E6) // Soft red
                    isSelected -> EditorialPrimaryContainer
                    else -> EditorialBackground
                }

                val borderColor = when {
                    showFeedback && isCorrect -> Color(0xFF137333) // Emerald green
                    showFeedback && isSelected && !isCorrect -> GoogleRed
                    isSelected -> EditorialPrimary
                    else -> EditorialSurfaceVariant
                }

                val letterBg = when {
                    showFeedback && isCorrect -> Color(0xFF137333)
                    showFeedback && isSelected && !isCorrect -> GoogleRed
                    isSelected -> EditorialPrimary
                    else -> EditorialSurfaceVariant
                }

                val letterTextColor = when {
                    showFeedback && (isCorrect || (isSelected && !isCorrect)) -> Color.White
                    isSelected -> Color.White
                    else -> EditorialTextSecondary
                }

                val interactionEnabled = !isSubmitted && (!isImmediateFeedbackMode || selectedOptionIndex == null)

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(enabled = interactionEnabled) {
                            onOptionSelected(optIdx)
                        }
                        .testTag("quiz_option_$optIdx"),
                    shape = RoundedCornerShape(16.dp),
                    color = containerBg,
                    border = BorderStroke(if (isSelected || (showFeedback && isCorrect)) 1.5.dp else 1.dp, borderColor)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Letter Badge (A, B, C, D)
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(letterBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = letter,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = letterTextColor
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Option text
                        Text(
                            text = optionText,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            color = EditorialTextPrimary,
                            fontWeight = if (isSelected || (showFeedback && isCorrect)) FontWeight.SemiBold else FontWeight.Normal,
                            modifier = Modifier.weight(1f)
                        )

                        // Indicator Icon when feedback is visible
                        if (showFeedback) {
                            Spacer(modifier = Modifier.width(8.dp))
                            if (isCorrect) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Correct",
                                    tint = Color(0xFF137333),
                                    modifier = Modifier.size(20.dp)
                                )
                            } else if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Cancel,
                                    contentDescription = "Incorrect",
                                    tint = GoogleRed,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Animated Explanation Section
            AnimatedVisibility(
                visible = showFeedback,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                val isAnswerCorrect = selectedOptionIndex == question.correctOptionIndex

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    // Result Header Banner
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isAnswerCorrect) Color(0xFFE6F4EA) else Color(0xFFFCE8E6),
                        border = BorderStroke(1.dp, if (isAnswerCorrect) Color(0xFFA8DAB5) else Color(0xFFF5C2C0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isAnswerCorrect) Icons.Default.CheckCircle else Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = if (isAnswerCorrect) Color(0xFF137333) else GoogleRed,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isAnswerCorrect) "Correct! Great understanding of the concept." else "Incorrect. Review the study concept below:",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isAnswerCorrect) Color(0xFF137333) else GoogleRed
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Study Note Explanation Card
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = EditorialBackground),
                        border = BorderStroke(1.dp, EditorialSurfaceVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.MenuBook,
                                    contentDescription = null,
                                    tint = EditorialPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Study Notes Breakdown",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = EditorialPrimaryDark
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = question.explanation,
                                fontSize = 13.sp,
                                lineHeight = 19.sp,
                                color = EditorialTextPrimary
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Gemini AI Deep Dive Action Button
                            FilledTonalButton(
                                onClick = {
                                    onAskAi(
                                        "Quiz Question: ${question.topicCategory}",
                                        "Question: ${question.questionText}\nCorrect Answer: ${question.options.getOrNull(question.correctOptionIndex)}\nExplanation: ${question.explanation}"
                                    )
                                },
                                shape = CircleShape,
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = EditorialPrimaryContainer,
                                    contentColor = EditorialPrimaryDark
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("quiz_ask_ai_deep_dive_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Ask Gemini AI Tutor for Deep Dive",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Question Matrix Selector for jumping directly to any question.
 */
@Composable
fun QuestionMatrixNavigator(
    totalQuestions: Int,
    currentIndex: Int,
    selectedAnswers: Map<Int, Int>,
    bookmarkedQuestionIds: Set<Int>,
    isSubmitted: Boolean,
    questions: List<QuizQuestion>,
    onSelectQuestion: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = EditorialSurface),
        border = BorderStroke(1.dp, EditorialSurfaceVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Question Grid",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = EditorialTextPrimary
                )
                Text(
                    text = "${selectedAnswers.size}/$totalQuestions Answered",
                    fontSize = 12.sp,
                    color = EditorialTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Grid / Flow of question numbers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Chunk into rows of up to 10 or scroll horizontally
                androidx.compose.foundation.lazy.LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(totalQuestions) { idx ->
                        val isCurrent = idx == currentIndex
                        val isAnswered = selectedAnswers.containsKey(idx)
                        val question = questions.getOrNull(idx)
                        val isCorrect = question != null && selectedAnswers[idx] == question.correctOptionIndex
                        val isBookmarked = question != null && bookmarkedQuestionIds.contains(question.id)

                        val bg = when {
                            isCurrent -> EditorialPrimary
                            isSubmitted && isAnswered && isCorrect -> Color(0xFFE6F4EA)
                            isSubmitted && isAnswered && !isCorrect -> Color(0xFFFCE8E6)
                            isAnswered -> EditorialPrimaryContainer
                            else -> EditorialBackground
                        }

                        val textColor = when {
                            isCurrent -> Color.White
                            isSubmitted && isAnswered && isCorrect -> Color(0xFF137333)
                            isSubmitted && isAnswered && !isCorrect -> GoogleRed
                            isAnswered -> EditorialPrimaryDark
                            else -> EditorialTextSecondary
                        }

                        val borderColor = when {
                            isCurrent -> EditorialPrimary
                            isSubmitted && isAnswered && isCorrect -> Color(0xFF137333)
                            isSubmitted && isAnswered && !isCorrect -> GoogleRed
                            isAnswered -> EditorialPrimary
                            else -> EditorialSurfaceVariant
                        }

                        Surface(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .clickable { onSelectQuestion(idx) }
                                .testTag("jump_to_question_$idx"),
                            shape = CircleShape,
                            color = bg,
                            border = BorderStroke(1.dp, borderColor)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${idx + 1}",
                                    fontSize = 12.sp,
                                    fontWeight = if (isCurrent || isAnswered) FontWeight.Bold else FontWeight.Normal,
                                    color = textColor
                                )
                                if (isBookmarked && !isCurrent) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(2.dp)
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(GoogleYellow)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Results Summary Card showing overall score, pass/fail status, and module breakdowns.
 */
@Composable
fun QuizSummaryCard(
    score: Int,
    totalQuestions: Int,
    questions: List<QuizQuestion>,
    selectedAnswers: Map<Int, Int>,
    onRetakeAll: () -> Unit,
    onRetakeIncorrect: () -> Unit,
    onReviewMistakes: () -> Unit,
    modifier: Modifier = Modifier
) {
    val percentage = if (totalQuestions > 0) (score * 100) / totalQuestions else 0
    val passed = percentage >= 70
    val incorrectCount = totalQuestions - score

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = EditorialSurface),
        border = BorderStroke(1.dp, EditorialSurfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Score circle pill
            Surface(
                shape = CircleShape,
                color = if (passed) Color(0xFFE6F4EA) else Color(0xFFFCE8E6),
                border = BorderStroke(2.dp, if (passed) Color(0xFF137333) else GoogleRed),
                modifier = Modifier.size(100.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$percentage%",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (passed) Color(0xFF137333) else GoogleRed
                        )
                        Text(
                            text = "$score / $totalQuestions",
                            fontSize = 12.sp,
                            color = EditorialTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = if (passed) "Exam Passed! Excellent Work" else "Needs Review Before Exam",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = EditorialTextPrimary,
                textAlign = TextAlign.Center
            )

            Text(
                text = if (passed)
                    "You reached the ACE passing threshold (≥70%). You have solid knowledge of these Google Cloud concepts."
                else
                    "Google Cloud ACE requires ~70% to pass. Review the study notes for the missed sections below to reinforce your knowledge.",
                fontSize = 13.sp,
                color = EditorialTextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Topic Performance Breakdown
            val topicGroups = questions.groupBy { it.topicCategory }
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Module Performance Breakdown",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = EditorialTextPrimary
                )

                topicGroups.forEach { (category, qList) ->
                    var catCorrect = 0
                    qList.forEach { q ->
                        val qIdx = questions.indexOf(q)
                        if (selectedAnswers[qIdx] == q.correctOptionIndex) {
                            catCorrect++
                        }
                    }
                    val catTotal = qList.size
                    val catPercent = if (catTotal > 0) (catCorrect * 100) / catTotal else 0

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(EditorialBackground, RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = category,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = EditorialTextPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "$catCorrect/$catTotal ($catPercent%)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (catPercent >= 70) Color(0xFF137333) else GoogleRed
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Actions: Retake / Review
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (incorrectCount > 0) {
                    OutlinedButton(
                        onClick = onRetakeIncorrect,
                        shape = CircleShape,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = EditorialPrimary),
                        border = BorderStroke(1.dp, EditorialPrimary),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("retake_incorrect_btn")
                    ) {
                        Text("Practice $incorrectCount Missed", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Button(
                    onClick = onRetakeAll,
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = EditorialPrimary),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("retake_all_btn")
                ) {
                    Text("Retake Quiz", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
