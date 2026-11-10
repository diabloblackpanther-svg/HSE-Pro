package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Screen
import com.example.model.Language

data class MoreItem(
    val id: String,
    val titleAr: String,
    val titleEn: String,
    val subtitleAr: String,
    val subtitleEn: String,
    val icon: ImageVector,
    val targetScreen: Screen,
    val color: Color
)

@Composable
fun MoreHubScreen(
    lang: Language,
    onNavigate: (Screen) -> Unit
) {
    val items = listOf(
        MoreItem(
            id = "compliance",
            titleAr = "مصفوفة الالتزام القانوني",
            titleEn = "Legal Compliance Matrix",
            subtitleAr = "قانون العمل 12/2003، البيئة 4/1994، الحماية المدنية، ISO & OSHA",
            subtitleEn = "Labor Law 12/2003, Env Law 4/1994, Civil Defense, ISO & OSHA",
            icon = Icons.Default.Gavel,
            targetScreen = Screen.COMPLIANCE_MATRIX,
            color = Color(0xFF7C4DFF)
        ),
        MoreItem(
            id = "training",
            titleAr = "التدريب ومهمات الوقاية PPE",
            titleEn = "Training & PPE Register",
            subtitleAr = "محادثات TBT اليومية، فحص الطفايات، ومعدات الطوارئ والشهادات",
            subtitleEn = "Toolbox Talks, Fire Extinguishers, Emergency Gear & Cards",
            icon = Icons.Default.School,
            targetScreen = Screen.TRAINING_EQUIPMENT,
            color = Color(0xFF0288D1)
        ),
        MoreItem(
            id = "utilities",
            titleAr = "استهلاك الكهرباء والمياه",
            titleEn = "Electricity & Water Utilities",
            subtitleAr = "سجل العدادات، تتبع الاستهلاك، وتنبيهات الارتفاع المفاجئ",
            subtitleEn = "Utility meters log, consumption tracking & spike anomaly alerts",
            icon = Icons.Default.FlashOn,
            targetScreen = Screen.UTILITIES,
            color = Color(0xFFF57C00)
        ),
        MoreItem(
            id = "risk",
            titleAr = "تقييم المخاطر JSA",
            titleEn = "Job Safety Analysis (JSA)",
            subtitleAr = "مصفوفة مخاطر 5×5، المخاطر الميدانية وضوابط التحكم الوقائية",
            subtitleEn = "5x5 Risk Matrix, hazard identification & preventive controls",
            icon = Icons.Default.Security,
            targetScreen = Screen.RISK_ASSESSMENT,
            color = Color(0xFFD32F2F)
        ),
        MoreItem(
            id = "inspections",
            titleAr = "جولات التفتيش والمراجعات",
            titleEn = "Audits & Field Inspections",
            subtitleAr = "التفتيش الميداني، نسب المطابقة، وقوائم فحص المعدات",
            subtitleEn = "Field audits, compliance scores & equipment verification",
            icon = Icons.Default.AssignmentTurnedIn,
            targetScreen = Screen.INSPECTIONS,
            color = Color(0xFF388E3C)
        ),
        MoreItem(
            id = "sites",
            titleAr = "إدارة المواقع والأدوار",
            titleEn = "Sites & User Roles",
            subtitleAr = "تبديل الموقع الحكالي، إضافة موقع جديد، وتحديد صلاحية المستخدم",
            subtitleEn = "Switch site, add new site & manage active user permissions",
            icon = Icons.Default.LocationOn,
            targetScreen = Screen.SITES,
            color = Color(0xFF00796B)
        ),
        MoreItem(
            id = "settings",
            titleAr = "إعدادات التطبيق والنسخ الاحتياطي",
            titleEn = "Settings & Backup",
            subtitleAr = "اللغات (عربي/إنجليزي)، المظهر (فاتح/داكن)، وتصدير JSON",
            subtitleEn = "App language, Dark/Light theme & JSON data backup export",
            icon = Icons.Default.Settings,
            targetScreen = Screen.SETTINGS,
            color = Color(0xFF455A64)
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.GridView,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = if (lang == Language.ARABIC) "الأقسام والوحدات الإضافية" else "All HSE Modules & Tools",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = if (lang == Language.ARABIC) "وصول سريع لكافة أدوات السلامة المهنية والمربوطة بالمنظومة" else "Quick access to specialized safety & compliance tools",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(items) { item ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigate(item.targetScreen) }
                        .testTag("more_card_${item.id}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(item.color.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = null,
                                tint = item.color,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = if (lang == Language.ARABIC) item.titleAr else item.titleEn,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (lang == Language.ARABIC) item.titleEn else item.titleAr,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = item.color,
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (lang == Language.ARABIC) item.subtitleAr else item.subtitleEn,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                                maxLines = 2
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
