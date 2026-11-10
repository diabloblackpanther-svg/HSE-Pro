package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.IncidentEntity
import com.example.data.local.entities.InspectionAuditEntity
import com.example.data.local.entities.MeterReadingEntity
import com.example.data.local.entities.PermitEntity
import com.example.data.local.entities.SiteEntity
import com.example.model.Language
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.HazardRed
import com.example.ui.theme.IndustrialDarkNavy
import com.example.ui.theme.SafetyOrangePrimary
import com.example.ui.util.ReportGenerator
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class ReportScope(val labelAr: String, val labelEn: String) {
    EXECUTIVE_SUMMARY("التقرير الشهري الشامل", "Monthly Executive Summary"),
    PERMITS_LOG("تقرير تصاريح العمل", "Work Permits Log"),
    INCIDENTS_CAPA("تقرير الحوادث والـ CAPA", "Incidents & CAPA Report"),
    RISK_ASSESSMENT("تقرير مصفوفة المخاطر JSA", "Risk Assessment JSA"),
    INSPECTIONS("تقرير التفتيش الميداني", "Field Inspections Audit"),
    UTILITIES("تقرير استهلاك الكهرباء والمياه", "Utilities Report")
}

@Composable
fun ReportsScreen(
    lang: Language,
    site: SiteEntity?,
    permits: List<PermitEntity>,
    incidents: List<IncidentEntity>,
    audits: List<InspectionAuditEntity>,
    readings: List<MeterReadingEntity>
) {
    val context = LocalContext.current
    var selectedScope by remember { mutableStateOf(ReportScope.EXECUTIVE_SUMMARY) }
    var selectedPeriodName by remember { mutableStateOf("الشهر الحالي (نوفمبر 2026)") }

    val sdf = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }
    val siteName = site?.let { if (lang == Language.ARABIC) it.nameAr else it.nameEn } ?: "جميع المواقع"

    val trirValue = String.format(Locale.US, "%.2f", (incidents.size * 200000.0) / (site?.totalManHours ?: 250000L))
    val avgAuditScore = if (audits.isNotEmpty()) audits.map { it.compliancePercentage }.average().toInt() else 95

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        item {
            Text(
                text = if (lang == Language.ARABIC) "مركز التقارير والتصدير الشامل" else "Reports & Export Center",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = if (lang == Language.ARABIC) "تصدير وتوفير تقارير رسمية بتوقيع معتمد (PDF / Excel / Word)" else "Export official signed PDF, Excel, and Word reports",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }

        // Report Scope Filter Chips
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = if (lang == Language.ARABIC) "اختر نوع التقرير المطلوب:" else "Select Report Type:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = SafetyOrangePrimary
                )

                ReportScope.values().forEach { scope ->
                    FilterChip(
                        selected = selectedScope == scope,
                        onClick = { selectedScope = scope },
                        label = { Text(if (lang == Language.ARABIC) scope.labelAr else scope.labelEn, fontSize = 11.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("report_scope_chip_${scope.name}")
                    )
                }
            }
        }

        // Action Buttons: PDF, Excel, Word & Share
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (lang == Language.ARABIC) "خيارات التصدير والمشاركة:" else "Export & Sharing Actions:",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val htmlContent = ReportGenerator.generatePdfHtml(
                                    site = site,
                                    permits = permits,
                                    incidents = incidents,
                                    audits = audits,
                                    readings = readings,
                                    lang = lang,
                                    periodName = selectedScope.labelAr
                                )
                                ReportGenerator.shareReportFile(
                                    context = context,
                                    content = htmlContent,
                                    fileName = "HSE_Pro_Official_Report_${System.currentTimeMillis()}.pdf.html",
                                    mimeType = "text/html"
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SafetyOrangePrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("export_pdf_button")
                        ) {
                            Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "PDF", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                val csvData = ReportGenerator.generateCsvContent(site, permits, incidents, lang)
                                ReportGenerator.shareReportFile(
                                    context = context,
                                    content = csvData,
                                    fileName = "HSE_Pro_Report_${System.currentTimeMillis()}.csv",
                                    mimeType = "text/csv"
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("export_excel_button")
                        ) {
                            Icon(imageVector = Icons.Default.TableChart, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Excel", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                val docContent = ReportGenerator.generateCsvContent(site, permits, incidents, lang)
                                ReportGenerator.shareReportFile(
                                    context = context,
                                    content = docContent,
                                    fileName = "HSE_Pro_Report_${System.currentTimeMillis()}.doc",
                                    mimeType = "text/plain"
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("export_word_button")
                        ) {
                            Icon(imageVector = Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Word", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = {
                            val csvData = ReportGenerator.generateCsvContent(site, permits, incidents, lang)
                            ReportGenerator.shareReportFile(
                                context = context,
                                content = csvData,
                                fileName = "HSE_Summary_${System.currentTimeMillis()}.csv",
                                mimeType = "text/csv"
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("share_report_button")
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = if (lang == Language.ARABIC) "مشاركة التقرير المعتمد (Share)" else "Share Official Report")
                    }
                }
            }
        }

        // Live Report Preview Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, SafetyOrangePrimary.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Report Header / Letterhead
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = SafetyOrangePrimary, modifier = Modifier.size(28.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "شركة الخدمات اللوجستية والإنشاءات",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = SafetyOrangePrimary
                                )
                                Text(
                                    text = "إدارة السلامة والصحة المهنية والبيئة (HSE Department)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(SafetyOrangePrimary))
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = selectedScope.labelAr,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "الموقع: $siteName • التاريخ: ${sdf.format(Date())}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Summary Stats Table
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("عدد تصاريح العمل:", style = MaterialTheme.typography.bodySmall)
                                Text("${permits.size}", fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("بلاغات الحوادث:", style = MaterialTheme.typography.bodySmall)
                                Text("${incidents.size}", fontWeight = FontWeight.Bold, color = HazardRed)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("معدل TRIR القياسي:", style = MaterialTheme.typography.bodySmall)
                                Text(trirValue, fontWeight = FontWeight.Bold, color = SafetyOrangePrimary)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("نسبة المطابقة بالتفتيش:", style = MaterialTheme.typography.bodySmall)
                                Text("$avgAuditScore%", fontWeight = FontWeight.Bold, color = EmeraldGreen)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Official Signatures Block
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                            Text("اعتماد مسؤول HSE", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(24.dp))
                            Box(modifier = Modifier.width(100.dp).height(1.dp).background(MaterialTheme.colorScheme.onSurface))
                            Text("أحمد المنصور", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp)
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                            Text("اعتماد مدير الموقع", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(24.dp))
                            Box(modifier = Modifier.width(100.dp).height(1.dp).background(MaterialTheme.colorScheme.onSurface))
                            Text("م. خالد الغامدي", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp)
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}
