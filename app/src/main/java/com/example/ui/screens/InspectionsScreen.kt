package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entities.InspectionAuditEntity
import com.example.model.Language
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.HazardRed
import com.example.ui.theme.SafetyOrangePrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun InspectionsScreen(
    lang: Language,
    inspectionAudits: List<InspectionAuditEntity>,
    onCreateInspectionAudit: (
        auditTitle: String,
        category: String,
        auditorName: String,
        compliancePercentage: Int,
        passedItems: Int,
        totalItems: Int,
        findingsJson: String
    ) -> Unit
) {
    var showCreateAuditDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (lang == Language.ARABIC) "التفتيش والمراجعات الميدانية" else "Field Safety Inspections & Audits",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = if (lang == Language.ARABIC) "تقارير نسب المطابقة والتفتيش البيئي" else "Compliance score % & site audits",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Button(
                onClick = { showCreateAuditDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = SafetyOrangePrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("create_audit_button")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = if (lang == Language.ARABIC) "تفتيش جديد" else "New Audit", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (inspectionAudits.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (lang == Language.ARABIC) "لا توجد مراجعات وتدقيقات مسجلة حالياً." else "No inspection audits created yet.",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(inspectionAudits) { audit ->
                    AuditItemCard(audit = audit, lang = lang)
                }
                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }
    }

    if (showCreateAuditDialog) {
        CreateAuditDialog(
            lang = lang,
            onDismiss = { showCreateAuditDialog = false },
            onConfirm = { title, cat, auditor, score, passed, total ->
                onCreateInspectionAudit(title, cat, auditor, score, passed, total, "")
                showCreateAuditDialog = false
            }
        )
    }
}

@Composable
fun AuditItemCard(
    audit: InspectionAuditEntity,
    lang: Language
) {
    val sdf = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }
    val isHighCompliance = audit.compliancePercentage >= 90
    val scoreColor = if (isHighCompliance) EmeraldGreen else SafetyOrangePrimary

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .testTag("audit_card_${audit.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AssignmentTurnedIn,
                        contentDescription = null,
                        tint = scoreColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = audit.auditTitle,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = audit.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = scoreColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Surface(
                    shape = CircleShape,
                    color = scoreColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "${audit.compliancePercentage}%",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                        color = scoreColor,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { audit.compliancePercentage / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = scoreColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${if (lang == Language.ARABIC) "المفتش:" else "Auditor:"} ${audit.auditorName}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Text(
                    text = sdf.format(Date(audit.auditDate)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
fun CreateAuditDialog(
    lang: Language,
    onDismiss: () -> Unit,
    onConfirm: (
        title: String, category: String, auditor: String,
        complianceScore: Int, passedItems: Int, totalItems: Int
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var auditor by remember { mutableStateOf("") }
    var scoreText by remember { mutableStateOf("95") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = if (lang == Language.ARABIC) "إجراء تفتيش ميداني جديد" else "New Field Inspection Audit",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(if (lang == Language.ARABIC) "عنوان التدقيق / التفتيش" else "Audit Title") },
                    modifier = Modifier.fillMaxWidth().testTag("audit_title_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text(if (lang == Language.ARABIC) "تصنيف الفحص (مثل: معدات / بيئة)" else "Category") },
                    modifier = Modifier.fillMaxWidth().testTag("audit_category_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = auditor,
                    onValueChange = { auditor = it },
                    label = { Text(if (lang == Language.ARABIC) "اسم المفتش المعاير" else "Auditor Name") },
                    modifier = Modifier.fillMaxWidth().testTag("audit_auditor_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = scoreText,
                    onValueChange = { scoreText = it },
                    label = { Text(if (lang == Language.ARABIC) "نسبة المطابقة %" else "Compliance Score %") },
                    modifier = Modifier.fillMaxWidth().testTag("audit_score_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text(if (lang == Language.ARABIC) "إلغاء" else "Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                val score = scoreText.toIntOrNull() ?: 90
                                onConfirm(
                                    title,
                                    category.ifBlank { "تفتيش عام" },
                                    auditor.ifBlank { "مفتش السلامة" },
                                    score,
                                    15,
                                    16
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SafetyOrangePrimary),
                        modifier = Modifier.testTag("save_audit_button")
                    ) {
                        Text(if (lang == Language.ARABIC) "حفظ التقرير" else "Save Audit")
                    }
                }
            }
        }
    }
}
