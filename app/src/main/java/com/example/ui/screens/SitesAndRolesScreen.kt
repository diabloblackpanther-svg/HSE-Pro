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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entities.SiteEntity
import com.example.model.Language
import com.example.model.UserRole
import com.example.ui.components.RoleBadge
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.SafetyOrangePrimary
import com.example.ui.util.HseStrings

@Composable
fun SitesAndRolesScreen(
    lang: Language,
    currentRole: UserRole,
    sites: List<SiteEntity>,
    selectedSiteId: Long,
    onSelectSite: (Long) -> Unit,
    onSelectRole: (UserRole) -> Unit,
    onAddNewSite: (nameAr: String, nameEn: String, code: String, location: String) -> Unit
) {
    var showAddSiteDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        item {
            Text(
                text = HseStrings.navSites(lang),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = if (lang == Language.ARABIC) "إدارة مواقع العمل المتعددة وتغيير دور المستخدم" else "Manage sites & active user permissions",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }

        // 1. SELECT USER ROLE
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = if (lang == Language.ARABIC) "تحديد دور المستخدم الحالي:" else "Select Active User Role:",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = SafetyOrangePrimary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    UserRole.values().forEach { role ->
                        val isSelected = role == currentRole
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) SafetyOrangePrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { onSelectRole(role) }
                                .testTag("select_role_${role.name}")
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RoleBadge(role = role, lang = lang)

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = SafetyOrangePrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. SITES LIST & ADD NEW SITE
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (lang == Language.ARABIC) "مواقع العمل المسجلة" else "Registered Work Sites",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Button(
                    onClick = { showAddSiteDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = SafetyOrangePrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("add_site_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = if (lang == Language.ARABIC) "موقع جديد" else "Add Site", fontSize = 12.sp)
                }
            }
        }

        items(sites) { site ->
            val isSelected = site.id == selectedSiteId

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onSelectSite(site.id) }
                    .testTag("site_card_${site.code}"),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 0.dp)
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = if (isSelected) SafetyOrangePrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.size(32.dp)
                    )

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (lang == Language.ARABIC) site.nameAr else site.nameEn,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Text(
                            text = site.location,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "${site.incidentFreeDays} ${if (lang == Language.ARABIC) "أيام بدون حوادث" else "days incident free"}",
                            style = MaterialTheme.typography.labelSmall,
                            color = EmeraldGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (isSelected) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldGreen)
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }

    if (showAddSiteDialog) {
        AddSiteDialog(
            lang = lang,
            onDismiss = { showAddSiteDialog = false },
            onConfirm = { nameAr, nameEn, code, loc ->
                onAddNewSite(nameAr, nameEn, code, loc)
                showAddSiteDialog = false
            }
        )
    }
}

@Composable
fun AddSiteDialog(
    lang: Language,
    onDismiss: () -> Unit,
    onConfirm: (nameAr: String, nameEn: String, code: String, location: String) -> Unit
) {
    var nameAr by remember { mutableStateOf("") }
    var nameEn by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = if (lang == Language.ARABIC) "إضافة موقع عمل جديد" else "Add New Site",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = nameAr,
                    onValueChange = { nameAr = it },
                    label = { Text(if (lang == Language.ARABIC) "اسم الموقع بالعربية" else "Site Name (Arabic)") },
                    modifier = Modifier.fillMaxWidth().testTag("add_site_name_ar"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = nameEn,
                    onValueChange = { nameEn = it },
                    label = { Text(if (lang == Language.ARABIC) "اسم الموقع بالإنجليزية" else "Site Name (English)") },
                    modifier = Modifier.fillMaxWidth().testTag("add_site_name_en"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text(if (lang == Language.ARABIC) "رمز الموقع (Code)" else "Site Code") },
                    placeholder = { Text("e.g. DC-RUH-04") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text(if (lang == Language.ARABIC) "العنوان / المدينة" else "Location / City") },
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
                            if (nameAr.isNotBlank() || nameEn.isNotBlank()) {
                                onConfirm(
                                    nameAr.ifBlank { nameEn },
                                    nameEn.ifBlank { nameAr },
                                    code.ifBlank { "SITE-01" },
                                    location
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SafetyOrangePrimary),
                        modifier = Modifier.testTag("save_site_button")
                    ) {
                        Text(HseStrings.saveButton(lang))
                    }
                }
            }
        }
    }
}
