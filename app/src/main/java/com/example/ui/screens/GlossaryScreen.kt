package com.example.ui.screens

import androidx.compose.animation.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GcpTerm
import com.example.ui.AceViewModel
import com.example.ui.components.AiExplanationBottomSheet
import com.example.ui.theme.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlossaryScreen(
    viewModel: AceViewModel,
    onNavigateBack: (() -> Unit)? = null
) {
    val searchQuery by viewModel.searchTermQuery.collectAsState()
    val selectedCategory by viewModel.selectedTermCategory.collectAsState()
    val terms by viewModel.filteredGcpTerms.collectAsState()

    val flashcards by viewModel.flashcards.collectAsState()
    val flashcardIndex by viewModel.flashcardIndex.collectAsState()
    val isCardFlipped by viewModel.isCardFlipped.collectAsState()

    var activeTab by remember { mutableStateOf(1) } // 0: Search List, 1: Flashcards

    val categories = listOf(
        "All",
        "Compute & Containers",
        "Storage & Databases",
        "Messaging & Serverless",
        "Networking & Security",
        "Operations & Management"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Flip,
                            contentDescription = null,
                            tint = EditorialPrimary
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "GCP Glossary & Flashcards",
                            fontWeight = FontWeight.Medium,
                            fontSize = 20.sp,
                            color = EditorialTextPrimary
                        )
                    }
                },
                navigationIcon = {
                    if (onNavigateBack != null) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = EditorialTextPrimary
                            )
                        }
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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 950.dp)
            ) {
            // View Mode Tab Switcher (List View vs Flashcards)
            TabRow(
                selectedTabIndex = activeTab,
                containerColor = EditorialSurface,
                contentColor = EditorialPrimary,
                divider = { HorizontalDivider(color = EditorialSurfaceVariant) }
            ) {
                Tab(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.List, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Term Search List", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    },
                    modifier = Modifier.testTag("tab_glossary_list")
                )
                Tab(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Flip, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Flashcards", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    },
                    modifier = Modifier.testTag("tab_interactive_flashcards")
                )
                Tab(
                    selected = activeTab == 2,
                    onClick = {
                        activeTab = 2
                        viewModel.generateFlashcardQuiz()
                    },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Quiz, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Knowledge Check", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    },
                    modifier = Modifier.testTag("tab_knowledge_check_quiz")
                )
            }

            if (activeTab == 0) {
                // Search Input Field
                Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.updateSearchTermQuery(it) },
                        placeholder = {
                            Text(
                                text = "Search terms (e.g. Pub/Sub, Bigtable, GKE, IAM)...",
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
                                IconButton(onClick = { viewModel.updateSearchTermQuery("") }) {
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
                            .testTag("gcp_terms_search_input")
                    )
                }

                // Category Filter Chips
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        val isSelected = selectedCategory.equals(category, ignoreCase = true)
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.updateSelectedTermCategory(category) },
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
                            modifier = Modifier.testTag("term_chip_${category.lowercase().replace(" ", "_")}")
                        )
                    }
                }

                // Results Summary Text
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "FOUND ${terms.size} TERMS",
                        style = EditorialLabelCaps,
                        color = EditorialPrimary
                    )
                    if (searchQuery.isNotEmpty() || selectedCategory != "All") {
                        TextButton(
                            onClick = {
                                viewModel.updateSearchTermQuery("")
                                viewModel.updateSelectedTermCategory("All")
                            }
                        ) {
                            Text(
                                text = "Reset Filters",
                                fontSize = 11.sp,
                                color = EditorialPrimaryDark,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                if (terms.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.SearchOff,
                                contentDescription = null,
                                modifier = Modifier.size(56.dp),
                                tint = EditorialTextSecondary.copy(alpha = 0.4f)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No matching GCP terms found",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp,
                                color = EditorialTextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Try searching for acronyms like 'MIG', 'GKE', or 'Pub/Sub'",
                                fontSize = 13.sp,
                                color = EditorialTextSecondary
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(terms, key = { it.acronymOrTerm }) { term ->
                            GcpTermCard(
                                term = term,
                                onAskAiClick = {
                                    viewModel.requestAiExpandedExplanation(
                                        topicTitle = "${term.acronymOrTerm} — ${term.fullName}",
                                        contextDetail = term.definition
                                    )
                                }
                            )
                        }
                    }

                }
            } else if (activeTab == 1) {
                // Interactive Flashcard Mode View
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Category Filter for Flashcards
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(categories) { category ->
                            val isSelected = selectedCategory.equals(category, ignoreCase = true)
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.updateSelectedTermCategory(category) },
                                label = {
                                    Text(
                                        text = category,
                                        fontSize = 11.sp,
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
                                modifier = Modifier.testTag("fc_chip_${category.lowercase().replace(" ", "_")}")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (flashcards.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No flashcards found for category $selectedCategory",
                                color = EditorialTextSecondary
                            )
                        }
                    } else {
                        val currentTerm = flashcards.getOrNull(flashcardIndex) ?: flashcards.first()

                        // Flashcard Progress Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "FLASHCARD ${flashcardIndex + 1} OF ${flashcards.size}",
                                style = EditorialLabelCaps,
                                color = EditorialPrimary
                            )

                            IconButton(
                                onClick = { viewModel.shuffleFlashcards() },
                                modifier = Modifier.testTag("shuffle_flashcards_btn")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Shuffle,
                                        contentDescription = "Shuffle",
                                        tint = EditorialPrimaryDark,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Shuffle",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EditorialPrimaryDark
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Interactive 3D Flip Card Container
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .clickable { viewModel.flipCard() }
                                .testTag("flashcard_flip_container"),
                            contentAlignment = Alignment.Center
                        ) {
                            AnimatedContent(
                                targetState = isCardFlipped,
                                transitionSpec = {
                                    fadeIn() + slideInHorizontally { width -> if (targetState) width else -width } togetherWith
                                            fadeOut() + slideOutHorizontally { width -> if (targetState) -width else width }
                                },
                                label = "FlashcardFlipAnimation"
                            ) { flipped ->
                                if (!flipped) {
                                    // Front Side of Card (Question / Acronym)
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .fillMaxHeight(0.85f),
                                        shape = RoundedCornerShape(32.dp),
                                        colors = CardDefaults.cardColors(containerColor = EditorialPrimaryContainer),
                                        border = androidx.compose.foundation.BorderStroke(2.dp, EditorialBorderAccent)
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(28.dp),
                                            verticalArrangement = Arrangement.SpaceBetween,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Surface(
                                                    shape = CircleShape,
                                                    color = EditorialBadgeBg
                                                ) {
                                                    Text(
                                                        text = currentTerm.category.uppercase(),
                                                        style = EditorialLabelCaps,
                                                        color = EditorialPrimaryDark,
                                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                                                    )
                                                }
                                                Icon(
                                                    imageVector = Icons.Default.TouchApp,
                                                    contentDescription = null,
                                                    tint = EditorialPrimaryDark
                                                )
                                            }

                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                modifier = Modifier.padding(vertical = 16.dp)
                                            ) {
                                                Text(
                                                    text = currentTerm.acronymOrTerm,
                                                    fontSize = 42.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = EditorialPrimaryDark
                                                )

                                                Spacer(modifier = Modifier.height(12.dp))

                                                Text(
                                                    text = currentTerm.fullName,
                                                    fontSize = 20.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = EditorialTextPrimary
                                                )
                                            }

                                            Surface(
                                                shape = CircleShape,
                                                color = Color.White.copy(alpha = 0.8f)
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Flip,
                                                        contentDescription = null,
                                                        tint = EditorialPrimary,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = "Tap Card to Reveal Definition & Use Case",
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = EditorialPrimary
                                                    )
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    // Back Side of Card (Definition, Use Cases & ACE Exam Tip)
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .fillMaxHeight(0.85f),
                                        shape = RoundedCornerShape(32.dp),
                                        colors = CardDefaults.cardColors(containerColor = EditorialSurface),
                                        border = androidx.compose.foundation.BorderStroke(2.dp, EditorialPrimary)
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(24.dp),
                                            verticalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = currentTerm.acronymOrTerm + " — " + currentTerm.fullName,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 15.sp,
                                                    color = EditorialPrimary
                                                )
                                                Surface(
                                                    shape = CircleShape,
                                                    color = EditorialPrimaryContainer
                                                ) {
                                                    Text(
                                                        text = "ANSWER",
                                                        style = EditorialLabelCaps,
                                                        color = EditorialPrimaryDark,
                                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(10.dp))

                                            Column {
                                                Text(
                                                    text = "Definition & Core Purpose:",
                                                    style = EditorialLabelCaps,
                                                    color = EditorialTextSecondary
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = currentTerm.definition,
                                                    fontSize = 14.sp,
                                                    lineHeight = 20.sp,
                                                    color = EditorialTextPrimary,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(12.dp))

                                            // Exam Tip Box
                                            Card(
                                                shape = RoundedCornerShape(16.dp),
                                                colors = CardDefaults.cardColors(containerColor = EditorialPrimaryContainer)
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(12.dp),
                                                    verticalAlignment = Alignment.Top
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Lightbulb,
                                                        contentDescription = null,
                                                        tint = EditorialPrimaryDark,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Column {
                                                        Text(
                                                            text = "ACE EXAM RULE",
                                                            style = EditorialLabelCaps,
                                                            color = EditorialPrimaryDark
                                                        )
                                                        Spacer(modifier = Modifier.height(2.dp))
                                                        Text(
                                                            text = currentTerm.aceExamTip,
                                                            fontSize = 12.sp,
                                                            lineHeight = 17.sp,
                                                            color = EditorialTextPrimary,
                                                            fontWeight = FontWeight.Medium
                                                        )
                                                    }
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(10.dp))

                                            OutlinedButton(
                                                onClick = {
                                                    viewModel.requestAiExpandedExplanation(
                                                        topicTitle = "${currentTerm.acronymOrTerm} — ${currentTerm.fullName}",
                                                        contextDetail = currentTerm.definition
                                                    )
                                                },
                                                shape = CircleShape,
                                                colors = ButtonDefaults.outlinedButtonColors(contentColor = EditorialPrimary),
                                                border = androidx.compose.foundation.BorderStroke(1.dp, EditorialPrimary),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .testTag("flashcard_ask_ai_btn")
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.AutoAwesome,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("Ask AI for Expanded Explanation", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }

                                            Spacer(modifier = Modifier.height(8.dp))

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.Center
                                            ) {
                                                Text(
                                                    text = "Tap to flip back",
                                                    fontSize = 11.sp,
                                                    color = EditorialTextSecondary,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Navigation Controls (Previous, Flip, Next)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { viewModel.previousFlashcard() },
                                enabled = flashcardIndex > 0,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = EditorialSurface,
                                    disabledContainerColor = EditorialSurfaceVariant
                                ),
                                shape = CircleShape,
                                modifier = Modifier.testTag("prev_flashcard_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Previous",
                                    tint = EditorialTextPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Previous", color = EditorialTextPrimary)
                            }

                            Button(
                                onClick = { viewModel.flipCard() },
                                colors = ButtonDefaults.buttonColors(containerColor = EditorialPrimaryContainer),
                                shape = CircleShape,
                                modifier = Modifier.testTag("flip_card_center_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Flip,
                                    contentDescription = "Flip",
                                    tint = EditorialPrimaryDark,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Flip Card", color = EditorialPrimaryDark, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { viewModel.nextFlashcard() },
                                enabled = flashcardIndex < flashcards.size - 1,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = EditorialPrimary,
                                    disabledContainerColor = EditorialSurfaceVariant
                                ),
                                shape = CircleShape,
                                modifier = Modifier.testTag("next_flashcard_btn")
                            ) {
                                Text("Next")
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.NavigateNext,
                                    contentDescription = "Next",
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            } else {
                FlashcardKnowledgeCheckView(viewModel = viewModel)
            }
        }
    }
}

    AiExplanationBottomSheet(viewModel = viewModel)
}

@Composable
fun GcpTermCard(
    term: GcpTerm,
    onAskAiClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("gcp_term_card_${term.acronymOrTerm.lowercase().replace("/", "_")}"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = EditorialSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, EditorialSurfaceVariant)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = EditorialBadgeBg
                ) {
                    Text(
                        text = term.category.uppercase(),
                        style = EditorialLabelCaps,
                        color = EditorialPrimaryDark,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EditorialPrimaryContainer
                ) {
                    Text(
                        text = term.acronymOrTerm,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp,
                        color = EditorialPrimaryDark,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = term.fullName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = EditorialTextPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = term.definition,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = EditorialTextSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ACE Exam Tip Callout
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = EditorialPrimaryContainer),
                border = androidx.compose.foundation.BorderStroke(1.dp, EditorialBorderAccent)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = EditorialPrimaryDark,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "ACE EXAM RULE",
                            style = EditorialLabelCaps,
                            color = EditorialPrimaryDark
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = term.aceExamTip,
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                            fontWeight = FontWeight.Medium,
                            color = EditorialTextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Ask AI for Expanded Explanation Button
            Button(
                onClick = onAskAiClick,
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = EditorialPrimaryContainer),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ai_explain_term_${term.acronymOrTerm.lowercase().replace("/", "_")}")
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = EditorialPrimaryDark,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Ask AI for Expanded Explanation",
                    fontSize = 12.sp,
                    color = EditorialPrimaryDark,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun FlashcardKnowledgeCheckView(viewModel: AceViewModel) {
    val quizQuestions by viewModel.flashcardQuizQuestions.collectAsState()
    val currentIdx by viewModel.flashcardQuizIndex.collectAsState()
    val selectedAnswers by viewModel.flashcardQuizAnswers.collectAsState()
    val isSubmitted by viewModel.flashcardQuizSubmitted.collectAsState()
    val score by viewModel.flashcardQuizScore.collectAsState()

    if (quizQuestions.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = EditorialPrimary)
        }
        return
    }

    val currentQuestion = quizQuestions.getOrNull(currentIdx) ?: quizQuestions[0]
    val selectedOption = selectedAnswers[currentIdx]

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Control Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = EditorialSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, EditorialSurfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "KNOWLEDGE CHECK QUIZ",
                                style = EditorialLabelCaps,
                                color = EditorialPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Question ${currentIdx + 1} of ${quizQuestions.size}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = EditorialTextPrimary
                            )
                        }

                        OutlinedButton(
                            onClick = { viewModel.generateFlashcardQuiz() },
                            shape = CircleShape,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = EditorialPrimary),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EditorialPrimary),
                            modifier = Modifier.testTag("new_knowledge_check_quiz_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "New Quiz",
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("New Quiz", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { (currentIdx + 1).toFloat() / quizQuestions.size.toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape),
                        color = EditorialPrimary,
                        trackColor = EditorialSurfaceVariant
                    )
                }
            }
        }

        // Score Card Summary (If Submitted)
        if (isSubmitted) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("knowledge_check_score_summary_card"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (score >= quizQuestions.size * 0.7) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (score >= quizQuestions.size * 0.7) Color(0xFF81C784) else Color(0xFFFFB74D)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = if (score >= quizQuestions.size * 0.7) Icons.Default.CheckCircle else Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = if (score >= quizQuestions.size * 0.7) Color(0xFF2E7D32) else Color(0xFFE65100),
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (score == quizQuestions.size) "ACE Exam Master! Perfect 100%"
                                   else if (score >= quizQuestions.size * 0.7) "Great Knowledge Reinforcement!"
                                   else "Keep Studying Flashcards!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = EditorialTextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Score: $score / ${quizQuestions.size} (${((score.toFloat() / quizQuestions.size) * 100).toInt()}%)",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            color = EditorialPrimaryDark
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Result recorded to Room Database local learning progress.",
                            fontSize = 11.sp,
                            color = EditorialTextSecondary
                        )
                    }
                }
            }
        }

        // Question Card & Multiple Choice Options
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("knowledge_check_question_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = EditorialSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, EditorialSurfaceVariant)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Surface(
                        shape = CircleShape,
                        color = EditorialBadgeBg
                    ) {
                        Text(
                            text = currentQuestion.term.category.uppercase(),
                            style = EditorialLabelCaps,
                            color = EditorialPrimaryDark,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = currentQuestion.questionPrompt,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        lineHeight = 22.sp,
                        color = EditorialTextPrimary
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Options List
                    currentQuestion.options.forEachIndexed { optionIdx, optionText ->
                        val isSelected = selectedOption == optionIdx
                        val isCorrectOption = optionIdx == currentQuestion.correctOptionIndex

                        val optionBgColor = when {
                            isSubmitted && isCorrectOption -> Color(0xFFE8F5E9)
                            isSubmitted && isSelected && !isCorrectOption -> Color(0xFFFFEBEE)
                            isSelected -> EditorialPrimaryContainer
                            else -> EditorialBackground
                        }

                        val optionBorderColor = when {
                            isSubmitted && isCorrectOption -> Color(0xFF4CAF50)
                            isSubmitted && isSelected && !isCorrectOption -> Color(0xFFEF5350)
                            isSelected -> EditorialPrimary
                            else -> EditorialSurfaceVariant
                        }

                        val optionTextColor = when {
                            isSubmitted && isCorrectOption -> Color(0xFF1B5E20)
                            isSubmitted && isSelected && !isCorrectOption -> Color(0xFFB71C1C)
                            isSelected -> EditorialPrimaryDark
                            else -> EditorialTextPrimary
                        }

                        Card(
                            onClick = { viewModel.answerFlashcardQuizQuestion(currentIdx, optionIdx) },
                            enabled = !isSubmitted,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .testTag("knowledge_check_option_${optionIdx}"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = optionBgColor),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, optionBorderColor)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (isSelected) EditorialPrimary else EditorialSurfaceVariant,
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = ('A' + optionIdx).toString(),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = if (isSelected) Color.White else EditorialTextSecondary
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Text(
                                    text = optionText,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected || (isSubmitted && isCorrectOption)) FontWeight.Bold else FontWeight.Normal,
                                    color = optionTextColor,
                                    modifier = Modifier.weight(1f)
                                )

                                if (isSubmitted) {
                                    if (isCorrectOption) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Correct",
                                            tint = Color(0xFF2E7D32),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    } else if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Cancel,
                                            contentDescription = "Incorrect",
                                            tint = Color(0xFFC62828),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Post-submission Explanation & AI Deep Dive Button
                    if (isSubmitted || selectedOption != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = EditorialBackground),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EditorialSurfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "FLASHCARD REINFORCEMENT",
                                    style = EditorialLabelCaps,
                                    color = EditorialPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = currentQuestion.explanation,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp,
                                    color = EditorialTextPrimary
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = {
                                        viewModel.requestAiExpandedExplanation(
                                            topicTitle = "${currentQuestion.term.acronymOrTerm} — ${currentQuestion.term.fullName}",
                                            contextDetail = currentQuestion.term.definition
                                        )
                                    },
                                    shape = CircleShape,
                                    colors = ButtonDefaults.buttonColors(containerColor = EditorialPrimaryContainer),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("knowledge_check_ask_ai_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = EditorialPrimaryDark,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Ask AI for Expanded Explanation",
                                        fontSize = 12.sp,
                                        color = EditorialPrimaryDark,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bottom Navigation & Submit Actions
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { viewModel.previousFlashcardQuizQuestion() },
                    enabled = currentIdx > 0,
                    shape = CircleShape,
                    modifier = Modifier.testTag("prev_knowledge_question_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Previous",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Prev")
                }

                if (!isSubmitted) {
                    Button(
                        onClick = { viewModel.submitFlashcardQuiz() },
                        enabled = selectedAnswers.size == quizQuestions.size || selectedAnswers.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(containerColor = EditorialPrimary),
                        shape = CircleShape,
                        modifier = Modifier.testTag("submit_knowledge_check_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Submit Quiz", fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = { viewModel.generateFlashcardQuiz() },
                        colors = ButtonDefaults.buttonColors(containerColor = EditorialPrimary),
                        shape = CircleShape,
                        modifier = Modifier.testTag("retry_knowledge_check_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Retake Quiz", fontWeight = FontWeight.Bold)
                    }
                }

                OutlinedButton(
                    onClick = { viewModel.nextFlashcardQuizQuestion() },
                    enabled = currentIdx < quizQuestions.size - 1,
                    shape = CircleShape,
                    modifier = Modifier.testTag("next_knowledge_question_btn")
                ) {
                    Text("Next")
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.NavigateNext,
                        contentDescription = "Next",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}


