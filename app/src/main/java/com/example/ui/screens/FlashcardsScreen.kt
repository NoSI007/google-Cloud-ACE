package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.AceFlashcardEntity
import com.example.ui.AceViewModel
import com.example.ui.components.AiExplanationBottomSheet
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardsScreen(
    viewModel: AceViewModel,
    onNavigateBack: (() -> Unit)? = null
) {
    val roomCards by viewModel.roomFlashcards.collectAsStateWithLifecycle()
    
    var selectedCategory by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var currentIndex by remember { mutableIntStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }

    // Categories list
    val categories = remember {
        listOf("All", "Compute", "Storage & Database", "Networking", "Security & IAM", "DevOps & Operations", "Big Data & AI")
    }

    // Filter cards based on selected category and search query
    val filteredCards = remember(roomCards, selectedCategory, searchQuery) {
        roomCards.filter { card ->
            val matchesCat = selectedCategory == "All" || card.serviceCategory.equals(selectedCategory, ignoreCase = true)
            val q = searchQuery.trim().lowercase()
            val matchesQuery = q.isEmpty() ||
                    card.serviceName.lowercase().contains(q) ||
                    card.frontPrompt.lowercase().contains(q) ||
                    card.backDefinition.lowercase().contains(q) ||
                    card.examTip.lowercase().contains(q)
            matchesCat && matchesQuery
        }
    }

    // Reset index & flip state when filters change
    LaunchedEffect(selectedCategory, searchQuery) {
        currentIndex = 0
        isFlipped = false
    }

    // Ensure index stays in bounds
    val safeIndex = if (filteredCards.isEmpty()) 0 else currentIndex.coerceIn(0, filteredCards.size - 1)
    val currentCard = filteredCards.getOrNull(safeIndex)

    // Calculate mastery stats
    val totalCount = roomCards.size
    val masteredCount = roomCards.count { it.isMastered }
    val masteryPercentage = if (totalCount > 0) (masteredCount.toFloat() / totalCount) else 0f

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "ACE Flashcards",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = EditorialTextPrimary
                        )
                        Text(
                            text = "Room DB Schema • 3D Flip Animation",
                            fontSize = 12.sp,
                            color = EditorialTextSecondary
                        )
                    }
                },
                navigationIcon = {
                    if (onNavigateBack != null) {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.testTag("flashcards_back_button")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Default.Flip,
                            contentDescription = "Flashcards Icon",
                            tint = EditorialPrimary,
                            modifier = Modifier.padding(start = 16.dp, end = 8.dp)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showAddDialog = true },
                        modifier = Modifier.testTag("add_custom_card_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddCard,
                            contentDescription = "Add Custom Card",
                            tint = EditorialPrimary
                        )
                    }
                    IconButton(
                        onClick = { viewModel.resetRoomFlashcardProgress() },
                        modifier = Modifier.testTag("reset_mastery_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = "Reset Progress",
                            tint = EditorialTextSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = EditorialSurface)
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
            val isWideScreen = maxWidth > 650.dp
            val horizontalPadding = if (isWideScreen) 40.dp else 16.dp

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 850.dp)
                    .padding(horizontal = horizontalPadding, vertical = 12.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Room DB Mastery Progress Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = EditorialSurface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Storage,
                                    contentDescription = null,
                                    tint = EditorialPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Room Persistence Mastery",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = EditorialTextPrimary
                                )
                            }
                            Text(
                                text = "$masteredCount / $totalCount Mastered (${(masteryPercentage * 100).toInt()}%)",
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.sp,
                                color = EditorialPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        LinearProgressIndicator(
                            progress = { masteryPercentage },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = EditorialPrimary,
                            trackColor = EditorialSurfaceVariant
                        )
                    }
                }

                // Category Filter Chips Row
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { category ->
                        val isSelected = selectedCategory == category
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = category },
                            label = { Text(category, fontSize = 13.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EditorialPrimaryContainer,
                                selectedLabelColor = EditorialPrimaryDark,
                                containerColor = EditorialSurface,
                                labelColor = EditorialTextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = EditorialBorder,
                                selectedBorderColor = EditorialPrimary
                            ),
                            modifier = Modifier.testTag("filter_chip_$category")
                        )
                    }
                }

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search GCP service or exam tip...", fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = EditorialTextSecondary) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear search")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("flashcard_search_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = EditorialSurface,
                        unfocusedContainerColor = EditorialSurface,
                        focusedBorderColor = EditorialPrimary,
                        unfocusedBorderColor = EditorialBorder
                    )
                )

                // Flashcard Status Bar (e.g., Card 3 of 15)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (filteredCards.isNotEmpty()) "Card ${safeIndex + 1} of ${filteredCards.size}" else "No cards found",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = EditorialTextSecondary
                    )
                    if (currentCard != null && currentCard.isMastered) {
                        Surface(
                            color = Color(0xFFE8F5E9),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF81C784))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF2E7D32),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Mastered",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32)
                                )
                            }
                        }
                    }
                }

                // Main 3D Flipping Flashcard Container
                if (currentCard != null) {
                    Flashcard3DItem(
                        card = currentCard,
                        isFlipped = isFlipped,
                        onFlipToggle = { isFlipped = !isFlipped },
                        onToggleMastery = { viewModel.toggleRoomFlashcardMastery(currentCard.id, currentCard.isMastered) },
                        onRequestAiExplanation = {
                            viewModel.requestAiExpandedExplanation(
                                topicTitle = currentCard.serviceName,
                                contextDetail = "Service Category: ${currentCard.serviceCategory}\nDefinition: ${currentCard.backDefinition}\nACE Exam Tip: ${currentCard.examTip}"
                            )
                        }
                    )
                } else {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp),
                        colors = CardDefaults.cardColors(containerColor = EditorialSurface),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.SearchOff,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = EditorialTextSecondary
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "No flashcards match your filter.",
                                    fontSize = 16.sp,
                                    color = EditorialTextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = {
                                        selectedCategory = "All"
                                        searchQuery = ""
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = EditorialPrimary)
                                ) {
                                    Text("Reset Filters")
                                }
                            }
                        }
                    }
                }

                // Navigation & Control Action Buttons Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = {
                            if (safeIndex > 0) {
                                currentIndex = safeIndex - 1
                                isFlipped = false
                            }
                        },
                        enabled = safeIndex > 0,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("flashcard_prev_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Prev")
                    }

                    Button(
                        onClick = { isFlipped = !isFlipped },
                        colors = ButtonDefaults.buttonColors(containerColor = EditorialPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 12.dp)
                            .testTag("flashcard_flip_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Flip,
                            contentDescription = "Flip Card",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isFlipped) "Show Prompt" else "Flip Card", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            if (filteredCards.isNotEmpty() && safeIndex < filteredCards.size - 1) {
                                currentIndex = safeIndex + 1
                                isFlipped = false
                            }
                        },
                        enabled = filteredCards.isNotEmpty() && safeIndex < filteredCards.size - 1,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("flashcard_next_button")
                    ) {
                        Text("Next")
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                }

                // Secondary Action Row: Shuffle & Add Custom Card
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            if (filteredCards.isNotEmpty()) {
                                currentIndex = (0 until filteredCards.size).random()
                                isFlipped = false
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("shuffle_cards_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Shuffle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Shuffle Cards")
                    }

                    FilledTonalButton(
                        onClick = { showAddDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("create_card_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(containerColor = EditorialPrimaryContainer)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add Custom Card", color = EditorialPrimaryDark, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Add Custom Flashcard Dialog
    if (showAddDialog) {
        AddCustomFlashcardDialog(
            onDismiss = { showAddDialog = false },
            onSave = { serviceName, category, prompt, definition, examTip, keyFeatures ->
                viewModel.addCustomFlashcard(serviceName, category, prompt, definition, examTip, keyFeatures)
                showAddDialog = false
            }
        )
    }

    // Gemini AI Explanation Bottom Sheet
    AiExplanationBottomSheet(viewModel = viewModel)
}

@Composable
fun Flashcard3DItem(
    card: AceFlashcardEntity,
    isFlipped: Boolean,
    onFlipToggle: () -> Unit,
    onToggleMastery: () -> Unit,
    onRequestAiExplanation: () -> Unit
) {
    val density = LocalDensity.current.density
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "card_rotation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 340.dp)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 14f * density
            }
            .clickable { onFlipToggle() }
            .testTag("flashcard_3d_card"),
        colors = CardDefaults.cardColors(
            containerColor = if (isFlipped) EditorialSurface else EditorialSurfaceVariant
        ),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, EditorialBorderAccent)
    ) {
        if (rotation <= 90f) {
            // FRONT OF FLASHCARD
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Front Top Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = EditorialPrimaryContainer,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = card.serviceCategory,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = EditorialPrimaryDark,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }

                    IconButton(onClick = onToggleMastery) {
                        Icon(
                            imageVector = if (card.isMastered) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = "Toggle Mastery",
                            tint = if (card.isMastered) Color(0xFF2E7D32) else EditorialTextSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Front Center Content
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Surface(
                        color = EditorialPrimary.copy(alpha = 0.1f),
                        shape = CircleShape,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = when (card.serviceCategory) {
                                    "Compute" -> Icons.Default.Dns
                                    "Storage & Database" -> Icons.Default.Storage
                                    "Networking" -> Icons.Default.Router
                                    "Security & IAM" -> Icons.Default.Security
                                    "DevOps & Operations" -> Icons.Default.Build
                                    else -> Icons.Default.Cloud
                                },
                                contentDescription = null,
                                tint = EditorialPrimary,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = card.serviceName,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        color = EditorialTextPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = card.frontPrompt,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = EditorialTextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }

                // Front Bottom Cue
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.TouchApp,
                        contentDescription = null,
                        tint = EditorialPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Tap card to flip answer",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = EditorialPrimary
                    )
                }
            }
        } else {
            // BACK OF FLASHCARD (Rotate 180 back so text renders upright)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { rotationY = 180f }
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Back Top Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = card.serviceName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif,
                            color = EditorialTextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = EditorialBadgeBg,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Answer & Tip",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EditorialPrimaryDark,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Divider(color = EditorialBorder)

                    // Definition Section
                    Text(
                        text = card.backDefinition,
                        fontSize = 14.sp,
                        color = EditorialTextPrimary,
                        lineHeight = 20.sp
                    )

                    // Key Features List
                    if (card.keyFeaturesCsv.isNotEmpty()) {
                        Column {
                            Text(
                                text = "Key Capabilities:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = EditorialTextSecondary
                            )
                            val features = card.keyFeaturesCsv.split(",")
                            features.take(3).forEach { feat ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 1.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = EditorialPrimary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = feat.trim(),
                                        fontSize = 12.sp,
                                        color = EditorialTextSecondary
                                    )
                                }
                            }
                        }
                    }

                    // ACE Exam Tip Highlight Box
                    Surface(
                        color = Color(0xFFFFF8E1),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFE082)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = Color(0xFFF57F17),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "ACE Exam Tip",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color(0xFFE65100)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = card.examTip,
                                    fontSize = 12.sp,
                                    color = Color(0xFF3E2723),
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }

                // Back Bottom Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onToggleMastery,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = if (card.isMastered) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = null,
                            tint = if (card.isMastered) Color(0xFF2E7D32) else EditorialTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (card.isMastered) "Mastered" else "Mark Mastered",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    FilledTonalButton(
                        onClick = onRequestAiExplanation,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(containerColor = EditorialPrimaryContainer)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = EditorialPrimaryDark,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Ask AI Deep Dive",
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

@Composable
fun AddCustomFlashcardDialog(
    onDismiss: () -> Unit,
    onSave: (serviceName: String, category: String, prompt: String, definition: String, examTip: String, keyFeatures: String) -> Unit
) {
    var serviceName by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Compute") }
    var prompt by remember { mutableStateOf("") }
    var definition by remember { mutableStateOf("") }
    var examTip by remember { mutableStateOf("") }
    var keyFeatures by remember { mutableStateOf("") }

    val categories = remember {
        listOf("Compute", "Storage & Database", "Networking", "Security & IAM", "DevOps & Operations", "Big Data & AI")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add Custom Flashcard to Room DB",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = serviceName,
                    onValueChange = { serviceName = it },
                    label = { Text("Service Name (e.g. Cloud Run)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Text("Category:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EditorialTextSecondary)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = prompt,
                    onValueChange = { prompt = it },
                    label = { Text("Front Prompt Question") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = definition,
                    onValueChange = { definition = it },
                    label = { Text("Back Answer Definition") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = examTip,
                    onValueChange = { examTip = it },
                    label = { Text("ACE Exam Tip") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = keyFeatures,
                    onValueChange = { keyFeatures = it },
                    label = { Text("Key Features (comma separated)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (serviceName.isNotBlank() && prompt.isNotBlank()) {
                        onSave(
                            serviceName.ifBlank { "Custom GCP Service" },
                            category,
                            prompt,
                            definition.ifBlank { "Custom user definition." },
                            examTip.ifBlank { "ACE Tip: Review configuration details." },
                            keyFeatures
                        )
                    }
                },
                enabled = serviceName.isNotBlank() && prompt.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = EditorialPrimary)
            ) {
                Text("Save to Room DB")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
