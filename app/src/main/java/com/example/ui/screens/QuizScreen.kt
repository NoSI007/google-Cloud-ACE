package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.ui.theme.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(viewModel: AceViewModel) {
    val questions = viewModel.quizQuestions
    val currentIndex by viewModel.currentQuizIndex.collectAsState()
    val selectedAnswers by viewModel.selectedQuizAnswers.collectAsState()
    val submitted by viewModel.quizSubmitted.collectAsState()

    val currentQuestion = questions.getOrNull(currentIndex)

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
                        Text(
                            text = "ACE Practice Exam Quiz",
                            fontWeight = FontWeight.Medium,
                            fontSize = 20.sp,
                            color = EditorialTextPrimary
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
            val horizontalPadding = if (isWide) 40.dp else 20.dp

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 850.dp),
                contentPadding = PaddingValues(horizontal = horizontalPadding, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
            // Quiz Progress Header
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = EditorialSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EditorialSurfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Question ${currentIndex + 1} of ${questions.size}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = EditorialTextPrimary
                            )
                            currentQuestion?.let {
                                Surface(
                                    shape = CircleShape,
                                    color = EditorialBadgeBg
                                ) {
                                    Text(
                                        text = it.topicCategory.uppercase(),
                                        style = EditorialLabelCaps,
                                        color = EditorialPrimaryDark,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LinearProgressIndicator(
                            progress = { (currentIndex + 1).toFloat() / questions.size.toFloat() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp),
                            color = EditorialPrimary,
                            trackColor = EditorialSurfaceVariant
                        )
                    }
                }
            }

            // Question Box
            currentQuestion?.let { question ->
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("quiz_question_card"),
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(containerColor = EditorialSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EditorialSurfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = question.questionText,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp,
                                lineHeight = 22.sp,
                                color = EditorialTextPrimary
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            val selectedOpt = selectedAnswers[currentIndex]

                            question.options.forEachIndexed { optIdx, optionText ->
                                val isSelected = selectedOpt == optIdx
                                val isCorrect = optIdx == question.correctOptionIndex

                                val optionColor = when {
                                    submitted && isCorrect -> EditorialPrimaryContainer
                                    submitted && isSelected && !isCorrect -> Color(0xFFFCE8E6)
                                    isSelected -> EditorialPrimaryContainer
                                    else -> EditorialBackground
                                }

                                val borderColor = when {
                                    submitted && isCorrect -> EditorialPrimaryDark
                                    submitted && isSelected && !isCorrect -> GoogleRed
                                    isSelected -> EditorialPrimary
                                    else -> EditorialSurfaceVariant
                                }

                                OutlinedCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable(enabled = !submitted) {
                                            viewModel.answerQuizQuestion(currentIndex, optIdx)
                                        }
                                        .testTag("quiz_option_$optIdx"),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.outlinedCardColors(containerColor = optionColor),
                                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(borderColor))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = isSelected,
                                            onClick = {
                                                if (!submitted) {
                                                    viewModel.answerQuizQuestion(currentIndex, optIdx)
                                                }
                                            },
                                            enabled = !submitted,
                                            colors = RadioButtonDefaults.colors(selectedColor = EditorialPrimary)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = optionText,
                                            fontSize = 14.sp,
                                            lineHeight = 18.sp,
                                            color = EditorialTextPrimary
                                        )
                                    }
                                }
                            }

                            // Submitted Explanation
                            if (submitted) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = EditorialPrimaryContainer)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Info,
                                                contentDescription = null,
                                                tint = EditorialPrimaryDark,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "ACE Explanation",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = EditorialPrimaryDark
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = question.explanation,
                                            fontSize = 13.sp,
                                            color = EditorialTextPrimary,
                                            lineHeight = 18.sp
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Button(
                                            onClick = {
                                                viewModel.requestAiExpandedExplanation(
                                                    topicTitle = "Quiz Question: ${question.topicCategory}",
                                                    contextDetail = "Question: ${question.questionText}\nExplanation: ${question.explanation}"
                                                )
                                            },
                                            shape = CircleShape,
                                            colors = ButtonDefaults.buttonColors(containerColor = EditorialPrimaryDark),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("quiz_ask_ai_btn")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.AutoAwesome,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(15.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "Ask AI for Expanded Explanation",
                                                fontSize = 12.sp,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }

                        }
                    }
                }
            }

            // Navigation / Action Buttons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { viewModel.previousQuizQuestion() },
                        enabled = currentIndex > 0,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EditorialSurface,
                            disabledContainerColor = EditorialSurfaceVariant
                        ),
                        shape = CircleShape
                    ) {
                        Text("Previous", color = EditorialTextPrimary)
                    }

                    if (currentIndex < questions.size - 1) {
                        Button(
                            onClick = { viewModel.nextQuizQuestion() },
                            enabled = selectedAnswers.containsKey(currentIndex),
                            colors = ButtonDefaults.buttonColors(containerColor = EditorialPrimary),
                            shape = CircleShape,
                            modifier = Modifier.testTag("next_question_btn")
                        ) {
                            Text("Next Question")
                        }
                    } else if (!submitted) {
                        Button(
                            onClick = { viewModel.submitQuiz() },
                            colors = ButtonDefaults.buttonColors(containerColor = EditorialPrimaryDark),
                            shape = CircleShape,
                            modifier = Modifier.testTag("submit_quiz_btn")
                        ) {
                            Text("Submit Quiz")
                        }
                    } else {
                        Button(
                            onClick = { viewModel.resetQuiz() },
                            colors = ButtonDefaults.buttonColors(containerColor = EditorialPrimary),
                            shape = CircleShape,
                            modifier = Modifier.testTag("retake_quiz_btn")
                        ) {
                            Text("Retake Quiz")
                        }
                    }
                }
            }
        }
    }
}

    AiExplanationBottomSheet(viewModel = viewModel)
}

