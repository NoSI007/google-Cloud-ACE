package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CloudBestPractice
import com.example.data.model.GcloudCliCommand
import com.example.ui.AceViewModel
import com.example.ui.components.AiExplanationBottomSheet
import com.example.ui.components.ExportStudyGuideDialog
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickReferenceScreen(
    viewModel: AceViewModel,
    onNavigateBack: (() -> Unit)? = null
) {
    val context = LocalContext.current
    var showExportDialog by remember { mutableStateOf(false) }
    val searchQuery by viewModel.quickRefSearchQuery.collectAsState()
    val commandCategory by viewModel.quickRefCommandCategory.collectAsState()
    val practiceCategory by viewModel.quickRefPracticeCategory.collectAsState()
    val activeTab by viewModel.quickRefActiveTab.collectAsState()
    val highYieldOnly by viewModel.quickRefHighYieldOnly.collectAsState()

    val filteredCommands by viewModel.filteredCliCommands.collectAsState()
    val filteredPractices by viewModel.filteredBestPractices.collectAsState()

    val commandCategories = listOf(
        "All", "Config & Auth", "Compute Engine", "IAM & Security",
        "Cloud Storage", "GKE & Containers", "Cloud Run & Functions",
        "Networking & VPC", "Billing & Budgets"
    )

    val practiceCategories = listOf(
        "All", "Security & IAM", "Cost Optimization",
        "High Availability & Resilience", "Networking Architecture", "DevOps & Operations"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Quick Reference",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = EditorialTextPrimary
                        )
                        Text(
                            text = "Essential CLI Commands & Architecture Best Practices",
                            fontSize = 11.sp,
                            color = EditorialTextSecondary
                        )
                    }
                },
                navigationIcon = {
                    if (onNavigateBack != null) {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.testTag("quick_ref_back_btn")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = EditorialTextPrimary
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showExportDialog = true },
                        modifier = Modifier.testTag("export_quick_ref_action_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FileDownload,
                            contentDescription = "Export Study Guide",
                            tint = EditorialPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = EditorialBackground)
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
            val horizontalPadding = if (isWide) 32.dp else 16.dp

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 950.dp)
            ) {
                // Search Input Field
                Box(modifier = Modifier.padding(horizontal = horizontalPadding, vertical = 8.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.updateQuickRefSearch(it) },
                        placeholder = {
                            Text(
                                text = if (activeTab == 0)
                                    "Search gcloud, gsutil, flags (e.g. instances create, IAP)..."
                                else
                                    "Search best practices (e.g. least privilege, VPC, CUDs)...",
                                fontSize = 13.sp,
                                color = EditorialTextSecondary
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search Icon",
                                tint = EditorialPrimary
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = { viewModel.updateQuickRefSearch("") },
                                    modifier = Modifier.testTag("clear_quick_ref_search_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear Search",
                                        tint = EditorialTextSecondary
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(20.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EditorialPrimary,
                            unfocusedBorderColor = EditorialSurfaceVariant,
                            focusedContainerColor = EditorialSurface,
                            unfocusedContainerColor = EditorialSurface,
                            focusedTextColor = EditorialTextPrimary,
                            unfocusedTextColor = EditorialTextPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("quick_ref_search_input")
                    )
                }

                // Dual Mode Tab Switcher
                TabRow(
                    selectedTabIndex = activeTab,
                    containerColor = EditorialSurface,
                    contentColor = EditorialPrimary,
                    divider = { HorizontalDivider(color = EditorialSurfaceVariant) },
                    modifier = Modifier.testTag("quick_ref_tab_row")
                ) {
                    Tab(
                        selected = activeTab == 0,
                        onClick = { viewModel.setQuickRefTab(0) },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Terminal,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "CLI Commands (${filteredCommands.size})",
                                    fontSize = 13.sp,
                                    fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        },
                        modifier = Modifier.testTag("tab_cli_commands")
                    )
                    Tab(
                        selected = activeTab == 1,
                        onClick = { viewModel.setQuickRefTab(1) },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Best Practices (${filteredPractices.size})",
                                    fontSize = 13.sp,
                                    fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        },
                        modifier = Modifier.testTag("tab_best_practices")
                    )
                }

                // Category Filter Chips & High Yield Filter Toggle
                LazyRow(
                    contentPadding = PaddingValues(horizontal = horizontalPadding, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // High Yield Exam Filter Chip
                    item {
                        FilterChip(
                            selected = highYieldOnly,
                            onClick = { viewModel.toggleQuickRefHighYieldOnly() },
                            leadingIcon = {
                                Icon(
                                    imageVector = if (highYieldOnly) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = null,
                                    tint = if (highYieldOnly) Color.White else EditorialPrimaryDark,
                                    modifier = Modifier.size(14.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = "ACE High-Yield",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            shape = CircleShape,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EditorialPrimaryDark,
                                selectedLabelColor = Color.White,
                                containerColor = EditorialBadgeBg,
                                labelColor = EditorialPrimaryDark
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = highYieldOnly,
                                borderColor = EditorialBorderAccent,
                                selectedBorderColor = EditorialPrimaryDark
                            ),
                            modifier = Modifier.testTag("chip_filter_high_yield")
                        )
                    }

                    val currentCategories = if (activeTab == 0) commandCategories else practiceCategories
                    val activeSelectedCategory = if (activeTab == 0) commandCategory else practiceCategory

                    items(currentCategories) { category ->
                        val isSelected = activeSelectedCategory.equals(category, ignoreCase = true)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                if (activeTab == 0) {
                                    viewModel.updateQuickRefCommandCategory(category)
                                } else {
                                    viewModel.updateQuickRefPracticeCategory(category)
                                }
                            },
                            label = {
                                Text(
                                    text = category,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            shape = CircleShape,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EditorialPrimary,
                                selectedLabelColor = Color.White,
                                containerColor = EditorialSurface,
                                labelColor = EditorialTextPrimary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = EditorialSurfaceVariant,
                                selectedBorderColor = EditorialPrimary
                            ),
                            modifier = Modifier.testTag("chip_category_${category.lowercase().replace(" ", "_")}")
                        )
                    }
                }

                // Main Content List
                if (activeTab == 0) {
                    if (filteredCommands.isEmpty()) {
                        QuickRefEmptyState(
                            query = searchQuery,
                            onClearSearch = {
                                viewModel.updateQuickRefSearch("")
                                viewModel.updateQuickRefCommandCategory("All")
                            }
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = horizontalPadding, vertical = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(filteredCommands, key = { it.id }) { cmd ->
                                CliCommandCard(
                                    command = cmd,
                                    onCopyCommand = { textToCopy ->
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("GCP Command", textToCopy)
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "Command copied to clipboard", Toast.LENGTH_SHORT).show()
                                    },
                                    onAskAi = { title, contextDetail ->
                                        viewModel.requestAiExpandedExplanation(title, contextDetail)
                                    }
                                )
                            }
                        }
                    }
                } else {
                    if (filteredPractices.isEmpty()) {
                        QuickRefEmptyState(
                            query = searchQuery,
                            onClearSearch = {
                                viewModel.updateQuickRefSearch("")
                                viewModel.updateQuickRefPracticeCategory("All")
                            }
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = horizontalPadding, vertical = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(filteredPractices, key = { it.id }) { practice ->
                                BestPracticeCard(
                                    practice = practice,
                                    onAskAi = { title, contextDetail ->
                                        viewModel.requestAiExpandedExplanation(title, contextDetail)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    AiExplanationBottomSheet(viewModel = viewModel)

    if (showExportDialog) {
        ExportStudyGuideDialog(
            viewModel = viewModel,
            onDismiss = { showExportDialog = false },
            preselectedCliOnly = true
        )
    }
}

@Composable
fun CliCommandCard(
    command: GcloudCliCommand,
    onCopyCommand: (String) -> Unit,
    onAskAi: (String, String) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("cli_command_card_${command.id}"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = EditorialSurface),
        border = BorderStroke(1.dp, EditorialSurfaceVariant)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Category Badge & Quick Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = EditorialPrimaryContainer
                ) {
                    Text(
                        text = command.category.uppercase(),
                        style = EditorialLabelCaps,
                        color = EditorialPrimaryDark,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                IconButton(
                    onClick = { onCopyCommand(command.command) },
                    modifier = Modifier.testTag("copy_command_btn_${command.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy Command",
                        tint = EditorialPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bash Code Block Display
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCopyCommand(command.command) },
                shape = RoundedCornerShape(14.dp),
                color = DarkBackground
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$ ",
                        color = EditorialPrimary,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp
                    )
                    Text(
                        text = command.command,
                        color = Color(0xFFD0BCFF),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Description
            Text(
                text = command.description,
                fontSize = 14.sp,
                color = EditorialTextPrimary,
                lineHeight = 20.sp
            )

            // Syntax Structure
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Top) {
                Text(
                    text = "Syntax: ",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = EditorialTextSecondary
                )
                Text(
                    text = command.syntaxBreakdown,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    color = EditorialPrimaryDark
                )
            }

            // ACE Exam Tip Callout
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = EditorialBadgeBg,
                border = BorderStroke(1.dp, EditorialBorderAccent)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = EditorialPrimaryDark,
                        modifier = Modifier
                            .size(16.dp)
                            .padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "ACE EXAM TIP",
                            style = EditorialLabelCaps,
                            color = EditorialPrimaryDark
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = command.aceExamTip,
                            fontSize = 12.sp,
                            color = EditorialTextPrimary,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // Expandable Parameter Flags
            if (command.commonFlags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier.testTag("expand_flags_btn_${command.id}")
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = EditorialPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isExpanded) "Hide Common Flags (${command.commonFlags.size})" else "View Common Flags (${command.commonFlags.size})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialPrimary
                    )
                }

                AnimatedVisibility(visible = isExpanded) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp)
                            .background(EditorialBackground, RoundedCornerShape(12.dp))
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        command.commonFlags.forEach { (flag, explanation) ->
                            Column {
                                Text(
                                    text = flag,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = EditorialPrimaryDark
                                )
                                Text(
                                    text = explanation,
                                    fontSize = 11.sp,
                                    color = EditorialTextSecondary,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = {
                        val promptContext = "Explain the Google Cloud CLI command: '${command.command}'. Context & purpose: ${command.description}. Common flags: ${command.commonFlags.joinToString { "${it.first} (${it.second})" }}. What are real-world ACE exam scenarios where this command is required?"
                        onAskAi(command.command, promptContext)
                    },
                    shape = CircleShape,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = EditorialPrimary),
                    border = BorderStroke(1.dp, EditorialPrimary),
                    modifier = Modifier.testTag("ask_ai_cmd_${command.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Ask AI to Explain", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun BestPracticeCard(
    practice: CloudBestPractice,
    onAskAi: (String, String) -> Unit
) {
    val isHighYield = practice.aceExamPriority.contains("Critical", ignoreCase = true)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("best_practice_card_${practice.id}"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = EditorialSurface),
        border = BorderStroke(1.dp, if (isHighYield) EditorialPrimary.copy(alpha = 0.5f) else EditorialSurfaceVariant)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header Row: Category & Priority
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = EditorialPrimaryContainer
                ) {
                    Text(
                        text = practice.category.uppercase(),
                        style = EditorialLabelCaps,
                        color = EditorialPrimaryDark,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = if (isHighYield) EditorialBadgeBg else EditorialSurfaceVariant
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isHighYield) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = EditorialPrimaryDark,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = practice.aceExamPriority,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isHighYield) EditorialPrimaryDark else EditorialTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Title
            Text(
                text = practice.title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = EditorialTextPrimary,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Golden Rule
            Text(
                text = practice.rule,
                fontSize = 13.sp,
                color = EditorialTextPrimary,
                lineHeight = 19.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            // DO: Actionable Guideline
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFE8F5E9),
                border = BorderStroke(1.dp, Color(0xFF81C784))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Recommended Do",
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "RECOMMENDED ARCHITECTURE",
                            style = EditorialLabelCaps,
                            color = Color(0xFF2E7D32)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = practice.actionableGuideline,
                            fontSize = 12.sp,
                            color = EditorialTextPrimary,
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // DON'T: Anti-Pattern
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFFFEBEE),
                border = BorderStroke(1.dp, Color(0xFFE57373))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Cancel,
                        contentDescription = "Avoid Anti-Pattern",
                        tint = Color(0xFFC62828),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "ANTI-PATTERN TO AVOID",
                            style = EditorialLabelCaps,
                            color = Color(0xFFC62828)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = practice.antiPattern,
                            fontSize = 12.sp,
                            color = EditorialTextPrimary,
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Architectural Rationale
            Text(
                text = "Why this matters: ${practice.rationale}",
                fontSize = 12.sp,
                color = EditorialTextSecondary,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Ask AI Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = {
                        val promptContext = "Explain the Google Cloud Architecture Best Practice: '${practice.title}'. Rule: ${practice.rule}. Recommended approach: ${practice.actionableGuideline}. Anti-pattern: ${practice.antiPattern}. Architectural rationale: ${practice.rationale}. How is this evaluated on the Google Cloud ACE Certification Exam?"
                        onAskAi(practice.title, promptContext)
                    },
                    shape = CircleShape,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = EditorialPrimary),
                    border = BorderStroke(1.dp, EditorialPrimary),
                    modifier = Modifier.testTag("ask_ai_bp_${practice.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Ask AI Deep Dive", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun QuickRefEmptyState(
    query: String,
    onClearSearch: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.SearchOff,
            contentDescription = null,
            tint = EditorialPrimary,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No matching items found",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = EditorialTextPrimary
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = if (query.isNotEmpty()) "No results matching \"$query\"" else "Try selecting a different category or clearing the filter",
            fontSize = 13.sp,
            color = EditorialTextSecondary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onClearSearch,
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = EditorialPrimary),
            modifier = Modifier.testTag("clear_filter_empty_state_btn")
        ) {
            Text("Reset Search & Filters", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}
