package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AceViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiExplanationBottomSheet(
    viewModel: AceViewModel
) {
    val aiState by viewModel.aiExplanationState.collectAsState()

    if (aiState.isOpen) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.dismissAiExplanation() },
            containerColor = EditorialBackground,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            modifier = Modifier.testTag("ai_explanation_bottom_sheet")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                // Header with AI Title & Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = EditorialPrimaryContainer
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "AI Sparkle",
                                tint = EditorialPrimaryDark,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "GEMINI AI DEEP DIVE",
                                style = EditorialLabelCaps,
                                color = EditorialPrimary
                            )
                            Text(
                                text = "Expanded Concept Explanation",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = EditorialTextPrimary
                            )
                        }
                    }

                    IconButton(
                        onClick = { viewModel.dismissAiExplanation() },
                        modifier = Modifier.testTag("close_ai_sheet_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = EditorialTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Topic Badge Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = EditorialSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EditorialSurfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = EditorialPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = aiState.topicTitle,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = EditorialTextPrimary
                            )
                            if (aiState.contextDetail.isNotBlank()) {
                                Text(
                                    text = aiState.contextDetail,
                                    fontSize = 12.sp,
                                    color = EditorialTextSecondary,
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Loading State or Result Text
                if (aiState.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                color = EditorialPrimary,
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Consulting Gemini AI Tutor...",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = EditorialTextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Analyzing GCP architecture patterns & ACE exam rules",
                                fontSize = 12.sp,
                                color = EditorialTextSecondary
                            )
                        }
                    }
                } else {
                    val scrollState = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 380.dp)
                            .verticalScroll(scrollState)
                    ) {
                        Text(
                            text = aiState.explanationText,
                            fontSize = 13.sp,
                            lineHeight = 20.sp,
                            color = EditorialTextPrimary,
                            fontWeight = FontWeight.Normal
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = {
                                viewModel.requestAiExpandedExplanation(
                                    aiState.topicTitle,
                                    aiState.contextDetail
                                )
                            },
                            shape = CircleShape,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = EditorialPrimary),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EditorialPrimary),
                            modifier = Modifier.testTag("refresh_ai_explanation_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Regenerate",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Re-query AI", fontSize = 12.sp)
                        }

                        Button(
                            onClick = { viewModel.dismissAiExplanation() },
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = EditorialPrimary),
                            modifier = Modifier.testTag("done_ai_sheet_btn")
                        ) {
                            Text("Got it", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
