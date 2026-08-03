package com.example.ui.screens

import androidx.compose.foundation.background
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
fun StorageSimulatorScreen(viewModel: AceViewModel) {
    val storageState by viewModel.storageState.collectAsState()
    val simResult by viewModel.storageSimulationResult.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Storage,
                            contentDescription = null,
                            tint = EditorialPrimary
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Cloud Storage Lab",
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
                    .widthIn(max = 900.dp),
                contentPadding = PaddingValues(horizontal = horizontalPadding, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
            // Recommendation Result Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("storage_class_result_card"),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = EditorialPrimaryContainer),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EditorialBorderAccent)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "RECOMMENDED STORAGE CLASS",
                                style = EditorialLabelCaps,
                                color = EditorialPrimaryDark
                            )
                            Surface(
                                shape = CircleShape,
                                color = EditorialBadgeBg
                            ) {
                                Text(
                                    text = "$${simResult.estimatedStorageCostPerGb} / GB / mo",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EditorialPrimaryDark,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = simResult.recommendedClass,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = EditorialPrimaryDark
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = simResult.recommendedLocation,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = EditorialTextPrimary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = simResult.keyExamRule,
                            fontSize = 12.sp,
                            color = EditorialTextSecondary,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Lifecycle Strategy:",
                            style = EditorialLabelCaps,
                            color = EditorialPrimaryDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = simResult.lifecycleRecommendation,
                            fontSize = 12.sp,
                            color = EditorialTextPrimary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                viewModel.requestAiExpandedExplanation(
                                    topicTitle = "Storage Architecture: ${simResult.recommendedClass} (${simResult.recommendedLocation})",
                                    contextDetail = "Access Frequency: ${storageState.accessFreqDays} days, Retention: ${storageState.retentionMonths} months, Multi-Region: ${storageState.isMultiRegion}, Lifecycle Policy: ${storageState.hasLifecycle}\nExam Rule: ${simResult.keyExamRule}"
                                )
                            },
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = EditorialPrimaryDark),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("storage_ask_ai_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Ask AI for Architecture Deep Dive",
                                fontSize = 12.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }


            // Interactive Sliders & Configuration
            item {
                Text(
                    text = "Data Access & Retention Profile",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = EditorialTextPrimary
                )
            }

            // Access Frequency Slider
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
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Access Frequency",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = EditorialTextPrimary
                            )
                            Text(
                                text = when {
                                    storageState.accessFreqDays <= 1 -> "Daily (Hot)"
                                    storageState.accessFreqDays <= 30 -> "Monthly"
                                    storageState.accessFreqDays <= 90 -> "Quarterly"
                                    else -> "Yearly (Cold)"
                                },
                                fontWeight = FontWeight.Bold,
                                color = EditorialPrimary,
                                fontSize = 13.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Slider(
                            value = storageState.accessFreqDays.toFloat(),
                            onValueChange = { viewModel.updateStorageAccessFreq(it.toInt()) },
                            valueRange = 1f..365f,
                            colors = SliderDefaults.colors(
                                thumbColor = EditorialPrimary,
                                activeTrackColor = EditorialPrimary
                            ),
                            modifier = Modifier.testTag("slider_access_freq")
                        )

                        Text(
                            text = "Access interval: Every ${storageState.accessFreqDays} days",
                            fontSize = 12.sp,
                            color = EditorialTextSecondary
                        )
                    }
                }
            }

            // Retention Period & Multi-Region Toggles
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
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Location Redundancy: Multi-Region",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = EditorialTextPrimary
                                )
                                Text(
                                    text = "Replicates objects across multiple geographic regions for high availability.",
                                    fontSize = 11.sp,
                                    color = EditorialTextSecondary
                                )
                            }
                            Switch(
                                checked = storageState.isMultiRegion,
                                onCheckedChange = { viewModel.updateStorageIsMultiRegion(it) },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = EditorialPrimary),
                                modifier = Modifier.testTag("switch_multi_region")
                            )
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = EditorialSurfaceVariant
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Object Lifecycle Management Rules",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = EditorialTextPrimary
                                )
                                Text(
                                    text = "Automates transitioning aging objects to colder classes or deleting expired backups.",
                                    fontSize = 11.sp,
                                    color = EditorialTextSecondary
                                )
                            }
                            Switch(
                                checked = storageState.hasLifecycle,
                                onCheckedChange = { viewModel.updateStorageHasLifecycle(it) },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = EditorialPrimary),
                                modifier = Modifier.testTag("switch_lifecycle_rule")
                            )
                        }
                    }
                }
            }

            // Quick Reference Comparison Table Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = EditorialSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EditorialSurfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "ACE Quick Reference: Storage Classes",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = EditorialTextPrimary
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        val tableData = listOf(
                            Triple("Standard", "Frequent", "Zero min duration"),
                            Triple("Nearline", "< 1x / month", "30-day min duration"),
                            Triple("Coldline", "< 1x / quarter", "90-day min duration"),
                            Triple("Archive", "< 1x / year", "365-day min duration")
                        )

                        tableData.forEachIndexed { idx, (cName, access, dur) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = cName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = EditorialPrimary,
                                    modifier = Modifier.weight(0.3f)
                                )
                                Text(
                                    text = access,
                                    fontSize = 12.sp,
                                    color = EditorialTextPrimary,
                                    modifier = Modifier.weight(0.35f)
                                )
                                Text(
                                    text = dur,
                                    fontSize = 12.sp,
                                    color = EditorialTextSecondary,
                                    modifier = Modifier.weight(0.35f)
                                )
                            }
                            if (idx < tableData.size - 1) {
                                HorizontalDivider(color = EditorialSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}

    AiExplanationBottomSheet(viewModel = viewModel)
}

