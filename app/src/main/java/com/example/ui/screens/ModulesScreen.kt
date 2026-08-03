package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AceLesson
import com.example.data.model.AceModule
import com.example.ui.AceViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModulesScreen(
    viewModel: AceViewModel,
    onNavigateToLesson: (String) -> Unit
) {
    val completedIds by viewModel.completedLessonIds.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "ACE Curriculum",
                        fontWeight = FontWeight.Medium,
                        fontSize = 20.sp,
                        color = EditorialTextPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = EditorialBackground
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(EditorialBackground),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column(modifier = Modifier.padding(bottom = 8.dp)) {
                    Text(
                        text = "GCP OFFICIAL ACE GUIDE",
                        style = EditorialLabelCaps,
                        color = EditorialPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Exam Modules",
                        style = EditorialSerifDisplay,
                        color = EditorialTextPrimary
                    )
                }
            }

            items(viewModel.modules) { module ->
                ModuleExpandableCard(
                    module = module,
                    completedIds = completedIds,
                    onLessonSelect = onNavigateToLesson,
                    onToggleComplete = { lessonId, current ->
                        viewModel.toggleLessonCompleted(lessonId, current)
                    }
                )
            }
        }
    }
}

@Composable
fun ModuleExpandableCard(
    module: AceModule,
    completedIds: Set<String>,
    onLessonSelect: (String) -> Unit,
    onToggleComplete: (String, Boolean) -> Unit
) {
    var expanded by remember { mutableStateOf(true) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("module_expandable_${module.id}"),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = EditorialSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, EditorialSurfaceVariant)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = EditorialBadgeBg
                        ) {
                            Text(
                                text = module.sectionNumber.uppercase(),
                                style = EditorialLabelCaps,
                                color = EditorialPrimaryDark,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = module.examWeight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EditorialPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = module.title,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = EditorialTextPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = module.summary,
                        fontSize = 12.sp,
                        color = EditorialTextSecondary,
                        lineHeight = 16.sp
                    )
                }

                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Toggle lessons",
                        tint = EditorialPrimary
                    )
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HorizontalDivider(color = EditorialSurfaceVariant)

                    module.lessons.forEach { lesson ->
                        val isCompleted = completedIds.contains(lesson.id)
                        LessonItemRow(
                            lesson = lesson,
                            isCompleted = isCompleted,
                            onClick = { onLessonSelect(lesson.id) },
                            onToggleComplete = { onToggleComplete(lesson.id, isCompleted) }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun LessonItemRow(
    lesson: AceLesson,
    isCompleted: Boolean,
    onClick: () -> Unit,
    onToggleComplete: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("lesson_row_${lesson.id}"),
        shape = RoundedCornerShape(16.dp),
        color = if (isCompleted) EditorialPrimaryContainer else EditorialBackground
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onToggleComplete,
                modifier = Modifier.size(32.dp).testTag("checkbox_lesson_${lesson.id}")
            ) {
                Icon(
                    imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = "Mark lesson complete",
                    tint = if (isCompleted) EditorialPrimaryDark else EditorialTextSecondary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lesson.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = EditorialTextPrimary
                )
                Text(
                    text = "${lesson.readingTimeMinutes} min read • ${lesson.subtitle}",
                    fontSize = 11.sp,
                    color = EditorialTextSecondary
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = EditorialPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
