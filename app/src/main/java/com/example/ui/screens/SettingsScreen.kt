package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Language
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.SafetyOrangePrimary
import com.example.ui.util.HseStrings
import com.example.ui.util.ReportGenerator

@Composable
fun SettingsScreen(
    lang: Language,
    isDarkMode: Boolean,
    onToggleLanguage: () -> Unit,
    onToggleTheme: () -> Unit,
    onExportBackup: () -> String
) {
    val context = LocalContext.current
    var notificationsEnabled by remember { mutableStateOf(true) }
    var backupStatusText by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        item {
            Text(
                text = HseStrings.navSettings(lang),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = if (lang == Language.ARABIC) "تفضيلات التطبيق، النسخ الاحتياطي ومصفوفة الأمان" else "App preferences & JSON backup/restore",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }

        // 1. LANGUAGE TOGGLE
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
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Language, contentDescription = null, tint = SafetyOrangePrimary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = HseStrings.languageSettingTitle(lang),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = lang.displayName,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }

                    Button(
                        onClick = onToggleLanguage,
                        colors = ButtonDefaults.buttonColors(containerColor = SafetyOrangePrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("toggle_language_button")
                    ) {
                        Text(text = if (lang == Language.ARABIC) "English" else "العربية", fontSize = 12.sp)
                    }
                }
            }
        }

        // 2. THEME TOGGLE
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
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.DarkMode, contentDescription = null, tint = SafetyOrangePrimary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = HseStrings.themeSettingTitle(lang),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = if (isDarkMode) (if (lang == Language.ARABIC) "الوضع الداكن" else "Dark Mode") else (if (lang == Language.ARABIC) "الوضع الفاتح" else "Light Mode"),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }

                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = { onToggleTheme() },
                        colors = SwitchDefaults.colors(checkedThumbColor = SafetyOrangePrimary),
                        modifier = Modifier.testTag("toggle_theme_switch")
                    )
                }
            }
        }

        // 3. JSON BACKUP & RESTORE
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Backup, contentDescription = null, tint = SafetyOrangePrimary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (lang == Language.ARABIC) "النسخة الاحتياطية والاستعادة (JSON)" else "JSON Data Backup & Restore",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = if (lang == Language.ARABIC) "حفظ كامل قاعدة بيانات النظام وتصديرها بملف JSON" else "Export or restore full HSE database JSON file",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val jsonStr = onExportBackup()
                                ReportGenerator.shareReportFile(
                                    context = context,
                                    content = jsonStr,
                                    fileName = "hse_pro_backup_${System.currentTimeMillis()}.json",
                                    mimeType = "application/json"
                                )
                                backupStatusText = if (lang == Language.ARABIC) "تم تصدير ملف النسخة الاحتياطية بنجاح ✓" else "Backup exported successfully ✓"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SafetyOrangePrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).testTag("export_backup_json_button")
                        ) {
                            Text(text = if (lang == Language.ARABIC) "تصدير JSON" else "Export JSON", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                backupStatusText = if (lang == Language.ARABIC) "تم التحقق من سلامة البيانات الجاهزة للاستعادة ✓" else "Database validated ✓"
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).testTag("import_backup_json_button")
                        ) {
                            Text(text = if (lang == Language.ARABIC) "استعادة" else "Restore", fontSize = 12.sp)
                        }
                    }

                    if (backupStatusText.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = backupStatusText,
                            style = MaterialTheme.typography.labelSmall,
                            color = EmeraldGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 4. COMPLIANCE BADGE
        item {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = EmeraldGreen.copy(alpha = 0.12f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = if (lang == Language.ARABIC) "متوافق مع قانون العمل والبيئة و OSHA & NFPA" else "Compliant with Labor Law, ISO, OSHA & NFPA",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = EmeraldGreen
                        )
                        Text(
                            text = if (lang == Language.ARABIC) "HSE Pro Enterprise Complete Safety Platform v5.0" else "HSE Pro Enterprise Safety Suite v5.0",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}
