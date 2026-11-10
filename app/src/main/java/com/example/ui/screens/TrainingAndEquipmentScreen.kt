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
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.FireExtinguisher
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
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
import com.example.data.local.entities.EquipmentPpeEntity
import com.example.data.local.entities.TrainingLogEntity
import com.example.model.Language
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.SafetyOrangePrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TrainingAndEquipmentScreen(
    lang: Language,
    trainingLogs: List<TrainingLogEntity>,
    equipmentList: List<EquipmentPpeEntity>,
    onAddTrainingLog: (topicTitle: String, trainerName: String, attendeesCount: Int, isTbt: Boolean, expiryDays: Int) -> Unit,
    onAddEquipmentPpe: (name: String, category: String, serialNumber: String, assignedTo: String, status: String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Training & TBT, 1: PPE & Fire Equipment
    var showAddTrainingDialog by remember { mutableStateOf(false) }
    var showAddEquipmentDialog by remember { mutableStateOf(false) }

    val sdf = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

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
                    text = if (lang == Language.ARABIC) "التدريب ومعدات الطوارئ والـ PPE" else "Training, PPE & Fire Equipment",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = if (lang == Language.ARABIC) "سجل لقاءات السلامة TBT، الشهادات وصيانة طفايات الحريق" else "TBT talks, certificates & fire extinguisher logs",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tab Selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                label = { Text(if (lang == Language.ARABIC) "سجل التدريب و TBT" else "Training & TBT") },
                modifier = Modifier.weight(1f).testTag("tab_training")
            )
            FilterChip(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                label = { Text(if (lang == Language.ARABIC) "فحص PPE والطفايات" else "PPE & Fire Safety") },
                modifier = Modifier.weight(1f).testTag("tab_ppe")
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (selectedTab == 0) {
            // TAB 1: TRAINING LOGS & TBT
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (lang == Language.ARABIC) "لقاءات السلامة والدورات المسجلة" else "Recorded Training Sessions",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Button(
                    onClick = { showAddTrainingDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = SafetyOrangePrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("add_training_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = if (lang == Language.ARABIC) "جلسة تدريب" else "Add Training", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(trainingLogs) { log ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).testTag("training_card_${log.id}"),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.School, contentDescription = null, tint = SafetyOrangePrimary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (log.isToolboxTalk) "Toolbox Talk (TBT)" else "دورة تدريبية معتمدة",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = SafetyOrangePrimary
                                    )
                                }

                                Surface(shape = CircleShape, color = EmeraldGreen.copy(alpha = 0.15f)) {
                                    Text(
                                        text = "${log.attendeesCount} ${if (lang == Language.ARABIC) "حاضر" else "attendees"}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = EmeraldGreen,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = log.topicTitle,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "${if (lang == Language.ARABIC) "المدرب:" else "Trainer:"} ${log.trainerName} • ${sdf.format(Date(log.trainingDate))}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        } else {
            // TAB 2: PPE & FIRE EXTINGUISHER EQUIPMENT REGISTER
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (lang == Language.ARABIC) "سجل فحص وتخصيص الطفايات و PPE" else "PPE & Fire Equipment Register",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Button(
                    onClick = { showAddEquipmentDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = SafetyOrangePrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("add_equipment_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = if (lang == Language.ARABIC) "إضافة معدة" else "Add Item", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(equipmentList) { eq ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).testTag("equipment_card_${eq.id}"),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.FireExtinguisher, contentDescription = null, tint = SafetyOrangePrimary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = eq.serialNumber,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = SafetyOrangePrimary
                                    )
                                }

                                Surface(shape = RoundedCornerShape(8.dp), color = EmeraldGreen.copy(alpha = 0.15f)) {
                                    Text(
                                        text = eq.inspectionStatus,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = EmeraldGreen,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = eq.equipmentName,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "${if (lang == Language.ARABIC) "المخصص له / المكان:" else "Assigned:"} ${eq.assignedTo} • ${if (lang == Language.ARABIC) "تاريخ الانتهاء:" else "Expires:"} ${sdf.format(Date(eq.nextExpiryDate))}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }
    }

    if (showAddTrainingDialog) {
        AddTrainingDialog(
            lang = lang,
            onDismiss = { showAddTrainingDialog = false },
            onConfirm = { topic, trainer, count, isTbt ->
                onAddTrainingLog(topic, trainer, count, isTbt, 365)
                showAddTrainingDialog = false
            }
        )
    }

    if (showAddEquipmentDialog) {
        AddEquipmentDialog(
            lang = lang,
            onDismiss = { showAddEquipmentDialog = false },
            onConfirm = { name, cat, serial, assigned ->
                onAddEquipmentPpe(name, cat, serial, assigned, "PASS")
                showAddEquipmentDialog = false
            }
        )
    }
}

@Composable
fun AddTrainingDialog(
    lang: Language,
    onDismiss: () -> Unit,
    onConfirm: (topic: String, trainer: String, count: Int, isTbt: Boolean) -> Unit
) {
    var topic by remember { mutableStateOf("") }
    var trainer by remember { mutableStateOf("") }
    var countText by remember { mutableStateOf("15") }
    var isTbt by remember { mutableStateOf(true) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = if (lang == Language.ARABIC) "إضافة جلسة تدريب / TBT" else "Add Training Session", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(value = topic, onValueChange = { topic = it }, label = { Text(if (lang == Language.ARABIC) "موضوع التدريب" else "Topic") }, modifier = Modifier.fillMaxWidth().testTag("add_training_topic_input"), singleLine = true)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(value = trainer, onValueChange = { trainer = it }, label = { Text(if (lang == Language.ARABIC) "اسم المحاضر / المدرب" else "Trainer Name") }, modifier = Modifier.fillMaxWidth().testTag("add_training_trainer_input"), singleLine = true)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(value = countText, onValueChange = { countText = it }, label = { Text(if (lang == Language.ARABIC) "عدد الحضور" else "Attendees Count") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    OutlinedButton(onClick = onDismiss) { Text(if (lang == Language.ARABIC) "إلغاء" else "Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { if (topic.isNotBlank()) onConfirm(topic, trainer.ifBlank { "مسؤول HSE" }, countText.toIntOrNull() ?: 10, isTbt) }, colors = ButtonDefaults.buttonColors(containerColor = SafetyOrangePrimary), modifier = Modifier.testTag("save_training_button")) { Text(if (lang == Language.ARABIC) "حفظ" else "Save") }
                }
            }
        }
    }
}

@Composable
fun AddEquipmentDialog(
    lang: Language,
    onDismiss: () -> Unit,
    onConfirm: (name: String, cat: String, serial: String, assigned: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var serial by remember { mutableStateOf("") }
    var assigned by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = if (lang == Language.ARABIC) "تسجيل طفاية حريق / معدة PPE" else "Register Equipment", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text(if (lang == Language.ARABIC) "اسم المعدة / الطفاية" else "Equipment Name") }, modifier = Modifier.fillMaxWidth().testTag("add_equipment_name_input"), singleLine = true)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(value = serial, onValueChange = { serial = it }, label = { Text(if (lang == Language.ARABIC) "الرقم التسلسلي (Serial #)" else "Serial Number") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(value = assigned, onValueChange = { assigned = it }, label = { Text(if (lang == Language.ARABIC) "المكان / الشخص المخصص له" else "Assigned Location") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    OutlinedButton(onClick = onDismiss) { Text(if (lang == Language.ARABIC) "إلغاء" else "Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { if (name.isNotBlank()) onConfirm(name, "EQUIPMENT", serial.ifBlank { "EQ-100" }, assigned) }, colors = ButtonDefaults.buttonColors(containerColor = SafetyOrangePrimary), modifier = Modifier.testTag("save_equipment_button")) { Text(if (lang == Language.ARABIC) "حفظ" else "Save") }
                }
            }
        }
    }
}
