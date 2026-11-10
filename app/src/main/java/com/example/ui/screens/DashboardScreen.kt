package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entities.IncidentEntity
import com.example.data.local.entities.PermitEntity
import com.example.data.local.entities.SiteEntity
import com.example.model.IncidentSeverity
import com.example.model.Language
import com.example.model.PermitStatus
import com.example.model.UserRole
import com.example.ui.components.RoleBadge
import com.example.ui.components.SafetyBanner
import com.example.ui.components.StatCard
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.HazardRed
import com.example.ui.theme.IndustrialDarkNavy
import com.example.ui.theme.SafetyOrangePrimary
import com.example.ui.theme.SafetyOrangeDark
import com.example.ui.theme.SafetyYellow
import com.example.ui.util.HseStrings
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    lang: Language,
    role: UserRole,
    sites: List<SiteEntity>,
    selectedSiteId: Long,
    permits: List<PermitEntity>,
    incidents: List<IncidentEntity>,
    onSelectSite: (Long) -> Unit,
    onRecordIncident: (severity: String, title: String, desc: String, location: String, reportedBy: String) -> Unit,
    onAdjustCounter: (Int) -> Unit,
    onOpenQrScanner: () -> Unit,
    onCreatePermitClick: () -> Unit,
    onPermitClick: (PermitEntity) -> Unit
) {
    val currentSite = sites.find { it.id == selectedSiteId } ?: sites.firstOrNull()
    var showRecordIncidentDialog by remember { mutableStateOf(false) }
    var showAdjustCounterDialog by remember { mutableStateOf(false) }
    var showSiteDropdown by remember { mutableStateOf(false) }

    val activePermitsCount = permits.count { it.status == PermitStatus.APPROVED.name || it.status == PermitStatus.IN_PROGRESS.name }
    val pendingPermitsCount = permits.count { it.status == PermitStatus.DRAFT.name || it.status == PermitStatus.REVIEWED.name }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // 1. Site Selector Header & Role Badge
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { showSiteDropdown = true }
                                .padding(vertical = 4.dp, horizontal = 8.dp)
                                .testTag("site_selector_dropdown")
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = SafetyOrangePrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = HseStrings.currentSite(lang),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                                Text(
                                    text = currentSite?.let { if (lang == Language.ARABIC) it.nameAr else it.nameEn } ?: "اختر الموقع",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showSiteDropdown,
                            onDismissRequest = { showSiteDropdown = false }
                        ) {
                            sites.forEach { site ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = if (lang == Language.ARABIC) site.nameAr else site.nameEn,
                                            fontWeight = if (site.id == selectedSiteId) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    onClick = {
                                        onSelectSite(site.id)
                                        showSiteDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    RoleBadge(role = role, lang = lang)
                }
            }
        }

        // 2. BIG ZERO-INCIDENT COUNTER CARD
        item {
            val incidentDays = currentSite?.incidentFreeDays ?: 0
            val recordStreak = currentSite?.recordStreakDays ?: 0

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("incident_counter_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    IndustrialDarkNavy,
                                    Color(0xFF1E293B)
                                )
                            ),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .padding(24.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = EmeraldGreen.copy(alpha = 0.2f),
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = EmeraldGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = HseStrings.incidentFreeDaysTitle(lang),
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = EmeraldGreen
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "$incidentDays",
                            fontSize = 68.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )

                        Text(
                            text = if (lang == Language.ARABIC) "يـوم بـدون أي حـوادث" else "DAYS INCIDENT FREE",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White.copy(alpha = 0.85f)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "${HseStrings.recordStreak(lang)} $recordStreak ${if (lang == Language.ARABIC) "يوم" else "days"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = SafetyYellow,
                                fontWeight = FontWeight.SemiBold
                            )
                            if (role == UserRole.ADMIN || role == UserRole.SAFETY_OFFICER) {
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(
                                    onClick = { showAdjustCounterDialog = true },
                                    modifier = Modifier
                                        .size(24.dp)
                                        .testTag("edit_counter_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = null,
                                        tint = SafetyYellow,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Incident Record Button & Quick Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = { showRecordIncidentDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = HazardRed),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .weight(1.2f)
                                    .testTag("record_incident_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ReportProblem,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = HseStrings.recordIncidentButton(lang),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            OutlinedButton(
                                onClick = onOpenQrScanner,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("dashboard_qr_scanner_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.QrCodeScanner,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = HseStrings.scanQrButton(lang),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. STATS & SAFETY KPI METRICS
        item {
            val totalHours = currentSite?.totalManHours ?: 250000L
            val trirValue = String.format(Locale.US, "%.2f", (incidents.size * 200000.0) / totalHours)
            val lostTimeCount = incidents.count { it.lostTimeDays > 0 || it.severity == "SEVERE" }
            val ltifrValue = String.format(Locale.US, "%.2f", (lostTimeCount * 1000000.0) / totalHours)

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = if (lang == Language.ARABIC) "تصاريح نشطة" else "Active Permits",
                        value = "$activePermitsCount",
                        icon = Icons.Default.Assignment,
                        accentColor = EmeraldGreen,
                        modifier = Modifier.weight(1f)
                    )

                    StatCard(
                        title = if (lang == Language.ARABIC) "بانتظار الاعتماد" else "Pending Review",
                        value = "$pendingPermitsCount",
                        icon = Icons.Default.Warning,
                        accentColor = SafetyYellow,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "معدل الحوادث TRIR",
                        value = trirValue,
                        subtitle = if (lang == Language.ARABIC) "لكل 200k ساعة عمل" else "per 200k man-hours",
                        icon = Icons.Default.ReportProblem,
                        accentColor = SafetyOrangePrimary,
                        modifier = Modifier.weight(1f)
                    )

                    StatCard(
                        title = "معدل الإصابات LTIFR",
                        value = ltifrValue,
                        subtitle = if (lang == Language.ARABIC) "لكل 1M ساعة عمل" else "per 1M man-hours",
                        icon = Icons.Default.Shield,
                        accentColor = HazardRed,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 4. ACTIVE PERMITS PREVIEW HEADER & LIST
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = HseStrings.activePermitsHeader(lang),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Button(
                    onClick = onCreatePermitClick,
                    colors = ButtonDefaults.buttonColors(containerColor = SafetyOrangePrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("dashboard_create_permit_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = HseStrings.createPermitFab(lang), fontSize = 12.sp)
                }
            }
        }

        if (permits.isEmpty()) {
            item {
                SafetyBanner(
                    title = if (lang == Language.ARABIC) "لا توجد تصاريح عمل" else "No Work Permits",
                    message = if (lang == Language.ARABIC) "انقر على 'تصريح جديد' لإصدار تصريح عمل لهذا الموقع." else "Tap 'New Permit' to issue a work permit for this site.",
                    lang = lang
                )
            }
        } else {
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(permits) { permit ->
                        PermitCardCompact(permit = permit, lang = lang, onClick = { onPermitClick(permit) })
                    }
                }
            }
        }

        // 5. INCIDENT HISTORY LOG
        item {
            Text(
                text = HseStrings.recentIncidentsHeader(lang),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (incidents.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldGreen)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (lang == Language.ARABIC) "سجل نظيف! لم تسجل أي حوادث في هذا الموقع." else "Clean record! No incidents reported on this site.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        } else {
            items(incidents) { incident ->
                IncidentItemCard(incident = incident, lang = lang)
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }

    // DIALOG 1: RECORD INCIDENT & RESET COUNTER
    if (showRecordIncidentDialog) {
        RecordIncidentDialog(
            lang = lang,
            onDismiss = { showRecordIncidentDialog = false },
            onConfirm = { severity, title, desc, location, reportedBy ->
                onRecordIncident(severity, title, desc, location, reportedBy)
                showRecordIncidentDialog = false
            }
        )
    }

    // DIALOG 2: ADJUST COUNTER DAYS
    if (showAdjustCounterDialog) {
        AdjustCounterDialog(
            currentDays = currentSite?.incidentFreeDays ?: 0,
            lang = lang,
            onDismiss = { showAdjustCounterDialog = false },
            onConfirm = { newDays ->
                onAdjustCounter(newDays)
                showAdjustCounterDialog = false
            }
        )
    }
}

@Composable
fun PermitCardCompact(
    permit: PermitEntity,
    lang: Language,
    onClick: () -> Unit
) {
    val statusColor = when (permit.status) {
        PermitStatus.APPROVED.name, PermitStatus.IN_PROGRESS.name -> EmeraldGreen
        PermitStatus.DRAFT.name, PermitStatus.REVIEWED.name -> SafetyYellow
        else -> HazardRed
    }

    Card(
        modifier = Modifier
            .width(260.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("permit_card_${permit.permitNumber}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SafetyOrangePrimary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = permit.permitNumber,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = SafetyOrangeDark,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = permit.status,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = permit.title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = permit.locationDetail,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                maxLines = 1
            )
        }
    }
}

@Composable
fun IncidentItemCard(
    incident: IncidentEntity,
    lang: Language
) {
    val sdf = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
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
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = incident.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = HazardRed.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = incident.severity,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = HazardRed,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = incident.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${HseStrings.reportedByLabel(lang)}: ${incident.reportedBy}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = sdf.format(Date(incident.incidentDate)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun RecordIncidentDialog(
    lang: Language,
    onDismiss: () -> Unit,
    onConfirm: (severity: String, title: String, desc: String, location: String, reportedBy: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var reportedBy by remember { mutableStateOf("") }
    var selectedSeverity by remember { mutableStateOf(IncidentSeverity.MINOR.name) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = HazardRed)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = HseStrings.recordIncidentDialogTitle(lang),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = HazardRed
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(HseStrings.incidentTitleLabel(lang)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("incident_title_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(HseStrings.incidentDescLabel(lang)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("incident_desc_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text(HseStrings.incidentLocationLabel(lang)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = reportedBy,
                    onValueChange = { reportedBy = it },
                    label = { Text(HseStrings.reportedByLabel(lang)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text(HseStrings.cancelButton(lang))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onConfirm(selectedSeverity, title, description, location, reportedBy)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = HazardRed),
                        modifier = Modifier.testTag("confirm_incident_reset_button")
                    ) {
                        Text(HseStrings.confirmResetButton(lang))
                    }
                }
            }
        }
    }
}

@Composable
fun AdjustCounterDialog(
    currentDays: Int,
    lang: Language,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var daysText by remember { mutableStateOf("$currentDays") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = HseStrings.adjustCounterDialogTitle(lang),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = daysText,
                    onValueChange = { daysText = it },
                    label = { Text(HseStrings.daysInputLabel(lang)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("adjust_days_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text(HseStrings.cancelButton(lang))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val newDays = daysText.toIntOrNull() ?: 0
                            onConfirm(newDays)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SafetyOrangePrimary),
                        modifier = Modifier.testTag("save_adjusted_counter_button")
                    ) {
                        Text(HseStrings.saveButton(lang))
                    }
                }
            }
        }
    }
}
