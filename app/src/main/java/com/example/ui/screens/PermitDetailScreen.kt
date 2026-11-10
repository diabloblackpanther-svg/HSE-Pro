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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import com.example.data.local.entities.PermitEntity
import com.example.model.Language
import com.example.model.PermitStatus
import com.example.model.PermitType
import com.example.model.UserRole
import com.example.ui.components.QrCodeGenerator
import com.example.ui.components.SafetyBanner
import com.example.ui.components.SignatureCanvas
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.HazardRed
import com.example.ui.theme.SafetyOrangePrimary
import com.example.ui.theme.SafetyYellow
import com.example.ui.util.HseStrings
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ChecklistItem(val itemAr: String, val itemEn: String, var checked: Boolean)

@Composable
fun PermitDetailScreen(
    permit: PermitEntity,
    lang: Language,
    role: UserRole,
    onBack: () -> Unit,
    onUpdateStatus: (PermitStatus) -> Unit,
    onSaveSignature: (String) -> Unit
) {
    val permitTypeEnum = PermitType.values().find { it.name == permit.type } ?: PermitType.GENERAL
    val currentStatusEnum = PermitStatus.values().find { it.name == permit.status } ?: PermitStatus.DRAFT
    val sdf = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

    // Parse checklist JSON
    val checklistItems = remember(permit.checklistJson) {
        val list = mutableStateListOf<ChecklistItem>()
        try {
            val jsonArray = JSONArray(permit.checklistJson)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    ChecklistItem(
                        itemAr = obj.optString("itemAr", ""),
                        itemEn = obj.optString("itemEn", ""),
                        checked = obj.optBoolean("checked", false)
                    )
                )
            }
        } catch (e: Exception) {
            list.add(ChecklistItem("فحص الأمان والسلامة العامة", "General safety audit", true))
        }
        list
    }

    val stages = listOf(
        PermitStatus.DRAFT,
        PermitStatus.REVIEWED,
        PermitStatus.APPROVED,
        PermitStatus.IN_PROGRESS,
        PermitStatus.CLOSED
    )

    val currentStageIndex = stages.indexOf(currentStatusEnum).coerceAtLeast(0)

    val now = System.currentTimeMillis()
    val isExpired = permit.validUntil < now
    val hoursLeft = ((permit.validUntil - now) / 3600000L).coerceAtLeast(0)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // 1. Top Bar Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("permit_detail_back_button")) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = permit.permitNumber,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = permitTypeEnum.title(lang),
                        style = MaterialTheme.typography.labelMedium,
                        color = SafetyOrangePrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(android.graphics.Color.parseColor(currentStatusEnum.hexColor)).copy(alpha = 0.18f)
                ) {
                    Text(
                        text = currentStatusEnum.label(lang),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(android.graphics.Color.parseColor(currentStatusEnum.hexColor)),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // 2. Lifecycle Progress Stepper
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (lang == Language.ARABIC) "مراحل القبول والاعتماد (Workflow)" else "Approval Lifecycle",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        stages.forEachIndexed { index, stage ->
                            val isCompleted = index <= currentStageIndex
                            val isCurrent = index == currentStageIndex
                            val circleColor = if (isCompleted) EmeraldGreen else MaterialTheme.colorScheme.surfaceVariant

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(circleColor)
                                ) {
                                    if (isCompleted) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    } else {
                                        Text(
                                            text = "${index + 1}",
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = stage.label(lang).take(6),
                                    fontSize = 10.sp,
                                    color = if (isCurrent) EmeraldGreen else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                                )
                            }

                            if (index < stages.size - 1) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(3.dp)
                                        .background(if (index < currentStageIndex) EmeraldGreen else MaterialTheme.colorScheme.surfaceVariant)
                                        .padding(horizontal = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Expiry alert banner
        if (isExpired) {
            item {
                SafetyBanner(
                    title = HseStrings.expiredNotice(lang),
                    message = if (lang == Language.ARABIC) "انتهت فترة تصريح العمل! يجب تجديد أو إغلاق التصريح فوراً." else "Work permit validity expired! Renew or close permit immediately.",
                    isWarning = true,
                    lang = lang
                )
            }
        } else if (hoursLeft <= 3) {
            item {
                SafetyBanner(
                    title = if (lang == Language.ARABIC) "تنبيه اقتراب الانتهاء" else "Expiry Warning",
                    message = HseStrings.hoursLeft(hoursLeft, lang),
                    isWarning = true,
                    lang = lang
                )
            }
        }

        // 3. Permit Information Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = permit.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${HseStrings.incidentLocationLabel(lang)}: ${permit.locationDetail}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text(text = HseStrings.applicantNameLabel(lang), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            Text(text = permit.applicantName, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                        }
                        Column {
                            Text(text = HseStrings.supervisorLabel(lang), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            Text(text = permit.supervisorName, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text(text = HseStrings.validFromLabel(lang), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            Text(text = sdf.format(Date(permit.validFrom)), style = MaterialTheme.typography.bodySmall)
                        }
                        Column {
                            Text(text = HseStrings.validUntilLabel(lang), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            Text(text = sdf.format(Date(permit.validUntil)), style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    if (permit.notes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "ملاحظات إضافية: ${permit.notes}",
                            style = MaterialTheme.typography.bodySmall,
                            color = SafetyYellow
                        )
                    }
                }
            }
        }

        // 4. Interactive Safety Checklist
        item {
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
                        Text(
                            text = HseStrings.safetyChecklistHeader(lang),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        val checkedCount = checklistItems.count { it.checked }
                        Text(
                            text = "$checkedCount / ${checklistItems.size}",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = EmeraldGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    checklistItems.forEachIndexed { index, item ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Checkbox(
                                checked = item.checked,
                                onCheckedChange = { isChecked ->
                                    checklistItems[index] = item.copy(checked = isChecked)
                                },
                                colors = CheckboxDefaults.colors(checkedColor = EmeraldGreen),
                                modifier = Modifier.testTag("checklist_checkbox_$index")
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (lang == Language.ARABIC) item.itemAr else item.itemEn,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // 5. Finger Signature Canvas Section
        item {
            SignatureCanvas(
                initialPathData = permit.signaturePathData,
                lang = lang,
                onSaveSignature = { sigPath ->
                    onSaveSignature(sigPath)
                }
            )
        }

        // 6. QR Code Section for Site Verification
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    QrCodeGenerator(
                        contentData = "${permit.permitNumber}|${permit.siteId}|${permit.status}|${permit.validUntil}",
                        permitNumber = permit.permitNumber,
                        lang = lang
                    )
                }
            }
        }

        // 7. Workflow Action Buttons (Role Gated)
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                when (currentStatusEnum) {
                    PermitStatus.DRAFT -> {
                        Button(
                            onClick = { onUpdateStatus(PermitStatus.REVIEWED) },
                            colors = ButtonDefaults.buttonColors(containerColor = SafetyOrangePrimary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("action_submit_review")
                        ) {
                            Icon(imageVector = Icons.Default.ThumbUp, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(HseStrings.reviewAction(lang))
                        }
                    }

                    PermitStatus.REVIEWED -> {
                        if (role == UserRole.ADMIN || role == UserRole.SAFETY_OFFICER) {
                            Button(
                                onClick = { onUpdateStatus(PermitStatus.APPROVED) },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("action_approve")
                            ) {
                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(HseStrings.approveAction(lang))
                            }
                        } else {
                            Text(
                                text = if (lang == Language.ARABIC) "بانتظار اعتماد مسؤول السلامة أو مدير الموقع" else "Awaiting HSE Officer or Admin approval",
                                style = MaterialTheme.typography.bodySmall,
                                color = SafetyYellow
                            )
                        }
                    }

                    PermitStatus.APPROVED -> {
                        Button(
                            onClick = { onUpdateStatus(PermitStatus.IN_PROGRESS) },
                            colors = ButtonDefaults.buttonColors(containerColor = SafetyOrangePrimary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("action_start")
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(HseStrings.startAction(lang))
                        }
                    }

                    PermitStatus.IN_PROGRESS -> {
                        Button(
                            onClick = { onUpdateStatus(PermitStatus.CLOSED) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("action_close")
                        ) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(HseStrings.closeAction(lang))
                        }
                    }

                    PermitStatus.CLOSED, PermitStatus.EXPIRED -> {
                        Text(
                            text = if (lang == Language.ARABIC) "هذا التصريح مغلق ومؤرشف بالكامل." else "This permit is closed and archived.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}
