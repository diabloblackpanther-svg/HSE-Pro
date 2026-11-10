package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.data.local.entities.IncidentEntity
import com.example.model.Language
import com.example.model.UserRole
import com.example.ui.components.CapaTrackerView
import com.example.ui.components.FishboneDiagramView
import com.example.ui.components.FiveWhysComposer
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.HazardRed
import com.example.ui.theme.SafetyOrangePrimary
import com.example.ui.util.HseStrings
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun IncidentsScreen(
    lang: Language,
    role: UserRole,
    incidents: List<IncidentEntity>,
    onRecordNewIncidentClick: () -> Unit,
    onUpdateAnalysis: (incidentId: Long, fiveWhys: String, fishbone: String, capa: String, photoUri: String) -> Unit
) {
    val sdf = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

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
                    text = if (lang == Language.ARABIC) "سجل الحوادث والـ Near Miss" else "Incidents & Near-Miss Log",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = if (lang == Language.ARABIC) "تحليل الأسباب الجذرية (5-Why / Fishbone) والـ CAPA" else "Root Cause Analysis (5 Whys / Fishbone) & CAPA",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Button(
                onClick = onRecordNewIncidentClick,
                colors = ButtonDefaults.buttonColors(containerColor = HazardRed),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("report_incident_page_button")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = if (lang == Language.ARABIC) "بلاغ حادث" else "Report", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (incidents.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.ReportProblem, contentDescription = null, tint = EmeraldGreen)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (lang == Language.ARABIC) "لا توجد بلاغات حوادث مسجلة في هذا الموقع." else "No incidents reported on this site.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(incidents) { incident ->
                    IncidentDetailCardItem(
                        incident = incident,
                        lang = lang,
                        onUpdateAnalysis = { fiveWhys, fishbone, capa, photo ->
                            onUpdateAnalysis(incident.id, fiveWhys, fishbone, capa, photo)
                        }
                    )
                }
                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }
    }
}

@Composable
fun IncidentDetailCardItem(
    incident: IncidentEntity,
    lang: Language,
    onUpdateAnalysis: (fiveWhys: String, fishbone: String, capa: String, photoUri: String) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    var capturedPhoto by remember { mutableStateOf(incident.photoUri.ifBlank { "simulated_camera_photo_attachment.jpg" }) }
    var hasPhoto by remember { mutableStateOf(incident.photoUri.isNotBlank()) }

    var whySteps by remember {
        mutableStateOf(
            listOf(
                "لماذا وقع الحادث؟ تسرب هيدروليكي.",
                "لماذا تسرب الهيدروليك؟ انقطاع الخرطوم.",
                "لماذا انقطع الخرطوم؟ احتكاك بالهيكل.",
                "لماذا احتك بالهيكل؟ غياب غلاف الحماية.",
                "السبب الجذر: عدم شمول فحص الخراطيم بجدول الصيانة الدوري."
            )
        )
    }

    var fishboneJson by remember { mutableStateOf(incident.fishboneJson) }
    var capaJson by remember { mutableStateOf(incident.capaJson) }

    val sdf = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .testTag("incident_detail_card_${incident.id}"),
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
                    Icon(
                        imageVector = Icons.Default.ReportProblem,
                        contentDescription = null,
                        tint = HazardRed,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = incident.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${incident.location} • ${sdf.format(Date(incident.incidentDate))}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = HazardRed.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = incident.severity,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = HazardRed,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = incident.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Expand / Collapse Deep Analysis Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { isExpanded = !isExpanded }
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Psychology, contentDescription = null, tint = SafetyOrangePrimary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (lang == Language.ARABIC) "تحليل الأسباب الجذرية وإجراءات CAPA" else "Root Cause Analysis & CAPA Plan",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = SafetyOrangePrimary
                    )
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = SafetyOrangePrimary
                )
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(12.dp))

                // Camera Photo Attachment Simulator
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, tint = SafetyOrangePrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (hasPhoto) (if (lang == Language.ARABIC) "تم إرفاق صورة الحادث الميدانية ✓" else "Field photo attached ✓")
                                else (if (lang == Language.ARABIC) "إلتقاط / إرفاق صورة الكاميرا" else "Capture Camera Photo"),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Button(
                            onClick = {
                                hasPhoto = true
                                capturedPhoto = "incident_site_photo_${incident.id}.jpg"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SafetyOrangePrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("capture_photo_button_${incident.id}")
                        ) {
                            Icon(imageVector = Icons.Default.AddAPhoto, contentDescription = null, modifier = Modifier.size(14.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 5 Whys Analysis
                FiveWhysComposer(
                    whySteps = whySteps,
                    lang = lang,
                    onWhyStepChanged = { idx, text ->
                        val updated = whySteps.toMutableList()
                        if (idx < updated.size) updated[idx] = text
                        whySteps = updated
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Fishbone Diagram
                FishboneDiagramView(
                    fishboneJson = fishboneJson,
                    lang = lang,
                    onFishboneCategoryChanged = { cat, txt ->
                        val json = try { JSONObject(fishboneJson) } catch (e: Exception) { JSONObject() }
                        json.put(cat, txt)
                        fishboneJson = json.toString()
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // CAPA Action Items Manager
                CapaTrackerView(
                    capaJson = capaJson,
                    lang = lang,
                    onCapaUpdated = { newCapa ->
                        capaJson = newCapa
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        val whysJson = "[\"${whySteps.joinToString("\",\"")}\"]"
                        onUpdateAnalysis(whysJson, fishboneJson, capaJson, capturedPhoto)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("save_incident_analysis_button_${incident.id}")
                ) {
                    Text(text = if (lang == Language.ARABIC) "حفظ التحليل الميداني والإجراءات" else "Save Analysis & CAPA Plan")
                }
            }
        }
    }
}
