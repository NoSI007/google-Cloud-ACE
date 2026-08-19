package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class IamPersona(
    val title: String,
    val roleName: String,
    val targetLevel: String,
    val explanation: String,
    val gcloudCommand: String,
    val whyNotOwner: String
)

/**
 * Interactive Lab Component for Google Cloud Billing & IAM Permissions.
 * Allows learners to experiment with IAM role assignments, least privilege,
 * budget alerts vs hard cap actions, and Committed Use Discounts (CUD).
 */
@Composable
fun BillingAndPermissionsLabComponent(
    onAskAi: (title: String, context: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) } // 0 = IAM Permissions, 1 = Billing & Budgets

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("billing_permissions_lab_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = EditorialSurface),
        border = BorderStroke(1.dp, EditorialSurfaceVariant)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = EditorialPrimaryContainer,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (selectedTab == 0) Icons.Outlined.Security else Icons.Outlined.AttachMoney,
                                contentDescription = null,
                                tint = EditorialPrimaryDark,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Interactive Lab: Billing & IAM",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = EditorialTextPrimary
                        )
                        Text(
                            text = "Explore Least Privilege roles, spend alerts & CUD discounts",
                            fontSize = 12.sp,
                            color = EditorialTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Sub-tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = EditorialBackground,
                contentColor = EditorialPrimary,
                indicator = {},
                divider = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    modifier = Modifier.testTag("iam_tab_btn"),
                    text = {
                        Text(
                            text = "IAM & Permissions",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    modifier = Modifier.testTag("billing_tab_btn"),
                    text = {
                        Text(
                            text = "Billing & Budgets",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedTab == 0) {
                IamPermissionsSimulatorSection(onAskAi = onAskAi)
            } else {
                BillingBudgetsSimulatorSection(onAskAi = onAskAi)
            }
        }
    }
}

@Composable
private fun IamPermissionsSimulatorSection(
    onAskAi: (title: String, context: String) -> Unit
) {
    val personas = listOf(
        IamPersona(
            title = "Project Creator & Dev Lead",
            roleName = "roles/resourcemanager.projectCreator + roles/billing.user",
            targetLevel = "Organization & Billing Account",
            explanation = "Allows creating new projects and linking them to the corporate billing account without granting full financial payment admin rights.",
            gcloudCommand = "gcloud organizations add-iam-policy-binding ORG_ID \\\n  --member=\"user:lead@corp.com\" \\\n  --role=\"roles/resourcemanager.projectCreator\"",
            whyNotOwner = "Owner at Org level would grant delete access to all enterprise projects and company billing methods."
        ),
        IamPersona(
            title = "Compute VM Deployer",
            roleName = "roles/compute.instanceAdmin.v1 + roles/iam.serviceAccountUser",
            targetLevel = "Project & Service Account",
            explanation = "Allows spinning up VMs and attaching a dedicated Service Account identity to the VM instances for least privilege API calls.",
            gcloudCommand = "gcloud projects add-iam-policy-binding PROJECT_ID \\\n  --member=\"user:dev@corp.com\" \\\n  --role=\"roles/compute.instanceAdmin.v1\"",
            whyNotOwner = "Editor or Owner would allow modifying network firewall rules, IAM bindings, and database schemas."
        ),
        IamPersona(
            title = "Security & Compliance Auditor",
            roleName = "roles/iam.securityReviewer + roles/logging.viewer",
            targetLevel = "Folder or Organization",
            explanation = "Allows read-only auditing of IAM policies, Cloud Audit Logs, and compliance posture across all workloads without changing anything.",
            gcloudCommand = "gcloud organizations add-iam-policy-binding ORG_ID \\\n  --member=\"user:auditor@corp.com\" \\\n  --role=\"roles/iam.securityReviewer\"",
            whyNotOwner = "Auditors must have strictly read-only view access to prevent accidental or malicious policy modifications."
        ),
        IamPersona(
            title = "CI/CD Pipeline (GitHub Actions)",
            roleName = "Workload Identity User (No static JSON keys)",
            targetLevel = "Service Account",
            explanation = "Uses OpenID Connect (OIDC) token exchange to securely deploy Docker images to Artifact Registry without exposing long-lived credentials.",
            gcloudCommand = "gcloud iam service-accounts add-iam-policy-binding SA_EMAIL \\\n  --role=\"roles/iam.workloadIdentityUser\" \\\n  --member=\"principalSet://iam.googleapis.com/...\"",
            whyNotOwner = "Static JSON service account keys are the #1 source of credential leaks; Workload Identity eliminates key exports."
        )
    )

    var selectedPersonaIdx by remember { mutableStateOf(0) }
    val currentPersona = personas[selectedPersonaIdx]

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "SELECT AN ENTERPRISE SCENARIO / PERSONA:",
            style = EditorialLabelCaps,
            color = EditorialPrimary
        )

        // Persona selector pills
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            personas.forEachIndexed { idx, p ->
                val isSel = idx == selectedPersonaIdx
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSel) EditorialPrimary else EditorialBackground,
                    border = BorderStroke(1.dp, if (isSel) EditorialPrimary else EditorialSurfaceVariant),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedPersonaIdx = idx }
                        .testTag("iam_persona_btn_$idx")
                ) {
                    Box(
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = p.title.split(" ").take(2).joinToString(" "),
                            fontSize = 11.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSel) Color.White else EditorialTextPrimary,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        // Persona Details Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = EditorialBackground),
            border = BorderStroke(1.dp, EditorialSurfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = Color(0xFF137333),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Recommended Least-Privilege Role:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color(0xFF137333)
                    )
                }

                Text(
                    text = currentPersona.roleName,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = EditorialPrimaryDark
                )

                Text(
                    text = "Applied At: ${currentPersona.targetLevel}",
                    fontSize = 11.sp,
                    color = EditorialTextSecondary
                )

                HorizontalDivider(color = EditorialSurfaceVariant)

                Text(
                    text = currentPersona.explanation,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    color = EditorialTextPrimary
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EditorialSurface,
                    border = BorderStroke(1.dp, EditorialSurfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Why avoid Primitive Owner/Editor?",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = GoogleRed
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = currentPersona.whyNotOwner,
                            fontSize = 11.sp,
                            color = EditorialTextSecondary
                        )
                    }
                }

                FilledTonalButton(
                    onClick = {
                        onAskAi(
                            "IAM Policy Concept: ${currentPersona.title}",
                            "Explain how to configure ${currentPersona.roleName} in Google Cloud IAM following least privilege, and why policy inheritance is additive."
                        )
                    },
                    shape = CircleShape,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = EditorialPrimaryContainer,
                        contentColor = EditorialPrimaryDark
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Ask Gemini AI: IAM Deep Dive", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun BillingBudgetsSimulatorSection(
    onAskAi: (title: String, context: String) -> Unit
) {
    var monthlyBudget by remember { mutableStateOf(1000f) }
    var selectedThreshold by remember { mutableStateOf("100%") }
    var isAutomatedCapEnabled by remember { mutableStateOf(false) }

    val cudSavings1Yr = (monthlyBudget * 0.37f).toInt()
    val cudSavings3Yr = (monthlyBudget * 0.55f).toInt()
    val spotSavings = (monthlyBudget * 0.80f).toInt()

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "MONTHLY WORKLOAD SPEND ESTIMATOR:",
            style = EditorialLabelCaps,
            color = EditorialPrimary
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Estimated Spend: $${monthlyBudget.toInt()} / month",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = EditorialTextPrimary
            )
        }

        Slider(
            value = monthlyBudget,
            onValueChange = { monthlyBudget = it },
            valueRange = 100f..5000f,
            steps = 49,
            colors = SliderDefaults.colors(
                thumbColor = EditorialPrimary,
                activeTrackColor = EditorialPrimary
            ),
            modifier = Modifier.testTag("budget_spend_slider")
        )

        // Cost Discount Calculator Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = EditorialBackground),
            border = BorderStroke(1.dp, EditorialSurfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "GCP Cost Optimization Savings:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = EditorialPrimaryDark
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("1-Year Committed Use (CUD):", fontSize = 12.sp, color = EditorialTextPrimary)
                    Text("Save ~$cudSavings1Yr/mo (37%)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF137333))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("3-Year Committed Use (CUD):", fontSize = 12.sp, color = EditorialTextPrimary)
                    Text("Save ~$cudSavings3Yr/mo (55%)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF137333))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Spot VMs (Batch workloads):", fontSize = 12.sp, color = EditorialTextPrimary)
                    Text("Save ~$spotSavings/mo (80%)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF137333))
                }
            }
        }

        // Budget Alert & Action Rules
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = if (isAutomatedCapEnabled) Color(0xFFE6F4EA) else Color(0xFFFFF8E1)),
            border = BorderStroke(1.dp, if (isAutomatedCapEnabled) Color(0xFFA8DAB5) else Color(0xFFFFE082)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isAutomatedCapEnabled) Icons.Default.CheckCircle else Icons.Default.Info,
                            contentDescription = null,
                            tint = if (isAutomatedCapEnabled) Color(0xFF137333) else GoogleYellow,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isAutomatedCapEnabled) "Hard Spend Cap Active (Pub/Sub + Functions)" else "Standard Budget Alert (Email Only)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (isAutomatedCapEnabled) Color(0xFF137333) else EditorialTextPrimary
                        )
                    }

                    Switch(
                        checked = isAutomatedCapEnabled,
                        onCheckedChange = { isAutomatedCapEnabled = it },
                        modifier = Modifier.testTag("budget_auto_cap_switch")
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (isAutomatedCapEnabled)
                        "When spend exceeds $${monthlyBudget.toInt()}, a Cloud Pub/Sub message triggers a Cloud Function that disables billing or shuts down non-essential VM instances."
                    else
                        "ACE Rule: Budget alerts send emails only and DO NOT shut down instances by default. Workloads will keep running and accumulating charges.",
                    fontSize = 11.sp,
                    color = EditorialTextSecondary,
                    lineHeight = 16.sp
                )
            }
        }

        FilledTonalButton(
            onClick = {
                onAskAi(
                    "Cloud Billing & Cost Optimization",
                    "Explain the difference between Sustained Use Discounts (SUDs) and Committed Use Discounts (CUDs), and how to configure programmatic budget caps with Cloud Pub/Sub and Cloud Functions."
                )
            },
            shape = CircleShape,
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = EditorialPrimaryContainer,
                contentColor = EditorialPrimaryDark
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Ask Gemini AI: Billing Architecture", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}
