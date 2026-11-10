package com.example.ui.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Language
import com.example.model.PermitType
import com.example.ui.theme.SafetyOrangePrimary
import com.example.ui.util.HseStrings
import kotlin.random.Random

@Composable
fun CreatePermitScreen(
    lang: Language,
    onBack: () -> Unit,
    onCreatePermit: (
        permitNumber: String,
        type: String,
        title: String,
        locationDetail: String,
        applicantName: String,
        supervisorName: String,
        hseOfficerName: String,
        validFrom: Long,
        validUntil: Long,
        checklistItems: List<String>,
        notes: String
    ) -> Unit
) {
    var selectedType by remember { mutableStateOf(PermitType.HOT_WORK) }
    var showTypeDropdown by remember { mutableStateOf(false) }

    val randomNum = remember { Random.nextInt(8800, 9999) }
    val generatedPermitNumber = "PTW-2026-$randomNum"

    var title by remember { mutableStateOf("") }
    var locationDetail by remember { mutableStateOf("") }
    var applicantName by remember { mutableStateOf("") }
    var supervisorName by remember { mutableStateOf("") }
    var hseOfficerName by remember { mutableStateOf("") }
    var validityHours by remember { mutableStateOf(8) }
    var notes by remember { mutableStateOf("") }

    val defaultChecklists = remember(selectedType) {
        when (selectedType) {
            PermitType.HOT_WORK -> listOf(
                "فحص خلو المنطقة من المواد القابلة للاشتعال (قطر 11 متر)",
                "توفير مراقب حريق وطفاية حريق جاهزة للعمل",
                "ارتداء معدات الوقاية الشخصية الخاصة باللحام والقص",
                "تغطية البالوعات والفتحات الأرضية بأغطية مقاومة للحريق"
            )
            PermitType.HEIGHTS -> listOf(
                "فحص وتجريب حزام الأمان الكامل (Full Body Harness)",
                "التأكد من الكارت الأخضر للسقالة قبل الصعود",
                "تحديد نقاط ربط معتمدة فوق رأس العامل",
                "توفير حواجز حماية وخطوط تثبيت الأدوات"
            )
            PermitType.CONFINED_SPACE -> listOf(
                "قياس نسبة الأكسجين والغازات السامة بداخل الفراغ",
                "توفير تهوية ميكانيكية مستمرة وشخص مراقب خارجي",
                "تجهيز خطوط الإنقاذ وأجهزة التنفس الذاتي",
                "عزل جميع التوصيلات الميكانيكية والهيدروليكية"
            )
            PermitType.ELECTRICAL_LOTO -> listOf(
                "عزل المصدر الرئيسي للكهرباء وقفل المفتاح (LOTO)",
                "وضع بطاقة التنبيه باسم المهندس المسئول",
                "فحص عدم وجود فولتية باستخدام الفولتميتر المعاير",
                "تفدية الشحنات المتبقية وتأريض الخطوط"
            )
            else -> listOf(
                "فحص منطقة العمل والتأكد من خلوها من المخاطر",
                "التأكد من ارتداء معدات الوقاية الشخصية PPE",
                "إبلاغ جميع العاملين بمخاطر المهمة وخطة الطوارئ"
            )
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("create_permit_back_button")) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = HseStrings.createPermitTitle(lang),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        // Permit Type Selection
        item {
            Column {
                Text(
                    text = HseStrings.permitTypeLabel(lang),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showTypeDropdown = true }
                            .padding(14.dp)
                            .testTag("select_permit_type_dropdown")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = selectedType.title(lang),
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = SafetyOrangePrimary
                            )
                            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    }

                    DropdownMenu(
                        expanded = showTypeDropdown,
                        onDismissRequest = { showTypeDropdown = false }
                    ) {
                        PermitType.values().forEach { pType ->
                            DropdownMenuItem(
                                text = { Text(pType.title(lang)) },
                                onClick = {
                                    selectedType = pType
                                    showTypeDropdown = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // Form Fields
        item {
            OutlinedTextField(
                value = generatedPermitNumber,
                onValueChange = {},
                readOnly = true,
                label = { Text(HseStrings.permitNumberLabel(lang)) },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(HseStrings.permitTitleLabel(lang)) },
                placeholder = { Text(if (lang == Language.ARABIC) "مثال: لحام وقص قواعد السير 4..." else "e.g. Welding conveyor belt 4...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("new_permit_title_input"),
                singleLine = true
            )
        }

        item {
            OutlinedTextField(
                value = locationDetail,
                onValueChange = { locationDetail = it },
                label = { Text(HseStrings.incidentLocationLabel(lang)) },
                placeholder = { Text(if (lang == Language.ARABIC) "المستودع الرئيسي - رصيف 3" else "Main Warehouse - Dock 3") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("new_permit_location_input"),
                singleLine = true
            )
        }

        item {
            OutlinedTextField(
                value = applicantName,
                onValueChange = { applicantName = it },
                label = { Text(HseStrings.applicantNameLabel(lang)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("new_permit_applicant_input"),
                singleLine = true
            )
        }

        item {
            OutlinedTextField(
                value = supervisorName,
                onValueChange = { supervisorName = it },
                label = { Text(HseStrings.supervisorLabel(lang)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("new_permit_supervisor_input"),
                singleLine = true
            )
        }

        item {
            OutlinedTextField(
                value = hseOfficerName,
                onValueChange = { hseOfficerName = it },
                label = { Text(HseStrings.hseOfficerLabel(lang)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("new_permit_hse_input"),
                singleLine = true
            )
        }

        item {
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text(if (lang == Language.ARABIC) "ملاحظات إضافية وتدابير الحماية" else "Additional Notes & Controls") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Safety Checklist Preview
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = HseStrings.safetyChecklistHeader(lang),
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = SafetyOrangePrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    defaultChecklists.forEach { chk ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = SafetyOrangePrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = chk, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }

        // Submit Button
        item {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val now = System.currentTimeMillis()
                        val validUntil = now + (validityHours * 3600000L)
                        onCreatePermit(
                            generatedPermitNumber,
                            selectedType.name,
                            title,
                            locationDetail.ifBlank { "المنطقة الرئيسية" },
                            applicantName.ifBlank { "طالب العمل" },
                            supervisorName.ifBlank { "مشرف الموقع" },
                            hseOfficerName.ifBlank { "مسؤول السلامة" },
                            now,
                            validUntil,
                            defaultChecklists,
                            notes
                        )
                        onBack()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SafetyOrangePrimary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("submit_create_permit_button")
            ) {
                Text(
                    text = HseStrings.createPermitFab(lang),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}
