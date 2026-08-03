package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AceLesson
import com.example.data.model.LessonSection
import com.example.ui.AceViewModel
import com.example.ui.components.AiExplanationBottomSheet
import com.example.ui.theme.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonDetailScreen(
    lessonId: String,
    viewModel: AceViewModel,
    onNavigateBack: () -> Unit
) {
    val completedIds by viewModel.completedLessonIds.collectAsState()
    val bookmarkedTips by viewModel.bookmarkedTips.collectAsState()

    val lesson = viewModel.modules.flatMap { it.lessons }.find { it.id == lessonId }

    if (lesson == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Lesson not found.")
        }
        return
    }

    val isCompleted = completedIds.contains(lesson.id)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = lesson.title,
                        fontWeight = FontWeight.Medium,
                        fontSize = 18.sp,
                        color = EditorialTextPrimary,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = EditorialTextPrimary
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.toggleLessonCompleted(lesson.id, isCompleted) },
                        modifier = Modifier.testTag("toggle_lesson_completed_btn")
                    ) {
                        Icon(
                            imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = "Mark Complete",
                            tint = if (isCompleted) EditorialPrimaryDark else EditorialTextSecondary
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
            // Header Info Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = EditorialPrimaryContainer),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EditorialBorderAccent)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = lesson.subtitle,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = EditorialTextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = EditorialPrimaryDark,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${lesson.readingTimeMinutes} min estimated reading time",
                                fontSize = 12.sp,
                                color = EditorialTextSecondary
                            )
                        }
                    }
                }
            }

            // Lesson Content Sections
            items(lesson.contentSections) { section ->
                LessonSectionCard(
                    section = section,
                    onAskAiClick = {
                        viewModel.requestAiExpandedExplanation(
                            topicTitle = section.heading,
                            contextDetail = section.bodyParagraphs.joinToString(" ")
                        )
                    }
                )
            }


            // Key Takeaways Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = EditorialSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EditorialSurfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = EditorialPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Key Takeaways",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = EditorialTextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        lesson.keyTakeaways.forEach { takeaway ->
                            Row(
                                modifier = Modifier.padding(vertical = 4.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = "• ",
                                    fontWeight = FontWeight.Bold,
                                    color = EditorialPrimary,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = takeaway,
                                    fontSize = 13.sp,
                                    color = EditorialTextPrimary,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }

            // ACE Exam Tips with Bookmarking
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = EditorialBadgeBg),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EditorialBorderAccent)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = EditorialPrimaryDark,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "ACE Exam Rules & Tips",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = EditorialPrimaryDark
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        lesson.aceExamTips.forEachIndexed { idx, tip ->
                            val tipId = "${lesson.id}_tip_$idx"
                            val isBookmarked = bookmarkedTips.any { it.id == tipId }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = tip,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = EditorialTextPrimary,
                                        lineHeight = 18.sp
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        viewModel.toggleBookmark(tipId, lesson.title, tip, isBookmarked)
                                    },
                                    modifier = Modifier.testTag("bookmark_tip_$tipId")
                                ) {
                                    Icon(
                                        imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                        contentDescription = "Bookmark tip",
                                        tint = EditorialPrimaryDark
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Mark Complete Action Button
            item {
                Button(
                    onClick = { viewModel.toggleLessonCompleted(lesson.id, isCompleted) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("complete_lesson_action_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isCompleted) EditorialPrimaryDark else EditorialPrimary
                    ),
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.Done,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isCompleted) "Completed! (Click to unmark)" else "Mark Lesson as Completed",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

    AiExplanationBottomSheet(viewModel = viewModel)
}

@Composable
fun LessonSectionCard(
    section: LessonSection,
    onAskAiClick: () -> Unit
) {
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
                    text = section.heading,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = EditorialTextPrimary,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = onAskAiClick,
                    modifier = Modifier.testTag("ai_explain_section_${section.heading.lowercase().replace(" ", "_")}")
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Ask AI for Expanded Explanation",
                        tint = EditorialPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            section.bodyParagraphs.forEach { para ->
                Text(
                    text = para,
                    fontSize = 14.sp,
                    color = EditorialTextSecondary,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Visual Concept or Diagram Snippet Box
            section.codeOrConceptSnippet?.let { snippet ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = DarkBackground
                ) {
                    Text(
                        text = snippet,
                        color = Color(0xFFD0BCFF),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // Comparison Table
            section.tableRows?.let { rows ->
                Spacer(modifier = Modifier.height(6.dp))
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = EditorialBackground)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        rows.forEachIndexed { idx, (key, value) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = key,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = EditorialPrimary,
                                    modifier = Modifier.weight(0.4f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = value,
                                    fontSize = 12.sp,
                                    color = EditorialTextPrimary,
                                    modifier = Modifier.weight(0.6f)
                                )
                            }
                            if (idx < rows.size - 1) {
                                HorizontalDivider(color = EditorialSurfaceVariant)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Ask AI Button
            OutlinedButton(
                onClick = onAskAiClick,
                shape = CircleShape,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = EditorialPrimary),
                border = androidx.compose.foundation.BorderStroke(1.dp, EditorialPrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ask_ai_section_btn_${section.heading.lowercase().replace(" ", "_")}")
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Ask AI for Expanded Explanation",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

