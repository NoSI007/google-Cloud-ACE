package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AceModule
import com.example.ui.AceViewModel
import com.example.ui.components.AiExplanationBottomSheet
import com.example.ui.theme.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: AceViewModel,
    onNavigateToModules: () -> Unit,
    onNavigateToLesson: (String) -> Unit,
    onNavigateToComputeSim: () -> Unit,
    onNavigateToStorageSim: () -> Unit,
    onNavigateToQuiz: () -> Unit,
    onNavigateToGlossary: () -> Unit
) {
    val completedIds by viewModel.completedLessonIds.collectAsState()
    val quizScores by viewModel.quizScores.collectAsState()

    val totalLessons = viewModel.modules.sumOf { it.lessons.size }
    val completedCount = completedIds.size
    val progressFraction = if (totalLessons > 0) completedCount.toFloat() / totalLessons.toFloat() else 0f

    val lastQuizScore = quizScores.firstOrNull()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu",
                                tint = EditorialTextSecondary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "ACE Guide",
                                fontWeight = FontWeight.Medium,
                                fontSize = 20.sp,
                                color = EditorialTextPrimary
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            IconButton(
                                onClick = onNavigateToGlossary,
                                modifier = Modifier.testTag("top_bar_search_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search Glossary",
                                    tint = EditorialPrimary
                                )
                            }
                            Surface(
                                shape = CircleShape,
                                color = EditorialBadgeBg,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "ACE",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EditorialPrimaryDark
                                    )
                                }
                            }
                        }
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
                    .widthIn(max = 950.dp),
                contentPadding = PaddingValues(horizontal = horizontalPadding, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
            // Editorial Hero Section Header
            item {
                Column(modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)) {
                    Text(
                        text = "MODULE 01 • OVERVIEW",
                        style = EditorialLabelCaps,
                        color = EditorialPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Cloud\nFundamentals",
                        style = EditorialSerifDisplay,
                        color = EditorialTextPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Google Cloud is a suite of services that allows you to build, test, and deploy applications on Google's infrastructure.",
                        fontSize = 13.sp,
                        color = EditorialTextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }

            // Progress Readiness Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("hero_banner_card"),
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
                                text = "EXAM READINESS PROGRESS",
                                style = EditorialLabelCaps,
                                color = EditorialPrimary
                            )
                            Text(
                                text = "${(progressFraction * 100).toInt()}%",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = EditorialPrimaryDark
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$completedCount of $totalLessons Lessons Completed",
                            fontSize = 13.sp,
                            color = EditorialTextSecondary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        LinearProgressIndicator(
                            progress = { progressFraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape),
                            color = EditorialPrimary,
                            trackColor = EditorialPrimaryContainer
                        )
                    }
                }
            }

            // Quick GCP Terms & Acronyms Search Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToGlossary() }
                        .testTag("dashboard_glossary_search_card"),
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
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = EditorialPrimary
                                )
                                Text(
                                    text = "QUICK REFERENCE",
                                    style = EditorialLabelCaps,
                                    color = EditorialPrimary
                                )
                            }
                            Surface(
                                shape = CircleShape,
                                color = EditorialBadgeBg
                            ) {
                                Text(
                                    text = "${viewModel.allGcpTerms.size} Terms",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EditorialPrimaryDark,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "GCP Terms & Service Acronyms",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = EditorialTextPrimary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Filter key service acronyms (GKE, Pub/Sub, IAM, Bigtable, MIG, CMEK) with definition rules and ACE exam tips.",
                            fontSize = 13.sp,
                            color = EditorialTextSecondary,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = onNavigateToGlossary,
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(containerColor = EditorialPrimary),
                                modifier = Modifier.testTag("open_glossary_search_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Search Acronyms & Terms", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }

            // Interactive Infrastructure Labs Title
            item {
                Text(
                    text = "Interactive Labs",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = EditorialTextPrimary
                )
            }

            // Compute & Storage Cards with Large Watermarks
            item {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Compute Engine Lab Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToComputeSim() }
                            .testTag("compute_sim_card"),
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(containerColor = EditorialSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EditorialSurfaceVariant)
                    ) {
                        Box(modifier = Modifier.padding(20.dp)) {
                            // Large Watermark
                            Text(
                                text = "01",
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Black,
                                color = EditorialPrimary.copy(alpha = 0.12f),
                                modifier = Modifier.align(Alignment.TopEnd)
                            )

                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Memory,
                                        contentDescription = null,
                                        tint = EditorialPrimary
                                    )
                                    Text(
                                        text = "COMPUTE ENGINE",
                                        style = EditorialLabelCaps,
                                        color = EditorialPrimary
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Virtual Machines & MIGs",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = EditorialTextPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Virtual Machines (VMs) running in Google's data centers. Customize vCPUs, RAM & Spot pricing.",
                                    fontSize = 13.sp,
                                    color = EditorialTextSecondary,
                                    lineHeight = 18.sp
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                    Button(
                                        onClick = onNavigateToComputeSim,
                                        shape = CircleShape,
                                        colors = ButtonDefaults.buttonColors(containerColor = EditorialPrimary)
                                    ) {
                                        Text("Explore VMs", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }
                        }
                    }

                    // Cloud Storage Lab Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToStorageSim() }
                            .testTag("storage_sim_card"),
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(containerColor = EditorialPrimaryContainer),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EditorialBorderAccent)
                    ) {
                        Box(modifier = Modifier.padding(20.dp)) {
                            // Large Watermark
                            Text(
                                text = "02",
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Black,
                                color = EditorialPrimaryDark.copy(alpha = 0.12f),
                                modifier = Modifier.align(Alignment.TopEnd)
                            )

                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CloudDone,
                                        contentDescription = null,
                                        tint = EditorialPrimaryDark
                                    )
                                    Text(
                                        text = "CLOUD STORAGE",
                                        style = EditorialLabelCaps,
                                        color = EditorialPrimaryDark
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Object Storage & Lifecycle",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = EditorialTextPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "High durability storage for unstructured data. Simulate Standard, Nearline, Coldline & Archive classes.",
                                    fontSize = 13.sp,
                                    color = EditorialTextSecondary,
                                    lineHeight = 18.sp
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                    Button(
                                        onClick = onNavigateToStorageSim,
                                        shape = CircleShape,
                                        colors = ButtonDefaults.buttonColors(containerColor = EditorialPrimaryDark)
                                    ) {
                                        Text("View Storage", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Core ACE Exam Modules Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Core Exam Modules",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EditorialTextPrimary
                    )
                    TextButton(
                        onClick = onNavigateToModules,
                        modifier = Modifier.testTag("view_all_modules_btn")
                    ) {
                        Text("View All (${viewModel.modules.size})", fontSize = 13.sp, color = EditorialPrimary)
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = EditorialPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            items(viewModel.modules) { module ->
                ModuleCardItem(
                    module = module,
                    completedIds = completedIds,
                    onClick = {
                        module.lessons.firstOrNull()?.let { firstLesson ->
                            onNavigateToLesson(firstLesson.id)
                        } ?: onNavigateToModules()
                    }
                )
            }

            // Practice Quiz Banner Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToQuiz() }
                        .testTag("practice_quiz_card"),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = EditorialSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EditorialSurfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = EditorialBadgeBg,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Quiz,
                                    contentDescription = null,
                                    tint = EditorialPrimaryDark,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "ACE Practice Exam Quiz",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = EditorialTextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (lastQuizScore != null) {
                                    "Last Score: ${lastQuizScore.score}/${lastQuizScore.totalQuestions} (${(lastQuizScore.score * 100 / lastQuizScore.totalQuestions)}%)"
                                } else {
                                    "Test your knowledge with realistic scenario practice questions."
                                },
                                fontSize = 12.sp,
                                color = EditorialTextSecondary
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = EditorialPrimary
                        )
                    }
                }
            }
        }
    }
}

    AiExplanationBottomSheet(viewModel = viewModel)
}


@Composable
fun ModuleCardItem(
    module: AceModule,
    completedIds: Set<String>,
    onClick: () -> Unit
) {
    val completedInModule = module.lessons.count { completedIds.contains(it.id) }
    val totalInModule = module.lessons.size

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("module_card_${module.id}"),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = EditorialSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, EditorialSurfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = EditorialPrimaryContainer,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    val icon = when (module.iconName) {
                        "dns" -> Icons.Default.Dns
                        "storage" -> Icons.Default.Storage
                        else -> Icons.Default.Cloud
                    }
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = EditorialPrimaryDark
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = module.sectionNumber.uppercase(),
                        style = EditorialLabelCaps,
                        color = EditorialPrimary
                    )
                    Surface(
                        shape = CircleShape,
                        color = EditorialBadgeBg
                    ) {
                        Text(
                            text = module.examWeight,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = EditorialPrimaryDark,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = module.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = EditorialTextPrimary
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "$completedInModule of $totalInModule lessons completed",
                    fontSize = 12.sp,
                    color = EditorialTextSecondary
                )
            }
        }
    }
}

