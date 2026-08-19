package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GcpTerm
import com.example.ui.theme.*

/**
 * Secondary Study Mode Component: Interactive Flippable Term Card for quick review.
 * Flips smoothly in 3D to reveal definitions, ACE exam tips, and architectural insights.
 */
@Composable
fun QuickTermFlipCardComponent(
    terms: List<GcpTerm>,
    onAskAi: (title: String, context: String) -> Unit,
    modifier: Modifier = Modifier,
    initialIndex: Int = 0
) {
    if (terms.isEmpty()) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = EditorialSurface)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No key terms available for this section.",
                    fontSize = 14.sp,
                    color = EditorialTextSecondary
                )
            }
        }
        return
    }

    var currentIndex by remember(terms) { mutableIntStateOf(initialIndex.coerceIn(0, terms.size - 1)) }
    var isFlipped by remember(currentIndex) { mutableStateOf(false) }
    var masteredTermSet by remember { mutableStateOf(setOf<String>()) }
    var isShuffled by remember { mutableStateOf(false) }

    val activeTerms = remember(terms, isShuffled) {
        if (isShuffled) terms.shuffled() else terms
    }

    val safeIndex = currentIndex.coerceIn(0, activeTerms.size - 1)
    val currentTerm = activeTerms[safeIndex]
    val isCurrentMastered = masteredTermSet.contains(currentTerm.acronymOrTerm)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("quick_term_review_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = EditorialSurface),
        border = BorderStroke(1.dp, EditorialSurfaceVariant)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header & Counter Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = EditorialPrimaryContainer,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Flip,
                                contentDescription = null,
                                tint = EditorialPrimaryDark,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Quick Term Review",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = EditorialTextPrimary
                        )
                        Text(
                            text = "Flip to test memory & definitions",
                            fontSize = 12.sp,
                            color = EditorialTextSecondary
                        )
                    }
                }

                // Mastery Status Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isCurrentMastered) Color(0xFFE6F4EA) else EditorialBackground,
                    border = BorderStroke(1.dp, if (isCurrentMastered) Color(0xFFA8DAB5) else EditorialSurfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isCurrentMastered) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = null,
                            tint = if (isCurrentMastered) Color(0xFF137333) else EditorialTextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isCurrentMastered) "Mastered" else "Term ${safeIndex + 1} of ${activeTerms.size}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isCurrentMastered) Color(0xFF137333) else EditorialTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Linear Progress Indicator
            LinearProgressIndicator(
                progress = { (safeIndex + 1).toFloat() / activeTerms.size },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = EditorialPrimary,
                trackColor = EditorialSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 3D Flippable Card Box
            QuickTerm3DFlippableCard(
                term = currentTerm,
                isFlipped = isFlipped,
                onFlipToggle = { isFlipped = !isFlipped },
                onAskAi = onAskAi
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Self-Assessment Mastery & Navigation Controls Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Previous Button
                IconButton(
                    onClick = {
                        if (safeIndex > 0) {
                            currentIndex = safeIndex - 1
                            isFlipped = false
                        }
                    },
                    enabled = safeIndex > 0,
                    modifier = Modifier.testTag("quick_term_prev_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Previous Term",
                        tint = if (safeIndex > 0) EditorialTextPrimary else EditorialTextSecondary.copy(alpha = 0.4f)
                    )
                }

                // Mastery Toggle Pill (Got it vs Review Later)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilledTonalButton(
                        onClick = { isFlipped = !isFlipped },
                        shape = CircleShape,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = EditorialPrimaryContainer,
                            contentColor = EditorialPrimaryDark
                        ),
                        modifier = Modifier.testTag("quick_term_flip_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Flip, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isFlipped) "Show Term" else "Reveal Definition",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = {
                            masteredTermSet = if (isCurrentMastered) {
                                masteredTermSet - currentTerm.acronymOrTerm
                            } else {
                                masteredTermSet + currentTerm.acronymOrTerm
                            }
                        },
                        modifier = Modifier.testTag("quick_term_toggle_mastery_btn")
                    ) {
                        Icon(
                            imageVector = if (isCurrentMastered) Icons.Default.BookmarkAdded else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark Mastery",
                            tint = if (isCurrentMastered) EditorialPrimaryDark else EditorialTextSecondary
                        )
                    }
                }

                // Next Button
                IconButton(
                    onClick = {
                        if (safeIndex < activeTerms.size - 1) {
                            currentIndex = safeIndex + 1
                            isFlipped = false
                        }
                    },
                    enabled = safeIndex < activeTerms.size - 1,
                    modifier = Modifier.testTag("quick_term_next_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next Term",
                        tint = if (safeIndex < activeTerms.size - 1) EditorialTextPrimary else EditorialTextSecondary.copy(alpha = 0.4f)
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickTerm3DFlippableCard(
    term: GcpTerm,
    isFlipped: Boolean,
    onFlipToggle: () -> Unit,
    onAskAi: (title: String, context: String) -> Unit
) {
    val density = LocalDensity.current.density
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "term_flip_rotation"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 280.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onFlipToggle
            )
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .testTag("flippable_term_card_surface")
    ) {
        if (rotation <= 90f) {
            // FRONT OF CARD: Term / Prompt
            FrontTermCardFace(term = term, onFlipToggle = onFlipToggle)
        } else {
            // BACK OF CARD: Definition / Exam Tip (Rotated 180 so it appears upright)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { rotationY = 180f }
            ) {
                BackTermCardFace(
                    term = term,
                    onFlipToggle = onFlipToggle,
                    onAskAi = {
                        onAskAi(
                            "Google Cloud Concept: ${term.fullName} (${term.acronymOrTerm})",
                            "Category: ${term.category}\nDefinition: ${term.definition}\nACE Exam Tip: ${term.aceExamTip}"
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun FrontTermCardFace(
    term: GcpTerm,
    onFlipToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = EditorialBackground),
        border = BorderStroke(1.dp, EditorialSurfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Category Badge
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = EditorialPrimaryContainer,
                border = BorderStroke(1.dp, EditorialBorderAccent)
            ) {
                Text(
                    text = term.category,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = EditorialPrimaryDark,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }

            // Main Acronym / Term
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = term.acronymOrTerm,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    color = EditorialTextPrimary,
                    textAlign = TextAlign.Center
                )

                if (term.fullName.isNotEmpty() && term.fullName != term.acronymOrTerm) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = term.fullName,
                        fontSize = 14.sp,
                        color = EditorialTextSecondary,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Tap to Flip Prompt
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.TouchApp,
                    contentDescription = null,
                    tint = EditorialPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Tap card to reveal definition & exam tip",
                    fontSize = 12.sp,
                    color = EditorialPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun BackTermCardFace(
    term: GcpTerm,
    onFlipToggle: () -> Unit,
    onAskAi: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = EditorialBackground),
        border = BorderStroke(1.5.dp, EditorialPrimary.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // Header Row with Term Name
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = term.fullName.ifEmpty { term.acronymOrTerm },
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = EditorialPrimaryDark,
                        maxLines = 1
                    )
                    Surface(
                        shape = CircleShape,
                        color = EditorialPrimaryContainer,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = EditorialPrimaryDark,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Definition Text
                Text(
                    text = term.definition,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = EditorialTextPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))

                // ACE Exam Tip Highlight Box
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = EditorialBadgeBg,
                    border = BorderStroke(1.dp, EditorialBorderAccent),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFB06000),
                            modifier = Modifier
                                .size(16.dp)
                                .padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "ACE Exam Tip:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = Color(0xFFB06000)
                            )
                            Text(
                                text = term.aceExamTip,
                                fontSize = 11.sp,
                                lineHeight = 15.sp,
                                color = EditorialTextPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // AI Deep Dive Button
            FilledTonalButton(
                onClick = onAskAi,
                shape = CircleShape,
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = EditorialPrimaryContainer,
                    contentColor = EditorialPrimaryDark
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quick_term_ask_ai_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Ask Gemini AI: Explain ${term.acronymOrTerm}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
