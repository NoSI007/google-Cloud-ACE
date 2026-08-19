package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.AceViewModel
import com.example.ui.theme.*
import com.example.util.StudyNotesExporter
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ExportStudyGuideDialog(
    viewModel: AceViewModel,
    onDismiss: () -> Unit,
    preselectedCliOnly: Boolean = false
) {
    val context = LocalContext.current

    var includeLessons by remember { mutableStateOf(!preselectedCliOnly) }
    var includeCli by remember { mutableStateOf(true) }
    var includeBestPractices by remember { mutableStateOf(true) }
    var includeGlossary by remember { mutableStateOf(!preselectedCliOnly) }

    val fullText = remember(includeLessons, includeCli, includeBestPractices, includeGlossary) {
        StudyNotesExporter.generateCompleteStudyGuide(
            modules = viewModel.modules,
            cliCommands = viewModel.allCliCommands,
            bestPractices = viewModel.allBestPractices,
            terms = viewModel.allGcpTerms,
            includeLessons = includeLessons,
            includeCli = includeCli,
            includeBestPractices = includeBestPractices,
            includeGlossary = includeGlossary
        )
    }

    val defaultFileName = remember {
        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmm", Locale.US)
        val dateStr = dateFormat.format(Date())
        "GCP_ACE_Study_Guide_$dateStr.txt"
    }

    val saveFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/plain")
    ) { uri ->
        if (uri != null) {
            val success = StudyNotesExporter.writeTextToUri(context, uri, fullText)
            if (success) {
                onDismiss()
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("export_study_guide_dialog"),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = EditorialSurface),
            border = BorderStroke(1.dp, EditorialSurfaceVariant)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
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
                            color = EditorialPrimaryContainer,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.FileDownload,
                                    contentDescription = null,
                                    tint = EditorialPrimaryDark,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "Export for Offline",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = EditorialTextPrimary
                            )
                            Text(
                                text = "Plain text / Markdown study file",
                                fontSize = 12.sp,
                                color = EditorialTextSecondary
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_export_dialog_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = EditorialTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Select sections to bundle into the local text file:",
                    fontSize = 13.sp,
                    color = EditorialTextSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Options
                ExportOptionRow(
                    title = "Comprehensive Lesson Notes",
                    subtitle = "All ${viewModel.modules.size} modules, architecture notes & exam tips",
                    icon = Icons.Default.MenuBook,
                    checked = includeLessons,
                    onCheckedChange = { includeLessons = it },
                    testTag = "checkbox_export_lessons"
                )

                Spacer(modifier = Modifier.height(8.dp))

                ExportOptionRow(
                    title = "Quick Reference CLI Commands",
                    subtitle = "${viewModel.allCliCommands.size} gcloud, gsutil & kubectl commands with flags",
                    icon = Icons.Default.Terminal,
                    checked = includeCli,
                    onCheckedChange = { includeCli = it },
                    testTag = "checkbox_export_cli"
                )

                Spacer(modifier = Modifier.height(8.dp))

                ExportOptionRow(
                    title = "Architecture Best Practices",
                    subtitle = "${viewModel.allBestPractices.size} rules, actionable guidelines & anti-patterns",
                    icon = Icons.Default.Verified,
                    checked = includeBestPractices,
                    onCheckedChange = { includeBestPractices = it },
                    testTag = "checkbox_export_best_practices"
                )

                Spacer(modifier = Modifier.height(8.dp))

                ExportOptionRow(
                    title = "Key Terms & Acronym Glossary",
                    subtitle = "${viewModel.allGcpTerms.size} cloud terms, definitions & exam pointers",
                    icon = Icons.Default.Abc,
                    checked = includeGlossary,
                    onCheckedChange = { includeGlossary = it },
                    testTag = "checkbox_export_glossary"
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Document Stats Badge
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = EditorialBadgeBg,
                    border = BorderStroke(1.dp, EditorialBorderAccent)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = EditorialPrimaryDark,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "File Size Estimate:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = EditorialPrimaryDark
                            )
                        }
                        val approxChars = fullText.length
                        val approxWords = fullText.split("\\s+".toRegex()).size
                        Text(
                            text = "~${approxWords} words (${(approxChars / 1024).coerceAtLeast(1)} KB)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = EditorialTextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Save to Local File (.txt) via SAF
                    Button(
                        onClick = {
                            if (!includeLessons && !includeCli && !includeBestPractices && !includeGlossary) {
                                Toast.makeText(context, "Please select at least one section to export", Toast.LENGTH_SHORT).show()
                            } else {
                                saveFileLauncher.launch(defaultFileName)
                            }
                        },
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = EditorialPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("save_text_file_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SaveAlt,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Save Local File (.txt)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Share via Intent
                    OutlinedButton(
                        onClick = {
                            if (!includeLessons && !includeCli && !includeBestPractices && !includeGlossary) {
                                Toast.makeText(context, "Please select at least one section to export", Toast.LENGTH_SHORT).show()
                            } else {
                                StudyNotesExporter.shareText(context, fullText, "Google Cloud ACE Offline Study Guide")
                                onDismiss()
                            }
                        },
                        shape = CircleShape,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = EditorialPrimary),
                        border = BorderStroke(1.dp, EditorialPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("share_text_guide_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Share / Open in Other Apps",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Copy to Clipboard
                    TextButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("ACE Study Guide", fullText)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Full study guide copied to clipboard", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("copy_full_guide_clipboard_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            tint = EditorialTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Copy Full Text to Clipboard",
                            fontSize = 12.sp,
                            color = EditorialTextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ExportOptionRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onCheckedChange(!checked) },
        shape = RoundedCornerShape(14.dp),
        color = if (checked) EditorialPrimaryContainer.copy(alpha = 0.5f) else EditorialBackground,
        border = BorderStroke(1.dp, if (checked) EditorialPrimary.copy(alpha = 0.5f) else EditorialSurfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (checked) EditorialPrimaryDark else EditorialTextSecondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialTextPrimary
                    )
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = EditorialTextSecondary
                    )
                }
            }

            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = EditorialPrimary,
                    checkmarkColor = Color.White
                ),
                modifier = Modifier.testTag(testTag)
            )
        }
    }
}
