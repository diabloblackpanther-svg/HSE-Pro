package com.example.ui.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.data.local.entities.IncidentEntity
import com.example.data.local.entities.InspectionAuditEntity
import com.example.data.local.entities.MeterReadingEntity
import com.example.data.local.entities.PermitEntity
import com.example.data.local.entities.SiteEntity
import com.example.model.Language
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ReportGenerator {

    fun generatePdfHtml(
        site: SiteEntity?,
        permits: List<PermitEntity>,
        incidents: List<IncidentEntity>,
        audits: List<InspectionAuditEntity>,
        readings: List<MeterReadingEntity>,
        lang: Language,
        periodName: String = "التقرير الشهري الشامل"
    ): String {
        val siteName = site?.let { if (lang == Language.ARABIC) it.nameAr else it.nameEn } ?: "جميع المواقع"
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val generatedDate = sdf.format(Date())

        val totalPermits = permits.size
        val totalIncidents = incidents.size
        val avgAuditScore = if (audits.isNotEmpty()) audits.map { it.compliancePercentage }.average().toInt() else 100
        val streakDays = site?.incidentFreeDays ?: 0

        val trir = String.format(Locale.US, "%.2f", (totalIncidents * 200000.0) / (site?.totalManHours ?: 250000L))
        val ltifr = String.format(Locale.US, "%.2f", (incidents.count { it.lostTimeDays > 0 } * 1000000.0) / (site?.totalManHours ?: 250000L))

        val dirAttr = if (lang == Language.ARABIC) "rtl" else "ltr"

        val htmlBuilder = StringBuilder()
        htmlBuilder.append("""
            <!DOCTYPE html>
            <html lang="${lang.code}" dir="$dirAttr">
            <head>
                <meta charset="UTF-8">
                <title>HSE Pro Report</title>
                <style>
                    body {
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                        margin: 25px;
                        color: #0F172A;
                        background-color: #FFFFFF;
                    }
                    .header-container {
                        display: flex;
                        justify-content: space-between;
                        align-items: center;
                        border-bottom: 3px solid #F97316;
                        padding-bottom: 12px;
                        margin-bottom: 20px;
                    }
                    .logo-title {
                        font-size: 24px;
                        font-weight: bold;
                        color: #F97316;
                    }
                    .subtitle {
                        font-size: 13px;
                        color: #64748B;
                    }
                    .kpi-grid {
                        display: flex;
                        gap: 12px;
                        margin-bottom: 24px;
                    }
                    .kpi-card {
                        flex: 1;
                        background: #F8FAFC;
                        border: 1px solid #E2E8F0;
                        border-radius: 10px;
                        padding: 12px;
                        text-align: center;
                    }
                    .kpi-value {
                        font-size: 22px;
                        font-weight: bold;
                        color: #0F172A;
                    }
                    .kpi-label {
                        font-size: 11px;
                        color: #64748B;
                    }
                    table {
                        width: 100%;
                        border-collapse: collapse;
                        margin-bottom: 24px;
                        font-size: 12px;
                    }
                    th, td {
                        border: 1px solid #CBD5E1;
                        padding: 8px 10px;
                        text-align: ${if (lang == Language.ARABIC) "right" else "left"};
                    }
                    th {
                        background-color: #0F172A;
                        color: #FFFFFF;
                    }
                    tr:nth-child(even) {
                        background-color: #F8FAFC;
                    }
                    .signatures-container {
                        margin-top: 40px;
                        display: flex;
                        justify-content: space-between;
                        padding-top: 20px;
                        border-top: 1px dashed #94A3B8;
                    }
                    .sig-block {
                        text-align: center;
                        width: 45%;
                    }
                    .sig-line {
                        margin-top: 45px;
                        border-top: 1px solid #0F172A;
                    }
                </style>
            </head>
            <body>
                <div class="header-container">
                    <div>
                        <div class="logo-title">🛡️ HSE Pro - ${if (lang == Language.ARABIC) "تقرير السلامة والصحة المهنية" else "HSE Summary Report"}</div>
                        <div class="subtitle">$periodName | $siteName</div>
                    </div>
                    <div style="text-align: left; font-size: 11px; color: #64748B;">
                        <div>${if (lang == Language.ARABIC) "تاريخ الإصدار:" else "Generated Date:"} $generatedDate</div>
                        <div>${if (lang == Language.ARABIC) "النظام:" else "System:"} HSE Pro Enterprise v2.4</div>
                    </div>
                </div>

                <div class="kpi-grid">
                    <div class="kpi-card">
                        <div class="kpi-value" style="color: #10B981;">$streakDays</div>
                        <div class="kpi-label">${if (lang == Language.ARABIC) "أيام بدون حوادث" else "Incident-Free Days"}</div>
                    </div>
                    <div class="kpi-card">
                        <div class="kpi-value">$totalPermits</div>
                        <div class="kpi-label">${if (lang == Language.ARABIC) "إجمالي تصاريح العمل" else "Total Permits"}</div>
                    </div>
                    <div class="kpi-card">
                        <div class="kpi-value" style="color: #EF4444;">$totalIncidents</div>
                        <div class="kpi-label">${if (lang == Language.ARABIC) "الحوادث المسجلة" else "Incidents Logged"}</div>
                    </div>
                    <div class="kpi-card">
                        <div class="kpi-value" style="color: #F97316;">$trir</div>
                        <div class="kpi-label">TRIR (200k hrs)</div>
                    </div>
                    <div class="kpi-card">
                        <div class="kpi-value" style="color: #10B981;">$avgAuditScore%</div>
                        <div class="kpi-label">${if (lang == Language.ARABIC) "نسبة المطابقة للتفتيش" else "Audit Compliance %"}</div>
                    </div>
                </div>

                <h3>${if (lang == Language.ARABIC) "1. تصاريح العمل النشطة والمؤرشفة" else "1. Work Permits Summary"}</h3>
                <table>
                    <thead>
                        <tr>
                            <th>${if (lang == Language.ARABIC) "رقم التصريح" else "Permit #"}</th>
                            <th>${if (lang == Language.ARABIC) "نوع التصريح" else "Type"}</th>
                            <th>${if (lang == Language.ARABIC) "العنوان / المكان" else "Title / Location"}</th>
                            <th>${if (lang == Language.ARABIC) "المشرف المسؤول" else "Supervisor"}</th>
                            <th>${if (lang == Language.ARABIC) "الحالة" else "Status"}</th>
                        </tr>
                    </thead>
                    <tbody>
        """.trimIndent())

        permits.forEach { permit ->
            htmlBuilder.append("""
                <tr>
                    <td><b>${permit.permitNumber}</b></td>
                    <td>${permit.type}</td>
                    <td>${permit.title} (${permit.locationDetail})</td>
                    <td>${permit.supervisorName}</td>
                    <td>${permit.status}</td>
                </tr>
            """.trimIndent())
        }

        htmlBuilder.append("""
                    </tbody>
                </table>

                <h3>${if (lang == Language.ARABIC) "2. سجل الحوادث والإجراءات التصحيحية CAPA" else "2. Incidents & CAPA Log"}</h3>
                <table>
                    <thead>
                        <tr>
                            <th>${if (lang == Language.ARABIC) "عنوان البلاغ" else "Incident Title"}</th>
                            <th>${if (lang == Language.ARABIC) "المكان" else "Location"}</th>
                            <th>${if (lang == Language.ARABIC) "الخطورة" else "Severity"}</th>
                            <th>${if (lang == Language.ARABIC) "المُبلغ" else "Reported By"}</th>
                        </tr>
                    </thead>
                    <tbody>
        """.trimIndent())

        incidents.forEach { incident ->
            htmlBuilder.append("""
                <tr>
                    <td><b>${incident.title}</b></td>
                    <td>${incident.location}</td>
                    <td>${incident.severity}</td>
                    <td>${incident.reportedBy}</td>
                </tr>
            """.trimIndent())
        }

        htmlBuilder.append("""
                    </tbody>
                </table>

                <div class="signatures-container">
                    <div class="sig-block">
                        <div><b>${if (lang == Language.ARABIC) "توقيع مسؤول السلامة والصحة المهنية (HSE)" else "HSE Officer Signature"}</b></div>
                        <div class="sig-line"></div>
                        <div style="font-size: 11px; margin-top: 4px; color: #64748B;">أحمد المنصور - مدير HSE</div>
                    </div>
                    <div class="sig-block">
                        <div><b>${if (lang == Language.ARABIC) "توقيع واعتماد مدير الموقع" else "Site Manager Approval"}</b></div>
                        <div class="sig-line"></div>
                        <div style="font-size: 11px; margin-top: 4px; color: #64748B;">م. خالد الغامدي - مدير الموقع</div>
                    </div>
                </div>
            </body>
            </html>
        """.trimIndent())

        return htmlBuilder.toString()
    }

    fun generateCsvContent(
        site: SiteEntity?,
        permits: List<PermitEntity>,
        incidents: List<IncidentEntity>,
        lang: Language
    ): String {
        val sb = StringBuilder()
        sb.append("Record Type,ID/Number,Title/Description,Location,Supervisor/Reporter,Status/Severity,Date\n")

        permits.forEach { p ->
            val cleanTitle = p.title.replace(",", " ")
            sb.append("WorkPermit,${p.permitNumber},\"$cleanTitle\",${p.locationDetail},${p.supervisorName},${p.status},${p.createdAt}\n")
        }

        incidents.forEach { inc ->
            val cleanTitle = inc.title.replace(",", " ")
            sb.append("Incident,INC-${inc.id},\"$cleanTitle\",${inc.location},${inc.reportedBy},${inc.severity},${inc.incidentDate}\n")
        }

        return sb.toString()
    }

    fun shareReportFile(
        context: Context,
        content: String,
        fileName: String,
        mimeType: String
    ) {
        try {
            val file = File(context.cacheDir, fileName)
            file.writeText(content, Charsets.UTF_8)

            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "HSE Pro Official Report Export")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(intent, "تصدير ومشاركة التقرير عبر")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
