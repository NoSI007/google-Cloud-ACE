package com.example.ui.screens

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
fun ComputeSimulatorScreen(viewModel: AceViewModel) {
    val vmState by viewModel.vmState.collectAsState()
    val simResult by viewModel.vmSimulationResult.collectAsState()

    val families = listOf(
        "N2 (General-Purpose)",
        "E2 (Cost-Optimized)",
        "C2 (Compute-Optimized)",
        "M2 (Memory-Optimized)"
    )

    val diskTypes = listOf(
        "Standard Persistent Disk",
        "Balanced Persistent Disk",
        "SSD Persistent Disk",
        "Local SSD (Ephemeral)"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Memory,
                            contentDescription = null,
                            tint = EditorialPrimary
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Compute Engine Lab",
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
            // Live Simulation Outcome Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("vm_pricing_card"),
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
                                text = "ESTIMATED MONTHLY COST",
                                style = EditorialLabelCaps,
                                color = EditorialPrimaryDark
                            )
                            if (vmState.isSpot) {
                                Surface(
                                    shape = CircleShape,
                                    color = EditorialBadgeBg
                                ) {
                                    Text(
                                        text = "70% SPOT DISCOUNT",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EditorialPrimaryDark,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "$${simResult.estimatedMonthlyCost} / mo",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = EditorialPrimaryDark
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = simResult.machineType + if (vmState.isMig) " (Autoscaling MIG)" else " (Single Instance)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = EditorialTextPrimary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = simResult.recommendationSummary,
                            fontSize = 12.sp,
                            color = EditorialTextSecondary,
                            lineHeight = 16.sp
                        )

                        if (simResult.bestWorkloadUseCases.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Ideal ACE Workloads:",
                                style = EditorialLabelCaps,
                                color = EditorialPrimaryDark
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            simResult.bestWorkloadUseCases.forEach { useCase ->
                                Text(
                                    text = "• $useCase",
                                    fontSize = 12.sp,
                                    color = EditorialTextPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                viewModel.requestAiExpandedExplanation(
                                    topicTitle = "Compute Architecture: ${vmState.family} (${vmState.cpus} vCPUs, ${vmState.ram} GB RAM)",
                                    contextDetail = "Spot VM: ${vmState.isSpot}, Managed Instance Group (MIG): ${vmState.isMig}, Disk: ${vmState.diskType} (${vmState.diskSize} GB)\nRecommendation: ${simResult.recommendationSummary}"
                                )
                            },
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = EditorialPrimaryDark),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("sim_ask_ai_btn")
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


            // Controls Section
            item {
                Text(
                    text = "Configure Parameters",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = EditorialTextPrimary
                )
            }

            // Machine Family Selector
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = EditorialSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EditorialSurfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Machine Family",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = EditorialTextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        families.forEach { fam ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.updateVmFamily(fam) }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = vmState.family == fam,
                                    onClick = { viewModel.updateVmFamily(fam) },
                                    colors = RadioButtonDefaults.colors(selectedColor = EditorialPrimary),
                                    modifier = Modifier.testTag("radio_family_${fam.take(2)}")
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = fam, fontSize = 13.sp, color = EditorialTextPrimary)
                            }
                        }
                    }
                }
            }

            // vCPUs and RAM Sliders
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
                            Text(text = "vCPUs: ${vmState.cpus}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = EditorialTextPrimary)
                            Text(text = "RAM: ${vmState.ram} GB", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = EditorialTextPrimary)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(text = "vCPUs", fontSize = 12.sp, color = EditorialTextSecondary)
                        Slider(
                            value = vmState.cpus.toFloat(),
                            onValueChange = { viewModel.updateVmCpus(it.toInt()) },
                            valueRange = 1f..32f,
                            steps = 30,
                            colors = SliderDefaults.colors(
                                thumbColor = EditorialPrimary,
                                activeTrackColor = EditorialPrimary
                            ),
                            modifier = Modifier.testTag("slider_cpus")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(text = "RAM (GB)", fontSize = 12.sp, color = EditorialTextSecondary)
                        Slider(
                            value = vmState.ram.toFloat(),
                            onValueChange = { viewModel.updateVmRam(it.toInt()) },
                            valueRange = 2f..128f,
                            steps = 62,
                            colors = SliderDefaults.colors(
                                thumbColor = EditorialPrimary,
                                activeTrackColor = EditorialPrimary
                            ),
                            modifier = Modifier.testTag("slider_ram")
                        )
                    }
                }
            }

            // Provisioning Model (Spot vs Standard) & MIG
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
                                    text = "Provisioning Model: Spot VM",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = EditorialTextPrimary
                                )
                                Text(
                                    text = "Preemptible excess capacity with 60-91% savings. Subject to termination.",
                                    fontSize = 11.sp,
                                    color = EditorialTextSecondary
                                )
                            }
                            Switch(
                                checked = vmState.isSpot,
                                onCheckedChange = { viewModel.updateVmIsSpot(it) },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = EditorialPrimary),
                                modifier = Modifier.testTag("switch_spot_vm")
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
                                    text = "Managed Instance Group (MIG)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = EditorialTextPrimary
                                )
                                Text(
                                    text = "Enables Autoscaling & Autohealing health probes across zones.",
                                    fontSize = 11.sp,
                                    color = EditorialTextSecondary
                                )
                            }
                            Switch(
                                checked = vmState.isMig,
                                onCheckedChange = { viewModel.updateVmIsMig(it) },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = EditorialPrimary),
                                modifier = Modifier.testTag("switch_mig")
                            )
                        }
                    }
                }
            }

            // Storage Disk Type & Size
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = EditorialSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EditorialSurfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Attached Block Disk Type",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = EditorialTextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        diskTypes.forEach { dtype ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.updateVmDiskType(dtype) }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = vmState.diskType == dtype,
                                    onClick = { viewModel.updateVmDiskType(dtype) },
                                    colors = RadioButtonDefaults.colors(selectedColor = EditorialPrimary),
                                    modifier = Modifier.testTag("radio_disk_${dtype.take(3)}")
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = dtype, fontSize = 13.sp, color = EditorialTextPrimary)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Disk Size: ${vmState.diskSize} GB",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = EditorialTextPrimary
                        )
                        Slider(
                            value = vmState.diskSize.toFloat(),
                            onValueChange = { viewModel.updateVmDiskSize(it.toInt()) },
                            valueRange = 10f..2000f,
                            steps = 199,
                            colors = SliderDefaults.colors(
                                thumbColor = EditorialPrimary,
                                activeTrackColor = EditorialPrimary
                            ),
                            modifier = Modifier.testTag("slider_disk_size")
                        )
                    }
                }
            }
        }
    }
}

    AiExplanationBottomSheet(viewModel = viewModel)
}

