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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import com.example.data.local.entities.RiskAssessmentEntity
import com.example.model.Language
import com.example.model.RiskLevel
import com.example.ui.components.RiskMatrixGrid
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.SafetyOrangePrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RiskAssessmentScreen(
    lang: Language,
    riskAssessments: List<RiskAssessmentEntity>,
    onCreateRiskAssessment: (
        taskTitle: String,
        location: String,
        assessorName: String,
        initialLikelihood: Int,
        initialSeverity: Int,
        residualLikelihood: Int,
        residualSeverity: Int,
        hazardsJson: String,
        controlMeasures: String
    ) -> Unit
) {
    var showCreateDialog by remember { mutableStateOf(false) }

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
                    text = if (lang == Language.ARABIC) "تقييم المخاطر (JSA & Risk Matrix)" else "Risk Assessment & JSA",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = if (lang == Language.ARABIC) "تحليل سلامة المهام ومصفوفة المخاطر 5×5" else "Job Safety Analysis & 5x5 Matrix",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Button(
                onClick = { showCreateDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = SafetyOrangePrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("create_jsa_button")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = if (lang == Language.ARABIC) "تقييم جديد" else "New JSA", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (riskAssessments.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (lang == Language.ARABIC) "لا توجد تقييمات مخاطر مسجلة حالياً." else "No risk assessments created yet.",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(riskAssessments) { jsa ->
                    JsaItemCard(jsa = jsa, lang = lang)
                }
                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }
    }

    if (showCreateDialog) {
        CreateJsaDialog(
            lang = lang,
            onDismiss = { showCreateDialog = false },
            onConfirm = { task, loc, assessor, iL, iS, rL, rS, controls ->
                onCreateRiskAssessment(task, loc, assessor, iL, iS, rL, rS, "", controls)
                showCreateDialog = false
            }
        )
    }
}

@Composable
fun JsaItemCard(
    jsa: RiskAssessmentEntity,
    lang: Language
) {
    val initialRisk = RiskLevel.fromScore(jsa.initialRiskScore)
    val residualRisk = RiskLevel.fromScore(jsa.residualRiskScore)
    val sdf = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .testTag("jsa_card_${jsa.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = jsa.taskTitle,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(android.graphics.Color.parseColor(residualRisk.hexColor)).copy(alpha = 0.18f)
                ) {
                    Text(
                        text = "Residual: ${residualRisk.label(lang)} (${jsa.residualRiskScore})",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(android.graphics.Color.parseColor(residualRisk.hexColor)),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "${if (lang == Language.ARABIC) "المكان:" else "Location:"} ${jsa.location} • ${jsa.assessorName}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(android.graphics.Color.parseColor(initialRisk.hexColor)).copy(alpha = 0.12f)
                ) {
                    Text(
                        text = "Initial Risk: ${initialRisk.label(lang)} (${jsa.initialRiskScore})",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(android.graphics.Color.parseColor(initialRisk.hexColor)),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = sdf.format(Date(jsa.createdAt)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            if (jsa.controlMeasuresJson.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${if (lang == Language.ARABIC) "تدابير الحماية:" else "Controls:"} ${jsa.controlMeasuresJson}",
                    style = MaterialTheme.typography.bodySmall,
                    color = EmeraldGreen
                )
            }
        }
    }
}

@Composable
fun CreateJsaDialog(
    lang: Language,
    onDismiss: () -> Unit,
    onConfirm: (
        task: String, loc: String, assessor: String,
        iL: Int, iS: Int, rL: Int, rS: Int, controls: String
    ) -> Unit
) {
    var taskTitle by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var assessorName by remember { mutableStateOf("") }
    var controls by remember { mutableStateOf("") }

    var initialL by remember { mutableStateOf(4) }
    var initialS by remember { mutableStateOf(4) }
    var residualL by remember { mutableStateOf(1) }
    var residualS by remember { mutableStateOf(2) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            LazyColumn(modifier = Modifier.padding(20.dp)) {
                item {
                    Text(
                        text = if (lang == Language.ARABIC) "إنشاء تحليل سلامة المهمة (JSA)" else "Create Job Safety Analysis (JSA)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                item {
                    OutlinedTextField(
                        value = taskTitle,
                        onValueChange = { taskTitle = it },
                        label = { Text(if (lang == Language.ARABIC) "عنوان المهمة / العمل" else "Task Title") },
                        modifier = Modifier.fillMaxWidth().testTag("jsa_task_title_input"),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                item {
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text(if (lang == Language.ARABIC) "موقع التنفيذ بالضبط" else "Location") },
                        modifier = Modifier.fillMaxWidth().testTag("jsa_location_input"),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                item {
                    OutlinedTextField(
                        value = assessorName,
                        onValueChange = { assessorName = it },
                        label = { Text(if (lang == Language.ARABIC) "اسم مُعد التقييم" else "Assessor Name") },
                        modifier = Modifier.fillMaxWidth().testTag("jsa_assessor_input"),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Initial Risk Matrix Grid
                item {
                    Text(
                        text = if (lang == Language.ARABIC) "1. تحديد الخطر الأولي (Initial Risk):" else "1. Select Initial Risk:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    RiskMatrixGrid(
                        selectedLikelihood = initialL,
                        selectedSeverity = initialS,
                        lang = lang,
                        onCellSelected = { l, s ->
                            initialL = l
                            initialS = s
                        }
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                }

                item {
                    OutlinedTextField(
                        value = controls,
                        onValueChange = { controls = it },
                        label = { Text(if (lang == Language.ARABIC) "تدابير الحماية والتحكم المقترحة" else "Proposed Control Measures") },
                        modifier = Modifier.fillMaxWidth().testTag("jsa_controls_input")
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Residual Risk Matrix Grid
                item {
                    Text(
                        text = if (lang == Language.ARABIC) "2. الخطر المتبقي بعد التحكم (Residual Risk):" else "2. Select Residual Risk:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    RiskMatrixGrid(
                        selectedLikelihood = residualL,
                        selectedSeverity = residualS,
                        lang = lang,
                        onCellSelected = { l, s ->
                            residualL = l
                            residualS = s
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
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
                                if (taskTitle.isNotBlank()) {
                                    onConfirm(
                                        taskTitle, location, assessorName.ifBlank { "مسؤول HSE" },
                                        initialL, initialS, residualL, residualS, controls
                                    )
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SafetyOrangePrimary),
                            modifier = Modifier.testTag("save_jsa_button")
                        ) {
                            Text(if (lang == Language.ARABIC) "حفظ التقييم" else "Save JSA")
                        }
                    }
                }
            }
        }
    }
}
