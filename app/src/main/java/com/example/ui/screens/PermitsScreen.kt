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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
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
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.HazardRed
import com.example.ui.theme.SafetyOrangePrimary
import com.example.ui.theme.SafetyYellow
import com.example.ui.util.HseStrings
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PermitsScreen(
    lang: Language,
    role: UserRole,
    permits: List<PermitEntity>,
    onSelectPermit: (PermitEntity) -> Unit,
    onCreatePermitClick: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTypeFilter by remember { mutableStateOf<String?>(null) }
    var selectedStatusFilter by remember { mutableStateOf<String?>(null) }

    val filteredPermits = remember(permits, searchQuery, selectedTypeFilter, selectedStatusFilter) {
        permits.filter { permit ->
            val matchesQuery = searchQuery.isBlank() ||
                    permit.permitNumber.contains(searchQuery, ignoreCase = true) ||
                    permit.title.contains(searchQuery, ignoreCase = true) ||
                    permit.applicantName.contains(searchQuery, ignoreCase = true)

            val matchesType = selectedTypeFilter == null || permit.type == selectedTypeFilter
            val matchesStatus = selectedStatusFilter == null || permit.status == selectedStatusFilter

            matchesQuery && matchesType && matchesStatus
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text(HseStrings.searchPermitsPlaceholder(lang)) },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("permits_search_input"),
            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Filter Chips Row 1: Permit Types
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                FilterChip(
                    selected = selectedTypeFilter == null,
                    onClick = { selectedTypeFilter = null },
                    label = { Text(if (lang == Language.ARABIC) "الكل" else "All Types") }
                )
            }
            items(PermitType.values()) { pType ->
                FilterChip(
                    selected = selectedTypeFilter == pType.name,
                    onClick = {
                        selectedTypeFilter = if (selectedTypeFilter == pType.name) null else pType.name
                    },
                    label = { Text(pType.title(lang)) }
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Filter Chips Row 2: Statuses
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                FilterChip(
                    selected = selectedStatusFilter == null,
                    onClick = { selectedStatusFilter = null },
                    label = { Text(if (lang == Language.ARABIC) "جميع الحالات" else "All Statuses") }
                )
            }
            items(PermitStatus.values()) { pStatus ->
                FilterChip(
                    selected = selectedStatusFilter == pStatus.name,
                    onClick = {
                        selectedStatusFilter = if (selectedStatusFilter == pStatus.name) null else pStatus.name
                    },
                    label = { Text(pStatus.label(lang)) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Header Row & Issue Permit Action
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${HseStrings.navPermits(lang)} (${filteredPermits.size})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Button(
                onClick = onCreatePermitClick,
                colors = ButtonDefaults.buttonColors(containerColor = SafetyOrangePrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("issue_permit_button")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = HseStrings.createPermitFab(lang), fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Permits List
        if (filteredPermits.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (lang == Language.ARABIC) "لا توجد تصاريح عمل مطابقة للبحث" else "No permits matching your search",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredPermits) { permit ->
                    PermitListItemCard(permit = permit, lang = lang, onClick = { onSelectPermit(permit) })
                }
                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }
    }
}

@Composable
fun PermitListItemCard(
    permit: PermitEntity,
    lang: Language,
    onClick: () -> Unit
) {
    val permitTypeEnum = PermitType.values().find { it.name == permit.type } ?: PermitType.GENERAL
    val statusEnum = PermitStatus.values().find { it.name == permit.status } ?: PermitStatus.DRAFT

    val typeIcon = when (permitTypeEnum) {
        PermitType.HOT_WORK -> Icons.Default.LocalFireDepartment
        PermitType.HEIGHTS -> Icons.Default.Height
        PermitType.CONFINED_SPACE -> Icons.Default.MeetingRoom
        PermitType.ELECTRICAL_LOTO -> Icons.Default.FlashOn
        PermitType.EXCAVATION -> Icons.Default.Terrain
        PermitType.CRANES -> Icons.Default.Construction
        PermitType.GENERAL -> Icons.Default.Assignment
    }

    val typeColor = Color(android.graphics.Color.parseColor(permitTypeEnum.hexColor))
    val statusColor = Color(android.graphics.Color.parseColor(statusEnum.hexColor))
    val sdf = remember { SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault()) }

    val now = System.currentTimeMillis()
    val isNearExpiry = permit.validUntil in (now + 1)..(now + 3600000L * 2)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .testTag("permit_list_item_${permit.permitNumber}"),
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
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(typeColor.copy(alpha = 0.15f))
                    ) {
                        Icon(imageVector = typeIcon, contentDescription = null, tint = typeColor, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = permit.permitNumber,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = permitTypeEnum.title(lang),
                            style = MaterialTheme.typography.labelSmall,
                            color = typeColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = statusEnum.label(lang),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = permit.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = permit.locationDetail,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(10.dp))

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
                        text = permit.applicantName,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                Text(
                    text = "${HseStrings.validUntilLabel(lang)}: ${sdf.format(Date(permit.validUntil))}",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isNearExpiry) HazardRed else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontWeight = if (isNearExpiry) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}
