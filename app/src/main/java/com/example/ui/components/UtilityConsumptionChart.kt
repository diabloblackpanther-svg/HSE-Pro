package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.MeterReadingEntity
import com.example.data.local.entities.SiteEntity
import com.example.model.Language
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.HazardRed
import com.example.ui.theme.SafetyOrangePrimary
import com.example.ui.theme.SafetyYellow

enum class TimePeriod(val labelAr: String, val labelEn: String) {
    DAILY("يومي (Daily)", "Daily"),
    MONTHLY("شهري (Monthly)", "Monthly"),
    YEARLY("سنوي (Yearly)", "Yearly"),
    SITE_COMPARE("مقارنة المواقع", "Site Compare")
}

@Composable
fun UtilityConsumptionChart(
    readings: List<MeterReadingEntity>,
    sites: List<SiteEntity>,
    lang: Language = Language.ARABIC
) {
    var selectedPeriod by remember { mutableStateOf(TimePeriod.DAILY) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = if (lang == Language.ARABIC) "مخطط تحليل الاستهلاك والمقارنات" else "Consumption Analysis & Comparisons",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Time period filter chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                TimePeriod.values().forEach { period ->
                    FilterChip(
                        selected = selectedPeriod == period,
                        onClick = { selectedPeriod = period },
                        label = { Text(period.labelAr, fontSize = 10.sp) },
                        modifier = Modifier.testTag("period_chip_${period.name}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Chart Canvas
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(12.dp)
                    .testTag("utility_chart_canvas")
            ) {
                Canvas(modifier = Modifier.fillMaxWidth().height(160.dp)) {
                    val barWidth = size.width / 8f
                    val maxVal = 2000f

                    if (selectedPeriod == TimePeriod.SITE_COMPARE) {
                        // Compare between sites bar chart
                        sites.forEachIndexed { idx, site ->
                            val simulatedConsumption = (site.id * 850.0 + 400.0).toFloat()
                            val barHeight = (simulatedConsumption / maxVal) * size.height
                            val x = idx * (barWidth + 30f) + 40f
                            val y = size.height - barHeight

                            val barColor = if (idx == 0) SafetyOrangePrimary else if (idx == 1) Color(0xFF06B6D4) else SafetyYellow

                            drawRoundRect(
                                color = barColor,
                                topLeft = Offset(x, y),
                                size = Size(barWidth, barHeight),
                                cornerRadius = CornerRadius(8f, 8f)
                            )
                        }
                    } else {
                        // Period trend bar chart with anomaly spike highlight
                        val sampleData = listOf(950f, 1100f, 1050f, 1850f, 1200f, 1150f)
                        val labels = listOf("السبت", "الأحد", "الإثنين", "الثلاثاء", "الأربعاء", "الخميس")

                        sampleData.forEachIndexed { idx, valAmount ->
                            val barHeight = (valAmount / maxVal) * size.height
                            val x = idx * (barWidth + 18f) + 20f
                            val y = size.height - barHeight

                            val isSpike = valAmount > 1500f
                            val barColor = if (isSpike) HazardRed else SafetyOrangePrimary

                            drawRoundRect(
                                color = barColor,
                                topLeft = Offset(x, y),
                                size = Size(barWidth, barHeight),
                                cornerRadius = CornerRadius(6f, 6f)
                            )

                            if (isSpike) {
                                // Draw Anomaly Warning Indicator
                                drawCircle(
                                    color = HazardRed,
                                    center = Offset(x + barWidth / 2, y - 10f),
                                    radius = 8f
                                )
                            }
                        }
                    }

                    // Draw baseline grid line
                    drawLine(
                        color = Color.Gray.copy(alpha = 0.4f),
                        start = Offset(0f, size.height * 0.4f),
                        end = Offset(size.width, size.height * 0.4f),
                        strokeWidth = 2f
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(10.dp).background(SafetyOrangePrimary, RoundedCornerShape(2.dp)))
                    Text(text = " استهلاك طبيعي", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(10.dp).background(HazardRed, RoundedCornerShape(2.dp)))
                    Text(text = " ارتفاع حاد / تسريب!", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, color = HazardRed)
                }
            }
        }
    }
}
