package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
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
import androidx.compose.runtime.mutableStateListOf
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
import com.example.model.CapaStatus
import com.example.model.Language
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.HazardRed
import com.example.ui.theme.SafetyOrangePrimary
import com.example.ui.theme.SafetyYellow
import org.json.JSONArray
import org.json.JSONObject

data class CapaItem(
    val action: String,
    val owner: String,
    val dueDate: String,
    var status: String
)

@Composable
fun CapaTrackerView(
    capaJson: String,
    lang: Language = Language.ARABIC,
    onCapaUpdated: (updatedJson: String) -> Unit
) {
    var showAddCapaDialog by remember { mutableStateOf(false) }

    val capaItems = remember(capaJson) {
        val list = mutableStateListOf<CapaItem>()
        try {
            val jsonArray = JSONArray(capaJson)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    CapaItem(
                        action = obj.optString("action", ""),
                        owner = obj.optString("owner", ""),
                        dueDate = obj.optString("dueDate", ""),
                        status = obj.optString("status", CapaStatus.PENDING.name)
                    )
                )
            }
        } catch (e: Exception) {
            // Default placeholder if empty
        }
        list
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
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
                        tint = SafetyOrangePrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (lang == Language.ARABIC) "الإجراءات التصحيحية والوقائية (CAPA Plan)" else "CAPA Action Plan",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Button(
                    onClick = { showAddCapaDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = SafetyOrangePrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("add_capa_item_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = if (lang == Language.ARABIC) "إجراء جديد" else "Add Action", fontSize = 10.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (capaItems.isEmpty()) {
                Text(
                    text = if (lang == Language.ARABIC) "لم تضاف أي إجراءات تصحيحية حتى الآن." else "No CAPA action items added yet.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            } else {
                capaItems.forEachIndexed { index, item ->
                    val statusEnum = CapaStatus.values().find { it.name == item.status } ?: CapaStatus.PENDING
                    val statusColor = Color(android.graphics.Color.parseColor(statusEnum.hexColor))

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.action,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = statusColor.copy(alpha = 0.15f),
                                    modifier = Modifier.padding(start = 6.dp)
                                ) {
                                    Text(
                                        text = statusEnum.label(lang),
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = statusColor,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = item.owner,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = item.dueDate,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddCapaDialog) {
        AddCapaDialog(
            lang = lang,
            onDismiss = { showAddCapaDialog = false },
            onConfirm = { action, owner, dueDate ->
                val newItem = CapaItem(action, owner, dueDate, CapaStatus.PENDING.name)
                capaItems.add(newItem)

                val jsonArray = JSONArray()
                capaItems.forEach {
                    val obj = JSONObject()
                    obj.put("action", it.action)
                    obj.put("owner", it.owner)
                    obj.put("dueDate", it.dueDate)
                    obj.put("status", it.status)
                    jsonArray.put(obj)
                }
                onCapaUpdated(jsonArray.toString())
                showAddCapaDialog = false
            }
        )
    }
}

@Composable
fun AddCapaDialog(
    lang: Language,
    onDismiss: () -> Unit,
    onConfirm: (action: String, owner: String, dueDate: String) -> Unit
) {
    var action by remember { mutableStateOf("") }
    var owner by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("25/11/2026") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = if (lang == Language.ARABIC) "إضافة إجراء تصحيحي / وقائي" else "Add Corrective Action",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = action,
                    onValueChange = { action = it },
                    label = { Text(if (lang == Language.ARABIC) "وصف الإجراء المطلوب" else "Action Description") },
                    modifier = Modifier.fillMaxWidth().testTag("add_capa_action_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = owner,
                    onValueChange = { owner = it },
                    label = { Text(if (lang == Language.ARABIC) "الشخص المسؤول والتنفيذي" else "Assigned Owner") },
                    modifier = Modifier.fillMaxWidth().testTag("add_capa_owner_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = { Text(if (lang == Language.ARABIC) "تاريخ الاستحقاق (Due Date)" else "Due Date") },
                    modifier = Modifier.fillMaxWidth().testTag("add_capa_date_input"),
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
                            if (action.isNotBlank()) {
                                onConfirm(action, owner.ifBlank { "مسؤول السلامة" }, dueDate)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SafetyOrangePrimary),
                        modifier = Modifier.testTag("confirm_save_capa_button")
                    ) {
                        Text(if (lang == Language.ARABIC) "حفظ الإجراء" else "Save Action")
                    }
                }
            }
        }
    }
}
